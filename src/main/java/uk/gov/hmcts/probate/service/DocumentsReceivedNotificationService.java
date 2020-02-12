package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.EmailAddressExecutorsApplyingValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentsReceivedNotificationService {

    private final CallbackResponseTransformer callbackResponseTransformer;
    private final NotificationService notificationService;
    private final EventValidationService eventValidationService;
    private final List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;

    public CallbackResponse handleDocumentReceivedNotification(CallbackRequest callbackRequest) throws NotificationClientException {

        log.info("Preparing to send email notification for documents being recieved");
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        CallbackResponse response;

        List<Document> documents = new ArrayList<>();

        if (caseData.isDocsReceivedEmailNotificationRequested() && !caseData.isCreatedFromBulkScan()) {
            response = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules);
            if (response.getErrors().isEmpty()) {
                Document documentsReceivedSentEmail = notificationService.sendEmail(DOCUMENTS_RECEIVED, caseDetails);
                documents.add(documentsReceivedSentEmail);
                response = callbackResponseTransformer.addDocuments(callbackRequest, documents, null, null);
            }
        } else {
            if (caseData.isCreatedFromBulkScan()) {
                log.info("Document received notification ignored for case: {} created from bulk scan.", caseDetails.getId());
            }
            response = callbackResponseTransformer.addDocuments(callbackRequest, documents, null, null);

        }
        return response;
    }
}
