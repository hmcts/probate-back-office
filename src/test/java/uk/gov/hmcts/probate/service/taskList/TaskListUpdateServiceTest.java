package uk.gov.hmcts.probate.service.taskList;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;
import uk.gov.hmcts.probate.service.tasklist.DefaultTaskListRenderer;
import uk.gov.hmcts.probate.service.tasklist.EscalatedTaskListRenderer;
import uk.gov.hmcts.probate.service.tasklist.StoppedTaskListRenderer;
import uk.gov.hmcts.probate.service.tasklist.TaskListRendererFactory;
import uk.gov.hmcts.probate.service.tasklist.TaskListUpdateService;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationState.BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_CREATED;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_STOPPED_AWAIT_REDEC;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_STOPPED_REISSUE;
import static uk.gov.hmcts.probate.model.ApplicationState.EXAMINING;
import static uk.gov.hmcts.probate.model.ApplicationState.REGISTRAR_ESCALATION;

@Slf4j
public class TaskListUpdateServiceTest {

    @InjectMocks
    private TaskListUpdateService taskListUpdateService;

    @Mock
    private TaskListRendererFactory taskListRendererFactory;

    @Mock
    private DefaultTaskListRenderer defaultTaskListRenderer;

    @Mock
    private StoppedTaskListRenderer stoppedTaskListRenderer;

    @Mock
    private EscalatedTaskListRenderer escalatedTaskListRenderer;

    private CaseData caseData = CaseData.builder()
            .taskList("")
            .escalatedDate(ESCALATED_DATE)
            .grantStoppedDate(STOPPED_DATE)
            .build();

    private CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

    private ResponseCaseDataBuilder builder = new ResponseCaseDataBuilder();

    private final static LocalDate ESCALATED_DATE = LocalDate.of(2020, 1, 1);
    private final static LocalDate STOPPED_DATE = LocalDate.of(2020, 10, 10);
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long ID = 1L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(taskListRendererFactory.getTaskListRenderer(anyString())).thenReturn(defaultTaskListRenderer);

        when(defaultTaskListRenderer.renderHtml(caseDetails)).thenCallRealMethod();
        when(stoppedTaskListRenderer.renderHtml(caseDetails)).thenCallRealMethod();
        when(escalatedTaskListRenderer.renderHtml(caseDetails)).thenCallRealMethod();
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_CaseCreated() {
        caseDetails.setState(CASE_CREATED.getId());

        String expectedCaseProgressCaseCreatedHtml = "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add solicitor details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
                "</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add deceased details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
                "</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add application details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
                "</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<br/>\n" +
                "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Review and sign legal statement and submit application</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
                "</div></div>\n" +
                "<reviewAndSubmitDate/><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">The legal statement is generated. You can review, change any details, then sign and submit your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Send documents<br/><details class=\"govuk-details\" data-module=\"govuk-details\">\n" +
                "  <summary class=\"govuk-details__summary\">\n" +
                "    <span class=\"govuk-details__summary-text\">\n" +
                "      View the documents needed by HM Courts and Tribunal Service\n" +
                "    </span>\n" +
                "  </summary>\n" +
                "  <div class=\"govuk-details__text\">\n" +
                "    You now need to send us<br/><ul><li>your reference number 1 written on a piece of paper</li><li>the stamped (receipted) IHT 421 with this application</li><li>a photocopy of the signed legal statement and declaration</li></ul>\n" +
                "  </div>\n" +
                "</details></p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n" +
                "</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<br/>\n" +
                "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Authenticate documents</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">We will authenticate your documents and match them with your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Examine application</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">We review your application for incomplete information or problems and validate it against other cases or caveats. After the review we prepare the grant.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by our team:</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<ul class=\"govuk-list govuk-list--bullet\">\n" +
                "<li>Examining</li>\n" +
                "<li>Case Matching</li>\n" +
                "<li>Case selected for Quality Assurance</li>\n" +
                "<li>Ready to issue</li>\n" +
                "</ul><hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">This step is completed by HM Courts and Tribunals Service staff.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Issue grant of representation</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">The grant will be delivered in the post a few days after issuing.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n";

        ResponseCaseDataBuilder response = taskListUpdateService.generateTaskList(caseDetails, builder);
        ResponseCaseData result = response.build();

        assertEquals(expectedCaseProgressCaseCreatedHtml, result.getTaskList());
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_BOExamining() {
        caseDetails.setState(EXAMINING.getId());

        String expectedCaseProgressCaseCreatedHtml = "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add solicitor details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
                "</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add deceased details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
                "</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add application details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
                "</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<br/>\n" +
                "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Review and sign legal statement and submit application</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
                "</div></div>\n" +
                "<reviewAndSubmitDate/><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">The legal statement is generated. You can review, change any details, then sign and submit your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Send documents<br/><details class=\"govuk-details\" data-module=\"govuk-details\">\n" +
                "  <summary class=\"govuk-details__summary\">\n" +
                "    <span class=\"govuk-details__summary-text\">\n" +
                "      View the documents needed by HM Courts and Tribunal Service\n" +
                "    </span>\n" +
                "  </summary>\n" +
                "  <div class=\"govuk-details__text\">\n" +
                "    You now need to send us<br/><ul><li>your reference number 1 written on a piece of paper</li><li>the stamped (receipted) IHT 421 with this application</li><li>a photocopy of the signed legal statement and declaration</li></ul>\n" +
                "  </div>\n" +
                "</details></p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
                "</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<br/>\n" +
                "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Authenticate documents</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
                "</div></div>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">We will authenticate your documents and match them with your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Examine application</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n" +
                "</div></div>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">We review your application for incomplete information or problems and validate it against other cases or caveats. After the review we prepare the grant.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by our team:</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<ul class=\"govuk-list govuk-list--bullet\">\n" +
                "<li>Examining</li>\n" +
                "<li>Case Matching</li>\n" +
                "<li>Case selected for Quality Assurance</li>\n" +
                "<li>Ready to issue</li>\n" +
                "</ul><hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">This step is completed by HM Courts and Tribunals Service staff.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Issue grant of representation</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">The grant will be delivered in the post a few days after issuing.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n";

        ResponseCaseDataBuilder response = taskListUpdateService.generateTaskList(caseDetails, builder);
        ResponseCaseData result = response.build();

        assertEquals(expectedCaseProgressCaseCreatedHtml, result.getTaskList());
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_BOCaseStopped() {
        when(taskListRendererFactory.getTaskListRenderer(anyString())).thenReturn(stoppedTaskListRenderer);

        caseDetails.setState(BO_CASE_STOPPED.getId());

        ResponseCaseDataBuilder response = taskListUpdateService.generateTaskList(caseDetails, builder);
        ResponseCaseData result = response.build();

        assertTrue(result.getTaskList().contains("Case stopped"));
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_BOCaseStoppedReissue() {
        when(taskListRendererFactory.getTaskListRenderer(anyString())).thenReturn(stoppedTaskListRenderer);

        caseDetails.setState(CASE_STOPPED_REISSUE.getId());

        ResponseCaseDataBuilder response = taskListUpdateService.generateTaskList(caseDetails, builder);
        ResponseCaseData result = response.build();

        assertTrue(result.getTaskList().contains("Case stopped"));
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_BOCaseStoppedAwaitRedec() {
        when(taskListRendererFactory.getTaskListRenderer(anyString())).thenReturn(stoppedTaskListRenderer);

        caseDetails.setState(CASE_STOPPED_AWAIT_REDEC.getId());

        ResponseCaseDataBuilder response = taskListUpdateService.generateTaskList(caseDetails, builder);
        ResponseCaseData result = response.build();

        assertTrue(result.getTaskList().contains("Case stopped"));
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_BORegistrarEscalation() {
        when(taskListRendererFactory.getTaskListRenderer(anyString())).thenReturn(escalatedTaskListRenderer);

        caseDetails.setState(REGISTRAR_ESCALATION.getId());

        ResponseCaseDataBuilder response = taskListUpdateService.generateTaskList(caseDetails, builder);
        ResponseCaseData result = response.build();

        assertTrue(result.getTaskList().contains("Case escalated to the Registrar"));
    }
}
