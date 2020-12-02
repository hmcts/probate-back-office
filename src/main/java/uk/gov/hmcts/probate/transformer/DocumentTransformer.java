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
            case DIGITAL_GRANT_REISSUE_DRAFT:
            case DIGITAL_GRANT_REISSUE:
            case WELSH_DIGITAL_GRANT_REISSUE_DRAFT:
            case WELSH_DIGITAL_GRANT_REISSUE:
            case INTESTACY_GRANT_DRAFT:
            case INTESTACY_GRANT:
            case INTESTACY_GRANT_REISSUE_DRAFT:
            case INTESTACY_GRANT_REISSUE:
            case WELSH_INTESTACY_GRANT_REISSUE_DRAFT:
            case WELSH_INTESTACY_GRANT_REISSUE:
            case ADMON_WILL_GRANT_DRAFT:
            case ADMON_WILL_GRANT:
            case ADMON_WILL_GRANT_REISSUE_DRAFT:
            case ADMON_WILL_GRANT_REISSUE:
            case WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT:
            case WELSH_ADMON_WILL_GRANT_REISSUE:
            case SOT_INFORMATION_REQUEST:
            case GRANT_COVER:
            case ASSEMBLED_LETTER:
            case WELSH_DIGITAL_GRANT_DRAFT:
            case WELSH_DIGITAL_GRANT:
            case WELSH_ADMON_WILL_GRANT_DRAFT:
            case WELSH_ADMON_WILL_GRANT:
            case WELSH_INTESTACY_GRANT_DRAFT:
            case WELSH_INTESTACY_GRANT:
                callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            case STATEMENT_OF_TRUTH:
            case WELSH_STATEMENT_OF_TRUTH:
            case LEGAL_STATEMENT_PROBATE:
            case LEGAL_STATEMENT_INTESTACY:
            case LEGAL_STATEMENT_ADMON:
                callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            case SENT_EMAIL:
            case GRANT_RAISED:
            case CAVEAT_STOPPED:
                callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated()
                        .add(new CollectionMember<>(null, document));
                break;
            case GRANT_COVERSHEET:
                if (coversheetNotification) {
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
            case CAVEAT_EXTENDED:
            case CAVEAT_WITHDRAWN:
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
