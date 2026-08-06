package uk.gov.hmcts.probate.service.notification;

import lombok.Setter;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Predicate;


import static uk.gov.hmcts.probate.model.NotificationType.FIRST_REDEC_REMINDER;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT;

@Service
public class FirstRedecReminderNotification implements NotificationStrategy {
    private static final String FIRST_REDEC_REMINDER_EVENT_DESCRIPTION = "Send First Redec Reminder";
    private static final String FIRST_REDEC_REMINDER_EVENT_SUMMARY = "Send First Redec Reminder";
    private static final String FIRST_REDEC_REMINDER_FAILURE_EVENT_DESCRIPTION = "Failed to send first Redec reminder";
    private static final String FIRST_REDEC_REMINDER_FAILURE_EVENT_SUMMARY = "Failed to send first Redec reminder";
    private static final String LAST_MODIFIED_DATE_FOR_DORMANT = "lastModifiedDateForDormant";

    private final NotificationService notificationService;

    @Setter
    private LocalDate referenceDate;

    public FirstRedecReminderNotification(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String getQueryTemplate() {
        return "templates/elasticsearch/caseMatching/first_redec_reminder_query.json";
    }

    @Override
    public boolean matchesType(NotificationType type) {
        return type == FIRST_REDEC_REMINDER;
    }

    @Override
    public Document sendNotification(CaseDetails caseDetails) throws NotificationClientException {
        return notificationService.sendRedecReminderEmail(caseDetails, true);
    }

    @Override
    public String getEventSummary() {
        return FIRST_REDEC_REMINDER_EVENT_SUMMARY;
    }

    @Override
    public String getEventDescription() {
        return FIRST_REDEC_REMINDER_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventDescription() {
        return FIRST_REDEC_REMINDER_FAILURE_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventSummary() {
        return FIRST_REDEC_REMINDER_FAILURE_EVENT_SUMMARY;
    }

    @Override
    public EventId getEventId() {
        return EventId.AUTO_NOTIFICATION_FIRST_STOP_REMINDER;
    }

    @Override
    public NotificationType getType() {
        return FIRST_REDEC_REMINDER;
    }

    @Override
    public Predicate<CaseDetails> accepts() {
        return cd -> cd != null
            && cd.getData() != null
            && referenceDate != null
            && STATE_BO_REDEC_NOTIFICATION_SENT.equals(cd.getState())
            && isValidLastModifiedDate(cd);
    }

    private boolean isValidLastModifiedDate(CaseDetails caseDetails) {
        return Optional.ofNullable(caseDetails.getData().get(LAST_MODIFIED_DATE_FOR_DORMANT))
                .map(Object::toString)
                .map(LocalDateTime::parse)
                .map(lastModifiedDate -> !lastModifiedDate.isAfter(referenceDate.plusDays(1).atStartOfDay()))
                .orElse(false);
    }
}
