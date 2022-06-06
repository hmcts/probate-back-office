package uk.gov.hmcts.probate.service.solicitorexecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class NotApplyingExecutorsMapperTest {

    @InjectMocks
    private NotApplyingExecutorsMapper underTest;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
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
    void shouldGetNotApplyingExecsWithPrimary() {
        when(caseDataMock.getSolsSOTName()).thenReturn("sotFN sotLN");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");
        when(caseDataMock.getSolsSolicitorNotApplyingReason()).thenReturn("Reason1");

        List<AdditionalExecutorNotApplying> execsNotApplying = underTest
            .getAllExecutorsNotApplying(caseDataMock, "Reason1");

        assertEquals(4, execsNotApplying.size());
    }

    @Test
    void shouldGetNotApplyingExecsWithPrimaryIncuded() {
        when(caseDataMock.getSolsSOTName()).thenReturn("solsAdditionalExecutorFN solsAdditionalExecutorLN");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");
        when(caseDataMock.getSolsSolicitorNotApplyingReason()).thenReturn("Reason1");

        List<AdditionalExecutorNotApplying> execsNotApplying = underTest
            .getAllExecutorsNotApplying(caseDataMock, "Reason1");

        assertEquals(4, execsNotApplying.size());
    }

    @Test
    void shouldGetApplyingExecsWithoutPrimaryWithSOTName() {
        when(caseDataMock.getSolsSOTName()).thenReturn("sotFN sotLN");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");

        List<AdditionalExecutorNotApplying> execsNotApplying = underTest
            .getAllExecutorsNotApplying(caseDataMock, "Reason1");

        assertEquals(3, execsNotApplying.size());
    }

    @Test
    void shouldGetNotApplyingExecsWithoutNullLists() {
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(null);
        when(caseDataMock.getAdditionalExecutorsNotApplying()).thenReturn(null);

        when(caseDataMock.getSolsSOTName()).thenReturn("sotFN sotLN");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");
        when(caseDataMock.getSolsSolicitorNotApplyingReason()).thenReturn("Reason1");

        List<AdditionalExecutorNotApplying> execsNotApplying = underTest
            .getAllExecutorsNotApplying(caseDataMock, "Reason1");

        assertEquals(1, execsNotApplying.size());
    }

    private AdditionalExecutor getSolsAddExec(int num, boolean applying, String reason) {
        return AdditionalExecutor.builder()
            .additionalExecForenames("solsAdditionalExecutor" + num + "FN")
            .additionalExecLastname("solsAdditionalExecutor" + num + "LN")
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
