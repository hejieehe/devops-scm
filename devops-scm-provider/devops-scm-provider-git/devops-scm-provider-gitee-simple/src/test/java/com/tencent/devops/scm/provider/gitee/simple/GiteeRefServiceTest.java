package com.tencent.devops.scm.provider.gitee.simple;

import com.tencent.devops.scm.api.pojo.BranchListOptions;
import com.tencent.devops.scm.api.pojo.Reference;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GiteeRefServiceTest extends AbstractGiteeServiceTest {

    private static GiteeRefService refService = new GiteeRefService();

    protected GiteeRefServiceTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        mockData();
    }

    public static void mockData() {
    }

    @Test
    public void testListBranches() {
        List<Reference> references = refService.listBranches(providerRepository, BranchListOptions.builder().build());
        references.forEach(System.out::println);
    }
}
