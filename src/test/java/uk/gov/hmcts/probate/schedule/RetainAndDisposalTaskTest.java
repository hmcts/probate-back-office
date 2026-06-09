package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.service.RetainAndDisposalService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class RetainAndDisposalTaskTest {

    @Mock
    private RetainAndDisposalService retainAndDisposalService;

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @InjectMocks
    private RetainAndDisposalTask retainAndDisposalTask;

    private static final String SWITCH_DATE = "2024-01-01";
    private static final String START_DATE = "2023-01-01";
    private static final String INACTIVITY_NOTIFICATION_PERIOD = "90";
    private static final String DISPOSAL_GRACE_PERIOD = "30";
    private static final String ADHOC_JOB_DATE = "2024-02-15";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(retainAndDisposalTask, "switchDate", SWITCH_DATE);
        ReflectionTestUtils.setField(retainAndDisposalTask, "startDate", START_DATE);
        ReflectionTestUtils.setField(retainAndDisposalTask, "inactivityNotificationPeriod",
                INACTIVITY_NOTIFICATION_PERIOD);
        ReflectionTestUtils.setField(retainAndDisposalTask, "disposalGracePeriod", DISPOSAL_GRACE_PERIOD);
    }

    @Test
    void shouldExecuteTaskWithCurrentDateWhenAdHocJobDateIsEmpty() {
        ReflectionTestUtils.setField(retainAndDisposalTask, "adHocJobDate", "");

        retainAndDisposalTask.run();

        String expectedRunDate = LocalDate.now().toString();
        verify(dataExtractDateValidator).dateValidator(SWITCH_DATE, expectedRunDate);
        verify(retainAndDisposalService)
                .sendEmailForInactiveCase(SWITCH_DATE, expectedRunDate, Long.parseLong(INACTIVITY_NOTIFICATION_PERIOD),
                        false);
        verify(retainAndDisposalService).disposeInactiveCase(SWITCH_DATE, expectedRunDate, START_DATE,
                Long.parseLong(INACTIVITY_NOTIFICATION_PERIOD), Long.parseLong(DISPOSAL_GRACE_PERIOD));
    }

    @Test
    void shouldExecuteTaskWithAdHocJobDateWhenAdHocJobDateIsProvided() {
        ReflectionTestUtils.setField(retainAndDisposalTask, "adHocJobDate", ADHOC_JOB_DATE);

        retainAndDisposalTask.run();

        verify(dataExtractDateValidator).dateValidator(SWITCH_DATE, ADHOC_JOB_DATE);
        verify(retainAndDisposalService)
                .sendEmailForInactiveCase(SWITCH_DATE, ADHOC_JOB_DATE, Long.parseLong(INACTIVITY_NOTIFICATION_PERIOD),
                        false);
        verify(retainAndDisposalService).disposeInactiveCase(SWITCH_DATE, ADHOC_JOB_DATE, START_DATE,
                Long.parseLong(INACTIVITY_NOTIFICATION_PERIOD), Long.parseLong(DISPOSAL_GRACE_PERIOD));
    }

    @Test
    void shouldHandleExceptionWhenSendEmailForInactiveCaseThrowsException() {
        doThrow(new RuntimeException("Email failure")).when(retainAndDisposalService)
                .sendEmailForInactiveCase(anyString(), anyString(), anyLong(), anyBoolean());

        assertDoesNotThrow(() -> retainAndDisposalTask.run());
        verify(retainAndDisposalService)
                .disposeInactiveCase(anyString(), anyString(), anyString(), anyLong(), anyLong());
    }

    @Test
    void shouldHandleExceptionWhenDisposeInactiveCaseThrowsException() {
        doThrow(new RuntimeException("Disposal failure")).when(retainAndDisposalService)
                .disposeInactiveCase(anyString(), anyString(), anyString(), anyLong(), anyLong());

        assertDoesNotThrow(() -> retainAndDisposalTask.run());
        verify(retainAndDisposalService, times(2)).sendEmailForInactiveCase(anyString(),
                anyString(), anyLong(), anyBoolean());
    }

    @Test
    void shouldHandleExceptionsGracefullyWhenBothServiceCallsFail() {
        doThrow(new RuntimeException("Email failure")).when(retainAndDisposalService)
                .sendEmailForInactiveCase(anyString(), anyString(), anyLong(), anyBoolean());
        doThrow(new RuntimeException("Disposal failure")).when(retainAndDisposalService)
                .disposeInactiveCase(anyString(), anyString(), anyString(), anyLong(), anyLong());

        assertDoesNotThrow(() -> retainAndDisposalTask.run());

        verify(retainAndDisposalService).sendEmailForInactiveCase(anyString(), anyString(), anyLong(), anyBoolean());
        verify(retainAndDisposalService)
                .disposeInactiveCase(anyString(), anyString(), anyString(), anyLong(), anyLong());
    }

    @Test
    void shouldSkipDisposalTaskWhenRunDateEqualsSwitchDate() {
        ReflectionTestUtils.setField(retainAndDisposalTask, "adHocJobDate", SWITCH_DATE);

        retainAndDisposalTask.run();

        verify(dataExtractDateValidator).dateValidator(SWITCH_DATE, SWITCH_DATE);
        verify(retainAndDisposalService)
                .sendEmailForInactiveCase(SWITCH_DATE, SWITCH_DATE, Long.parseLong(INACTIVITY_NOTIFICATION_PERIOD),
                        false);
        verify(retainAndDisposalService, times(0)).disposeInactiveCase(SWITCH_DATE, ADHOC_JOB_DATE, START_DATE,
                Long.parseLong(INACTIVITY_NOTIFICATION_PERIOD), Long.parseLong(DISPOSAL_GRACE_PERIOD));

    }
}