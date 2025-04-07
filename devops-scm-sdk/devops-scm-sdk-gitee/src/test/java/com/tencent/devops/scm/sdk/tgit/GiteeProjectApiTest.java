package com.tencent.devops.scm.sdk.tgit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseRepository;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeProjectHook;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeWebhookConfig;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GiteeProjectApiTest extends AbstractGiteeTest {

    private static GiteeApi giteeApi;

    public GiteeProjectApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        giteeApi = createTGitApi();
    }

    @Test
    public void testCommitCompare() throws JsonProcessingException {
        GiteeBaseRepository repository = giteeApi.getProjectApi().getProject(
                TEST_PROJECT_NAME
        );
        System.out.println(ScmJsonUtil.getJsonFactory().toJson(repository));
    }

    @Test
    public void addHook() throws JsonProcessingException {
        GiteeWebhookConfig hook = giteeApi.getProjectApi().addHook(
                TEST_PROJECT_NAME,
                GiteeProjectHook.builder()
                        .pushEvents(true)
                        .issuesEvents(true)
                        .mergeRequestsEvents(true)
                        .noteEvents(true)
                        .tagPushEvents(true)
                        .url("https://www.baidu.com")
                        .build(),
                "hejieehe"
        );
        System.out.println(ScmJsonUtil.getJsonFactory().toJson(hook));
    }

    @Test
    public void getHook() throws JsonProcessingException {
        List<GiteeWebhookConfig> hooks = giteeApi.getProjectApi().getHooks(
                TEST_PROJECT_NAME
        );
        System.out.println(ScmJsonUtil.getJsonFactory().toJson(hooks));
    }

    @Test
    public void delHook() throws JsonProcessingException {
        giteeApi.getProjectApi().deleteHook(
                TEST_PROJECT_NAME,
                1469611L
        );

        System.out.println("delHook success");
        getHook();
    }
}
