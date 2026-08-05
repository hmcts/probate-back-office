package uk.gov.hmcts.probate.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.NotFoundException;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.DocumentIssueType;
import uk.gov.hmcts.probate.model.DocumentStatus;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.OriginalDocuments;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchData;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.service.docmosis.DocumentTemplateService;
import uk.gov.hmcts.probate.service.docmosis.GenericMapperService;
import uk.gov.hmcts.probate.service.docmosis.PreviewLetterService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.service.template.pdf.PlaceholderDecorator;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_REISSUE_DRAFT;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentGeneratorService {

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
    private static final String LETTER_TYPE = "letterType";
    private static final String BLANK = "blank";
    private static final String TEMPLATE = "template";
    private final PlaceholderDecorator placeholderDecorator;
    private final PDFManagementService pdfManagementService;
    private final DocumentService documentService;
    private final GenericMapperService genericMapperService;
    private final PreviewLetterService previewLetterService;
    private final DocumentTemplateService documentTemplateService;
    private final CaseDataTransformer caseDataTransformer;

    private Document generateGrant(CallbackRequest callbackRequest, DocumentStatus status,
                                   DocumentIssueType issueType) {
        return getDocument(callbackRequest, status, Optional.of(issueType));
    }

    public Document generateGrantReissue(CallbackRequest callbackRequest, DocumentStatus status,
                                         Optional<DocumentIssueType> issueType) {
        return getDocument(callbackRequest, status, issueType);
    }

    public Document getDocument(CallbackRequest callbackRequest, DocumentStatus documentStatus,
                                DocumentIssueType documentIssueType) {
        Document document;
        if (callbackRequest.getCaseDetails().getData().isLanguagePreferenceWelsh()) {
            document = generateGrant(callbackRequest, documentStatus, documentIssueType);
        } else {
            document = getPDFGrant(callbackRequest, documentStatus, documentIssueType);
        }
        return document;
    }

    private Document getDocument(CallbackRequest callbackRequest, DocumentStatus status,
                                 Optional<DocumentIssueType> issueType) {
        Map<String, Object> images;

        images = new HashMap<>();
        images.put(CREST_IMAGE, CREST_FILE_PATH);
        images.put(SEAL_IMAGE, SEAL_FILE_PATH);

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        Document document;

        if (status == DocumentStatus.FINAL) {
            log.info("Generating Grant document");
            Map<String, Object> placeholders = genericMapperService.addCaseDataWithImages(images, caseDetails);
            placeholderDecorator.decorate(placeholders);
            placeholders.put("Signature", "image:base64:" + pdfManagementService.getDecodedSignature());
            document = generateAppropriateDocument(caseDetails, placeholders, status, issueType);
        } else {
            images.put(WATERMARK, WATERMARK_FILE_PATH);
            Map<String, Object> placeholders = genericMapperService.addCaseDataWithImages(images, caseDetails);
            placeholderDecorator.decorate(placeholders);
            document = generateAppropriateDocument(caseDetails, placeholders, status, issueType);
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

    public Document generateSoT(CallbackRequest callbackRequest) {
        final Document statementOfTruth;
        DocumentType documentType = DocumentType.STATEMENT_OF_TRUTH;
        final var cd = callbackRequest.getCaseDetails();
        switch (callbackRequest.getCaseDetails().getData().getApplicationType()) {
            case SOLICITOR:
                // Transform case data into expected format for legal statement
                caseDataTransformer.transformCaseDataForLegalStatementRegeneration(callbackRequest);
                log.info("Initiate call to generate SoT for case id: {}", cd.getId());
                statementOfTruth = generateSolicitorSoT(callbackRequest);
                log.info("Successful response for SoT for case id: {}", cd.getId());
                break;
            case PERSONAL:
            default:
                log.info("Initiate call to generate SoT for case id: {}", cd.getId());
                Map<String, Object> placeholders = genericMapperService.addCaseDataWithRegistryProperties(cd);
                if (cd.getData().isLanguagePreferenceWelsh()) {
                    placeholderDecorator.decorate(placeholders);
                    documentType = DocumentType.WELSH_STATEMENT_OF_TRUTH;
                }
                statementOfTruth = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, documentType);
                log.info("Successful response for SoT for case id: {}", cd.getId());
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

        if (BLANK.equals(placeholders.get(LETTER_TYPE))) {
            return pdfManagementService.generateDocmosisDocumentAndUpload(placeholders,
                DocumentType.BLANK_LETTER);
        } else if (TEMPLATE.equals(placeholders.get(LETTER_TYPE))) {
            return pdfManagementService.generateDocmosisDocumentAndUpload(placeholders,
                DocumentType.ASSEMBLED_LETTER);
        } else {
            throw new NotFoundException("Unable to determine LETTER_TYPE");
        }
    }

    public DocumentType getSolicitorSoTDocType(CallbackRequest callbackRequest) {
        DocumentType documentType;
        switch (callbackRequest.getCaseDetails().getData().getCaseType()) {
            case ADMON_WILL:
                documentType = LEGAL_STATEMENT_ADMON;
                break;
            case INTESTACY:
                documentType = LEGAL_STATEMENT_INTESTACY;
                break;
            case GRANT_OF_PROBATE:
            default:
                String schemaVersion = callbackRequest.getCaseDetails().getData().getSchemaVersion();
                // Set document version to newer trust corp legal statement for cases with 2.0.0 schema version
                documentType = schemaVersion != null && schemaVersion.equals("2.0.0")
                    ? LEGAL_STATEMENT_PROBATE_TRUST_CORPS : LEGAL_STATEMENT_PROBATE;
                break;
        }
        return documentType;
    }

    private Document generateSolicitorSoT(CallbackRequest callbackRequest) {
        Document statementOfTruth;
        DocumentType documentType = getSolicitorSoTDocType(callbackRequest);
        statementOfTruth = pdfManagementService.generateAndUpload(callbackRequest, documentType);
        return statementOfTruth;
    }

    private void expireDrafts(CallbackRequest callbackRequest) {
        log.info("Expiring drafts");
        DocumentType[] documentTypes = {DIGITAL_GRANT_DRAFT, INTESTACY_GRANT_DRAFT, ADMON_WILL_GRANT_DRAFT,
            AD_COLLIGENDA_BONA_GRANT_DRAFT, DIGITAL_GRANT_REISSUE_DRAFT, INTESTACY_GRANT_REISSUE_DRAFT,
            ADMON_WILL_GRANT_REISSUE_DRAFT, AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT, WELSH_DIGITAL_GRANT_DRAFT,
            WELSH_ADMON_WILL_GRANT_DRAFT, WELSH_INTESTACY_GRANT_DRAFT, WELSH_AD_COLLIGENDA_BONA_GRANT_DRAFT,
            WELSH_DIGITAL_GRANT_REISSUE_DRAFT, WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT,
            WELSH_INTESTACY_GRANT_REISSUE_DRAFT, WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT};
        for (DocumentType documentType : documentTypes) {
            documentService.expire(callbackRequest, documentType);
        }
    }

    public void permanentlyDeleteRemovedDocumentsForGrant(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        String caseRef = callbackRequest.getCaseDetails().getId().toString();

        OriginalDocuments originalDocuments = caseData.getOriginalDocuments();
        permanentlyDeleteRemovedDocuments(originalDocuments.getOriginalDocsGenerated(),
                caseData.getProbateDocumentsGenerated(),
                originalDocuments.getOriginalDocsUploaded(), caseData.getBoDocumentsUploaded(),
                originalDocuments.getOriginalDocsScanned(), caseData.getScannedDocuments(),
                caseRef);
    }

    public void permanentlyDeleteRemovedDocumentsForCaveat(CaveatCallbackRequest callbackRequest) {
        CaveatData caseData = callbackRequest.getCaseDetails().getData();
        String caseRef = callbackRequest.getCaseDetails().getId().toString();
        OriginalDocuments originalDocuments = caseData.getOriginalDocuments();

        permanentlyDeleteRemovedDocuments(originalDocuments.getOriginalDocsGenerated(),
                caseData.getDocumentsGenerated(),
                originalDocuments.getOriginalDocsUploaded(), caseData.getDocumentsUploaded(),
                originalDocuments.getOriginalDocsScanned(), caseData.getScannedDocuments(),
                caseRef);
    }

    public void permanentlyDeleteRemovedDocumentsForStandingSearch(StandingSearchCallbackRequest callbackRequest) {
        StandingSearchData caseData = callbackRequest.getCaseDetails().getData();
        String caseRef = callbackRequest.getCaseDetails().getId().toString();
        OriginalDocuments originalDocuments = caseData.getOriginalDocuments();

        permanentlyDeleteRemovedDocuments(null,null,
                originalDocuments.getOriginalDocsUploaded(), caseData.getDocumentsUploaded(),
                null, null,
                caseRef);
    }

    public void permanentlyDeleteRemovedDocumentsForWillLodgement(WillLodgementCallbackRequest callbackRequest) {
        WillLodgementData caseData = callbackRequest.getCaseDetails().getData();
        String caseRef = callbackRequest.getCaseDetails().getId().toString();
        OriginalDocuments originalDocuments = caseData.getOriginalDocuments();

        permanentlyDeleteRemovedDocuments(originalDocuments.getOriginalDocsGenerated(),
                caseData.getDocumentsGenerated(),
                originalDocuments.getOriginalDocsUploaded(), caseData.getDocumentsUploaded(),
                null, null,
                caseRef);
    }

    private void permanentlyDeleteRemovedDocuments(List<CollectionMember<Document>> originalGenerated,
                                                  List<CollectionMember<Document>> remainingGenerated,
                                                  List<CollectionMember<UploadDocument>> originalUploaded,
                                                  List<CollectionMember<UploadDocument>> remainingUploaded,
                                                  List<CollectionMember<ScannedDocument>> originalScanned,
                                                  List<CollectionMember<ScannedDocument>> remainingScanned,
                                                  String caseId) {
        log.info("attempting to permanently delete removed documents on case: {}", caseId);

        List<Document> documentsToDelete = new ArrayList<>();
        for (CollectionMember<Document> documentCollectionMember : ofNullable(originalGenerated).orElse(emptyList())) {
            if (!remainingGenerated.contains(documentCollectionMember)) {
                log.info("permanently removing generated document: {}", documentCollectionMember.getId());
                documentsToDelete.add(documentCollectionMember.getValue());
            }
        }
        for (CollectionMember<UploadDocument> documentCollectionMember :
                ofNullable(originalUploaded).orElse(emptyList())) {
            if (!remainingUploaded.contains(documentCollectionMember)) {
                Document document = Document.builder()
                        .documentLink(documentCollectionMember.getValue().getDocumentLink())
                        .documentType(documentCollectionMember.getValue().getDocumentType())
                        .build();
                log.info("permanently removing uploaded document: {}", documentCollectionMember.getId());
                documentsToDelete.add(document);
            }
        }
        for (CollectionMember<ScannedDocument> documentCollectionMember :
                ofNullable(originalScanned).orElse(emptyList())) {
            if (!remainingScanned.contains(documentCollectionMember)) {
                Document document = Document.builder()
                        .documentLink(documentCollectionMember.getValue().getUrl())
                        .documentType(DocumentType.lookup(documentCollectionMember.getValue().getType()))
                        .build();
                log.info("permanently removing scanned document: {}", documentCollectionMember.getId());
                documentsToDelete.add(document);
            }
        }

        for (Document document : documentsToDelete) {
            documentService.delete(document, caseId);
        }
    }

    private Document generateAppropriateDocument(CaseDetails caseDetails, Map<String, Object> placeholders,
                                                 DocumentStatus status, Optional<DocumentIssueType> issueType) {
        Document document;
        if (caseDetails.getData().getCaseType().equals(EDGE_CASE)) {
            document = Document.builder().documentType(DocumentType.EDGE_CASE).build();
        } else {
            DocumentIssueType documentIssueType = issueType.orElse(DocumentIssueType.GRANT);
            DocumentType template = getDocumentType(caseDetails, status, documentIssueType);
            document = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, template);
            log.info("For the case id {}, generated {} grant with  status {}, issue type {} and case type {} ",
                caseDetails.getId(), caseDetails.getData().getLanguagePreference(), status, documentIssueType,
                caseDetails.getData().getCaseType());
        }
        return document;
    }

    private Document getPDFGrant(CallbackRequest callbackRequest, DocumentStatus status, DocumentIssueType issueType) {
        Document document;
        if (callbackRequest.getCaseDetails().getData().getCaseType().equals(EDGE_CASE)) {
            document = Document.builder().documentType(DocumentType.EDGE_CASE).build();
        } else {
            DocumentType template = getDocumentType(callbackRequest.getCaseDetails(), status, issueType);
            document = pdfManagementService.generateAndUpload(callbackRequest, template);
            log.info("Generated and Uploaded {} {} document with template {} for the case id {}",
                callbackRequest.getCaseDetails().getData().getCaseType(), status,
                template.getTemplateName(), callbackRequest.getCaseDetails().getId().toString());
        }
        expireDrafts(callbackRequest);
        return document;
    }

    private DocumentType getDocumentType(CaseDetails caseDetails, DocumentStatus status, DocumentIssueType issueType) {
        return documentTemplateService.getTemplateId(caseDetails.getData().getLanguagePreference(),
            status,
            issueType,
            DocumentCaseType.getCaseType(caseDetails.getData().getCaseType()));
    }
}
