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
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

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

    private static final String ISSUE_GRANT_DOCUMENT_ERROR =
            "Documents cannot be added or removed during the Issue Grant event. "
                    + "Please use the appropriate document upload event.";

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
                Optional.of(callbackRequest.getCaseDetailsBefore())
                .map(CaseDetails::getData)
                .map(CaseData::getBoDocumentsUploaded)
                .orElse(Collections.emptyList());
        if (documentsBefore.isEmpty()) {
            return;
        }

        List<CollectionMember<UploadDocument>> documentsAfter = Optional.of(callbackRequest.getCaseDetails())
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
        Long caseId = callbackRequest.getCaseDetails().getId();
        long willCount = countWillDocuments(caseData);
        if (willCount <= 1) {
            log.info("Final will indicator validation skipped for case: {}  willCount: {}", caseId, willCount);
            return;
        }
        long finalWillCount = countFinalWills(caseData);
        if (finalWillCount != 1) {
            log.warn("Final Will indicator validation failed for case: {} willCount: {} finalWillCount: {}",
                caseId,
                willCount,
                finalWillCount);

            if (finalWillCount == 0) {
                throw new BusinessValidationException(NO_FINAL_WILL_ERROR, NO_FINAL_WILL_ERROR);
            }
            throw new BusinessValidationException(MULTIPLE_FINAL_WILLS_ERROR, MULTIPLE_FINAL_WILLS_ERROR);
        }
    }

    public long countWillDocuments(CaseData caseData) {
        return getUploadedWills(caseData).count()
                + getScannedWills(caseData).count();
    }

    public long countFinalWills(CaseData caseData) {
        return getUploadedWills(caseData).filter(UploadDocument::isFinalWill).count()
                + getScannedWills(caseData).filter(ScannedDocument::isFinalWill).count();
    }

    public boolean showReviewFinalWillPage(CaseData caseData) {
        return countWillDocuments(caseData) > 1 && countFinalWills(caseData) != 1;
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
        return DOC_TYPE_WILL.equalsIgnoreCase(doc.getType());
    }

    public List<CollectionMember<ScannedDocument>> defaultScannedFinalWillIndicator(
            List<CollectionMember<ScannedDocument>> documents, boolean singleWill) {
        if (documents == null) {
            return Collections.emptyList();
        }
        return documents.stream()
                .map(member -> updateScannedFinalWill(member, singleWill))
                .toList();
    }

    private CollectionMember<ScannedDocument> updateScannedFinalWill(
            CollectionMember<ScannedDocument> member, boolean singleWill) {
        ScannedDocument document = member.getValue();
        if (document == null || !isWillDocument(document)) {
            return member;
        }
        String value = resolveFinalWillValue(document.getFinalWill(), singleWill);
        if (value.equals(document.getFinalWill())) {
            return member;
        }
        return new CollectionMember<>(member.getId(),
                document.toBuilder().finalWill(value).build());
    }

    public List<CollectionMember<UploadDocument>> defaultUploadedFinalWillIndicator(
            List<CollectionMember<UploadDocument>> documents, boolean singleWill) {
        if (documents == null) {
            return Collections.emptyList();
        }

        return documents.stream()
                .map(member -> updateUploadedFinalWill(member, singleWill))
                .toList();
    }

    private CollectionMember<UploadDocument> updateUploadedFinalWill(
            CollectionMember<UploadDocument> member, boolean singleWill) {
        UploadDocument document = member.getValue();
        if (document == null || !DocumentType.WILL.equals(document.getDocumentType())) {
            return member;
        }
        String value = resolveFinalWillValue(document.getFinalWill(), singleWill);
        if (value.equals(document.getFinalWill())) {
            return member;
        }
        return new CollectionMember<>(member.getId(), document.toBuilder().finalWill(value).build());
    }

    private String resolveFinalWillValue(String currentValue, boolean singleWill) {
        if (singleWill) {
            return YES;
        }
        return (currentValue == null || currentValue.isBlank()) ? NO : currentValue;
    }


    /** The issue-grant page permits metadata edits but not changes to collection membership or file links. */
    public void validateIssueGrantDocuments(CallbackRequest request) {
        if (request == null || request.getCaseDetails() == null || request.getCaseDetailsBefore() == null
                || request.getCaseDetails().getData() == null
                || request.getCaseDetailsBefore().getData() == null) {
            throw new BusinessValidationException(MISSING_CASE_DETAILS_ERROR, MISSING_CASE_DETAILS_ERROR);
        }
        CaseData before = request.getCaseDetailsBefore().getData();
        CaseData after = request.getCaseDetails().getData();
        if (!sameUploadedDocuments(before.getBoDocumentsUploaded(), after.getBoDocumentsUploaded())
                || !sameScannedDocuments(before.getScannedDocuments(), after.getScannedDocuments())) {
            throw new BusinessValidationException(ISSUE_GRANT_DOCUMENT_ERROR, ISSUE_GRANT_DOCUMENT_ERROR);
        }
    }

    private boolean sameUploadedDocuments(List<CollectionMember<UploadDocument>> oldDocuments,
                                          List<CollectionMember<UploadDocument>> newDocuments) {
        return sameDocuments(oldDocuments, newDocuments, this::getDocumentUrl);
    }

    private boolean sameScannedDocuments(List<CollectionMember<ScannedDocument>> oldDocuments,
                                         List<CollectionMember<ScannedDocument>> newDocuments) {
        return sameDocuments(oldDocuments, newDocuments, this::getScannedDocumentUrl);
    }

    private <T> boolean sameDocuments(List<CollectionMember<T>> oldDocuments,
                                      List<CollectionMember<T>> newDocuments,
                                      Function<CollectionMember<T>, String> urlExtractor) {
        List<CollectionMember<T>> oldList = Optional.ofNullable(oldDocuments)
                .orElseGet(Collections::emptyList);
        List<CollectionMember<T>> newList = Optional.ofNullable(newDocuments)
                .orElseGet(Collections::emptyList);
        // Different collection sizes indicate documents have been added or removed
        if (oldList.size() != newList.size()) {
            return false;
        }
        // Map original collection member IDs to document URLs to verify document identity
        Map<String, String> originalUrls = new HashMap<>();
        for (CollectionMember<T> member : oldList) {
            if (member == null || member.getId() == null) {
                return false;
            }
            String documentUrl = urlExtractor.apply(member);
            if (documentUrl == null || originalUrls.containsKey(member.getId())) {
                return false;
            }
            originalUrls.put(member.getId(), documentUrl);
        }
        // Match each submitted document against the original ID and URL
        for (CollectionMember<T> member : newList) {
            if (member == null || member.getId() == null) {
                return false;
            }
            String documentUrl = urlExtractor.apply(member);
            if (documentUrl == null) {
                return false;
            }
            // Remove matched entries so missing or duplicate documents are detected
            String originalUrl = originalUrls.remove(member.getId());
            if (!documentUrl.equals(originalUrl)) {
                return false;
            }
        }
        // All original documents must have been matched exactly once
        return originalUrls.isEmpty();
    }
}
