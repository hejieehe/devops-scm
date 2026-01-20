package com.tencent.devops.scm.sdk.bkcode;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeCommitStateType;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitStatus;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitStatusInput;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BkCodeCheckRunApiTest extends AbstractBkCodeTest {

    private static final String TEST_REF = "0cfeacad6fd5ceb7dc5dece5252b1bbdc3da3cc8";
    private static final String TEST_TARGET_BRANCH = "master";
    private static final BkCodeCommitStateType TEST_CHECK_RUN_STATE = BkCodeCommitStateType.SUCCESS;
    private static final String TEST_CHECK_RUN_CONTEXT = "devops repo check";
    private static final String TEST_CHECK_RUN_DESC = "devops repo check desc";
    private static final String TEST_CHECK_RUN_TARGET_URL = "github.com";
    private static final String TEST_CHECK_RUN_REPORT_HTML = "<html><body><div>构建成功</div></body></html>";
    private static final List<String> targetBranches = List.of("master");
    private static final BkCodeCommitStatusInput input = BkCodeCommitStatusInput.builder()
            .state(TEST_CHECK_RUN_STATE)
            .context(TEST_CHECK_RUN_CONTEXT)
            .description(TEST_CHECK_RUN_DESC)
            .targetUrl(TEST_CHECK_RUN_TARGET_URL)
            .reportHtml(TEST_CHECK_RUN_REPORT_HTML)
            .build();

    public BkCodeCheckRunApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        if (MOCK_DATA) {
            mock();
        } else {
            bkCodeApi = createBkCodeApi();
        }
    }

    public static void mock() {
        bkCodeApi = mockBkCodeApi();
        when(bkCodeApi.getCheckRunApi()).thenReturn(Mockito.mock(BkCodeCheckRunApi.class));
        when(bkCodeApi.getCheckRunApi()
                .getCheckRuns(
                        TEST_PROJECT_NAME,
                        TEST_REF,
                        TEST_TARGET_BRANCH
                )
        ).thenReturn(
                read(
                        "get_check_runs_result.json",
                        new TypeReference<BkCodeResult<List<BkCodeCommitStatus>>>() {}
                ).getData()
        );
        when(bkCodeApi.getCheckRunApi()
                .create(
                        TEST_PROJECT_NAME,
                        TEST_REF,
                        input
                )
        ).thenReturn(
                read(
                        "create_check_run_result.json",
                        new TypeReference<BkCodeResult<BkCodeCommitStatus>>() {}
                ).getData()
        );
    }

    @Test
    public void create() throws IOException {
        BkCodeCommitStatus bkCodeCommitStatus = bkCodeApi.getCheckRunApi().create(
                TEST_PROJECT_NAME,
                TEST_REF,
                input
        );
        Assertions.assertEquals(bkCodeCommitStatus.getContext(), TEST_CHECK_RUN_CONTEXT);
        Assertions.assertEquals(bkCodeCommitStatus.getState(), TEST_CHECK_RUN_STATE);
        Assertions.assertEquals(bkCodeCommitStatus.getDescription(), TEST_CHECK_RUN_DESC);
        Assertions.assertEquals(bkCodeCommitStatus.getTargetUrl(), TEST_CHECK_RUN_TARGET_URL);
        Assertions.assertEquals(bkCodeCommitStatus.getReportHtml(), TEST_CHECK_RUN_REPORT_HTML);
    }

    @Test
    public void getCheckRuns() {
        List<BkCodeCommitStatus> checkRuns = bkCodeApi.getCheckRunApi().getCheckRuns(
                TEST_PROJECT_NAME,
                TEST_REF,
                TEST_TARGET_BRANCH
        );
        Assertions.assertNotNull(checkRuns);
        Assertions.assertFalse(checkRuns.isEmpty());
        
        // 验证第一个check run的字段
        BkCodeCommitStatus firstCheckRun = checkRuns.get(0);
        Assertions.assertNotNull(firstCheckRun.getContext());
        Assertions.assertNotNull(firstCheckRun.getState());
        Assertions.assertNotNull(firstCheckRun.getDescription());
        Assertions.assertNotNull(firstCheckRun.getTargetUrl());
        Assertions.assertNotNull(firstCheckRun.getReportHtml());
    }
}
