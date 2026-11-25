package uk.gov.hmcts.probate.service.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ScheduledTaskRunnerTest {

    @Mock
    private ApplicationContext context;

    @Mock
    private Runnable task;

    @InjectMocks
    private ScheduledTaskRunner taskRunner;

    @Test
    void shouldFindTheBean() {
        when(context.getBean("lowerCaseBean")).thenReturn(task);

        taskRunner.run("LowerCaseBean");

        verify(task).run();
    }

    @Test
    void shouldNotFindTheBean() {
        when(context.getBean("missingBean")).thenThrow();

        taskRunner.run("missingBean");

        verifyNoInteractions(task);
    }

}
