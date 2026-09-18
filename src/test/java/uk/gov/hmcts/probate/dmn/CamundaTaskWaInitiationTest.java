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
import static uk.gov.hmcts.probate.dmnutils.CamundaVerifier.resultsMatchUsingNameKey;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DOUBLE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DOUBLE_PROBATE_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INCAPACITY_UNDER_RULE_35;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INCAPACITY_UNDER_RULE_35_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LEADING_OR_FOLLOWING_GRANTS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LEADING_OR_FOLLOWING_GRANTS_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;


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

    private static final List<Map<String,Object>> handOffReasonListSection116 = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "Section116")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListLiteraryEstate = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "LiteraryEstate")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListPowerOfAttorney = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "POA")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListLostWill = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "LostWill")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListResealForeignGrant = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "ResealForeignGrant")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListMinorityInterest = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "MinorityInterest")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListInfectedBloodInterimScheme = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "IBIS")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

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
        assertThat(logic.getInputs().size(), is(8));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(46));
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} evidenceHandled: {2} caseType: {3}")
    @MethodSource({
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
