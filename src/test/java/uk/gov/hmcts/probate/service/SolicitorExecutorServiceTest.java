package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.util.CommonVariables.*;

@RunWith(MockitoJUnitRunner.class)
public class SolicitorExecutorServiceTest {

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

    private CaseData.CaseDataBuilder caseDataBuilder;

    @Before
    public void setup() {
        initMocks(this);

        caseDataBuilder = CaseData.builder()
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME + " UPDATED")
                .solsSolicitorAddress(SOLICITOR_ADDRESS)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        additionalExecutorsApplyingMock = new ArrayList<>();
        additionalExecutorsApplyingMock.add(new CollectionMember<>(EXEC_ID, EXECUTOR_APPLYING));
        additionalExecutorsApplyingMock.add(new CollectionMember<>(SOLICITOR_ID, EXECUTOR_APPLYING));

        additionalExecutorsNotApplyingMock = new ArrayList<>();
        additionalExecutorsNotApplyingMock.add(new CollectionMember<>(EXEC_ID, EXECUTOR_NOT_APPLYING));
        additionalExecutorsNotApplyingMock.add(new CollectionMember<>(SOLICITOR_ID, EXECUTOR_NOT_APPLYING));

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
    }

    @Test
    public void shouldUpdateSolNotApplyingExec() {
        List<CollectionMember<AdditionalExecutorNotApplying>> newExecsNotApplying;
        newExecsNotApplying = underTest.addSolicitorToNotApplyingList(callbackRequestMock.getCaseDetails().getData(), additionalExecutorsNotApplyingMock);

        assertEquals(2, newExecsNotApplying.size());
        assertEquals(SOLICITOR_SOT_FULLNAME + " UPDATED", newExecsNotApplying.get(1).getValue().getNotApplyingExecutorName());
        assertEquals(SOLICITOR_ID, newExecsNotApplying.get(1).getId());
    }

    @Test
    public void shouldAddSolNotApplyingExec() {
        additionalExecutorsNotApplyingMock.remove(1);
        assertEquals(1, additionalExecutorsNotApplyingMock.size());
        assertEquals(EXEC_ID, additionalExecutorsNotApplyingMock.get(0).getId());

        List<CollectionMember<AdditionalExecutorNotApplying>> newExecsNotApplying;
        newExecsNotApplying = underTest.addSolicitorToNotApplyingList(callbackRequestMock.getCaseDetails().getData(), additionalExecutorsNotApplyingMock);

        assertEquals(2, newExecsNotApplying.size());
        assertEquals(SOLICITOR_ID, newExecsNotApplying.get(1).getId());
    }

    @Test
    public void shouldRemoveSolApplyingExec() {
        additionalExecutorsApplyingMock.remove(1);
        List<CollectionMember<AdditionalExecutorApplying>> newExecsApplying;
        newExecsApplying = underTest.removeSolicitorFromApplyingList(additionalExecutorsApplyingMock);

        assertEquals(1, newExecsApplying.size());
        assertEquals(EXEC_NAME, newExecsApplying.get(0).getValue().getApplyingExecutorName());
        assertEquals(EXEC_ID, newExecsApplying.get(0).getId());
    }

    @Test
    public void shouldRemoveSolNotApplyingExec() {
        additionalExecutorsNotApplyingMock.remove(1);
        List<CollectionMember<AdditionalExecutorNotApplying>> newExecsNotApplying;
        newExecsNotApplying = underTest.removeSolicitorFromNotApplyingList(additionalExecutorsNotApplyingMock);

        assertEquals(1, newExecsNotApplying.size());
        assertEquals(EXEC_NAME, newExecsNotApplying.get(0).getValue().getNotApplyingExecutorName());
        assertEquals(EXEC_ID, newExecsNotApplying.get(0).getId());
    }
}