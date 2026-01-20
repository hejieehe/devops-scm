package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.CheckRunService
import com.tencent.devops.scm.api.pojo.CheckRun
import com.tencent.devops.scm.api.pojo.CheckRunInput
import com.tencent.devops.scm.api.pojo.CheckRunListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory

class BkCodeCheckRunService(private val apiFactory: BkCodeApiFactory) : CheckRunService {

    override fun create(repository: ScmProviderRepository, input: CheckRunInput): CheckRun {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val bkCodeCommitStatus = bkCodeApi.checkRunApi.create(
                repo.projectIdOrPath,
                input.ref ?: throw IllegalArgumentException("ref is required for BKCode check run"),
                BkCodeObjectConverter.convertCheckRunInput(input)
            )
            BkCodeObjectConverter.convertCheckRun(bkCodeCommitStatus)
        }
    }

    override fun update(repository: ScmProviderRepository, input: CheckRunInput): CheckRun {
        // BkCode API不支持更新check run，直接调用创建接口进行覆盖
        return create(repository, input)
    }

    override fun getCheckRuns(repository: ScmProviderRepository, opts: CheckRunListOptions): List<CheckRun> {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val checkRuns = bkCodeApi.checkRunApi.getCheckRuns(
                repo.projectIdOrPath,
                opts.ref,
                opts.targetBranch
            )
            checkRuns.map { BkCodeObjectConverter.convertCheckRun(it) }
        }
    }
}