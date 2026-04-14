package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.YES;

class AttorneyAppointedExecutorValidationRuleTest {

    @InjectMocks
    private AttorneyAppointedExecutorValidationRule attorneyAppointedExecutorValidationRule;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaseData caseDataMock;

    private List<CollectionMember<AdditionalExecutorNotApplying>> powerOfAttorneyList;
    private List<CollectionMember<AdditionalExecutorNotApplying>> nonPowerOfAttorneyList;
    private List<CollectionMember<AdditionalExecutorNotApplying>> emptyList;

    private static final String ATTORNEY_APPOINTED_EXECUTOR = "AttorneyAppointedExec";
    private static final String ATTORNEY_APPOINTED_EXECUTOR_WELSH = "AttorneyAppointedExecWelsh";
    private static final String ATTORNEY_APPOINTED_EXECUTOR_MESSAGE =
            "Cannot have an executor and then appoint another as an attorney";
    private static final String ATTORNEY_APPOINTED_EXECUTOR_WELSH_MESSAGE =
            "Ni all gael ysgutor ac yna penodi un arall yn atwrnai";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        powerOfAttorneyList = List.of(
                new CollectionMember<>(
                        "1",
                        AdditionalExecutorNotApplying.builder()
                                .notApplyingExecutorName("Ron Swanson")
                                .notApplyingExecutorReason("PowerOfAttorney")
                                .build()
                )
        );
        nonPowerOfAttorneyList = List.of(
                new CollectionMember<>(
                        "1",
                        AdditionalExecutorNotApplying.builder()
                                .notApplyingExecutorName("Ronald McDonald")
                                .notApplyingExecutorReason("Renunciation")
                                .build()
                )
        );
        emptyList = List.of();
    }

    @Test
    void shouldThrowExceptionWhenAppointExecYesAndPowerOfAttorneyExists() {
        caseDataMock = CaseData.builder()
                .appointExec(YES)
                .additionalExecutorsNotApplying(powerOfAttorneyList)
                .build();

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDetailsMock.getId()).thenReturn(1L);

        String[] args = {"1"};
        when(businessValidationMessageRetriever.getMessage(ATTORNEY_APPOINTED_EXECUTOR, args, Locale.UK))
                .thenReturn(ATTORNEY_APPOINTED_EXECUTOR_MESSAGE);
        when(businessValidationMessageRetriever.getMessage(ATTORNEY_APPOINTED_EXECUTOR_WELSH, args, Locale.UK))
                .thenReturn(ATTORNEY_APPOINTED_EXECUTOR_WELSH_MESSAGE);

        BusinessValidationException exception = assertThrows(
                BusinessValidationException.class,
                () -> attorneyAppointedExecutorValidationRule.validate(caseDetailsMock)
        );

        assertThat(exception.getMessage(),
                containsString("Cannot have an executor and then appoint another as an attorney for case id 1"));
    }

    @Test
    void shouldNotThrowWhenAppointExecYesButNoPowerOfAttorneyReason() {
        caseDataMock = CaseData.builder()
                .appointExec("YES")
                .additionalExecutorsNotApplying(nonPowerOfAttorneyList)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        assertDoesNotThrow(() -> attorneyAppointedExecutorValidationRule.validate(caseDetailsMock));
    }

    @Test
    void shouldNotThrowWhenAppointExecNoEvenIfPowerOfAttorneyExists() {
        caseDataMock = CaseData.builder()
                .appointExec("NO")
                .additionalExecutorsNotApplying(powerOfAttorneyList)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        assertDoesNotThrow(() -> attorneyAppointedExecutorValidationRule.validate(caseDetailsMock));
    }

    @Test
    void shouldNotThrowWhenListIsEmpty() {
        caseDataMock = CaseData.builder()
                .appointExec("YES")
                .additionalExecutorsNotApplying(emptyList)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        assertDoesNotThrow(() -> attorneyAppointedExecutorValidationRule.validate(caseDetailsMock));
    }

    @Test
    void shouldNotThrowWhenListIsNull() {
        caseDataMock = CaseData.builder()
                .appointExec("YES")
                .additionalExecutorsNotApplying(null)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        assertDoesNotThrow(() -> attorneyAppointedExecutorValidationRule.validate(caseDetailsMock));
    }
}