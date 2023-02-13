package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.Constants;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class ScannedDocumentOrderingService {
    public void orderScannedDocuments(List<CollectionMember<ScannedDocument>> scannedDocs) {
        scannedDocs
            .sort((o1,o2) -> {
                int compare1 = 0;
                int compare2 = 0;
                for (int i = 0; i < Constants.orderOfDocs.size(); i++) {
                    if (o1.getValue().getType().equals(Constants.orderOfDocs.get(i))) {
                        compare1 = i;
                    }
                    if (o2.getValue().getType().equals(Constants.orderOfDocs.get(i))) {
                        compare2 = i;
                    }
                }
                return compare1 - compare2;
            });
    }

}
