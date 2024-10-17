package uk.gov.hmcts.probate.model.htmltemplate;

public class CaseEscalatedToRegistrarHtmlTemplate {
    public static final String BASE_TEMPLATE = "<p>The case was escalated on <escalationDate>.</p>\n"
        + "<p>Cyfeiriwyd yr achos ar <escalationDateWelsh>.</p>\n"
        + "<p>The case will be reviewed by a Registrar and you will be notified by email "
        + "if we need any information from you to progress the case.</p>\n"
        + "<p>Bydd yr achos yn cael ei adolygu gan Gofrestrydd a byddwch yn cael eich hysbysu drwy e-bost "
        + "os bydd arnom angen unrhyw wybodaeth gennych i symud yr achos yn ei flaen.</p>\n"
        + "<p>Only contact the CTSC staff if your case has been escalated for <numWeeks> weeks or more "
        + "and you have not received any communication since then.</p>\n"
        + "<p>Dim ond os yw eich achos wedi'i gyfeirio ers <numWeeksWelsh> wythnos neu fwy y dylech gysylltu â staff "
        + "y CTSC ac nad ydych wedi cael unrhyw ohebiaeth ers hynny.</p>\n";

    private CaseEscalatedToRegistrarHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
