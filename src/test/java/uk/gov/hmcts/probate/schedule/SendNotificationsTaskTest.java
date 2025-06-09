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
import uk.gov.hmcts.probate.service.notification.DormantWarningNotification;
import uk.gov.hmcts.probate.service.notification.FirstStopReminderNotification;
import uk.gov.hmcts.probate.service.notification.HseReminderNotification;
import uk.gov.hmcts.probate.service.notification.SecondStopReminderNotification;
import uk.gov.hmcts.probate.service.notification.UnsubmittedApplicationNotification;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;
import static uk.gov.hmcts.probate.model.NotificationType.DORMANT_WARNING;
import static uk.gov.hmcts.probate.model.NotificationType.FIRST_STOP_REMINDER;
import static uk.gov.hmcts.probate.model.NotificationType.SECOND_STOP_REMINDER;
import static uk.gov.hmcts.probate.model.NotificationType.HSE_REMINDER;
import static uk.gov.hmcts.probate.model.NotificationType.UNSUBMITTED_APPLICATION;

@ExtendWith(SpringExtension.class)
class SendNotificationsTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private AutomatedNotificationService automatedNotificationService;

    @Mock
    private FeatureToggleService featureToggleService;

    @Mock
    private Clock clock;

    @Mock
    private FirstStopReminderNotification firstStopReminderNotification;

    @Mock
    private SecondStopReminderNotification secondStopReminderNotification;

    @Mock
    private HseReminderNotification hseReminderNotification;

    @Mock
    private DormantWarningNotification dormantWarningNotification;

    @Mock
    private UnsubmittedApplicationNotification unsubmittedApplicationNotification;

    @InjectMocks
    private SendNotificationsTask sendNotificationsTask;
    private static final String AD_HOC_DATE = "2022-09-05";
    private static final String FIRST_STOP_REMINDER_DATE = LocalDate.parse(AD_HOC_DATE).minusDays(56).toString();
    private static final String SECOND_STOP_REMINDER_DATE = LocalDate.parse(AD_HOC_DATE).minusDays(28).toString();
    private static final String HSE_REMINDER_DATE = LocalDate.parse(AD_HOC_DATE).minusDays(30).toString();
    private static final String DORMANT_WARNING_DATE = LocalDate.parse(AD_HOC_DATE).minusDays(150).toString();
    private static final String UNSUBMITTED_APPLICATION_DATE = LocalDate.parse(AD_HOC_DATE).minusDays(28).toString();
    private static final LocalDate FIXED_DATE = LocalDate.of(2025, 5, 19);
    private static final String DEFAULT_FIRST_DATE = DATE_FORMAT.format(FIXED_DATE.minusDays(56)); //2025-03-24
    private static final String DEFAULT_SECOND_DATE = DATE_FORMAT.format(FIXED_DATE.minusDays(28)); //2025-04-21
    private static final String DEFAULT_HSE_DATE = DATE_FORMAT.format(FIXED_DATE.minusDays(30)); //2025-04-21
    private static final String DEFAULT_DORMANT_WARNING_DATE = DATE_FORMAT.format(FIXED_DATE.minusDays(150));
    private static final String DEFAULT_UNSUBMITTED_APPLICATION_DATE =
            LocalDate.parse(AD_HOC_DATE).minusDays(28).toString();


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sendNotificationsTask, "firstNotificationDays", 56);
        ReflectionTestUtils.setField(sendNotificationsTask, "secondNotificationDays", 28);
        ReflectionTestUtils.setField(sendNotificationsTask, "hseYesNotificationDays", 30);
        ReflectionTestUtils.setField(sendNotificationsTask, "dormantWarningDays", 150);
        ReflectionTestUtils.setField(sendNotificationsTask, "unsubmittedApplicationDays", 28);
        ReflectionTestUtils.setField(sendNotificationsTask, "clock", clock);
        Instant fixedInstant = FIXED_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isHseReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isDormantWarningFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isUnsubmittedApplicationFeatureToggleOn()).thenReturn(true);
    }

    @Test
    void shouldSendAllRemindersWithAdhocDate() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", AD_HOC_DATE);

        sendNotificationsTask.run();

        verify(dataExtractDateValidator).dateValidator(AD_HOC_DATE);
        verify(automatedNotificationService).sendNotification(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(SECOND_STOP_REMINDER_DATE, SECOND_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(HSE_REMINDER_DATE, HSE_REMINDER);
        verify(automatedNotificationService).sendNotification(DORMANT_WARNING_DATE, DORMANT_WARNING);
    }

    @Test
    void shouldSendAllRemindersWithDefaultDateWhenNoAdhocDate() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", null);

        sendNotificationsTask.run();

        verify(automatedNotificationService).sendNotification(DEFAULT_FIRST_DATE, FIRST_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(DEFAULT_SECOND_DATE, SECOND_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(DEFAULT_HSE_DATE, HSE_REMINDER);
        verify(automatedNotificationService).sendNotification(DEFAULT_DORMANT_WARNING_DATE, DORMANT_WARNING);
    }

    @Test
    void shouldSkipFirstReminderWhenToggleOff() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(false);

        sendNotificationsTask.run();

        verify(dataExtractDateValidator).dateValidator(AD_HOC_DATE);
        verify(automatedNotificationService).sendNotification(SECOND_STOP_REMINDER_DATE, SECOND_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(HSE_REMINDER_DATE, HSE_REMINDER);
        verify(automatedNotificationService).sendNotification(DORMANT_WARNING_DATE, DORMANT_WARNING);
        verify(automatedNotificationService).sendNotification(UNSUBMITTED_APPLICATION_DATE, UNSUBMITTED_APPLICATION);
        verifyNoMoreInteractions(automatedNotificationService);
    }

    @Test
    void shouldSkipSecondReminderWhenToggleOff() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isHseReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isDormantWarningFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isUnsubmittedApplicationFeatureToggleOn()).thenReturn(false);

        sendNotificationsTask.run();

        verify(dataExtractDateValidator).dateValidator(AD_HOC_DATE);
        verify(automatedNotificationService).sendNotification(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER);
        verifyNoMoreInteractions(automatedNotificationService);
        verifyNoMoreInteractions(dataExtractDateValidator);
    }

    @Test
    void shouldNotSendAnyReminderWhenAllTogglesOff() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", null);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isHseReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isDormantWarningFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isUnsubmittedApplicationFeatureToggleOn()).thenReturn(false);

        sendNotificationsTask.run();

        verifyNoInteractions(dataExtractDateValidator);
        verifyNoInteractions(automatedNotificationService);
    }

    @Test
    void shouldOnlyHSEReminder() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isHseReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isDormantWarningFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isUnsubmittedApplicationFeatureToggleOn()).thenReturn(false);

        sendNotificationsTask.run();

        verify(dataExtractDateValidator).dateValidator(AD_HOC_DATE);
        verify(automatedNotificationService).sendNotification(HSE_REMINDER_DATE, HSE_REMINDER);
        verifyNoMoreInteractions(automatedNotificationService);
        verifyNoMoreInteractions(dataExtractDateValidator);
    }

    @Test
    void shouldAbortWhenValidationFails() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", AD_HOC_DATE);
        doThrow(new ClientException(400, "bad request"))
                .when(dataExtractDateValidator).dateValidator(AD_HOC_DATE);

        assertThrows(ClientException.class, () -> sendNotificationsTask.run());
        verifyNoInteractions(automatedNotificationService);
    }

    @Test
    void shouldContinueOtherRemindersWhenFirstApiClientException() {
        ReflectionTestUtils.setField(sendNotificationsTask, "adHocJobDate", AD_HOC_DATE);
        doThrow(new RuntimeException("error"))
                .when(automatedNotificationService).sendNotification(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER);

        sendNotificationsTask.run();

        verify(dataExtractDateValidator).dateValidator(AD_HOC_DATE);
        verify(automatedNotificationService).sendNotification(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(SECOND_STOP_REMINDER_DATE, SECOND_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(HSE_REMINDER_DATE, HSE_REMINDER);
        verify(automatedNotificationService).sendNotification(DORMANT_WARNING_DATE, DORMANT_WARNING);
        verify(automatedNotificationService).sendNotification(UNSUBMITTED_APPLICATION_DATE, UNSUBMITTED_APPLICATION);
    }
}
