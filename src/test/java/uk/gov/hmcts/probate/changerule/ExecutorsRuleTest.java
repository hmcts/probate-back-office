package uk.gov.hmcts.probate.changerule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExecutorsRuleTest {
    private static final String YES = "Yes";
    private static final String NO = "No";

    @InjectMocks
    private ExecutorsRule undertest;

    @Mock
    private CaseData caseDataMock;
    @Mock
    private CollectionMember<AdditionalExecutor> additionalExecutors1Mock;
    @Mock
    private CollectionMember<AdditionalExecutorTrustCorps> additionalTrustCorpExecutors1Mock;
    @Mock
    private AdditionalExecutor additionalExecutor1Mock;
    @Mock
    private AdditionalExecutorTrustCorps additionalTrustCorpExecutor1Mock;

    @Before
    public void setup() {
        initMocks(this);

        List<CollectionMember<AdditionalExecutor>> additionalExecutorsList = new ArrayList<>();
        when(additionalExecutors1Mock.getValue()).thenReturn(additionalExecutor1Mock);
        additionalExecutorsList.add(additionalExecutors1Mock);
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(additionalExecutorsList);
    }

    @Test
    public void shouldStopWithoutPrimary() {
        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn(NO);
        when(caseDataMock.getPrimaryApplicantIsApplying()).thenReturn(NO);

        assertTrue(undertest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotStopWithPrimary() {
        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn(NO);
        when(caseDataMock.getPrimaryApplicantIsApplying()).thenReturn(YES);

        assertFalse(undertest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotChangeStateWithPrimary() {
        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn(YES);
        when(caseDataMock.getPrimaryApplicantIsApplying()).thenReturn(NO);

        assertFalse(undertest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldReturnFalseWhenMultipleExecutorListsAreNotEmpty() {
        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn(YES);
        when(additionalExecutors1Mock.getValue()).thenReturn(additionalExecutor1Mock);
        List<CollectionMember<AdditionalExecutor>> additionalExecutorsList = new ArrayList<>();
        additionalExecutorsList.add(additionalExecutors1Mock);
        additionalExecutorsList.add(additionalExecutors1Mock);
        when(additionalTrustCorpExecutors1Mock.getValue()).thenReturn(additionalTrustCorpExecutor1Mock);
        List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList = new ArrayList<>();
        additionalExecutorsTrustCorpList.add(additionalTrustCorpExecutors1Mock);
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(additionalExecutorsList);
        when(caseDataMock.getAdditionalExecutorsTrustCorpList()).thenReturn(additionalExecutorsTrustCorpList);
        when(caseDataMock.getPrimaryApplicantIsApplying()).thenReturn(YES);

        assertFalse(undertest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldReturnTrueWhenExecutorListIsNullAndPrimaryApplicantIsNotApplying() {
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(null);
        when(caseDataMock.getPrimaryApplicantIsApplying()).thenReturn(NO);

        assertTrue(undertest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldGetBodyMessageKey() {
        assertEquals("stopBodyNoApplyingExecutors", undertest.getConfirmationBodyMessageKey());
    }
}
