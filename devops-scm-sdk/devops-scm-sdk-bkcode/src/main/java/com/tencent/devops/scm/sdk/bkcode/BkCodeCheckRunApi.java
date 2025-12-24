package com.tencent.devops.scm.sdk.bkcode;


import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitStatus;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitStatusInput;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import java.io.IOException;
import java.util.List;

public class BkCodeCheckRunApi extends AbstractBkCodeApi {

    private static final String CHECK_RUN_URI_PATTERN = "repos/:id/commit/status/ref";
    private static final String CHECK_RUN_SHA_URI_PATTERN = "repos/:id/commit/status/:sha";

    public BkCodeCheckRunApi(BkCodeApi bkCodeApi) {
        super(bkCodeApi);
    }

    /**
     * 创建检查项
     *
     * @param projectIdOrPath 仓库名
     */
    public BkCodeCommitStatus create(
            Object projectIdOrPath,
            String sha,
            BkCodeCommitStatusInput input
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        CHECK_RUN_SHA_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("sha", sha)
                                .build()
                )
                .withRepoId(repoId)
                .with(ScmJsonUtil.toJson(input))
                .fetch(BkCodeCommitStatus.class);
    }

    /**
     * 获取某个提交的检查项
     *
     * @param projectIdOrPath 仓库名
     * @param ref commit sha
     */
    public List<BkCodeCommitStatus> getCheckRuns(
            Object projectIdOrPath,
            String ref,
            String targetBranch
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        CHECK_RUN_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("ref", ref)
                .with("targetBranch", targetBranch)
                .fetch(new TypeReference<>() {
                });
    }
}
