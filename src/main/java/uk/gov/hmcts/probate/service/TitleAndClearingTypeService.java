package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.getNonTrustPtnrTitleClearingTypes;
import static uk.gov.hmcts.probate.model.Constants.getSuccessorTitleClearingTypes;
import static uk.gov.hmcts.probate.model.Constants.getTrustCorpTitleClearingTypes;

@Slf4j
@Service
public class TitleAndClearingTypeService {

    public boolean partnerTitleAndClearingOptionSelected(CaseData caseData) {
        String titleAndClearing = caseData.getTitleAndClearingType();
        return titleAndClearing != null && getNonTrustPtnrTitleClearingTypes().contains(titleAndClearing);
    }

    public boolean trustCorpTitleAndClearingOptionSelected(CaseData caseData) {
        String titleAndClearing = caseData.getTitleAndClearingType();
        return titleAndClearing != null && getTrustCorpTitleClearingTypes().contains(titleAndClearing);
    }

    public boolean successorFirmTitleAndClearingOptionSelected(CaseData caseData) {
        String titleAndClearing = caseData.getTitleAndClearingType();
        return titleAndClearing != null && getSuccessorTitleClearingTypes().contains(titleAndClearing);
    }
}