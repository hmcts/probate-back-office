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
    private static final String GRANT_OF_REPRESENTATION = "gop";
    private static final String ADMON_WILL = "admonWill";
    private static final String INTESTACY = "intestacy";

    @PostMapping(path = "/generate-grant-draft", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantDraft(@RequestBody CallbackRequest callbackRequest) {
        DocumentType template;
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        switch (caseData.getCaseType()) {
            case GRANT_OF_REPRESENTATION:
                template = DIGITAL_GRANT_DRAFT;
                break;
            case INTESTACY:
                template = INTESTACY_GRANT_DRAFT;
                break;
            case ADMON_WILL:
                template = ADMON_WILL_GRANT_DRAFT;
                break;
            default:
                template = DIGITAL_GRANT_DRAFT;
        }

        Document document = pdfManagementService.generateAndUpload(callbackRequest, template);

        documentService.expire(callbackRequest, template);

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
                Arrays.asList(document)));
    }

    @PostMapping(path = "/generate-grant", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrant(@RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {
        DocumentType template;
        DocumentType draftTemplate;
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        List<Document> documents = new ArrayList<>();

        switch (caseData.getCaseType()) {
            case GRANT_OF_REPRESENTATION:
                template = DIGITAL_GRANT;
                draftTemplate = DIGITAL_GRANT_DRAFT;
                break;
            case INTESTACY:
                template = INTESTACY_GRANT;
                draftTemplate = INTESTACY_GRANT_DRAFT;
                break;
            case ADMON_WILL:
                template = ADMON_WILL_GRANT;
                draftTemplate = ADMON_WILL_GRANT_DRAFT;
                break;
            default:
                template = DIGITAL_GRANT;
                draftTemplate = DIGITAL_GRANT_DRAFT;
        }

        Document digitalGrantDocument = pdfManagementService.generateAndUpload(callbackRequest, template);
        documents.add(digitalGrantDocument);

        documentService.expire(callbackRequest, draftTemplate);

        if (caseData.isGrantIssuedEmailNotificationRequested()) {
            Document grantIssuedSentEmail = notificationService.sendEmail(GRANT_ISSUED, caseDetails);
            documents.add(grantIssuedSentEmail);
        }

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest, documents));
    }
}
