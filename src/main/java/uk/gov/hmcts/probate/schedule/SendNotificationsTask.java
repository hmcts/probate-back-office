package uk.gov.hmcts.probate.schedule;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.notification.AutomatedNotificationService;
import uk.gov.hmcts.probate.service.notification.DeclarationNotSignedNotification;
import uk.gov.hmcts.probate.service.notification.DormantReminderNotification;
import uk.gov.hmcts.probate.service.notification.DormantWarningNotification;
import uk.gov.hmcts.probate.service.notification.FirstStopReminderNotification;
import uk.gov.hmcts.probate.service.notification.HseReminderNotification;
import uk.gov.hmcts.probate.service.notification.SecondStopReminderNotification;
import uk.gov.hmcts.probate.service.notification.UnsubmittedApplicationNotification;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.Clock;
import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;
import static uk.gov.hmcts.probate.model.NotificationType.DECLARATION_NOT_SIGNED;
import static uk.gov.hmcts.probate.model.NotificationType.DORMANT_REMINDER;
import static uk.gov.hmcts.probate.model.NotificationType.DORMANT_WARNING;
import static uk.gov.hmcts.probate.model.NotificationType.FIRST_STOP_REMINDER;
import static uk.gov.hmcts.probate.model.NotificationType.HSE_REMINDER;
import static uk.gov.hmcts.probate.model.NotificationType.SECOND_STOP_REMINDER;
import static uk.gov.hmcts.probate.model.NotificationType.UNSUBMITTED_APPLICATION;

@Component
@Slf4j
public class SendNotificationsTask implements Runnable {
    private final DataExtractDateValidator dataExtractDateValidator;
    private final AutomatedNotificationService automatedNotificationService;
    private final FeatureToggleService featureToggleService;
    private final Clock clock;
    private final FirstStopReminderNotification firstStopReminderNotification;
    private final SecondStopReminderNotification secondStopReminderNotification;
    private final HseReminderNotification hseReminderNotification;
    private final DormantWarningNotification dormantWarningNotification;
    private final DormantReminderNotification dormantReminderNotification;
    private final UnsubmittedApplicationNotification unsubmittedApplicationNotification;
    private final DeclarationNotSignedNotification declarationNotSignedNotification;
    private final int firstNotificationDays;
    private final int secondNotificationDays;
    private final int hseYesNotificationDays;
    private final int dormantWarningDays;
    private final int dormantReminderDays;
    private final int unsubmittedApplicationDays;
    private final int declarationNotSignedDays;
    public final String adHocJobDate;

    public SendNotificationsTask(
            DataExtractDateValidator dataExtractDateValidator,
            AutomatedNotificationService automatedNotificationService,
            FeatureToggleService featureToggleService,
            Clock clock,
            FirstStopReminderNotification firstStopReminderNotification,
            SecondStopReminderNotification secondStopReminderNotification,
            HseReminderNotification hseReminderNotification,
            DormantWarningNotification dormantWarningNotification,
            DormantReminderNotification dormantReminderNotification,
            UnsubmittedApplicationNotification unsubmittedApplicationNotification,
            DeclarationNotSignedNotification declarationNotSignedNotification,
            @Value("${automated_notification.stop_reminder.first_notification_days}") int firstNotificationDays,
            @Value("${automated_notification.stop_reminder.second_notification_days}") int secondNotificationDays,
            @Value("${automated_notification.hse_reminder.awaiting_documentation_days}") int hseYesNotificationDays,
            @Value("${automated_notification.dormant_warning_days}") int dormantWarningDays,
            @Value("${automated_notification.dormant_reminder_days}") int dormantReminderDays,
            @Value("${automated_notification.unsubmitted_application_days}") int unsubmittedApplicationDays,
            @Value("${automated_notification.declaration_not_signed_days}") int declarationNotSignedDays,
            @Value("${adhocSchedulerJobDate}") String adHocJobDate) {
        this.dataExtractDateValidator = dataExtractDateValidator;
        this.automatedNotificationService = automatedNotificationService;
        this.featureToggleService = featureToggleService;
        this.clock = clock;
        this.firstStopReminderNotification = firstStopReminderNotification;
        this.secondStopReminderNotification = secondStopReminderNotification;
        this.hseReminderNotification = hseReminderNotification;
        this.dormantWarningNotification = dormantWarningNotification;
        this.dormantReminderNotification = dormantReminderNotification;
        this.unsubmittedApplicationNotification = unsubmittedApplicationNotification;
        this.declarationNotSignedNotification = declarationNotSignedNotification;
        this.firstNotificationDays = firstNotificationDays;
        this.secondNotificationDays = secondNotificationDays;
        this.hseYesNotificationDays = hseYesNotificationDays;
        this.dormantWarningDays = dormantWarningDays;
        this.dormantReminderDays = dormantReminderDays;
        this.unsubmittedApplicationDays = unsubmittedApplicationDays;
        this.declarationNotSignedDays = declarationNotSignedDays;
        this.adHocJobDate = adHocJobDate;
    }

    @Override
    public void run() {
        log.info("Scheduled task SendNotificationsTask started");
        String firstStopReminderDate = DATE_FORMAT.format(LocalDate.now(clock).minusDays(firstNotificationDays));
        String secondStopReminderDate = DATE_FORMAT.format(LocalDate.now(clock).minusDays(secondNotificationDays));
        String hseReminderDate = DATE_FORMAT.format(LocalDate.now(clock).minusDays(hseYesNotificationDays));
        String dormantWarningDate = DATE_FORMAT.format(LocalDate.now(clock).minusDays(dormantWarningDays));
        String dormantReminderDate = DATE_FORMAT.format(LocalDate.now(clock).minusDays(dormantReminderDays));
        String unsubmittedApplicationDate =
                DATE_FORMAT.format(LocalDate.now(clock).minusDays(unsubmittedApplicationDays));
        String declarationNotSignedDate =
                DATE_FORMAT.format(LocalDate.now(clock).minusDays(declarationNotSignedDays));
        if (StringUtils.isNotEmpty(adHocJobDate)) {
            log.info("Running SendNotificationsTask with Adhoc dates {}", adHocJobDate);
            dataExtractDateValidator.dateValidator(adHocJobDate);
            firstStopReminderDate = LocalDate.parse(adHocJobDate).minusDays(firstNotificationDays).toString();
            secondStopReminderDate = LocalDate.parse(adHocJobDate).minusDays(secondNotificationDays).toString();
            hseReminderDate = LocalDate.parse(adHocJobDate).minusDays(hseYesNotificationDays).toString();
            dormantWarningDate = LocalDate.parse(adHocJobDate).minusDays(dormantWarningDays).toString();
            dormantReminderDate = LocalDate.parse(adHocJobDate).minusDays(dormantReminderDays).toString();
            unsubmittedApplicationDate =
                    LocalDate.parse(adHocJobDate).minusDays(unsubmittedApplicationDays).toString();
            declarationNotSignedDate =
                    LocalDate.parse(adHocJobDate).minusDays(declarationNotSignedDays).toString();
        }

        try {
            if (!featureToggleService.isFirstStopReminderFeatureToggleOn()) {
                log.info("Feature toggle FirstStopReminderFeatureToggle is off, skipping task");
            } else {
                log.info("Calling Send First Stop Reminder for date {}", firstStopReminderDate);
                dataExtractDateValidator.dateValidator(firstStopReminderDate);
                firstStopReminderNotification.setReferenceDate(LocalDate.parse(firstStopReminderDate));
                log.info("Perform Send First Stop Reminder started");
                automatedNotificationService.sendNotification(firstStopReminderDate, FIRST_STOP_REMINDER);
                log.info("Perform Send First Stop Reminder finished");
            }
        } catch (ApiClientException e) {
            log.error("API client exception during Send First Stop Reminder", e);
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler Send First Stop Reminder task", e);
        }

        try {
            if (!featureToggleService.isSecondStopReminderFeatureToggleOn()) {
                log.info("Feature toggle SecondStopReminderFeatureToggle is off, skipping task");
            } else {
                log.info("Calling Send Second Stop Reminder for date {}", secondStopReminderDate);
                dataExtractDateValidator.dateValidator(secondStopReminderDate);
                secondStopReminderNotification.setReferenceDate(LocalDate.parse(secondStopReminderDate));
                log.info("Perform Send Second Stop Reminder started");
                automatedNotificationService.sendNotification(secondStopReminderDate, SECOND_STOP_REMINDER);
                log.info("Perform Send Second Stop Reminder finished");
            }
        } catch (ApiClientException e) {
            log.error("API client exception during Send Second Stop Reminder", e);
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler Send Second Stop Reminder task ", e);
        }

        try {
            if (!featureToggleService.isHseReminderFeatureToggleOn()) {
                log.info("Feature toggle HseReminderFeatureToggle is off, skipping task");
            } else {
                log.info("Calling Send HSE YES notification for date {}", hseReminderDate);
                dataExtractDateValidator.dateValidator(hseReminderDate);
                hseReminderNotification.setReferenceDate(LocalDate.parse(hseReminderDate));
                log.info("Perform Send HSE YES notification started");
                automatedNotificationService.sendNotification(hseReminderDate, HSE_REMINDER);
                log.info("Perform Send HSE YES notification finished");
            }
        } catch (ApiClientException e) {
            log.error("API client exception during Send HSE Reminder", e);
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler Send HSE Reminder task ", e);
        }

        try {
            if (!featureToggleService.isDormantWarningFeatureToggleOn()) {
                log.info("Feature toggle DormantWarningFeatureToggle is off, skipping task");
            } else {
                log.info("Calling Send Dormant Warning for date {}", dormantWarningDate);
                dataExtractDateValidator.dateValidator(dormantWarningDate);
                dormantWarningNotification.setReferenceDate(LocalDate.parse(dormantWarningDate));
                log.info("Perform Send Dormant Warning started");
                automatedNotificationService.sendNotification(dormantWarningDate, DORMANT_WARNING);
                log.info("Perform Send Dormant Warning finished");
            }
        } catch (ApiClientException e) {
            log.error("API client exception during Send Dormant Warning", e);
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler Send Dormant Warning task ", e);
        }

        try {
            if (!featureToggleService.isUnsubmittedApplicationFeatureToggleOn()) {
                log.info("Feature toggle UnsubmittedApplicationFeatureToggle is off, skipping task");
            } else {
                log.info("Calling Send Unsubmitted Application Reminder for date {}", unsubmittedApplicationDate);
                dataExtractDateValidator.dateValidator(unsubmittedApplicationDate);
                unsubmittedApplicationNotification.setReferenceDate(LocalDate.parse(unsubmittedApplicationDate));
                log.info("Perform Send Unsubmitted Application Reminder started");
                automatedNotificationService.sendNotification(unsubmittedApplicationDate, UNSUBMITTED_APPLICATION);
                log.info("Perform Send Unsubmitted Application Reminder finished");
            }
        } catch (ApiClientException e) {
            log.error("API client exception during Send Unsubmitted Application Reminder", e);
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler Send Unsubmitted Application Reminder task ", e);
        }

        try {
            if (!featureToggleService.isDeclarationNotSignedFeatureToggleOn()) {
                log.info("Feature toggle DeclarationNotSignedFeatureToggle is off, skipping task");
            } else {
                log.info("Calling Send Declaration Not Signed Reminder for date {}", declarationNotSignedDate);
                dataExtractDateValidator.dateValidator(declarationNotSignedDate);
                declarationNotSignedNotification.setReferenceDate(LocalDate.parse(declarationNotSignedDate));
                log.info("Perform Send Declaration Not Signed Reminder started");
                automatedNotificationService.sendNotification(declarationNotSignedDate, DECLARATION_NOT_SIGNED);
                log.info("Perform Send Declaration Not Signed Reminder finished");
            }
        } catch (ApiClientException e) {
            log.error("API client exception during Send Declaration Not Signed Reminder", e);
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler Send Declaration Not Signed Reminder task ", e);
        }

        try {
            if (!featureToggleService.isDormantReminderFeatureToggleOn()) {
                log.info("Feature toggle DormantReminderFeatureToggle is off, skipping task");
            } else {
                log.info("Calling Send Dormant Reminder for date {}", dormantReminderDate);
                dataExtractDateValidator.dateValidator(dormantReminderDate);
                dormantReminderNotification.setReferenceDate(LocalDate.parse(dormantReminderDate));
                log.info("Perform Send Dormant Reminder started");
                automatedNotificationService.sendNotification(dormantReminderDate, DORMANT_REMINDER);
                log.info("Perform Send Dormant Reminder finished");
            }
        } catch (ApiClientException e) {
            log.error("API client exception during Send Dormant Warning", e);
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler Send Dormant Warning task ", e);
        }

    }
}