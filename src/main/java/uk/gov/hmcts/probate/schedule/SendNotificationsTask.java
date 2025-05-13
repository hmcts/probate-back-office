package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.notification.AutomatedNotificationService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;
import static uk.gov.hmcts.probate.model.NotificationType.FIRST_STOP_REMINDER;
import static uk.gov.hmcts.probate.model.NotificationType.SECOND_STOP_REMINDER;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendNotificationsTask implements Runnable {
    private final DataExtractDateValidator dataExtractDateValidator;
    private final AutomatedNotificationService automatedNotificationService;
    private final FeatureToggleService featureToggleService;


    @Value("${automated_notification.stop_reminder.first_notification_days}")
    private int firstNotificationDays;

    @Value("${automated_notification.stop_reminder.second_notification_days}")
    private int secondNotificationDays;

    @Value("${adhocSchedulerJobDate}")
    public String adHocJobDate;

    @Override
    public void run() {
        log.info("Scheduled task SendNotificationsTask started");
        String firstStopReminderDate = DATE_FORMAT.format(LocalDate.now().minusDays(firstNotificationDays));
        String secondStopReminderDate = DATE_FORMAT.format(LocalDate.now().minusDays(secondNotificationDays));
        if (StringUtils.isNotEmpty(adHocJobDate)) {
            log.info("Running SendNotificationsTask with Adhoc dates {}", adHocJobDate);
            firstStopReminderDate = LocalDate.parse(adHocJobDate).minusDays(firstNotificationDays).toString();
            secondStopReminderDate = LocalDate.parse(adHocJobDate).minusDays(secondNotificationDays).toString();
        }

        try {
            if (!featureToggleService.isFirstStopReminderFeatureToggleOn()) {
                log.info("Feature toggle FirstStopReminderFeatureToggle is off, skipping task");
            } else {
                log.info("Calling Send First Stop Reminder for date {}", firstStopReminderDate);
                dataExtractDateValidator.dateValidator(firstStopReminderDate);
                log.info("Perform Send First Stop Reminder started");
                automatedNotificationService.sendNotification(firstStopReminderDate, FIRST_STOP_REMINDER);
                log.info("Perform Send First Stop Reminder finished");

            }
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler Send First Stop Reminder task {}", e.getMessage());
        }

        try {
            if (!featureToggleService.isSecondStopReminderFeatureToggleOn()) {
                log.info("Feature toggle SecondStopReminderFeatureToggle is off, skipping task");
            } else {
                log.info("Calling Send Second Stop Reminder for date {}", secondStopReminderDate);
                dataExtractDateValidator.dateValidator(secondStopReminderDate);
                log.info("Perform Send Second Stop Reminder started");
                automatedNotificationService.sendNotification(secondStopReminderDate, SECOND_STOP_REMINDER);
                log.info("Perform Send Second Stop Reminder finished");
            }
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler Send Second Stop Reminder task {}", e.getMessage());
        }
    }
}