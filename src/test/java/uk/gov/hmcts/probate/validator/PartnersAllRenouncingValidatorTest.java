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

public class PartnersAllRenouncingValidatorTest {
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    @InjectMocks
    private PartnersAllRenouncingValidator partnersAllRenouncingValidator;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaseData caseDataSuccessorFirmAllRenouncing;
    private CaseData caseDataFirmAllRenouncing;
    private CaseData caseDataNotAllRenouncing;
    private CaseData caseDataNoNoAllRenouncing;
    private CaseData caseDataYesYesAllRenouncing;
    private CaseData caseDataYesNoAllRenouncing;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caseDataSuccessorFirmAllRenouncing = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("No")
            .solsSolicitorIsApplying("Yes")
            .titleAndClearingType("TCTPartSuccAllRenouncing")
            .registryLocation("Bristol").build();

        caseDataFirmAllRenouncing = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("No")
            .solsSolicitorIsApplying("Yes")
            .titleAndClearingType("TCTPartAllRenouncing")
            .registryLocation("Bristol").build();

        caseDataNotAllRenouncing = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("No")
            .solsSolicitorIsApplying("Yes")
            .titleAndClearingType("TCTTrustCorpResWithApp")
            .registryLocation("Bristol").build();

        caseDataNoNoAllRenouncing = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("No")
            .solsSolicitorIsApplying("No")
            .titleAndClearingType("TCTPartAllRenouncing")
            .registryLocation("Bristol").build();

        caseDataYesYesAllRenouncing = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("Yes")
            .solsSolicitorIsApplying("Yes")
            .titleAndClearingType("TCTPartAllRenouncing")
            .registryLocation("Bristol").build();

        caseDataYesNoAllRenouncing = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("Yes")
            .solsSolicitorIsApplying("No")
            .titleAndClearingType("TCTPartAllRenouncing")
            .registryLocation("Bristol").build();

    }

    @Test
    public void shouldThrowErrorSuccAllRenouncing() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataSuccessorFirmAllRenouncing, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            partnersAllRenouncingValidator.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("Probate practitioner cannot be applying if part of a group which is "
                + "all renouncing for case id 12345678987654321");
    }

    @Test
    public void shouldThrowErrorAllRenouncing() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataFirmAllRenouncing, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            partnersAllRenouncingValidator.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("Probate practitioner cannot be applying if part of a group which is "
                + "all renouncing for case id 12345678987654321");
    }

    @Test
    public void shouldNotThrowError() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataNotAllRenouncing, LAST_MODIFIED, CASE_ID);

        partnersAllRenouncingValidator.validate(caseDetails);

    }

    @Test
    public void shouldNotThrowErrorNoNoAllRenouncing() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataNoNoAllRenouncing, LAST_MODIFIED, CASE_ID);

        partnersAllRenouncingValidator.validate(caseDetails);

    }

    @Test
    public void shouldNotThrowErrorYesYesAllRenouncing() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataYesYesAllRenouncing, LAST_MODIFIED, CASE_ID);

        partnersAllRenouncingValidator.validate(caseDetails);

    }

    @Test
    public void shouldNotThrowErrorYesNoAllRenouncing() {
        final CaseDetails caseDetails =
            new CaseDetails(caseDataYesNoAllRenouncing, LAST_MODIFIED, CASE_ID);

        partnersAllRenouncingValidator.validate(caseDetails);

    }
}
