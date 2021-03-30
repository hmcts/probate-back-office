package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;

@Slf4j
@Service
public class TitleAndClearingTypeService {

    public boolean partnerTitleAndClearingOptionSelected(CaseData caseData) {
        String titleAndClearing = caseData.getTitleAndClearingType();

        return titleAndClearing != null && List.of(
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED,
                TITLE_AND_CLEARING_PARTNER_POWER_RESERVED,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE,
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING,
                TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING
        ).contains(titleAndClearing);
    }

    public boolean trustCorpTitleAndClearingOptionSelected(CaseData caseData) {
        String titleAndClearing = caseData.getTitleAndClearingType();

        return titleAndClearing != null && List.of(
                TITLE_AND_CLEARING_TRUST_CORP_SDJ,
                TITLE_AND_CLEARING_TRUST_CORP
        ).contains(titleAndClearing);
    }

    public boolean successorFirmTitleAndClearingOptionSelected(CaseData caseData) {
        String titleAndClearing = caseData.getTitleAndClearingType();

        return titleAndClearing != null && List.of(
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR,
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING
        ).contains(titleAndClearing);
    }

}