package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@RunWith(MockitoJUnitRunner.class)
public class SolicitorExecutorServiceTest {
    private static final String SOLICITOR_ID = "solicitor";
    private static final String SOLICITOR_SOT_ID = "solicitor";
    private static final String SOLICITOR_SOT_NAME = "Solicitor_fn Solicitor_mn Solicitor_ln";
    private static final String SOLICITOR_SOT_FORENAMES = "Solicitor_fn Solicitor_mn";
    private static final String SOLICITOR_SOT_SURNAME = "Solicitor_ln";
    private static final String EXEC1_APPLYING_NAME = "ExecApplying1_fn ExecApplying1_mn ExecApplying1_ln";
    private static final String EXEC1_NOT_APPLYING_NAME = "ExecNotApplying1_fn ExecNotApplying1_mn ExecNotApplying1_ln";
    private static final String SOLICITOR_FIRM_LINE1 = "Firm St";
    private static final String SOLICITOR_FIRM_POSTCODE = "postcode";
    private static final String SOLICITOR_SOT_NOT_APPLYING_REASON = "Power reserved";
    private static final String EXEC_NOT_APPLYING_REASON = "Power reserved";
    private static final AdditionalExecutorApplying ADDITIONAL_SOL_EXECUTOR_APPLYING =
        AdditionalExecutorApplying.builder()
            .applyingExecutorName(SOLICITOR_SOT_NAME)
            .build();
    private static final AdditionalExecutorNotApplying ADDITIONAL_SOL_EXECUTOR_NOT_APPLYING =
        AdditionalExecutorNotApplying.builder()
            .notApplyingExecutorName(SOLICITOR_SOT_NAME)
            .notApplyingExecutorReason(SOLICITOR_SOT_NOT_APPLYING_REASON)
            .build();
    private static final AdditionalExecutorApplying ADDITIONAL_EXECUTOR_APPLYING = AdditionalExecutorApplying.builder()
        .applyingExecutorName(EXEC1_APPLYING_NAME)
        .build();
    private static final AdditionalExecutorNotApplying ADDITIONAL_EXECUTOR_NOT_APPLYING =
        AdditionalExecutorNotApplying.builder()
            .notApplyingExecutorName(EXEC1_NOT_APPLYING_NAME)
            .notApplyingExecutorReason(EXEC_NOT_APPLYING_REASON)
            .build();
    private static final AdditionalExecutor SOLICITOR_ADDITIONAL_EXECUTOR_APPLYING = AdditionalExecutor.builder()
        .additionalExecForenames(SOLICITOR_SOT_FORENAMES)
        .additionalExecLastname(SOLICITOR_SOT_SURNAME)
        .additionalExecNameOnWill(NO)
        .additionalApplying(YES)
        .additionalExecAddress(SolsAddress.builder().addressLine1(SOLICITOR_FIRM_LINE1)
            .postCode(SOLICITOR_FIRM_POSTCODE).build())
        .build();
    private static final AdditionalExecutor SOLICITOR_ADDITIONAL_EXECUTOR_NOT_APPLYING = AdditionalExecutor.builder()
        .additionalExecForenames(SOLICITOR_SOT_FORENAMES)
        .additionalExecLastname(SOLICITOR_SOT_SURNAME)
        .additionalExecNameOnWill(NO)
        .additionalApplying(NO)
        .additionalExecReasonNotApplying(SOLICITOR_SOT_NOT_APPLYING_REASON)
        .build();
    
    @InjectMocks
    private SolicitorExecutorService underTest;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplyingMock;
    @Mock
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplyingMock;
    @Mock
    private List<CollectionMember<AdditionalExecutor>> solAdditionalExecutorsApplyingMock;
    @Mock
    private List<CollectionMember<AdditionalExecutor>> solAdditionalExecutorsNotApplyingMock;
    private CaseData.CaseDataBuilder caseDataBuilder;

    @Before
    public void setup() {
        initMocks(this);

        caseDataBuilder = CaseData.builder()
            .solsSOTForenames(SOLICITOR_SOT_FORENAMES)
            .solsSOTSurname(SOLICITOR_SOT_SURNAME + " UPDATED")
            .solsSolicitorAddress(
                SolsAddress.builder().addressLine1(SOLICITOR_FIRM_LINE1).postCode(SOLICITOR_FIRM_POSTCODE).build())
            .solsSolicitorNotApplyingReason(SOLICITOR_SOT_NOT_APPLYING_REASON);

        additionalExecutorsApplyingMock = new ArrayList<>();
        additionalExecutorsNotApplyingMock = new ArrayList<>();
        additionalExecutorsApplyingMock.add(new CollectionMember<>(null, ADDITIONAL_EXECUTOR_APPLYING));
        additionalExecutorsNotApplyingMock.add(new CollectionMember<>(null, ADDITIONAL_EXECUTOR_NOT_APPLYING));
        additionalExecutorsApplyingMock.add(new CollectionMember<>(SOLICITOR_SOT_ID, ADDITIONAL_SOL_EXECUTOR_APPLYING));
        additionalExecutorsNotApplyingMock
            .add(new CollectionMember<>(SOLICITOR_SOT_ID, ADDITIONAL_SOL_EXECUTOR_NOT_APPLYING));
        solAdditionalExecutorsApplyingMock
            .add(new CollectionMember<>(SOLICITOR_SOT_ID, SOLICITOR_ADDITIONAL_EXECUTOR_APPLYING));
        solAdditionalExecutorsNotApplyingMock
            .add(new CollectionMember<>(SOLICITOR_SOT_ID, SOLICITOR_ADDITIONAL_EXECUTOR_NOT_APPLYING));

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
    }

    @Test
    public void shouldUpdateSolApplyingExec() {
        List<CollectionMember<AdditionalExecutorApplying>> newExecsApplying;
        newExecsApplying = underTest.updateSolicitorApplyingExecutor(callbackRequestMock.getCaseDetails().getData(),
            additionalExecutorsApplyingMock);

        assertEquals(2, newExecsApplying.size());
        assertEquals(SOLICITOR_SOT_NAME + " UPDATED", newExecsApplying.get(1).getValue().getApplyingExecutorName());
        assertEquals(SOLICITOR_ID, newExecsApplying.get(1).getId());
    }

    @Test
    public void shouldUpdateSolNotApplyingExec() {
        List<CollectionMember<AdditionalExecutorNotApplying>> newExecsNotApplying;
        newExecsNotApplying = underTest
            .addSolicitorToNotApplyingList(callbackRequestMock.getCaseDetails().getData(),
                additionalExecutorsNotApplyingMock);

        assertEquals(2, newExecsNotApplying.size());
        assertEquals(SOLICITOR_SOT_NAME + " UPDATED",
            newExecsNotApplying.get(1).getValue().getNotApplyingExecutorName());
        assertEquals(SOLICITOR_SOT_ID, newExecsNotApplying.get(1).getId());
    }

    @Test
    public void shouldAddSolApplyingExec() {
        additionalExecutorsApplyingMock.remove(1);
        assertEquals(1, additionalExecutorsApplyingMock.size());
        assertNull(additionalExecutorsApplyingMock.get(0).getId());

        List<CollectionMember<AdditionalExecutorApplying>> newExecsApplying;
        newExecsApplying = underTest.updateSolicitorApplyingExecutor(callbackRequestMock.getCaseDetails().getData(),
            additionalExecutorsApplyingMock);

        assertEquals(2, newExecsApplying.size());
        assertEquals(SOLICITOR_ID, newExecsApplying.get(1).getId());
    }

    @Test
    public void shouldAddSolNotApplyingExec() {
        additionalExecutorsNotApplyingMock.remove(1);
        assertEquals(1, additionalExecutorsNotApplyingMock.size());
        assertNull(additionalExecutorsNotApplyingMock.get(0).getId());

        List<CollectionMember<AdditionalExecutorNotApplying>> newExecsNotApplying;
        newExecsNotApplying = underTest
            .addSolicitorToNotApplyingList(callbackRequestMock.getCaseDetails().getData(),
                additionalExecutorsNotApplyingMock);

        assertEquals(2, newExecsNotApplying.size());
        assertEquals(SOLICITOR_ID, newExecsNotApplying.get(1).getId());
    }

    @Test
    public void shouldRemoveSolApplyingExec() {
        additionalExecutorsApplyingMock.remove(1);
        List<CollectionMember<AdditionalExecutorApplying>> newExecsApplying;
        newExecsApplying = underTest.removeSolicitorFromApplyingList(additionalExecutorsApplyingMock);

        assertEquals(1, newExecsApplying.size());
        assertEquals(EXEC1_APPLYING_NAME, newExecsApplying.get(0).getValue().getApplyingExecutorName());
        assertNull(newExecsApplying.get(0).getId());
    }

    @Test
    public void shouldRemoveSolNotApplyingExec() {
        additionalExecutorsNotApplyingMock.remove(1);
        List<CollectionMember<AdditionalExecutorNotApplying>> newExecsNotApplying;
        newExecsNotApplying = underTest.removeSolicitorFromNotApplyingList(additionalExecutorsNotApplyingMock);

        assertEquals(1, newExecsNotApplying.size());
        assertEquals(EXEC1_NOT_APPLYING_NAME, newExecsNotApplying.get(0).getValue().getNotApplyingExecutorName());
        assertNull(newExecsNotApplying.get(0).getId());
    }

    @Test
    public void shouldAddSolicitorAsNotApplyingExecToList() {
        caseDataBuilder = CaseData.builder()
                .solsSOTForenames(SOLICITOR_SOT_FORENAMES)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorAddress(mock(SolsAddress.class))
                .solsSolicitorNotApplyingReason(SOLICITOR_SOT_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        List<CollectionMember<AdditionalExecutor>> updateExecsList =
                underTest.addSolicitorAsNotApplyingExecutorToList(callbackRequestMock.getCaseDetails().getData());

        assertEquals(1, updateExecsList.size());
        assertEquals(SOLICITOR_ID, updateExecsList.get(0).getId());
        assertEquals(SOLICITOR_ADDITIONAL_EXECUTOR_NOT_APPLYING, updateExecsList.get(0).getValue());
    }
}
