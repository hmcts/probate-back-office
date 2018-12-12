package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;

@RequiredArgsConstructor
@RequestMapping(value = "/document", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class DocumentController {

    private final PDFManagementService pdfManagementService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final DocumentService documentService;
    private final NotificationService notificationService;
    private static final String GRANT_OF_PROBATE = "gop";
    private static final String ADMON_WILL = "admonWill";
    private static final String INTESTACY = "intestacy";
    private static final String EDGE_CASE = "edgeCase";

    @PostMapping(path = "/generate-grant-draft", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantDraft(@RequestBody CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        Document document = generateDocument(caseData.getCaseType(), callbackRequest);

        DocumentType[] documentTypes = {DIGITAL_GRANT_DRAFT, DIGITAL_GRANT, INTESTACY_GRANT, INTESTACY_GRANT_DRAFT, ADMON_WILL_GRANT_DRAFT, ADMON_WILL_GRANT};
        for (DocumentType documentType : documentTypes) {
            documentService.expire(callbackRequest, documentType);
        }

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
                Arrays.asList(document)));
    }

    @PostMapping(path = "/generate-grant", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrant(@RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        List<Document> documents = new ArrayList<>();

        Document digitalGrantDocument = generateDocument(caseData.getCaseType(), callbackRequest);
        documents.add(digitalGrantDocument);

        DocumentType[] documentTypes = {DIGITAL_GRANT_DRAFT, DIGITAL_GRANT, INTESTACY_GRANT, INTESTACY_GRANT_DRAFT, ADMON_WILL_GRANT_DRAFT, ADMON_WILL_GRANT};
        for (DocumentType documentType : documentTypes) {
            documentService.expire(callbackRequest, documentType);
        }

        if (caseData.isGrantIssuedEmailNotificationRequested()) {
            Document grantIssuedSentEmail = notificationService.sendEmail(GRANT_ISSUED, caseDetails);
            documents.add(grantIssuedSentEmail);
        }

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest, documents));
    }

    private Document generateDocument(String caseType, CallbackRequest callbackRequest) {
        DocumentType template;

        switch (caseType) {
            case GRANT_OF_PROBATE:
                template = DIGITAL_GRANT;
                return pdfManagementService.generateAndUpload(callbackRequest, template);
            case INTESTACY:
                template = INTESTACY_GRANT;
                return pdfManagementService.generateAndUpload(callbackRequest, template);
            case ADMON_WILL:
                template = ADMON_WILL_GRANT;
                return pdfManagementService.generateAndUpload(callbackRequest, template);
            case EDGE_CASE:
                return Document.builder().documentType(DocumentType.EDGE_CASE).build();
            default:
                template = DIGITAL_GRANT;
                return pdfManagementService.generateAndUpload(callbackRequest, template);

        }


    }
}
