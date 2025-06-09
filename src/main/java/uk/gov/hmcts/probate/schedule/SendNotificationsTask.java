package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.notification.AutomatedNotificationService;
import uk.gov.hmcts.probate.service.notification.DormantWarningNotification;
import uk.gov.hmcts.probate.service.notification.FirstStopReminderNotification;
import uk.gov.hmcts.probate.service.notification.HseReminderNotification;
import uk.gov.hmcts.probate.service.notification.SecondStopReminderNotification;
import uk.gov.hmcts.probate.service.notification.UnsubmittedApplicationNotification;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.Clock;
import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;
import static uk.gov.hmcts.probate.model.NotificationType.DORMANT_WARNING;
import static uk.gov.hmcts.probate.model.NotificationType.FIRST_STOP_REMINDER;
import static uk.gov.hmcts.probate.model.NotificationType.SECOND_STOP_REMINDER;
import static uk.gov.hmcts.probate.model.NotificationType.HSE_REMINDER;
import static uk.gov.hmcts.probate.model.NotificationType.UNSUBMITTED_APPLICATION;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendNotificationsTask implements Runnable {
    private final DataExtractDateValidator dataExtractDateValidator;
    private final AutomatedNotificationService automatedNotificationService;
    private final FeatureToggleService featureToggleService;
    private final Clock clock;
    private final FirstStopReminderNotification firstStopReminderNotification;
    private final SecondStopReminderNotification secondStopReminderNotification;
    private final HseReminderNotification hseReminderNotification;
    private final DormantWarningNotification dormantWarningNotification;
    private final UnsubmittedApplicationNotification unsubmittedApplicationNotification;


    @Value("${automated_notification.stop_reminder.first_notification_days}")
    private int firstNotificationDays;

    @Value("${automated_notification.stop_reminder.second_notification_days}")
    private int secondNotificationDays;

    @Value("${automated_notification.hse_reminder.awaiting_documentation_days}")
    private int hseYesNotificationDays;

    @Value("${automated_notification.dormant_warning_days}")
    private int dormantWarningDays;

    @Value("${automated_notification.unsubmitted_application_days}")
    private int unsubmittedApplicationDays;

    @Value("${adhocSchedulerJobDate}")
    public String adHocJobDate;

    @Override
    public void run() {
        log.info("Scheduled task SendNotificationsTask started");
        String firstStopReminderDate = DATE_FORMAT.format(LocalDate.now(clock).minusDays(firstNotificationDays));
        String secondStopReminderDate = DATE_FORMAT.format(LocalDate.now(clock).minusDays(secondNotificationDays));
        String hseReminderDate = DATE_FORMAT.format(LocalDate.now(clock).minusDays(hseYesNotificationDays));
        String dormantWarningDate = DATE_FORMAT.format(LocalDate.now(clock).minusDays(dormantWarningDays));
        String unsubmittedApplicationDate =
                DATE_FORMAT.format(LocalDate.now(clock).minusDays(unsubmittedApplicationDays));
        if (StringUtils.isNotEmpty(adHocJobDate)) {
            log.info("Running SendNotificationsTask with Adhoc dates {}", adHocJobDate);
            dataExtractDateValidator.dateValidator(adHocJobDate);
            firstStopReminderDate = LocalDate.parse(adHocJobDate).minusDays(firstNotificationDays).toString();
            secondStopReminderDate = LocalDate.parse(adHocJobDate).minusDays(secondNotificationDays).toString();
            hseReminderDate = LocalDate.parse(adHocJobDate).minusDays(hseYesNotificationDays).toString();
            dormantWarningDate = LocalDate.parse(adHocJobDate).minusDays(dormantWarningDays).toString();
            unsubmittedApplicationDate =
                    LocalDate.parse(adHocJobDate).minusDays(unsubmittedApplicationDays).toString();
        }

        try {
            if (!featureToggleService.isFirstStopReminderFeatureToggleOn()) {
                log.info("Feature toggle FirstStopReminderFeatureToggle is off, skipping task");
            } else {
                log.info("Calling Send First Stop Reminder for date {}", firstStopReminderDate);
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
    }
}