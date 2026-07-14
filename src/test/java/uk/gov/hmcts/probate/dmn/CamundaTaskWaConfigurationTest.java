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
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DESCRIPTION;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaVerifier.resultsMatchUsingNameKey;

class CamundaTaskWaConfigurationTest extends DmnDecisionTableBaseUnitTest {

    private static final String taskId = UUID.randomUUID().toString();
    private static final String roleAssignmentId = UUID.randomUUID().toString();

    /** CCD state ID for "Awaiting documentation" */
    private static final String STATE_CASE_PRINTED = "CasePrinted";
    /** CCD state ID for "Ready to issue" */
    private static final String STATE_READY_TO_ISSUE = "BOReadyToIssue";

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_PROBATE;
    }

    static Stream<Arguments> scenarioProvider() {
        return Stream.of(
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_PROBATE,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        STATE_CASE_PRINTED,
                        "handleEvidence",
                        ConfigurationExpectationBuilder.ExamineDigitalCaseExpectationsForState(STATE_CASE_PRINTED).build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_PROBATE,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        STATE_CASE_PRINTED,
                        "boAmendCaseDetailsForAwaitingDocumentation",
                        ConfigurationExpectationBuilder.ExamineDigitalCaseExpectationsForState(STATE_CASE_PRINTED).build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_PROBATE,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        STATE_READY_TO_ISSUE,
                        "handleEvidence",
                        ConfigurationExpectationBuilder.ExamineDigitalCaseExpectationsForState(STATE_READY_TO_ISSUE).build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_ADMON,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        STATE_CASE_PRINTED,
                        "handleEvidence",
                        ConfigurationExpectationBuilder.ExamineDigitalCaseExpectationsForState(STATE_CASE_PRINTED).build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_ADMON,
                        CaseDataBuilder.defaultWaCase().isUrgent().build(),
                        STATE_READY_TO_ISSUE,
                        "handleEvidence",
                        ConfigurationExpectationBuilder.ExamineDigitalCaseExpectationsForState(STATE_READY_TO_ISSUE).build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_INTESTACY,
                        CaseDataBuilder.defaultCase().isUrgent().build(),
                        null,
                        "handleEvidence",
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(DESCRIPTION, "[Select For QA](/cases/case-details/${[CASE_REFERENCE]}"
                                        + "/trigger/boSelectForQA)", true)
                                .build()
                )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(3));
        assertThat(logic.getOutputs().size(), is(3));
        assertEquals(31, logic.getRules().size());
    }

    @ParameterizedTest(name = "task type: {0} state: {2}")
    @MethodSource("scenarioProvider")
    void should_return_correct_configuration_values_for_scenario(
            String taskType,
            Map<String, Object> caseData,
            String state,
            String eventId,
            List<Map<String, Object>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();

        Map<String, Object> taskAttributes = Map.of(
                "taskType", taskType,
                "roleAssignmentId", roleAssignmentId,
                "taskId", taskId
        );
        inputVariables.putValue("taskAttributes", taskAttributes);
        inputVariables.putValue("taskType", taskType);
        inputVariables.putValue("state", state);
        inputVariables.putValue("caseData", caseData);
        inputVariables.putValue("eventId", eventId);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        resultsMatchUsingNameKey(dmnDecisionTableResult.getResultList(), expectation);
    }

}
