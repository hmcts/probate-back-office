package uk.gov.hmcts.probate.service.taskList;

import org.junit.Before;
import org.junit.Test;

import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.tasklist.AppStoppedTaskListRenderer;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.probate.model.ApplicationState.STOPPED;

public class AppStoppedTaskListRendererTest {

    public static final Long ID = 1L;
    public static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    private AppStoppedTaskListRenderer renderer = new AppStoppedTaskListRenderer();

    @Test
    public void shouldRenderStoppedCaseProgressHtmlCorrectly() {
        final CaseData.CaseDataBuilder caseDataBuilder = CaseData.builder();
        final CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState(STOPPED.getId());
        final String expected = "<div class=\"width-50\">\n" +
                "\n" +
                "<h2 class=\"govuk-heading-l\">Case progress</h2>\n" +
                "\n" +
                "<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">Paper application needed</div>\n" +
                "\n" +
                "<h2 class=\"govuk-heading-l\">What to do next</h2>\n" +
                "\n" +
                "<p class=\"govuk-body-s\">This application has been stopped. Based on the information you have entered, our online service cannot yet handle this type of application.<br/>You will need to apply for a grant of representation using a <a href=\"https://www.gov.uk/government/collections/probate-forms\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">paper form</a>.</p>\n" +
                "<p class=\"govuk-body-s\"><a href=\"https://www.gov.uk/guidance/probate-paper-applications-for-legal-professionals\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">Guidance on exemptions, conditions and when applications must be submitted by paper.</a></p>\n" +
                "<p class=\"govuk-body-s\">If having read this guidance you're sure that the online service should handle your application, contact <a href=\"mailto:probatefeedback@justice.co.uk\" class=\"govuk-link\" target=\"_blank\">probatefeedback@justice.co.uk</a> with the broad details of your application and we will help to progress your application.</p>\n" +
                "\n" +
                "\n" +
                "\n" +
                "</div>";
        final String result = renderer.renderHtml(caseDetails);

        assertTrue(expected.equals(result));
    }
}