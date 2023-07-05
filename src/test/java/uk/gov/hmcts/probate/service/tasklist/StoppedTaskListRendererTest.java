package uk.gov.hmcts.probate.service.tasklist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.probate.model.ApplicationState.BO_CASE_STOPPED;

class StoppedTaskListRendererTest {

    public static final Long ID = 1L;
    public static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    private StoppedTaskListRenderer renderer = new StoppedTaskListRenderer();

    private CaseData.CaseDataBuilder caseDataBuilder;


    @BeforeEach
    public void setup() {

        caseDataBuilder = CaseData.builder()
                .grantStoppedDate(LocalDate.of(2020,1,1));
        ReflectionTestUtils.setField(renderer, "grandDelayNumberOfWeeks", "16");
    }

    @Test
    void shouldRenderStoppedCaseProgressHtmlCorrectly() {
        final CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState(BO_CASE_STOPPED.getId());
        final String expectedHtml = "<div class=\"width-50\">\n\n"
            + "<h2 class=\"govuk-heading-l\">Case progress</h2>\n\n"
            + "<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">Case stopped</div>\n\n"
            + "<h2 class=\"govuk-heading-l\">What happens next</h2>\n\n"
            + "<p class=\"govuk-body-s\">The case was stopped on 01 Jan 2020 for one of two reasons:</p>\n"
            + "<ul class=\"govuk-list govuk-list--bullet\">\n"
            + "<li>an internal review is needed</li>\n"
            + "<li>further information from the applicant or Probate practitioner is needed</li>\n"
            + "</ul>\n\n"
            + "<p class=\"govuk-body-s\">You will be notified by email if we need any information from you to "
            + "progress the case.</p>\n"
            + "<p class=\"govuk-body-s\">You'll usually get the grant within 16 weeks. It can take longer if you need "
            + "to provide additional information.</p>\n"
            + "<p class=\"govuk-body-s\">You don't need to do anything else now, we'll email you if we need more "
            + "information</p>\n\n"
            + "<h2 class=\"govuk-heading-l\">Get help with your application</h2>\n\n"
            + "<h3 class=\"govuk-heading-m\">Telephone</h3>\n\n"
            + "<p class=\"govuk-body-s\">You will need the case reference or the deceased's full name when you call."
            + "</p><br/>"
            + "<p class=\"govuk-body-s\">Telephone: 0300 303 0648</p><p class=\"govuk-body-s\">"
            + "Monday to Friday, 8am to 6pm. Closed on Saturdays, Sundays and bank holidays</p><br/>"
            + "<p class=\"govuk-body-s\">Welsh language: 0300 303 0654</p><p class=\"govuk-body-s\">"
            + "Monday to Friday, 8am to 5pm (except public holidays)</p><br/>\n\n"
            + "<a href=\"https://www.gov.uk/call-charges\" target=\"_blank\" rel=\"noopener noreferrer\" "
            + "class=\"govuk-link\">"
            + "Find out about call charges</a>\n\n"
            + "<h3 class=\"govuk-heading-m\">Email</h3>\n\n"
            + "<a href=\"mailto:contactprobate@justice.gov.uk\" class=\"govuk-link\">contactprobate@justice.gov.uk</a>"
            + "<p class=\"govuk-body-s\">We aim to respond within 10 working days</p>\n\n"
            + "</div>";
        final String result = renderer.renderHtml(caseDetails);

        assertEquals(expectedHtml, result);
    }
}
