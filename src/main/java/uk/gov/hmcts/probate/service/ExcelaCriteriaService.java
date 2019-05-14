package uk.gov.hmcts.probate.service;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;

import java.time.LocalDateTime;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;

@Service
public class ExcelaCriteriaService {

    private ImmutableList.Builder<ReturnedCaseDetails> filteredCases;
    private static final LocalDateTime EARLIEST_DATE = LocalDateTime.parse("2019-03-31T23:59:59");

    public List<ReturnedCaseDetails> getFilteredCases(List<ReturnedCaseDetails> cases) {
        filteredCases = new ImmutableList.Builder<>();
        for (ReturnedCaseDetails caseItem : cases) {
            if (caseItem.getData().getScannedDocuments() != null) {
                scannedDocumentsFilter(caseItem);
            }
        }
        return filteredCases.build();
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
