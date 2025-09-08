package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_FIRM_CEASED_TRADING_NO_SUCCESSOR;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;

@ExtendWith(SpringExtension.class)
class TitleAndClearingTypeServiceTest {

    private final CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();

    @InjectMocks
    private TitleAndClearingTypeService titleAndClearingTypeService;

    @Mock
    private CaseDetails caseDetailsMock;

    @Test
    void shouldReturnTrueIfTitleAndClearingTypeIsPartner() {

        caseDataBuilder.titleAndClearingType(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Boolean result = titleAndClearingTypeService.partnerTitleAndClearingOptionSelected(caseDetailsMock.getData());

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueIfTitleAndClearingTypeIsTrustCorp() {

        caseDataBuilder.titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Boolean result = titleAndClearingTypeService.trustCorpTitleAndClearingOptionSelected(caseDetailsMock.getData());

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueIfTitleAndClearingTypeIsSuccessorFirm() {

        caseDataBuilder.titleAndClearingType(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Boolean result = titleAndClearingTypeService.successorFirmTitleAndClearingOptionSelected(
                caseDetailsMock.getData());

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueIfTitleAndClearingTypeIsFirmCeasedTradingNoSucc() {

        caseDataBuilder.titleAndClearingType(TITLE_AND_CLEARING_FIRM_CEASED_TRADING_NO_SUCCESSOR);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Boolean result = titleAndClearingTypeService.firmCeasedTradingNoSuccTitleAndClearingOptionSelected(
                caseDetailsMock.getData());

        assertTrue(result);
    }
}
