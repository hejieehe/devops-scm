package com.tencent.devops.scm.sdk.tgit;


import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GiteeBranchesApiTest extends AbstractGiteeTest {

    private static GiteeApi giteeApi;

    public GiteeBranchesApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        giteeApi = createTGitApi();
    }

    @Test
    public void testGetBranches() {
        List<GiteeBranch> branches = giteeApi.getBranchesApi().getBranches(TEST_PROJECT_NAME);
        branches.forEach(System.out::println);
    }
}
