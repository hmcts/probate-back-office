package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FindWillsService {

    public List<Document> findWills(CaseData caseData) {
        List<Document> wills = new ArrayList<>();
        if (!caseData.getCaseType().equals(DocumentCaseType.INTESTACY.getCaseType())) {
            if (caseData.getBoDocumentsUploaded() != null) {
                for (CollectionMember<UploadDocument> document : caseData.getBoDocumentsUploaded()) {
                    if (document.getValue().getDocumentType() == DocumentType.WILL) {
                        wills.add(buildDocument(document.getValue()));
                    }
                }
            }
            if (caseData.getScannedDocuments() != null) {
                for (CollectionMember<ScannedDocument> document : caseData.getScannedDocuments()) {
                    if ("Other".equalsIgnoreCase(document.getValue().getType()) && "will".equalsIgnoreCase(document.getValue().getSubtype())) {
                        wills.add(buildDocument(document.getValue()));
                    }
                }
            }

        }

        return wills;
    }

    private Document buildDocument(UploadDocument uploadDocument) {
        return Document.builder()
            .documentType(uploadDocument.getDocumentType())
            .documentLink(uploadDocument.getDocumentLink())
            .documentFileName(uploadDocument.getDocumentLink().getDocumentFilename())
            .build();
    }

    private Document buildDocument(ScannedDocument scannedDocument) {
        return Document.builder()
            .documentType(DocumentType.OTHER)
            .documentLink(scannedDocument.getUrl())
            .documentFileName(scannedDocument.getFileName())
            .build();
    }
}
