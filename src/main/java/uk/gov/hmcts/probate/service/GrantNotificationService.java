package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.GrantScheduleResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;
import uk.gov.hmcts.reform.probate.model.ProbateDocumentLink;
import uk.gov.hmcts.reform.probate.model.ProbateDocumentType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static uk.gov.hmcts.probate.model.ccd.EventId.SCHEDULED_UPDATE_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_SENT;
import static uk.gov.hmcts.probate.model.ccd.EventId.SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_SENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrantNotificationService {

    private static final String IDENTIFIED_KEY = "grantDelayedNotificationIdentified";
    private static final String DELAY_SENT_KEY = "grantDelayedNotificationSent";
    private static final String AWAITING_SENT_KEY = "grantAwaitingDocumentatioNotificationSent";

    private final NotificationService notificationService;
    private final EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;
    private final CaseQueryService caseQueryService;
    private final CcdClientApi ccdClientApi;
    private final SecurityUtils securityUtils;

    public GrantScheduleResponse handleGrantDelayedNotification(String date) {
        List<String> delayedRepsonseData = new ArrayList<>();
        List<ReturnedCaseDetails> foundCases = caseQueryService.findCasesForGrantDelayed(date);
        Collections.shuffle(foundCases);
        log.info("Found cases for grant delayed notification: {}", foundCases.size());
        for (ReturnedCaseDetails foundCase : foundCases) {
            delayedRepsonseData.add(sendNotificationForCase(foundCase, SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_SENT));
        }
        return GrantScheduleResponse.builder().scheduleResponseData(delayedRepsonseData).build();
    }

    public GrantScheduleResponse handleAwaitingDocumentationNotification(String date) {
        List<String> delayedRepsonseData = new ArrayList<>();
        List<ReturnedCaseDetails> foundCases = caseQueryService.findCasesForGrantAwaitingDocumentation(date);
        log.info("Found cases for grant awaiting documentation notification: {}", foundCases.size());
        for (ReturnedCaseDetails foundCase : foundCases) {
            delayedRepsonseData.add(sendNotificationForCase(foundCase, SCHEDULED_UPDATE_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_SENT));
        }
        return GrantScheduleResponse.builder().scheduleResponseData(delayedRepsonseData).build();
    }

    private String sendNotificationForCase(ReturnedCaseDetails foundCase, EventId sentEvent) {
        log.info("Preparing to send email to executors for grant notification");
        CCDData dataForEmailAddress = CCDData.builder()
            .primaryApplicantEmailAddress(foundCase.getData().getPrimaryApplicantEmailAddress())
            .applicationType(foundCase.getData().getApplicationType().getCode())
            .build();
        List<FieldErrorResponse> emailErrors = emailAddressNotifyApplicantValidationRule.validate(dataForEmailAddress);
        String caseId = foundCase.getId().toString();
        if (!emailErrors.isEmpty()) {
            log.error("Cannot send Grant notification, for email validation errors: {}", emailErrors.get(0).getMessage());
            return getErroredCaseIdentifier(caseId, emailErrors.get(0).getMessage());
        }

        if (hasCaseSinceBeenUpdated(foundCase, sentEvent)) {
            return getErroredCaseIdentifier(caseId, "Case has already been updated");
        }
        try {
            updateCaseIdentified(foundCase);
        } catch (RuntimeException e) {
            log.error("Cannot identify Grant for notification, message: {}", e.getMessage());
            return getErroredCaseIdentifier(caseId, e.getMessage());
        }
        try {
            Boolean grantDelayedNotificationSent = null;
            Boolean grantAwaitingDocumentatioNotificationSent = null;
            Document emailDocument = null;
            if (SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_SENT.equals(sentEvent)) {
                grantDelayedNotificationSent = TRUE;
                emailDocument = notificationService.sendGrantDelayedEmail(foundCase);
            } else if (SCHEDULED_UPDATE_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_SENT.equals(sentEvent)) {
                grantAwaitingDocumentatioNotificationSent = TRUE;
                emailDocument = notificationService.sendGrantAwaitingDocumentationEmail(foundCase);
            } else {
                throw new RuntimeException("EventId not recognised for sending email");
            }
            updateFoundCase(foundCase, emailDocument, sentEvent, grantDelayedNotificationSent, grantAwaitingDocumentatioNotificationSent);
        } catch (NotificationClientException e) {
            log.error("Error sending email for Grant notification with exception: {}. Has message: {}", e.getClass(), e.getMessage());
            caseId = getErroredCaseIdentifier(caseId, e.getMessage());
        } catch (RuntimeException re) {
            log.error("Error updating case for Grant notification with exception: {}. Has message: {}", re.getClass(), re.getMessage());
            caseId = getErroredCaseIdentifier(caseId, re.getMessage());
        }

        return caseId;
    }

    private boolean hasCaseSinceBeenUpdated(ReturnedCaseDetails foundCase, EventId sentEvent) {
        CaseDetails caseDetails = ccdClientApi.readForCaseWorker(CcdCaseType.GRANT_OF_REPRESENTATION, foundCase.getId().toString(),
            securityUtils.getUserAndServiceSecurityDTO());
        if (SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_SENT.equals(sentEvent)) { 
            if ( (caseDetails.getData().get(IDENTIFIED_KEY) != null && "Yes".equalsIgnoreCase(caseDetails.getData().get(IDENTIFIED_KEY).toString()))
                || (caseDetails.getData().get(DELAY_SENT_KEY) != null && "Yes".equalsIgnoreCase(caseDetails.getData().get(DELAY_SENT_KEY).toString()))
            ){
                return true;
            }
        } else if (SCHEDULED_UPDATE_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_SENT.equals(sentEvent)) {
            if ( (caseDetails.getData().get(IDENTIFIED_KEY) != null && "Yes".equalsIgnoreCase(caseDetails.getData().get(IDENTIFIED_KEY).toString()))
                || (caseDetails.getData().get(AWAITING_SENT_KEY) != null && "Yes".equalsIgnoreCase(caseDetails.getData().get(AWAITING_SENT_KEY).toString()))
            ){
                return true;
            }
        }
        return false;
    }

    private String getErroredCaseIdentifier(String caseId, String message) {
        return "<" + caseId + ":" + message + ">";
    }

    private void updateCaseIdentified(ReturnedCaseDetails foundCase) {
        log.info("Updating case for grant identified, caseId: {}", foundCase.getId());

        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
            .grantDelayedNotificationIdentified(TRUE)
            .build();

        ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, foundCase.getId().toString(),
            grantOfRepresentationData, EventId.SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_IDENTIFIED, securityUtils.getUserAndServiceSecurityDTO());

    }

    private void updateFoundCase(ReturnedCaseDetails foundCase, Document emailDocument, EventId sentEvent, Boolean grantDelayedNotificationSent,
                                 Boolean grantAwaitingDocumentatioNotificationSent) {
        log.info("Updating case for grant notification, caseId: {}", foundCase.getId());

        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
            .grantDelayedNotificationSent(grantDelayedNotificationSent)
            .grantAwaitingDocumentatioNotificationSent(grantAwaitingDocumentatioNotificationSent)
            .grantDelayedNotificationIdentified(FALSE)
            .probateNotificationsGenerated(getProbateDocuments(emailDocument, foundCase.getData().getProbateNotificationsGenerated()))
            .build();
        ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, foundCase.getId().toString(),
            grantOfRepresentationData, sentEvent, securityUtils.getUserAndServiceSecurityDTO());
        log.info("Updated found case:{}", foundCase.getId());

    }

    private List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<ProbateDocument>> getProbateDocuments(Document emailDocument,
                                                                                                                List<CollectionMember<Document>> probateDocumentsGenerated) {
        List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<ProbateDocument>> probateDocuments = new ArrayList<>();
        for (CollectionMember<Document> documentCollectionMember : probateDocumentsGenerated) {
            probateDocuments.add(new uk.gov.hmcts.reform.probate.model.cases.CollectionMember<ProbateDocument>(documentCollectionMember.getId(),
                getProbateDocument(documentCollectionMember.getValue())));
        }
        probateDocuments.add(new uk.gov.hmcts.reform.probate.model.cases.CollectionMember<>(null, getProbateDocument(emailDocument)));
        return probateDocuments;
    }

    private ProbateDocument getProbateDocument(Document boDocument) {
        ProbateDocumentLink probateDocumentLink = ProbateDocumentLink.builder()
            .documentBinaryUrl(boDocument.getDocumentLink().getDocumentBinaryUrl())
            .documentFilename(boDocument.getDocumentLink().getDocumentFilename())
            .documentUrl(boDocument.getDocumentLink().getDocumentUrl())
            .build();
        ProbateDocumentType probateDocumentType = ProbateDocumentType.valueOf(boDocument.getDocumentType().name());
        return ProbateDocument.builder()
            .documentDateAdded(boDocument.getDocumentDateAdded())
            .documentFileName(boDocument.getDocumentFileName())
            .documentGeneratedBy(boDocument.getDocumentGeneratedBy())
            .documentLink(probateDocumentLink)
            .documentType(probateDocumentType)
            .build();

    }
}
