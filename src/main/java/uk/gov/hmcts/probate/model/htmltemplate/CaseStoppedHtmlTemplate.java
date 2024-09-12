package uk.gov.hmcts.probate.model.htmltemplate;

import java.util.List;

public class CaseStoppedHtmlTemplate {

    public static final String BASE_TEMPLATE =
        "<p>The case was stopped on <stopDate> for one of two reasons:</p>\n"
        + "<p>Cafodd yr achos ei atal ar <stopDateWelsh> am un o ddau reswm:</p>\n"
        + "<caseStopReasonsList>\n"
        + "<p>You will be notified by email if we need any information from you to progress the case.</p><p>\n"
        + "Fe'ch hysbysir drwy e-bost os bydd arnom angen unrhyw wybodaeth gennych i symud yr achos yn ei flaen.</p>\n"
        + "<p>You'll usually get the grant within <numWeeks> weeks. It can take longer if you need "
        + "to provide additional information.</p>\n"
        + "<p>Byddwch fel arfer yn cael y grant o fewn <numWeeksWelsh> wythnos. Gall gymryd mwy o amser os oes angen i "
        + "chi ddarparu gwybodaeth ychwanegol.</p>\n"
        + "<p>You don't need to do anything else now, we'll email you if we need more information</p>"
        + "<p>Nid oes angen i chi wneud unrhyw beth arall nawr, byddwn yn anfon e-bost atoch os oes "
        + "arnom angen mwy o wybodaeth.</p>";

    public static final List<String> CASE_STOP_REASONS =
        List.of("an internal review is needed", "Mae angen adolygiad mewnol",
                "further information from the applicant or Probate practitioner is needed",
                "Mae angen rhagor o wybodaeth gan y ceisydd neu'r ymarferydd profiant");

    private CaseStoppedHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
