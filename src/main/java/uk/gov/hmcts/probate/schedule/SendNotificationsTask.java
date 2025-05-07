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
                log.info("Calling Send Stop Reminder from date, to date {} {}",
                        firstStopReminderDate, firstStopReminderDate);
                dataExtractDateValidator.dateValidator(firstStopReminderDate, firstStopReminderDate);
                log.info("Perform Send Stop Reminder (8-week) started");
                automatedNotificationService.sendStopReminder(firstStopReminderDate, true);
                log.info("Perform Send Stop Reminder (8-week) finished");

            }
            if (!featureToggleService.isSecondStopReminderFeatureToggleOn()) {
                log.info("Feature toggle SecondStopReminderFeatureToggle is off, skipping task");
            } else {
                dataExtractDateValidator.dateValidator(secondStopReminderDate, secondStopReminderDate);
                log.info("Perform Send Stop Reminder (12-week) started");
                automatedNotificationService.sendStopReminder(secondStopReminderDate, false);
                log.info("Perform Send Stop Reminder (12-week) finished");

            }
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler Send Stop Reminder task {}", e.getMessage());
        }
    }


}
