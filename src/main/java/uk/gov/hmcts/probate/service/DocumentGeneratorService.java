package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.docmosis.GenericMapperService;
import uk.gov.hmcts.probate.service.docmosis.PreviewLetterService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentGeneratorService {

    private final PDFManagementService pdfManagementService;
    private final DocumentService documentService;
    private final GenericMapperService genericMapperService;
    private final PreviewLetterService previewLetterService;

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
    private static final String FULL_REDEC = "fullRedec";
    private static final String APP_NAME = "applicantName";

    public Document generateGrantReissue(CallbackRequest callbackRequest, String version) {
        Map<String, Object> images;

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        images = new HashMap<>();
        images.put(CREST_IMAGE, CREST_FILE_PATH);
        images.put(SEAL_IMAGE, SEAL_FILE_PATH);

        Document document;
        if (version.equals(FINAL)) {
            log.info("Generating Grant document");
            Map<String, Object> placeholders = genericMapperService.addCaseDataWithImages(images, caseDetails);
            placeholders.put("Signature", "image:base64:" + pdfManagementService.getDecodedSignature());
            document = generateAppropriateDocument(caseDetails, placeholders, FINAL);
        } else {
            images.put(WATERMARK, WATERMARK_FILE_PATH);
            Map<String, Object> placeholders = genericMapperService.addCaseDataWithImages(images, caseDetails);
            document = generateAppropriateDocument(caseDetails, placeholders, DRAFT);
        }

        expireDrafts(callbackRequest);

        log.info("Grant generated: {}", document.getDocumentFileName());
        return document;
    }

    public Document generateCoversheet(CallbackRequest callbackRequest) {
        return generateCoversheet(callbackRequest,
                callbackRequest.getCaseDetails().getData().getPrimaryApplicantFullName(),
                callbackRequest.getCaseDetails().getData().getPrimaryApplicantAddress());
    }


    public Document generateCoversheet(CallbackRequest callbackRequest, String name, SolsAddress address) {

        log.info("Initiate call to generate coversheet for case id {} ",
                callbackRequest.getCaseDetails().getId());
        Map<String, Object> placeholders = genericMapperService.addCaseData(callbackRequest.getCaseDetails().getData());
        genericMapperService.appendExecutorDetails(placeholders, name, address);
        Document coversheet = pdfManagementService
                .generateDocmosisDocumentAndUpload(placeholders, DocumentType.GRANT_COVERSHEET);
        log.info("Successful response for coversheet for case id {} ",
                callbackRequest.getCaseDetails().getId());

        return coversheet;
    }

    public Document generateRequestForInformation(CaseDetails caseDetails, ExecutorsApplyingNotification exec) {
        log.info("Initiate call to generate information request letter for case id {}", caseDetails.getId());
        Map<String, Object> placeholders = genericMapperService.addCaseDataWithRegistryProperties(caseDetails);
        String appName;

        if (caseDetails.getData().getApplicationType() == ApplicationType.SOLICITOR) {
            appName = caseDetails.getData().getSolsSOTName();
        } else {
            appName = exec.getName();
        }

        placeholders.put(APP_NAME, appName);
        placeholders.put(FULL_REDEC, NO);

        Document letter = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders,
                DocumentType.SOT_INFORMATION_REQUEST);
        log.info("Successful response for letter for case id {}", caseDetails.getId());

        return letter;
    }

    public Document generateSoT(CallbackRequest callbackRequest) {
        Document statementOfTruth;

        switch (callbackRequest.getCaseDetails().getData().getApplicationType()) {
            case SOLICITOR:
                log.info("Initiate call to generate SoT for case id: {}", callbackRequest.getCaseDetails().getId());
                statementOfTruth = generateSolicitorSoT(callbackRequest);
                log.info("Successful response for SoT for case id: {}", callbackRequest.getCaseDetails().getId());
                break;
            case PERSONAL:
            default:
                log.info("Initiate call to generate SoT for case id: {}", callbackRequest.getCaseDetails().getId());
                Map<String, Object> placeholders =
                        genericMapperService.addCaseDataWithRegistryProperties(callbackRequest.getCaseDetails());
                statementOfTruth = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders,
                        DocumentType.STATEMENT_OF_TRUTH);
                log.info("Successful response for SoT for case id: {}", callbackRequest.getCaseDetails().getId());
                break;
        }

        return statementOfTruth;
    }

    public Document generateLetter(CallbackRequest callbackRequest, boolean forFinal) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();

        Map<String, Object> placeholders = previewLetterService.addLetterData(caseDetails);
        if (!forFinal) {
            Map<String, Object> images = new HashMap<>();
            images.put(WATERMARK, WATERMARK_FILE_PATH);
            Map<String, Object> mappedImages = genericMapperService.mappedBase64Images(images);
            placeholders.putAll(mappedImages);
        }

        Document letterDocument = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders,
                DocumentType.ASSEMBLED_LETTER);
        return letterDocument;
    }

    private Document generateSolicitorSoT(CallbackRequest callbackRequest) {
        Document statementOfTruth;
        switch (callbackRequest.getCaseDetails().getData().getCaseType()) {
            case ADMON_WILL:
                statementOfTruth = pdfManagementService.generateAndUpload(callbackRequest, LEGAL_STATEMENT_ADMON);
                break;
            case INTESTACY:
                statementOfTruth = pdfManagementService.generateAndUpload(callbackRequest, LEGAL_STATEMENT_INTESTACY);
                break;
            case GRANT_OF_PROBATE:
            default:
                statementOfTruth = pdfManagementService.generateAndUpload(callbackRequest, LEGAL_STATEMENT_PROBATE);
                break;
        }
        return statementOfTruth;
    }


    private void expireDrafts(CallbackRequest callbackRequest) {
        log.info("Expiring drafts");
        DocumentType[] documentTypes = {DIGITAL_GRANT_DRAFT, INTESTACY_GRANT_DRAFT, ADMON_WILL_GRANT_DRAFT,
                                        DIGITAL_GRANT_REISSUE_DRAFT, INTESTACY_GRANT_REISSUE_DRAFT,
                                        ADMON_WILL_GRANT_REISSUE_DRAFT};
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
                template = version.equals(FINAL) ? INTESTACY_GRANT_REISSUE : INTESTACY_GRANT_REISSUE_DRAFT;
                document = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, template);
                log.info("Generated and Uploaded Intestacy grant {} document with template {} for the case id {}",
                        version, template.getTemplateName(), caseDetails.getId().toString());
                break;
            case ADMON_WILL:
                template = version.equals(FINAL) ? ADMON_WILL_GRANT_REISSUE : ADMON_WILL_GRANT_REISSUE_DRAFT;
                document = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, template);
                log.info("Generated and Uploaded Admon Will grant {} document with template {} for the case id {}",
                        version, template.getTemplateName(), caseDetails.getId().toString());
                break;
            case EDGE_CASE:
                document = Document.builder().documentType(DocumentType.EDGE_CASE).build();
                break;
            case GRANT_OF_PROBATE:
            default:
                template = version.equals(FINAL) ? DIGITAL_GRANT_REISSUE : DIGITAL_GRANT_REISSUE_DRAFT;
                document = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, template);
                log.info("Generated and Uploaded Grant of Probate {} document with template {} for the case id {}",
                        version, template.getTemplateName(), caseDetails.getId().toString());
                break;
        }

        return document;
    }

}
