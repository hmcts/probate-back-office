package uk.gov.hmcts.probate.service.solicitorexecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class NotApplyingExecutorsMapperTest {

    @InjectMocks
    private NotApplyingExecutorsMapper underTest;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
        AdditionalExecutor solsAdditionalExecutor1 = getSolsAddExec(1, false, "Reason1");
        AdditionalExecutor solsAdditionalExecutor2 = getSolsAddExec(2, false, "Reason2");
        AdditionalExecutor solsAdditionalExecutor3 = getSolsAddExec(3, true, null);
        List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList = Arrays.asList(
            new CollectionMember(null, solsAdditionalExecutor1),
            new CollectionMember(null, solsAdditionalExecutor2),
            new CollectionMember(null, solsAdditionalExecutor3)
        );
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(solsAdditionalExecutorList);

        AdditionalExecutorNotApplying additionalExecutorNotApplying1 = getAddExec(1, "Reason1");
        AdditionalExecutorNotApplying additionalExecutorNotApplying2 = getAddExec(2, "Reason2");
        AdditionalExecutorNotApplying additionalExecutorNotApplying3 = getAddExec(3, "Reason1");
        List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplyingList = Arrays.asList(
            new CollectionMember(null, additionalExecutorNotApplying1),
            new CollectionMember(null, additionalExecutorNotApplying2),
            new CollectionMember(null, additionalExecutorNotApplying3));
        when(caseDataMock.getAdditionalExecutorsNotApplying()).thenReturn(additionalExecutorsNotApplyingList);
    }

    @Test
    public void shouldGetNotApplyingExecsWithPrimary() {
        when(caseDataMock.getPrimaryApplicantForenames()).thenReturn("primaryFN");
        when(caseDataMock.getPrimaryApplicantSurname()).thenReturn("primarySN");
        when(caseDataMock.getPrimaryApplicantIsApplying()).thenReturn("No");
        when(caseDataMock.getSolsPrimaryExecutorNotApplyingReason()).thenReturn("Reason1");

        List<AdditionalExecutorNotApplying> execsNotApplying = underTest
            .getAllExecutorsNotApplying(caseDataMock, "Reason1");

        assertEquals(4, execsNotApplying.size());
    }

    @Test
    public void shouldGetNotApplyingExecsWithoutPrimary() {
        when(caseDataMock.getPrimaryApplicantForenames()).thenReturn("primaryFN");
        when(caseDataMock.getPrimaryApplicantSurname()).thenReturn("primarySN");
        when(caseDataMock.getPrimaryApplicantIsApplying()).thenReturn("Yes");

        List<AdditionalExecutorNotApplying> execsNotApplying = underTest
            .getAllExecutorsNotApplying(caseDataMock, "Reason1");

        assertEquals(3, execsNotApplying.size());
    }

    private AdditionalExecutor getSolsAddExec(int num, boolean applying, String reason) {
        return AdditionalExecutor.builder()
            .additionalExecForenames("solsAdditionalExecutor" + num + "FN")
            .additionalExecLastname("solsAdditionalExecutor\"+num+\"LN")
            .additionalApplying(applying ? "Yes" : "No")
            .additionalExecReasonNotApplying(applying ? null : reason)
            .build();
    }

    private AdditionalExecutorNotApplying getAddExec(int num, String reason) {
        return AdditionalExecutorNotApplying.builder()
            .notApplyingExecutorName("solsAdditionalExecutor" + num + "N")
            .notApplyingExecutorReason(reason)
            .build();
    }

}