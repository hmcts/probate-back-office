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
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_CONFIGURATION_ST_CIC_CRIMINALINJURIESCOMPENSATION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ACCESS_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ADDITIONAL_PROPERTIES_ROLE_ASSIGNMENT_ID;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.APPLICATION_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.CASE_MANAGEMENT_CATEGORY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.CASE_NAME;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.COMPLETE_HEARING_OUTCOME_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.CREATE_DUE_DATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DECISION_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DEFAULT_MAJOR_PRIORITY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DEFAULT_MINOR_PRIORITY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DESCRIPTION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_INTERVAL_DAYS;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_ORIGIN;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.HEARING_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ISSUE_CASE_TO_RESPONDENT_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ISSUE_DECISION_NOTICE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ISSUE_DUE_DATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.LOCATION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.LOCATION_NAME;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.MAJOR_PRIORITY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.MINOR_PRIORITY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PRIORITY_DATE_ORIGIN_REF;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PRIORITY_WORK_TYPE;
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
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REGION;
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
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_ADMIN;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_CTSC;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_JUDICIAL;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_LO;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROUTINE_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.STITCH_COLLATE_HEARING_BUNDLE_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.URGENT_MAJOR_PRIORITY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.VET_NEW_CASE_DOCUMENTS_TASK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.WORK_TYPE;

class CamundaTaskWaConfigurationTest extends DmnDecisionTableBaseUnitTest {

    private static final String REQUEST = "classpath:custom-case-data.json";
    private static final String taskId = UUID.randomUUID().toString();
    private static final String roleAssignmentId = UUID.randomUUID().toString();

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_ST_CIC_CRIMINALINJURIESCOMPENSATION;
    }

    static Stream<Arguments> scenarioProvider() throws IOException {
        return Stream.of(

                Arguments.of(
                        PROCESS_CASE_WITHDRAWAL_DIR_TASK,
                        CaseDataBuilder.defaultCase()
                                .isUrgent()
                                .build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, URGENT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "7", true)
                                .expectedValue(DESCRIPTION, "[Orders: Send order](/cases/case-details"
                                        + "/${[CASE_REFERENCE]}/trigger/caseworker-send-order)", true)
                                .expectedValue(DUE_DATE_ORIGIN, ZonedDateTime.now(), false)
                                .build()
                ),
                Arguments.of(
                        PROCESS_CASE_WITHDRAWAL_DIR_LISTED_TASK,
                        CaseDataBuilder.defaultCase()
                                .isUrgent()
                                .build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, URGENT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "7", true)
                                .expectedValue(DESCRIPTION, "[Orders: Send order](/cases/case-details"
                                        + "/${[CASE_REFERENCE]}/trigger/caseworker-send-order)", true)
                                .expectedValue(DUE_DATE_ORIGIN, ZonedDateTime.now(), false)
                                .build()
                ),
                Arguments.of(
                        PROCESS_RULE27_DECISION_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "7", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_RULE27_DECISION_LISTED_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "7", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_LISTING_DIR_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "3", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_LISTING_DIR_LISTED_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "3", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_DIR_RELISTED_CASE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_DIR_RELISTED_CASE_WITHIN_5DAYS_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, PRIORITY_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_SET_ASIDE_DIR_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_CORRECTIONS_TASK,
                        CaseDataBuilder.defaultCase()
                                .isUrgent()
                                .build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, URGENT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, HEARING_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "3", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_DIR_RETURNED_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "7", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_POSTPONEMENT_DIR_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_TIME_EXT_DIR_RETURNED_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_REINSTATEMENT_DECISION_NOTICE_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_OTHER_DIR_RETURNED_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "7", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_WRITTEN_REASONS_TASK,
                        CaseDataBuilder.defaultCase()
                                .isUrgent()
                                .build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, URGENT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "3", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_STRIKE_OUT_DIR_RETURNED_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "7", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_STAY_DIR_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "7", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_STAY_DIR_LISTED_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "7", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Send order](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-send-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        ISSUE_DECISION_NOTICE_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, HEARING_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Decision: Issue a decision](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-issue-decision)<br/>[Decision: Issue final decision]"
                                                + "(/cases/case-details/${[CASE_REFERENCE]}"
                                                + "/trigger/caseworker-issue-final-decision)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        COMPLETE_HEARING_OUTCOME_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, HEARING_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Hearings:Create summary](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-hearing-summary)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        ISSUE_CASE_TO_RESPONDENT_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "2", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Case: Issue to respondent](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-issue-case)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        VET_NEW_CASE_DOCUMENTS_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, APPLICATION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Case: Build case](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-case-built)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_NEW_CASE_PROVIDE_DIR_LO_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_TIME_EXT_REQ_LO_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_STRIKE_OUT_REQ_LO_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_STAY_REQ_LO_TASK,
                        CaseDataBuilder.defaultCase()
                                .isUrgent()
                                .build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, URGENT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_STAY_REQ_CASE_LISTED_LO_TASK,
                        CaseDataBuilder.defaultCase()
                                .isUrgent()
                                .build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, URGENT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_LISTING_DIR_LO_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_LISTING_DIR_CASE_LISTED_LO_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_WITHDRAWAL_REQ_LO_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_WITHDRAWAL_REQ_CASE_LISTED_LO_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_RULE27_REQ_LO_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_RULE27_REQ_CASE_LISTED_LO_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_LIST_CASE_LO_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_OTHER_REQ_LO_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_LIST_CASE_WITHIN_5DAYS_LO_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_POSTPONEMENT_REQ_LO_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_REINSTATEMENT_REQ_LO_TASK,
                        CaseDataBuilder.defaultCase()
                                .isUrgent()
                                .build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, URGENT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_LIST_CASE_WITHIN_5DAYS_JUDGE_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_POSTPONEMENT_REQ_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_CORRECTIONS_REQ_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_WRITTEN_REASONS_REQ_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "28", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_REINSTATEMENT_REQ_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_SET_ASIDE_REQ_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "2", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_STAY_REQ_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_STAY_REQ_CASE_LISTED_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_NEW_CASE_PROVIDE_DIR_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_OTHER_REQ_JUDGE_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_WITHDRAWAL_REQ_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_WITHDRAWAL_REQ_CASE_LISTED_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_RULE27_REQ_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_RULE27_REQ_CASE_LISTED_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_LISTING_DIR_JUDGE_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_LISTING_DIR_CASE_LISTED_JUDGE_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_LIST_CASE_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_STRIKE_OUT_REQ_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_TIME_EXT_REQ_JUDGE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Document management: Upload](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-document-management)<br/>"
                                                + "[Orders: Manage due date]"
                                                + "(/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-amend-due-date)<br/>"
                                                + "[Refer case to judge](/cases/case-details/${[CASE_REFERENCE"
                                                + "]}/trigger"
                                                + "/refer-to-judge)<br/>"
                                                + "[Refer case to legal officer]"
                                                + "(/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/refer-to-legal-officer)<br/>"
                                                + "[Case: Contact parties](/cases/case-details/${[CASE_REFERENCE"
                                                + "]}/trigger"
                                                + "/contact-parties)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REGISTER_NEW_CASE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, APPLICATION_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "5", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Case: Edit case](/cases/case-details/${[CASE_REFERENCE]}/trigger/edit-case)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        PROCESS_FURTHER_EVIDENCE_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "7", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Document management: Amend](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/caseworker-amend-document)<br/>"
                                                + "[Case: Edit case](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/edit-case)<br/>"
                                                + "[Refer case to judge](/cases/case-details/${[CASE_REFERENCE"
                                                + "]}/trigger"
                                                + "/refer-to-judge)<br/>"
                                                + "[Refer case to legal officer]"
                                                + "(/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/refer-to-legal-officer)<br/>"
                                                + "[Case: Contact parties](/cases/case-details/${[CASE_REFERENCE"
                                                + "]}/trigger"
                                                + "/contact-parties)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        STITCH_COLLATE_HEARING_BUNDLE_TASK,
                        CaseDataBuilder.customCase(REQUEST).build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(CASE_NAME, "Rio Read", true)
                                .expectedValue(CASE_MANAGEMENT_CATEGORY, "ST CIC", true)
                                .expectedValue(REGION, "123", true)
                                .expectedValue(LOCATION, "123456", true)
                                .expectedValue(LOCATION_NAME, "GTC", true)
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, HEARING_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "1", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Bundle: Create a bundle](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/createBundle)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        REVIEW_SPECIFIC_ACCESS_REQ_JUDICIARY_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ACCESS_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_JUDICIAL, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "2", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Review Access Request](/role-access/" + taskId + "/assignment/"
                                                + roleAssignmentId + "/specific-access)",
                                        true
                                )
                                .expectedValue(ADDITIONAL_PROPERTIES_ROLE_ASSIGNMENT_ID, roleAssignmentId, false)
                                .build()
                ),
                Arguments.of(
                        REVIEW_SPECIFIC_ACCESS_REQ_LO_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ACCESS_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_LO, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "2", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Review Access Request](/role-access/" + taskId + "/assignment/"
                                                + roleAssignmentId + "/specific-access)",
                                        true
                                )
                                .expectedValue(ADDITIONAL_PROPERTIES_ROLE_ASSIGNMENT_ID, roleAssignmentId, false)
                                .build()
                ),
                Arguments.of(
                        REVIEW_SPECIFIC_ACCESS_REQ_ADMIN_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ACCESS_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "2", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Review Access Request](/role-access/" + taskId + "/assignment/"
                                                + roleAssignmentId + "/specific-access)",
                                        true
                                )
                                .expectedValue(ADDITIONAL_PROPERTIES_ROLE_ASSIGNMENT_ID, roleAssignmentId, false)
                                .build()
                ),
                Arguments.of(
                        REVIEW_SPECIFIC_ACCESS_REQ_CTSC_TASK,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ACCESS_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_CTSC, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "2", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Review Access Request](/role-access/" + taskId + "/assignment/"
                                                + roleAssignmentId + "/specific-access)",
                                        true
                                )
                                .expectedValue(ADDITIONAL_PROPERTIES_ROLE_ASSIGNMENT_ID, roleAssignmentId, false)
                                .build()
                ),
                Arguments.of(
                        CREATE_DUE_DATE,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "2", true)
                                .expectedValue(
                                        DESCRIPTION,
                                        "[Orders: Create draft](/cases/case-details/${[CASE_REFERENCE]}/trigger"
                                                + "/create-draft-order)",
                                        true
                                )
                                .build()
                ),
                Arguments.of(
                        ISSUE_DUE_DATE,
                        CaseDataBuilder.defaultCase().build(),
                        ConfigurationExpectationBuilder.defaultExpectations()
                                .expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true)
                                .expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true)
                                .expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true)
                                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true)
                                .expectedValue(DUE_DATE_INTERVAL_DAYS, "2", true)
                                .expectedValue(DESCRIPTION, "[Orders: Send order](/cases/case-details"
                                        + "/${[CASE_REFERENCE]}/trigger/caseworker-send-order)", true)
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
        assertEquals(43, logic.getRules().size());
    }

    @ParameterizedTest(name = "task type: {0} case data: {1}")
    @MethodSource("scenarioProvider")
    void should_return_correct_configuration_values_for_scenario(
            String taskType, Map<String, Object> caseData,
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

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        resultsMatch(dmnDecisionTableResult.getResultList(), expectation);
    }

    private void resultsMatch(List<Map<String, Object>> results, List<Map<String, Object>> expectation) {
        assertThat(results.size(), is(expectation.size()));

        for (int index = 0; index < expectation.size(); index++) {
            if (DUE_DATE_ORIGIN.equals(expectation.get(index).get("name"))) {
                assertEquals(
                        expectation.get(index).get("canReconfigure"),
                        results.get(index).get("canReconfigure")
                );
                assertTrue(validNow(
                        ZonedDateTime.parse(expectation.get(index).get("value").toString()),
                        ZonedDateTime.parse(results.get(index).get("value").toString())
                ));

            } else if (PRIORITY_DATE_ORIGIN_REF.equals(expectation.get(index).get("name"))) {
                assertEquals(
                        expectation.get(index).get("canReconfigure"),
                        results.get(index).get("canReconfigure")
                );
                assertTrue(LocalDate.parse(expectation.get(index).get("value").toString()).isEqual(
                        LocalDate.parse(results.get(index).get("value").toString()))
                        || LocalDate.parse(expectation.get(index).get("value").toString()).isAfter(
                        LocalDate.parse(results.get(index).get("value").toString()))
                );

            } else {
                assertThat(results.get(index), is(expectation.get(index)));
            }
        }
    }

    private boolean validNow(ZonedDateTime expected, ZonedDateTime result) {
        ZonedDateTime now = ZonedDateTime.now();
        return result != null
                && (expected.isEqual(result) || expected.isBefore(result))
                && (now.isEqual(result) || now.isAfter(result));
    }
}
