package uk.gov.hmcts.probate.model.htmlTemplate;

import java.util.Arrays;
import java.util.List;

public class CaseEscalatedToRegistrarHtmlTemplate {
    public static String baseTemplate = "<div>The case was escalated on <escalationDate>.</div>\n" +
        "<div>The case will be reviewed by the Registrar and you will be notified by email if we need any information from you to progress the case</div>\n" +
        "<div>Only contact the CTSC staff if your case has been escalated for <numWeeks> weeks or more and you have not received any communication since then.</div>\n";
}
