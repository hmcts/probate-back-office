package uk.gov.hmcts.probate.model.htmltemplate;

import java.util.List;

public class CaseStoppedHtmlTemplate {

    public static final String BASE_TEMPLATE =
        "<p>The case was stopped on <stopDate> for one of two reasons:</p>\n<caseStopReasonsList>\n"
        + "<p>You will be notified by email if we need any information from you to progress the case.</p>\n"
        + "<p>You'll usually get the grant within <numWeeks> weeks. It can take longer if you need "
        + "to provide additional information.</p>\n"
        + "<p>You don't need to do anything else now, we'll email you if we need more information</p>";

    public static final List<String> CASE_STOP_REASONS =
        List.of("an internal review is needed", "further information from the applicant or Probate practitioner "
                + "is needed");

    private CaseStoppedHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
