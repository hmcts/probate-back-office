package uk.gov.hmcts.probate.service.notification;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.service.notify.NotificationClientException;

import static uk.gov.hmcts.probate.model.NotificationType.HSE_REMINDER;

@Service
public class HseReminderNotification implements NotificationStrategy {
    private static final String EVENT_DESCRIPTION = "HSE Reminder";
    private static final String EVENT_SUMMARY = "HSE Reminder";
    private static final String HSE_REMINDER_FAILURE_EVENT_DESCRIPTION = "Failed to send HSE reminder";
    private static final String HSE_REMINDER_FAILURE_EVENT_SUMMARY = "Failed to send HSE reminder";
    private final NotificationService notificationService;

    public HseReminderNotification(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String getQueryTemplate() {
        return "templates/elasticsearch/caseMatching/hse_reminder_query.json";
    }

    @Override
    public boolean matchesType(NotificationType type) {
        return type == HSE_REMINDER;
    }

    @Override
    public Document sendEmail(CaseDetails caseDetails) throws NotificationClientException {
        return notificationService.sendHseReminderEmail(caseDetails);
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
    public String getFailureEventDescription() {
        return HSE_REMINDER_FAILURE_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventSummary() {
        return HSE_REMINDER_FAILURE_EVENT_SUMMARY;
    }

    @Override
    public EventId getEventId() {
        return EventId.AUTOMATED_NOTIFICATION;
    }

    @Override
    public NotificationType getType() {
        return HSE_REMINDER;
    }
}