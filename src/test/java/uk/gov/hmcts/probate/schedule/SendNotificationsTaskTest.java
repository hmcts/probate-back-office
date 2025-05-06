package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.notification.AutomatedNotificationService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@ExtendWith(SpringExtension.class)
class SendNotificationsTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private AutomatedNotificationService automatedNotificationService;

    @Mock
    private FeatureToggleService featureToggleService;

    @InjectMocks
    private SendNotificationsTask sendNotificationsTask;
    private static final String DATE = DATE_FORMAT.format(LocalDate.now().minusDays(56));
    private static final String FROM_DATE = "2022-09-05";
    private static final String FIRST_STOP_REMINDER_DATE = LocalDate.parse(FROM_DATE).minusDays(56).toString();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sendNotificationsTask, "firstNotificationDays", 56);
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", FROM_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn())
                .thenReturn(true);
    }

    @Test
    void shouldSendFirstReminderWithDate() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform Send Stop Reminder (8-week) finished");
        sendNotificationsTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform Send Stop Reminder (8-week) finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER_DATE);
        verify(automatedNotificationService).sendStopReminder(FIRST_STOP_REMINDER_DATE, true);
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForSendFirstReminderWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER_DATE);
        sendNotificationsTask.run();
        verify(dataExtractDateValidator).dateValidator(FIRST_STOP_REMINDER_DATE,FIRST_STOP_REMINDER_DATE);
        verifyNoInteractions(automatedNotificationService);
    }

    @Test
    void shouldSendFirstReminderWithDefaultPeriodIfNoAdhocJobDate() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", null);
        sendNotificationsTask.run();
        verify(dataExtractDateValidator).dateValidator(DATE, DATE);
        verify(automatedNotificationService).sendStopReminder(DATE, true);
    }

    @Test
    void shouldNotSendFirstReminderWhenToggleOff() {
        when(featureToggleService.isFirstStopReminderFeatureToggleOn())
                .thenReturn(false);
        sendNotificationsTask.run();
        verifyNoInteractions(dataExtractDateValidator);
        verifyNoInteractions(automatedNotificationService);
    }
}
