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
    private static final String date = DATE_FORMAT.format(LocalDate.now().minusDays(56));
    private final String fromDate = "2022-09-05";

    @BeforeEach
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(sendNotificationsTask, "firstNotificationDays", 56);
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", fromDate);
        when(featureToggleService.isFeatureToggleOn("probate-cron-first-stop-reminder", false))
                .thenReturn(true);
    }

    @Test
    void shouldSendFirstReminderWithDate() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform Send Stop Reminder (8-week) finished");
        sendNotificationsTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform Send Stop Reminder (8-week) finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(fromDate, fromDate);
        verify(automatedNotificationService).sendFirstStopReminder(fromDate);
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForSendFirstReminderWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(fromDate, fromDate);
        sendNotificationsTask.run();
        verify(dataExtractDateValidator).dateValidator(fromDate,fromDate);
        verifyNoInteractions(automatedNotificationService);
    }

    @Test
    void shouldSendFirstReminderWithDefaultPeriodIfNoAdhocJobDate() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", null);
        sendNotificationsTask.run();
        verify(dataExtractDateValidator).dateValidator(date, date);
        verify(automatedNotificationService).sendFirstStopReminder(date);
    }

    @Test
    void shouldNotSendFirstReminderWhenToggleOff() {
        when(featureToggleService.isFeatureToggleOn("probate-cron-first-stop-reminder", false))
                .thenReturn(false);
        sendNotificationsTask.run();
        verifyNoInteractions(dataExtractDateValidator);
        verifyNoInteractions(automatedNotificationService);
    }
}
