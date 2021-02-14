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
                .applyingExecutorFirstName(EXEC_FIRST_NAME)
                .applyingExecutorLastName(EXEC_SURNAME)
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
    public void shouldUpdateNotApplyingListWhenSolicitorIs_Exec_NotApplying() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO);

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedNotApplyingList = additionalExecutorNotApplying;
        AdditionalExecutorNotApplying execNotApplying = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(SOLICITOR_SOT_FORENAME + " " + SOLICITOR_SOT_SURNAME)
                .notApplyingExecutorReason(SOLICITOR_NOT_APPLYING_REASON)
                .build();
        updatedNotApplyingList.add(new CollectionMember(SOLICITOR_ID, execNotApplying));

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        when(solicitorExecutorServiceMock.addSolicitorToNotApplyingList(caseDetailsMock.getData(), additionalExecutorNotApplying)).thenReturn(updatedNotApplyingList);

        List<CollectionMember<AdditionalExecutorNotApplying>> result = solicitorExecutorTransformerMock.setExecutorNotApplyingListWithSolicitorInfo(additionalExecutorNotApplying, caseDetailsMock.getData());

        assertEquals(updatedNotApplyingList, result);
        verify(solicitorExecutorServiceMock, times(1)).addSolicitorToNotApplyingList(any(), any());
    }

    @Test
    public void shouldUpdateNotApplyingListWhenSolicitor_IsApplying() {
        caseDataBuilder
                .solsSolicitorIsApplying(YES);

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedNotApplyingList = additionalExecutorNotApplying;
        updatedNotApplyingList.remove(0);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(solicitorExecutorServiceMock.removeSolicitorFromNotApplyingList(additionalExecutorNotApplying)).thenReturn(updatedNotApplyingList);

        List<CollectionMember<AdditionalExecutorNotApplying>> result = solicitorExecutorTransformerMock.setExecutorNotApplyingListWithSolicitorInfo(additionalExecutorNotApplying, caseDetailsMock.getData());

        assertEquals(updatedNotApplyingList, result);
        assertTrue(result.isEmpty());
        verify(solicitorExecutorServiceMock, times(1)).removeSolicitorFromNotApplyingList(any());
    }

    @Test
    public void shouldUpdateNotApplyingListWhenSolicitorIs_NotExec() {
        caseDataBuilder
                .solsSolicitorIsExec(NO);

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedNotApplyingList = additionalExecutorNotApplying;
        updatedNotApplyingList.remove(0);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(solicitorExecutorServiceMock.removeSolicitorFromNotApplyingList(additionalExecutorNotApplying)).thenReturn(updatedNotApplyingList);

        List<CollectionMember<AdditionalExecutorNotApplying>> result = solicitorExecutorTransformerMock.setExecutorNotApplyingListWithSolicitorInfo(additionalExecutorNotApplying, caseDetailsMock.getData());

        assertEquals(updatedNotApplyingList, result);
        assertTrue(result.isEmpty());
        verify(solicitorExecutorServiceMock, times(1)).removeSolicitorFromNotApplyingList(any());
    }

    @Test
    public void shouldSetPrimaryApplicantFieldsWithExecutorInfo_NullPrimaryForename_ApplyExecutorList() {
        caseDataBuilder
                .primaryApplicantForenames(null);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        List<CollectionMember<AdditionalExecutorApplying>> result = solicitorExecutorTransformerMock.setPrimaryApplicantWithExecutorInfo(additionalExecutorApplying, caseDetailsMock.getData(), responseCaseDataBuilder);

        assertTrue(result.isEmpty());
        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(EXEC_FIRST_NAME, responseCaseData.getPrimaryApplicantForenames());
        assertEquals(EXEC_SURNAME, responseCaseData.getPrimaryApplicantSurname());
        assertEquals(EXEC_ADDRESS, responseCaseData.getPrimaryApplicantAddress());
        assertNull(responseCaseDataBuilder.build().getPrimaryApplicantAlias());
        assertEquals(NO, responseCaseData.getPrimaryApplicantHasAlias());
        assertEquals(YES, responseCaseData.getPrimaryApplicantIsApplying());
        assertNull(responseCaseData.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldNotSetPrimaryApplicantFieldsWithExecutorInfo_NullPrimaryForename_EmptyApplyExecutorList() {
        caseDataBuilder
                .primaryApplicantForenames(null);
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = new ArrayList<>();

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        List<CollectionMember<AdditionalExecutorApplying>> result = solicitorExecutorTransformerMock.setPrimaryApplicantWithExecutorInfo(execsApplying, caseDetailsMock.getData(), responseCaseDataBuilder);

        assertTrue(result.isEmpty());
        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertNull(responseCaseData.getPrimaryApplicantForenames());
        assertNull(responseCaseData.getPrimaryApplicantSurname());
        assertNull(responseCaseData.getPrimaryApplicantAddress());
        assertNull(responseCaseDataBuilder.build().getPrimaryApplicantAlias());
        assertNull(responseCaseData.getPrimaryApplicantHasAlias());
        assertNull(responseCaseData.getPrimaryApplicantIsApplying());
        assertNull(responseCaseData.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldNotSetPrimaryApplicantFieldsWithExecutorInfo_PrimaryForename_ApplyExecutorList() {
        caseDataBuilder
                .primaryApplicantForenames(EXEC_FIRST_NAME);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        List<CollectionMember<AdditionalExecutorApplying>> result = solicitorExecutorTransformerMock.setPrimaryApplicantWithExecutorInfo(additionalExecutorApplying, caseDetailsMock.getData(), responseCaseDataBuilder);

        assertEquals(result, additionalExecutorApplying);
        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertNull(responseCaseData.getPrimaryApplicantForenames());
        assertNull(responseCaseData.getPrimaryApplicantSurname());
        assertNull(responseCaseData.getPrimaryApplicantAddress());
        assertNull(responseCaseDataBuilder.build().getPrimaryApplicantAlias());
        assertNull(responseCaseData.getPrimaryApplicantHasAlias());
        assertNull(responseCaseData.getPrimaryApplicantIsApplying());
        assertNull(responseCaseData.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldReturnEmptyCaseworkerLists_NullExecutorsLists() {
        caseDataBuilder
                .additionalExecutorsTrustCorpList(null)
                .otherPartnersApplyingAsExecutors(null)
                .powerReservedExecutorList(null)
                .solsAdditionalExecutorList(null);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        solicitorExecutorTransformerMock.mapSolicitorExecutorListsToCaseworkerExecutorsLists(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertTrue(responseCaseData.getAdditionalExecutorsApplying().isEmpty());
        assertTrue(responseCaseData.getAdditionalExecutorsNotApplying().isEmpty());
    }

}
