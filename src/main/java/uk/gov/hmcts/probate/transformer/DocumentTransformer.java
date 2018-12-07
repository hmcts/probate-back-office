package uk.gov.hmcts.probate.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;

import java.util.List;

@Slf4j
@Component
public class DocumentTransformer {

    public boolean hasDocumentWithType(List<Document> documents, DocumentType documentType) {
        return documents.stream()
                .filter(document -> document.getDocumentType().equals(documentType))
                .count() > 0;
    }

    public void addDocument(CallbackRequest callbackRequest, Document document) {
        switch (document.getDocumentType()) {
            case DIGITAL_GRANT_DRAFT:
                callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            case DIGITAL_GRANT:
                callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            case INTESTACY_GRANT_DRAFT:
                callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            case INTESTACY_GRANT:
                callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            case ADMON_WILL_GRANT_DRAFT:
                callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            case ADMON_WILL_GRANT:
                callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            case SENT_EMAIL:
                callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            default:
        }
    }

}
