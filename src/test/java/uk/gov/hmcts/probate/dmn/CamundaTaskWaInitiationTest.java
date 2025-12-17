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
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_INITIATION_ST_CIC_CRIMINALINJURIESCOMPENSATION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.APPLICATION_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.COMPLETE_HEARING_OUTCOME_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.CREATE_DUE_DATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DECISION_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.HEARING_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ISSUE_CASE_TO_RESPONDENT_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ISSUE_DECISION_NOTICE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ISSUE_DUE_DATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PRIORITY_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CASE_WITHDRAWAL_DIR_LISTED_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CASE_WITHDRAWAL_DIR_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CATEGORY_AMENDMENT;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CATEGORY_APPLICATION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CATEGORY_DECISION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CATEGORY_HEARING;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CATEGORY_HEARING_BUNDLE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CATEGORY_HEARING_COMPLETION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CATEGORY_ISSUE_CASE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PROCESS_CATEGORY_PROCESSING;
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
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_ADMIN;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_JUDICIAL;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_LO;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROUTINE_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.STITCH_COLLATE_HEARING_BUNDLE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.VET_NEW_CASE_DOCUMENTS_TASK;

class CamundaTaskWaInitiationTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_INITIATION_ST_CIC_CRIMINALINJURIESCOMPENSATION;
    }

    static Stream<Arguments> scenarioProvider() {
        return Stream.of(
                Arguments.of(
                        "create-draft-order",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Withdrawal request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_CASE_WITHDRAWAL_DIR_TASK,
                                        "name", "Process case withdrawal directions",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Withdrawal request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_CASE_WITHDRAWAL_DIR_TASK,
                                        "name", "Process case withdrawal directions",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Withdrawal request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_CASE_WITHDRAWAL_DIR_LISTED_TASK,
                                        "name", "Process case withdrawal directions listed",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Rule 27 request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_RULE27_DECISION_TASK,
                                        "name", "Process Rule 27 decision",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Rule 27 request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_RULE27_DECISION_TASK,
                                        "name", "Process Rule 27 decision",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Rule 27 request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_RULE27_DECISION_LISTED_TASK,
                                        "name", "Process Rule 27 decision listed",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listing directions")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_LISTING_DIR_TASK,
                                        "name", "Process listing directions",
                                        "workingDaysAllowed", 3,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listing directions")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_LISTING_DIR_LISTED_TASK,
                                        "name", "Process listing directions listed",
                                        "workingDaysAllowed", 3,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listed case")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_DIR_RELISTED_CASE_TASK,
                                        "name", "Process directions re. listed case",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_HEARING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listed case (within 5 days)")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_DIR_RELISTED_CASE_WITHIN_5DAYS_TASK,
                                        "name", "Process directions re. listed case (within 5 days)",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_HEARING,
                                        "workType", PRIORITY_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "CaseClosed",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Set aside request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_SET_ASIDE_DIR_TASK,
                                        "name", "Process set aside directions",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_DECISION,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "CaseClosed",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Corrections")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_CORRECTIONS_TASK,
                                        "name", "Process corrections",
                                        "workingDaysAllowed", 3,
                                        "processCategories", PROCESS_CATEGORY_AMENDMENT,
                                        "workType", HEARING_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "New case")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_DIR_RETURNED_TASK,
                                        "name", "Process directions returned",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "New case")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_DIR_RETURNED_TASK,
                                        "name", "Process directions returned",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Postponement request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_POSTPONEMENT_DIR_TASK,
                                        "name", "Process postponement directions",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_HEARING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Time extension request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_TIME_EXT_DIR_RETURNED_TASK,
                                        "name", "Process time extension directions returned",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Time extension request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_TIME_EXT_DIR_RETURNED_TASK,
                                        "name", "Process time extension directions returned",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "CaseClosed",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Reinstatement request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_REINSTATEMENT_DECISION_NOTICE_TASK,
                                        "name", "Process reinstatement decision notice",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_APPLICATION,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "*",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Other")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_OTHER_DIR_RETURNED_TASK,
                                        "name", "Process other directions returned",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "CaseClosed",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Written reasons request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_WRITTEN_REASONS_TASK,
                                        "name", "Process written reasons",
                                        "workingDaysAllowed", 3,
                                        "processCategories", PROCESS_CATEGORY_DECISION,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Strike out request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_STRIKE_OUT_DIR_RETURNED_TASK,
                                        "name", "Process strike out directions returned",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Strike out request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_STRIKE_OUT_DIR_RETURNED_TASK,
                                        "name", "Process strike out directions returned",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Stay request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_STAY_DIR_TASK,
                                        "name", "Process stay directions",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Stay request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_STAY_DIR_TASK,
                                        "name", "Process stay directions",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "")),
                        List.of(
                                Map.of(
                                        "taskId", ISSUE_DUE_DATE,
                                        "name", "Issue due date",
                                        "workingDaysAllowed", 2,
                                        "processCategories", PROCESS_CATEGORY_ISSUE_CASE,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-draft-order",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Stay request")),
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_STAY_DIR_LISTED_TASK,
                                        "name", "Process stay directions listed",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "create-hearing-summary",
                        "AwaitingOutcome",
                        null,
                        List.of(
                                Map.of(
                                        "taskId", ISSUE_DECISION_NOTICE_TASK,
                                        "name", "Issue decision notice",
                                        "workingDaysAllowed", 1,
                                        "workType", HEARING_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "system-trigger-stitch-collate-hearing-bundle",
                        "AwaitingHearing",
                        null,
                        List.of(
                                Map.of(
                                        "taskId", STITCH_COLLATE_HEARING_BUNDLE_TASK,
                                        "name", "Stitch/collate hearing bundle",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_HEARING_BUNDLE,
                                        "workType", HEARING_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "system-trigger-complete-hearing-outcome",
                        "AwaitingHearing",
                        null,
                        List.of(
                                Map.of(
                                        "taskId", COMPLETE_HEARING_OUTCOME_TASK,
                                        "name", "Complete hearing outcome",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_HEARING_COMPLETION,
                                        "workType", HEARING_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "caseworker-case-built",
                        "CaseManagement",
                        null,
                        List.of(
                                Map.of(
                                        "taskId", ISSUE_CASE_TO_RESPONDENT_TASK,
                                        "name", "Issue case to respondent",
                                        "workingDaysAllowed", 2,
                                        "processCategories", PROCESS_CATEGORY_ISSUE_CASE,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "edit-case",
                        "Submitted",
                        null,
                        List.of(
                                Map.of(
                                        "taskId", VET_NEW_CASE_DOCUMENTS_TASK,
                                        "name", "Vet new case documents",
                                        "workingDaysAllowed", 5,
                                        "workType", APPLICATION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "citizen-cic-submit-dss-application",
                        "DSS_Submitted",
                        null,
                        List.of(
                                Map.of(
                                        "taskId", REGISTER_NEW_CASE_TASK,
                                        "name", "Register new case",
                                        "workingDaysAllowed", 5,
                                        "workType", APPLICATION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "New case")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_NEW_CASE_PROVIDE_DIR_LO_TASK,
                                        "name", "Review new case and provide directions - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "New case")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_NEW_CASE_PROVIDE_DIR_LO_TASK,
                                        "name", "Review new case and provide directions - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Time extension request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_TIME_EXT_REQ_LO_TASK,
                                        "name", "Review time extension request - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Time extension request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_TIME_EXT_REQ_LO_TASK,
                                        "name", "Review time extension request - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Strike out request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_STRIKE_OUT_REQ_LO_TASK,
                                        "name", "Review strike out request - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Strike out request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_STRIKE_OUT_REQ_LO_TASK,
                                        "name", "Review strike out request - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Stay request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_STAY_REQ_LO_TASK,
                                        "name", "Review stay request - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Stay request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_STAY_REQ_LO_TASK,
                                        "name", "Review stay request - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Stay request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_STAY_REQ_CASE_LISTED_LO_TASK,
                                        "name", "Review stay request case listed - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listing directions")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_LISTING_DIR_LO_TASK,
                                        "name", "Review listing directions - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listing directions")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_LISTING_DIR_CASE_LISTED_LO_TASK,
                                        "name", "Review listing directions case listed - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Withdrawal request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_WITHDRAWAL_REQ_LO_TASK,
                                        "name", "Review withdrawal request - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Withdrawal request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_WITHDRAWAL_REQ_LO_TASK,
                                        "name", "Review withdrawal request - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Withdrawal request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_WITHDRAWAL_REQ_CASE_LISTED_LO_TASK,
                                        "name", "Review withdrawal request case listed - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Rule 27 request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_RULE27_REQ_LO_TASK,
                                        "name", "Review Rule 27 request - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Rule 27 request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_RULE27_REQ_LO_TASK,
                                        "name", "Review Rule 27 request - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Rule 27 request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_RULE27_REQ_CASE_LISTED_LO_TASK,
                                        "name", "Review Rule 27 request case listed - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listed case")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_LIST_CASE_LO_TASK,
                                        "name", "Review list case - Legal Officer",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_HEARING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listed case (within 5 days)")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_LIST_CASE_WITHIN_5DAYS_LO_TASK,
                                        "name", "Review list case (within 5 days) - Legal Officer",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_HEARING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Postponement request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_POSTPONEMENT_REQ_LO_TASK,
                                        "name", "Review postponement request - Legal Officer",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_HEARING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "CaseClosed",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Reinstatement request")),
                        List.of(
                                Map.of(
                                        "name", "Review reinstatement request - Legal Officer",
                                        "workType", DECISION_WORK_TYPE,
                                        "taskId", REVIEW_REINSTATEMENT_REQ_LO_TASK,
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_APPLICATION,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-legal-officer",
                        "*",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Other")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_OTHER_REQ_LO_TASK,
                                        "name", "Review other request - Legal Officer",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_LO
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listed case (within 5 days)")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_LIST_CASE_WITHIN_5DAYS_JUDGE_TASK,
                                        "name", "Review list case (within 5 days) - Judge",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_HEARING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Postponement request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_POSTPONEMENT_REQ_JUDGE_TASK,
                                        "name", "Review postponement request - Judge",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_HEARING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "CaseClosed",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Corrections")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_CORRECTIONS_REQ_TASK,
                                        "name", "Review corrections request",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_AMENDMENT,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "CaseClosed",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Written reasons request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_WRITTEN_REASONS_REQ_TASK,
                                        "name", "Review written reasons request",
                                        "workingDaysAllowed", 28,
                                        "processCategories", PROCESS_CATEGORY_DECISION,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "CaseClosed",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Reinstatement request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_REINSTATEMENT_REQ_JUDGE_TASK,
                                        "name", "Review reinstatement request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_APPLICATION,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "CaseClosed",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Set aside request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_SET_ASIDE_REQ_TASK,
                                        "name", "Review set aside request",
                                        "workingDaysAllowed", 2,
                                        "processCategories", PROCESS_CATEGORY_DECISION,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Stay request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_STAY_REQ_JUDGE_TASK,
                                        "name", "Review stay request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Stay request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_STAY_REQ_JUDGE_TASK,
                                        "name", "Review stay request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Stay request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_STAY_REQ_CASE_LISTED_JUDGE_TASK,
                                        "name", "Review stay request case listed - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "New case")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_NEW_CASE_PROVIDE_DIR_JUDGE_TASK,
                                        "name", "Review new case and provide directions - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "New case")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_NEW_CASE_PROVIDE_DIR_JUDGE_TASK,
                                        "name", "Review new case and provide directions - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "*",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Other")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_OTHER_REQ_JUDGE_TASK,
                                        "name", "Review other request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Withdrawal request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_WITHDRAWAL_REQ_JUDGE_TASK,
                                        "name", "Review withdrawal request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Withdrawal request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_WITHDRAWAL_REQ_JUDGE_TASK,
                                        "name", "Review withdrawal request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Withdrawal request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_WITHDRAWAL_REQ_CASE_LISTED_JUDGE_TASK,
                                        "name", "Review withdrawal request case listed - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Rule 27 request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_RULE27_REQ_JUDGE_TASK,
                                        "name", "Review Rule 27 request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Rule 27 request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_RULE27_REQ_JUDGE_TASK,
                                        "name", "Review Rule 27 request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Rule 27 request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_RULE27_REQ_CASE_LISTED_JUDGE_TASK,
                                        "name", "Review Rule 27 request case listed - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listing directions")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_LISTING_DIR_JUDGE_TASK,
                                        "name", "Review listing directions - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listing directions")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_LISTING_DIR_CASE_LISTED_JUDGE_TASK,
                                        "name", "Review listing directions case listed - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "AwaitingHearing",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Listed case")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_LIST_CASE_JUDGE_TASK,
                                        "name", "Review list case - Judge",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_HEARING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Strike out request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_STRIKE_OUT_REQ_JUDGE_TASK,
                                        "name", "Review strike out request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Strike out request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_STRIKE_OUT_REQ_JUDGE_TASK,
                                        "name", "Review strike out request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "CaseManagement",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Time extension request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_TIME_EXT_REQ_JUDGE_TASK,
                                        "name", "Review time extension request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "refer-to-judge",
                        "ReadyToList",
                        Map.of("Data", Map.of("cicCaseReferralTypeForWA", "Time extension request")),
                        List.of(
                                Map.of(
                                        "taskId", REVIEW_TIME_EXT_REQ_JUDGE_TASK,
                                        "name", "Review time extension request - Judge",
                                        "workingDaysAllowed", 5,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", DECISION_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_JUDICIAL
                                )
                        )
                ),
                Arguments.of(
                        "caseworker-send-order",
                        "CaseManagement",
                        null,
                        List.of(
                                Map.of(
                                        "taskId", FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK,
                                        "name", "Follow up noncompliance of directions",
                                        "workingDaysAllowed", 1,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "citizen-cic-dss-update-case",
                        "*",
                        null,
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_FURTHER_EVIDENCE_TASK,
                                        "name", "Process further evidence",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "caseworker-document-management",
                        "*",
                        null,
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_FURTHER_EVIDENCE_TASK,
                                        "name", "Process further evidence",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "respondent-document-management",
                        "*",
                        null,
                        List.of(
                                Map.of(
                                        "taskId", PROCESS_FURTHER_EVIDENCE_TASK,
                                        "name", "Process further evidence",
                                        "workingDaysAllowed", 7,
                                        "processCategories", PROCESS_CATEGORY_PROCESSING,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                ),
                Arguments.of(
                        "caseworker-issue-case",
                        "CaseManagement",
                        null,
                        List.of(
                                Map.of(
                                        "taskId", CREATE_DUE_DATE,
                                        "name", "Create due date",
                                        "workingDaysAllowed", 2,
                                        "processCategories", PROCESS_CATEGORY_ISSUE_CASE,
                                        "workType", ROUTINE_WORK_TYPE,
                                        "roleCategory", ROLE_CATEGORY_ADMIN
                                )
                        )
                )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(3));
        assertThat(logic.getOutputs().size(), is(7));
        assertThat(logic.getRules().size(), is(64));
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} appeal type: {2}")
    @MethodSource("scenarioProvider")
    void given_multiple_event_ids_should_evaluate_dmn(String eventId,
                                                      String postEventState,
                                                      Map<String, Object> map,
                                                      List<Map<String, String>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue("additionalData", map);
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }
}
