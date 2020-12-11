package uk.gov.hmcts.probate.model;

public class UrlConstants {
    public static final String DECEASED_DETAILS_URL_TEMPLATE =
            "v2/case/<CASE_ID>/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1";
    public static final String ADD_APPLICATION_DETAILS_URL_TEMPLATE =
            "v2/case/<CASE_ID>/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1";
    public static final String REVIEW_OR_SUBMIT_URL_TEMPLATE =
            "v2/case/<CASE_ID>/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1";
    public static final String STATUS_URL_COMPLETED =
            "https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-715-case-progress-tab/src/main/resources/statusImages/completed.png";
    public static final String STATUS_URL_IN_PROGRESS =
            "https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-715-case-progress-tab/src/main/resources/statusImages/in-progress.png";
    public static final String STATUS_URL_NOT_STARTED =
            "https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-715-case-progress-tab/src/main/resources/statusImages/not-started.png";

    private UrlConstants() {
        throw new IllegalStateException("Utility class");
    }
}
