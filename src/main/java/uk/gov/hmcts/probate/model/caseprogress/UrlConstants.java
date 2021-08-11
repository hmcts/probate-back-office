package uk.gov.hmcts.probate.model.caseprogress;

public class UrlConstants {
    // for CCD should be v2/case/
    private static final String URL_PREFIX = "cases/case-details/";

    public static final String DECEASED_DETAILS_URL_TEMPLATE = URL_PREFIX
        + "<CASE_ID>/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1";

    public static final String ADD_APPLICATION_DETAILS_URL_TEMPLATE_GOP = URL_PREFIX
        + "<CASE_ID>/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1";

    public static final String ADD_APPLICATION_DETAILS_URL_TEMPLATE_INTESTACY = URL_PREFIX
        + "<CASE_ID>/trigger/solicitorUpdateIntestacy/solicitorUpdateIntestacysolicitorUpdateIntestacyPage1";

    public static final String ADD_APPLICATION_DETAILS_URL_TEMPLATE_ADMON_WILL = URL_PREFIX
        + "<CASE_ID>/trigger/solicitorUpdateAdmon/solicitorUpdateAdmonsolicitorUpdateAdmonPage1";

    public static final String REVIEW_OR_SUBMIT_URL_TEMPLATE = URL_PREFIX
        + "<CASE_ID>/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1";

    public static final String TL_COVERSHEET_URL_TEMPLATE = "<DOCUMENT_LINK>";

    private UrlConstants() {
        throw new IllegalStateException("Utility class");
    }
}
