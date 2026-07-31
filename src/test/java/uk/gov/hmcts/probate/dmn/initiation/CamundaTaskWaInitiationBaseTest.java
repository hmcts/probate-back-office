package uk.gov.hmcts.probate.dmn.initiation;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.gov.hmcts.probate.DmnDecisionTableBaseUnitTest;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_INITIATION_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaVerifier.resultsMatchUsingNameKey;

public class CamundaTaskWaInitiationBaseTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_INITIATION_PROBATE;
    }

    protected static final String defaultHandOffReasonId = "df3be732-2172-49da-80fe-cad8586e4928";
    protected static final String caseTypeVar = "caseType";
    protected static final String evidenceHandledVar = "evidenceHandled";
    protected static final String caseHandedOffToLegacySiteVar = "caseHandedOffToLegacySite";
    protected static final String boHandoffReasonListVar = "boHandoffReasonList";
    protected static final String deBonisNonHandOffReason = "DeBonisNon";
    protected static final String infectedBloodCompensationAuthorityHandOffReason = "IBCA";
    protected static final String doubleProbateHandOffReason = "DoubleProbate";
    protected static final String fiatWillHandOffReason = "FiatWill";
    protected static final String invalidHandOffReason = "OtherReason";
    protected static final String incapacityUnderRule35HandOffReason = "Incapacity under rule 35";
    protected static final String leadingFollowingGrantsHandOffReason = "Leading / following Grants";

    protected static Map<String, Map<String, Object>> additionalData(boolean evidenceHandled,
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

    protected static Map<String, Map<String, Object>> additionalDataNoHandOffList() {
        return Map.of(
                "Data", Map.of(
                        evidenceHandledVar, false,
                        caseTypeVar, "",
                        caseHandedOffToLegacySiteVar, true
                )
        );
    }

    protected static List<Map<String,Object>> handOffReasonListWithHandOffReason(String handOffReason) {
        return List.of(
                Map.of(
                        "id", defaultHandOffReasonId,
                        "value", Map.of("caseHandoffReason", handOffReason)
                )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(7));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(13));
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} evidenceHandled: {2} caseType: {3}")
    @ArgumentsSource(CamundaTaskWaInitiationProbateTestProvider.class)
    @ArgumentsSource(CamundaTaskWaInitiationAdmonTestProvider.class)
    @ArgumentsSource(CamundaTaskWaInitiationDeBonisNonTestProvider.class)
    @ArgumentsSource(CamundaTaskWaInitiationIntestacyTestProvider.class)
    @ArgumentsSource(CamundaTaskWaInitiationAdCollingendaTestProvider.class)
    @ArgumentsSource(CamundaTaskWaInitiationDoubleProbateTestProvider.class)
    @ArgumentsSource(CamundaTaskWaInitiationFiatWillTestProvider.class)
    @ArgumentsSource(CamundaTaskWaInitiationIncapacityUnderRule35TestProvider.class)
    @ArgumentsSource(CamundaTaskWaInitiationInfectedBloodTestProvider.class)
    @ArgumentsSource(CamundaTaskWaInitiationLeadingOrFollowingGrantTestProvider.class)
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
