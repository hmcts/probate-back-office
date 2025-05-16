package uk.gov.hmcts.probate.service.notification;

import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

public interface NotificationStrategy {

    String getQueryTemplate();

    boolean matchesType(NotificationType type);

    Document sendEmail(CaseDetails caseDetails) throws NotificationClientException;

    String getEventSummary();

    String getEventDescription();

    NotificationType getType();
}
