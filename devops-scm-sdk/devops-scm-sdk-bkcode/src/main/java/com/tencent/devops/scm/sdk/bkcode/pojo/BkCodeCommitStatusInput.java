package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeCommitStateType;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BkCodeCommitStatusInput {
    /**
     * 检测系统标签，默认为 'default'
     */
    private String context;
    /**
     * 检测结果状态
     */
    private BkCodeCommitStateType state;
    /**
     * 检测结果描述
     */
    private String description;
    /**
     * 检测结果详情页面URL
     */
    @JsonProperty("targetUrl")
    private String targetUrl;
    /**
     * 检测结果 HTML 报告
     */
    @JsonProperty("reportHtml")
    private String reportHtml;
    /**
     * 检查结果关联的 MR（按目标分支识别），target_branches 为空时（默认），检查展示在所有 MR 中
     */
    @JsonProperty("targetBranches")
    private List<String> targetBranches;
}
