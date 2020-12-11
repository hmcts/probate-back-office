package uk.gov.hmcts.probate.model;

public class UrlConstants {
    // change on merge
    public static final String DECEASED_DETAILS_URL_TEMPLATE =
            "v2/case/<CASE_ID>/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1";
    public static final String ADD_APPLICATION_DETAILS_URL_TEMPLATE =
            "v2/case/<CASE_ID>/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1";
    public static final String REVIEW_OR_SUBMIT_URL_TEMPLATE =
            "v2/case/<CASE_ID>/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1";

    private UrlConstants() {
        throw new IllegalStateException("Utility class");
    }
}
