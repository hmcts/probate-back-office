package uk.gov.hmcts.probate.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.WillDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentCaseType.INTESTACY;

@Service
@Slf4j
@AllArgsConstructor
public class FindWillsService {

    public List<Document> findDefaultOrSelectedWills(CaseData caseData) {
        List<Document> willDocuments;
        if (YES.equals(caseData.getHasMultipleWills())) {
            willDocuments = findSelectedWills(caseData);
        } else {
            willDocuments = findWills(caseData);
        }
        return willDocuments;
    }

    public List<Document> findWills(CaseData caseData) {
        List<Document> wills = new ArrayList<>();
        if (!caseData.getCaseType().equals(INTESTACY.getCaseType())) {
            Optional<List<CollectionMember<UploadDocument>>> optionalUploaded =
                Optional.ofNullable(caseData.getBoDocumentsUploaded());
            for (CollectionMember<UploadDocument> document : optionalUploaded.orElse(emptyList())) {
                if (isUploadedDocTypePermissible(document)) {
                    wills.add(buildUploadedDocument(document.getValue()));
                }
            }
            Optional<List<CollectionMember<ScannedDocument>>> optionalScanned =
                Optional.ofNullable(caseData.getScannedDocuments());
            for (CollectionMember<ScannedDocument> document : optionalScanned.orElse(emptyList())) {
                if (isScannedDocTypePermissible(document)) {
                    wills.add(buildScannedDocument(document.getValue()));
                }
            }

        }

        return wills;
    }

    private boolean isUploadedDocTypePermissible(CollectionMember<UploadDocument> document) {
        return (document.getValue().getDocumentType() == DocumentType.WILL
            || document.getValue().getDocumentType() == DocumentType.OTHER
            || document.getValue().getDocumentType() == DocumentType.IHT);

    }

    private boolean isScannedDocTypePermissible(CollectionMember<ScannedDocument> document) {
        return (DocumentType.OTHER.getTemplateName().equalsIgnoreCase(document.getValue().getType())
            && DOC_SUBTYPE_WILL.equalsIgnoreCase(document.getValue().getSubtype()));
    }

    private List<Document> findSelectedWills(CaseData caseData) {
        List<Document> documents = new ArrayList<>();
        for (CollectionMember<WillDocument> collectionMember : caseData.getWillSelection()) {
            if (isDocumentSelected(collectionMember.getValue())) {
                documents.add(findDocumentByBinaryURL(caseData,
                    collectionMember.getValue().getDocumentLink().getDocumentBinaryUrl()));
            }
        }

        return documents;
    }

    private boolean isDocumentSelected(WillDocument willDocument) {
        return willDocument.getDocumentSelected().contains(YES);
    }

    private Document findDocumentByBinaryURL(CaseData caseData, String documentBinaryUrl) {
        for (CollectionMember<UploadDocument> collectionMember : caseData.getBoDocumentsUploaded()) {
            UploadDocument uploadDocument = collectionMember.getValue();
            if (documentBinaryUrl.equals(uploadDocument.getDocumentLink().getDocumentBinaryUrl())) {
                return buildUploadedDocument(uploadDocument);
            }
        }
        for (CollectionMember<ScannedDocument> collectionMember : caseData.getScannedDocuments()) {
            ScannedDocument scannedDocument = collectionMember.getValue();
            if (documentBinaryUrl.equals(scannedDocument.getUrl().getDocumentBinaryUrl())) {
                return buildScannedDocument(scannedDocument);
            }
        }
        return null;
    }

    private Document buildUploadedDocument(UploadDocument uploadDocument) {
        return Document.builder()
            .documentType(uploadDocument.getDocumentType())
            .documentLink(uploadDocument.getDocumentLink())
            .documentFileName(uploadDocument.getDocumentLink().getDocumentFilename())
            .build();
    }

    private Document buildScannedDocument(ScannedDocument scannedDocument) {
        DocumentType documentType = DocumentType.OTHER;
        if (DOC_SUBTYPE_WILL.equalsIgnoreCase(scannedDocument.getSubtype())) {
            documentType = DocumentType.WILL;
        }
        return Document.builder()
            .documentType(documentType)
            .documentLink(scannedDocument.getUrl())
            .documentDateAdded(scannedDocument.getScannedDate().toLocalDate())
            .documentFileName(scannedDocument.getFileName())
            .build();
    }

}
