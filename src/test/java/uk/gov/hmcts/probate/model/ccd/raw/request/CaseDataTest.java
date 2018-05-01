package uk.gov.hmcts.probate.model.ccd.raw.request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.changerule.ChangeRule.NO;

public class CaseDataTest {

    private static final String YES = "Yes";
    private static final String PRIMARY_APP_FNAME = "fName";
    private static final String PRIMARY_APP_SNAME = "sName";
    private static final SolsAddress PRIMARY_APP_ADDRESS = mock(SolsAddress.class);
    private static final String PRIMARY_APP_NAME_ON_WILL = "willName";
    private static final String NOT_APPLYING_REASON = "not applying reason";

    @Mock
    private AdditionalExecutor additionalExecutor1Mock;
    @Mock
    private AdditionalExecutor additionalExecutor2Mock;
    @Mock
    private AdditionalExecutor additionalExecutor3Mock;

    @Mock
    private AdditionalExecutors additionalExecutors1Mock;
    @Mock
    private AdditionalExecutors additionalExecutors2Mock;
    @Mock
    private AdditionalExecutors additionalExecutors3Mock;

    @InjectMocks
    private CaseData underTest;

    @Before
    public void setup() {

        initMocks(this);

        when(additionalExecutors1Mock.getAdditionalExecutor()).thenReturn(additionalExecutor1Mock);
        when(additionalExecutors2Mock.getAdditionalExecutor()).thenReturn(additionalExecutor2Mock);
        when(additionalExecutors3Mock.getAdditionalExecutor()).thenReturn(additionalExecutor3Mock);

        List<AdditionalExecutors> additionalExecutorsList = new ArrayList<>();
        additionalExecutorsList.add(additionalExecutors1Mock);
        additionalExecutorsList.add(additionalExecutors2Mock);
        additionalExecutorsList.add(additionalExecutors3Mock);
        additionalExecutorsList.add(null);

        underTest = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APP_FNAME)
            .primaryApplicantForenames(PRIMARY_APP_SNAME)
            .primaryApplicantIsApplying(YES)
            .primaryApplicantAddress(PRIMARY_APP_ADDRESS)
            .solsExecutorAliasNames(PRIMARY_APP_NAME_ON_WILL)
            .solsAdditionalExecutorList(additionalExecutorsList)
            .build();
    }

    @Test
    public void shouldGetExecutorsApplying() {
        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor3Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);

        List<AdditionalExecutors> applying = underTest.getExecutorsApplying();

        assertEquals(3, applying.size());
    }

    @Test
    public void shouldGetExecutorsNotApplying() {
        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor2Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor3Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);

        List<AdditionalExecutors> notApplying = underTest.getExecutorsNotApplying();

        assertEquals(2, notApplying.size());
    }

    @Test
    public void shouldGetExecutorsCombinationsOfNulls() {
        when(additionalExecutors1Mock.getAdditionalExecutor()).thenReturn(null);
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn(null);
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor3Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);

        List<AdditionalExecutors> notApplying = underTest.getExecutorsNotApplying();

        assertEquals(1, notApplying.size());
    }

    @Test
    public void shouldAdditionalExecutorsApplyingWhenPrimaryExecutorIsNotApplying() {
        final CaseData caseData = getCaseDataWhenPrimaryExecutorNotApplying();

        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");

        assertThat(caseData.getExecutorsApplying(), hasSize(2));
    }

    @Test
    public void shouldAdditionalExecutorsNotApplyingWhenPrimaryExecutorIsNotApplying() {
        final CaseData caseData = getCaseDataWhenPrimaryExecutorNotApplying();

        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");

        assertThat(caseData.getExecutorsNotApplying(), hasSize(2));
    }

    private CaseData getCaseDataWhenPrimaryExecutorNotApplying() {
        List<AdditionalExecutors> additionalExecutorsList = new ArrayList<>();
        additionalExecutorsList.add(additionalExecutors1Mock);
        additionalExecutorsList.add(additionalExecutors2Mock);
        additionalExecutorsList.add(additionalExecutors3Mock);
        additionalExecutorsList.add(null);
        return CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APP_FNAME)
                .primaryApplicantForenames(PRIMARY_APP_SNAME)
                .primaryApplicantIsApplying(NO)
                .primaryApplicantAddress(PRIMARY_APP_ADDRESS)
                .solsExecutorAliasNames(PRIMARY_APP_NAME_ON_WILL)
                .solsAdditionalExecutorList(additionalExecutorsList)
                .build();
    }

}
