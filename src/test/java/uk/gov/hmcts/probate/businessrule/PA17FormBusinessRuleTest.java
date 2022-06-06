package uk.gov.hmcts.probate.businessrule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_ALL_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCC_ALL_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;

class PA17FormBusinessRuleTest {


    @InjectMocks
    private PA17FormBusinessRule underTest;

    @Mock
    private CaseData mockCaseData;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldBeApplicableForTCTPartAllRenouncing() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn(TITLE_AND_CLEARING_PARTNER_ALL_RENOUNCING);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForTCTPartOthersRenouncing() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn(TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForTCTPartSuccOthersRenouncing() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForTCTPartSuccAllRenouncing() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn(TITLE_AND_CLEARING_PARTNER_SUCC_ALL_RENOUNCING);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTPartSuccPowerRes() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTPartPowerRes() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn(TITLE_AND_CLEARING_PARTNER_POWER_RESERVED);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTSolePrinSucc() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn(TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTSolePrin() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn(TITLE_AND_CLEARING_SOLE_PRINCIPLE);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTTrustCorpResWithSDJ() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn(TITLE_AND_CLEARING_TRUST_CORP_SDJ);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTTrustCorpResWithApp() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn(TITLE_AND_CLEARING_TRUST_CORP);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForNull() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn(null);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForEmpty() {
        when(mockCaseData.getTitleAndClearingType()).thenReturn("");
        assertFalse(underTest.isApplicable(mockCaseData));
    }
}
