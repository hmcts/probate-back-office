package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class DocumentService {
    private final DocumentManagementService documentManagementService;

    public void expire(CallbackRequest callbackRequest, DocumentType documentType) {
        List<CollectionMember<Document>> collect = callbackRequest.getCaseDetails().getData()
            .getProbateDocumentsGenerated().stream()
            .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(documentType))
            .collect(Collectors.toList());


        collect.forEach(collectionMember -> {
                try {
                    documentManagementService.delete(collectionMember.getValue());
                    callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().remove(collectionMember);
                } catch (Exception e) {
                    log.warn("Unable to expire document: {} for case id: {}",
                        collectionMember.getValue().getDocumentLink(),
                        callbackRequest.getCaseDetails().getId());
                }
            }
        );

    }
}
