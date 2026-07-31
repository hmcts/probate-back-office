package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.invalidHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.handOffReasonListWithHandOffReason;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ATTACH_SCANNED_DOCS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CREATE_CASE_FROM_BULK_SCAN_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ADMON_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;

public class CamundaTaskWaInitiationAdmonTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        Map<String,Object> examineDigitalCaseAdmonTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_ADMON,
                "name", ADMON_TASK_TYPE_NAME,
                "processCategories", "case progression"
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
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    CASE_PRINTED_STATE,
                    null,
                    Collections.emptyList()
            ),
            Arguments.of(
                    ATTACH_SCANNED_DOCS_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    ATTACH_SCANNED_DOCS_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    ATTACH_SCANNED_DOCS_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    ATTACH_SCANNED_DOCS_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    ATTACH_SCANNED_DOCS_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    CREATE_CASE_FROM_BULK_SCAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    List.of(examineDigitalCaseAdmonTaskAttributes)
            ),
            Arguments.of(
                    CREATE_CASE_FROM_BULK_SCAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(true, "admonWill", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    CREATE_CASE_FROM_BULK_SCAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "other", false, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    CREATE_CASE_FROM_BULK_SCAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", false,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    CREATE_CASE_FROM_BULK_SCAN_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "admonWill", true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "admonWill", false, Collections.emptyList()),
                    List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
            )
        );
    }
}

