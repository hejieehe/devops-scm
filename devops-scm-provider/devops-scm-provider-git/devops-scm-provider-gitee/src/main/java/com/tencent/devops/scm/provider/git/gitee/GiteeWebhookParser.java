package com.tencent.devops.scm.provider.git.gitee;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ACTION;

import com.tencent.devops.scm.api.WebhookParser;
import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.HookRequest;
import com.tencent.devops.scm.api.pojo.PullRequest;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook;
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook;
import com.tencent.devops.scm.api.util.GitUtils;
import com.tencent.devops.scm.provider.git.gitee.enums.GiteeEventType;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseLabel;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventCommit;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventPullRequest;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventRef;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteePullRequestHook;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteePushHook;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import kotlin.Pair;

public class GiteeWebhookParser implements WebhookParser {

    // 忽略的pull request动作, Gitee 新建PR时会瞬间发送三个webhook动作，对无效的webhook进行过滤
    private static final List<Pair<String, String>> IGNORED_PULL_REQUEST_ACTION = List.of(
            new Pair<>(EventAction.OPEN.value, "test"),
            new Pair<>(EventAction.OPEN.value, "assign")
    );

    @Override
    public Webhook parse(HookRequest request) {
        Webhook hook = null;
        switch (request.getHeaders().get("X-Gitee-Event")) {
            case "Push Hook":
                hook = parsePushHook(request.getBody());
                break;
            case "Merge Request Hook":
                hook = parsePullRequestHook(request.getBody());
                break;
            default:

        }
        return hook;
    }

    private Webhook parsePushHook(String body) {
        GiteePushHook giteePushHook = ScmJsonUtil.fromJson(body, GiteePushHook.class);
        GitScmServerRepository repository = GiteeObjectConverter.convertRepository(
                giteePushHook.getRepository()
        );
        EventAction action = EventAction.PUSH_FILE;
        if (giteePushHook.getCreated()) {
            action = EventAction.CREATE;
        } else if (giteePushHook.getDeleted()) {
            action = EventAction.DELETE;
        }
        GiteeEventCommit headCommit = giteePushHook.getHeadCommit();
        return GitPushHook.builder()
                .action(action)
                .ref(GitUtils.trimRef(giteePushHook.getRef()))
                .repo(repository)
                .eventType(GiteeEventType.PUSH.name())
                .before(giteePushHook.getBefore())
                .after(giteePushHook.getAfter())
                .commit(GiteeObjectConverter.convertCommit(headCommit))
                .sender(GiteeObjectConverter.convertUser(giteePushHook.getSender()))
                .commits(giteePushHook.getCommits()
                        .stream()
                        .map(GiteeObjectConverter::convertCommit)
                        .collect(Collectors.toList())
                )
                .totalCommitsCount(giteePushHook.getTotalCommitsCount().intValue())
                .extras(new HashMap<>())
                .build();
    }

    private Webhook parsePullRequestHook(String body) {
        GiteePullRequestHook giteePullRequestHook = ScmJsonUtil.fromJson(body, GiteePullRequestHook.class);
        GitScmServerRepository repository = GiteeObjectConverter.convertRepository(
                giteePullRequestHook.getRepository()
        );
        GiteeEventPullRequest sourcePullRequest = giteePullRequestHook.getPullRequest();
        GiteeEventRef head = sourcePullRequest.getHead();
        PullRequest pullRequest = PullRequest.builder()
                .id(sourcePullRequest.getId())
                .number(sourcePullRequest.getNumber().intValue())
                .sourceRepo(
                        GiteeObjectConverter.convertRepository(
                                giteePullRequestHook.getSourceRepo().getRepository()
                        )
                )
                .sourceRef(GiteeObjectConverter.convertRef(head))
                .targetRef(GiteeObjectConverter.convertRef(sourcePullRequest.getBase()))
                .targetRepo(repository)
                .title(sourcePullRequest.getTitle())
                .body(sourcePullRequest.getBody())
                .description(sourcePullRequest.getBody())
                .link(sourcePullRequest.getHtmlUrl())
                .mergeCommitSha(sourcePullRequest.getMergeCommitSha())
                .merged(sourcePullRequest.getMerged())
                .author(GiteeObjectConverter.convertAuthor(sourcePullRequest.getUser()))
                .created(sourcePullRequest.getCreatedAt())
                .updated(sourcePullRequest.getUpdatedAt())
                .labels(sourcePullRequest.getLabels()
                        .stream()
                        .map(GiteeBaseLabel::getName)
                        .collect(Collectors.toList())
                )
                .milestone(GiteeObjectConverter.convertMilestone(sourcePullRequest.getMilestone()))
                .assignee(sourcePullRequest.getAssignees()
                        .stream().map(GiteeObjectConverter::convertAuthor)
                        .collect(Collectors.toList())
                )
                .baseCommit(sourcePullRequest.getBase().getSha())
                .build();
        String hookAction = giteePullRequestHook.getAction();
        String hookActionDesc = giteePullRequestHook.getActionDesc();
        // 无效的hook消息跳过CI流程
        boolean skipCi = isIgnorePullRequestAction(hookAction, hookActionDesc);
        HashMap<String, Object> extras = new HashMap<>();
        EventAction action = GiteeObjectConverter.convertAction(
                hookAction,
                hookActionDesc
        );
        // 汉化需要, [更新] → [编辑]
        if (EventAction.UPDATE.equals(action)) {
            action = EventAction.EDIT;
        }
        extras.put(PIPELINE_GIT_MR_ACTION, action);
        return PullRequestHook.builder()
                .repo(repository)
                .action(action)
                .eventType(GiteeEventType.MERGE_REQUEST.name())
                .pullRequest(pullRequest)
                .sender(GiteeObjectConverter.convertUser(giteePullRequestHook.getSender()))
                .commit(Commit.builder().sha(head.getSha()).build())
                .extras(extras)
                .skipCi(skipCi)
                .build();
    }

    @Override
    public boolean verify(HookRequest request, String secretToken) {
        return true;
    }

    public static boolean isIgnorePullRequestAction(String action, String actionDesc) {
        return IGNORED_PULL_REQUEST_ACTION.stream()
                .anyMatch(pair -> pair.getFirst().equals(action) && pair.getSecond().equals(actionDesc));
    }
}
