package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


class ZeroApplyingExecutorsValidationRuleTest {

    @InjectMocks
    private ZeroApplyingExecutorsValidationRule underTest;

    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        caseDataMock = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("No")
            .numberOfExecutors(0L)
            .otherExecutorExists("No")
            .solsSolicitorIsApplying("No")
            .titleAndClearingType("TCTPartSuccPowerRes")
            .primaryApplicantForenames("Probate")
            .primaryApplicantSurname("Practitioner")
            .anyOtherApplyingPartners("No")
            .registryLocation("Bristol")
            .build();
    }

    @Test
    void shouldReturnErrorForZeroExecutors() {
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);

        BusinessValidationException bve = assertThrows(BusinessValidationException.class, () -> {
            underTest.validate(caseDetailsMock);
        });

        assertThat(bve.getMessage(),
                containsString("There must be at least one executor applying."
                        + " You have not added an applying probate practitioner or any executors for case id 0"));
    }
}
