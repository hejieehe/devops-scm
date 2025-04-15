package com.tencent.devops.scm.provider.svn.tsvn;

import com.tencent.devops.scm.api.WebhookParser;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.HookRequest;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;
import com.tencent.devops.scm.api.pojo.webhook.svn.PostCommitHook;
import com.tencent.devops.scm.provider.svn.tsvn.enums.TSvnEventType;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnPostCommitEvent;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TSvnWebhookParser implements WebhookParser {
    private static final Logger logger = LoggerFactory.getLogger(TSvnWebhookParser.class);

    @Override
    public Webhook parse(HookRequest request) {
        Webhook hook = null;
        switch (request.getHeaders().get("X-Event")) {
            case "Svn Post Commit":
                hook = parsePostCommitHook(request.getBody());
                break;
            default:

        }
        return hook;
    }

    @Override
    public boolean verify(HookRequest request, String secretToken) {
        // 没有值不需要校验
        if (secretToken == null || secretToken.isEmpty()) {
            return true;
        }
        String token = request.getHeaders().get("X-Token");
        return secretToken.equals(token);
    }

    private Webhook parsePostCommitHook(String body) {
        TSvnPostCommitEvent src = ScmJsonUtil.fromJson(body, TSvnPostCommitEvent.class);
        List<Change> changes = CollectionUtils.emptyIfNull(src.getFiles())
                .stream()
                .map(TSvnObjectConverter::convertChange)
                .collect(Collectors.toList());
        User sender = User.builder()
                .id(src.getUserId())
                .name(src.getUserName())
                .email(src.getUserEmail())
                .build();
        return PostCommitHook.builder()
                .revision(src.getRevision())
                .message(src.getMessage())
                .changes(changes)
                .commitTime(src.getCommitTime())
                .sender(sender)
                .eventType(TSvnEventType.POST_COMMIT.toValue())
                .build();
    }
}
