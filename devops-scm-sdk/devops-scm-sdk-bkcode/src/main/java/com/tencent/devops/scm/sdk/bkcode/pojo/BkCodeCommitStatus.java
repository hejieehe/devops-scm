package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeCommitStateType;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提交状态实体类
 * 用于描述代码提交后的检测状态信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BkCodeCommitStatus {

    /**
     * 提交状态ID（主键）
     */
    private Long id;

    /**
     * 仓库ID（关联代码仓库表）
     */
    @JsonProperty("repoId")
    private Long repoId;

    /**
     * 提交SHA值（唯一标识某次代码提交）
     */
    @JsonProperty("commitSha")
    private String commitSha;

    /**
     * 检测状态
     * 可选值：PENDING（待检测）、SUCCESS（检测通过）、ERROR（系统错误）、FAILURE（检测失败）
     */
    private BkCodeCommitStateType state;

    /**
     * 检测系统标签（用于区分不同检测维度，如：单元测试、代码规范、安全扫描等）
     */
    private String context;

    /**
     * 检测结果详情页面URL（跳转至具体检测报告页面）
     */
    @JsonProperty("targetUrl")
    private String targetUrl;

    /**
     * 检测结果简短描述（如："单元测试全部通过"、"发现2处代码规范问题"）
     */
    private String description;

    /**
     * 检测结果HTML报告（包含完整HTML标签的富文本内容）
     */
    @JsonProperty("reportHtml")
    private String reportHtml;

    /**
     * 检查结果关联的MR目标分支列表
     * 1. 非空时：仅展示在指定分支的MR中
     * 2. 为空时（默认）：展示在所有MR中
     */
    @JsonProperty("targetBranches")
    private List<String> targetBranches;

    /**
     * 创建人信息（关联用户实体）
     */
    private BkCodeUser creator;

    /**
     * 创建时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    @JsonProperty("createTime")
    private String createTime;

    /**
     * 更新人信息（关联用户实体）
     */
    private BkCodeUser updater;

    /**
     * 更新时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    @JsonProperty("updateTime")
    private String updateTime;
}