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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_COMPLETION_ST_CIC_CRIMINALINJURIESCOMPENSATION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.AUTO_COMPLETE_MODE;
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

class CamundaTaskCompletionTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_COMPLETION_ST_CIC_CRIMINALINJURIESCOMPENSATION;
    }

    static Stream<Arguments> scenarioProvider() {

        Stream<Arguments> of = Stream.of(
                Arguments.of(
                        "caseworker-send-order",
                        List.of(
                                Map.of(
                                        "taskType", PROCESS_CASE_WITHDRAWAL_DIR_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_CASE_WITHDRAWAL_DIR_LISTED_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_RULE27_DECISION_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_RULE27_DECISION_LISTED_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_LISTING_DIR_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_LISTING_DIR_LISTED_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_DIR_RELISTED_CASE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_DIR_RELISTED_CASE_WITHIN_5DAYS_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_SET_ASIDE_DIR_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_CORRECTIONS_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_DIR_RETURNED_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_POSTPONEMENT_DIR_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_TIME_EXT_DIR_RETURNED_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_REINSTATEMENT_DECISION_NOTICE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_OTHER_DIR_RETURNED_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_WRITTEN_REASONS_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_STRIKE_OUT_DIR_RETURNED_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_STAY_DIR_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_STAY_DIR_LISTED_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", ISSUE_DUE_DATE,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "caseworker-issue-final-decision",
                        List.of(
                                Map.of(
                                        "taskType", ISSUE_DECISION_NOTICE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "caseworker-issue-decision",
                        List.of(
                                Map.of(
                                        "taskType", ISSUE_DECISION_NOTICE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "create-hearing-summary",
                        List.of(
                                Map.of(
                                        "taskType", COMPLETE_HEARING_OUTCOME_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        List.of(
                                Map.of(
                                        "taskType", FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_FURTHER_EVIDENCE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        List.of(
                                Map.of(
                                        "taskType", FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_FURTHER_EVIDENCE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "caseworker-issue-case",
                        List.of(
                                Map.of(
                                        "taskType", ISSUE_CASE_TO_RESPONDENT_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                )
                        )
                ),
                Arguments.of(
                        "caseworker-case-built",
                        List.of(
                                Map.of(
                                        "taskType", VET_NEW_CASE_DOCUMENTS_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        List.of(
                                Map.of(
                                        "taskType", REVIEW_NEW_CASE_PROVIDE_DIR_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_TIME_EXT_REQ_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_STRIKE_OUT_REQ_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_STAY_REQ_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_STAY_REQ_CASE_LISTED_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_LISTING_DIR_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_LISTING_DIR_CASE_LISTED_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_WITHDRAWAL_REQ_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_WITHDRAWAL_REQ_CASE_LISTED_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_RULE27_REQ_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_RULE27_REQ_CASE_LISTED_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_LIST_CASE_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_OTHER_REQ_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_LIST_CASE_WITHIN_5DAYS_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_POSTPONEMENT_REQ_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_REINSTATEMENT_REQ_LO_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_LIST_CASE_WITHIN_5DAYS_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_POSTPONEMENT_REQ_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_CORRECTIONS_REQ_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_WRITTEN_REASONS_REQ_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_REINSTATEMENT_REQ_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_SET_ASIDE_REQ_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_STAY_REQ_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_STAY_REQ_CASE_LISTED_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_NEW_CASE_PROVIDE_DIR_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_OTHER_REQ_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_WITHDRAWAL_REQ_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_WITHDRAWAL_REQ_CASE_LISTED_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_RULE27_REQ_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_RULE27_REQ_CASE_LISTED_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_LISTING_DIR_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_LISTING_DIR_CASE_LISTED_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_LIST_CASE_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_STRIKE_OUT_REQ_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", REVIEW_TIME_EXT_REQ_JUDGE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", CREATE_DUE_DATE,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "edit-case",
                        List.of(
                                Map.of(
                                        "taskType", REGISTER_NEW_CASE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_FURTHER_EVIDENCE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "caseworker-amend-document",
                        List.of(
                                Map.of(
                                        "taskType", PROCESS_FURTHER_EVIDENCE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "contact-parties",
                        List.of(
                                Map.of(
                                        "taskType", FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Map.of(
                                        "taskType", PROCESS_FURTHER_EVIDENCE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "createBundle",
                        List.of(
                                Map.of(
                                        "taskType", STITCH_COLLATE_HEARING_BUNDLE_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "caseworker-document-management",
                        List.of(
                                Map.of(
                                        "taskType", FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                ),
                Arguments.of(
                        "caseworker-amend-due-date",
                        List.of(
                                Map.of(
                                        "taskType", FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK,
                                        "completionMode", AUTO_COMPLETE_MODE
                                ),
                                Collections.emptyMap()
                        )
                )
        );
        return of;
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(1));
        assertThat(logic.getOutputs().size(), is(2));
        assertThat(logic.getRules().size(), is(65));
    }

    @ParameterizedTest(name = "event id: {0}")
    @MethodSource("scenarioProvider")
    void given_event_ids_should_evaluate_dmn(String eventId, List<Map<String, String>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

}
