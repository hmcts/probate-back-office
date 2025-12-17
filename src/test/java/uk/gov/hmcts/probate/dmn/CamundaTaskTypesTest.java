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
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_TYPES_ST_CIC_CRIMINALINJURIESCOMPENSATION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.COMPLETE_HEARING_OUTCOME_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.CREATE_DUE_DATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ISSUE_CASE_TO_RESPONDENT_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ISSUE_DECISION_NOTICE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ISSUE_DUE_DATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CASE_WITHDRAWAL_DIR_LISTED_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CASE_WITHDRAWAL_DIR_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CORRECTIONS_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_DIR_RELISTED_CASE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_DIR_RELISTED_CASE_WITHIN_5DAYS_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_DIR_RETURNED_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_FURTHER_EVIDENCE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_LISTING_DIR_LISTED_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_LISTING_DIR_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_OTHER_DIR_RETURNED_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_POSTPONEMENT_DIR_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_REINSTATEMENT_DECISION_NOTICE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_RULE27_DECISION_LISTED_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_RULE27_DECISION_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_SET_ASIDE_DIR_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_STAY_DIR_LISTED_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_STAY_DIR_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_STRIKE_OUT_DIR_RETURNED_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_TIME_EXT_DIR_RETURNED_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_WRITTEN_REASONS_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REGISTER_NEW_CASE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_CORRECTIONS_REQ_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_LISTING_DIR_CASE_LISTED_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_LISTING_DIR_CASE_LISTED_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_LISTING_DIR_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_LISTING_DIR_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_LIST_CASE_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_LIST_CASE_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_LIST_CASE_WITHIN_5DAYS_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_LIST_CASE_WITHIN_5DAYS_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_NEW_CASE_PROVIDE_DIR_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_NEW_CASE_PROVIDE_DIR_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_OTHER_REQ_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_OTHER_REQ_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_POSTPONEMENT_REQ_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_POSTPONEMENT_REQ_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_REINSTATEMENT_REQ_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_REINSTATEMENT_REQ_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_RULE27_REQ_CASE_LISTED_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_RULE27_REQ_CASE_LISTED_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_RULE27_REQ_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_RULE27_REQ_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_SET_ASIDE_REQ_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_SPECIFIC_ACCESS_REQ_ADMIN_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_SPECIFIC_ACCESS_REQ_CTSC_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_SPECIFIC_ACCESS_REQ_JUDICIARY_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_SPECIFIC_ACCESS_REQ_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_STAY_REQ_CASE_LISTED_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_STAY_REQ_CASE_LISTED_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_STAY_REQ_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_STAY_REQ_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_STRIKE_OUT_REQ_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_STRIKE_OUT_REQ_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_TIME_EXT_REQ_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_TIME_EXT_REQ_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_WITHDRAWAL_REQ_CASE_LISTED_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_WITHDRAWAL_REQ_CASE_LISTED_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_WITHDRAWAL_REQ_JUDGE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_WITHDRAWAL_REQ_LO_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REVIEW_WRITTEN_REASONS_REQ_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.STITCH_COLLATE_HEARING_BUNDLE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.VET_NEW_CASE_DOCUMENTS_TASK;

class CamundaTaskTypesTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_TYPES_ST_CIC_CRIMINALINJURIESCOMPENSATION;
    }

    static Stream<Arguments> scenarioProvider() {
        List<Map<String, String>> taskTypes = List.of(
                Map.of(
                        "taskTypeId", PROCESS_CASE_WITHDRAWAL_DIR_TASK,
                        "taskTypeName", "Process case withdrawal directions"
                ),
                Map.of(
                        "taskTypeId", PROCESS_CASE_WITHDRAWAL_DIR_LISTED_TASK,
                        "taskTypeName", "Process case withdrawal directions listed"
                ),
                Map.of(
                        "taskTypeId", PROCESS_RULE27_DECISION_TASK,
                        "taskTypeName", "Process Rule 27 decision"
                ),
                Map.of(
                        "taskTypeId", PROCESS_RULE27_DECISION_LISTED_TASK,
                        "taskTypeName", "Process Rule 27 decision listed"
                ),
                Map.of(
                        "taskTypeId", PROCESS_LISTING_DIR_TASK,
                        "taskTypeName", "Process listing directions"
                ),
                Map.of(
                        "taskTypeId", PROCESS_LISTING_DIR_LISTED_TASK,
                        "taskTypeName", "Process listing directions listed"
                ),
                Map.of(
                        "taskTypeId", PROCESS_DIR_RELISTED_CASE_TASK,
                        "taskTypeName", "Process directions re. listed case"
                ),
                Map.of(
                        "taskTypeId", PROCESS_DIR_RELISTED_CASE_WITHIN_5DAYS_TASK,
                        "taskTypeName", "Process directions re. listed case (within 5 days)"
                ),
                Map.of(
                        "taskTypeId", PROCESS_SET_ASIDE_DIR_TASK,
                        "taskTypeName", "Process set aside directions"
                ),
                Map.of(
                        "taskTypeId", PROCESS_CORRECTIONS_TASK,
                        "taskTypeName", "Process corrections"
                ),
                Map.of(
                        "taskTypeId", PROCESS_DIR_RETURNED_TASK,
                        "taskTypeName", "Process directions returned"
                ),
                Map.of(
                        "taskTypeId", PROCESS_POSTPONEMENT_DIR_TASK,
                        "taskTypeName", "Process postponement directions"
                ),
                Map.of(
                        "taskTypeId", PROCESS_TIME_EXT_DIR_RETURNED_TASK,
                        "taskTypeName", "Process time extension directions returned"
                ),
                Map.of(
                        "taskTypeId", PROCESS_REINSTATEMENT_DECISION_NOTICE_TASK,
                        "taskTypeName", "Process reinstatement decision notice"
                ),
                Map.of(
                        "taskTypeId", PROCESS_OTHER_DIR_RETURNED_TASK,
                        "taskTypeName", "Process other directions returned"
                ),
                Map.of(
                        "taskTypeId", PROCESS_WRITTEN_REASONS_TASK,
                        "taskTypeName", "Process written reasons"
                ),
                Map.of(
                        "taskTypeId", PROCESS_STRIKE_OUT_DIR_RETURNED_TASK,
                        "taskTypeName", "Process strike out directions returned"
                ),
                Map.of(
                        "taskTypeId", PROCESS_STAY_DIR_TASK,
                        "taskTypeName", "Process stay directions"
                ),
                Map.of(
                        "taskTypeId", PROCESS_STAY_DIR_LISTED_TASK,
                        "taskTypeName", "Process stay directions listed"
                ),
                Map.of(
                        "taskTypeId", ISSUE_DECISION_NOTICE_TASK,
                        "taskTypeName", "Issue decision notice"
                ),
                Map.of(
                        "taskTypeId", COMPLETE_HEARING_OUTCOME_TASK,
                        "taskTypeName", "Complete hearing outcome"
                ),
                Map.of(
                        "taskTypeId", ISSUE_CASE_TO_RESPONDENT_TASK,
                        "taskTypeName", "Issue case to respondent"
                ),
                Map.of(
                        "taskTypeId", VET_NEW_CASE_DOCUMENTS_TASK,
                        "taskTypeName", "Vet new case documents"
                ),
                Map.of(
                        "taskTypeId", REVIEW_NEW_CASE_PROVIDE_DIR_LO_TASK,
                        "taskTypeName", "Review new case and provide directions - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_TIME_EXT_REQ_LO_TASK,
                        "taskTypeName", "Review time extension request - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_STRIKE_OUT_REQ_LO_TASK,
                        "taskTypeName", "Review strike out request - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_STAY_REQ_LO_TASK,
                        "taskTypeName", "Review stay request - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_STAY_REQ_CASE_LISTED_LO_TASK,
                        "taskTypeName", "Review stay request case listed - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_LISTING_DIR_LO_TASK,
                        "taskTypeName", "Review listing directions - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_LISTING_DIR_CASE_LISTED_LO_TASK,
                        "taskTypeName", "Review listing directions case listed - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_WITHDRAWAL_REQ_LO_TASK,
                        "taskTypeName", "Review withdrawal request - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_WITHDRAWAL_REQ_CASE_LISTED_LO_TASK,
                        "taskTypeName", "Review withdrawal request case listed - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_RULE27_REQ_LO_TASK,
                        "taskTypeName", "Review Rule 27 request - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_RULE27_REQ_CASE_LISTED_LO_TASK,
                        "taskTypeName", "Review Rule 27 request case listed - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_LIST_CASE_LO_TASK,
                        "taskTypeName", "Review list case - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_OTHER_REQ_LO_TASK,
                        "taskTypeName", "Review reinstatement request - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_LIST_CASE_WITHIN_5DAYS_LO_TASK,
                        "taskTypeName", "Review list case (within 5 days) - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_POSTPONEMENT_REQ_LO_TASK,
                        "taskTypeName", "Review postponement request - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_REINSTATEMENT_REQ_LO_TASK,
                        "taskTypeName", "Review reinstatement request - Legal Officer"
                ),
                Map.of(
                        "taskTypeId", REVIEW_LIST_CASE_WITHIN_5DAYS_JUDGE_TASK,
                        "taskTypeName", "Review list case (within 5 days) - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_POSTPONEMENT_REQ_JUDGE_TASK,
                        "taskTypeName", "Review postponement request - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_CORRECTIONS_REQ_TASK,
                        "taskTypeName", "Review corrections request"
                ),
                Map.of(
                        "taskTypeId", REVIEW_WRITTEN_REASONS_REQ_TASK,
                        "taskTypeName", "Review written reasons request"
                ),
                Map.of(
                        "taskTypeId", REVIEW_REINSTATEMENT_REQ_JUDGE_TASK,
                        "taskTypeName", "Review reinstatement request - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_SET_ASIDE_REQ_TASK,
                        "taskTypeName", "Review set aside request"
                ),
                Map.of(
                        "taskTypeId", REVIEW_STAY_REQ_JUDGE_TASK,
                        "taskTypeName", "Review stay request - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_STAY_REQ_CASE_LISTED_JUDGE_TASK,
                        "taskTypeName", "Review stay request case listed - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_NEW_CASE_PROVIDE_DIR_JUDGE_TASK,
                        "taskTypeName", "Review new case and provide directions - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_OTHER_REQ_JUDGE_TASK,
                        "taskTypeName", "Review other request - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_WITHDRAWAL_REQ_JUDGE_TASK,
                        "taskTypeName", "Review withdrawal request - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_WITHDRAWAL_REQ_CASE_LISTED_JUDGE_TASK,
                        "taskTypeName", "Review withdrawal request case listed - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_RULE27_REQ_JUDGE_TASK,
                        "taskTypeName", "Review Rule 27 request - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_RULE27_REQ_CASE_LISTED_JUDGE_TASK,
                        "taskTypeName", "Review Rule 27 request case listed - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_LISTING_DIR_JUDGE_TASK,
                        "taskTypeName", "Review listing directions - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_LISTING_DIR_CASE_LISTED_JUDGE_TASK,
                        "taskTypeName", "Review listing directions case listed - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_LIST_CASE_JUDGE_TASK,
                        "taskTypeName", "Review list case - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_STRIKE_OUT_REQ_JUDGE_TASK,
                        "taskTypeName", "Review strike out request - Judge"
                ),
                Map.of(
                        "taskTypeId", REVIEW_TIME_EXT_REQ_JUDGE_TASK,
                        "taskTypeName", "Review time extension request - Judge"
                ),
                Map.of(
                        "taskTypeId", FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK,
                        "taskTypeName", "Follow up noncompliance of directions"
                ),
                Map.of(
                        "taskTypeId", REGISTER_NEW_CASE_TASK,
                        "taskTypeName", "Register new case"
                ),
                Map.of(
                        "taskTypeId", PROCESS_FURTHER_EVIDENCE_TASK,
                        "taskTypeName", "Process further evidence"
                ),
                Map.of(
                        "taskTypeId", STITCH_COLLATE_HEARING_BUNDLE_TASK,
                        "taskTypeName", "Stitch/collate hearing bundle"
                ),
                Map.of(
                        "taskTypeId", REVIEW_SPECIFIC_ACCESS_REQ_JUDICIARY_TASK,
                        "taskTypeName", "Review Specific Access Request Judiciary"
                ),
                Map.of(
                        "taskTypeId", REVIEW_SPECIFIC_ACCESS_REQ_LO_TASK,
                        "taskTypeName", "Review Specific Access Request Legal Ops"
                ),
                Map.of(
                        "taskTypeId", REVIEW_SPECIFIC_ACCESS_REQ_ADMIN_TASK,
                        "taskTypeName", "Review Specific Access Request Admin"
                ),
                Map.of(
                        "taskTypeId", REVIEW_SPECIFIC_ACCESS_REQ_CTSC_TASK,
                        "taskTypeName", "Review Specific Access Request CTSC"
                ),
                Map.of(
                        "taskTypeId", CREATE_DUE_DATE,
                        "taskTypeName", "Create due date"
                ),
                Map.of(
                        "taskTypeId", ISSUE_DUE_DATE,
                        "taskTypeName", "Issue due date"
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
        assertThat(logic.getRules().size(), is(68));
    }

    @ParameterizedTest(name = "retrieve all task type data")
    @MethodSource("scenarioProvider")
    void should_evaluate_dmn_return_all_task_type_fields(List<Map<String, Object>> expectedTaskTypes) {

        VariableMap inputVariables = new VariableMapImpl();
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(expectedTaskTypes));
    }

}
