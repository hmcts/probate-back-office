package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.handOffReasonListWithHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.invalidHandOffReason;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ADMON_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ATTACH_SCANNED_DOCS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CREATE_CASE_FROM_BULK_SCAN_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;

public class CamundaTaskWaInitiationAdmonTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        Map<String,Object> examineDigitalCaseAdmonTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_ADMON,
                "name", ADMON_TASK_TYPE_NAME,
                "processCategories", "case progression,examineDigitalCaseTypes"
        );

        Map<String,Object> examineDigitalCaseAdmonReadyToIssueTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE,
                "name", ADMON_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
            Arguments.of(
                    "someOtherEventId",
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false,
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    null,
                    emptyList()
            ),
            Arguments.of(
                    ATTACH_SCANNED_DOCS_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    ATTACH_SCANNED_DOCS_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    ATTACH_SCANNED_DOCS_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    ATTACH_SCANNED_DOCS_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    ATTACH_SCANNED_DOCS_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    CREATE_CASE_FROM_BULK_SCAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    CREATE_CASE_FROM_BULK_SCAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    CREATE_CASE_FROM_BULK_SCAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, 
                            emptyList(), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    CREATE_CASE_FROM_BULK_SCAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    CREATE_CASE_FROM_BULK_SCAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false,
                            emptyList()),
                    emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "admonWill", false, 
                            emptyList(), false,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            emptyList(), true,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "admonWill", false,
                            emptyList(), true,
                            emptyList()),
                    List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
            )
        );
    }
}

