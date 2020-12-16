package uk.gov.hmcts.probate.model;

public class UrlConstants {
    // for CCD should be v2/case/
    private static final String URL_PREFIX = "cases/case-details/";

    public static final String DECEASED_DETAILS_URL_TEMPLATE =
            URL_PREFIX + "<CASE_ID>/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1";
    public static final String ADD_APPLICATION_DETAILS_URL_TEMPLATE =
            URL_PREFIX + "<CASE_ID>/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1";
    public static final String REVIEW_OR_SUBMIT_URL_TEMPLATE =
            URL_PREFIX + "<CASE_ID>/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1";

    private UrlConstants() {
        throw new IllegalStateException("Utility class");
    }
}
