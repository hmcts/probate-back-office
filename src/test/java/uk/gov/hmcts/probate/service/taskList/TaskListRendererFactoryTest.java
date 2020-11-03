package uk.gov.hmcts.probate.service.taskList;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import uk.gov.hmcts.probate.service.tasklist.*;

public class TaskListRendererFactoryTest {
    private TaskListRendererFactory rendererFactory = new TaskListRendererFactory();

    @Test
    public void shouldReturnCorrectRenderForState_CaseCreated() {
        BaseTaskListRenderer renderer = rendererFactory.getTaskListRenderer("CaseCreated", "");
        assertTrue(renderer.getClass() == DefaultTaskListRenderer.class);
    }

    @Test
    public void shouldReturnCorrectRenderForState_BOExamining() {
        BaseTaskListRenderer renderer = rendererFactory.getTaskListRenderer("BOExamining", "");
        assertTrue(renderer.getClass() == DefaultTaskListRenderer.class);
    }

    @Test
    public void shouldReturnCorrectRenderForState_BOCaseStopped() {
        BaseTaskListRenderer renderer = rendererFactory.getTaskListRenderer("BOCaseStopped", "");
        assertTrue(renderer.getClass() == StoppedTaskListRenderer.class);
    }
    @Test
    public void shouldReturnCorrectRenderForState_BOCaseStoppedReissue() {
        BaseTaskListRenderer renderer = rendererFactory.getTaskListRenderer("BOCaseStoppedReissue", "");
        assertTrue(renderer.getClass() == StoppedTaskListRenderer.class);
    }

    @Test
    public void shouldReturnCorrectRenderForState_BOCaseStoppedAwaitRedec() {
        BaseTaskListRenderer renderer = rendererFactory.getTaskListRenderer("BOCaseStoppedAwaitRedec", "");
        assertTrue(renderer.getClass() == StoppedTaskListRenderer.class);
    }

    @Test
    public void shouldReturnCorrectRenderForState_BORegistrarEscalation() {
        BaseTaskListRenderer renderer = rendererFactory.getTaskListRenderer("BORegistrarEscalation", "");
        assertTrue(renderer.getClass() == EscalatedTaskListRenderer.class);
    }
}
