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

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    private static final String NO_EXECUTORS = "zeroExecutors";
    private static final String NO_EXECUTORS_WELSH = "zeroExecutorsWelsh";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnErrorForZeroExecutors() {
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

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDetailsMock.getId()).thenReturn(0L);

        String[] args = {"0"};
        when(businessValidationMessageRetriever.getMessage(NO_EXECUTORS, args, Locale.UK))
                .thenReturn("User message for no executors");
        when(businessValidationMessageRetriever.getMessage(NO_EXECUTORS_WELSH, args, Locale.UK))
                .thenReturn("Welsh user message for no executors");

        BusinessValidationException bve = assertThrows(BusinessValidationException.class, () -> {
            underTest.validate(caseDetailsMock);
        });
        assertThat(bve.getMessage(),
                containsString("There must be at least one executor applying."
                        + " You have not added an applying probate practitioner or any executors for case id 0"));
    }

    @Test
    void shouldNotThrowExceptionWhenExecutorsExist() {
        // Override the default setup for this test
        caseDataMock = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorIsExec("No")
                .numberOfExecutors(1L)
                .otherExecutorExists("No")
                .solsSolicitorIsApplying("No")
                .titleAndClearingType("TCTPartSuccPowerRes")
                .primaryApplicantForenames("Probate")
                .primaryApplicantSurname("Practitioner")
                .anyOtherApplyingPartners("No")
                .registryLocation("Bristol")
                .build();

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);

        assertDoesNotThrow(() -> underTest.validate(caseDetailsMock));
    }
}
