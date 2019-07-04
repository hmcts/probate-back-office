package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.docmosis.GenericMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT_REISSUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentGeneratorService {

    private final PDFManagementService pdfManagementService;
    private final DocumentService documentService;
    private final GenericMapperService genericMapperService;

    private static final String GRANT_OF_PROBATE = "gop";
    private static final String ADMON_WILL = "admonWill";
    private static final String INTESTACY = "intestacy";
    private static final String EDGE_CASE = "edgeCase";
    private static final String CREST_IMAGE = "GrantOfProbateCrest";
    private static final String SEAL_IMAGE = "GrantOfProbateSeal";
    private static final String CREST_FILE_PATH = "crestImage.txt";
    private static final String SEAL_FILE_PATH = "sealImage.txt";
    private static final String WATERMARK = "draftbackground";
    private static final String WATERMARK_FILE_PATH = "watermarkImage.txt";
    private static final String DRAFT = "preview";
    private static final String FINAL = "final";

    private Map<String, Object> images;

    public Document generateGrantReissue(CallbackRequest callbackRequest, String version) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        images = new HashMap<>();
        images.put(CREST_IMAGE, CREST_FILE_PATH);
        images.put(SEAL_IMAGE, SEAL_FILE_PATH);

        Document document;
        if (version.equals(FINAL)) {
            Map<String, Object> placeholders = genericMapperService.addCaseDataWithImages(images, caseDetails);
            placeholders.put("Signature", "image:base64:" + pdfManagementService.getDecodedSignature());
            document = generateAppropriateDocument(caseDetails, placeholders, FINAL);
        } else {
            images.put(WATERMARK, WATERMARK_FILE_PATH);
            Map<String, Object> placeholders = genericMapperService.addCaseDataWithImages(images, caseDetails);
            document = generateAppropriateDocument(caseDetails, placeholders, DRAFT);
        }

        expireDrafts(callbackRequest);

        return document;
    }

    private void expireDrafts(CallbackRequest callbackRequest) {
        DocumentType[] documentTypes = {DIGITAL_GRANT_DRAFT, INTESTACY_GRANT_DRAFT, ADMON_WILL_GRANT_DRAFT,
                                        DIGITAL_GRANT_DRAFT_REISSUE, INTESTACY_GRANT_DRAFT_REISSUE,
                                        ADMON_WILL_GRANT_DRAFT_REISSUE, DIGITAL_GRANT_REISSUE};
        for (DocumentType documentType : documentTypes) {
            documentService.expire(callbackRequest, documentType);
        }
    }

    private Document generateAppropriateDocument(CaseDetails caseDetails, Map<String, Object> placeholders,
                                                 String version) {
        Document document;
        DocumentType template;
        switch (caseDetails.getData().getCaseType()) {
            case INTESTACY:
                template = INTESTACY_GRANT_DRAFT_REISSUE;
                document = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, template);
                log.info("Generated and Uploaded Intestacy grant {} document with template {} for the case id {}",
                        version, template.getTemplateName(), caseDetails.getId().toString());
                break;
            case ADMON_WILL:
                template = ADMON_WILL_GRANT_DRAFT_REISSUE;
                document = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, template);
                log.info("Generated and Uploaded Admon Will grant {} document with template {} for the case id {}",
                        version, template.getTemplateName(), caseDetails.getId().toString());
                break;
            case EDGE_CASE:
                document = Document.builder().documentType(DocumentType.EDGE_CASE).build();
                break;
            case GRANT_OF_PROBATE:
            default:
                template = version.equals(FINAL) ? DIGITAL_GRANT_REISSUE : DIGITAL_GRANT_DRAFT_REISSUE;
                document = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, template);
                log.info("Generated and Uploaded Grant of Probate {} document with template {} for the case id {}",
                        version, template.getTemplateName(), caseDetails.getId().toString());
                break;
        }

        return document;
    }
}
