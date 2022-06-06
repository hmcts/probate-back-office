package uk.gov.hmcts.probate.businessrule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class PA14FormBusinessRuleTest {

    @InjectMocks
    private PA14FormBusinessRule underTest;

    @Mock
    private CaseData mockCaseData;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    public void shouldBeApplicableForPrimaryNotApplyingExecRenounced() {
        when(mockCaseData.getPrimaryApplicantIsApplying()).thenReturn(NO);
        when(mockCaseData.getSolsPrimaryExecutorNotApplyingReason()).thenReturn("MentallyIncapable");
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldNotBeApplicableForPrimaryNotApplyingExecPowerReserved() {
        when(mockCaseData.getPrimaryApplicantIsApplying()).thenReturn(NO);
        when(mockCaseData.getSolsPrimaryExecutorNotApplyingReason()).thenReturn("Renunciation");
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldBeApplicableForSolIsExecRenounced() {
        when(mockCaseData.getSolsSolicitorIsExec()).thenReturn(YES);
        when(mockCaseData.getSolsSolicitorIsApplying()).thenReturn(NO);
        when(mockCaseData.getSolsSolicitorNotApplyingReason()).thenReturn("MentallyIncapable");
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldNotBeApplicableForSolIsExecPowerReserved() {
        when(mockCaseData.getSolsSolicitorIsExec()).thenReturn(YES);
        when(mockCaseData.getSolsSolicitorIsApplying()).thenReturn(NO);
        when(mockCaseData.getSolsSolicitorNotApplyingReason()).thenReturn("Renunciation");
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldBeApplicableForNotApplyingExecRenounced() {
        when(mockCaseData.getOtherExecutorExists()).thenReturn(YES);
        List<CollectionMember<AdditionalExecutor>> execs = new ArrayList();
        CollectionMember<AdditionalExecutor> exec1 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(YES).build());
        CollectionMember<AdditionalExecutor> exec2 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(NO)
                .additionalExecReasonNotApplying("MentallyIncapable")
                .build());
        CollectionMember<AdditionalExecutor> exec3 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(NO)
                .additionalExecReasonNotApplying("MentallyIncapable")
                .build());
        execs.add(exec1);
        execs.add(exec2);
        execs.add(exec3);
        when(mockCaseData.getSolsAdditionalExecutorList()).thenReturn(execs);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldNotBeApplicableForNotApplyingExecPowerReserved() {
        when(mockCaseData.getOtherExecutorExists()).thenReturn(YES);
        List<CollectionMember<AdditionalExecutor>> execs = new ArrayList();
        CollectionMember<AdditionalExecutor> exec1 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(YES).build());
        CollectionMember<AdditionalExecutor> exec2 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(NO)
                .additionalExecReasonNotApplying("PowerReserved")
                .build());
        execs.add(exec1);
        execs.add(exec2);
        when(mockCaseData.getSolsAdditionalExecutorList()).thenReturn(execs);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldNotBeApplicableForNotApplyingExecPowerReservedNoOtherExecs() {
        List<CollectionMember<AdditionalExecutor>> execs = new ArrayList();
        CollectionMember<AdditionalExecutor> exec1 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(YES).build());
        CollectionMember<AdditionalExecutor> exec2 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(NO)
                .additionalExecReasonNotApplying("PowerReserved")
                .build());
        execs.add(exec1);
        execs.add(exec2);
        when(mockCaseData.getSolsAdditionalExecutorList()).thenReturn(execs);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldBeApplicableForNotApplyingExecsRenounced() {
        when(mockCaseData.getOtherExecutorExists()).thenReturn(YES);
        List<CollectionMember<AdditionalExecutorNotApplying>> execs = new ArrayList();
        CollectionMember<AdditionalExecutorNotApplying> exec1 =
            new CollectionMember(AdditionalExecutorNotApplying.builder().build());
        CollectionMember<AdditionalExecutorNotApplying> exec2 =
            new CollectionMember(AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorReason("MentallyIncapable")
                .build());
        execs.add(exec1);
        execs.add(exec2);
        when(mockCaseData.getAdditionalExecutorsNotApplying()).thenReturn(execs);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldNotBeApplicableForNotApplyingExecsRenounced() {
        when(mockCaseData.getOtherExecutorExists()).thenReturn(YES);
        List<CollectionMember<AdditionalExecutorNotApplying>> execs = new ArrayList();
        CollectionMember<AdditionalExecutorNotApplying> exec1 =
            new CollectionMember(AdditionalExecutorNotApplying.builder().build());
        execs.add(exec1);
        when(mockCaseData.getAdditionalExecutorsNotApplying()).thenReturn(execs);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldNotBeApplicableForNotApplyingExecsNull() {
        when(mockCaseData.getOtherExecutorExists()).thenReturn(YES);
        when(mockCaseData.getAdditionalExecutorsNotApplying()).thenReturn(null);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

}
