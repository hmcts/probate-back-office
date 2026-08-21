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
import uk.gov.hmcts.probate.dmnutils.CaseDataBuilder;
import uk.gov.hmcts.probate.dmnutils.ConfigurationExpectationBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_CONFIGURATION_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DE_BONIS_NON;
import static uk.gov.hmcts.probate.dmnutils.CamundaVerifier.resultsMatchUsingNameKey;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_POWER_OF_ATTORNEY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_RESEAL_FOREIGN_GRANT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_SECTION_116;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REFERENCE_VALUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WINDRUSH_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_FIAT_WILL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_HORIZON_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WITNESS_INTERVIEW;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DOUBLE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INCAPACITY_UNDER_RULE_35;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LEADING_OR_FOLLOWING_GRANTS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME_CASE_PRINTED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_RECTIFY_WILL_OR_CODICIL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_CODICIL_MIS_RECITAL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LITERARY_ESTATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LOST_WILL_OR_CODICIL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_MINORITY_INTEREST;

class CamundaTaskWaConfigurationTest extends DmnDecisionTableBaseUnitTest {

    private static final String taskId = UUID.randomUUID().toString();
    private static final String roleAssignmentId = UUID.randomUUID().toString();

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_PROBATE;
    }

    static Stream<Arguments> scenarioProvider() {
        return Stream.of(
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_PROBATE,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        HANDLE_EVIDENCE_EVENT,
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("state", CASE_PRINTED_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_PROBATE,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT,
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("state", CASE_PRINTED_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_ADMON,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        HANDLE_EVIDENCE_EVENT,
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("state", CASE_PRINTED_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_INTESTACY,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        "handleEvidence",
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("state", CASE_PRINTED_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_DE_BONIS_NON,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        HANDLE_EVIDENCE_EVENT,
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                          Map.of("state", READY_TO_ISSUE_STATE, "taskType", EXAMINE_DE_BONIS_NON)).build()
                ),
                Arguments.of(
                    EXAMINE_FIAT_WILL,
                    CaseDataBuilder.defaultWaCase()
                        .isUrgent()
                        .build(),
                    HANDLE_EVIDENCE_EVENT,
                    ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                            Map.of("taskType", EXAMINE_FIAT_WILL, "state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                    EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY,
                    CaseDataBuilder.defaultWaCase()
                        .isUrgent()
                        .build(),
                    HANDLE_EVIDENCE_EVENT,
                    ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                            Map.of("taskType", EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY,
                                    "state", READY_TO_ISSUE_STATE)).build()
                ),
               Arguments.of(
                   EXAMINE_WINDRUSH_SCHEME,
                    CaseDataBuilder.defaultWaCase()
                        .isUrgent()
                        .build(),
                    "handleEvidence",
                    ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                        Map.of("taskType", EXAMINE_WINDRUSH_SCHEME,
                            "state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                    EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE,
                    CaseDataBuilder.defaultWaCase()
                            .isUrgent()
                            .build(),
                    HANDLE_EVIDENCE_EVENT,
                    ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                            Map.of("state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        HANDLE_EVIDENCE_EVENT,
                        ConfigurationExpectationBuilder
                                .examineDigitalCaseExpectationsForConditions(
                                        Map.of("state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                    EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED,
                        CaseDataBuilder.defaultWaCase()
                            .isUrgent()
                            .build(),
                        "handleEvidence",
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("taskType", EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED, "state",
                                        READY_TO_ISSUE_STATE)).build()
                ),
            Arguments.of(
                EXAMINE_WITNESS_INTERVIEW,
                    CaseDataBuilder.defaultWaCase()
                        .isUrgent()
                        .build(),
                    "handleEvidence",
                    ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                            Map.of("taskType", EXAMINE_WITNESS_INTERVIEW, "state", READY_TO_ISSUE_STATE)).build()
            ),
            Arguments.of(
                EXAMINE_HORIZON_SCHEME,
                    CaseDataBuilder.defaultWaCase()
                        .isUrgent()
                        .build(),
                    "handleEvidence",
                    ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                            Map.of("taskType", EXAMINE_HORIZON_SCHEME, "state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA,
                        CaseDataBuilder.defaultWaCase()
                                .isUrgent()
                                .build(),
                        "handleEvidence",
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("taskType", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA,
                                        "state", CASE_PRINTED_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        HANDLE_EVIDENCE_EVENT,
                        ConfigurationExpectationBuilder
                                .examineDigitalCaseExpectationsForConditions(
                                        Map.of("state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                    EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE,
                    CaseDataBuilder.defaultWaCase().isUrgent().build(),
                    HANDLE_EVIDENCE_EVENT,
                    ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                            Map.of("state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_DOUBLE_PROBATE,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        HANDLE_EVIDENCE_EVENT,
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("taskType", EXAMINE_DOUBLE_PROBATE,
                                        "state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_INCAPACITY_UNDER_RULE_35,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        HANDLE_EVIDENCE_EVENT,
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("taskType", EXAMINE_INCAPACITY_UNDER_RULE_35,
                                        "state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_LEADING_OR_FOLLOWING_GRANTS,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        HANDLE_EVIDENCE_EVENT,
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("taskType", EXAMINE_LEADING_OR_FOLLOWING_GRANTS,
                                        "state", READY_TO_ISSUE_STATE)).build()
            ),
            Arguments.of(
                EXAMINE_SECTION_116,
                    CaseDataBuilder.defaultWaCase()
                            .isUrgent()
                            .build(),
                    "handleEvidence",
                    ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                            Map.of("taskType", EXAMINE_SECTION_116, "state", READY_TO_ISSUE_STATE)).build()
            ),
            Arguments.of(
                EXAMINE_POWER_OF_ATTORNEY,
                    CaseDataBuilder.defaultWaCase()
                            .isUrgent()
                            .build(),
                    "handleEvidence",
                    ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                            Map.of("taskType", EXAMINE_POWER_OF_ATTORNEY, "state", READY_TO_ISSUE_STATE)).build()
            ),
            Arguments.of(
                EXAMINE_RESEAL_FOREIGN_GRANT,
                    CaseDataBuilder.defaultWaCase()
                            .isUrgent()
                            .build(),
                    "handleEvidence",
                    ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                            Map.of("taskType", EXAMINE_RESEAL_FOREIGN_GRANT, "state", READY_TO_ISSUE_STATE)).build()
            ),
            Arguments.of(
                EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME,
                    CaseDataBuilder.defaultWaCase()
                            .isUrgent()
                            .build(),
                    "handleEvidence",
                    ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                            Map.of("taskType", EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME, "state", READY_TO_ISSUE_STATE))
                            .build()
                ),
                Arguments.of(
                        EXAMINE_CODICIL_MIS_RECITAL,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        HANDLE_EVIDENCE_EVENT,
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("taskType", EXAMINE_CODICIL_MIS_RECITAL,
                                        "state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_RECTIFY_WILL_OR_CODICIL,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        HANDLE_EVIDENCE_EVENT,
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("taskType", EXAMINE_RECTIFY_WILL_OR_CODICIL,
                                        "state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME_CASE_PRINTED,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        HANDLE_EVIDENCE_EVENT,
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("taskType", EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME_CASE_PRINTED,
                                        "state", CASE_PRINTED_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_LITERARY_ESTATE,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        "handleEvidence",
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("taskType", EXAMINE_LITERARY_ESTATE, "state", READY_TO_ISSUE_STATE)).build()
                ),
                Arguments.of(
                        EXAMINE_LOST_WILL_OR_CODICIL,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        "handleEvidence",
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                        Map.of("taskType", EXAMINE_LOST_WILL_OR_CODICIL, "state", READY_TO_ISSUE_STATE))
                                .build()
                ),
                Arguments.of(
                        EXAMINE_MINORITY_INTEREST,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        "handleEvidence",
                        ConfigurationExpectationBuilder.examineDigitalCaseExpectationsForConditions(
                                Map.of("taskType", EXAMINE_MINORITY_INTEREST, "state", READY_TO_ISSUE_STATE)).build()
                )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(2));
        assertThat(logic.getOutputs().size(), is(3));
        assertEquals(17, logic.getRules().size());
    }

    @ParameterizedTest(name = "task type: {0} case data: {1}")
    @MethodSource("scenarioProvider")
    void should_return_correct_configuration_values_for_scenario(
            String taskType, Map<String, Object> caseData,
            String eventId,
            List<Map<String, Object>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();

        Map<String, String> taskAttributes = Map.of(
                "taskType", taskType,
                "roleAssignmentId", roleAssignmentId,
                "taskId", taskId,
                "caseId", REFERENCE_VALUE
        );
        inputVariables.putValue("taskAttributes", taskAttributes);
        inputVariables.putValue("taskType", taskType);
        inputVariables.putValue("caseData", caseData);
        inputVariables.putValue("eventId", eventId);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        resultsMatchUsingNameKey(dmnDecisionTableResult.getResultList(), expectation);
    }
}
