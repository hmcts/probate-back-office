package uk.gov.hmcts.probate.service.notification;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import static uk.gov.hmcts.probate.model.NotificationType.SECOND_STOP_REMINDER;

@Service
public class SecondStopReminderNotification implements NotificationStrategy {
    private static final String SECOND_STOP_REMINDER_EVENT_DESCRIPTION = "Send Second Stop Reminder";
    private static final String SECOND_STOP_REMINDER_EVENT_SUMMARY = "Send Second Stop Reminder";
    private static final String SECOND_STOP_REMINDER_FAILURE_EVENT_DESCRIPTION = "Failed to send second stop reminder";
    private static final String SECOND_STOP_REMINDER_FAILURE_EVENT_SUMMARY = "Failed to send second stop reminder";
    private final NotificationService notificationService;

    public SecondStopReminderNotification(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String getQueryTemplate() {
        return "templates/elasticsearch/caseMatching/second_stop_reminder_query.json";
    }

    @Override
    public boolean matchesType(NotificationType type) {
        return type == SECOND_STOP_REMINDER;
    }

    @Override
    public Document sendEmail(CaseDetails caseDetails) throws NotificationClientException {
        return notificationService.sendStopReminderEmail(caseDetails, false);
    }

    @Override
    public String getEventSummary() {
        return SECOND_STOP_REMINDER_EVENT_SUMMARY;
    }

    @Override
    public String getEventDescription() {
        return SECOND_STOP_REMINDER_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventDescription() {
        return SECOND_STOP_REMINDER_FAILURE_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventSummary() {
        return SECOND_STOP_REMINDER_FAILURE_EVENT_SUMMARY;
    }

    @Override
    public EventId getEventId() {
        return EventId.AUTOMATED_NOTIFICATION;
    }

    @Override
    public NotificationType getType() {
        return SECOND_STOP_REMINDER;
    }
}