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

import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;

public class PractitionersPositionInTrustValidationRuleTest {
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    @InjectMocks
    private PractitionersPositionInTrustValidationRule practitionersPositionInTrustRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaseData caseDataNoYesNoPosInTrust;

    private CaseData caseDataYesYesTrustCorpNoPosInTrust;
    private CaseData caseDataYesNoTrustCorpNoPosInTrust;
    private CaseData caseDataNoNoTrustCorpNoPosInTrust;
    private CaseData caseDataNoYesTrustCorpNoPosInTrust;
    private CaseData caseDataNoYesTrustCorpWithPosInTrust;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caseDataNoYesNoPosInTrust = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .titleAndClearingType(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED)
                .solsSolicitorIsExec("No")
                .solsSolicitorIsApplying("Yes")
                .build();

        caseDataYesYesTrustCorpNoPosInTrust = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
                .solsSolicitorIsExec("Yes")
                .solsSolicitorIsApplying("Yes")
                .build();

        caseDataYesNoTrustCorpNoPosInTrust = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP)
                .solsSolicitorIsExec("Yes")
                .solsSolicitorIsApplying("No")
                .build();

        caseDataNoNoTrustCorpNoPosInTrust = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
                .solsSolicitorIsExec("No")
                .solsSolicitorIsApplying("No")
                .build();

        caseDataNoYesTrustCorpNoPosInTrust = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP)
                .solsSolicitorIsExec("No")
                .solsSolicitorIsApplying("Yes")
                .build();

        caseDataNoYesTrustCorpWithPosInTrust = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
                .solsSolicitorIsExec("No")
                .solsSolicitorIsApplying("Yes")
                .probatePractitionersPositionInTrust("Director")
                .build();
    }

    @Test
    public void shouldNotThrowErrorNoYesNotTrustCorp() {
        final CaseDetails caseDetails =
                new CaseDetails(caseDataNoYesNoPosInTrust, LAST_MODIFIED, CASE_ID);

        practitionersPositionInTrustRule.validate(caseDetails);
    }

    @Test
    public void shouldNotThrowErrorYesYesTrustCorpNoPosInTrust() {
        final CaseDetails caseDetails =
                new CaseDetails(caseDataYesYesTrustCorpNoPosInTrust, LAST_MODIFIED, CASE_ID);

        practitionersPositionInTrustRule.validate(caseDetails);
    }

    @Test
    public void shouldNotThrowErrorYesNoTrustCorpNoPosInTrust() {
        final CaseDetails caseDetails =
                new CaseDetails(caseDataYesNoTrustCorpNoPosInTrust, LAST_MODIFIED, CASE_ID);

        practitionersPositionInTrustRule.validate(caseDetails);
    }

    @Test
    public void shouldNotThrowErrorNoNoTrustCorpNoPosInTrust() {
        final CaseDetails caseDetails =
                new CaseDetails(caseDataNoNoTrustCorpNoPosInTrust, LAST_MODIFIED, CASE_ID);

        practitionersPositionInTrustRule.validate(caseDetails);
    }

    @Test
    public void shouldNotThrowErrorNoYesTrustCorpWithPosInTrust() {
        final CaseDetails caseDetails =
                new CaseDetails(caseDataNoYesTrustCorpWithPosInTrust, LAST_MODIFIED, CASE_ID);

        practitionersPositionInTrustRule.validate(caseDetails);
    }


    @Test
    public void shouldThrowPositionRequiredNoYesTrustCorp() {
        final CaseDetails caseDetailsTc =
                new CaseDetails(caseDataNoYesTrustCorpNoPosInTrust, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            practitionersPositionInTrustRule.validate(caseDetailsTc);
        })
        .isInstanceOf(BusinessValidationException.class)
        .hasMessage("Position in Trust must be specified for question probatePractitionersPositionInTrust for case id "
                + "12345678987654321 if practitioner is not named in the will and is applying.");
    }
}
