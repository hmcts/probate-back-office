package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REDECLARATION;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REDECLARATION_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.GOP_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_CASE_STOPPED_AWAIT_REDEC_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_REDECLARATION_SOT_FOR_CASE_STOPPED_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;

public class CamundaTaskWaInitiationRedeclarationTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Map<String,Object> redeclarationTaskAttributes = Map.of(
                "taskId", REDECLARATION,
                "name", REDECLARATION_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        BO_REDECLARATION_SOT_FOR_CASE_STOPPED_EVENT,
                        BO_CASE_STOPPED_AWAIT_REDEC_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        List.of(redeclarationTaskAttributes)
                ),
                Arguments.of(
                        "someOtherEvent",
                        BO_CASE_STOPPED_AWAIT_REDEC_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_REDECLARATION_SOT_FOR_CASE_STOPPED_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_REDECLARATION_SOT_FOR_CASE_STOPPED_EVENT,
                        BO_CASE_STOPPED_AWAIT_REDEC_STATE,
                        additionalData(false, GOP_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(redeclarationTaskAttributes)
                ),

                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        BO_CASE_STOPPED_AWAIT_REDEC_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        List.of(redeclarationTaskAttributes)
                ),
                Arguments.of(
                        "someOtherEvent",
                        BO_CASE_STOPPED_AWAIT_REDEC_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        BO_CASE_STOPPED_AWAIT_REDEC_STATE,
                        additionalData(false, GOP_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(redeclarationTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_STOPPED_AWAIT_REDEC_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        List.of(redeclarationTaskAttributes)
                ),
                Arguments.of(
                        "someOtherEvent",
                        BO_CASE_STOPPED_AWAIT_REDEC_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_STOPPED_AWAIT_REDEC_STATE,
                        additionalData(false, GOP_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(redeclarationTaskAttributes)
                )
        );
    }

}
