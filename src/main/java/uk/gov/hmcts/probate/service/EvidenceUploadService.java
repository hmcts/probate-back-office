package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class EvidenceUploadService {

    private static final String DOCUMENT_MODIFICATION_NOT_ALLOWED_ERROR =
            "You have removed or modified a document that was previously uploaded to this application.";

    public void updateLastEvidenceAddedDate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        log.info("Updating lastEvidenceAddedDate for case {}", caseDetails.getId());
        caseData.setLastEvidenceAddedDate(LocalDate.now());
    }

    public void validateExistingUploadedDocuments(CallbackRequest callbackRequest) {
        List<CollectionMember<UploadDocument>> documentsBefore =
                Optional.ofNullable(callbackRequest.getCaseDetailsBefore())
                .map(CaseDetails::getData)
                .map(CaseData::getBoDocumentsUploaded)
                .orElse(Collections.emptyList());
        if (documentsBefore.isEmpty()) {
            return;
        }

        List<CollectionMember<UploadDocument>> documentsAfter = Optional.ofNullable(callbackRequest.getCaseDetails())
                .map(CaseDetails::getData)
                .map(CaseData::getBoDocumentsUploaded)
                .orElse(Collections.emptyList());

        Map<String, UploadDocument> afterById = new HashMap<>();
        for (CollectionMember<UploadDocument> document : documentsAfter) {
            if (document != null && document.getId() != null) {
                afterById.put(document.getId(), document.getValue());
            }
        }

        for (CollectionMember<UploadDocument> existingDocument : documentsBefore) {
            if (existingDocument == null || existingDocument.getId() == null) {
                continue;
            }
            String existingDocumentId = existingDocument.getId();
            boolean hasAfterDocument = afterById.containsKey(existingDocumentId);
            UploadDocument afterDocument = hasAfterDocument ? afterById.get(existingDocumentId) : null;
            if (!hasAfterDocument
                    || !Objects.equals(existingDocument.getValue(), afterDocument)) {
                Long caseId = Optional.ofNullable(callbackRequest.getCaseDetails())
                        .map(CaseDetails::getId)
                        .orElse(null);
                log.error("Document with ID {} has been modified or removed for case {}",
                        existingDocumentId,
                        caseId);
                throw new BusinessValidationException(
                        DOCUMENT_MODIFICATION_NOT_ALLOWED_ERROR,
                        DOCUMENT_MODIFICATION_NOT_ALLOWED_ERROR
                );
            }
        }
    }
}
