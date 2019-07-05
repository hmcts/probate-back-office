package uk.gov.hmcts.probate.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;

import java.util.List;

import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;

@Slf4j
@Component
public class DocumentTransformer {

    public boolean hasDocumentWithType(List<Document> documents, DocumentType documentType) {
        return documents.stream()
                .filter(document -> document.getDocumentType().equals(documentType))
                .count() > 0;
    }

    public void addDocument(CallbackRequest callbackRequest, Document document, Boolean coversheetNotification) {
        switch (document.getDocumentType()) {
            case DIGITAL_GRANT_DRAFT:
            case DIGITAL_GRANT:
            case DIGITAL_GRANT_DRAFT_REISSUE:
            case DIGITAL_GRANT_REISSUE:
            case INTESTACY_GRANT_DRAFT:
            case INTESTACY_GRANT_DRAFT_REISSUE:
            case INTESTACY_GRANT:
            case ADMON_WILL_GRANT_DRAFT:
            case ADMON_WILL_GRANT:
            case ADMON_WILL_GRANT_DRAFT_REISSUE:
            case GRANT_COVER:
                callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            case SENT_EMAIL:
            case CAVEAT_STOPPED:
                callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            case GRANT_COVERSHEET:
                if (coversheetNotification == true) {
                    callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated()
                            .add(new CollectionMember<>(null, document));
                } else {
                    callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                            .add(new CollectionMember<>(null, document));
                }
                break;
            case EDGE_CASE:
                break;
            default:
        }
    }

    public void addDocument(CaveatCallbackRequest caveatCallbackRequest, Document document) {
        switch (document.getDocumentType()) {
            case CAVEAT_COVERSHEET:
            case CAVEAT_RAISED:
            case SENT_EMAIL:
                caveatCallbackRequest.getCaseDetails().getData().getNotificationsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            default:
        }
    }

    public void addDocument(WillLodgementCallbackRequest callbackRequest, Document document) {
        if (document.getDocumentType().equals(WILL_LODGEMENT_DEPOSIT_RECEIPT)) {
            callbackRequest.getCaseDetails().getData().getDocumentsGenerated()
                    .add(new CollectionMember<>(null, document));
        }
    }

}
