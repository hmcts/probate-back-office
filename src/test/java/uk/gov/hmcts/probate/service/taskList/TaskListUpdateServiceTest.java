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

        String expectedCaseProgressCaseCreatedHtml = "";

        ResponseCaseDataBuilder response = taskListUpdateService.generateTaskList(caseDetails, builder);
        ResponseCaseData result = response.build();

        assertEquals(expectedCaseProgressCaseCreatedHtml, result.getTaskList());
    }

    @Test
    public void shouldBuildCaseProgressHtmlCorrectly_BOExamining() {
        caseDetails.setState(EXAMINING.getId());

        String expectedCaseProgressCaseCreatedHtml = "";

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
