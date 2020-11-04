package uk.gov.hmcts.probate.model;

public class UrlConstants {
    public static final String deceasedDetailsUrlTemplate =
            "v2/case/<CASE_ID>/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1";
            //"case/PROBATE/GrantOfRepresentation/<CASE_ID>/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1";
    public static final String addApplicationDetailsUrlTemplate =
            "v2/case/<CASE_ID>/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1";
    public static final String reviewOrSubmitUrlTemplate =
            "v2/case/<CASE_ID>/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1";
    public static final String viewDocumentsToBeSentInUrlTemplate =
            null; // TODO - await from Narm
    public static final String statusUrlCompleted =
            "https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png";
    public static final String statusUrlInProgress =
            "https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/in-progress.png";
    public static final String statusUrlNotStarted =
            "https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/not-started.png";

}
