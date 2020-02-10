package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.docmosis.GrantOfRepresentationDocmosisMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_COVERSHEET;
import static uk.gov.hmcts.probate.model.State.GRANT_RAISED;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaiseGrantOfRepresentationNotificationService {

    private final NotificationService notificationService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final EventValidationService eventValidationService;
    private final List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;
    private final PDFManagementService pdfManagementService;
    private final GrantOfRepresentationDocmosisMapperService gorDocmosisService;
    private final BulkPrintService bulkPrintService;
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;

    public CallbackResponse handleGrantReceivedNotification(CallbackRequest callbackRequest) throws NotificationClientException {

        log.info("Preparing to send notifications for raising a grant application.");
        CallbackResponse response = CallbackResponse.builder().errors(new ArrayList<>()).build();
        List<Document> documents = new ArrayList<>();
        String letterId = null;
        boolean useEmailNotification =
                callbackRequest.getCaseDetails().getData().getDefaultValueForEmailNotifications().equals(YES) ? true : false;

        if (useEmailNotification) {
            log.info("Email address available, sending email to applicant.");
            response = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules);
            if (response.getErrors().isEmpty()) {
                Document document = notificationService.sendEmail(GRANT_RAISED, callbackRequest.getCaseDetails());
                documents.add(document);
                log.info("Adding document {}", document);
            }

        } else {
            log.info("Email address not available, sending a letter to applicant.");
            Map<String, Object> placeholders = gorDocmosisService.caseDataAsPlaceholders(callbackRequest.getCaseDetails());
            Document coversheet = pdfManagementService
                    .generateDocmosisDocumentAndUpload(placeholders, GRANT_COVERSHEET);
            documents.add(coversheet);
            Document grantRaisedDoc = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, DocumentType.GRANT_RAISED);
            documents.add(grantRaisedDoc);

            SendLetterResponse letterResponse = bulkPrintService.sendToBulkPrint(callbackRequest, grantRaisedDoc, coversheet);
            letterId = letterResponse != null
                    ? letterResponse.letterId.toString()
                    : null;
            response = eventValidationService.validateBulkPrintResponse(letterId, bulkPrintValidationRules);
        }

        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.grantRaised(callbackRequest, documents, letterId);
        }
        return response;
    }

}
