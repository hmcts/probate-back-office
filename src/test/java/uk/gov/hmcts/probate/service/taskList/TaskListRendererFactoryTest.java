package uk.gov.hmcts.probate.service.taskList;

import org.junit.Test;
import uk.gov.hmcts.probate.service.tasklist.BaseTaskListRenderer;
import uk.gov.hmcts.probate.service.tasklist.DefaultTaskListRenderer;
import uk.gov.hmcts.probate.service.tasklist.EscalatedTaskListRenderer;
import uk.gov.hmcts.probate.service.tasklist.StoppedTaskListRenderer;
import uk.gov.hmcts.probate.service.tasklist.TaskListRendererFactory;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.probate.model.ApplicationState.BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_CREATED;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_STOPPED_AWAIT_REDEC;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_STOPPED_REISSUE;
import static uk.gov.hmcts.probate.model.ApplicationState.EXAMINING;
import static uk.gov.hmcts.probate.model.ApplicationState.REGISTRAR_ESCALATION;


public class TaskListRendererFactoryTest {

    private BaseTaskListRenderer renderer;
    private TaskListRendererFactory taskListRendererFactory = new TaskListRendererFactory();

    @Test
    public void shouldReturnCorrectRenderForState_CaseCreated() {
        renderer = taskListRendererFactory.getTaskListRenderer(CASE_CREATED.getId());
        assertEquals(DefaultTaskListRenderer.class, renderer.getClass());
    }

    @Test
    public void shouldReturnCorrectRenderForState_BOExamining() {
        renderer = taskListRendererFactory.getTaskListRenderer(EXAMINING.getId());
        assertEquals(DefaultTaskListRenderer.class, renderer.getClass());
    }

    @Test
    public void shouldReturnCorrectRenderForState_BOCaseStopped() {
        renderer = taskListRendererFactory.getTaskListRenderer(BO_CASE_STOPPED.getId());
        assertEquals(StoppedTaskListRenderer.class, renderer.getClass());
    }
    @Test
    public void shouldReturnCorrectRenderForState_BOCaseStoppedReissue() {
        renderer = taskListRendererFactory.getTaskListRenderer(CASE_STOPPED_REISSUE.getId());
        assertEquals(StoppedTaskListRenderer.class, renderer.getClass());
    }

    @Test
    public void shouldReturnCorrectRenderForState_BOCaseStoppedAwaitRedec() {
        renderer = taskListRendererFactory.getTaskListRenderer(CASE_STOPPED_AWAIT_REDEC.getId());
        assertEquals(StoppedTaskListRenderer.class, renderer.getClass());
    }

    @Test
    public void shouldReturnCorrectRenderForState_BORegistrarEscalation() {
        renderer = taskListRendererFactory.getTaskListRenderer(REGISTRAR_ESCALATION.getId());
        assertEquals(EscalatedTaskListRenderer.class, renderer.getClass());
    }
}
