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

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_CONFIGURATION_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.CASE_MANAGEMENT_CATEGORY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.CASE_NAME;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.CASE_TYPE_VALUE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DECEASED_FORENAMES_VALUE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DECEASED_SURNAME_VALUE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DECISION_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DESCRIPTION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_INTERVAL_DAYS;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_INTERVAL_DAYS_VALUE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_NON_WORKING_CALENDAR;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_NON_WORKING_CALENDAR_VALUE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_NON_WORKING_DAYS_OF_WEEK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_NON_WORKING_DAYS_OF_WEEK_VALUE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.EXAMINE_DIGITAL_CASE_ADMON;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.LOCATION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.LOCATION_NAME;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PRIORITY_DATE_ORIGIN_REF;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PRIORITY_DATE_ORIGIN_REF_VALUE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REGION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REGISTRY_LOCATION_VALUE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_CTSC;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaUtils.resultsMatchUsingNameKey;

class CamundaTaskWaConfigurationTest extends DmnDecisionTableBaseUnitTest {

    private static final String REQUEST = "classpath:custom-case-data.json";
    private static final String taskId = UUID.randomUUID().toString();
    private static final String roleAssignmentId = UUID.randomUUID().toString();
    private static final String DESCRIPTION_DEFAULT_VALUE = "[Amend Case Details](/cases/case-details/"
            + "${[CASE_REFERENCE]}/trigger/boAmendCaseDetails)  "
            + "[Issue Grant](/cases/case-details/"
            + "${[CASE_REFERENCE]}/trigger/boIssueGrantForCaseMatching)  "
            + "[Escalate to Registrar](/cases/case-details/"
            + "${[CASE_REFERENCE]}/trigger/boEscalateToRegistrar)  "
            + "[Select For QA](/cases/case-details/"
            + "${[CASE_REFERENCE]}/trigger/boSelectForQA)  "
            + "[SME Referral](/cases/case-details/"
            + "${[CASE_REFERENCE]}/trigger/boUploadDocsForSMEReferral)  "
            + "[Stop Case](/cases/case-details/"
            + "${[CASE_REFERENCE]}/trigger/boStopCaseForCasePrinted)";

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_PROBATE;
    }

    static Stream<Arguments> scenarioProvider() throws IOException {
        String dateOrigin = ZonedDateTime.now(ZoneId.of("UTC")).toString();
        return Stream.of(
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_PROBATE,
                        CaseDataBuilder.defaultWaCase()
                                .isUrgent()
                                .build(),
                        "handleEvidence",
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(DESCRIPTION, DESCRIPTION_DEFAULT_VALUE, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, CASE_TYPE_VALUE, true)
                                .expectedValue(CASE_NAME, DECEASED_FORENAMES_VALUE + " "
                                        + DECEASED_SURNAME_VALUE, true)
                                .expectedValue(REGION, "DUMMY_PLACEHOLDER_REGION", true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_CTSC, true)
                                .expectedValue(LOCATION, REGISTRY_LOCATION_VALUE, true)
                                .expectedValue(LOCATION_NAME, REGISTRY_LOCATION_VALUE, true)
                                .expectedValue(DUE_DATE_NON_WORKING_CALENDAR, DUE_DATE_NON_WORKING_CALENDAR_VALUE, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, DUE_DATE_INTERVAL_DAYS_VALUE, true)
                                .expectedValue(DUE_DATE_NON_WORKING_DAYS_OF_WEEK,
                                        DUE_DATE_NON_WORKING_DAYS_OF_WEEK_VALUE, true)
                                .expectedValue(PRIORITY_DATE_ORIGIN_REF, PRIORITY_DATE_ORIGIN_REF_VALUE, true)
                                .build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_ADMON,
                        CaseDataBuilder.defaultCase()
                                .isUrgent()
                                .build(),
                        "handleEvidence",
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(DESCRIPTION, "[Select For QA](/cases/case-details/${[CASE_REFERENCE]}"
                                         + "/trigger/boSelectForQA)", true)
                                .build()
                ),
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_INTESTACY,
                        CaseDataBuilder.defaultCase()
                                .isUrgent()
                                .build(),
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
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(2));
        assertThat(logic.getOutputs().size(), is(3));
        assertEquals(15, logic.getRules().size());
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
                "taskId", taskId
        );
        inputVariables.putValue("taskAttributes", taskAttributes);
        inputVariables.putValue("taskType", taskType);
        inputVariables.putValue("caseData", caseData);
        inputVariables.putValue("eventId", eventId);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        resultsMatchUsingNameKey(dmnDecisionTableResult.getResultList(), expectation);
    }

    private boolean validNow(ZonedDateTime expected, ZonedDateTime result) {
        ZonedDateTime now = ZonedDateTime.now();
        return result != null
                && (expected.isEqual(result) || expected.isBefore(result))
                && (now.isEqual(result) || now.isAfter(result));
    }

}
