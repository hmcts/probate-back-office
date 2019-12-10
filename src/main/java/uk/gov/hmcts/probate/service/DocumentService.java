package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.UploadService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE_DRAFT;

@Slf4j
@AllArgsConstructor
@Service
public class DocumentService {

    private final UploadService uploadService;

    public void expire(CallbackRequest callbackRequest, DocumentType documentType) {

        List<CollectionMember<Document>> documentsToExpire = new ArrayList<>();

        switch (documentType) {
            case DIGITAL_GRANT_DRAFT:
                documentsToExpire.addAll(callbackRequest.getCaseDetails().getData()
                        .getProbateDocumentsGenerated().stream()
                        .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(DIGITAL_GRANT_DRAFT))
                        .collect(Collectors.toList()));
                break;
            case ADMON_WILL_GRANT_DRAFT:
                documentsToExpire.addAll(callbackRequest.getCaseDetails().getData()
                        .getProbateDocumentsGenerated().stream()
                        .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(ADMON_WILL_GRANT_DRAFT))
                        .collect(Collectors.toList()));
                break;
            case INTESTACY_GRANT_DRAFT:
                documentsToExpire.addAll(callbackRequest.getCaseDetails().getData()
                        .getProbateDocumentsGenerated().stream()
                        .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(INTESTACY_GRANT_DRAFT))
                        .collect(Collectors.toList()));
                break;
            case DIGITAL_GRANT_REISSUE_DRAFT:
                documentsToExpire.addAll(callbackRequest.getCaseDetails().getData()
                        .getProbateDocumentsGenerated().stream()
                        .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(DIGITAL_GRANT_REISSUE_DRAFT))
                        .collect(Collectors.toList()));
                break;
            case INTESTACY_GRANT_REISSUE_DRAFT:
                documentsToExpire.addAll(callbackRequest.getCaseDetails().getData()
                        .getProbateDocumentsGenerated().stream()
                        .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(INTESTACY_GRANT_REISSUE_DRAFT))
                        .collect(Collectors.toList()));
                break;
            case ADMON_WILL_GRANT_REISSUE_DRAFT:
                documentsToExpire.addAll(callbackRequest.getCaseDetails().getData()
                        .getProbateDocumentsGenerated().stream()
                        .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(ADMON_WILL_GRANT_REISSUE_DRAFT))
                        .collect(Collectors.toList()));
                break;
            case WELSH_DIGITAL_GRANT_DRAFT:
            case WELSH_AMON_WILL_GRANT_DRAFT:
            case WELSH_INTESTACY_GRANT_DRAFT:
                documentsToExpire.addAll(callbackRequest.getCaseDetails().getData()
                        .getProbateDocumentsGenerated().stream()
                        .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(documentType))
                        .collect(Collectors.toList()));
                break;
            default:
                documentsToExpire.addAll(callbackRequest.getCaseDetails().getData()
                        .getProbateDocumentsGenerated().stream()
                        .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(DIGITAL_GRANT_DRAFT))
                        .collect(Collectors.toList()));
                break;

        }

        documentsToExpire.forEach(collectionMember -> {
            try {
                uploadService.expire(collectionMember.getValue());
                callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().remove(collectionMember);
            } catch (Exception e) {
                log.warn("Unable to expiry document: {}", collectionMember.getValue().getDocumentLink());
            }
        });
    }
}
