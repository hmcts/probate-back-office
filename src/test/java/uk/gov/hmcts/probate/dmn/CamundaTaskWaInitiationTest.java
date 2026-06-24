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
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaVerifier.mapAdditionalData;
import static uk.gov.hmcts.probate.dmnutils.CamundaVerifier.resultsMatchUsingNameKey;

class CamundaTaskWaInitiationTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_INITIATION_PROBATE;
    }

    static Stream<Arguments> scenarioProvider() {
        Map<String,Object> examineDigitalCaseProbateTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_PROBATE,
                "name", "Examine Digital Case - Probate",
                "processCategories", "case progression"
        );

        Map<String, Object> additionalData = mapAdditionalData("{\n"
                + "  \"Data\":{\n"
                + "  \"evidenceHandled\" : \"" + false + "\",\n"
                + "  \"caseType\" : \"" + "gop" + "\",\n"
                + "  \"boHandoffReasonList\" : []\n"
                + "  }\n"
                + "}");

        Map<String, Object> additionalDataEvidenceHandledTrue = mapAdditionalData("{\n"
                + "  \"Data\":{\n"
                + "  \"evidenceHandled\" : \"" + true + "\",\n"
                + "  \"caseType\" : \"" + "gop" + "\",\n"
                + "  \"boHandoffReasonList\" : []\n"
                + "  }\n"
                + "}");

        Map<String, Object> additionalDataCaseTypeOther = mapAdditionalData("{\n"
                + "  \"Data\":{\n"
                + "  \"evidenceHandled\" : \"" + false + "\",\n"
                + "  \"caseType\" : \"" + "other" + "\",\n"
                + "  \"boHandoffReasonList\" : []\n"
                + "  }\n"
                + "}");

        Map<String, Object> additionalDataHandOffListNotEmpty = mapAdditionalData("{\n"
                + "  \"Data\":{\n"
                + "  \"evidenceHandled\" : \"" + false + "\",\n"
                + "  \"caseType\" : \"" + "gop" + "\",\n"
                + "  \"boHandoffReasonList\" : [1]\n"
                + "  }\n"
                + "}");

        return Stream.of(
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData,
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        "someOtherEventId",
                        "CasePrinted",
                        additionalData,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalDataEvidenceHandledTrue,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalDataCaseTypeOther,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalDataHandOffListNotEmpty,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplication",
                        "CasePrinted",
                        additionalData,
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData,
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData,
                        List.of(examineDigitalCaseProbateTaskAttributes)
                )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(5));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(6));
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} evidenceHandled: {2} caseType: {3}")
    @MethodSource("scenarioProvider")
    void given_multiple_event_ids_should_evaluate_dmn(String eventId,
                                                      String postEventState,
                                                      Map<String, Object> additionalData,
                                                      List<Map<String, Object>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        if (additionalData != null) {
            inputVariables.putAll(additionalData);
        }
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        resultsMatchUsingNameKey(dmnDecisionTableResult.getResultList(), expectation);
    }
}
