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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_INITIATION_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_CTSC;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROUTINE_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaUtils.mapAdditionalData;
import static uk.gov.hmcts.probate.dmnutils.CamundaUtils.resultsMatchUsingNameKey;

class CamundaTaskWaInitiationTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_INITIATION_PROBATE;
    }


    /**
     * Builds the variable in the shape WA passes to the DMN at runtime.
     * The initiation DMN reads evidenceHandled and caseType from additionalData.Data.* via a
     * FEEL expression
     */
    private static Map<String, Object> additionalData(String handleSuppEvidence, String caseType) {
        return Map.of("Data", Map.of(
                "evidenceHandled", handleSuppEvidence,
                "caseType", caseType
        ));
    }

    static Stream<Arguments> scenarioProvider() {

        Map<String, Object> examineDigitalCaseProbate7Days = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_PROBATE,
                "name", "Examine Digital Case - Probate",
                "workingDaysAllowed", 7,
                "processCategories", "case progression",
                "workType", ROUTINE_WORK_TYPE,
                "roleCategory", ROLE_CATEGORY_CTSC
        );

        Map<String, Object> examineDigitalCaseProbate10Days = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_PROBATE,
                "name", "Examine Digital Case - Probate",
                "workingDaysAllowed", 10,
                "processCategories", "case progression",
                "workType", ROUTINE_WORK_TYPE,
                "roleCategory", ROLE_CATEGORY_CTSC
        );

        return Stream.of(
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData("No", "gop"),
                        List.of(examineDigitalCaseProbate7Days)
                ),
                Arguments.of(
                        "applyforGrantPaperApplication",
                        "CasePrinted",
                        additionalData("No", "gop"),
                        List.of(examineDigitalCaseProbate7Days)
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData("No", "gop"),
                        List.of(examineDigitalCaseProbate10Days)
                )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(4));
        assertThat(logic.getOutputs().size(), is(7));
        assertThat(logic.getRules().size(), is(5));
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1}")
    @MethodSource("scenarioProvider")
    void given_multiple_event_ids_should_evaluate_dmn(String eventId,
                                                      String postEventState,
                                                      Map<String, Object> additionalData,
                                                      List<Map<String, Object>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue("additionalData", additionalData);
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        resultsMatchUsingNameKey(dmnDecisionTableResult.getResultList(), expectation);
    }
}
