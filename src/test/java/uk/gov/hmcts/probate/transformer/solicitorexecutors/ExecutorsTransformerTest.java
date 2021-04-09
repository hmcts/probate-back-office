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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.util.CommonVariables.ADDITIONAL_EXECUTOR_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.ADDITIONAL_EXECUTOR_NOT_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_ADDRESS;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_FIRST_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.NO;
import static uk.gov.hmcts.probate.util.CommonVariables.PARTNER_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.PRIMARY_APPLICANT_FORENAME;
import static uk.gov.hmcts.probate.util.CommonVariables.PRIMARY_APPLICANT_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_ADDITIONAL_EXECUTOR_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_ADDRESS;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_FIRM_EMAIL;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_FIRM_PHONE;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_ID;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_NOT_APPLYING_REASON;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_SOT_FORENAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_SOT_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLS_EXEC_ADDITIONAL_APPLYING;
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
    private List<CollectionMember<AdditionalExecutorApplying>> solsAdditionalExecutorApplyingList = new ArrayList<>();
    private List<CollectionMember<AdditionalExecutorTrustCorps>> trustCorpsExecutorList;
    private List<CollectionMember<AdditionalExecutorPartners>> partnerExecutorList;
    private List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeExecList;

    @Before
    public void setUp() {
        additionalExecutorApplying = new ArrayList<>();
        additionalExecutorApplying.add(new CollectionMember<>(SOLICITOR_ID, ADDITIONAL_EXECUTOR_APPLYING));

        additionalExecutorNotApplying = new ArrayList<>();
        additionalExecutorNotApplying.add(new CollectionMember<>(SOLICITOR_ID, ADDITIONAL_EXECUTOR_NOT_APPLYING));

        solsAdditionalExecutorList = new ArrayList<>();
        solsAdditionalExecutorList.add(SOLS_EXEC_ADDITIONAL_APPLYING);
        solsAdditionalExecutorList.add(SOLS_EXEC_NOT_APPLYING);

        solsAdditionalExecutorApplyingList.add(SOLS_EXEC_APPLYING);

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
        solAdditionalExecutorApplying.add(new CollectionMember<>(SOLICITOR_ID, SOLICITOR_ADDITIONAL_EXECUTOR_APPLYING));

        caseDataBuilder.solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorPhoneNumber(SOLICITOR_FIRM_PHONE)
                .solsSolicitorEmail(SOLICITOR_FIRM_EMAIL)
                .solsSolicitorAddress(SOLICITOR_ADDRESS);

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);
        when(executorListMapperServiceMock.addSolicitorToApplyingList(
                caseDetailsMock.getData(), new ArrayList<>())).thenReturn(solAdditionalExecutorApplying);

        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());

        assertEquals(SOLICITOR_SOT_FORENAME, cd.getPrimaryApplicantForenames());
        assertEquals(SOLICITOR_SOT_SURNAME, cd.getPrimaryApplicantSurname());
        assertEquals(SOLICITOR_FIRM_PHONE, cd.getPrimaryApplicantPhoneNumber());
        assertEquals(SOLICITOR_FIRM_EMAIL, cd.getPrimaryApplicantEmailAddress());
        assertEquals(SOLICITOR_ADDRESS, cd.getPrimaryApplicantAddress());
        assertNull(responseCaseDataBuilder.build().getPrimaryApplicantAlias());
        assertEquals(NO, cd.getPrimaryApplicantHasAlias());
        assertEquals(YES, cd.getPrimaryApplicantIsApplying());
        assertNull(cd.getSolsSolicitorNotApplyingReason());
        assertNull(cd.getSolsPrimaryExecutorNotApplyingReason());
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

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);

        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());

        assertNull(cd.getSolsPrimaryExecutorNotApplyingReason());
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

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);

        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());

        assertNull(cd.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldNotSetSolicitorAsPrimaryApplicantIfSolIsMainAppFieldSetToNo() {
        responseCaseDataBuilder
                .primaryApplicantForenames(PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES);

        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON);

        final CaseData cd = caseDataBuilder.build();
        when(caseDetailsMock.getData()).thenReturn(cd);

        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());

        ResponseCaseData response = responseCaseDataBuilder.build();
        assertNull(response.getSolsPrimaryExecutorNotApplyingReason());
        assertEquals(PRIMARY_APPLICANT_FORENAME, response.getPrimaryApplicantForenames());
        assertEquals(PRIMARY_APPLICANT_SURNAME, response.getPrimaryApplicantSurname());
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

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);
        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());


        assertEquals(YES, cd.getSolsSolicitorIsExec());
        assertEquals(SOLICITOR_SOT_FORENAME, cd.getSolsSOTForenames());
        assertEquals(SOLICITOR_SOT_SURNAME, cd.getSolsSOTSurname());
        assertEquals(NO, cd.getSolsSolicitorIsApplying());
        assertEquals(SOLICITOR_NOT_APPLYING_REASON, cd.getSolsSolicitorNotApplyingReason());
    }

    @Test
    public void shouldSwapSolicitorToNotApplyingList() {
        caseDataBuilder
                .additionalExecutorsApplying(additionalExecutorApplying)
                .additionalExecutorsNotApplying(additionalExecutorNotApplying)
                .primaryApplicantForenames(EXEC_FIRST_NAME);

        final CaseData cd = caseDataBuilder.build();

        new SolicitorApplicationCompletionTransformer(new ExecutorListMapperService())
                .mapSolicitorExecutorFieldsOnCompletion(cd);

        assertEquals(0, cd.getAdditionalExecutorsApplying().size());
        assertEquals(1, cd.getAdditionalExecutorsNotApplying().size());
    }

    @Test
    public void shouldSetCaseworkerExecutorListsAsEmpty() {
        caseDataBuilder
                .additionalExecutorsTrustCorpList(null)
                .otherPartnersApplyingAsExecutors(null)
                .dispenseWithNoticeOtherExecsList(null)
                .solsAdditionalExecutorList(null);

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);

        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());

        assertTrue(cd.getAdditionalExecutorsApplying().isEmpty());
        assertTrue(cd.getAdditionalExecutorsNotApplying().isEmpty());
    }

    @Test
    public void shouldSetCaseworkerNotApplyingExecutorLists() {
        caseDataBuilder
                .additionalExecutorsTrustCorpList(null)
                .otherPartnersApplyingAsExecutors(null)
                .dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecList)
                .solsAdditionalExecutorList(solsAdditionalExecutorList);

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);
        when(executorListMapperServiceMock.mapFromDispenseWithNoticeExecsToNotApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorNotApplying);
        when(executorListMapperServiceMock.addSolicitorToNotApplyingList(caseDetailsMock.getData(),
                additionalExecutorNotApplying)).thenReturn(additionalExecutorNotApplying);

        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());

        List<CollectionMember<AdditionalExecutorNotApplying>> expected = additionalExecutorNotApplying;
        expected.addAll(additionalExecutorNotApplying);

        assertTrue(cd.getAdditionalExecutorsApplying().isEmpty());
        assertEquals(expected, cd.getAdditionalExecutorsNotApplying());
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

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);
        when(executorListMapperServiceMock.mapFromTrustCorpExecutorsToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.removeSolicitorFromApplyingList(
                additionalExecutorApplying)).thenReturn(additionalExecutorApplying);

        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());

        assertTrue(cd.getAdditionalExecutorsNotApplying().isEmpty());
        assertEquals(additionalExecutorApplying, cd.getAdditionalExecutorsApplying());
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

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);
        when(executorListMapperServiceMock.mapFromPartnerExecutorsToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.removeSolicitorFromApplyingList(
                additionalExecutorApplying)).thenReturn(additionalExecutorApplying);

        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());

        assertTrue(cd.getAdditionalExecutorsNotApplying().isEmpty());
        assertEquals(additionalExecutorApplying, cd.getAdditionalExecutorsApplying());
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

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);
        when(executorListMapperServiceMock.removeSolicitorFromApplyingList(
                additionalExecutorApplying)).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.mapFromSolsAdditionalExecutorListToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);


        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());

        assertTrue(cd.getAdditionalExecutorsNotApplying().isEmpty());
        assertEquals(additionalExecutorApplying, cd.getAdditionalExecutorsApplying());
        verify(executorListMapperServiceMock, times(1))
                .mapFromSolsAdditionalExecutorListToApplyingExecutors(any());
    }

    @Test
    public void shouldSetCaseworkerNotApplyingWithSolicitorInfo_IsExec_NotApplying() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO);

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.addSolicitorToNotApplyingList(
                caseDetailsMock.getData(), new ArrayList<>())).thenReturn(additionalExecutorNotApplying);

        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(cd);

        verify(executorListMapperServiceMock, times(1))
                .addSolicitorToNotApplyingList(any(), any());
    }
    

    @Test
    public void shouldRemoveSolicitorInfoFromCaseworkerNotApplying_IsApplying() {
        caseDataBuilder
                .solsSolicitorIsApplying(YES);

        final CaseData cd = caseDataBuilder.build();

        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(cd);

        assertTrue(cd.getAdditionalExecutorsNotApplying().isEmpty());
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

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);
        when(executorListMapperServiceMock.mapFromSolsAdditionalExecutorListToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.addSolicitorToApplyingList(
                caseDetailsMock.getData(), additionalExecutorApplying)).thenReturn(additionalExecutorApplying);

        solicitorExecutorTransformerMock
                .mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());

        assertEquals(EXEC_FIRST_NAME, cd.getPrimaryApplicantForenames());
        assertEquals(EXEC_SURNAME, cd.getPrimaryApplicantSurname());
        assertEquals(EXEC_ADDRESS, cd.getPrimaryApplicantAddress());
        assertNull(cd.getPrimaryApplicantAlias());
        assertEquals(NO, cd.getPrimaryApplicantHasAlias());
        assertEquals(YES, cd.getPrimaryApplicantIsApplying());
        assertNull(cd.getSolsPrimaryExecutorNotApplyingReason());
        assertTrue(cd.getAdditionalExecutorsApplying().isEmpty());
    }


    @Test
    public void shouldNotSetPrimaryApplicantFields_ForenameSet() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .primaryApplicantForenames(EXEC_FIRST_NAME)
                .solsAdditionalExecutorList(solsAdditionalExecutorList);

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);
        ExecutorsTransformer et = new ExecutorsTransformer(new ExecutorListMapperService());
        et.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());

        assertEquals(solsAdditionalExecutorApplyingList.get(0), cd.getAdditionalExecutorsApplying().get(0));
        assertEquals(EXEC_FIRST_NAME, cd.getPrimaryApplicantForenames());
        assertNull(cd.getPrimaryApplicantSurname());
        assertNull(cd.getPrimaryApplicantAddress());
        assertNull(cd.getPrimaryApplicantAlias());
        assertNull(cd.getPrimaryApplicantHasAlias());
        assertNull(cd.getPrimaryApplicantIsApplying());
        assertNull(cd.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldNotSetPrimaryApplicantFields_ApplicantExecsListEmpty() {
        caseDataBuilder
                .primaryApplicantForenames(EXEC_FIRST_NAME);

        final CaseData cd = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(cd);

        ExecutorsTransformer et = new ExecutorsTransformer(new ExecutorListMapperService());
        et.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseDetailsMock.getData());
        assertTrue(cd.getAdditionalExecutorsApplying().isEmpty());
        assertEquals(EXEC_FIRST_NAME, cd.getPrimaryApplicantForenames());
        assertNull(cd.getPrimaryApplicantSurname());
        assertNull(cd.getPrimaryApplicantAddress());
        assertNull(cd.getPrimaryApplicantAlias());
        assertNull(cd.getPrimaryApplicantHasAlias());
        assertNull(cd.getPrimaryApplicantIsApplying());
        assertNull(cd.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldSetExecutorNamesList_SolicitorIsApplying() {
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecsApplyingWithSol = new ArrayList<>();
        additionalExecsApplyingWithSol.add(new CollectionMember<>(SOLICITOR_ID, ADDITIONAL_EXECUTOR_APPLYING));
        additionalExecsApplyingWithSol.add(new CollectionMember<>(null, ADDITIONAL_EXECUTOR_APPLYING));

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
    public void shouldSetExecutorNamesListToNone_SolicitorIsApplying() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToExecutorNamesLists(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals("None", responseCaseData.getSolsIdentifiedApplyingExecs());
    }

    @Test
    public void shouldSetExecutorNamesListToNone_SolicitorNotApplying() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mapSolicitorExecutorFieldsToExecutorNamesLists(
                caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals("None", responseCaseData.getSolsIdentifiedNotApplyingExecs());
    }

    @Test
    public void shouldSetSolicitorExecutorListsToNull() {
        caseDataBuilder
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .otherPartnersApplyingAsExecutors(partnerExecutorList)
                .solsAdditionalExecutorList(solsAdditionalExecutorList)
                .dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecList);

        final CaseData cd = caseDataBuilder.build();

        solicitorExecutorTransformerMock.clearSolicitorExecutorLists(cd);

        assertTrue(cd.getAdditionalExecutorsTrustCorpList().isEmpty());
        assertTrue(cd.getOtherPartnersApplyingAsExecutors().isEmpty());
        assertTrue(cd.getSolsAdditionalExecutorList().isEmpty());
        assertTrue(cd.getDispenseWithNoticeOtherExecsList().isEmpty());
    }


}
