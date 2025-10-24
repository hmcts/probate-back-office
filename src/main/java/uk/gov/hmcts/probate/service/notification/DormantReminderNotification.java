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
import java.util.Optional;
import java.util.function.Predicate;

import static uk.gov.hmcts.probate.model.NotificationType.DORMANT_REMINDER;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_DORMANT;

@Service
public class DormantReminderNotification implements NotificationStrategy {
    private static final String DORMANT_REMINDER_EVENT_DESCRIPTION = "Dormant 12-month Reminder (AN) sent";
    private static final String DORMANT_REMINDER_EVENT_SUMMARY = "Dormant 12-month Reminder (AN) sent";
    private static final String DORMANT_REMINDER_FAILURE_EVENT_DESCRIPTION =
            "Failed to send Dormant 12-month Reminder (AN)";
    private static final String DORMANT_REMINDER_FAILURE_EVENT_SUMMARY =
            "Failed to send Dormant 12-month Reminder (AN)";
    private static final String LAST_MODIFIED_DATE_FOR_DORMANT = "lastModifiedDateForDormant";

    private final NotificationService notificationService;

    @Setter
    private LocalDate referenceDate;

    public DormantReminderNotification(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String getQueryTemplate() {
        return "templates/elasticsearch/caseMatching/dormant_reminder_query.json";
    }

    @Override
    public boolean matchesType(NotificationType type) {
        return type == DORMANT_REMINDER;
    }

    @Override
    public Document sendNotification(CaseDetails caseDetails) throws NotificationClientException {
        return notificationService.sendDormantReminder(caseDetails);
    }

    @Override
    public String getEventSummary() {
        return DORMANT_REMINDER_EVENT_SUMMARY;
    }

    @Override
    public String getEventDescription() {
        return DORMANT_REMINDER_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventDescription() {
        return DORMANT_REMINDER_FAILURE_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventSummary() {
        return DORMANT_REMINDER_FAILURE_EVENT_SUMMARY;
    }

    @Override
    public EventId getEventId() {
        return EventId.AUTO_NOTIFICATION_DORMANT_REMINDER;
    }

    @Override
    public NotificationType getType() {
        return DORMANT_REMINDER;
    }

    @Override
    public Predicate<CaseDetails> accepts() {
        return cd -> cd != null
                && cd.getData() != null
                && referenceDate != null
                && STATE_DORMANT.equals(cd.getState())
                && isValidLastMofifiedDateForDormant(cd);

    }

    private boolean isValidLastMofifiedDateForDormant(CaseDetails caseDetails) {
        return Optional.ofNullable(caseDetails.getData().get(LAST_MODIFIED_DATE_FOR_DORMANT))
                .map(Object::toString)
                .map(LocalDateTime::parse)
                .map(lastModifiedDateForDormant -> !lastModifiedDateForDormant
                        .isAfter(referenceDate.plusDays(1).atStartOfDay()))
                .orElse(false);
    }
}