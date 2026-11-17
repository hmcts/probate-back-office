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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_OTHER;
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

        long finalWillCount = countFinalWills(caseData);


        if (finalWillCount == 0) {
            throw new BusinessValidationException(NO_FINAL_WILL_ERROR, NO_FINAL_WILL_ERROR);
        }

        if (finalWillCount > 1) {
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
        return DOC_TYPE_WILL.equalsIgnoreCase(doc.getType())
            || (DOC_TYPE_OTHER.equalsIgnoreCase(doc.getType()) && DOC_SUBTYPE_WILL.equalsIgnoreCase(doc.getSubtype()));
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
        List<CollectionMember<UploadDocument>> oldList = Optional.ofNullable(oldDocuments)
                .orElseGet(Collections::emptyList);
        List<CollectionMember<UploadDocument>> newList = Optional.ofNullable(newDocuments)
                .orElseGet(Collections::emptyList);
        if (oldList.size() != newList.size()) {
            return false;
        }
        Map<String, String> originalUrls = new HashMap<>();
        for (CollectionMember<UploadDocument> member : oldList) {
            if (member == null || member.getId() == null || getDocumentUrl(member) == null
                    || originalUrls.putIfAbsent(member.getId(), getDocumentUrl(member)) != null) {
                return false;
            }
        }
        for (CollectionMember<UploadDocument> member : newList) {
            if (member == null || member.getId() == null || getDocumentUrl(member) == null
                    || !Objects.equals(originalUrls.remove(member.getId()), getDocumentUrl(member))) {
                return false;
            }
        }
        return originalUrls.isEmpty();
    }

    private boolean sameScannedDocuments(List<CollectionMember<ScannedDocument>> oldDocuments,
                                         List<CollectionMember<ScannedDocument>> newDocuments) {
        List<CollectionMember<ScannedDocument>> oldList = Optional.ofNullable(oldDocuments)
                .orElseGet(Collections::emptyList);
        List<CollectionMember<ScannedDocument>> newList = Optional.ofNullable(newDocuments)
                .orElseGet(Collections::emptyList);
        if (oldList.size() != newList.size()) {
            return false;
        }
        Map<String, ScannedDocument> original = new HashMap<>();
        for (CollectionMember<ScannedDocument> member : oldList) {
            if (member == null || member.getId() == null || member.getValue() == null
                    || getScannedDocumentUrl(member) == null
                    || original.putIfAbsent(member.getId(), member.getValue()) != null) {
                return false;
            }
        }
        for (CollectionMember<ScannedDocument> member : newList) {
            if (member == null || member.getId() == null || member.getValue() == null) {
                return false;
            }
            ScannedDocument old = original.remove(member.getId());
            ScannedDocument current = member.getValue();
            if (old == null || !Objects.equals(old.getUrl(), current.getUrl())
                    || !Objects.equals(old.getControlNumber(), current.getControlNumber())
                    || !Objects.equals(old.getFileName(), current.getFileName())
                    || !Objects.equals(old.getScannedDate(), current.getScannedDate())
                    || !Objects.equals(old.getExceptionRecordReference(), current.getExceptionRecordReference())
                    || !Objects.equals(old.getDeliveryDate(), current.getDeliveryDate())) {
                return false;
            }
        }
        return original.isEmpty();
    }
}
