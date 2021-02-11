package uk.gov.hmcts.probate.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.WillDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Service
@Slf4j
@AllArgsConstructor
public class FindWillsService {

    public List<Document> findWills(CaseData caseData) {
        List<Document> wills = new ArrayList<>();
        if (!caseData.getCaseType().equals(DocumentCaseType.INTESTACY.getCaseType())) {
            if (caseData.getBoDocumentsUploaded() != null) {
                for (CollectionMember<UploadDocument> document : caseData.getBoDocumentsUploaded()) {
                    if (document.getValue().getDocumentType() == DocumentType.WILL || document.getValue().getDocumentType() == DocumentType.OTHER
                        || document.getValue().getDocumentType() == DocumentType.IHT) {
                        wills.add(buildUploadedDocument(document.getValue()));
                    }
                }
            }
            if (caseData.getScannedDocuments() != null) {
                for (CollectionMember<ScannedDocument> document : caseData.getScannedDocuments()) {
                    if (DocumentType.OTHER.getTemplateName().equalsIgnoreCase(document.getValue().getType()) && DOC_SUBTYPE_WILL.equalsIgnoreCase(document.getValue().getSubtype())) {
                        wills.add(buildScannedDocument(document.getValue()));
                    }
                }
            }

        }

        return wills;
    }

    public List<Document> findSelectedWills(CaseData caseData) {
        List<Document> documents = new ArrayList<>();
        for (CollectionMember<WillDocument> collectionMember : caseData.getWillSelection()) {
            if (YES.equals(collectionMember.getValue().getDocumentSelected())) {
                Document doc = findDocumentByBinaryURL(caseData, collectionMember.getValue().getDocumentLink().getDocumentBinaryUrl());
                if (doc != null) {
                    documents.add(doc);
                }
            }
        }
       
        return documents;
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
            .documentFileName(scannedDocument.getFileName())
            .build();
    }
}
