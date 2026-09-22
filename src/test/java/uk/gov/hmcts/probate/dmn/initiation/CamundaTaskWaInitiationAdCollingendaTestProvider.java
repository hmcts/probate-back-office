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
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.AD_COLLIGENDA_BONA_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;


public class CamundaTaskWaInitiationAdCollingendaTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

        Map<String,Object> examineDigitalCaseAdColligendaBonaTaskAttributes
                = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA,
                "name", AD_COLLIGENDA_BONA_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        Map<String,Object> examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes
                = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE,
                "name", AD_COLLIGENDA_BONA_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "adColligendaBona",
                                false, emptyList(), true),
                        List.of(examineDigitalCaseAdColligendaBonaTaskAttributes)
                ),
                Arguments.of(
                        "someOtherEventId",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(true, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(true, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(true, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),

                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(true, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "someOtherEventId",
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, Collections.emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(true, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, Collections.emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(true, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, Collections.emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(true, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, Collections.emptyList(), false),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(true, "adColligendaBona",false,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                )
        );
    }

}
