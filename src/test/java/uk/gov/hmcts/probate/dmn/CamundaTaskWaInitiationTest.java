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
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_FIAT_WILL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WINDRUSH_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.CamundaVerifier.resultsMatchUsingNameKey;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_HORIZON_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WITNESS_INTERVIEW;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.PROBATE_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ADMON_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.AD_COLLIGENDA_BONA_TASK_TYPE_NAME;
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
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DOUBLE_PROBATE_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DOUBLE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INCAPACITY_UNDER_RULE_35;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INCAPACITY_UNDER_RULE_35_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LEADING_OR_FOLLOWING_GRANTS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LEADING_OR_FOLLOWING_GRANTS_TASK_TYPE_NAME;

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
    private static final String deBonisNonHandOffReason = "DeBonisNon";
    private static final String infectedBloodCompensationAuthorityHandOffReason = "IBCA";
    private static final String doubleProbateHandOffReason = "DoubleProbate";
    private static final String fiatWillHandOffReason = "FiatWill";
    private static final String invalidHandOffReason = "OtherReason";
    private static final String incapacityUnderRule35HandOffReason = "IncapacityRule35";
    private static final String leadingFollowingGrantsHandOffReason = "LeadingFollowing Grants";
    private static final String windrushSchemeHandOffReason = "WindrushScheme";

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

    private static Map<String, Map<String, Object>> additionalDataNoHandOffList() {
        return Map.of(
                "Data", Map.of(
                        evidenceHandledVar, false,
                        caseTypeVar, "",
                        caseHandedOffToLegacySiteVar, true
                )
        );
    }

    private static List<Map<String,Object>> handOffReasonListWithHandOffReason(String handOffReason) {
        return List.of(
                Map.of(
                        "id", defaultHandOffReasonId,
                        "value", Map.of("caseHandoffReason", handOffReason)
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

    private static final List<Map<String,Object>> handOffReasonListInfectedBloodCompensationAuthority = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "IBCA")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
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

    private static final List<Map<String,Object>> handOffReasonListWillOrCodicilToBeNotated = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "WillCodicilNotated")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListWitnessInterview = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "WitnessInterview")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListHorizonScheme = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "HorizonScheme")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListOtherReason = List.of(
            Map.of(
                    "id", defaultHandOffReasonId,
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListWindrush = List.of(
        Map.of(
            "id", "df3be732-2172-49da-80fe-cad8586e4928",
            "value", Map.of("caseHandoffReason", "WindrushScheme")
        ),
        Map.of(
            "id", "df3be732-2172-49da-80fe-cad8586e4928",
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
                        additionalData(false, "gop", true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false,
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
                        additionalData(false, "gop", false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
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
                        additionalData(false, "gop", false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
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
                        additionalData(false, "gop", false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
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
                        additionalData(false, "gop", false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
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
                        additionalData(false, "gop", false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
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
                        additionalData(false, "gop", false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_FROM_BULK_SCAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", false, Collections.emptyList()),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        CREATE_CASE_FROM_BULK_SCAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "gop", false, Collections.emptyList()),
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
                        additionalData(false, "gop", false,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_FROM_BULK_SCAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "gop", true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
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

    static Stream<Arguments> intestacyScenarios() {

        Map<String, Object> examineDigitalCaseIntestacyTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_INTESTACY,
                "name", "Examine Digital Case - Intestacy",
                "processCategories", "case progression"
        );

        Map<String,Object> examineDigitalCaseIntestacyReadyToIssueTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE,
                "name", "Examine Digital Case - Intestacy",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "someOtherEventId",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "solicitorPaymentSuccessCase",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "solicitorPaymentSuccessCase",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "solicitorPaymentSuccessCase",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "solicitorPaymentSuccessCase",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCase",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "createCase",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCase",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCase",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCasePaymentSuccess",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "createCasePaymentSuccess",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCasePaymentSuccess",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCasePaymentSuccess",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "paymentSuccessApp",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "paymentSuccessApp",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "paymentSuccessApp",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "paymentSuccessApp",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                // BOReadyToIssue Tests
                Arguments.of(
                        "someOtherEventId",
                        "BOReadyToIssue",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "intestacy", false, Collections.emptyList()),
                        List.of(examineDigitalCaseIntestacyReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(true, "intestacy", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "other", false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason),
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
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(deBonisNonHandOffReason)),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(deBonisNonHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
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
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(deBonisNonHandOffReason)),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(deBonisNonHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
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
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(deBonisNonHandOffReason)),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(deBonisNonHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
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
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(deBonisNonHandOffReason)),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(deBonisNonHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
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
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
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
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason)),
                        List.of(examineFiatWillTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(true, "",true,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason)),
                        List.of(examineFiatWillTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(true, "",true,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason)),
                        List.of(examineFiatWillTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(true, "",true,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason)),
                        List.of(examineFiatWillTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
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
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> infectedBloodCompensationAuthorityScenarios() {

        Map<String,Object> examineInfectedBloodCompensationAuthorityTaskAttributes = Map.of(
                "taskId", EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY,
                "name", "Examine - Infected Blood Compensation Authority",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                        List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                        List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                        List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                        List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> windRushScenarios() {

        Map<String,Object> examineWindrushSchemeTaskAttributes = Map.of(
            "taskId", EXAMINE_WINDRUSH_SCHEME,
            "name", "Examine - Windrush Scheme",
            "processCategories", "case progression"
        );

        return Stream.of(
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWithHandOffReason(windrushSchemeHandOffReason)),
                List.of(examineWindrushSchemeTaskAttributes)
            ),
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                additionalData(false, "",false, handOffReasonListWithHandOffReason(windrushSchemeHandOffReason)),
                Collections.emptyList()
            ),
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWithHandOffReason(invalidHandOffReason)),
                Collections.emptyList()
            ),
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                additionalData(false, "",true, Collections.emptyList()),
                Collections.emptyList()
            ),
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                additionalDataNoHandOffList(),
                Collections.emptyList()
            ),
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                null,
                Collections.emptyList()
            ),
            Arguments.of(
                "boResolveStop",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWithHandOffReason(windrushSchemeHandOffReason)),
                List.of(examineWindrushSchemeTaskAttributes)
            ),
            Arguments.of(
                "boResolveStop",
                "BOReadyToIssue",
                additionalData(false, "",false, handOffReasonListWithHandOffReason(windrushSchemeHandOffReason)),
                Collections.emptyList()
            ),
            Arguments.of(
                "boResolveStop",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWithHandOffReason(invalidHandOffReason)),
                Collections.emptyList()
            ),
            Arguments.of(
                "boResolveStop",
                "BOReadyToIssue",
                additionalData(false, "",true, Collections.emptyList()),
                Collections.emptyList()
            ),
            Arguments.of(
                "boResolveStop",
                "BOReadyToIssue",
                additionalDataNoHandOffList(),
                Collections.emptyList()
            ),
            Arguments.of(
                "resolveCWEscalation",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWithHandOffReason(windrushSchemeHandOffReason)),
                List.of(examineWindrushSchemeTaskAttributes)
            ),
            Arguments.of(
                "resolveCWEscalation",
                "BOReadyToIssue",
                additionalData(false, "",false, handOffReasonListWithHandOffReason(windrushSchemeHandOffReason)),
                Collections.emptyList()
            ),
            Arguments.of(
                "resolveCWEscalation",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWithHandOffReason(invalidHandOffReason)),
                Collections.emptyList()
            ),
            Arguments.of(
                "resolveCWEscalation",
                "BOReadyToIssue",
                additionalData(false, "",true, Collections.emptyList()),
                Collections.emptyList()
            ),
            Arguments.of(
                "changeState",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWithHandOffReason(windrushSchemeHandOffReason)),
                List.of(examineWindrushSchemeTaskAttributes)
            ),
            Arguments.of(
                "changeState",
                "BOReadyToIssue",
                additionalData(false, "",false, handOffReasonListWithHandOffReason(windrushSchemeHandOffReason)),
                Collections.emptyList()
            ),
            Arguments.of(
                "changeState",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWithHandOffReason(invalidHandOffReason)),
                Collections.emptyList()
            ),
            Arguments.of(
                "changeState",
                "BOReadyToIssue",
                additionalData(false, "",true, Collections.emptyList()),
                Collections.emptyList()
            ),
            Arguments.of(
                "changeState",
                "BOReadyToIssue",
                additionalDataNoHandOffList(),
                Collections.emptyList()
            )
        );
    }

    static Stream<Arguments> willOrCodicilToBeNotatedScenarios() {

        Map<String,Object> examineWillOrCodicilToBeNotatedTaskAttributes = Map.of(
                "taskId", EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED,
                "name", "Examine - Will or Codicil to be Notated",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWillOrCodicilToBeNotated),
                        List.of(examineWillOrCodicilToBeNotatedTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWillOrCodicilToBeNotated),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWillOrCodicilToBeNotated),
                        List.of(examineWillOrCodicilToBeNotatedTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWillOrCodicilToBeNotated),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWillOrCodicilToBeNotated),
                        List.of(examineWillOrCodicilToBeNotatedTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWillOrCodicilToBeNotated),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWillOrCodicilToBeNotated),
                        List.of(examineWillOrCodicilToBeNotatedTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWillOrCodicilToBeNotated),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> witnessInterviewScenarios() {

        Map<String,Object> examineWitnessInterviewTaskAttributes = Map.of(
                "taskId", EXAMINE_WITNESS_INTERVIEW,
                "name", "Examine - Witness Interview",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWitnessInterview),
                        List.of(examineWitnessInterviewTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWitnessInterview),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWitnessInterview),
                        List.of(examineWitnessInterviewTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWitnessInterview),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWitnessInterview),
                        List.of(examineWitnessInterviewTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWitnessInterview),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWitnessInterview),
                        List.of(examineWitnessInterviewTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWitnessInterview),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> horizonSchemeScenarios() {

        Map<String,Object> examineHorizonSchemeTaskAttributes = Map.of(
                "taskId", EXAMINE_HORIZON_SCHEME,
                "name", "Examine - Horizon Scheme",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListHorizonScheme),
                        List.of(examineHorizonSchemeTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListHorizonScheme),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListHorizonScheme),
                        List.of(examineHorizonSchemeTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListHorizonScheme),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListHorizonScheme),
                        List.of(examineHorizonSchemeTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListHorizonScheme),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListHorizonScheme),
                        List.of(examineHorizonSchemeTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListHorizonScheme),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> adColligendaBonaScenarios() {

        Map<String,Object> examineDigitalCaseAdColligendaBonaTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA,
                "name", "Examine Digital Case - Ad Colligenda Bona",
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
                        "someOtherEventId",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(true, "adColligendaBona",false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(true, "adColligendaBona",false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(true, "adColligendaBona",false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),

                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(true, "adColligendaBona",false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "adColligendaBona",false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "someOtherEventId",
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(true, "adColligendaBona",false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(true, "adColligendaBona",false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(true, "adColligendaBona",false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false, handOffReasonListOtherReason),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",
                                false, Collections.emptyList()),
                        List.of(examineDigitalCaseAdColligendaBonaReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(true, "adColligendaBona",false, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "adColligendaBona",false, handOffReasonListOtherReason),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> doubleProbateScenarios() {

        Map<String,Object> examineDigitalCaseDoubleProbateReadyToIssueTaskAttributes
                = Map.of(
                        "taskId", EXAMINE_DOUBLE_PROBATE,
                        "name", EXAMINE_DOUBLE_PROBATE_TASK_TYPE_NAME,
                        "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(doubleProbateHandOffReason)),
                        List.of(examineDigitalCaseDoubleProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(doubleProbateHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(doubleProbateHandOffReason)),
                        List.of(examineDigitalCaseDoubleProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(doubleProbateHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(doubleProbateHandOffReason)),
                        List.of(examineDigitalCaseDoubleProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(doubleProbateHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(doubleProbateHandOffReason)),
                        List.of(examineDigitalCaseDoubleProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(doubleProbateHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> incapacityUnderRule35Scenarios() {

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
                                handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason)),
                        List.of(examineDigitalCaseIncapacityUnderRule35TaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason)),
                        List.of(examineDigitalCaseIncapacityUnderRule35TaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason)),
                        List.of(examineDigitalCaseIncapacityUnderRule35TaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason)),
                        List.of(examineDigitalCaseIncapacityUnderRule35TaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(incapacityUnderRule35HandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> leadingOrFollowingGrantsScenarios() {

        Map<String,Object> examineDigitalCaseLeadingOrFollowingGrantsTaskAttributes
                = Map.of(
                        "taskId", EXAMINE_LEADING_OR_FOLLOWING_GRANTS,
                        "name", EXAMINE_LEADING_OR_FOLLOWING_GRANTS_TASK_TYPE_NAME,
                        "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(leadingFollowingGrantsHandOffReason)),
                        List.of(examineDigitalCaseLeadingOrFollowingGrantsTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(leadingFollowingGrantsHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(leadingFollowingGrantsHandOffReason)),
                        List.of(examineDigitalCaseLeadingOrFollowingGrantsTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(leadingFollowingGrantsHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(leadingFollowingGrantsHandOffReason)),
                        List.of(examineDigitalCaseLeadingOrFollowingGrantsTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(leadingFollowingGrantsHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(leadingFollowingGrantsHandOffReason)),
                        List.of(examineDigitalCaseLeadingOrFollowingGrantsTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(leadingFollowingGrantsHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }



    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(7));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(21));
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} evidenceHandled: {2} caseType: {3}")
    @MethodSource({"probateScenarios","admonScenarios","deBonisNonScenarios", "fiatWillScenarios",
        "infectedBloodCompensationAuthorityScenarios","windRushScenarios","willOrCodicilToBeNotatedScenarios",
        "witnessInterviewScenarios", "horizonSchemeScenarios","intestacyScenarios","adColligendaBonaScenarios",
        "doubleProbateScenarios","incapacityUnderRule35Scenarios","leadingOrFollowingGrantsScenarios"})
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
