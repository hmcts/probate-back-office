package uk.gov.hmcts.probate.service.taskList;

import org.junit.Test;
import uk.gov.hmcts.probate.service.tasklist.*;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.probate.model.ApplicationState.BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_CREATED;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_STOPPED_AWAIT_REDEC;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_STOPPED_REISSUE;
import static uk.gov.hmcts.probate.model.ApplicationState.EXAMINING;
import static uk.gov.hmcts.probate.model.ApplicationState.REGISTRAR_ESCALATION;
import static uk.gov.hmcts.probate.model.ApplicationState.STOPPED;


public class TaskListRendererFactoryTest {

    private BaseTaskListRenderer renderer;
    private TaskListRendererFactory taskListRendererFactory = new TaskListRendererFactory();

    @Test
    public void shouldReturnCorrectRendererForState_CaseCreated() {
        renderer = taskListRendererFactory.getTaskListRenderer(CASE_CREATED.getId());
        assertEquals(DefaultTaskListRenderer.class, renderer.getClass());
    }

    @Test
    public void shouldReturnCorrectRendererForState_BOExamining() {
        renderer = taskListRendererFactory.getTaskListRenderer(EXAMINING.getId());
        assertEquals(DefaultTaskListRenderer.class, renderer.getClass());
    }

    @Test
    public void shouldReturnCorrectRendererForState_BOCaseStopped() {
        renderer = taskListRendererFactory.getTaskListRenderer(BO_CASE_STOPPED.getId());
        assertEquals(StoppedTaskListRenderer.class, renderer.getClass());
    }
    @Test
    public void shouldReturnCorrectRendererForState_BOCaseStoppedReissue() {
        renderer = taskListRendererFactory.getTaskListRenderer(CASE_STOPPED_REISSUE.getId());
        assertEquals(StoppedTaskListRenderer.class, renderer.getClass());
    }

    @Test
    public void shouldReturnCorrectRendererForState_BOCaseStoppedAwaitRedec() {
        renderer = taskListRendererFactory.getTaskListRenderer(CASE_STOPPED_AWAIT_REDEC.getId());
        assertEquals(StoppedTaskListRenderer.class, renderer.getClass());
    }

    @Test
    public void shouldReturnCorrectRendererForState_BORegistrarEscalation() {
        renderer = taskListRendererFactory.getTaskListRenderer(REGISTRAR_ESCALATION.getId());
        assertEquals(EscalatedTaskListRenderer.class, renderer.getClass());
    }

    @Test
    public void shouldReturnCorrectRendererForState_Stopped() {
        renderer = taskListRendererFactory.getTaskListRenderer(STOPPED.getId());
        assertEquals(AppStoppedTaskListRenderer.class, renderer.getClass());
    }
}
