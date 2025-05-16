package uk.gov.hmcts.probate.service.notification;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.service.notify.NotificationClientException;

import static uk.gov.hmcts.probate.model.NotificationType.FIRST_STOP_REMINDER;

@Service
public class FirstStopReminderNotification implements NotificationStrategy {
    private static final String EVENT_DESCRIPTION = "Send First Stop Reminder";
    private static final String EVENT_SUMMARY = "Send First Stop Reminder";
    private final NotificationService notificationService;

    public FirstStopReminderNotification(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String getQueryTemplate() {
        return "templates/elasticsearch/caseMatching/first_stop_reminder_query.json";
    }

    @Override
    public boolean matchesType(NotificationType type) {
        return type == FIRST_STOP_REMINDER;
    }

    @Override
    public Document sendEmail(CaseDetails caseDetails) throws NotificationClientException {
        return notificationService.sendStopReminderEmail(caseDetails, true);
    }

    @Override
    public String getEventSummary() {
        return EVENT_SUMMARY;
    }

    @Override
    public String getEventDescription() {
        return EVENT_DESCRIPTION;
    }

    @Override
    public NotificationType getType() {
        return FIRST_STOP_REMINDER;
    }
}