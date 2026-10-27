package uk.gov.hmcts.probate.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.probate.DmnDecisionTableBaseUnitTest;
import uk.gov.hmcts.probate.dmnutils.CancellationScenarioBuilder;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.wa.TaskTypes;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_CANCELLATION_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_CASE_CLOSED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_CASE_QA_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_CASE_STOPPED_AWAIT_REDEC_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_CASE_STOPPED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_REGISTRAR_ESCALATION;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CLOSE_AWAITING_DOCUMENTATION_HANDOFFS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CLOSE_READY_TO_ISSUE_HANDOFFS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;


class CamundaTaskWaCancellationTest extends DmnDecisionTableBaseUnitTest {

    private static final String
            WITHDRAW_APPLICATION_FOR_CASE_PRINTED_EVENT_ID = "boWithdrawApplicationForCasePrinted";
    private static final String
            WITHDRAW_APPLICATION_FOR_READY_TO_ISSUE_EVENT_ID = "boWithdrawApplicationForReadyToIssue";
    private static final String
            WITHDRAW_APPLICATION_FOR_CASE_STOPPED_EVENT_ID = "boWithdrawApplicationForCaseStopped";
    private static final String
            WITHDRAW_APPLICATION_FOR_REGISTER_ESCALATION_EVENT_ID = "boWithdrawApplicationForRegistrarEscalation";
    private static final String
            WITHDRAW_APPLICATION_FOR_REDECLARATION_SOT_EVENT_ID = "boWithdrawForRedeclarationSOT";
    private static final String
            WITHDRAW_APPLICATION_FOR_CASE_QA_EVENT_ID = "boWithdrawApplicationForCaseQA";

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CANCELLATION_PROBATE;
    }

    static Stream<Arguments> reconfigureScenarioProvider() {

        List<Map<String, String>> action = List.of(
                Map.of(
                        "action", "Reconfigure"
                )
        );
        return Stream.of(
                Arguments.of(
                        null,
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT,
                        CASE_PRINTED_STATE,
                        action
                ),
                Arguments.of(
                        null,
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        action
                ),
                Arguments.of(
                        null,
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        action
                ),
                Arguments.of(
                        null,
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        action
                ),
                Arguments.of(
                        null,
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        action
                ),
                Arguments.of(
                        null,
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        action
                ),
                Arguments.of(
                        null,
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        action
                )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(3));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(23));
    }

    @ParameterizedTest(name = "[{index}] map data: {0}")
    @ArgumentsSource(CancellationScenarioBuilder.class)
    void given_multiple_event_ids_should_evaluate_dmn(Map<String, String> cancellationProperties) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables = putAllCancellationProperties(inputVariables, cancellationProperties);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        List<Map<String, Object>> dmnResultList = dmnDecisionTableResult.getResultList();

        if (cancellationProperties.containsValue(WITHDRAW_APPLICATION_FOR_CASE_PRINTED_EVENT_ID)
                || cancellationProperties.containsValue(WITHDRAW_APPLICATION_FOR_READY_TO_ISSUE_EVENT_ID)
                || cancellationProperties.containsValue(WITHDRAW_APPLICATION_FOR_CASE_STOPPED_EVENT_ID)
                || cancellationProperties.containsValue(WITHDRAW_APPLICATION_FOR_REGISTER_ESCALATION_EVENT_ID)
                || cancellationProperties.containsValue(WITHDRAW_APPLICATION_FOR_REDECLARATION_SOT_EVENT_ID)
                || cancellationProperties.containsValue(WITHDRAW_APPLICATION_FOR_CASE_QA_EVENT_ID)) {
            testBoWithdrawApplicationEvent(dmnResultList, cancellationProperties);
        } else if (cancellationProperties.containsValue(CLOSE_READY_TO_ISSUE_HANDOFFS)) {
            assertResponse(dmnResultList, cancellationProperties, READY_TO_ISSUE_STATE::equals);
        } else if (cancellationProperties.containsValue(CLOSE_AWAITING_DOCUMENTATION_HANDOFFS)
                || isAwaitingDocIceCancellationTask(cancellationProperties.get("event"))) {
            assertResponse(dmnResultList, cancellationProperties, CASE_PRINTED_STATE::equals);
        } else if (cancellationProperties.containsValue(BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE_EVENT)
                || cancellationProperties.containsValue(CLOSE_READY_TO_ISSUE_HANDOFFS)) {
            assertAmendCaseDetailsReadyToIssueResponse(dmnResultList, cancellationProperties);
        } else if (cancellationProperties.containsValue(CHANGE_STATE_EVENT)) {
            testChangeStateEvent(dmnResultList, cancellationProperties);
        } else {
            Assertions.assertEquals(0, dmnResultList.size());
        }
    }

    @ParameterizedTest(name = "[{index}] from state: {0}, event id: {1}, state: {2}")
    @MethodSource("reconfigureScenarioProvider")
    void given_multiple_event_ids_should_evaluate_dmn(String fromState,
                                                      String eventId,
                                                      String state,
                                                      List<Map<String, String>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("fromState", fromState);
        inputVariables.putValue("event", eventId);
        inputVariables.putValue("state", state);
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        Assertions.assertEquals(expectation, dmnDecisionTableResult.getResultList());
    }

    private boolean isAwaitingDocIceCancellationTask(String event) {
        return TaskTypes.examineAwaitingDocIceTasks().stream()
                .map(TaskTypes::getClosingEventId)
                .map(EventId::getName)
                .anyMatch(event::equals);
    }

    private void assertAmendCaseDetailsReadyToIssueResponse(List<Map<String, Object>> dmnResultList,
                                                            Map<String, String> cancellationProperties) {
        List<String> expectedProcessCategories = List.of("examineDigitalCaseTypes", "awaitingDocumentationHandOffs");
        long occurrence = cancellationProperties.values().stream()
                .filter(READY_TO_ISSUE_STATE::equals)
                .count();

        if (occurrence == 2) {
            List<Object> processCategories = dmnResultList.stream()
                    .filter(result -> "Cancel".equals(result.get("action")))
                    .map(result -> result.get("processCategories"))
                    .toList();

            Assertions.assertEquals(expectedProcessCategories,
                    processCategories);
        }
    }

    private void assertResponse(List<Map<String, Object>> dmnResultList,
                                Map<String, String> cancellationProperties,
                                Predicate<String> stateFilter) {
        long occurrence = cancellationProperties.values().stream()
                .filter(stateFilter)
                .count();

        if (occurrence == 2) {
            validate(dmnResultList, cancellationProperties);
        } else {
            Assertions.assertEquals(0, dmnResultList.size());
        }
    }


    private VariableMap putAllCancellationProperties(VariableMap inputVariables,
                                                 Map<String, String> cancellationProperties) {
        if (cancellationProperties != null && !cancellationProperties.isEmpty()) {
            inputVariables.putValue("event", cancellationProperties.get("event"));
            inputVariables.putValue("fromState", cancellationProperties.get("fromState"));
            inputVariables.putValue("state", cancellationProperties.get("state"));
            if (cancellationProperties.size() == 3) {
                return inputVariables;
            }
            inputVariables.putValue("action", cancellationProperties.get("action"));
            // For when it is a 'cancel all' type of task cancellation
            if (cancellationProperties.containsKey("processCategories")) {
                inputVariables.putValue("processCategories",
                        cancellationProperties.get("processCategories"));
            }
        }
        return inputVariables;
    }

    private void testBoWithdrawApplicationEvent(List<Map<String, Object>> dmnResultList,
                                                Map<String, String> cancellationProperties) {
        if ((cancellationProperties.containsValue(CASE_PRINTED_STATE)
                || cancellationProperties.containsValue(READY_TO_ISSUE_STATE)
                || cancellationProperties.containsValue(BO_CASE_STOPPED_STATE)
                || cancellationProperties.containsValue(BO_REGISTRAR_ESCALATION)
                || cancellationProperties.containsValue(BO_CASE_STOPPED_AWAIT_REDEC_STATE)
                || cancellationProperties.containsValue(BO_CASE_QA_STATE))
                && cancellationProperties.containsValue(BO_CASE_CLOSED)) {
            validate(dmnResultList, cancellationProperties);
        } else {
            Assertions.assertEquals(0, dmnResultList.size());
        }
    }

    private void validate(List<Map<String, Object>> dmnResultList, Map<String, String> cancellationProperties) {
        Assertions.assertEquals(1, dmnResultList.size());
        Assertions.assertEquals(dmnResultList.getFirst().get("processCategories"),
                cancellationProperties.get("processCategories"));
        Assertions.assertEquals(dmnResultList.getFirst().get("action"),
                cancellationProperties.get("action"));
    }

    private void testChangeStateEvent(List<Map<String, Object>> dmnResultList,
                                                Map<String, String> cancellationProperties) {
        Assertions.assertEquals(1, dmnResultList.size());
        Assertions.assertEquals(dmnResultList.getFirst().get("processCategories"),
                cancellationProperties.get("processCategories"));
        Assertions.assertEquals(dmnResultList.getFirst().get("action"),
                cancellationProperties.get("action"));
    }
}
