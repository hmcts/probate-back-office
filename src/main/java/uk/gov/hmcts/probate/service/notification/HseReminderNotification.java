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
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.NotificationType.HSE_REMINDER;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_CLOSED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_GRANT_ISSUED;

@Service
public class HseReminderNotification implements NotificationStrategy {
    private static final String EVENT_DESCRIPTION = "HSE Reminder";
    private static final String EVENT_SUMMARY = "HSE Reminder";
    private static final String HSE_REMINDER_FAILURE_EVENT_DESCRIPTION = "Failed to send HSE reminder";
    private static final String HSE_REMINDER_FAILURE_EVENT_SUMMARY = "Failed to send HSE reminder";
    private static final String HSE_ES_QUERY_PATH = "templates/elasticsearch/caseMatching/hse_reminder_query.json";
    private static final String EVIDENCE_HANDLED_DATE = "evidenceHandledDate";
    private static final List<String> EXCLUDED_STATES = List.of(STATE_BO_GRANT_ISSUED, STATE_BO_CASE_CLOSED);
    private final NotificationService notificationService;

    @Setter
    private LocalDate referenceDate;

    public HseReminderNotification(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String getQueryTemplate() {
        return HSE_ES_QUERY_PATH;
    }

    @Override
    public boolean matchesType(NotificationType type) {
        return type == HSE_REMINDER;
    }

    @Override
    public Document sendNotification(CaseDetails caseDetails) throws NotificationClientException {
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
        return EventId.AUTO_NOTIFICATION_HSE_REMINDER;
    }

    @Override
    public NotificationType getType() {
        return HSE_REMINDER;
    }

    @Override
    public Predicate<CaseDetails> accepts() {
        return cd -> {
            if (cd == null || cd.getData() == null || referenceDate == null) {
                return false;
            }

            Object evidenceHandled = cd.getData().get("evidenceHandled");
            LocalDate evidenceHandledDate = Optional.ofNullable(cd.getData().get(EVIDENCE_HANDLED_DATE))
                    .map(Object::toString)
                    .map(LocalDate::parse)
                    .orElse(null);

            return YES.equals(evidenceHandled)
                    && isValidState(cd.getState())
                    && evidenceHandledDate != null
                    && evidenceHandledDate.equals(referenceDate);
        };
    }

    private boolean isValidState(String state) {
        return EXCLUDED_STATES.stream().noneMatch(state::equals);
    }
}