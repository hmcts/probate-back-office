package uk.gov.hmcts.probate.transformer;

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
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.SolicitorExecutorService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.util.CommonVariables.*;

@RunWith(MockitoJUnitRunner.class)
public class SolicitorExecutorTransformerTest {

    private CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();

    private ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder = ResponseCaseData.builder();

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private SolicitorExecutorService solicitorExecutorServiceMock;

    @Mock
    private List<CollectionMember<AdditionalExecutor>> solAdditionalExecutorsNotApplying;

    @InjectMocks
    private SolicitorExecutorTransformer solicitorExecutorTransformerMock;

    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorApplying;
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorNotApplying;

    @Before
    public void setUp() {
        additionalExecutorApplying = new ArrayList<>();
        additionalExecutorNotApplying = new ArrayList<>();

        AdditionalExecutorApplying execApplying = AdditionalExecutorApplying.builder()
                .applyingExecutorName(EXEC_NAME)
                .applyingExecutorPhoneNumber(EXEC_PHONE)
                .applyingExecutorEmail(EXEC_EMAIL)
                .applyingExecutorAddress(EXEC_ADDRESS)
                .build();
        additionalExecutorApplying.add(new CollectionMember<>(SOLICITOR_ID, execApplying));

        AdditionalExecutorNotApplying execNotApplying = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(EXEC_NAME)
                .notApplyingExecutorReason(SOLICITOR_NOT_APPLYING_REASON)
                .build();
        additionalExecutorNotApplying.add(new CollectionMember<>(SOLICITOR_ID, execNotApplying));
    }

    @Test
    public void shouldSetPrimaryApplicantDetailsWithSolicitorInfo() {

        caseDataBuilder.solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorPhoneNumber(SOLICITOR_FIRM_PHONE)
                .solsSolicitorEmail(SOLICITOR_FIRM_EMAIL)
                .solsSolicitorAddress(SOLICITOR_ADDRESS);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.setPrimaryApplicantFieldsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(SOLICITOR_SOT_FORENAME, responseCaseData.getPrimaryApplicantForenames());
        assertEquals(SOLICITOR_SOT_SURNAME, responseCaseData.getPrimaryApplicantSurname());
        assertEquals(SOLICITOR_FIRM_PHONE, responseCaseData.getPrimaryApplicantPhoneNumber());
        assertEquals(SOLICITOR_FIRM_EMAIL, responseCaseData.getPrimaryApplicantEmailAddress());
        assertEquals(SOLICITOR_ADDRESS, responseCaseData.getPrimaryApplicantAddress());
        assertNull(responseCaseDataBuilder.build().getPrimaryApplicantAlias());
        assertEquals(NO, responseCaseData.getPrimaryApplicantHasAlias());
        assertEquals(YES, responseCaseData.getPrimaryApplicantIsApplying());
        assertNull(responseCaseData.getSolsSolicitorNotApplyingReason());
        assertNull(responseCaseData.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldRemoveSolicitorAsApplicant() {
        caseDataBuilder
                .solsSolicitorIsExec(NO)
                .solsSolicitorIsApplying(NO)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.setPrimaryApplicantFieldsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertEquals(NO, responseCaseData.getSolsSolicitorIsApplying());
        assertNull(responseCaseData.getSolsSolicitorNotApplyingReason());
    }


    @Test
    public void shouldRemoveSolicitorDetailsFromPrimaryApplicant() {
        // Solicitor names are same as primary names.
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .primaryApplicantForenames(SOLICITOR_SOT_FORENAME)
                .primaryApplicantSurname(SOLICITOR_SOT_SURNAME)
                .primaryApplicantEmailAddress(SOLICITOR_FIRM_EMAIL)
                .primaryApplicantPhoneNumber(SOLICITOR_FIRM_PHONE)
                .primaryApplicantAddress(SOLICITOR_ADDRESS)
                .primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES)
                .primaryApplicantHasAlias(YES)
                .primaryApplicantIsApplying(YES)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.setPrimaryApplicantFieldsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertNull(responseCaseData.getPrimaryApplicantForenames());
        assertNull(responseCaseData.getPrimaryApplicantSurname());
        assertNull(responseCaseData.getPrimaryApplicantPhoneNumber());
        assertNull(responseCaseData.getPrimaryApplicantEmailAddress());
        assertNull(responseCaseData.getPrimaryApplicantAddress());
        assertNull(responseCaseData.getPrimaryApplicantAlias());
        assertNull(responseCaseData.getPrimaryApplicantHasAlias());
        assertNull(responseCaseData.getPrimaryApplicantIsApplying());
        assertNull(responseCaseData.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldRemoveSolicitorNotApplyingReasonWhenSolicitorApplying() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .primaryApplicantForenames(PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);


        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.setPrimaryApplicantFieldsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertNull(responseCaseDataBuilder.build().getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldRemoveSolicitorNotApplyingReasonWhenSolicitorApplyingIsNull() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(null)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .primaryApplicantForenames(PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);


        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.setPrimaryApplicantFieldsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertNull(responseCaseDataBuilder.build().getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldNotChangeResponseCaseData() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .primaryApplicantForenames(PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .solsSolicitorNotApplyingReason("Not applying");

        responseCaseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        solicitorExecutorTransformerMock.setPrimaryApplicantFieldsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertEquals(YES, responseCaseData.getSolsSolicitorIsExec());
        assertEquals(SOLICITOR_SOT_FORENAME, responseCaseData.getSolsSOTForenames());
        assertEquals(SOLICITOR_SOT_SURNAME, responseCaseData.getSolsSOTSurname());
        assertEquals(NO, responseCaseData.getSolsSolicitorIsApplying());
        assertEquals(SOLICITOR_NOT_APPLYING_REASON, responseCaseData.getSolsSolicitorNotApplyingReason());
    }

    @Test
    public void shouldUpdateExecutorListsWhenSolicitorIs_Exec_NotApplying() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO)
                .additionalExecutorsNotApplying(additionalExecutorNotApplying);

        List<CollectionMember<AdditionalExecutorApplying>> updatedApplyingList = additionalExecutorApplying;
        updatedApplyingList.remove(0);

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedNotApplyingList = additionalExecutorNotApplying;
        AdditionalExecutorNotApplying execNotApplying = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(SOLICITOR_SOT_FORENAME + " " + SOLICITOR_SOT_SURNAME)
                .notApplyingExecutorReason(SOLICITOR_NOT_APPLYING_REASON)
                .build();
        updatedNotApplyingList.add(new CollectionMember(SOLICITOR_ID, execNotApplying));

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        when(solicitorExecutorServiceMock.addSolicitorToNotApplyingList(caseDetailsMock.getData(), additionalExecutorNotApplying)).thenReturn(updatedNotApplyingList);
        when(solicitorExecutorServiceMock.removeSolicitorFromApplyingList(additionalExecutorApplying)).thenReturn(updatedApplyingList);

        solicitorExecutorTransformerMock.setExecutorApplyingListsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().isEmpty());
        assertEquals(additionalExecutorNotApplying, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying());
        verify(solicitorExecutorServiceMock, times(1)).removeSolicitorFromApplyingList(any());
        verify(solicitorExecutorServiceMock, times(1)).addSolicitorToNotApplyingList(any(), any());
    }

    @Test
    public void shouldUpdateExecutorListsWhenSolicitor_IsApplying() {
        caseDataBuilder
                .solsSolicitorIsApplying(YES)
                .additionalExecutorsApplying(additionalExecutorApplying);

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedNotApplying = additionalExecutorNotApplying;
        updatedNotApplying.remove(0);

        List<CollectionMember<AdditionalExecutorApplying>> updatedApplying = additionalExecutorApplying;
        updatedApplying.remove(0);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(solicitorExecutorServiceMock.removeSolicitorFromApplyingList(additionalExecutorApplying)).thenReturn(updatedApplying);
        when(solicitorExecutorServiceMock.removeSolicitorFromNotApplyingList(additionalExecutorNotApplying)).thenReturn(updatedNotApplying);

        solicitorExecutorTransformerMock.setExecutorApplyingListsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().isEmpty());
        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().isEmpty());
        verify(solicitorExecutorServiceMock, times(1)).removeSolicitorFromApplyingList(any());
        verify(solicitorExecutorServiceMock, times(1)).removeSolicitorFromNotApplyingList(any());
    }

    @Test
    public void shouldUpdateExecutorListsWhenSolicitorIs_NotExec() {
        caseDataBuilder
                .solsSolicitorIsExec(NO)
                .additionalExecutorsApplying(additionalExecutorApplying);

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedNotApplying = additionalExecutorNotApplying;
        updatedNotApplying.remove(0);

        List<CollectionMember<AdditionalExecutorApplying>> updatedApplying = additionalExecutorApplying;
        updatedApplying.remove(0);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(solicitorExecutorServiceMock.removeSolicitorFromApplyingList(additionalExecutorApplying)).thenReturn(updatedApplying);
        when(solicitorExecutorServiceMock.removeSolicitorFromNotApplyingList(additionalExecutorNotApplying)).thenReturn(updatedNotApplying);

        solicitorExecutorTransformerMock.setExecutorApplyingListsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().isEmpty());
        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().isEmpty());
        verify(solicitorExecutorServiceMock, times(1)).removeSolicitorFromApplyingList(any());
        verify(solicitorExecutorServiceMock, times(1)).removeSolicitorFromNotApplyingList(any());
    }

    @Test
    public void shouldInitialiseExecutorListsWithCaseData() {
        caseDataBuilder
                .additionalExecutorsApplying(additionalExecutorApplying)
                .additionalExecutorsNotApplying(additionalExecutorNotApplying);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.setExecutorApplyingListsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertEquals(additionalExecutorApplying, responseCaseDataBuilder.build().getAdditionalExecutorsApplying());
        assertEquals(additionalExecutorNotApplying, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying());
    }

    @Test
    public void shouldInitialiseExecutorListsWithEmptyList() {
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.setExecutorApplyingListsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().isEmpty());
        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().isEmpty());
    }


    @Test
    public void shouldSetExecName() {

        // Create exec without name set
        List<CollectionMember<AdditionalExecutorApplying>> additionExecApplyingNoName = new ArrayList<>();
        AdditionalExecutorApplying execApplying = AdditionalExecutorApplying.builder()
                .applyingExecutorFirstName(EXEC_FIRST_NAME)
                .applyingExecutorLastName(EXEC_SURNAME)
                .applyingExecutorPhoneNumber(EXEC_PHONE)
                .build();
        additionExecApplyingNoName.add(new CollectionMember<>(SOLICITOR_ID, execApplying));

        caseDataBuilder
                .additionalExecutorsApplying(additionExecApplyingNoName)
                .additionalExecutorsNotApplying(additionalExecutorNotApplying);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.setExecutorApplyingListsWithSolicitorInfo(caseDetailsMock.getData(), responseCaseDataBuilder);

        // Check that name has been set and other values are unchanged
        assertEquals(EXEC_NAME, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().get(0).getValue().getApplyingExecutorName());
        assertEquals(EXEC_PHONE, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().get(0).getValue().getApplyingExecutorPhoneNumber());
    }

    // Todo do we need this?
    @Test
    public void shouldTransformCaseForSolsAddExecListEmpty() {
        caseDataBuilder.applicationType(SOLICITOR);
        caseDataBuilder.recordId(null);
        caseDataBuilder.paperForm(NO);
        caseDataBuilder.additionalExecutorsApplying(emptyList());
        caseDataBuilder.additionalExecutorsNotApplying(emptyList());

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mapFromSolsAdditionalExecutorListToCaseworkerExecutorLists(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertEquals(EMPTY_LIST, responseCaseDataBuilder.build().getAdditionalExecutorsApplying());
        assertEquals(EMPTY_LIST, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying());
    }

    @Test
    public void shouldAddSolicitorToSolsAdditionalExecutorList() {

        // Add executor to solAdditionalExecutors list.
        List<CollectionMember<AdditionalExecutor>> solAdditionalExecutors = new ArrayList<>();
        solAdditionalExecutors.add(new CollectionMember<>(EXEC_ID, SOL_ADDITIONAL_EXECUTOR));

        // Solicitor is executor and applying
        caseDataBuilder
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .solsWillType(GOP)
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO)
                .solsAdditionalExecutorList(solAdditionalExecutors);


        List<CollectionMember<AdditionalExecutor>> updatedSolAdditionalExecutorList = new ArrayList<>(solAdditionalExecutors);
        updatedSolAdditionalExecutorList.add(new CollectionMember<>(SOLICITOR_ID, SOL_ADDITIONAL_EXECUTOR));

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(solicitorExecutorServiceMock.addSolicitorAsNotApplyingExecutorToList(caseDetailsMock.getData())).thenReturn(updatedSolAdditionalExecutorList);

        solicitorExecutorTransformerMock.addSolicitorToSolsAdditionalExecList(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertEquals(updatedSolAdditionalExecutorList, responseCaseDataBuilder.build().getSolsAdditionalExecutorList());
        verify(solicitorExecutorServiceMock, times(1)).addSolicitorAsNotApplyingExecutorToList(any());
    }

    @Test
    public void shouldNotAddSolicitorToSolsAdditionalExecutorList_SolicitorIsNotExec() {

        // Add executor to solAdditionalExecutors list.
        List<CollectionMember<AdditionalExecutor>> solAdditionalExecutors = new ArrayList<>();
        solAdditionalExecutors.add(new CollectionMember<>(EXEC_ID, SOL_ADDITIONAL_EXECUTOR));

        // Solicitor is not executor
        caseDataBuilder
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .solsWillType(GOP)
                .solsSolicitorIsExec(NO)
                .solsSolicitorIsApplying(NO)
                .solsAdditionalExecutorList(solAdditionalExecutors);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        solicitorExecutorTransformerMock.addSolicitorToSolsAdditionalExecList(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertEquals(solAdditionalExecutors, responseCaseDataBuilder.build().getSolsAdditionalExecutorList());
        verify(solicitorExecutorServiceMock, times(0)).addSolicitorAsNotApplyingExecutorToList(any());
    }

    @Test
    public void shouldNotAddSolicitorToSolsAdditionalExecutorList_SolicitorIsApplying() {

        // Add executor to solAdditionalExecutors list.
        List<CollectionMember<AdditionalExecutor>> solAdditionalExecutors = new ArrayList<>();
        solAdditionalExecutors.add(new CollectionMember<>(EXEC_ID, SOL_ADDITIONAL_EXECUTOR));

        // Solicitor is applying
        caseDataBuilder
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .solsWillType(GOP)
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .solsAdditionalExecutorList(solAdditionalExecutors);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        solicitorExecutorTransformerMock.addSolicitorToSolsAdditionalExecList(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertEquals(solAdditionalExecutors, responseCaseDataBuilder.build().getSolsAdditionalExecutorList());
        verify(solicitorExecutorServiceMock, times(0)).addSolicitorAsNotApplyingExecutorToList(any());
    }


    @Test
    public void shouldNotAddSolicitorToSolsAdditionalExecutorList_ListAlreadyContainsSolicitor() {

        // Add solicitor executor to solAdditionalExecutors list.
        List<CollectionMember<AdditionalExecutor>> solAdditionalExecutors = new ArrayList<>();
        solAdditionalExecutors.add(new CollectionMember<>(SOLICITOR_ID, SOL_ADDITIONAL_EXECUTOR));

        // Solicitor is executor and applying
        caseDataBuilder
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .solsWillType(GOP)
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO)
                .solsAdditionalExecutorList(solAdditionalExecutors);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        solicitorExecutorTransformerMock.addSolicitorToSolsAdditionalExecList(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertEquals(solAdditionalExecutors, responseCaseDataBuilder.build().getSolsAdditionalExecutorList());
        verify(solicitorExecutorServiceMock, times(0)).addSolicitorAsNotApplyingExecutorToList(any());
    }

    private static final AdditionalExecutor SOL_ADDITIONAL_EXECUTOR = AdditionalExecutor.builder()
            .additionalExecForenames(SOLICITOR_SOT_FORENAME)
            .additionalExecLastname(SOLICITOR_SOT_SURNAME)
            .additionalExecNameOnWill(NO)
            .additionalApplying(YES)
            .additionalExecAddress(SOLICITOR_ADDRESS)
            .build();
}
