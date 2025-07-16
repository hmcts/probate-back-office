package uk.gov.hmcts.probate.service.notification;

import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import java.util.function.Predicate;

import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.Constants.GRANT_OF_REPRESENTATION_NAME;

public interface NotificationStrategy {

    String getQueryTemplate();

    boolean matchesType(NotificationType type);

    Document sendNotification(CaseDetails caseDetails) throws NotificationClientException;

    String getEventSummary();

    String getEventDescription();

    String getFailureEventDescription();

    String getFailureEventSummary();

    EventId getEventId();

    NotificationType getType();

    Predicate<CaseDetails> accepts();

    default String getCaseTypeName() {
        return GRANT_OF_REPRESENTATION_NAME;
    }

    default boolean skipSaveNotification() {
        return false;
    }
}