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
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_COMPLETION_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaVerifier.resultsMatchUsingTaskTypeKey;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.AUTO_COMPLETE_MODE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_FIAT_WILL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DE_BONIS_NON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DOUBLE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INCAPACITY_UNDER_RULE_35;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LEADING_OR_FOLLOWING_GRANTS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_POWER_OF_ATTORNEY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_RESEAL_FOREIGN_GRANT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_SECTION_116;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WINDRUSH_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_HORIZON_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WITNESS_INTERVIEW;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA;

class CamundaTaskCompletionTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_COMPLETION_PROBATE;
    }

    static Stream<Arguments> scenarioProvider() {

        return Stream.of(
                Arguments.of(
                        "boSelectForQA",
                        List.of(
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_PROBATE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_INTESTACY
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_ADMON
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA
                                )
                        )
                ),
                Arguments.of(
                        "boStopCaseForCasePrinted",
                        List.of(
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_PROBATE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_ADMON
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_INTESTACY
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA
                                )
                        )
                ),
                Arguments.of(
                        "boStopCaseForCaseMatchingForExamining",
                        List.of(
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DE_BONIS_NON
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_FIAT_WILL
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WINDRUSH_SCHEME
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WITNESS_INTERVIEW
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_HORIZON_SCHEME
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DOUBLE_PROBATE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_INCAPACITY_UNDER_RULE_35
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_LEADING_OR_FOLLOWING_GRANTS
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_SECTION_116
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_POWER_OF_ATTORNEY
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_RESEAL_FOREIGN_GRANT
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME
                                )
                        )
                ),
                Arguments.of(
                        "moveToCWEscalation",
                        List.of(
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_PROBATE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_ADMON
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DE_BONIS_NON
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_FIAT_WILL
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WINDRUSH_SCHEME
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WITNESS_INTERVIEW
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_HORIZON_SCHEME
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_INTESTACY
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DOUBLE_PROBATE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_INCAPACITY_UNDER_RULE_35
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_LEADING_OR_FOLLOWING_GRANTS
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_SECTION_116
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_POWER_OF_ATTORNEY
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_RESEAL_FOREIGN_GRANT
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME
                                )
                        )
                ),
                Arguments.of(
                       "boEscalateToRegistrar",
                        List.of(
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_PROBATE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_ADMON
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DE_BONIS_NON
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_FIAT_WILL
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WINDRUSH_SCHEME
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WITNESS_INTERVIEW
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_HORIZON_SCHEME
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_INTESTACY
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DOUBLE_PROBATE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_INCAPACITY_UNDER_RULE_35
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_LEADING_OR_FOLLOWING_GRANTS
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_SECTION_116
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_POWER_OF_ATTORNEY
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_RESEAL_FOREIGN_GRANT
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME
                                )
                        )
                ),
                Arguments.of(
                        "boIssueGrantForCaseMatching",
                        List.of(
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_PROBATE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_ADMON
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DE_BONIS_NON
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_FIAT_WILL
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WINDRUSH_SCHEME
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_WITNESS_INTERVIEW
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_HORIZON_SCHEME
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_INTESTACY
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE
                                ),
                                Map.of(
                                    "completionMode", AUTO_COMPLETE_MODE,
                                    "taskType", EXAMINE_DOUBLE_PROBATE
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_INCAPACITY_UNDER_RULE_35
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_LEADING_OR_FOLLOWING_GRANTS
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_SECTION_116
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_POWER_OF_ATTORNEY
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_RESEAL_FOREIGN_GRANT
                                ),
                                Map.of(
                                        "completionMode", AUTO_COMPLETE_MODE,
                                        "taskType", EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME
                                )
                        )
                ),
                Arguments.of(
                        "otherEventId",
                        Collections.emptyList()
                )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(1));
        assertThat(logic.getOutputs().size(), is(2));
        assertThat(logic.getRules().size(), is(18));
    }

    @ParameterizedTest(name = "event id: {0}")
    @MethodSource("scenarioProvider")
    void given_event_ids_should_evaluate_dmn(String eventId, List<Map<String, Object>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        resultsMatchUsingTaskTypeKey(dmnDecisionTableResult.getResultList(), expectation);
    }

}
