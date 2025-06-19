package uk.gov.hmcts.probate.service.notification;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static uk.gov.hmcts.probate.model.NotificationType.UNSUBMITTED_APPLICATION;

@Slf4j
@Service
public class UnsubmittedApplicationNotification implements NotificationStrategy {
    private static final String EVENT_DESCRIPTION = "Unsubmitted Application Reminder";
    private static final String EVENT_SUMMARY = "Unsubmitted Application Reminder";
    private static final String UNSUBMITTED_APPLICATION_FAILURE_EVENT_DESCRIPTION =
            "Failed to send Unsubmitted Application Reminder";
    private static final String UNSUBMITTED_APPLICATION_FAILURE_EVENT_SUMMARY =
            "Failed to send Unsubmitted Application Reminder";
    private static final String UNSUBMITTED_APPLICATION_ES_QUERY_PATH =
            "templates/elasticsearch/caseMatching/unsubmitted_application_query.json";
    private static final List<String> UNSUBMITTED_APPLICATION_STATES = List.of(
            "Pending",
            "CasePaymentFailed",
            "PAAppCreated",
            "SolAdmonCreated",
            "SolAppCreatedDeceasedDtls",
            "SolAppCreatedSolicitorDtls",
            "SolAppUpdated",
            "SolProbateCreated",
            "SolIntestacyCreated",
            "CaseCreated"
    );
    private final NotificationService notificationService;

    @Setter
    private LocalDate referenceDate;

    public UnsubmittedApplicationNotification(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String getQueryTemplate() {
        return UNSUBMITTED_APPLICATION_ES_QUERY_PATH;
    }

    @Override
    public boolean matchesType(NotificationType type) {
        return type == UNSUBMITTED_APPLICATION;
    }

    @Override
    public Document sendEmail(CaseDetails caseDetails) throws NotificationClientException {
        notificationService.sendUnsubmittedApplicationEmail(caseDetails);
        return new Document(); //Dummy return, as the actual document is not used in this context.
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
        return UNSUBMITTED_APPLICATION_FAILURE_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventSummary() {
        return UNSUBMITTED_APPLICATION_FAILURE_EVENT_SUMMARY;
    }

    @Override
    public EventId getEventId() {
        return EventId.AUTO_NOTIFICATION_UNSUBMITTED_APPLICATION;
    }

    @Override
    public NotificationType getType() {
        return UNSUBMITTED_APPLICATION;
    }

    @Override
    public Predicate<CaseDetails> accepts() {
        return cd -> {
            if (cd == null || cd.getData() == null || referenceDate == null) {
                return false;
            }
            boolean isValidState = UNSUBMITTED_APPLICATION_STATES.stream()
                    .anyMatch(state -> state.equals(cd.getState()));
            boolean isValidLastModified =
                    !cd.getLastModified().isAfter(referenceDate.plusDays(1).atStartOfDay());
            return isValidState && isValidLastModified;
        };
    }

    @Override
    public boolean skipSaveNotification() {
        return true;
    }
}