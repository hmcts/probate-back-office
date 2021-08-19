package uk.gov.hmcts.probate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;

@RunWith(MockitoJUnitRunner.class)
public class TitleAndClearingTypeServiceTest {

    private final CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();

    @InjectMocks
    private TitleAndClearingTypeService titleAndClearingTypeService;

    @Mock
    private CaseDetails caseDetailsMock;

    @Test
    public void shouldReturnTrueIfTitleAndClearingTypeIsPartner() {

        caseDataBuilder.titleAndClearingType(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Boolean result = titleAndClearingTypeService.partnerTitleAndClearingOptionSelected(caseDetailsMock.getData());

        assertTrue(result);
    }

    @Test
    public void shouldReturnTrueIfTitleAndClearingTypeIsTrustCorp() {

        caseDataBuilder.titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Boolean result = titleAndClearingTypeService.trustCorpTitleAndClearingOptionSelected(caseDetailsMock.getData());

        assertTrue(result);
    }

    @Test
    public void shouldReturnTrueIfTitleAndClearingTypeIsSuccessorFirm() {

        caseDataBuilder.titleAndClearingType(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Boolean result = titleAndClearingTypeService.successorFirmTitleAndClearingOptionSelected(
                caseDetailsMock.getData());

        assertTrue(result);
    }
}
