package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.DocumentService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;

@RequiredArgsConstructor
@RequestMapping(value = "/document", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class DocumentController {

    private final PDFManagementService pdfManagementService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final DocumentService documentService;
    private final NotificationService notificationService;

    @PostMapping(path = "/generate-grant-draft", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantDraft(@RequestBody CallbackRequest callbackRequest) {

        Document document = pdfManagementService.generateAndUpload(callbackRequest, DIGITAL_GRANT_DRAFT);

        documentService.expire(callbackRequest, DIGITAL_GRANT_DRAFT);

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
                Arrays.asList(document)));
    }

    @PostMapping(path = "/generate-grant", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrant(@RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        List<Document> documents = new ArrayList<>();

        Document digitalGrantDocument = pdfManagementService.generateAndUpload(callbackRequest, DIGITAL_GRANT);
        documents.add(digitalGrantDocument);

        documentService.expire(callbackRequest, DIGITAL_GRANT_DRAFT);

        CaseData caseData = callbackRequest.getCaseDetails().getData();

        if (caseData.isGrantIssuedEmailNotificationRequested()) {
            Document grantIssuedSentEmail = notificationService.sendEmail(GRANT_ISSUED, caseData);
            documents.add(grantIssuedSentEmail);
        }

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest, documents));
    }
}
