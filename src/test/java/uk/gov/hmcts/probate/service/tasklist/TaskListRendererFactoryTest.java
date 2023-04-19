package uk.gov.hmcts.probate.service.tasklist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.ApplicationState.BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_CREATED;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_STOPPED_AWAIT_REDEC;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_STOPPED_REISSUE;
import static uk.gov.hmcts.probate.model.ApplicationState.REGISTRAR_ESCALATION;
import static uk.gov.hmcts.probate.model.ApplicationState.STOPPED;


class TaskListRendererFactoryTest {

    @InjectMocks
    private TaskListRendererFactory taskListRendererFactory;
    @Mock
    private StoppedTaskListRenderer stoppedTaskListRenderer;
    @Mock
    private EscalatedTaskListRenderer escalatedTaskListRenderer;
    @Mock
    private AppStoppedTaskListRenderer appStoppedTaskListRenderer;
    @Mock
    private DefaultTaskListRenderer defaultTaskListRenderer;

    private BaseTaskListRenderer renderer;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldReturnCorrectRendererForState_CaseCreated() {
        renderer = taskListRendererFactory.getTaskListRenderer(CASE_CREATED.getId());
        assertEquals(defaultTaskListRenderer, renderer);
    }

    @Test
    void shouldReturnCorrectRendererForState_BOCaseStopped() {
        renderer = taskListRendererFactory.getTaskListRenderer(BO_CASE_STOPPED.getId());
        assertEquals(stoppedTaskListRenderer, renderer);
    }

    @Test
    void shouldReturnCorrectRendererForState_BOCaseStoppedReissue() {
        renderer = taskListRendererFactory.getTaskListRenderer(CASE_STOPPED_REISSUE.getId());
        assertEquals(stoppedTaskListRenderer, renderer);
    }

    @Test
    void shouldReturnCorrectRendererForState_BOCaseStoppedAwaitRedec() {
        renderer = taskListRendererFactory.getTaskListRenderer(CASE_STOPPED_AWAIT_REDEC.getId());
        assertEquals(stoppedTaskListRenderer, renderer);
    }

    @Test
    void shouldReturnCorrectRendererForState_BORegistrarEscalation() {
        renderer = taskListRendererFactory.getTaskListRenderer(REGISTRAR_ESCALATION.getId());
        assertEquals(escalatedTaskListRenderer, renderer);
    }

    @Test
    void shouldReturnCorrectRendererForState_Stopped() {
        renderer = taskListRendererFactory.getTaskListRenderer(STOPPED.getId());
        assertEquals(appStoppedTaskListRenderer, renderer);
    }
}
