package uk.gov.hmcts.probate.transformer.solicitorexecutors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.EXECUTOR_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.EXECUTOR_NOT_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_ADDRESS;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_FIRST_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.NO;
import static uk.gov.hmcts.probate.util.CommonVariables.PARTNER_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.PRIMARY_APPLICANT_FORENAME;
import static uk.gov.hmcts.probate.util.CommonVariables.PRIMARY_APPLICANT_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_ADDRESS;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_EXECUTOR_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_FIRM_EMAIL;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_FIRM_PHONE;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_ID;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_NOT_APPLYING_REASON;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_SOT_FORENAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_SOT_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLS_EXEC_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLS_EXEC_NOT_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.TRUST_CORP_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.YES;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorsTransformerTest {

    private final CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();

    private final ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder = ResponseCaseData.builder();

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private ExecutorListMapperService executorListMapperServiceMock;

    @InjectMocks
    private ExecutorsTransformer solicitorExecutorTransformerMock;
    
    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorApplying;
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorNotApplying;
    private List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList;
    private List<CollectionMember<AdditionalExecutorTrustCorps>> trustCorpsExecutorList;
    private List<CollectionMember<AdditionalExecutorPartners>> partnerExecutorList;
    private List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeExecList;

    @Before
    public void setUp() {
        additionalExecutorApplying = new ArrayList<>();
        additionalExecutorApplying.add(new CollectionMember<>(SOLICITOR_ID, EXECUTOR_APPLYING));

        additionalExecutorNotApplying = new ArrayList<>();
        additionalExecutorNotApplying.add(new CollectionMember<>(SOLICITOR_ID, EXECUTOR_NOT_APPLYING));

        solsAdditionalExecutorList = new ArrayList<>();
        solsAdditionalExecutorList.add(SOLS_EXEC_APPLYING);
        solsAdditionalExecutorList.add(SOLS_EXEC_NOT_APPLYING);

        trustCorpsExecutorList = new ArrayList<>();
        trustCorpsExecutorList.add(TRUST_CORP_EXEC);

        partnerExecutorList = new ArrayList<>();
        partnerExecutorList.add(PARTNER_EXEC);

        dispenseWithNoticeExecList = new ArrayList<>();
        dispenseWithNoticeExecList.add(DISPENSE_WITH_NOTICE_EXEC);
    }

    @Test
    public void shouldSetPrimaryApplicantDetailsWithSolicitorInfo() {
        List<CollectionMember<AdditionalExecutorApplying>> solAdditionalExecutorApplying;
        solAdditionalExecutorApplying = new ArrayList<>();
        solAdditionalExecutorApplying.add(new CollectionMember<>(SOLICITOR_ID, SOLICITOR_EXECUTOR_APPLYING));

        caseDataBuilder.solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorPhoneNumber(SOLICITOR_FIRM_PHONE)
                .solsSolicitorEmail(SOLICITOR_FIRM_EMAIL)
                .solsSolicitorAddress(SOLICITOR_ADDRESS);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.addSolicitorToApplyingList(
                caseDetailsMock.getData(), new ArrayList<>())).thenReturn(solAdditionalExecutorApplying);

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData(),
                responseCaseDataBuilder);

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

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData(),
                responseCaseDataBuilder);

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

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData(),
                responseCaseDataBuilder);

        assertNull(responseCaseDataBuilder.build().getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldNotSetSolicitorAsPrimaryApplicantIfSolIsMainAppFieldSetToNo() {
        responseCaseDataBuilder
                .primaryApplicantForenames(PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES);

        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(NO)
                .solsSolicitorIsApplying(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.setPrimaryApplicantFieldsWithSolicitorInfo(caseDetailsMock.getData(),
                responseCaseDataBuilder);

        ResponseCaseData response = responseCaseDataBuilder.build();
        assertNull(response.getSolsPrimaryExecutorNotApplyingReason());
        assertEquals(PRIMARY_APPLICANT_FORENAME, response.getPrimaryApplicantForenames());
        assertEquals(PRIMARY_APPLICANT_SURNAME, response.getPrimaryApplicantSurname());
    }

    @Test
    public void shouldSetSolicitorAsPrimaryApplicantIfSolIsMainAppFieldIsNull() {
        responseCaseDataBuilder
                .primaryApplicantForenames(PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES);

        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(null)
                .solsSolicitorIsApplying(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.setPrimaryApplicantFieldsWithSolicitorInfo(caseDetailsMock.getData(),
                responseCaseDataBuilder);

        ResponseCaseData response = responseCaseDataBuilder.build();
        assertEquals(SOLICITOR_SOT_FORENAME, response.getPrimaryApplicantForenames());
        assertEquals(SOLICITOR_SOT_SURNAME, response.getPrimaryApplicantSurname());
    }

    @Test
    public void shouldSetSolicitorAsPrimaryApplicantIfSolIsMainAppFieldIsYes() {
        responseCaseDataBuilder
                .primaryApplicantForenames(PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES);

        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(YES)
                .solsSolicitorIsApplying(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.setPrimaryApplicantFieldsWithSolicitorInfo(caseDetailsMock.getData(),
                responseCaseDataBuilder);

        ResponseCaseData response = responseCaseDataBuilder.build();
        assertEquals(SOLICITOR_SOT_FORENAME, response.getPrimaryApplicantForenames());
        assertEquals(SOLICITOR_SOT_SURNAME, response.getPrimaryApplicantSurname());
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
        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData(),
                responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertEquals(YES, responseCaseData.getSolsSolicitorIsExec());
        assertEquals(SOLICITOR_SOT_FORENAME, responseCaseData.getSolsSOTForenames());
        assertEquals(SOLICITOR_SOT_SURNAME, responseCaseData.getSolsSOTSurname());
        assertEquals(NO, responseCaseData.getSolsSolicitorIsApplying());
        assertEquals(SOLICITOR_NOT_APPLYING_REASON, responseCaseData.getSolsSolicitorNotApplyingReason());
    }

    @Test
    public void shouldDefaultCaseworkerExecutorListsToExistingValues() {
        caseDataBuilder
                .additionalExecutorsApplying(additionalExecutorApplying)
                .additionalExecutorsNotApplying(additionalExecutorNotApplying)
                .primaryApplicantForenames(EXEC_FIRST_NAME);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.removeSolicitorFromApplyingList(
                additionalExecutorApplying)).thenReturn(additionalExecutorApplying);

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(additionalExecutorApplying,responseCaseData.getAdditionalExecutorsApplying());
        assertEquals(additionalExecutorNotApplying,responseCaseData.getAdditionalExecutorsNotApplying());

    }

    @Test
    public void shouldSetCaseworkerExecutorListsAsEmpty() {
        caseDataBuilder
                .additionalExecutorsTrustCorpList(null)
                .otherPartnersApplyingAsExecutors(null)
                .dispenseWithNoticeOtherExecsList(null)
                .solsAdditionalExecutorList(null);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertTrue(responseCaseData.getAdditionalExecutorsApplying().isEmpty());
        assertTrue(responseCaseData.getAdditionalExecutorsNotApplying().isEmpty());
    }

    @Test
    public void shouldSetCaseworkerNotApplyingExecutorLists() {
        caseDataBuilder
                .additionalExecutorsTrustCorpList(null)
                .otherPartnersApplyingAsExecutors(null)
                .dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecList)
                .solsAdditionalExecutorList(solsAdditionalExecutorList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.mapFromDispenseWithNoticeExecsToNotApplyingExecutors(
                        caseDetailsMock.getData())).thenReturn(additionalExecutorNotApplying);
        when(executorListMapperServiceMock.mapFromSolsAdditionalExecsToNotApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorNotApplying);

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        List<CollectionMember<AdditionalExecutorNotApplying>> expected = additionalExecutorNotApplying;
        expected.addAll(additionalExecutorNotApplying);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertTrue(responseCaseData.getAdditionalExecutorsApplying().isEmpty());
        assertEquals(expected, responseCaseData.getAdditionalExecutorsNotApplying());
        verify(executorListMapperServiceMock, times(1))
                .mapFromDispenseWithNoticeExecsToNotApplyingExecutors(any());
        verify(executorListMapperServiceMock, times(1))
                .mapFromSolsAdditionalExecsToNotApplyingExecutors(any());
    }

    @Test
    public void shouldSetCaseworkerApplyingWithTrustCorpExecutorLists() {
        caseDataBuilder
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .otherPartnersApplyingAsExecutors(null)
                .dispenseWithNoticeOtherExecsList(null)
                .solsAdditionalExecutorList(null)
                .primaryApplicantForenames("forename");

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.mapFromTrustCorpExecutorsToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.removeSolicitorFromApplyingList(
                additionalExecutorApplying)).thenReturn(additionalExecutorApplying);

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertTrue(responseCaseData.getAdditionalExecutorsNotApplying().isEmpty());
        assertEquals(additionalExecutorApplying, responseCaseData.getAdditionalExecutorsApplying());
        verify(executorListMapperServiceMock, times(1))
                .mapFromTrustCorpExecutorsToApplyingExecutors(any());
    }

    @Test
    public void shouldSetCaseworkerApplyingWithPartnerExecutorLists() {
        caseDataBuilder
                .additionalExecutorsTrustCorpList(null)
                .otherPartnersApplyingAsExecutors(partnerExecutorList)
                .dispenseWithNoticeOtherExecsList(null)
                .solsAdditionalExecutorList(null)
                .primaryApplicantForenames("forename");

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.mapFromPartnerExecutorsToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.removeSolicitorFromApplyingList(
                additionalExecutorApplying)).thenReturn(additionalExecutorApplying);

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertTrue(responseCaseData.getAdditionalExecutorsNotApplying().isEmpty());
        assertEquals(additionalExecutorApplying, responseCaseData.getAdditionalExecutorsApplying());
        verify(executorListMapperServiceMock, times(1))
                .mapFromPartnerExecutorsToApplyingExecutors(any());
    }

    @Test
    public void shouldSetCaseworkerApplyingWithSolsAdditionalExecutorLists() {
        caseDataBuilder
                .additionalExecutorsTrustCorpList(null)
                .otherPartnersApplyingAsExecutors(null)
                .dispenseWithNoticeOtherExecsList(null)
                .solsAdditionalExecutorList(solsAdditionalExecutorList)
                .primaryApplicantForenames("forename");

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.removeSolicitorFromApplyingList(
                additionalExecutorApplying)).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.mapFromSolsAdditionalExecutorListToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);


        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertTrue(responseCaseData.getAdditionalExecutorsNotApplying().isEmpty());
        assertEquals(additionalExecutorApplying, responseCaseData.getAdditionalExecutorsApplying());
        verify(executorListMapperServiceMock, times(1))
                .mapFromSolsAdditionalExecutorListToApplyingExecutors(any());
    }

    @Test
    public void shouldSetCaseworkerNotApplyingWithSolicitorInfo_IsExec_NotApplying() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.addSolicitorToNotApplyingList(
                caseDetailsMock.getData(), new ArrayList<>())).thenReturn(additionalExecutorNotApplying);

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(additionalExecutorNotApplying, responseCaseData.getAdditionalExecutorsNotApplying());
        verify(executorListMapperServiceMock, times(1))
                .addSolicitorToNotApplyingList(any(), any());
    }

    @Test
    public void caseworkerNotApplyingListShouldBeEmpty_NotExec() {
        caseDataBuilder
                .solsSolicitorIsExec(NO);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertTrue(responseCaseData.getAdditionalExecutorsNotApplying().isEmpty());
        verify(executorListMapperServiceMock, times(0))
                .addSolicitorToNotApplyingList(any(), any());
    }
    

    @Test
    public void shouldRemoveSolicitorInfoFromCaseworkerNotApplying_IsApplying() {
        caseDataBuilder
                .solsSolicitorIsApplying(YES);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertTrue(responseCaseData.getAdditionalExecutorsNotApplying().isEmpty());
        verify(executorListMapperServiceMock, times(1))
                .removeSolicitorFromNotApplyingList(any());
    }

    @Test
    public void shouldSetPrimaryApplicantFields() {
        caseDataBuilder
                .primaryApplicantForenames(null)
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .solsAdditionalExecutorList(solsAdditionalExecutorList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.mapFromSolsAdditionalExecutorListToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.addSolicitorToApplyingList(
                caseDetailsMock.getData(), additionalExecutorApplying)).thenReturn(additionalExecutorApplying);

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(EXEC_FIRST_NAME, responseCaseData.getPrimaryApplicantForenames());
        assertEquals(EXEC_SURNAME, responseCaseData.getPrimaryApplicantSurname());
        assertEquals(EXEC_ADDRESS, responseCaseData.getPrimaryApplicantAddress());
        assertNull(responseCaseData.getPrimaryApplicantAlias());
        assertEquals(NO, responseCaseData.getPrimaryApplicantHasAlias());
        assertEquals(YES, responseCaseData.getPrimaryApplicantIsApplying());
        assertNull(responseCaseData.getSolsPrimaryExecutorNotApplyingReason());
        assertTrue(responseCaseData.getAdditionalExecutorsApplying().isEmpty());
    }


    @Test
    public void shouldNotSetPrimaryApplicantFields_ForenameSet() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .primaryApplicantForenames(EXEC_FIRST_NAME)
                .solsAdditionalExecutorList(solsAdditionalExecutorList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.mapFromSolsAdditionalExecutorListToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.addSolicitorToApplyingList(
                caseDetailsMock.getData(), additionalExecutorApplying)).thenReturn(additionalExecutorApplying);

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(additionalExecutorApplying, responseCaseData.getAdditionalExecutorsApplying());
        assertNull(responseCaseData.getPrimaryApplicantForenames());
        assertNull(responseCaseData.getPrimaryApplicantSurname());
        assertNull(responseCaseData.getPrimaryApplicantAddress());
        assertNull(responseCaseData.getPrimaryApplicantAlias());
        assertNull(responseCaseData.getPrimaryApplicantHasAlias());
        assertNull(responseCaseData.getPrimaryApplicantIsApplying());
        assertNull(responseCaseData.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldNotSetPrimaryApplicantFields_ApplicantExecsListEmpty() {
        caseDataBuilder
                .primaryApplicantForenames(EXEC_FIRST_NAME);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertTrue(responseCaseData.getAdditionalExecutorsApplying().isEmpty());
        assertNull(responseCaseData.getPrimaryApplicantForenames());
        assertNull(responseCaseData.getPrimaryApplicantSurname());
        assertNull(responseCaseData.getPrimaryApplicantAddress());
        assertNull(responseCaseData.getPrimaryApplicantAlias());
        assertNull(responseCaseData.getPrimaryApplicantHasAlias());
        assertNull(responseCaseData.getPrimaryApplicantIsApplying());
        assertNull(responseCaseData.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldSetExecutorNamesList_SolicitorIsApplying() {
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecsApplyingWithSol = new ArrayList<>();
        additionalExecsApplyingWithSol.add(new CollectionMember<>(SOLICITOR_ID, EXECUTOR_APPLYING));
        additionalExecsApplyingWithSol.add(new CollectionMember<>(null, EXECUTOR_APPLYING));

        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.addSolicitorToApplyingList(
                caseDetailsMock.getData(), additionalExecutorApplying)).thenReturn(additionalExecsApplyingWithSol);
        when(executorListMapperServiceMock.removeSolicitorFromNotApplyingList(
                additionalExecutorNotApplying)).thenReturn(additionalExecutorNotApplying);
        when(executorListMapperServiceMock.mapFromTrustCorpExecutorsToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.mapFromDispenseWithNoticeExecsToNotApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorNotApplying);

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToExecutorNamesLists(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(EXEC_NAME + ", " + EXEC_NAME, responseCaseData.getSolsIdentifiedApplyingExecs());
        assertEquals(EXEC_NAME, responseCaseData.getSolsIdentifiedNotApplyingExecs());
        verify(executorListMapperServiceMock, times(1))
                .mapFromTrustCorpExecutorsToApplyingExecutors(any());
        verify(executorListMapperServiceMock, times(1))
                .mapFromDispenseWithNoticeExecsToNotApplyingExecutors(any());
        verify(executorListMapperServiceMock, times(1))
                .removeSolicitorFromNotApplyingList(any());
    }

    @Test
    public void shouldSetExecutorNamesList_SolicitorNotApplying() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.addSolicitorToNotApplyingList(
                caseDetailsMock.getData(), additionalExecutorNotApplying)).thenReturn(additionalExecutorNotApplying);
        when(executorListMapperServiceMock.removeSolicitorFromApplyingList(
                additionalExecutorApplying)).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.mapFromTrustCorpExecutorsToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.mapFromDispenseWithNoticeExecsToNotApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorNotApplying);
        when(executorListMapperServiceMock.addSolicitorToNotApplyingList(caseDetailsMock.getData(),
                additionalExecutorNotApplying)).thenReturn(additionalExecutorNotApplying);


        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToExecutorNamesLists(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(EXEC_NAME, responseCaseData.getSolsIdentifiedApplyingExecs());
        assertEquals(EXEC_NAME, responseCaseData.getSolsIdentifiedNotApplyingExecs());
        verify(executorListMapperServiceMock, times(1))
                .mapFromTrustCorpExecutorsToApplyingExecutors(any());
        verify(executorListMapperServiceMock, times(1))
                .mapFromDispenseWithNoticeExecsToNotApplyingExecutors(any());
        verify(executorListMapperServiceMock, times(1))
                .addSolicitorToNotApplyingList(any(), any());
    }

    @Test
    public void shouldSetSolicitorExecutorListsToNull() {
        responseCaseDataBuilder
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .otherPartnersApplyingAsExecutors(partnerExecutorList)
                .solsAdditionalExecutorList(solsAdditionalExecutorList)
                .dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecList);

        solicitorExecutorTransformerMock.nullSolicitorExecutorLists(responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertNull(responseCaseData.getAdditionalExecutorsTrustCorpList());
        assertNull(responseCaseData.getOtherPartnersApplyingAsExecutors());
        assertNull(responseCaseData.getSolsAdditionalExecutorList());
        assertNull(responseCaseData.getDispenseWithNoticeOtherExecsList());
    }


}
