package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.notification.AutomatedNotificationService;
import uk.gov.hmcts.probate.service.notification.DeclarationNotSignedNotification;
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
import static uk.gov.hmcts.probate.model.NotificationType.DECLARATION_NOT_SIGNED;
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

    @Mock
    private DeclarationNotSignedNotification declarationNotSignedNotification;

    private SendNotificationsTask underTest;

    private static final int FIRST_STOP_REMINDER_NOTIFICATION_DAYS = 56;
    private static final int SECOND_STOP_REMINDER_NOTIFICATION_DAYS = 28;
    private static final int HSE_REMINDER_NOTIFICATION_DAYS = 30;
    private static final int DORMANT_WARNING_NOTIFICATION_DAYS = 150;
    private static final int UNSUBMITTED_APPLICATION_NOTIFICATION_DAYS = 28;
    private static final int DECLARATION_NOT_SIGNED_NOTIFICATION_DAYS = 3;

    private static final String AD_HOC_DATE = "2022-09-05";
    private static final String FIRST_STOP_REMINDER_DATE =
            LocalDate.parse(AD_HOC_DATE).minusDays(FIRST_STOP_REMINDER_NOTIFICATION_DAYS).toString(); // 2022-07-11
    private static final String SECOND_STOP_REMINDER_DATE =
            LocalDate.parse(AD_HOC_DATE).minusDays(SECOND_STOP_REMINDER_NOTIFICATION_DAYS).toString(); // 2022-08-08
    private static final String HSE_REMINDER_DATE =
            LocalDate.parse(AD_HOC_DATE).minusDays(HSE_REMINDER_NOTIFICATION_DAYS).toString(); // 2022-08-06
    private static final String DORMANT_WARNING_DATE =
            LocalDate.parse(AD_HOC_DATE).minusDays(DORMANT_WARNING_NOTIFICATION_DAYS).toString(); //2022-04-08
    private static final String UNSUBMITTED_APPLICATION_DATE =
            LocalDate.parse(AD_HOC_DATE).minusDays(UNSUBMITTED_APPLICATION_NOTIFICATION_DAYS).toString(); // 2022-08-08
    private static final String DECLARATION_NOT_SIGNED_DATE =
            LocalDate.parse(AD_HOC_DATE).minusDays(DECLARATION_NOT_SIGNED_NOTIFICATION_DAYS).toString(); // 2022-09-02
    private static final LocalDate FIXED_DATE = LocalDate.of(2025, 5, 19);
    private static final String DEFAULT_FIRST_DATE =
            DATE_FORMAT.format(FIXED_DATE.minusDays(FIRST_STOP_REMINDER_NOTIFICATION_DAYS)); //2025-03-24
    private static final String DEFAULT_SECOND_DATE =
            DATE_FORMAT.format(FIXED_DATE.minusDays(SECOND_STOP_REMINDER_NOTIFICATION_DAYS)); //2025-04-21
    private static final String DEFAULT_HSE_DATE =
            DATE_FORMAT.format(FIXED_DATE.minusDays(HSE_REMINDER_NOTIFICATION_DAYS)); //2025-04-19
    private static final String DEFAULT_DORMANT_WARNING_DATE =
            DATE_FORMAT.format(FIXED_DATE.minusDays(DORMANT_WARNING_NOTIFICATION_DAYS)); //2024-12-20
    private static final String DEFAULT_UNSUBMITTED_APPLICATION_DATE =
            DATE_FORMAT.format(FIXED_DATE.minusDays(UNSUBMITTED_APPLICATION_NOTIFICATION_DAYS)); //2025-04-21
    private static final String DEFAULT_DECLARATION_NOT_SIGNED_DATE =
            DATE_FORMAT.format(FIXED_DATE.minusDays(DECLARATION_NOT_SIGNED_NOTIFICATION_DAYS)); //2025-05-16


    @BeforeEach
    void setUp() {
        clock = Clock.fixed(
                Instant.parse("2025-05-19T02:00:00Z"),
                ZoneId.of("UTC")
        );

        underTest = new SendNotificationsTask(dataExtractDateValidator,
                automatedNotificationService,
                featureToggleService,
                clock,
                firstStopReminderNotification,
                secondStopReminderNotification,
                hseReminderNotification,
                dormantWarningNotification,
                unsubmittedApplicationNotification,
                declarationNotSignedNotification,
                FIRST_STOP_REMINDER_NOTIFICATION_DAYS,
                SECOND_STOP_REMINDER_NOTIFICATION_DAYS,
                HSE_REMINDER_NOTIFICATION_DAYS,
                DORMANT_WARNING_NOTIFICATION_DAYS,
                UNSUBMITTED_APPLICATION_NOTIFICATION_DAYS,
                DECLARATION_NOT_SIGNED_NOTIFICATION_DAYS,
                AD_HOC_DATE);

        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isHseReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isDormantWarningFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isUnsubmittedApplicationFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isDeclarationNotSignedFeatureToggleOn()).thenReturn(true);
    }

    @Test
    void shouldSendAllRemindersWithAdhocDate() {
        ReflectionTestUtils.setField(underTest, "adHocJobDate", AD_HOC_DATE);

        underTest.run();

        verify(dataExtractDateValidator).dateValidator(AD_HOC_DATE);
        verify(automatedNotificationService).sendNotification(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(SECOND_STOP_REMINDER_DATE, SECOND_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(HSE_REMINDER_DATE, HSE_REMINDER);
        verify(automatedNotificationService).sendNotification(DORMANT_WARNING_DATE, DORMANT_WARNING);
        verify(automatedNotificationService).sendNotification(DECLARATION_NOT_SIGNED_DATE, DECLARATION_NOT_SIGNED);
    }

    @Test
    void shouldSendAllRemindersWithDefaultDateWhenNoAdhocDate() {
        ReflectionTestUtils.setField(underTest, "adHocJobDate", null);

        underTest.run();

        verify(automatedNotificationService).sendNotification(DEFAULT_FIRST_DATE, FIRST_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(DEFAULT_SECOND_DATE, SECOND_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(DEFAULT_HSE_DATE, HSE_REMINDER);
        verify(automatedNotificationService).sendNotification(DEFAULT_DORMANT_WARNING_DATE, DORMANT_WARNING);
        verify(automatedNotificationService)
                .sendNotification(DEFAULT_UNSUBMITTED_APPLICATION_DATE, UNSUBMITTED_APPLICATION);
        verify(automatedNotificationService)
                .sendNotification(DEFAULT_DECLARATION_NOT_SIGNED_DATE, DECLARATION_NOT_SIGNED);
    }

    @Test
    void shouldSkipFirstReminderWhenToggleOff() {
        ReflectionTestUtils.setField(underTest, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(false);

        underTest.run();

        verify(dataExtractDateValidator).dateValidator(AD_HOC_DATE);
        verify(automatedNotificationService).sendNotification(SECOND_STOP_REMINDER_DATE, SECOND_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(HSE_REMINDER_DATE, HSE_REMINDER);
        verify(automatedNotificationService).sendNotification(DORMANT_WARNING_DATE, DORMANT_WARNING);
        verify(automatedNotificationService).sendNotification(UNSUBMITTED_APPLICATION_DATE, UNSUBMITTED_APPLICATION);
        verify(automatedNotificationService).sendNotification(DECLARATION_NOT_SIGNED_DATE, DECLARATION_NOT_SIGNED);
        verifyNoMoreInteractions(automatedNotificationService);
    }

    @Test
    void shouldSkipSecondReminderWhenToggleOff() {
        ReflectionTestUtils.setField(underTest, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isHseReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isDormantWarningFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isUnsubmittedApplicationFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isDeclarationNotSignedFeatureToggleOn()).thenReturn(false);

        underTest.run();

        verify(dataExtractDateValidator).dateValidator(FIRST_STOP_REMINDER_DATE);
        verify(automatedNotificationService).sendNotification(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER);
        verifyNoMoreInteractions(automatedNotificationService);
    }

    @Test
    void shouldNotSendAnyReminderWhenAllTogglesOff() {
        ReflectionTestUtils.setField(underTest, "adHocJobDate", null);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isHseReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isDormantWarningFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isUnsubmittedApplicationFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isDeclarationNotSignedFeatureToggleOn()).thenReturn(false);

        underTest.run();

        verifyNoInteractions(dataExtractDateValidator);
        verifyNoInteractions(automatedNotificationService);
    }

    @Test
    void shouldOnlyHSEReminder() {
        ReflectionTestUtils.setField(underTest, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isHseReminderFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isDormantWarningFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isUnsubmittedApplicationFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isDeclarationNotSignedFeatureToggleOn()).thenReturn(false);

        underTest.run();

        verify(dataExtractDateValidator).dateValidator(HSE_REMINDER_DATE);
        verify(automatedNotificationService).sendNotification(HSE_REMINDER_DATE, HSE_REMINDER);
        verifyNoMoreInteractions(automatedNotificationService);
    }

    @Test
    void shouldOnlyUnsubmittedApplicationReminder() {
        ReflectionTestUtils.setField(underTest, "adHocJobDate", AD_HOC_DATE);
        when(featureToggleService.isFirstStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isSecondStopReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isHseReminderFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isDormantWarningFeatureToggleOn()).thenReturn(false);
        when(featureToggleService.isUnsubmittedApplicationFeatureToggleOn()).thenReturn(true);
        when(featureToggleService.isDeclarationNotSignedFeatureToggleOn()).thenReturn(false);

        underTest.run();

        verify(dataExtractDateValidator).dateValidator(UNSUBMITTED_APPLICATION_DATE);
        verify(automatedNotificationService).sendNotification(UNSUBMITTED_APPLICATION_DATE, UNSUBMITTED_APPLICATION);
        verifyNoMoreInteractions(automatedNotificationService);
    }

    @Test
    void shouldAbortWhenValidationFails() {
        ReflectionTestUtils.setField(underTest, "adHocJobDate", AD_HOC_DATE);
        doThrow(new ClientException(400, "bad request"))
                .when(dataExtractDateValidator).dateValidator(AD_HOC_DATE);

        assertThrows(ClientException.class, () -> underTest.run());
        verifyNoInteractions(automatedNotificationService);
    }

    @Test
    void shouldContinueOtherRemindersWhenFirstApiClientException() {
        ReflectionTestUtils.setField(underTest, "adHocJobDate", AD_HOC_DATE);
        doThrow(new RuntimeException("error"))
                .when(automatedNotificationService).sendNotification(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER);

        underTest.run();

        verify(dataExtractDateValidator).dateValidator(AD_HOC_DATE);
        verify(automatedNotificationService).sendNotification(FIRST_STOP_REMINDER_DATE, FIRST_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(SECOND_STOP_REMINDER_DATE, SECOND_STOP_REMINDER);
        verify(automatedNotificationService).sendNotification(HSE_REMINDER_DATE, HSE_REMINDER);
        verify(automatedNotificationService).sendNotification(DORMANT_WARNING_DATE, DORMANT_WARNING);
        verify(automatedNotificationService).sendNotification(UNSUBMITTED_APPLICATION_DATE, UNSUBMITTED_APPLICATION);
        verify(automatedNotificationService).sendNotification(DECLARATION_NOT_SIGNED_DATE, DECLARATION_NOT_SIGNED);
    }
}
