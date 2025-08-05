package uk.gov.hmcts.probate.service.notification;

import lombok.Setter;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static uk.gov.hmcts.probate.model.NotificationType.SECOND_STOP_REMINDER;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_STOPPED_REISSUE;

@Service
public class SecondStopReminderNotification implements NotificationStrategy {
    private static final String SECOND_STOP_REMINDER_EVENT_DESCRIPTION = "Send Second Stop Reminder";
    private static final String SECOND_STOP_REMINDER_EVENT_SUMMARY = "Send Second Stop Reminder";
    private static final String SECOND_STOP_REMINDER_FAILURE_EVENT_DESCRIPTION = "Failed to send second stop reminder";
    private static final String SECOND_STOP_REMINDER_FAILURE_EVENT_SUMMARY = "Failed to send second stop reminder";
    private static final String FIRST_STOP_REMINDER_SENT_DATE = "firstStopReminderSentDate";
    private static final String LAST_MODIFIED_DATE_FOR_DORMANT = "lastModifiedDateForDormant";

    private final NotificationService notificationService;

    @Setter
    private LocalDate referenceDate;

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
    public Document sendNotification(CaseDetails caseDetails) throws NotificationClientException {
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
        return EventId.AUTO_NOTIFICATION_SECOND_STOP_REMINDER;
    }

    @Override
    public NotificationType getType() {
        return SECOND_STOP_REMINDER;
    }

    @Override
    public Predicate<CaseDetails> accepts() {
        return cd -> {
            if (cd == null || cd.getData() == null || referenceDate == null) {
                return false;
            }
            Map<String, Object> data = cd.getData();

            LocalDateTime  lastModifiedDateForDormant = Optional.ofNullable(data
                            .get(LAST_MODIFIED_DATE_FOR_DORMANT))
                    .map(Object::toString)
                    .map(LocalDateTime::parse)
                    .orElse(null);

            LocalDate firstStopReminderDate = Optional.ofNullable(data.get(FIRST_STOP_REMINDER_SENT_DATE))
                    .map(Object::toString)
                    .map(LocalDate::parse)
                    .orElse(null);
            return  (STATE_BO_CASE_STOPPED.equals(cd.getState()) || STATE_BO_CASE_STOPPED_REISSUE.equals(cd.getState()))
                    && firstStopReminderDate != null
                    && firstStopReminderDate.equals(referenceDate)
                    && lastModifiedDateForDormant != null
                    && lastModifiedDateForDormant.isBefore(referenceDate.atStartOfDay());
        };
    }

}