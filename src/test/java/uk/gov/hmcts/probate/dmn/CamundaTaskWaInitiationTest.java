package uk.gov.hmcts.probate.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.probate.DmnDecisionTableBaseUnitTest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_INITIATION_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DE_BONIS_NON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_FIAT_WILL;
import static uk.gov.hmcts.probate.dmnutils.CamundaVerifier.resultsMatchUsingNameKey;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.PROBATE_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ADMON_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.AD_COLLIGENDA_BONA_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.INTESTACY_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DE_BONIS_NON_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.FIAT_WILL_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ATTACH_SCANNED_DOCS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CREATE_CASE_FROM_BULK_SCAN_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;

class CamundaTaskWaInitiationTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_INITIATION_PROBATE;
    }

    private static final String defaultHandOffReasonId = "df3be732-2172-49da-80fe-cad8586e4928";
    private static final String caseTypeVar = "caseType";
    private static final String evidenceHandledVar = "evidenceHandled";
    private static final String caseHandedOffToLegacySiteVar = "caseHandedOffToLegacySite";
    private static final String boHandoffReasonListVar = "boHandoffReasonList";

    private static Map<String, Map<String, Object>> additionalData(boolean evidenceHandled,
                                                                   String caseType,
                                                                   boolean caseHandedOffToLegacySite,
                                                                   List<Map<String,Object>> boHandoffReasonList) {
        return Map.of(
                "Data", Map.of(
                        evidenceHandledVar, evidenceHandled,
                        caseTypeVar, caseType,
                        caseHandedOffToLegacySiteVar, caseHandedOffToLegacySite,
                        boHandoffReasonListVar, boHandoffReasonList
                )
        );
    }

    private static Map<String, Map<String, Object>> additionalDataNoHandOffList(String caseType,
                                                                                boolean caseHandedOffToLegacySite) {
        return Map.of(
                "Data", Map.of(
                        evidenceHandledVar, false,
                        caseTypeVar, caseType,
                        caseHandedOffToLegacySiteVar, caseHandedOffToLegacySite
                )
        );
    }

    private static final List<Map<String,Object>> handOffReasonListFiatWill = List.of(
            Map.of(
                    "id", defaultHandOffReasonId,
                    "value", Map.of("caseHandoffReason", "FiatWill")
            ),
            Map.of(
                    "id", defaultHandOffReasonId,
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListDeBonisNon = List.of(
            Map.of(
                    "id", defaultHandOffReasonId,
                    "value", Map.of("caseHandoffReason", "DeBonisNon")
            ),
            Map.of(
                    "id", defaultHandOffReasonId,
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListOtherReason = List.of(
            Map.of(
                    "id", defaultHandOffReasonId,
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    static Stream<Arguments> probateScenarios() {

        Map<String,Object> examineDigitalCaseProbateTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_PROBATE,
                "name", PROBATE_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        Map<String,Object> examineDigitalCaseProbateReadyToIssueTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE,
                "name", PROBATE_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );


        return Stream.of(
                Arguments.of(
                        "someOtherEventId",
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "gop", false, Collections.emptyList()),
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
                        additionalData(false, "gop", true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "gop", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                  APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "gop", false, Collections.emptyList()),
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
                        additionalData(false, "gop", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "gop", false, Collections.emptyList()),
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
                        additionalData(false, "gop", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "gop", false, Collections.emptyList()),
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
                        additionalData(false, "gop", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "gop", false, Collections.emptyList()),
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
                        additionalData(false, "gop", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "gop", false, Collections.emptyList()),
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
                        additionalData(false, "gop", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true, handOffReasonListOtherReason),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> admonScenarios() {

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
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "admonWill", false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
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
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason),
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
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason),
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
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason),
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
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "admonWill", false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
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
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason),
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
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "admonWill", false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
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
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason),
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
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "admonWill", false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
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
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason),
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
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_FROM_BULK_SCAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> deBonisNonScenarios() {

        Map<String,Object> examineDeBonisNonTaskAttributes = Map.of(
                "taskId", EXAMINE_DE_BONIS_NON,
                "name", DE_BONIS_NON_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListDeBonisNon),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false, handOffReasonListDeBonisNon),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalDataNoHandOffList("",true),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListDeBonisNon),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false, handOffReasonListDeBonisNon),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalDataNoHandOffList("",true),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListDeBonisNon),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false, handOffReasonListDeBonisNon),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListDeBonisNon),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false, handOffReasonListDeBonisNon),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalDataNoHandOffList("",true),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> intestacyScenarios() {

        Map<String,Object> examineDigitalCaseIntestacyReadyToIssueTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE,
                "name", INTESTACY_TASK_TYPE_NAME,
                "processCategories", "case progression"
          );
  
  
        return Stream.of(
              Arguments.of(
                      CHANGE_STATE_EVENT,
                      READY_TO_ISSUE_STATE,
                      additionalData(false, "intestacy",false, Collections.emptyList()),
                      List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
              ),
              Arguments.of(
                      RESOLVE_SME_REFERRAL_EVENT,
                      READY_TO_ISSUE_STATE,
                      additionalData(false, "intestacy",false, Collections.emptyList()),
                      List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
              ),
              Arguments.of(
                      HANDLE_EVIDENCE_EVENT,
                      READY_TO_ISSUE_STATE,
                      additionalData(false, "intestacy",false, Collections.emptyList()),
                      List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
              ),
              Arguments.of(
                      BO_RESOLVE_STOP_EVENT,
                      READY_TO_ISSUE_STATE,
                      additionalData(false, "intestacy",false, Collections.emptyList()),
                      List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
              )
        );
    }
  
    static Stream<Arguments> fiatWillScenarios() {

        Map<String,Object> examineFiatWillTaskAttributes = Map.of(
                "taskId", EXAMINE_FIAT_WILL,
                "name", FIAT_WILL_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );
            

        return Stream.of(
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListFiatWill),
                        List.of(examineFiatWillTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListFiatWill),
                        List.of(examineFiatWillTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListFiatWill),
                        List.of(examineFiatWillTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, handOffReasonListFiatWill),
                        List.of(examineFiatWillTaskAttributes)
                )
        );

    }

    static Stream<Arguments> adCollScenarios() {

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
                        additionalData(false, "adColligendaBona",false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                )
        );
    }



        @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(7));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(9));
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} evidenceHandled: {2} caseType: {3}")
    @MethodSource({
        "probateScenarios",
        "admonScenarios",
        "deBonisNonScenarios",
        "fiatWillScenarios",
        "intestacyScenarios",
        "adCollScenarios"})
    void given_multiple_event_ids_should_evaluate_dmn_for_probate_scenarios(String eventId,
                                                      String postEventState,
                                                      Map<String, Object> additionalData,
                                                      List<Map<String, Object>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        if (additionalData != null) {
            inputVariables.putValue("additionalData", additionalData);
        }
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        resultsMatchUsingNameKey(dmnDecisionTableResult.getResultList(), expectation);
    }
 
}
