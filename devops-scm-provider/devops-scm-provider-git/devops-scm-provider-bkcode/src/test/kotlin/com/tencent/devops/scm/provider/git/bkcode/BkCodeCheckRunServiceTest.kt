package com.tencent.devops.scm.provider.git.bkcode

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.enums.CheckRunConclusion
import com.tencent.devops.scm.api.enums.CheckRunStatus
import com.tencent.devops.scm.api.pojo.CheckRunInput
import com.tencent.devops.scm.api.pojo.CheckRunListOptions
import com.tencent.devops.scm.api.pojo.CheckRunOutput
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitStatus
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import java.time.LocalDateTime
import org.mockito.Mockito.`when` as whenMock

class BkCodeCheckRunServiceTest : AbstractBkCodeServiceTest() {

    companion object {
        private lateinit var checkRunService: BkCodeCheckRunService
    }

    private val TEST_REF = "0cfeacad6fd5ceb7dc5dece5252b1bbdc3da3cc8"

    private val checkRunInput = CheckRunInput(
        name = "devops-scm-bkcode-check",
        ref = TEST_REF,
        pullRequestId = 12345,
        status = CheckRunStatus.IN_PROGRESS,
        startedAt = LocalDateTime.now(),
        output = CheckRunOutput(
            title = "BKCode Check Run Test",
            summary = "BKCode check run service test summary",
            text = "BKCode check run service test details"
        ),
        targetBranches = listOf("master", "develop"),
        conclusion = CheckRunConclusion.FAILURE,
        detailsUrl = "https://github.com"
    )

    init {
        apiFactory = createBkCodeApiFactory()
        checkRunService = BkCodeCheckRunService(apiFactory)
        mockData()
    }

    private fun mockData() {
        val apiFactory = Mockito.mock(BkCodeApiFactory::class.java)
        checkRunService = BkCodeCheckRunService(apiFactory)

        val bkCodeApi = Mockito.mock(com.tencent.devops.scm.sdk.bkcode.BkCodeApi::class.java)
        whenMock(apiFactory.fromAuthProvider(any()))
            .thenReturn(bkCodeApi)
        
        val checkRunApi = Mockito.mock(com.tencent.devops.scm.sdk.bkcode.BkCodeCheckRunApi::class.java)
        whenMock(bkCodeApi.checkRunApi).thenReturn(checkRunApi)
        
        whenMock(checkRunApi.create(anyString(), anyString(), any()))
            .thenReturn(
                read(
                    "create_check_run_result.json",
                    object : TypeReference<BkCodeResult<BkCodeCommitStatus>>() {}
                ).data
            )
        
        whenMock(checkRunApi.getCheckRuns(anyString(), anyString(), anyString()))
            .thenReturn(
                read(
                    "get_check_runs_result.json",
                    object : TypeReference<BkCodeResult<List<BkCodeCommitStatus>>>() {}
                ).data
            )
    }

    @Test
    fun create() {
        val result = checkRunService.create(providerRepository, checkRunInput)
        Assertions.assertEquals(result.status, CheckRunStatus.COMPLETED)
        Assertions.assertEquals(result.conclusion, CheckRunConclusion.SUCCESS)
    }

    @Test
    fun update() {
        val updateCheckRun = checkRunInput.copy(
            status = CheckRunStatus.COMPLETED,
            completedAt = LocalDateTime.now(),
            conclusion = CheckRunConclusion.SUCCESS,
            id = 67890
        )
        val result = checkRunService.update(providerRepository, updateCheckRun)
        println("Updated check run: $result")
    }

    @Test
    fun getCheckRuns() {
        val opts = CheckRunListOptions(
            ref = TEST_REF,
            targetBranch = "master"
        )
        val results = checkRunService.getCheckRuns(providerRepository, opts)
        println("Found ${results.size} check runs")
    }
}