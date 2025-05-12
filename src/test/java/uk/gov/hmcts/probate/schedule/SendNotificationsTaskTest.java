package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.notification.AutomatedNotificationService;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    private static final String AD_HOC_DATE = "2022-09-05";
    private static final String FIRST_STOP_REMINDER_DATE = LocalDate.parse(AD_HOC_DATE).minusDays(56).toString();
    private static final String SECOND_STOP_REMINDER_DATE = LocalDate.parse(AD_HOC_DATE).minusDays(28).toString();
    private static final String DEFAULT_FIRST_DATE = DATE_FORMAT.format(LocalDate.now().minusDays(56));
    private static final String DEFAULT_SECOND_DATE = DATE_FORMAT.format(LocalDate.now().minusDays(28));

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sendNotificationsTask, "firstNotificationDays", 56);
        ReflectionTestUtils.setField(sendNotificationsTask, "secondNotificationDays", 28);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn())
                .thenReturn(true);
    }

    @Test
    void shouldSendBothRemindersWithAdhocDate() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(true);

        sendNotificationsTask.run();

        verify(dataExtractDateValidator).dateValidator(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER_DATE);
        verify(dataExtractDateValidator).dateValidator(SECOND_STOP_REMINDER_DATE, SECOND_STOP_REMINDER_DATE);
        verify(automatedNotificationService).sendStopReminder(FIRST_STOP_REMINDER_DATE, true);
        verify(automatedNotificationService).sendStopReminder(SECOND_STOP_REMINDER_DATE, false);
    }

    @Test
    void shouldSendBothRemindersWithDefaultDateWhenNoAdhocDate() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", null);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(true);

        sendNotificationsTask.run();

        verify(dataExtractDateValidator).dateValidator(DEFAULT_FIRST_DATE, DEFAULT_FIRST_DATE);
        verify(dataExtractDateValidator).dateValidator(DEFAULT_SECOND_DATE, DEFAULT_SECOND_DATE);
        verify(automatedNotificationService).sendStopReminder(DEFAULT_FIRST_DATE, true);
        verify(automatedNotificationService).sendStopReminder(DEFAULT_SECOND_DATE, false);
    }

    @Test
    void shouldSkipFirstReminderWhenToggleOff() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(true);

        sendNotificationsTask.run();

        verify(dataExtractDateValidator).dateValidator(SECOND_STOP_REMINDER_DATE, SECOND_STOP_REMINDER_DATE);
        verify(automatedNotificationService).sendStopReminder(SECOND_STOP_REMINDER_DATE, false);
        verifyNoMoreInteractions(automatedNotificationService);
    }

    @Test
    void shouldSkipSecondReminderWhenToggleOff() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(false);

        sendNotificationsTask.run();

        verify(dataExtractDateValidator).dateValidator(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER_DATE);
        verify(automatedNotificationService).sendStopReminder(FIRST_STOP_REMINDER_DATE, true);
        verifyNoMoreInteractions(automatedNotificationService);
    }

    @Test
    void shouldNotSendAnyReminderWhenBothTogglesOff() {
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(false);

        sendNotificationsTask.run();

        verifyNoInteractions(dataExtractDateValidator);
        verifyNoInteractions(automatedNotificationService);
    }

    @Test
    void shouldCatchClientExceptionInFirstReminder() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(false);

        doThrow(new ClientException(400, "bad request"))
                .when(dataExtractDateValidator).dateValidator(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER_DATE);

        sendNotificationsTask.run();

        verify(dataExtractDateValidator).dateValidator(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER_DATE);
        verifyNoInteractionsWithAutomatedNotificationService();
    }

    @Test
    void shouldCatchClientExceptionInSecondReminder() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(true);

        doThrow(new ClientException(400, "bad request"))
                .when(dataExtractDateValidator).dateValidator(SECOND_STOP_REMINDER_DATE, SECOND_STOP_REMINDER_DATE);

        sendNotificationsTask.run();

        verify(dataExtractDateValidator).dateValidator(SECOND_STOP_REMINDER_DATE, SECOND_STOP_REMINDER_DATE);
        verifyNoInteractionsWithAutomatedNotificationService();
    }

    private void verifyNoInteractionsWithAutomatedNotificationService() {
        verifyNoInteractions(automatedNotificationService);
    }
}
