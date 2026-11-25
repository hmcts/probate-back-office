package uk.gov.hmcts.probate.businessrule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@ExtendWith(MockitoExtension.class)
class PA15FormBusinessRuleTest {

    @InjectMocks
    private PA15FormBusinessRule underTest;

    private CaseData mockCaseData;

    @Test
    void shouldBeApplicableForPrimaryNotApplyingExecRenounced() {
        mockCaseData = CaseData.builder()
            .primaryApplicantIsApplying(NO)
            .solsPrimaryExecutorNotApplyingReason("Renunciation")
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForPrimaryNotApplyingExecPowerReserved() {
        mockCaseData = CaseData.builder()
            .primaryApplicantIsApplying(NO)
            .solsPrimaryExecutorNotApplyingReason("PowerReserved")
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForSolIsExecRenounced() {
        mockCaseData = CaseData.builder()
            .solsSolicitorIsExec(YES)
            .solsSolicitorIsApplying(NO)
            .solsSolicitorNotApplyingReason("Renunciation")
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForSolIsExecPowerReserved() {
        mockCaseData = CaseData.builder()
            .solsSolicitorIsExec(YES)
            .solsSolicitorIsApplying(NO)
            .solsSolicitorNotApplyingReason("PowerReserved")
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForNotApplyingExecRenounced() {

        List<CollectionMember<AdditionalExecutor>> execs = new ArrayList();
        CollectionMember<AdditionalExecutor> exec1 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(YES).build());
        CollectionMember<AdditionalExecutor> exec2 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(NO)
                .additionalExecReasonNotApplying("Renunciation")
                .build());
        execs.add(exec1);
        execs.add(exec2);
        mockCaseData = CaseData.builder()
                .otherExecutorExists(YES)
                .solsAdditionalExecutorList(execs)
                .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForNotApplyingExecPowerReserved() {

        List<CollectionMember<AdditionalExecutor>> execs = new ArrayList();
        CollectionMember<AdditionalExecutor> exec1 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(YES).build());
        CollectionMember<AdditionalExecutor> exec2 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(NO)
                .additionalExecReasonNotApplying("PowerReserved")
                .build());
        execs.add(exec1);
        execs.add(exec2);
        mockCaseData = CaseData.builder()
                .otherExecutorExists(YES)
                .solsAdditionalExecutorList(execs)
                .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForNotApplyingExecPowerReservedNoOtherExecs() {
        List<CollectionMember<AdditionalExecutor>> execs = new ArrayList();
        CollectionMember<AdditionalExecutor> exec1 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(YES).build());
        CollectionMember<AdditionalExecutor> exec2 =
            new CollectionMember(AdditionalExecutor.builder().additionalApplying(NO)
                .additionalExecReasonNotApplying("PowerReserved")
                .build());
        execs.add(exec1);
        execs.add(exec2);
        mockCaseData = CaseData.builder()
                .solsAdditionalExecutorList(execs)
                .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForNotApplyingExecsRenounced() {
        List<CollectionMember<AdditionalExecutorNotApplying>> execs = new ArrayList();
        CollectionMember<AdditionalExecutorNotApplying> exec1 =
            new CollectionMember(AdditionalExecutorNotApplying.builder().build());
        CollectionMember<AdditionalExecutorNotApplying> exec2 =
            new CollectionMember(AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorReason("Renunciation")
                .build());
        execs.add(exec1);
        execs.add(exec2);
        mockCaseData = CaseData.builder()
                .otherExecutorExists(YES)
                .additionalExecutorsNotApplying(execs)
                .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForNotApplyingExecsRenounced() {
        List<CollectionMember<AdditionalExecutorNotApplying>> execs = new ArrayList();
        CollectionMember<AdditionalExecutorNotApplying> exec1 =
            new CollectionMember(AdditionalExecutorNotApplying.builder().build());
        execs.add(exec1);
        mockCaseData = CaseData.builder()
                .otherExecutorExists(YES)
                .additionalExecutorsNotApplying(execs)
                .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForNotApplyingExecsNull() {
        mockCaseData = CaseData.builder()
            .otherExecutorExists(YES)
            .additionalExecutorsNotApplying(null)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

}
