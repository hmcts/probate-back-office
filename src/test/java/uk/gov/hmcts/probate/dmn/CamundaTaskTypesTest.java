package uk.gov.hmcts.probate.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.hamcrest.MatcherAssert;
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
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_TYPES_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DE_BONIS_NON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_FIAT_WILL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.PROBATE_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ADMON_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.AD_COLLIGENDA_BONA_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.INTESTACY_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DE_BONIS_NON_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.FIAT_WILL_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.INFECTED_BLOOD_COMPENSATION_AUTHORITY_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DOUBLE_PROBATE_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DOUBLE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INCAPACITY_UNDER_RULE_35_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INCAPACITY_UNDER_RULE_35;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LEADING_OR_FOLLOWING_GRANTS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LEADING_OR_FOLLOWING_GRANTS_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_RECTIFY_WILL_OR_CODICIL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_CODICIL_MIS_RECITAL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME_CASE_PRINTED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_CODICIL_MIS_RECITAL_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_RECTIFY_WILL_OR_CODICIL_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WINDRUSH_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_HORIZON_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WITNESS_INTERVIEW;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_POWER_OF_ATTORNEY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_RESEAL_FOREIGN_GRANT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_SECTION_116;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WINDRUSH_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LITERARY_ESTATE_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LITERARY_ESTATE_CASE_PRINTED;

class CamundaTaskTypesTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_TYPES_PROBATE;
    }

    static Stream<Arguments> scenarioProvider() {
        List<Map<String, String>> taskTypes = List.of(
                Map.of(
                        "taskTypeName", PROBATE_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_DIGITAL_CASE_PROBATE
                ),
                Map.of(
                        "taskTypeName", ADMON_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_DIGITAL_CASE_ADMON
                ),
                Map.of(
                        "taskTypeName", INTESTACY_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_DIGITAL_CASE_INTESTACY
                ),
                Map.of(
                        "taskTypeName", ADMON_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE
                ),
                Map.of(
                        "taskTypeName", PROBATE_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE
                ),
                Map.of(
                        "taskTypeName", DE_BONIS_NON_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_DE_BONIS_NON
                ),
                Map.of(
                    "taskTypeName", FIAT_WILL_TASK_TYPE_NAME,
                    "taskTypeId", EXAMINE_FIAT_WILL
                ),
                Map.of(
                    "taskTypeName", INFECTED_BLOOD_COMPENSATION_AUTHORITY_TASK_TYPE_NAME,
                    "taskTypeId", EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY
                ),
                Map.of(
                    "taskTypeName", "Examine - Windrush Scheme",
                    "taskTypeId", EXAMINE_WINDRUSH_SCHEME
                ),
                Map.of(
                    "taskTypeName", "Examine - Will or Codicil to be Notated",
                    "taskTypeId", EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED
                ),
                Map.of(
                    "taskTypeName", "Examine - Witness Interview",
                    "taskTypeId", EXAMINE_WITNESS_INTERVIEW
                ),
                Map.of(
                    "taskTypeName", "Examine - Horizon Scheme",
                    "taskTypeId", EXAMINE_HORIZON_SCHEME
                ),
                Map.of(
                        "taskTypeName", "Examine Digital Case - Ad Colligenda Bona",
                        "taskTypeId", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA
                ),
                Map.of(
                    "taskTypeName", "Examine Digital Case - Intestacy",
                    "taskTypeId", EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE
                ),
                Map.of(
                    "taskTypeName", AD_COLLIGENDA_BONA_TASK_TYPE_NAME,
                    "taskTypeId", EXAMINE_DIGITAL_CASE_AD_COLLIGENDA_BONA_READY_TO_ISSUE
                ),
                Map.of(
                        "taskTypeName", EXAMINE_DOUBLE_PROBATE_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_DOUBLE_PROBATE
                ),
                Map.of(
                        "taskTypeName", EXAMINE_INCAPACITY_UNDER_RULE_35_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_INCAPACITY_UNDER_RULE_35
                ),
                Map.of(
                        "taskTypeName", EXAMINE_LEADING_OR_FOLLOWING_GRANTS_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_LEADING_OR_FOLLOWING_GRANTS
                ),
                Map.of(
                    "taskTypeName", "Examine - Section 116",
                    "taskTypeId", EXAMINE_SECTION_116
                ),
                Map.of(
                    "taskTypeName", "Examine - Power of Attorney (POA)",
                    "taskTypeId", EXAMINE_POWER_OF_ATTORNEY
                ),
                Map.of(
                    "taskTypeName", "Examine - Reseal Foreign Grant",
                    "taskTypeId", EXAMINE_RESEAL_FOREIGN_GRANT
                ),
                Map.of(
                    "taskTypeName", "Examine - Infected Blood Interim Scheme",
                    "taskTypeId", EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME
                ),
                Map.of(
                        "taskTypeName", EXAMINE_RECTIFY_WILL_OR_CODICIL_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_RECTIFY_WILL_OR_CODICIL
                ),
                Map.of(
                        "taskTypeName", EXAMINE_CODICIL_MIS_RECITAL_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_CODICIL_MIS_RECITAL
                ),
                Map.of(
                        "taskTypeName", EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_INFECTED_BLOOD_INTERIM_SCHEME_CASE_PRINTED
                ),
                Map.of(
                        "taskTypeName", EXAMINE_LITERARY_ESTATE_TASK_TYPE_NAME,
                        "taskTypeId", EXAMINE_LITERARY_ESTATE_CASE_PRINTED
                )
        );
        return Stream.of(
                Arguments.of(
                        taskTypes
                )
        );
    }

    @Test
    void check_dmn_changed() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(1));
        assertThat(logic.getOutputs().size(), is(2));
        assertThat(logic.getRules().size(), is(26));
    }

    @ParameterizedTest(name = "retrieve all task type data")
    @MethodSource("scenarioProvider")
    void should_evaluate_dmn_return_all_task_type_fields(List<Map<String, Object>> expectedTaskTypes) {

        VariableMap inputVariables = new VariableMapImpl();
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(expectedTaskTypes));
    }

}
