package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

@Slf4j
@AllArgsConstructor
@Service
public class ScannedDocumentOrderingService {
    public void orderScannedDocuments(CaseData caseData) {
        if (caseData.getScannedDocuments() != null) {
            caseData.getScannedDocuments()
                    .sort((o1, o2) -> {
                        int compare1 = 0;
                        int compare2 = 0;
                        for (int i = 0; i < Constants.SCANNED_DOCS_ORDER.size(); i++) {
                            if (o1.getValue().getType().equals(Constants.SCANNED_DOCS_ORDER.get(i))) {
                                compare1 = i;
                            }
                            if (o2.getValue().getType().equals(Constants.SCANNED_DOCS_ORDER.get(i))) {
                                compare2 = i;
                            }
                        }
                        if (compare1 == compare2
                                && o1.getValue().getSubtype() != null
                                && o2.getValue().getSubtype() != null) {
                            return (o1.getValue().getSubtype().compareToIgnoreCase(o2.getValue().getSubtype()));
                        }
                        return compare1 - compare2;
                    });
        }
    }

}
