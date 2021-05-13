package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;

public class PartnersAddedValidationRuleTest {
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    @InjectMocks
    private PartnersAddedValidationRule partnersAddedValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaseData caseDataYesYes;
    private CaseData caseDataYesNo;
    private CaseData caseDataNoNo;
    private CaseData caseDataNoError;
    private CaseData caseDataNoErrorTwo;

    private CaseData caseDataYesYesTrustCorp;
    private CaseData caseDataYesNoTrustCorp;
    private CaseData caseDataNoNoTrustCorp;
    private CaseData caseDataNoErrorTrustCorp;
    private CaseData caseDataNoErrorTwoTrustCorp;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caseDataYesYes = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .titleAndClearingType(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED)
            .solsSolicitorIsExec("Yes")
            .solsSolicitorIsApplying("Yes")
            .anyOtherApplyingPartners("No")
            .registryLocation("Bristol").build();

        caseDataYesNo = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .titleAndClearingType(TITLE_AND_CLEARING_PARTNER_POWER_RESERVED)
            .solsSolicitorIsExec("Yes")
            .solsSolicitorIsApplying("No")
            .anyOtherApplyingPartners("No")
            .registryLocation("Bristol").build();

        caseDataNoNo = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR)
            .solsSolicitorIsExec("No")
            .solsSolicitorIsApplying("No")
            .anyOtherApplyingPartners("No")
            .registryLocation("Bristol").build();

        caseDataNoError = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .titleAndClearingType(TITLE_AND_CLEARING_SOLE_PRINCIPLE)
            .solsSolicitorIsExec("No")
            .solsSolicitorIsApplying("No")
            .anyOtherApplyingPartners("Yes")
            .registryLocation("Bristol").build();

        caseDataNoErrorTwo = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .titleAndClearingType(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING)
            .solsSolicitorIsExec("No")
            .solsSolicitorIsApplying("Yes")
            .anyOtherApplyingPartners("Yes")
            .registryLocation("Bristol").build();

        caseDataYesYesTrustCorp = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
                .solsSolicitorIsExec("Yes")
                .solsSolicitorIsApplying("Yes")
                .anyOtherApplyingPartnersTrustCorp("No")
                .registryLocation("Bristol").build();

        caseDataYesNoTrustCorp = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP)
                .solsSolicitorIsExec("Yes")
                .solsSolicitorIsApplying("No")
                .anyOtherApplyingPartnersTrustCorp("No")
                .registryLocation("Bristol").build();

        caseDataNoNoTrustCorp = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
                .solsSolicitorIsExec("No")
                .solsSolicitorIsApplying("No")
                .anyOtherApplyingPartnersTrustCorp("No")
                .registryLocation("Bristol").build();

        caseDataNoErrorTrustCorp = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP)
                .solsSolicitorIsExec("No")
                .solsSolicitorIsApplying("No")
                .anyOtherApplyingPartnersTrustCorp("Yes")
                .registryLocation("Bristol").build();

        caseDataNoErrorTwoTrustCorp = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
                .solsSolicitorIsExec("No")
                .solsSolicitorIsApplying("Yes")
                .anyOtherApplyingPartnersTrustCorp("Yes")
                .registryLocation("Bristol").build();
    }

    @Test
    public void shouldThrowNeedAtLeastOneMorePartnerYesYes() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataYesYes, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            partnersAddedValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("'Yes' needs to be selected for question "
                + "anyOtherApplyingPartners for case id 12345678987654321");

    }

    @Test
    public void shouldThrowNeedAtLeastOneMorePartnerYesYesTrustCorp() {
        final CaseDetails caseDetailsTc =
                new CaseDetails(caseDataYesYesTrustCorp, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            partnersAddedValidationRule.validate(caseDetailsTc);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("'Yes' needs to be selected for question "
                        + "anyOtherApplyingPartnersTrustCorp for case id 12345678987654321");
    }

    @Test
    public void shouldThrowNeedAtLeastOneMorePartnerYesNo() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataYesNo, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            partnersAddedValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("'Yes' needs to be selected for question "
                + "anyOtherApplyingPartners for case id 12345678987654321");
    }

    @Test
    public void shouldThrowNeedAtLeastOneMorePartnerYesNoTrustCorp() {
        final CaseDetails caseDetailsTc =
                new CaseDetails(caseDataYesNoTrustCorp, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            partnersAddedValidationRule.validate(caseDetailsTc);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("'Yes' needs to be selected for question "
                        + "anyOtherApplyingPartnersTrustCorp for case id 12345678987654321");
    }

    @Test
    public void shouldThrowNeedAtLeastOneMorePartnerNoNo() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataNoNo, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            partnersAddedValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("'Yes' needs to be selected for question "
                + "anyOtherApplyingPartners for case id 12345678987654321");
    }

    @Test
    public void shouldThrowNeedAtLeastOneMorePartnerNoNoTrustCorp() {
        final CaseDetails caseDetailsTc =
                new CaseDetails(caseDataNoNoTrustCorp, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            partnersAddedValidationRule.validate(caseDetailsTc);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("'Yes' needs to be selected for question "
                    + "anyOtherApplyingPartnersTrustCorp for case id 12345678987654321");
    }

    @Test
    public void shouldThrowNeedAtLeastOneMorePartnerNoError() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataNoError, LAST_MODIFIED, CASE_ID);

        partnersAddedValidationRule.validate(caseDetails);
    }

    @Test
    public void shouldThrowNeedAtLeastOneMorePartnerNoErrorTrustCorp() {
        final CaseDetails caseDetailsTc =
                new CaseDetails(caseDataNoErrorTrustCorp, LAST_MODIFIED, CASE_ID);

        partnersAddedValidationRule.validate(caseDetailsTc);
    }

    @Test
    public void shouldThrowNeedAtLeastOneMorePartnerNoErrorTwo() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataNoErrorTwo, LAST_MODIFIED, CASE_ID);

        partnersAddedValidationRule.validate(caseDetails);
    }

    @Test
    public void shouldThrowNeedAtLeastOneMorePartnerNoErrorTwoTrustCorp() {
        final CaseDetails caseDetailsTc =
                new CaseDetails(caseDataNoErrorTwoTrustCorp, LAST_MODIFIED, CASE_ID);

        partnersAddedValidationRule.validate(caseDetailsTc);
    }
}
