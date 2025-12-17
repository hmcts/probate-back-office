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
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_CANCELLATION_ST_CIC_CRIMINALINJURIESCOMPENSATION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CATEGORY_HEARING_BUNDLE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CATEGORY_HEARING_COMPLETION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CATEGORY_ISSUE_CASE;
import static uk.gov.hmcts.probate.dmnutils.CancellationScenarioBuilder.event;

class CamundaTaskWaCancellationTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CANCELLATION_ST_CIC_CRIMINALINJURIESCOMPENSATION;
    }

    public static Stream<Arguments> scenarioProvider() {
        return Stream.of(
                event("caseworker-close-the-case")
                        .cancelAll()
                        .build(),
                event("caseworker-postpone-hearing")
                        .cancel(PROCESS_CATEGORY_HEARING_COMPLETION)
                        .cancel(PROCESS_CATEGORY_HEARING_BUNDLE)
                        .build(),
                event("caseworker-cancel-hearing")
                        .cancel(PROCESS_CATEGORY_HEARING_COMPLETION)
                        .cancel(PROCESS_CATEGORY_HEARING_BUNDLE)
                        .build(),
                event("refer-to-judge")
                        .cancel(PROCESS_CATEGORY_ISSUE_CASE)
                        .build(),
                event("refer-to-legal-officer")
                        .cancel(PROCESS_CATEGORY_ISSUE_CASE)
                        .build()
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(3));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(4));
    }

    @ParameterizedTest(name = "from state: {0}, event id: {1}, state: {2}")
    @MethodSource("scenarioProvider")
    void given_multiple_event_ids_should_evaluate_dmn(String fromState,
                                                      String eventId,
                                                      String state,
                                                      List<Map<String, Object>> expectedDmnOutcome) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("fromState", fromState);
        inputVariables.putValue("event", eventId);
        inputVariables.putValue("state", state);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertThat(dmnDecisionTableResult.getResultList(), is(expectedDmnOutcome));
    }

}
