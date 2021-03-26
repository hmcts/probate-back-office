package uk.gov.hmcts.probate.model;

public class StateConstants {
    public static final String STATE_CASE_PAYMENT_FAILED = "CasePaymentFailed";

    public static final String STATE_PA_APP_CREATED = "PAAppCreated";
    public static final String STATE_CASE_CREATED = "CaseCreated";
    public static final String STATE_SOL_APP_CREATED = "SolAppCreated";
    public static final String STATE_SOL_APP_UPDATED = "SolAppUpdated";

    public static final String STATE_SOL_PROBATE_CREATED = "SolProbateCreated";
    public static final String STATE_SOL_INTESTACY_CREATED = "SolIntestacyCreated";
    public static final String STATE_SOL_ADMON_CREATED = "SolAdmonCreated";


    public static final String STATE_CASE_PRINTED = "CasePrinted";
    public static final String STATE_BO_READY_FOR_EXAMINATION = "BOReadyForExamination";
    public static final String STATE_BO_EXAMINING = "BOExamining";

    public static final String STATE_BO_CAVEAT_PERMANENT = "BOCaveatPermenant";
    public static final String STATE_BO_READY_TO_ISSUE = "BOReadyToIssue";
    public static final String STATE_BO_QA = "BOCaseQA";

    public static final String STATE_BO_CASE_MATCHING_ISSUE_GRANT = "BOCaseMatchingIssueGrant";
    public static final String STATE_BO_CASE_MATCHING_EXAMINING = "BOCaseMatchingExamining";

    public static final String STATE_BO_GRANT_ISSUED = "BOGrantIssued";
    public static final String STATE_BO_CASE_CLOSED = "BOCaseClosed";
    public static final String STATE_BO_CASE_IMPORTED = "BOCaseImported";


    public static final String STATE_BO_EXAMINING_REISSUE = "BOExaminingReissue";
    public static final String STATE_BO_CASE_MATCHING_REISSUE = "BOCaseMatchingReissue";

    public static final String STATE_BO_REDEC_NOTIFICATION_SENT = "BORedecNotificationSent";
    public static final String STATE_BO_SOT_GENERATED = "BOSotGenerated";

    public static final String STATE_PENDING = "Pending";


    public static final String STATE_BO_CASE_STOPPED = "BOCaseStopped";
    public static final String STATE_BO_CASE_STOPPED_REISSUE = "BOCaseStoppedReissue";
    public static final String STATE_BO_CASE_STOPPED_AWAIT_REDEC = "BOCaseStoppedAwaitRedec";

    public static final String STATE_BO_REGISTRAR_ESCALATION = "BORegistrarEscalation";

    public static final String STATE_APPLICATION_STOPPED = "Stopped";

    // inset text for case progress tab
    public static final String STATE_DESC_DEFAULT = "Default";
    public static final String STATE_DESC_ESCALATED = "Case escalated to a Registrar";
    public static final String STATE_DESC_CASE_STOPPED = "Case stopped";
    public static final String STATE_DESC_APPLICATION_STOPPED = "Paper application needed";

    private StateConstants() {
        throw new IllegalStateException("Utility class");
    }
}
