package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.SolicitorExecutorService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
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

    @InjectMocks
    private SolicitorExecutorTransformer solicitorExecutorTransformerMock;

    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorApplying;
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorNotApplying;

    @Before
    public void setUp() {
        additionalExecutorApplying = new ArrayList<>();
        additionalExecutorNotApplying = new ArrayList<>();

        AdditionalExecutorApplying execApplying = AdditionalExecutorApplying.builder()
                .applyingExecutorName(SOLICITOR_SOT_FORENAME + " " + SOLICITOR_SOT_SURNAME)
                .applyingExecutorPhoneNumber(SOLICITOR_FIRM_PHONE)
                .applyingExecutorEmail(SOLICITOR_FIRM_EMAIL)
                .applyingExecutorAddress(SOLICITOR_ADDRESS)
                .build();
        additionalExecutorApplying.add(new CollectionMember<>(SOL_AS_EXEC_ID, execApplying));

        AdditionalExecutorNotApplying execNotApplying = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(SOLICITOR_SOT_FORENAME + " " + SOLICITOR_SOT_SURNAME)
                .notApplyingExecutorReason(SOLICITOR_NOT_APPLYING_REASON)
                .build();
        additionalExecutorNotApplying.add(new CollectionMember<>(SOL_AS_EXEC_ID, execNotApplying));
    }

    @Test
    public void shouldSetMainApplicantDetailsWithSolicitorInfo(){

        caseDataBuilder.solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorPhoneNumber(SOLICITOR_FIRM_PHONE)
                .solsSolicitorEmail(SOLICITOR_FIRM_EMAIL)
                .solsSolicitorAddress(SOLICITOR_ADDRESS);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(SOLICITOR_SOT_FORENAME, responseCaseData.getPrimaryApplicantForenames());
        assertEquals(SOLICITOR_SOT_SURNAME, responseCaseData.getPrimaryApplicantSurname());
        assertEquals(SOLICITOR_FIRM_PHONE, responseCaseData.getPrimaryApplicantPhoneNumber());
        assertEquals(SOLICITOR_FIRM_EMAIL, responseCaseData.getPrimaryApplicantEmailAddress());
        assertEquals(SOLICITOR_ADDRESS, responseCaseData.getPrimaryApplicantAddress());
        assertNull(responseCaseDataBuilder.build().getPrimaryApplicantAlias());
        assertEquals(NO, responseCaseData.getPrimaryApplicantHasAlias());
        assertEquals(YES, responseCaseData.getPrimaryApplicantIsApplying());
        assertEquals(YES, responseCaseData.getSolsSolicitorIsApplying());
        assertNull(responseCaseData.getSolsSolicitorNotApplyingReason());
        assertNull(responseCaseData.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldRemoveSolicitorAsMainApplicant(){
        caseDataBuilder
                .solsSolicitorIsExec(NO)
                .solsSolicitorIsMainApplicant(NO)
                .solsSolicitorIsApplying(NO)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertNull(responseCaseData.getSolsSolicitorIsMainApplicant());
        assertNull(responseCaseData.getSolsSolicitorIsApplying());
        assertNull(responseCaseData.getSolsSolicitorNotApplyingReason());
    }


    @Test
    public void shouldRemoveSolicitorDetailsFromPrimaryApplicant(){
        // Solicitor names are same as primary names.
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
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

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

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
    public void shouldRemoveSolicitorNotApplyingReasonWhenSolicitorApplying(){
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
                .solsSolicitorIsApplying(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .primaryApplicantForenames(PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);


        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertNull(responseCaseDataBuilder.build().getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldRemoveSolicitorNotApplyingReasonWhenSolicitorApplyingIsNull(){
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
                .solsSolicitorIsApplying(null)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .primaryApplicantForenames(PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);


        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertNull(responseCaseDataBuilder.build().getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldNotChangeResponseCaseData() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .primaryApplicantForenames(PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .solsSolicitorIsApplying(NO)
                .solsSolicitorNotApplyingReason("Not applying");

        responseCaseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorIsApplying(NO)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertEquals(YES, responseCaseData.getSolsSolicitorIsExec());
        assertEquals(NO, responseCaseData.getSolsSolicitorIsMainApplicant());
        assertEquals(SOLICITOR_SOT_FORENAME, responseCaseData.getSolsSOTForenames());
        assertEquals(SOLICITOR_SOT_SURNAME, responseCaseData.getSolsSOTSurname());
        assertEquals(NO, responseCaseData.getSolsSolicitorIsApplying());
        assertEquals(SOLICITOR_NOT_APPLYING_REASON, responseCaseData.getSolsSolicitorNotApplyingReason());
    }

    @Test
    public void shouldUpdateExecutorListsSolsIsExecNotMainApplicantIsApplyingTransform(){
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
                .solsSolicitorIsApplying(YES);

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedRemoveList = additionalExecutorNotApplying;
        updatedRemoveList.remove(0);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(solicitorExecutorServiceMock.updateSolicitorApplyingExecutor(caseDetailsMock.getData(), additionalExecutorApplying)).thenReturn(additionalExecutorApplying);
        when(solicitorExecutorServiceMock.removeSolicitorAsNotApplyingExecutor(additionalExecutorNotApplying)).thenReturn(updatedRemoveList);

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertEquals(additionalExecutorApplying, responseCaseDataBuilder.build().getAdditionalExecutorsApplying());
        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().isEmpty());
    }

    @Test
    public void shouldUpdateExecutorListsSolsIsExecNotMainApplicantIsNotApplyingTransform(){
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
                .solsSolicitorIsApplying(NO)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        List<CollectionMember<AdditionalExecutorApplying>> updatedApplyingList = additionalExecutorApplying;
        updatedApplyingList.remove(0);

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedNotApplyingList = additionalExecutorNotApplying;
        AdditionalExecutorNotApplying execNotApplying = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(SOLICITOR_SOT_FORENAME + " " + SOLICITOR_SOT_SURNAME)
                .notApplyingExecutorReason(SOLICITOR_NOT_APPLYING_REASON)
                .build();
        updatedNotApplyingList.add(new CollectionMember(SOL_AS_EXEC_ID, execNotApplying));

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        when(solicitorExecutorServiceMock.updateSolicitorNotApplyingExecutor(caseDetailsMock.getData(), additionalExecutorNotApplying)).thenReturn(updatedNotApplyingList);
        when(solicitorExecutorServiceMock.removeSolicitorAsApplyingExecutor(additionalExecutorApplying)).thenReturn(updatedApplyingList);

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().isEmpty());
        assertEquals(additionalExecutorNotApplying, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying());
    }

    @Test
    public void shouldUpdateExecutorListsSolsIsNotExecTransform(){
        caseDataBuilder
                .solsSolicitorIsExec(NO)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().isEmpty());
        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().isEmpty());
    }

    @Test
    public void shouldUpdateExecutorListsSolsIsNotExecIsMainApplicantTransform(){
        caseDataBuilder
                .solsSolicitorIsExec(NO)
                .solsSolicitorIsMainApplicant(YES)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().isEmpty());
        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().isEmpty());
    }

    @Test
    public void shouldTransformCaseForSolicitorWithSolsExecsDontExist() {
        caseDataBuilder
                .applicationType(ApplicationType.SOLICITOR)
                .solsAdditionalExecutorList(EMPTY_LIST);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertEquals(0, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().size());
        assertEquals(0, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().size());
    }

    @Test
    public void shouldTransformCaseForSolicitorWithCaseTypeIsGOP() {
        caseDataBuilder
                .applicationType(ApplicationType.SOLICITOR)
                .solsAdditionalExecutorList(EMPTY_LIST)
                .caseType("gop");

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertEquals(0, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().size());
        assertEquals(0, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().size());
    }

    @Test
    public void shouldTransformCaseForSolicitorWithCaseTypeIsNull() {
        caseDataBuilder
                .applicationType(ApplicationType.SOLICITOR)
                .solsAdditionalExecutorList(EMPTY_LIST)
                .caseType(null);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertEquals(0, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().size());
        assertEquals(0, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().size());
    }

    @Test
    public void shouldTransformForSolIsAdditionalExecApplyingUpdate() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
                .solsSolicitorIsApplying(YES)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .solsPrimaryExecutorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .additionalExecutorsNotApplying(additionalExecutorNotApplying);

        when(caseDetailsMock.getData())
                .thenReturn(caseDataBuilder.build());
        when(solicitorExecutorServiceMock.updateSolicitorApplyingExecutor(any(CaseData.class), anyList()))
                .thenReturn(additionalExecutorApplying);
        when(solicitorExecutorServiceMock.removeSolicitorAsNotApplyingExecutor(anyList()))
                .thenReturn(new ArrayList<>());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertEquals(SOLICITOR_SOT_FULLNAME, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().get(0).getValue().getApplyingExecutorName());
        assertEquals(SOL_AS_EXEC_ID, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().get(0).getId());
    }

    @Test
    public void shouldTransformCaseForSolIsAdditionalExecNotApplyingUpdate() {
        caseDataBuilder
                .applicationType(SOLICITOR)
                .recordId(null)
                .paperForm(NO)
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
                .solsSolicitorIsApplying(NO)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .additionalExecutorsApplying(additionalExecutorApplying);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        when(solicitorExecutorServiceMock.updateSolicitorNotApplyingExecutor(any(CaseData.class), anyList()))
                .thenReturn(additionalExecutorNotApplying);
        when(solicitorExecutorServiceMock.removeSolicitorAsApplyingExecutor(anyList())).thenReturn(new ArrayList<>());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().isEmpty());
        assertEquals(SOLICITOR_SOT_FULLNAME, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().get(0).getValue().getNotApplyingExecutorName());
        assertEquals(SOL_AS_EXEC_ID, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().get(0).getId());
        assertEquals(SOLICITOR_NOT_APPLYING_REASON, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().get(0).getValue().getNotApplyingExecutorReason());
    }

    @Test
    public void shouldTransformCaseForSolIsAdditionalExecApplyingUpdate() {
        caseDataBuilder
                .applicationType(SOLICITOR)
                .recordId(null)
                .paperForm(NO)
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
                .solsSolicitorIsApplying(YES)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .solsPrimaryExecutorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .additionalExecutorsNotApplying(additionalExecutorNotApplying);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        when(solicitorExecutorServiceMock.updateSolicitorApplyingExecutor(any(CaseData.class), anyList()))
                .thenReturn(additionalExecutorApplying);
        when(solicitorExecutorServiceMock.removeSolicitorAsNotApplyingExecutor(anyList())).thenReturn(new ArrayList<>());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().isEmpty());
        assertEquals(SOLICITOR_SOT_FULLNAME, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().get(0).getValue().getApplyingExecutorName());
        assertEquals(SOL_AS_EXEC_ID, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().get(0).getId());
    }

    @Test
    public void shouldTransformPersonalCaseForEmptySolsAdditionalExecs() {
        caseDataBuilder
                .applicationType(PERSONAL)
                .solsAdditionalExecutorList(EMPTY_LIST);


        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertEquals(0, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().size());
        assertEquals(0, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().size());
        assertTrue(responseCaseDataBuilder.build().getSolsAdditionalExecutorList().isEmpty());
    }

    @Test
    public void shouldTransformAdditionalExecApplyingName() {
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecsAppList = new ArrayList<>();
        additionalExecsAppList.add(createAdditionalExecutorApplying("0"));
        caseDataBuilder.additionalExecutorsApplying(additionalExecsAppList);


        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);
        assertEquals(EXEC_FIRST_NAME + " " + EXEC_SURNAME,
                responseCaseDataBuilder.build().getAdditionalExecutorsApplying().get(0).getValue().getApplyingExecutorName());
    }

    @Test
    public void shouldNotTransformAdditionalExecApplyingName() {
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecsAppList = new ArrayList<>();
        additionalExecsAppList.add(createAdditionalExecutorApplyingfNamelName("0"));
        caseDataBuilder.additionalExecutorsApplying(additionalExecsAppList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertEquals(EXEC_FIRST_NAME + " " + EXEC_SURNAME,
                responseCaseDataBuilder.build().getAdditionalExecutorsApplying().get(0).getValue().getApplyingExecutorName());

    }

    @Test
    public void shouldTransformPersonalCaseForAdditionalExecsExist() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        List<CollectionMember<AdditionalExecutorApplying>> additionalExecsAppList = new ArrayList<>();
        additionalExecsAppList.add(createAdditionalExecutorApplying("0"));
        caseDataBuilder.additionalExecutorsApplying(additionalExecsAppList);
        List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecsNotAppList = new ArrayList<>();
        additionalExecsNotAppList.add(createAdditionalExecutorNotApplying("0"));
        caseDataBuilder.additionalExecutorsNotApplying(additionalExecsNotAppList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertEquals(1, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().size());
        assertApplyingExecutorDetails(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().get(0).getValue());
        assertEquals(1, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().size());
        assertNotApplyingExecutorDetails(responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().get(0).getValue());
         assertEquals(0, responseCaseDataBuilder.build().getSolsAdditionalExecutorList().size());
        assertEquals(YES, responseCaseDataBuilder.build().getOtherExecutorExists());
    }

//    @Test
//    public void shouldTransformCaseForSolicitorWithSolsExecsExists() {
//        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
//        caseDataBuilder.recordId(null);
//        caseDataBuilder.paperForm("No");
//
//        List<CollectionMember<AdditionalExecutor>> additionalExecsList = new ArrayList<>();
//        additionalExecsList.add(createSolsAdditionalExecutor("0", NO, STOP_REASON));
//        additionalExecsList.add(createSolsAdditionalExecutor("1", YES, ""));
//        additionalExecsList.add(createSolsAdditionalExecutor("2", YES, ""));
//        caseDataBuilder.solsAdditionalExecutorList(additionalExecsList);
//
//        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
//        when(solicitorExecutorServiceMock.updateSolicitorApplyingExecutor(caseDetailsMock.getData(), additionalExecutorApplying)).thenReturn(additionalExecutorApplying);
//
//        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);
//
//        assertEquals(2, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().size());
//        assertApplyingExecutorDetailsFromSols(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().get(0).getValue());
//        assertEquals(1, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().size());
//    }

    @Test
    public void shouldTransformForSolExecMainApplicantInSolicitorJourney() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(YES)
                .solsSolicitorIsApplying(YES);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().isEmpty());
        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().isEmpty());
        assertNotEquals(YES, responseCaseDataBuilder.build().getOtherExecutorExists());
        assertNull(responseCaseDataBuilder.build().getSolsSolicitorNotApplyingReason());
        assertNull(responseCaseDataBuilder.build().getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldTransformCaseForSolicitorWithPaperFormIsNull() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsAdditionalExecutorList(EMPTY_LIST);
        caseDataBuilder.paperForm(null);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertEquals(0, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().size());
        assertEquals(0, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().size());
    }

    @Test
    public void shouldTransformCaseForSolIsChangedToNotNamedOnWill() {
        caseDataBuilder
                .applicationType(SOLICITOR)
                .recordId(null)
                .paperForm(NO)
                .solsSolicitorIsExec(NO)
                .solsSolicitorIsMainApplicant(null)
                .solsSolicitorIsApplying(null)
                .solsSolicitorNotApplyingReason(null)
                .additionalExecutorsApplying(additionalExecutorApplying)
                .additionalExecutorsNotApplying(additionalExecutorNotApplying);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().isEmpty());
        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().isEmpty());
        assertNull(responseCaseDataBuilder.build().getSolsSolicitorIsMainApplicant());
        assertNull(responseCaseDataBuilder.build().getSolsSolicitorIsApplying());
        assertNull(responseCaseDataBuilder.build().getSolsSolicitorNotApplyingReason());
    }

    @Test
    public void shouldTransformForSolIsAdditionalExecNotApplyingUpdate() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
                .solsSolicitorIsApplying(NO)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .additionalExecutorsApplying(additionalExecutorApplying);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(solicitorExecutorServiceMock.updateSolicitorNotApplyingExecutor(any(CaseData.class), anyList()))
                .thenReturn(additionalExecutorNotApplying);
        when(solicitorExecutorServiceMock.removeSolicitorAsApplyingExecutor(anyList())).thenReturn(new ArrayList<>());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertTrue(responseCaseDataBuilder.build().getAdditionalExecutorsApplying().isEmpty());
        assertEquals(SOLICITOR_SOT_FULLNAME, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().get(0).getValue().getNotApplyingExecutorName());
        assertEquals(SOL_AS_EXEC_ID, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().get(0).getId());
        assertEquals(SOLICITOR_NOT_APPLYING_REASON, responseCaseDataBuilder.build().getAdditionalExecutorsNotApplying().get(0).getValue().getNotApplyingExecutorReason());
    }

    @Test
    public void shouldTransformCaseForPAWithApplyExecAlias() {
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecsList = new ArrayList<>();
        additionalExecsList.add(createAdditionalExecutorApplying("0"));
        additionalExecsList.add(createAdditionalExecutorApplying("1"));
        caseDataBuilder.additionalExecutorsApplying(additionalExecsList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.solicitorExecutorTransformation(caseDetailsMock.getData(), solicitorExecutorServiceMock, responseCaseDataBuilder);

        assertEquals(2, responseCaseDataBuilder.build().getAdditionalExecutorsApplying().size());
    }






    private CollectionMember<AdditionalExecutorApplying> createAdditionalExecutorApplying(String id) {
        AdditionalExecutorApplying add1na = AdditionalExecutorApplying.builder()
                .applyingExecutorAddress(EXEC_ADDRESS)
                .applyingExecutorEmail(EXEC_EMAIL)
                .applyingExecutorName(EXEC_FIRST_NAME + " " + EXEC_SURNAME)
                .applyingExecutorOtherNames(ALIAS_FORENAME + " " + ALIAS_SURNAME)
                .applyingExecutorPhoneNumber(EXEC_PHONE)
                .applyingExecutorOtherNamesReason("Other")
                .applyingExecutorOtherReason("Married")
                .build();
        return new CollectionMember<>(id, add1na);
    }

    private CollectionMember<AdditionalExecutor> createSolsAdditionalExecutor(String id, String applying, String reason) {
        AdditionalExecutor add1na = AdditionalExecutor.builder()
                .additionalApplying(applying)
                .additionalExecAddress(EXEC_ADDRESS)
                .additionalExecForenames(EXEC_FIRST_NAME)
                .additionalExecLastname(EXEC_SURNAME)
                .additionalExecReasonNotApplying(reason)
                .additionalExecAliasNameOnWill(ALIAS_FORENAME + " " + ALIAS_SURNAME)
                .build();
        return new CollectionMember<>(id, add1na);
    }

    private CollectionMember<AdditionalExecutorNotApplying> createAdditionalExecutorNotApplying(String id) {
        AdditionalExecutorNotApplying add1na = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(EXEC_NAME)
                .notApplyingExecutorNameDifferenceComment(EXEC_NAME_DIFF)
                .notApplyingExecutorNameOnWill(EXEC_WILL_NAME)
                .notApplyingExecutorNotified(YES)
                .notApplyingExecutorReason(STOP_REASON)
                .build();
        return new CollectionMember<>(id, add1na);
    }

    private CollectionMember<AdditionalExecutorApplying> createAdditionalExecutorApplyingfNamelName(String id) {
        AdditionalExecutorApplying add1na = AdditionalExecutorApplying.builder()
                .applyingExecutorAddress(EXEC_ADDRESS)
                .applyingExecutorEmail(EXEC_EMAIL)
                .applyingExecutorFirstName(EXEC_FIRST_NAME)
                .applyingExecutorLastName(EXEC_SURNAME)
                .applyingExecutorOtherNames(ALIAS_FORENAME + " " + ALIAS_SURNAME)
                .applyingExecutorPhoneNumber(EXEC_PHONE)
                .applyingExecutorOtherNamesReason("Other")
                .applyingExecutorOtherReason("Married")
                .build();
        return new CollectionMember<>(id, add1na);
    }

    private void assertApplyingExecutorDetails(AdditionalExecutorApplying exec) {
        assertEquals(EXEC_FIRST_NAME + " " + EXEC_SURNAME, exec.getApplyingExecutorName());
        assertEquals(ALIAS_FORENAME + " " + ALIAS_SURNAME, exec.getApplyingExecutorOtherNames());
        assertEquals("Other", exec.getApplyingExecutorOtherNamesReason());
        assertEquals("Married", exec.getApplyingExecutorOtherReason());
        assertApplyingExecutorDetailsFromSols(exec);
    }

    private void assertApplyingExecutorDetailsFromSols(AdditionalExecutorApplying exec) {
        assertEquals(EXEC_ADDRESS, exec.getApplyingExecutorAddress());
        assertEquals(EXEC_FIRST_NAME + " " + EXEC_SURNAME, exec.getApplyingExecutorName());
        assertEquals(ALIAS_FORENAME + " " + ALIAS_SURNAME, exec.getApplyingExecutorOtherNames());
    }

    private void assertNotApplyingExecutorDetails(AdditionalExecutorNotApplying exec) {
        assertEquals(EXEC_NAME, exec.getNotApplyingExecutorName());
        assertEquals(EXEC_OTHER_NAMES, exec.getNotApplyingExecutorNameOnWill());
        assertEquals(EXEC_NAME_DIFF, exec.getNotApplyingExecutorNameDifferenceComment());
        assertEquals(STOP_REASON, exec.getNotApplyingExecutorReason());
        assertEquals(EXEC_NOTIFIED, exec.getNotApplyingExecutorNotified());
    }

}
