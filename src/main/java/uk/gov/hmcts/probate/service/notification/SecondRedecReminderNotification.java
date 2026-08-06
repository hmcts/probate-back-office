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

import static uk.gov.hmcts.probate.model.NotificationType.SECOND_REDEC_REMINDER;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT;

@Service
public class SecondRedecReminderNotification implements NotificationStrategy {
    private static final String SECOND_REDEC_REMINDER_EVENT_DESCRIPTION = "Send Second Redec Reminder";
    private static final String SECOND_REDEC_REMINDER_EVENT_SUMMARY = "Send Second Redec Reminder";
    private static final String SECOND_REDEC_REMINDER_FAILURE_EVENT_DESCRIPTION =
            "Failed to send second Redec reminder";
    private static final String SECOND_REDEC_REMINDER_FAILURE_EVENT_SUMMARY = "Failed to send second Redec reminder";
    private static final String FIRST_REDEC_REMINDER_SENT_DATE = "firstRedecReminderSentDate";
    private static final String LAST_MODIFIED_DATE_FOR_DORMANT = "lastModifiedDateForDormant";

    private final NotificationService notificationService;

    @Setter
    private LocalDate referenceDate;

    public SecondRedecReminderNotification(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String getQueryTemplate() {
        return "templates/elasticsearch/caseMatching/second_redec_reminder_query.json";
    }

    @Override
    public boolean matchesType(NotificationType type) {
        return type == SECOND_REDEC_REMINDER;
    }

    @Override
    public Document sendNotification(CaseDetails caseDetails) throws NotificationClientException {
        return notificationService.sendRedecReminderEmail(caseDetails, false);
    }

    @Override
    public String getEventSummary() {
        return SECOND_REDEC_REMINDER_EVENT_SUMMARY;
    }

    @Override
    public String getEventDescription() {
        return SECOND_REDEC_REMINDER_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventDescription() {
        return SECOND_REDEC_REMINDER_FAILURE_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventSummary() {
        return SECOND_REDEC_REMINDER_FAILURE_EVENT_SUMMARY;
    }

    @Override
    public EventId getEventId() {
        return EventId.AUTO_NOTIFICATION_SECOND_STOP_REMINDER;
    }

    @Override
    public NotificationType getType() {
        return SECOND_REDEC_REMINDER;
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

            LocalDate firstRedecReminderDate = Optional.ofNullable(data.get(FIRST_REDEC_REMINDER_SENT_DATE))
                    .map(Object::toString)
                    .map(LocalDate::parse)
                    .orElse(null);
            return  STATE_BO_REDEC_NOTIFICATION_SENT.equals(cd.getState())
                    && firstRedecReminderDate != null
                    && firstRedecReminderDate.equals(referenceDate)
                    && lastModifiedDateForDormant != null
                    && lastModifiedDateForDormant.isBefore(referenceDate.atStartOfDay());
        };
    }

}