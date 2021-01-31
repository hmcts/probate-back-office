package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReprintService {
    public static final String LABEL_GRANT = "Grant";
    public static final String LABEL_REISSUED_GRANT = "ReissuedGrant";
    public static final String LABEL_WILL = "Will";
    public static final String LABEL_SOT = "SOT";
    public static final String WILL_DOC_TYPE = "Other";
    public static final String WILL_DOC_SUB_TYPE = "Will";
    @Autowired
    private final BulkPrintService bulkPrintService;
    private final PDFManagementService pdfManagementService;
    private final CallbackResponseTransformer callbackResponseTransformer;

    public CallbackResponse reprintSelectedDocument(CallbackRequest callbackRequest) {

        DynamicListItem selectedDocumentItem =
            callbackRequest.getCaseDetails().getData().getReprintDocument().getValue();
        if (selectedDocumentItem == null || selectedDocumentItem.getCode() == null) {
            throw new BadRequestException("No selection made for document to reprint");
        }
        Document coverSheet = pdfManagementService.generateAndUpload(callbackRequest, DocumentType.GRANT_COVER);
        Document selectedDocument = findDocument(selectedDocumentItem, callbackRequest.getCaseDetails().getData());
        SendLetterResponse response =
            bulkPrintService.sendDocumentsForReprint(callbackRequest, selectedDocument, coverSheet);
        String letterId = response != null ? response.letterId.toString() : null;
        String pdfSize =
            String.valueOf(Integer.parseInt(callbackRequest.getCaseDetails().getData().getReprintNumberOfCopies()) + 1);

        log.info("Adding BP info for case={}, docType.template={}, letterId={}, pdfSize={}",
            callbackRequest.getCaseDetails().getId(),
            selectedDocument.getDocumentType().getTemplateName(), letterId, pdfSize);
        return callbackResponseTransformer
            .addBulkPrintInformationForReprint(callbackRequest, selectedDocument, letterId, pdfSize);
    }

    private Document findDocument(DynamicListItem selectedDocumentItem, CaseData data) {
        if (LABEL_WILL.equalsIgnoreCase(selectedDocumentItem.getLabel())) {
            String fileName = selectedDocumentItem.getCode();
            Optional<CollectionMember<ScannedDocument>> scannedDocument = data.getScannedDocuments().stream()
                .filter(doc -> doc.getValue().getFileName().equals(fileName))
                .findFirst();
            if (scannedDocument.isPresent()) {
                return Document.builder()
                    .documentType(DocumentType.OTHER)
                    .documentLink(scannedDocument.get().getValue().getUrl())
                    .documentFileName(scannedDocument.get().getValue().getFileName())
                    .build();
            }
        } else if (LABEL_GRANT.equalsIgnoreCase(selectedDocumentItem.getLabel()) ||
            LABEL_REISSUED_GRANT.equalsIgnoreCase(selectedDocumentItem.getLabel())) {
            String fileName = selectedDocumentItem.getCode();
            Optional<CollectionMember<Document>> document = data.getProbateDocumentsGenerated().stream()
                .filter(doc -> doc.getValue().getDocumentFileName().equals(fileName))
                .findFirst();
            if (document.isPresent()) {
                return document.get().getValue();
            }
        } else if (LABEL_SOT.equalsIgnoreCase(selectedDocumentItem.getLabel())
            && data.getProbateSotDocumentsGenerated() != null) {
            String fileName = selectedDocumentItem.getCode();
            Optional<CollectionMember<Document>> document = data.getProbateSotDocumentsGenerated().stream()
                .filter(doc -> doc.getValue().getDocumentFileName().equals(fileName))
                .findFirst();
            if (document.isPresent()) {
                return document.get().getValue();
            }
        }

        throw new BadRequestException("Could not find document to reprint");
    }
}
