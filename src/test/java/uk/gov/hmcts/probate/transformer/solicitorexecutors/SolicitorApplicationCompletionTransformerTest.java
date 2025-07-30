package uk.gov.hmcts.probate.transformer.solicitorexecutors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CASE_TYPE_GRANT_OF_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_NONE_OF_THESE;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;
import static uk.gov.hmcts.probate.util.CommonVariables.ADDITIONAL_EXECUTOR_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.ADDITIONAL_EXECUTOR_NOT_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_ADDRESS;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_FIRST_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_ID;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_TRUST_CORP_POS;
import static uk.gov.hmcts.probate.util.CommonVariables.NO;
import static uk.gov.hmcts.probate.util.CommonVariables.PARTNER_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.PRIMARY_EXEC_ALIAS_NAMES;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLS_EXEC_ADDITIONAL_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLS_EXEC_NOT_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.YES;

class SolicitorApplicationCompletionTransformerTest {

    @Mock
    private DateFormatterService dateFormatterServiceMock;

    @Mock
    private ExecutorListMapperService executorListMapperServiceMock;


    @InjectMocks
    private SolicitorApplicationCompletionTransformer solicitorApplicationCompletionTransformer;

    private AutoCloseable closeableMocks;

    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorApplying;
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorNotApplying;
    private List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList;
    private List<CollectionMember<AdditionalExecutorTrustCorps>> trustCorpsExecutorList;
    private List<CollectionMember<AdditionalExecutorPartners>> partnerExecutorList;
    private List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeExecList;
    private static final String NOT_APPLICABLE = "NotApplicable";

    @BeforeEach
    void setUp() {
        additionalExecutorApplying = new ArrayList<>();
        additionalExecutorApplying.add(new CollectionMember<>(EXEC_ID, ADDITIONAL_EXECUTOR_APPLYING));

        additionalExecutorNotApplying = new ArrayList<>();
        additionalExecutorNotApplying.add(new CollectionMember<>(EXEC_ID, ADDITIONAL_EXECUTOR_NOT_APPLYING));

        solsAdditionalExecutorList = new ArrayList<>();
        solsAdditionalExecutorList.add(SOLS_EXEC_ADDITIONAL_APPLYING);
        solsAdditionalExecutorList.add(SOLS_EXEC_NOT_APPLYING);

        trustCorpsExecutorList = new ArrayList<>();
        trustCorpsExecutorList.add(new CollectionMember(EXEC_ID,
                AdditionalExecutorTrustCorps.builder()
                        .additionalExecForenames(EXEC_FIRST_NAME)
                        .additionalExecLastname(EXEC_SURNAME)
                        .additionalExecutorTrustCorpPosition(EXEC_TRUST_CORP_POS)
                        .build()));

        partnerExecutorList = new ArrayList<>();
        partnerExecutorList.add(PARTNER_EXEC);

        dispenseWithNoticeExecList = new ArrayList<>();
        dispenseWithNoticeExecList.add(DISPENSE_WITH_NOTICE_EXEC);

        closeableMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void cleanUp() throws Exception {
        closeableMocks.close();
    }

    /* Should this test be in this Test class? It's really relying on the ExecutorListMapperService
     * to do the work being tested here. (Hence needing to create a new instance with a spy rather
     * than the common handling with a mock.)
     */
    @Test
    void shouldSetLegalStatementFieldsWithApplyingExecutorInfo() {
        final CaseData caseData = CaseData.builder()
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP)
                .anyOtherApplyingPartnersTrustCorp(YES)
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .solsAdditionalExecutorList(solsAdditionalExecutorList)
                .build();

        final var executorListMapperSpy = spy(ExecutorListMapperService.class);

        final var solApplComplXform = new SolicitorApplicationCompletionTransformer(
                executorListMapperSpy,
                dateFormatterServiceMock);

        solApplComplXform.mapSolicitorExecutorFieldsOnCompletion(caseData);

        assertAll(
                () -> assertEquals(2, caseData.getAdditionalExecutorsApplying().size()),
                () -> assertEquals(3, caseData.getExecutorsApplyingLegalStatement().size()),
                () -> verifyNoInteractions(dateFormatterServiceMock)
        );
    }

    @Test
    void shouldSetLegalStatementFieldsWithNotApplyingExecutorInfo() {
        final CaseDetails caseDetailsMock = mock(CaseDetails.class);
        final CaseData caseData = CaseData.builder()
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO)
                .additionalExecutorsTrustCorpList(null)
                .otherPartnersApplyingAsExecutors(null)
                .dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecList)
                .solsAdditionalExecutorList(solsAdditionalExecutorList)
                .build();

        when(caseDetailsMock.getData()).thenReturn(caseData);
        when(executorListMapperServiceMock.mapFromDispenseWithNoticeExecsToNotApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorNotApplying);
        when(executorListMapperServiceMock.addSolicitorToNotApplyingList(
                caseDetailsMock.getData(), additionalExecutorNotApplying)).thenReturn(additionalExecutorNotApplying);

        solicitorApplicationCompletionTransformer.mapSolicitorExecutorFieldsOnCompletion(caseData);

        List<CollectionMember<AdditionalExecutorNotApplying>> legalStatementExecutors = new ArrayList<>();
        legalStatementExecutors.addAll(additionalExecutorNotApplying);

        assertEquals(legalStatementExecutors, caseData.getExecutorsNotApplyingLegalStatement());
        assertEquals(new ArrayList<>(), caseData.getExecutorsApplyingLegalStatement());
    }

    @Test
    void shouldSetLegalStatementFieldsWithApplyingExecutorInfo_PrimaryApplicantApplying() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(EXEC_FIRST_NAME)
                .primaryApplicantSurname(EXEC_SURNAME)
                .primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES)
                .primaryApplicantAddress(EXEC_ADDRESS)
                .primaryApplicantIsApplying(YES)
                .build();


        when(executorListMapperServiceMock.mapFromPrimaryApplicantToApplyingExecutor(caseData))
                .thenReturn(new CollectionMember<>(EXEC_ID, ADDITIONAL_EXECUTOR_APPLYING));

        solicitorApplicationCompletionTransformer.mapSolicitorExecutorFieldsOnCompletion(caseData);

        assertEquals(additionalExecutorApplying, caseData.getExecutorsApplyingLegalStatement());
        assertEquals(new ArrayList<>(), caseData.getExecutorsNotApplyingLegalStatement());
    }

    @Test
    void shouldSetLegalStatementFieldsWithApplyingExecutorInfo_PrimaryApplicantNotApplying() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantIsApplying(NO)
                .solsSolicitorIsApplying(NO)
                .solsSolicitorIsExec(YES)
                .build();

        when(executorListMapperServiceMock.mapFromPrimaryApplicantToNotApplyingExecutor(caseData))
                .thenReturn(new CollectionMember<>(EXEC_ID, ADDITIONAL_EXECUTOR_NOT_APPLYING));

        solicitorApplicationCompletionTransformer.mapSolicitorExecutorFieldsOnCompletion(caseData);

        assertEquals(additionalExecutorNotApplying, caseData.getExecutorsNotApplyingLegalStatement());
        assertEquals(new ArrayList<>(), caseData.getExecutorsApplyingLegalStatement());
    }

    @Test
    void shouldSetLegalStatementFieldsWithApplyingExecutorInfoYesNo() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantIsApplying(NO)
                .solsSolicitorIsApplying(NO)
                .solsSolicitorIsExec(YES)
                .build();


        when(executorListMapperServiceMock.mapFromPrimaryApplicantToNotApplyingExecutor(caseData))
                .thenReturn(new CollectionMember<>(EXEC_ID, ADDITIONAL_EXECUTOR_NOT_APPLYING));

        solicitorApplicationCompletionTransformer.mapSolicitorExecutorFieldsOnAppDetailsComplete(caseData);

        assertEquals(additionalExecutorNotApplying, caseData.getExecutorsNotApplyingLegalStatement());
        assertEquals(new ArrayList<>(), caseData.getExecutorsApplyingLegalStatement());
    }

    @Test
    void shouldEraseCodicilAddedDateIfWillHasNoCodicils() {
        final List<CollectionMember<CodicilAddedDate>> codicilDates =
                Arrays.asList(new CollectionMember<>(CodicilAddedDate.builder()
                        .dateCodicilAdded(LocalDate.now().minusDays(1)).build()));
        final List<CollectionMember<String>> formattedDate =
                Arrays.asList(new CollectionMember<>("Formatted Date"));

        final CaseData caseData = CaseData.builder()
                .willHasCodicils(NO)
                .codicilAddedDateList(codicilDates)
                .codicilAddedFormattedDateList(formattedDate)
                .build();

        solicitorApplicationCompletionTransformer.eraseCodicilAddedDateIfWillHasNoCodicils(caseData);

        assertNull(caseData.getCodicilAddedDateList());
        assertNull(caseData.getCodicilAddedFormattedDateList());
    }

    @Test
    void shouldSetServiceRequest() {
        final BigDecimal totalAmount = BigDecimal.valueOf(100000);
        final CaseData caseData = CaseData.builder().build();
        final CaseDetails caseDetails = new CaseDetails(caseData, null, 0L);
        solicitorApplicationCompletionTransformer.setFieldsOnServiceRequest(caseDetails, totalAmount);

        assertNull(caseData.getPaymentTaken());
    }

    @Test
    void shouldSetPaymentTakenNotApplicableWhenNoServiceRequest() {
        final BigDecimal totalAmount = BigDecimal.ZERO;
        final CaseData caseData = CaseData.builder().build();
        final CaseDetails caseDetails = new CaseDetails(caseData, null, 0L);
        solicitorApplicationCompletionTransformer.setFieldsOnServiceRequest(caseDetails, totalAmount);

        assertEquals(NOT_APPLICABLE, caseData.getPaymentTaken());
    }

    // given Case
    // and CaseType is GrantOfProbate
    // and TitleClearingType is not NoneOfThese
    // and PrimaryApplicantApplying is true
    // when transformer clearPrimaryForNoneOfThese called
    // then primaryApplicantClear is called
    @Test
    void givenCaseWithoutNoneOfTheseTitleClearingTypeANDPrimaryApplicant_whenChecked_thenPrimaryApplicantDataCleared() {
        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = new CaseDetails(caseData, null, 0L);

        when(caseData.getCaseType()).thenReturn(CASE_TYPE_GRANT_OF_PROBATE);
        when(caseData.getTitleAndClearingType()).thenReturn("");
        when(caseData.isPrimaryApplicantApplying()).thenReturn(true);

        solicitorApplicationCompletionTransformer.clearPrimaryApplicantWhenNotInNoneOfTheseTitleAndClearingType(
                caseDetails);

        verify(caseData, times(1)).clearPrimaryApplicant();
    }

    // given Case
    // and CaseType is NOT GrantOfProbate
    // and TitleClearingType is not NoneOfThese
    // and PrimaryApplicantApplying is true
    // when transformer clearPrimaryForNoneOfThese called
    // then primaryApplicantClear is NOT called
    @Test
    void givenCaseNotGOPWoutNoneOfTheseTCTypeANDPrimaryApplicant_whenChecked_thenPrimaryApplicantDataNOTCleared() {
        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = new CaseDetails(caseData, null, 0L);


        when(caseData.getCaseType()).thenReturn("");
        when(caseData.getTitleAndClearingType()).thenReturn("");
        when(caseData.isPrimaryApplicantApplying()).thenReturn(true);

        solicitorApplicationCompletionTransformer.clearPrimaryApplicantWhenNotInNoneOfTheseTitleAndClearingType(
                caseDetails);

        verify(caseData, times(0)).clearPrimaryApplicant();
    }

    // given Case
    // and CaseType is GrantOfProbate
    // and TitleClearingType is NoneOfThese
    // and PrimaryApplicantApplying is true
    // when transformer clearPrimaryForNoneOfThese called
    // then primaryApplicantClear is not called
    @Test
    void givenCaseWithNoneOfTheseTitleClearingTypeANDPrimaryApplicant_whenChecked_thenPrimaryApplicantDataNOTCleared() {
        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = new CaseDetails(caseData, null, 0L);


        when(caseData.getCaseType()).thenReturn(CASE_TYPE_GRANT_OF_PROBATE);
        when(caseData.getTitleAndClearingType()).thenReturn(TITLE_AND_CLEARING_NONE_OF_THESE);
        when(caseData.isPrimaryApplicantApplying()).thenReturn(true);

        solicitorApplicationCompletionTransformer.clearPrimaryApplicantWhenNotInNoneOfTheseTitleAndClearingType(
                caseDetails);

        verify(caseData, times(0)).clearPrimaryApplicant();
    }

    // given Case
    // and CaseType is GrantOfProbate
    // and TitleClearingType is not NoneOfThese
    // and PrimaryApplicantApplying is false
    // when transformer clearPrimaryForNoneOfThese called
    // then primaryApplicantClear is not called
    @Test
    void givenCaseWoutNoneOfTheseTitleClearingTypeANDWoutPrmryApplicant_whenChecked_thenPrmryApplicantDataNOTCleared() {
        final CaseData realCaseData = CaseData.builder().build();
        final CaseData spyCaseData = spy(realCaseData);
        final CaseDetails caseDetails = new CaseDetails(spyCaseData, null, 0L);


        when(spyCaseData.getCaseType()).thenReturn(CASE_TYPE_GRANT_OF_PROBATE);
        when(spyCaseData.getTitleAndClearingType()).thenReturn("");
        when(spyCaseData.isPrimaryApplicantApplying()).thenReturn(false);

        solicitorApplicationCompletionTransformer.clearPrimaryApplicantWhenNotInNoneOfTheseTitleAndClearingType(
                caseDetails);

        verify(spyCaseData, times(0)).clearPrimaryApplicant();
    }

    @Test
    void givenNoAdditionalExecutorsCausesAdditionalExecutorsIsExplicitlyCleared() {
        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = new CaseDetails(caseData, null, 0L);

        when(caseData.getCaseType()).thenReturn(CASE_TYPE_GRANT_OF_PROBATE);
        when(caseData.getTitleAndClearingType()).thenReturn("");
        when(caseData.getOtherExecutorExists()).thenReturn(NO);

        solicitorApplicationCompletionTransformer.clearAdditionalExecutorWhenUpdatingApplicantDetails(caseDetails);

        verify(caseData, times(1)).clearAdditionalExecutorList();
        assertEquals(0, caseDetails.getData().getSolsAdditionalExecutorList().size());
    }
}
