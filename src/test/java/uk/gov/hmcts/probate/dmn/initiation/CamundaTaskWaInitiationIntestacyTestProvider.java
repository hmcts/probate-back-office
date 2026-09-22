package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.handOffReasonListWithHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.invalidHandOffReason;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.INTESTACY_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;


public class CamundaTaskWaInitiationIntestacyTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

        Map<String,Object> examineDigitalCaseIntestacyTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_INTESTACY,
                "name", INTESTACY_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        Map<String,Object> examineDigitalCaseIntestacyReadyToIssueTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE,
                "name", INTESTACY_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );


        return Stream.of(
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "intestacy", false,
                            emptyList(), false),
                    List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "intestacy", false,
                            emptyList(), false),
                    List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "intestacy", false,
                            emptyList(), false),
                    List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "intestacy", false,
                            emptyList(), false),
                    List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT,
                    CASE_PRINTED_STATE,
                    additionalData(false, "intestacy", false,
                            emptyList(), true),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "someOtherEventId",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "applyforGrantPaperApplicationMan",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "applyforGrantPaperApplicationMan",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "applyforGrantPaperApplicationMan",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "applyforGrantPaperApplicationMan",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "createCaseFromBulkScan",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "createCaseFromBulkScan",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "createCaseFromBulkScan",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "createCaseFromBulkScan",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "handleEvidence",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "handleEvidence",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "handleEvidence",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "handleEvidence",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "attachScannedDocs",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "attachScannedDocs",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "attachScannedDocs",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "attachScannedDocs",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "boResolveStop",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "boResolveStop",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "boResolveStop",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "boResolveStop",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "resolveCWEscalation",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "resolveCWEscalation",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "resolveCWEscalation",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "resolveCWEscalation",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "changeState",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "changeState",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "changeState",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "changeState",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "solicitorPaymentSuccessCase",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "solicitorPaymentSuccessCase",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "solicitorPaymentSuccessCase",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "solicitorPaymentSuccessCase",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "createCase",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "createCase",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "createCase",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "createCase",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "createCasePaymentSuccess",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "createCasePaymentSuccess",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "createCasePaymentSuccess",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "createCasePaymentSuccess",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "serviceRequestPaymentSuccess",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "serviceRequestPaymentSuccess",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "serviceRequestPaymentSuccess",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "serviceRequestPaymentSuccess",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "paymentSuccessApp",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyTaskAttributes)
            ),
            Arguments.of(
                    "paymentSuccessApp",
                    "CasePrinted",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "paymentSuccessApp",
                    "CasePrinted",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "paymentSuccessApp",
                    "CasePrinted",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            // BOReadyToIssue Tests
            Arguments.of(
                    "someOtherEventId",
                    "BOReadyToIssue",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "handleEvidence",
                    "BOReadyToIssue",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    "handleEvidence",
                    "BOReadyToIssue",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "handleEvidence",
                    "BOReadyToIssue",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "handleEvidence",
                    "BOReadyToIssue",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "boResolveStop",
                    "BOReadyToIssue",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    "boResolveStop",
                    "BOReadyToIssue",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "boResolveStop",
                    "BOReadyToIssue",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "boResolveStop",
                    "BOReadyToIssue",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "resolveCWEscalation",
                    "BOReadyToIssue",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    "resolveCWEscalation",
                    "BOReadyToIssue",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "resolveCWEscalation",
                    "BOReadyToIssue",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "resolveCWEscalation",
                    "BOReadyToIssue",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "changeState",
                    "BOReadyToIssue",
                    additionalData(false, "intestacy", false, 
                            Collections.emptyList(), false),
                    List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
            ),
            Arguments.of(
                    "changeState",
                    "BOReadyToIssue",
                    additionalData(true, "intestacy", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "changeState",
                    "BOReadyToIssue",
                    additionalData(false, "other", false, 
                            Collections.emptyList(), false),
                    Collections.emptyList()
            ),
            Arguments.of(
                    "changeState",
                    "BOReadyToIssue",
                    additionalData(false, "intestacy", false, 
                            handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                    Collections.emptyList()
            )
        );
    }

}
