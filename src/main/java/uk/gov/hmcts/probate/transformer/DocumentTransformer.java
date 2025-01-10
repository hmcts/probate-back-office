package uk.gov.hmcts.probate.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.SOT_INFORMATION_REQUEST;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_COVER;
import static uk.gov.hmcts.probate.model.DocumentType.ASSEMBLED_LETTER;
import static uk.gov.hmcts.probate.model.DocumentType.BLANK_LETTER;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;

@Slf4j
@Component
public class DocumentTransformer {
    private static final Set<DocumentType> COMMON_DOCUMENT_TYPES = new HashSet<>(Set.of(
            DIGITAL_GRANT_DRAFT, DIGITAL_GRANT, DIGITAL_GRANT_REISSUE_DRAFT, DIGITAL_GRANT_REISSUE,
            WELSH_DIGITAL_GRANT_REISSUE_DRAFT, WELSH_DIGITAL_GRANT_REISSUE, INTESTACY_GRANT_DRAFT,
            INTESTACY_GRANT, INTESTACY_GRANT_REISSUE_DRAFT, INTESTACY_GRANT_REISSUE,
            WELSH_INTESTACY_GRANT_REISSUE_DRAFT, WELSH_INTESTACY_GRANT_REISSUE, ADMON_WILL_GRANT_DRAFT,
            ADMON_WILL_GRANT, ADMON_WILL_GRANT_REISSUE_DRAFT, ADMON_WILL_GRANT_REISSUE,
            WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT, WELSH_ADMON_WILL_GRANT_REISSUE,
            AD_COLLIGENDA_BONA_GRANT_DRAFT, AD_COLLIGENDA_BONA_GRANT, AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT,
            AD_COLLIGENDA_BONA_GRANT_REISSUE, WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT,
            WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE, SOT_INFORMATION_REQUEST, GRANT_COVER, ASSEMBLED_LETTER,
            BLANK_LETTER, WELSH_DIGITAL_GRANT_DRAFT, WELSH_DIGITAL_GRANT, WELSH_ADMON_WILL_GRANT_DRAFT,
            WELSH_ADMON_WILL_GRANT, WELSH_INTESTACY_GRANT_DRAFT, WELSH_INTESTACY_GRANT,
            WELSH_AD_COLLIGENDA_BONA_GRANT_DRAFT, WELSH_AD_COLLIGENDA_BONA_GRANT
    ));

    public boolean hasDocumentWithType(List<Document> documents, DocumentType documentType) {
        return documents.stream()
                .filter(document -> document.getDocumentType().equals(documentType))
                .count() > 0;
    }

    public void addDocument(CallbackRequest callbackRequest, Document document, Boolean coversheetNotification) {
        if (COMMON_DOCUMENT_TYPES.contains(document.getDocumentType())) {
            callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                    .add(new CollectionMember<>(null, document));
        } else {
            switch (document.getDocumentType()) {
                case STATEMENT_OF_TRUTH, WELSH_STATEMENT_OF_TRUTH, LEGAL_STATEMENT_PROBATE,
                        LEGAL_STATEMENT_PROBATE_TRUST_CORPS, LEGAL_STATEMENT_INTESTACY, LEGAL_STATEMENT_ADMON:
                    callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated()
                            .add(new CollectionMember<>(null, document));
                    break;
                case SENT_EMAIL, GRANT_RAISED, CAVEAT_STOPPED:
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
