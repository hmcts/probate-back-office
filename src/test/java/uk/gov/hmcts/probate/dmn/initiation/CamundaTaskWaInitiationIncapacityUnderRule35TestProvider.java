package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalDataNoHandOffList;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.handOffReasonListWithHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.incapacityUnderRule35HandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.invalidHandOffReason;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INCAPACITY_UNDER_RULE_35;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INCAPACITY_UNDER_RULE_35_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;


public class CamundaTaskWaInitiationIncapacityUnderRule35TestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

        Map<String,Object> examineDigitalCaseIncapacityUnderRule35TaskAttributes
                = Map.of(
                "taskId", EXAMINE_INCAPACITY_UNDER_RULE_35,
                "name", EXAMINE_INCAPACITY_UNDER_RULE_35_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason), false,
                            emptyList()),
                    List.of(examineDigitalCaseIncapacityUnderRule35TaskAttributes)
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",false,
                            handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason), false,
                            emptyList()),
                    List.of(examineDigitalCaseIncapacityUnderRule35TaskAttributes)
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",false,
                            handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason), false,
                            emptyList()),
                    List.of(examineDigitalCaseIncapacityUnderRule35TaskAttributes)
            ),
            Arguments.of(
                    BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",false,
                            handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason), false,
                            emptyList()),
                    List.of(examineDigitalCaseIncapacityUnderRule35TaskAttributes)
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",false,
                            handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    null,
                    emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalDataNoHandOffList(),
                    emptyList()
            ),
            Arguments.of(
                    BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason), false,
                            handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason)),
                    List.of(examineDigitalCaseIncapacityUnderRule35TaskAttributes)
        )
        );
    }

}
