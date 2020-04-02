package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
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
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReprintService {
    @Autowired
    private final BulkPrintService bulkPrintService;
    private final PDFManagementService pdfManagementService;

    public static final String LABEL_GRANT = "Grant";
    public static final String LABEL_REISSUED_GRANT = "ReissuedGrant";
    public static final String LABEL_WILL = "Will";
    public static final String LABEL_SOT = "SOT";
    public static final String WILL_DOC_TYPE = "Other";
    public static final String WILL_DOC_SUB_TYPE = "Will";

    public void reprintSelectedDocument(CallbackRequest callbackRequest) {

        DynamicListItem selectedDocumentItem = callbackRequest.getCaseDetails().getData().getReprintDocument().getValue();
        if (selectedDocumentItem == null || selectedDocumentItem.getCode() == null) {
            throw new BadRequestException("No selection made for document to reprint");
        }
        Document coverSheet = pdfManagementService.generateAndUpload(callbackRequest, DocumentType.GRANT_COVER);
        Document selectedDocument = findDocument(selectedDocumentItem, callbackRequest.getCaseDetails().getData());
        bulkPrintService.sendDocumentForReprint(callbackRequest, selectedDocument, coverSheet);
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
            LABEL_REISSUED_GRANT.equalsIgnoreCase(selectedDocumentItem.getLabel()) ||
            LABEL_SOT.equalsIgnoreCase(selectedDocumentItem.getLabel())) {
            String fileName = selectedDocumentItem.getCode();
            Optional<CollectionMember<Document>> document = data.getProbateDocumentsGenerated().stream()
                .filter(doc -> doc.getValue().getDocumentFileName().equals(fileName))
                .findFirst();
            if (document.isPresent()) {
                return document.get().getValue();
            }
        }

        throw new BadRequestException("Could not find document to reprint");
    }
}
