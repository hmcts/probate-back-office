package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_OTHER;

@Slf4j
@AllArgsConstructor
@Service
public class ScannedDocumentOrderingService {
    public void orderScannedDocuments(CallbackRequest callbackRequest) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        caseData.getScannedDocuments()
            .sort((o1,o2) -> {
                String[] orderOfDocs = new String[]{"coversheet", "form", "will", "forensic_sheets",
                    "supporting_documents", "iht", "pps_legal_statement", "cherished", "other"};
                int compare1 = 0;
                int compare2 = 0;
                for (int i = 0; i < orderOfDocs.length; i++) {
                    if (o1.getValue().getType().equals(orderOfDocs[i])) {
                        compare1 = i;
                    }
                    if (o2.getValue().getType().equals(orderOfDocs[i])) {
                        compare2 = i;
                    }
                }
                return compare1 - compare2;
            });
    }

}
