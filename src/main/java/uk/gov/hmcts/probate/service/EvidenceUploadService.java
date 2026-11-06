package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_OTHER;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.NO;

@Slf4j
@RequiredArgsConstructor
@Component
public class EvidenceUploadService {

    private static final String DOCUMENT_MODIFICATION_NOT_ALLOWED_ERROR =
            "You have removed or modified a document that was previously uploaded to this application.";

    private static final String NO_DOCUMENT_RESOLVABLE_ERROR =
            "No document exists with a resolvable document URL for this document. "
                    + "Please check the document has been uploaded correctly.";

    private static final String MISSING_CASE_DETAILS_ERROR = "Case details are missing for this validation request.";

    private static final String NO_FINAL_WILL_ERROR =
            "There are multiple Will documents attached to this case. "
                    + "Please select one as the Final Will before issuing the grant.";

    private static final String MULTIPLE_FINAL_WILLS_ERROR =
            "More than one Will document has been marked as the Final Will. "
                    + "Please select only one before issuing the grant.";

    public void updateLastEvidenceAddedDate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        log.info("Updating lastEvidenceAddedDate for case {}", caseDetails.getId());
        caseData.setLastEvidenceAddedDate(LocalDate.now());
    }

    public void validateExistingUploadedDocuments(CallbackRequest callbackRequest) {
        if (callbackRequest == null || callbackRequest.getCaseDetails() == null
                || callbackRequest.getCaseDetailsBefore() == null) {
            throw new BusinessValidationException(
                    MISSING_CASE_DETAILS_ERROR,
                    MISSING_CASE_DETAILS_ERROR
            );
        }

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

        Set<String> afterDocumentUrls = documentsAfter.stream()
                .map(this::getDocumentUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Long caseId = callbackRequest.getCaseDetails().getId();
        for (CollectionMember<UploadDocument> existingDocument : documentsBefore) {
            String existingDocumentUrl = getDocumentUrl(existingDocument);
            if (existingDocumentUrl == null) {
                log.error("Skipping validation for uploaded document id {} on case {} - no resolvable document URL",
                        existingDocument.getId(),
                        caseId);
                throw new BusinessValidationException(
                        NO_DOCUMENT_RESOLVABLE_ERROR,
                        NO_DOCUMENT_RESOLVABLE_ERROR
                );
            }
            if (!afterDocumentUrls.contains(existingDocumentUrl)) {
                log.error("Uploaded Document with URL {} has been removed or replaced for case {}",
                        existingDocumentUrl,
                        caseId);
                throw new BusinessValidationException(
                        DOCUMENT_MODIFICATION_NOT_ALLOWED_ERROR,
                        DOCUMENT_MODIFICATION_NOT_ALLOWED_ERROR
                );
            }
        }
    }

    public void validateExistingScannedDocuments(CallbackRequest callbackRequest) {
        if (callbackRequest == null || callbackRequest.getCaseDetails() == null
                || callbackRequest.getCaseDetailsBefore() == null) {
            throw new BusinessValidationException(
                    MISSING_CASE_DETAILS_ERROR,
                    MISSING_CASE_DETAILS_ERROR
            );
        }

        List<CollectionMember<ScannedDocument>> documentsBefore =
                Optional.ofNullable(callbackRequest.getCaseDetailsBefore())
                        .map(CaseDetails::getData)
                        .map(CaseData::getScannedDocuments)
                        .orElse(Collections.emptyList());
        if (documentsBefore.isEmpty()) {
            return;
        }

        List<CollectionMember<ScannedDocument>> documentsAfter = Optional.ofNullable(callbackRequest.getCaseDetails())
                .map(CaseDetails::getData)
                .map(CaseData::getScannedDocuments)
                .orElse(Collections.emptyList());

        Set<String> afterDocumentUrls = documentsAfter.stream()
                .map(this::getScannedDocumentUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Long caseId = callbackRequest.getCaseDetails().getId();
        for (CollectionMember<ScannedDocument> existingDocument : documentsBefore) {
            String existingDocumentUrl = getScannedDocumentUrl(existingDocument);
            if (existingDocumentUrl == null) {
                log.error("Skipping validation for scanned document id {} on case {} - no resolvable document URL",
                        existingDocument.getId(),
                        caseId);
                throw new BusinessValidationException(
                        NO_DOCUMENT_RESOLVABLE_ERROR,
                        NO_DOCUMENT_RESOLVABLE_ERROR
                );
            }
            if (!afterDocumentUrls.contains(existingDocumentUrl)) {
                log.error("Scanned document with URL {} has been removed or replaced for case {}",
                        existingDocumentUrl,
                        caseId);
                throw new BusinessValidationException(
                        DOCUMENT_MODIFICATION_NOT_ALLOWED_ERROR,
                        DOCUMENT_MODIFICATION_NOT_ALLOWED_ERROR
                );
            }
        }
    }

    private String getDocumentUrl(CollectionMember<UploadDocument> documentMember) {
        return Optional.ofNullable(documentMember)
                .map(CollectionMember::getValue)
                .map(UploadDocument::getDocumentLink)
                .map(DocumentLink::getDocumentUrl)
                .orElse(null);
    }

    private String getScannedDocumentUrl(CollectionMember<ScannedDocument> documentMember) {
        return Optional.ofNullable(documentMember)
                .map(CollectionMember::getValue)
                .map(ScannedDocument::getUrl)
                .map(DocumentLink::getDocumentUrl)
                .orElse(null);
    }

    public void validateFinalWillIndicator(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        if (countWillDocuments(caseData) <= 1) {
            return;
        }

        long finalWillCount =
            getUploadedWills(caseData).filter(UploadDocument::isFinalWill).count()
                + getScannedWills(caseData).filter(ScannedDocument::isFinalWill).count();


        if (finalWillCount == 0) {
            throw new BusinessValidationException(
                    NO_FINAL_WILL_ERROR, NO_FINAL_WILL_ERROR);
        }

        if (finalWillCount > 1) {
            throw new BusinessValidationException(
                    MULTIPLE_FINAL_WILLS_ERROR,
                    MULTIPLE_FINAL_WILLS_ERROR);
        }
    }

    public long countWillDocuments(CaseData caseData) {
        return getUploadedWills(caseData).count()
                + getScannedWills(caseData).count();
    }

    public boolean hasMultipleWills(CaseData caseData) {
        return countWillDocuments(caseData) > 1;
    }

    private Stream<UploadDocument> getUploadedWills(CaseData caseData) {
        return Optional.ofNullable(caseData.getBoDocumentsUploaded())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(CollectionMember::getValue)
                .filter(Objects::nonNull)
                .filter(doc -> DocumentType.WILL.equals(doc.getDocumentType()));
    }

    private Stream<ScannedDocument> getScannedWills(CaseData caseData) {
        return Optional.ofNullable(caseData.getScannedDocuments())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(CollectionMember::getValue)
                .filter(Objects::nonNull)
                .filter(this::isWillDocument);
    }

    private boolean isWillDocument(ScannedDocument doc) {
        return DOC_TYPE_WILL.equalsIgnoreCase(doc.getType())
            || (DOC_TYPE_OTHER.equalsIgnoreCase(doc.getType()) && DOC_SUBTYPE_WILL.equalsIgnoreCase(doc.getSubtype()));
    }

    public List<CollectionMember<ScannedDocument>> defaultScannedFinalWillIndicator(
            List<CollectionMember<ScannedDocument>> documents) {
        if (documents == null) {
            return null;
        }
        return documents.stream()
                .map(member -> {
                    ScannedDocument document = member.getValue();

                    if (document == null
                            || !isWillDocument(document)
                            || (document.getFinalWill() != null
                            && !document.getFinalWill().isBlank())) {
                        return member;
                    }

                    return new CollectionMember<>(
                            member.getId(),
                            document.toBuilder()
                                    .finalWill(NO)
                                    .build()
                    );
                })
                .toList();
    }

    public List<CollectionMember<UploadDocument>> defaultUploadedFinalWillIndicator(
            List<CollectionMember<UploadDocument>> documents) {

        if (documents == null) {
            return null;
        }

        return documents.stream()
                .map(member -> {
                    UploadDocument document = member.getValue();

                    if (document == null
                            || !DocumentType.WILL.equals(document.getDocumentType())
                            || (document.getFinalWill() != null
                            && !document.getFinalWill().isBlank())) {
                        return member;
                    }

                    return new CollectionMember<>(
                            member.getId(),
                            document.toBuilder()
                                    .finalWill(NO)
                                    .build()
                    );
                })
                .toList();
    }
}
