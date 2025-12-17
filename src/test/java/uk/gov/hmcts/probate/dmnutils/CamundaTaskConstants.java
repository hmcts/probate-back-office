package uk.gov.hmcts.probate.dmnutils;

public class CamundaTaskConstants {

    private CamundaTaskConstants() {
    }

    public static final String CASE_NAME = "caseName";
    public static final String CASE_MANAGEMENT_CATEGORY = "caseManagementCategory";
    public static final String REGION = "region";
    public static final String LOCATION = "location";
    public static final String LOCATION_NAME = "locationName";
    public static final String DUE_DATE_NON_WORKING_CALENDAR = "dueDateNonWorkingCalendar";
    public static final String DUE_DATE_WORKING_DAYS_OF_WEEK = "dueDateNonWorkingDaysOfWeek";
    public static final String WORK_TYPE = "workType";
    public static final String ROLE_CATEGORY = "roleCategory";
    public static final String MINOR_PRIORITY = "minorPriority";
    public static final String MAJOR_PRIORITY = "majorPriority";
    public static final String DESCRIPTION = "description";
    public static final String DUE_DATE_INTERVAL_DAYS = "dueDateIntervalDays";
    public static final String DUE_DATE_ORIGIN = "dueDateOrigin";
    public static final String DUE_DATE_TIME = "dueDateTime";
    public static final String PRIORITY_DATE_ORIGIN_REF = "priorityDateOriginRef";
    public static final String ADDITIONAL_PROPERTIES_ROLE_ASSIGNMENT_ID = "additionalProperties_roleAssignmentId";

    public static final String DECISION_WORK_TYPE = "decision_making_work";
    public static final String ROUTINE_WORK_TYPE = "routine_work";
    public static final String HEARING_WORK_TYPE = "hearing_work";
    public static final String PRIORITY_WORK_TYPE = "priority";
    public static final String APPLICATION_WORK_TYPE = "applications";
    public static final String ACCESS_WORK_TYPE = "access_requests";

    public static final String ROLE_CATEGORY_ADMIN = "ADMIN";
    public static final String ROLE_CATEGORY_LO = "LEGAL_OPERATIONS";
    public static final String ROLE_CATEGORY_JUDICIAL = "JUDICIAL";
    public static final String ROLE_CATEGORY_CTSC = "CTSC";

    public static final String DEFAULT_MINOR_PRIORITY = "500";
    public static final String DEFAULT_MAJOR_PRIORITY = "5000";
    public static final String URGENT_MAJOR_PRIORITY = "2000";
    public static final String DEFAULT_CASE_MANAGEMENT_CATEGORY = "Criminal Injuries Compensation";
    public static final String DEFAULT_REGION = "1";
    public static final String DEFAULT_LOCATION = "336559";
    public static final String DEFAULT_LOCATION_NAME = "Glasgow Tribunals Centre";
    public static final String DEFAULT_DUE_DATE_NON_WORKING_CALENDAR
        = "https://www.gov.uk/bank-holidays/scotland.json, https://raw.githubusercontent.com/hmcts/sptribs-case-api/master/src/main/resources/dmn/privilege-calendar.json";
    public static final String DEFAULT_DUE_DATE_WORKING_DAYS_OF_WEEK = "SATURDAY,SUNDAY";

    public static final String REGISTER_NEW_CASE_TASK = "registerNewCase";
    public static final String VET_NEW_CASE_DOCUMENTS_TASK = "vetNewCaseDocuments";
    public static final String ISSUE_CASE_TO_RESPONDENT_TASK = "issueCaseToRespondent";
    public static final String REVIEW_NEW_CASE_PROVIDE_DIR_JUDGE_TASK = "reviewNewCaseAndProvideDirectionsJudge";
    public static final String REVIEW_NEW_CASE_PROVIDE_DIR_LO_TASK = "reviewNewCaseAndProvideDirectionsLO";
    public static final String PROCESS_DIR_RETURNED_TASK = "processDirectionsReturned";
    public static final String PROCESS_FURTHER_EVIDENCE_TASK = "processFurtherEvidence";
    public static final String REVIEW_TIME_EXT_REQ_JUDGE_TASK = "reviewTimeExtensionRequestJudge";
    public static final String REVIEW_TIME_EXT_REQ_LO_TASK = "reviewTimeExtensionRequestLO";
    public static final String PROCESS_TIME_EXT_DIR_RETURNED_TASK = "processTimeExtensionDirectionsReturned";
    public static final String FOLLOW_UP_NONCOMPLIANCE_OF_DIR_TASK = "followUpNoncomplianceOfDirections";
    public static final String REVIEW_STRIKE_OUT_REQ_JUDGE_TASK = "reviewStrikeOutRequestJudge";
    public static final String REVIEW_STRIKE_OUT_REQ_LO_TASK = "reviewStrikeOutRequestLO";
    public static final String PROCESS_STRIKE_OUT_DIR_RETURNED_TASK = "processStrikeOutDirectionsReturned";
    public static final String REVIEW_WITHDRAWAL_REQ_JUDGE_TASK = "reviewWithdrawalRequestJudge";
    public static final String REVIEW_WITHDRAWAL_REQ_CASE_LISTED_JUDGE_TASK = "reviewWithdrawalRequestCaseListedJudge";
    public static final String REVIEW_WITHDRAWAL_REQ_LO_TASK = "reviewWithdrawalRequestLO";
    public static final String REVIEW_WITHDRAWAL_REQ_CASE_LISTED_LO_TASK = "reviewWithdrawalRequestCaseListedLO";
    public static final String PROCESS_CASE_WITHDRAWAL_DIR_TASK = "processCaseWithdrawalDirections";
    public static final String PROCESS_CASE_WITHDRAWAL_DIR_LISTED_TASK = "processCaseWithdrawalDirectionsListed";
    public static final String REVIEW_RULE27_REQ_JUDGE_TASK = "reviewRule27RequestJudge";
    public static final String REVIEW_RULE27_REQ_CASE_LISTED_JUDGE_TASK = "reviewRule27RequestCaseListedJudge";
    public static final String REVIEW_RULE27_REQ_LO_TASK = "reviewRule27RequestLO";
    public static final String REVIEW_RULE27_REQ_CASE_LISTED_LO_TASK = "reviewRule27RequestCaseListedLO";
    public static final String PROCESS_RULE27_DECISION_TASK = "processRule27Decision";
    public static final String PROCESS_RULE27_DECISION_LISTED_TASK = "processRule27DecisionListed";
    public static final String REVIEW_STAY_REQ_JUDGE_TASK = "reviewStayRequestJudge";
    public static final String REVIEW_STAY_REQ_CASE_LISTED_JUDGE_TASK = "reviewStayRequestCaseListedJudge";
    public static final String REVIEW_STAY_REQ_LO_TASK = "reviewStayRequestLO";
    public static final String REVIEW_STAY_REQ_CASE_LISTED_LO_TASK = "reviewStayRequestCaseListedLO";
    public static final String PROCESS_STAY_DIR_TASK = "processStayDirections";
    public static final String PROCESS_STAY_DIR_LISTED_TASK = "processStayDirectionsListed";
    public static final String REVIEW_LISTING_DIR_JUDGE_TASK = "reviewListingDirectionsJudge";
    public static final String REVIEW_LISTING_DIR_CASE_LISTED_JUDGE_TASK = "reviewListingDirectionsCaseListedJudge";
    public static final String REVIEW_LISTING_DIR_LO_TASK = "reviewListingDirectionsLO";
    public static final String REVIEW_LISTING_DIR_CASE_LISTED_LO_TASK = "reviewListingDirectionsCaseListedLO";
    public static final String PROCESS_LISTING_DIR_TASK = "processListingDirections";
    public static final String PROCESS_LISTING_DIR_LISTED_TASK = "processListingDirectionsListed";
    public static final String STITCH_COLLATE_HEARING_BUNDLE_TASK = "stitchCollateHearingBundle";
    public static final String REVIEW_LIST_CASE_JUDGE_TASK = "reviewListCaseJudge";
    public static final String REVIEW_LIST_CASE_LO_TASK = "reviewListCaseLO";
    public static final String PROCESS_DIR_RELISTED_CASE_TASK = "processDirectionsReListedCase";
    public static final String REVIEW_LIST_CASE_WITHIN_5DAYS_JUDGE_TASK = "reviewListCaseWithin5DaysJudge";
    public static final String REVIEW_LIST_CASE_WITHIN_5DAYS_LO_TASK = "reviewListCaseWithin5DaysLO";
    public static final String PROCESS_DIR_RELISTED_CASE_WITHIN_5DAYS_TASK = "processDirectionsReListedCaseWithin5Days";
    public static final String REVIEW_POSTPONEMENT_REQ_JUDGE_TASK = "reviewPostponementRequestJudge";
    public static final String REVIEW_POSTPONEMENT_REQ_LO_TASK = "reviewPostponementRequestLO";
    public static final String PROCESS_POSTPONEMENT_DIR_TASK = "processPostponementDirections";
    public static final String COMPLETE_HEARING_OUTCOME_TASK = "completeHearingOutcome";
    public static final String ISSUE_DECISION_NOTICE_TASK = "issueDecisionNotice";
    public static final String REVIEW_CORRECTIONS_REQ_TASK = "reviewCorrectionsRequest";
    public static final String PROCESS_CORRECTIONS_TASK = "processCorrections";
    public static final String REVIEW_SET_ASIDE_REQ_TASK = "reviewSetAsideRequest";
    public static final String PROCESS_SET_ASIDE_DIR_TASK = "processSetAsideDirections";
    public static final String REVIEW_WRITTEN_REASONS_REQ_TASK = "reviewWrittenReasonsRequest";
    public static final String PROCESS_WRITTEN_REASONS_TASK = "processWrittenReasons";
    public static final String REVIEW_REINSTATEMENT_REQ_JUDGE_TASK = "reviewReinstatementRequestJudge";
    public static final String REVIEW_REINSTATEMENT_REQ_LO_TASK = "reviewReinstatementRequestLO";
    public static final String PROCESS_REINSTATEMENT_DECISION_NOTICE_TASK = "processReinstatementDecisionNotice";
    public static final String REVIEW_OTHER_REQ_JUDGE_TASK = "reviewOtherRequestJudge";
    public static final String REVIEW_OTHER_REQ_LO_TASK = "reviewOtherRequestLO";
    public static final String PROCESS_OTHER_DIR_RETURNED_TASK = "processOtherDirectionsReturned";
    public static final String REVIEW_SPECIFIC_ACCESS_REQ_JUDICIARY_TASK = "reviewSpecificAccessRequestJudiciary";
    public static final String REVIEW_SPECIFIC_ACCESS_REQ_LO_TASK = "reviewSpecificAccessRequestLegalOps";
    public static final String REVIEW_SPECIFIC_ACCESS_REQ_ADMIN_TASK = "reviewSpecificAccessRequestAdmin";
    public static final String REVIEW_SPECIFIC_ACCESS_REQ_CTSC_TASK = "reviewSpecificAccessRequestCTSC";

    public static final String CREATE_DUE_DATE = "createDueDate";
    public static final String ISSUE_DUE_DATE = "issueDueDate";

    public static final String AUTO_COMPLETE_MODE = "Auto";
    public static final String DEFAULT_NONE_COMPLETE_MODE = "defaultNone";

    public static final String PROCESS_CATEGORY_PROCESSING = "Processing";
    public static final String PROCESS_CATEGORY_HEARING = "Hearing";
    public static final String PROCESS_CATEGORY_DECISION = "Decision";
    public static final String PROCESS_CATEGORY_AMENDMENT = "Amendment";
    public static final String PROCESS_CATEGORY_APPLICATION = "Application";
    public static final String PROCESS_CATEGORY_HEARING_BUNDLE = "HearingBundle";
    public static final String PROCESS_CATEGORY_ISSUE_CASE = "IssueCase";
    public static final String PROCESS_CATEGORY_HEARING_COMPLETION = "HearingCompletion";
}
