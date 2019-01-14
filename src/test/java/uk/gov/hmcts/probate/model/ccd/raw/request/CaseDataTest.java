package uk.gov.hmcts.probate.model.ccd.raw.request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class CaseDataTest {

    private static final String PRIMARY_APPLICANT_FIRST_NAME = "fName";
    private static final String PRIMARY_APPLICANT_SURNAME = "sName";
    private static final SolsAddress PRIMARY_APPLICANT_ADDRESS = mock(SolsAddress.class);
    private static final String PRIMARY_APPLICANT_NAME_ON_WILL = "willName";
    private static final String DECEASED_FIRST_NAME = "Name";
    private static final String DECEASED_SURNAME = "Surname";
    private static final String NOT_APPLYING_REASON = "not applying reason";
    private static final LocalDate LOCAL_DATE = LocalDate.of(2000,01,01);

    @Mock
    private AdditionalExecutor additionalExecutor1Mock;
    @Mock
    private AdditionalExecutor additionalExecutor2Mock;
    @Mock
    private AdditionalExecutor additionalExecutor3Mock;

    @Mock
    private CollectionMember<AdditionalExecutor> additionalExecutors1Mock;
    @Mock
    private CollectionMember<AdditionalExecutor> additionalExecutors2Mock;
    @Mock
    private CollectionMember<AdditionalExecutor> additionalExecutors3Mock;

    @InjectMocks
    private CaseData underTest;

    @Before
    public void setup() {

        initMocks(this);

        when(additionalExecutors1Mock.getValue()).thenReturn(additionalExecutor1Mock);
        when(additionalExecutors2Mock.getValue()).thenReturn(additionalExecutor2Mock);
        when(additionalExecutors3Mock.getValue()).thenReturn(additionalExecutor3Mock);

        List<CollectionMember<AdditionalExecutor>> additionalExecutorsList = new ArrayList<>();
        additionalExecutorsList.add(additionalExecutors1Mock);
        additionalExecutorsList.add(additionalExecutors2Mock);
        additionalExecutorsList.add(additionalExecutors3Mock);
        additionalExecutorsList.add(null);

        underTest = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantForenames(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(additionalExecutorsList)
                .build();
    }

    @Test
    public void shouldGetExecutorsApplying() {
        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor3Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);

        List<CollectionMember<AdditionalExecutor>> applying = underTest.getExecutorsApplyingForLegalStatement();

        assertEquals(3, applying.size());
    }


    @Test
    public void shouldGetExecutorsNotApplying() {
        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor2Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor3Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);

        List<CollectionMember<AdditionalExecutor>> notApplying = underTest.getExecutorsNotApplyingForLegalStatement();

        assertEquals(2, notApplying.size());
    }

    @Test
    public void shouldGetExecutorsCombinationsOfNulls() {
        when(additionalExecutors1Mock.getValue()).thenReturn(null);
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn(null);
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor3Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);

        List<CollectionMember<AdditionalExecutor>> notApplying = underTest.getExecutorsNotApplyingForLegalStatement();

        assertEquals(1, notApplying.size());
    }

    @Test
    public void shouldAdditionalExecutorsApplyingWhenPrimaryExecutorIsNotApplying() {
        final CaseData caseData = getCaseDataWhenPrimaryExecutorNotApplying();

        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");

        assertThat(caseData.getExecutorsApplyingForLegalStatement(), hasSize(2));
    }

    @Test
    public void shouldAdditionalExecutorsNotApplyingWhenPrimaryExecutorIsNotApplying() {
        final CaseData caseData = getCaseDataWhenPrimaryExecutorNotApplying();

        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");

        assertThat(caseData.getExecutorsNotApplyingForLegalStatement(), hasSize(2));
    }

    private CaseData getCaseDataWhenPrimaryExecutorNotApplying() {
        List<CollectionMember<AdditionalExecutor>> additionalExecutorsList = new ArrayList<>();
        additionalExecutorsList.add(additionalExecutors1Mock);
        additionalExecutorsList.add(additionalExecutors2Mock);
        additionalExecutorsList.add(additionalExecutors3Mock);
        additionalExecutorsList.add(null);
        return CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantForenames(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(NO)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(additionalExecutorsList)
                .build();
    }

    @Test
    public void shouldReturnPrimaryApplicantFullName() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .build();

        assertEquals(PRIMARY_APPLICANT_FIRST_NAME + " " + PRIMARY_APPLICANT_SURNAME,
                caseData.getPrimaryApplicantFullName());
    }

    @Test
    public void shouldReturnDeceasedFullName() {
        final CaseData caseData = CaseData.builder()
                .deceasedForenames(DECEASED_FIRST_NAME)
                .deceasedSurname(DECEASED_SURNAME)
                .build();

        assertEquals(DECEASED_FIRST_NAME + " " + DECEASED_SURNAME, caseData.getDeceasedFullName());
    }

    @Test
    public void shouldReturnDODFormattedWithST() {
        final CaseData caseData = CaseData.builder()
                .deceasedDateOfDeath(LOCAL_DATE)
                .build();

        assertEquals("1st January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    public void shouldReturnDODFormattedWithND() {
        final CaseData caseData = CaseData.builder()
                .deceasedDateOfDeath(LocalDate.of(2000,01,02))
                .build();

        assertEquals("2nd January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    public void shouldReturnDODFormattedWithRD() {
        final CaseData caseData = CaseData.builder()
                .deceasedDateOfDeath(LocalDate.of(2000,01,03))
                .build();

        assertEquals("3rd January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    public void shouldReturnDODFormattedWithTH() {
        final CaseData caseData = CaseData.builder()
                .deceasedDateOfDeath(LocalDate.of(2000,01,04))
                .build();

        assertEquals("4th January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    public void shouldThrowParseException() {
        final CaseData caseData = CaseData.builder()
                .deceasedDateOfDeath(LocalDate.of(300000,01,04))
                .build();

        assertEquals(null, caseData.getDeceasedDateOfDeathFormatted());
    }
}
