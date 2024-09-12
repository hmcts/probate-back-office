package uk.gov.hmcts.probate.service.tasklist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.CaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.probate.model.ApplicationState.REGISTRAR_ESCALATION;

class EscalatedTaskListRendererTest {

    public static final Long ID = 1L;
    public static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    private EscalatedTaskListRenderer renderer = new EscalatedTaskListRenderer();

    private CaseDataBuilder caseDataBuilder;


    @BeforeEach
    public void setup() {

        caseDataBuilder = CaseData.builder()
                .escalatedDate(LocalDate.of(2020,1,1));

    }

    @Test
    void shouldRenderStoppedCaseProgressHtmlCorrectly() {
        final CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState(REGISTRAR_ESCALATION.getId());
        final String expectedHtml = "<div class=\"width-50\">\n\n"
            + "<h2 class=\"govuk-heading-l\">Case progress</h2>"
            + "<h2 class=\"govuk-heading-l\">Cynnydd yr achos</h2>\n\n"
            + "<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">"
            + "Case escalated to a Registrar</div>"
            + "<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">"
            + "Mae'r achos wedi ei gyfeirio at Gofrestrydd</div>\n\n"
            + "<h2 class=\"govuk-heading-l\">What happens next</h2>"
            + "<h2 class=\"govuk-heading-l\">Beth fydd yn digwydd nesaf</h2>\n\n"
            + "<p class=\"govuk-body-s\">The case was escalated on 01 Jan 2020.</p>\n"
            + "<p class=\"govuk-body-s\">The case will be reviewed by a Registrar and you will be notified "
            + "by email if we need any "
            + "information from you to progress the case.</p>\n"
            + "<p class=\"govuk-body-s\">Only contact the CTSC staff if your case has been escalated "
            + "for 6 weeks or more and you "
            + "have not received any communication since then.</p>\n\n\n"
            + "<h2 class=\"govuk-heading-l\">Get help with your application</h2>"
            + "<h2 class=\"govuk-heading-l\">Cael help gyda'ch cais</h2>\n\n"
            + "<h3 class=\"govuk-heading-m\">Telephone</h3>"
            + "<h3 class=\"govuk-heading-m\">Ffôn</h3>\n\n"
            + "<p class=\"govuk-body-s\">You will need the case reference or the deceased's full name when you call."
            + "</p>"
            + "<p class=\"govuk-body-s\">Bydd arnoch angen cyfeirnod yr achos neu enw llawn yr ymadawedig pan fyddwch "
            + "yn ffonio.</p><br/>"
            + "<p class=\"govuk-body-s\">Telephone: 0300 303 0648</p><p class=\"govuk-body-s\">"
            + "Monday to Friday, 9am to 1pm. Closed on Saturdays, Sundays and bank holidays</p><br/>"
            + "<p class=\"govuk-body-s\">Llinell Gymraeg: 0300 303 0654</p><p class=\"govuk-body-s\">"
            + "Dydd Llun i ddydd Iau, 9am - 5pm, dydd Gwener 9am - 4.30pm (ac eithrio gwyliau cyhoeddus)</p><br/>\n\n"
            + "<a href=\"https://www.gov.uk/call-charges\" target=\"_blank\" rel=\"noopener noreferrer\" "
            + "class=\"govuk-link\">"
            + "Find out about call charges</a><br/>\n\n"
            + "<a href=\"https://www.gov.uk/call-charges\" target=\"_blank\" rel=\"noopener noreferrer\" "
            + "class=\"govuk-link\">"
            + "Gwybodaeth am gost galwadau</a><br/>\n\n"
            + "<h3 class=\"govuk-heading-m\">Email</h3><h3 class=\"govuk-heading-m\">E-bost</h3>\n\n"
            + "<a href=\"mailto:contactprobate@justice.gov.uk\" class=\"govuk-link\">contactprobate@justice.gov.uk</a>"
            + "<p class=\"govuk-body-s\">We aim to respond within 10 working days</p>"
            + "<p class=\"govuk-body-s\">Rydym yn anelu at ymateb o fewn 10 diwrnod gwaith</p>\n\n"
            + "</div>";
        final String result = renderer.renderHtml(caseDetails);

        assertEquals(expectedHtml, result);
    }
}
