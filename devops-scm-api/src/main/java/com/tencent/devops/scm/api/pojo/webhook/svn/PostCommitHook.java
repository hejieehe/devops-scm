package com.tencent.devops.scm.api.pojo.webhook.svn;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_SVN_WEBHOOK_COMMIT_TIME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_SVN_WEBHOOK_REVERSION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_SVN_WEBHOOK_USERNAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE;

import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.ScmI18Variable;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmServerRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCommitHook implements Webhook {
    private SvnScmServerRepository repository;
    // 变更的文件路径
    private List<Change> changes;
    private String message;
    private User sender;

    @NonNull
    private String eventType;
    // 扩展属性,提供者额外补充需要输出的变量
    private Map<String, Object> extras;
    private Long revision;
    private Long commitTime;

    @Override
    public ScmServerRepository repository() {
        return repository;
    }

    @Override
    public String getUserName() {
        return sender.getName();
    }

    @Override
    public ScmI18Variable getEventDesc() {
        return null;
    }

    @Override
    public Map<String, Object> outputs() {
        Map<String, Object> outputParams = new HashMap<>();
        outputParams.put(BK_REPO_SVN_WEBHOOK_REVERSION, revision);
        outputParams.put(BK_REPO_SVN_WEBHOOK_USERNAME, sender.getName());
        outputParams.put(BK_REPO_SVN_WEBHOOK_COMMIT_TIME, commitTime);
        outputParams.put(PIPELINE_WEBHOOK_COMMIT_MESSAGE, message);
        if (extras != null) {
            outputParams.putAll(extras);
        }
        return outputParams;
    }
}
