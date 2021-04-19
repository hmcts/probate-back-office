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

public class NoneOfTheseSelectedValidationRuleTest {
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    @InjectMocks
    private NoneOfTheseSelectedValidationRule noneOfTheseSelectedValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaseData caseDataNoYes;
    private CaseData caseDataYesNo;
    private CaseData caseDataNoNo;
    private CaseData caseDataYesYes;
    private CaseData caseDataNoYesNoneOfTheseNotSelected;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caseDataNoYes = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("No")
            .solsSolicitorIsApplying("Yes")
            .titleAndClearingType("TCTNoT")
            .registryLocation("Bristol").build();

        caseDataYesNo = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("Yes")
            .solsSolicitorIsApplying("No")
            .titleAndClearingType("TCTNoT")
            .registryLocation("Bristol").build();

        caseDataNoNo = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("No")
            .solsSolicitorIsApplying("No")
            .anyOtherApplyingPartners("No")
            .titleAndClearingType("TCTNoT")
            .registryLocation("Bristol").build();

        caseDataYesYes = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("Yes")
            .solsSolicitorIsApplying("Yes")
            .anyOtherApplyingPartners("No")
            .titleAndClearingType("TCTNoT")
            .registryLocation("Bristol").build();

        caseDataNoYesNoneOfTheseNotSelected = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("No")
            .solsSolicitorIsApplying("Yes")
            .titleAndClearingType("TCTPartSuccPowerRes")
            .registryLocation("Bristol").build();

    }

    @Test
    public void shouldThrowErrorNoneOfTheseSelected() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataNoYes, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            noneOfTheseSelectedValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("None of these selected, you need to make a paper application for case id 12345678987654321");
    }

    @Test
    public void shouldNotThrowErrorYesNo() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataYesNo, LAST_MODIFIED, CASE_ID);

        noneOfTheseSelectedValidationRule.validate(caseDetails);
    }

    @Test
    public void shouldNotThrowErrorNoNo() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataNoNo, LAST_MODIFIED, CASE_ID);

        noneOfTheseSelectedValidationRule.validate(caseDetails);
    }

    @Test
    public void shouldNotThrowErrorYesYes() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataYesYes, LAST_MODIFIED, CASE_ID);

        noneOfTheseSelectedValidationRule.validate(caseDetails);
    }

    @Test
    public void shouldNotThrowErrorNoYesNoneOfTheseNotSelected() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataNoYesNoneOfTheseNotSelected, LAST_MODIFIED, CASE_ID);

        noneOfTheseSelectedValidationRule.validate(caseDetails);
    }
}
