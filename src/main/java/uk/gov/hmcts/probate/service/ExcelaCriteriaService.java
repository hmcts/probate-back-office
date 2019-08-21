package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;

@Slf4j
@Service
public class ExcelaCriteriaService {

    private ArrayList<ReturnedCaseDetails> filteredCases;
    private static final LocalDateTime EARLIEST_DATE = LocalDateTime.parse("2019-03-31T23:59:59");

    public List<ReturnedCaseDetails> getFilteredCases(List<ReturnedCaseDetails> cases) {
        log.info("filtering {} cases", cases.size());
        filteredCases = new ArrayList<>();
        for (ReturnedCaseDetails caseItem : cases) {
            if (caseItem.getData().getScannedDocuments() != null) {
                scannedDocumentsFilter(caseItem);
            }
        }
        filteredCases.sort(Comparator.comparing(o -> o.getData().getDeceasedSurname().toLowerCase()));
        log.info("Cases passed filtering: {}", filteredCases.size());

        return filteredCases;
    }

    private void scannedDocumentsFilter(ReturnedCaseDetails caseItem) {
        for (CollectionMember<ScannedDocument> document : caseItem.getData().getScannedDocuments()) {
            if (document.getValue().getSubtype() != null
                    && document.getValue().getSubtype().equalsIgnoreCase(DOC_SUBTYPE_WILL)
                    && document.getValue().getScannedDate().isAfter(EARLIEST_DATE)) {
                filteredCases.add(caseItem);
                break;
            }
        }
    }
}
