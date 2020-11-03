package uk.gov.hmcts.probate.model;

public class UrlConstants {
    public static final String deceasedDetailsUrlTemplate =
            "v2/case/<CASE_ID>/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1";
            //"case/PROBATE/GrantOfRepresentation/<CASE_ID>/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1";
    public static final String addApplicationDetailsUrlTemplate =
            "v2/case/<CASE_ID>/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1";
    public static final String reviewOrSubmitUrlTemplate =
            "v2/case/<CASE_ID>/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1";


}
