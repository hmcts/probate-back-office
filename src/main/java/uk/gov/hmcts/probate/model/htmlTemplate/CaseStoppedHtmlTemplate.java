package uk.gov.hmcts.probate.model.htmlTemplate;

import java.util.List;

public class CaseStoppedHtmlTemplate {

    public static final String baseTemplate = "<p>The case was stopped on <stopDate> for one of two reasons:</p>\n<caseStopReasonsList>\n" +
            "<p>You will be notified by email if we need any information from you to progress the case.</p>\n" +
            "<p>Only contact the CTSC staff if your case has been stopped for <numWeeks> weeks or more and you have not received any communication since then.</p>";

    public static final List<String> caseStopReasons = List.of("an internal review is needed", "further information from the applicant or solicitor is needed");

    private CaseStoppedHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
