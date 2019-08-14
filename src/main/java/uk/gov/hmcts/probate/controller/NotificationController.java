package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.docmosis.GrantOfRepresentationDocmosisMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_CAVEAT;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_REQUEST_INFORMATION;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;

@RequiredArgsConstructor
@RequestMapping(value = "/notify", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class NotificationController {

    @Autowired
    private final DocumentGeneratorService documentGeneratorService;
    private final NotificationService notificationService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final EventValidationService eventValidationService;
    private final List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;
    private final List<EmailAddressNotifyValidationRule> emailAddressNotifyValidationRules;
    private final PDFManagementService pdfManagementService;
    private final BulkPrintService bulkPrintService;
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;
    private final GrantOfRepresentationDocmosisMapperService gorDocmosisService;


    @PostMapping(path = "/documents-received")
    public ResponseEntity<CallbackResponse> sendDocumentReceivedNotification(
            @Validated({EmailAddressNotificationValidationRule.class})
            @RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        CallbackResponse response;

        List<Document> documents = new ArrayList<>();
        if (caseData.isDocsReceivedEmailNotificationRequested()) {
            response = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules);
            if (response.getErrors().isEmpty()) {
                Document documentsReceivedSentEmail = notificationService.sendEmail(DOCUMENTS_RECEIVED, caseDetails);
                documents.add(documentsReceivedSentEmail);
                response = callbackResponseTransformer.addDocuments(callbackRequest, documents, null, null);
            }
        } else {
            response = callbackResponseTransformer.addDocuments(callbackRequest, documents, null, null);

        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/case-stopped")
    public ResponseEntity<CallbackResponse> sendCaseStoppedNotification(
            @Validated({EmailAddressNotifyValidationRule.class})
            @RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        CallbackResponse response = CallbackResponse.builder().errors(new ArrayList<>()).build();

        Document document;
        List<Document> documents = new ArrayList<>();
        String letterId = null;

        if (caseData.isCaveatStopNotificationRequested() && caseData.isCaveatStopEmailNotificationRequested()) {
            response = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules);
            if (response.getErrors().isEmpty()) {
                log.info("Initiate call to send caveat email for case id {} ",
                        callbackRequest.getCaseDetails().getId());
                document = notificationService.sendEmail(CASE_STOPPED_CAVEAT, caseDetails);
                documents.add(document);
                log.info("Successful response for caveat email for case id {} ",
                        callbackRequest.getCaseDetails().getId());
            }
        } else if (caseData.isCaveatStopNotificationRequested() && !caseData.isCaveatStopEmailNotificationRequested()) {

            Document coversheet = documentGeneratorService.generateCoversheet(callbackRequest);
            documents.add(coversheet);

            log.info("Initiate call to generate Caveat stopped document for case id {} ",
                    callbackRequest.getCaseDetails().getId());
            Map<String, Object> placeholders = gorDocmosisService.caseDataForStoppedMatchedCaveat(callbackRequest.getCaseDetails());
            Document caveatRaisedDoc =
                    pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, DocumentType
                            .CAVEAT_STOPPED);
            documents.add(caveatRaisedDoc);
            log.info("Successful response for caveat stopped document for case id {} ", callbackRequest.getCaseDetails().getId());

            if (caseData.isCaveatStopSendToBulkPrintRequested()) {
                log.info("Initiate call to bulk print for Caveat stopped document and coversheet for case id {} ",
                        callbackRequest.getCaseDetails().getId());
                SendLetterResponse sendLetterResponse =
                        bulkPrintService.sendToBulkPrint(callbackRequest, caveatRaisedDoc, coversheet);
                letterId = sendLetterResponse != null
                        ? sendLetterResponse.letterId.toString()
                        : null;
                response = eventValidationService.validateBulkPrintResponse(letterId, bulkPrintValidationRules);
            }
        } else {
            response = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules);
            if (response.getErrors().isEmpty()) {
                log.info("Initiate call to notify applicant for case id {} ",
                        callbackRequest.getCaseDetails().getId());
                document = notificationService.sendEmail(CASE_STOPPED, caseDetails);
                documents.add(document);
                log.info("Successful response from notify for case id {} ",
                        callbackRequest.getCaseDetails().getId());
            }
        }
        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.caseStopped(callbackRequest, documents, letterId);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/request-information-default-values")
    public ResponseEntity<CallbackResponse> requestInformationDefaultValues(
            @RequestBody CallbackRequest callbackRequest) {

        CallbackResponse callbackResponse = callbackResponseTransformer.defaultRequestInformationValues(callbackRequest);

        return ResponseEntity.ok(callbackResponse);
    }


    @PostMapping(path = "/stopped-information-request")
    public ResponseEntity<CallbackResponse> informationRequest(@RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        List<Document> documents = new ArrayList<>();
        CallbackResponse response = CallbackResponse.builder().errors(new ArrayList<>()).build();

        if (callbackRequest.getCaseDetails().getData().isBoEmailRequestInfoNotificationRequested()) {
            response = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules);
            if (response.getErrors().isEmpty()) {
                log.info("Initiate call to send request for information email for case id {} ",
                        callbackRequest.getCaseDetails().getId());
                documents.add(notificationService.sendEmail(CASE_STOPPED_REQUEST_INFORMATION, callbackRequest.getCaseDetails()));
                log.info("Successful response for request for information email for case id {} ",
                        callbackRequest.getCaseDetails().getId());
            }

        }

        //if (!callbackRequest.getCaseDetails().getData().isBoEmailRequestInfoNotificationRequested()) {
        //    //generate coversheet and docmosis pdf template for requestInformation
        //    Document coversheet;
        //    Document requestInformation;
        //
        //    if (callbackRequest.getCaseDetails().getData().isBoRequestInfoSendToBulkPrintRequested()) {
        //        //send coversheet and docmosis pdf to bulk print
        //
        //    }
        //}

        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.addInformationRequestDocuments(callbackRequest, documents);
        }

        return ResponseEntity.ok(response);
    }

}
