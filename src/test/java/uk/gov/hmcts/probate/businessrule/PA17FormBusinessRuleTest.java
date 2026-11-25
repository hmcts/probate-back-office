package uk.gov.hmcts.probate.businessrule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@ExtendWith(MockitoExtension.class)
class PA17FormBusinessRuleTest {

    @InjectMocks
    private PA17FormBusinessRule underTest;

    private CaseData mockCaseData;

    @Test
    void shouldBeApplicableForTCTPartAllRenouncing() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType(TITLE_AND_CLEARING_PARTNER_ALL_RENOUNCING)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForTCTPartOthersRenouncing() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType(TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForTCTPartSuccOthersRenouncing() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForTCTPartSuccAllRenouncing() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType(TITLE_AND_CLEARING_PARTNER_SUCC_ALL_RENOUNCING)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTPartSuccPowerRes() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTPartPowerRes() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType(TITLE_AND_CLEARING_PARTNER_POWER_RESERVED)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTSolePrinSucc() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTSolePrin() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTTrustCorpResWithSDJ() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForTCTTrustCorpResWithApp() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForNull() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType(null)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForEmpty() {
        mockCaseData = CaseData.builder()
            .titleAndClearingType("")
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }
}
