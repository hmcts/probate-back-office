package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_FAIL_QA_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RECTIFY_QA_CASE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RECTIFY_QA_CASE_TASK_TYPE_NAME;

public class CamundaTaskWaInitiationRectifyQaCaseTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Map<String,Object> rectifyQaCaseTaskAttributes = Map.of(
                "taskId", RECTIFY_QA_CASE,
                "name", RECTIFY_QA_CASE_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        BO_FAIL_QA_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList()),
                        List.of(rectifyQaCaseTaskAttributes)
                ),
                Arguments.of(
                        "someOtherEvent",
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_FAIL_QA_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList()),
                        Collections.emptyList()
                )
        );
    }

}
