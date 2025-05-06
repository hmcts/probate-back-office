package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
    private static final String FIRST_STOP_REMINDER_TOGGLE = "probate-cron-first-stop-reminder";


    @Value("${automated_notification.stop_reminder.first_notification_days}")
    private int firstNotificationDays;

    @Value("${adhocSchedulerJobDate}")
    public String adHocJobDate;

    @Override
    public void run() {
        log.info("Scheduled task SendNotificationsTask started");
        String date = DATE_FORMAT.format(LocalDate.now().minusDays(firstNotificationDays));
        if (StringUtils.isNotEmpty(adHocJobDate)) {
            date = adHocJobDate;
            log.info("Running SendNotificationsTask with Adhoc dates {}", date);
        }
        log.info("Calling Send Stop Reminder (8-week) from date, to date {} {}", date, date);
        try {
            if (!isFirstStopReminderFeatureToggleOn()) {
                log.info("Feature toggle {} is off, skipping task", FIRST_STOP_REMINDER_TOGGLE);
                return;
            }
            dataExtractDateValidator.dateValidator(date, date);
            log.info("Perform Send Stop Reminder (8-week) started");
            automatedNotificationService.sendFirstStopReminder(date);
            log.info("Perform Send Stop Reminder (8-week) finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler Send Stop Reminder (8-week) task {}", e.getMessage());
        }
    }

    private boolean isFirstStopReminderFeatureToggleOn() {
        return featureToggleService.isFeatureToggleOn(
                FIRST_STOP_REMINDER_TOGGLE, false);
    }
}
