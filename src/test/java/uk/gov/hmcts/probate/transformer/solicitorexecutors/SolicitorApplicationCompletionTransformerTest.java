package uk.gov.hmcts.probate.transformer.solicitorexecutors;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
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

@RunWith(MockitoJUnitRunner.class)
public class SolicitorApplicationCompletionTransformerTest {

    private final CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private ExecutorListMapperService executorListMapperServiceMock;

    @InjectMocks
    private SolicitorApplicationCompletionTransformer solicitorApplicationCompletionTransformerMock;

    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorApplying;
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorNotApplying;
    private List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList;
    private List<CollectionMember<AdditionalExecutorTrustCorps>> trustCorpsExecutorList;
    private List<CollectionMember<AdditionalExecutorPartners>> partnerExecutorList;
    private List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeExecList;

    @Before
    public void setUp() {
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
    }


    @Test
    public void shouldSetLegalStatementFieldsWithApplyingExecutorInfo() {

        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .solsAdditionalExecutorList(solsAdditionalExecutorList);

        CaseData caseData = caseDataBuilder.build();

        SolicitorApplicationCompletionTransformer solJourneyCompletion =
                new SolicitorApplicationCompletionTransformer(new ExecutorListMapperService());

        solJourneyCompletion.mapSolicitorExecutorFieldsOnCompletion(caseData);

        assertEquals(2, caseData.getAdditionalExecutorsApplying().size());
        assertEquals(3, caseData.getExecutorsApplyingLegalStatement().size());
    }

    @Test
    public void shouldSetLegalStatementFieldsWithNotApplyingExecutorInfo() {
        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(NO)
                .additionalExecutorsTrustCorpList(null)
                .otherPartnersApplyingAsExecutors(null)
                .dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecList)
                .solsAdditionalExecutorList(solsAdditionalExecutorList);

        CaseData caseData = caseDataBuilder.build();

        when(caseDetailsMock.getData()).thenReturn(caseData);
        when(executorListMapperServiceMock.mapFromDispenseWithNoticeExecsToNotApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorNotApplying);
        when(executorListMapperServiceMock.addSolicitorToNotApplyingList(
                caseDetailsMock.getData(), additionalExecutorNotApplying)).thenReturn(additionalExecutorNotApplying);

        solicitorApplicationCompletionTransformerMock.mapSolicitorExecutorFieldsOnCompletion(caseData);

        List<CollectionMember<AdditionalExecutorNotApplying>> legalStatementExecutors = new ArrayList<>();
        legalStatementExecutors.addAll(additionalExecutorNotApplying);

        assertEquals(legalStatementExecutors, caseData.getExecutorsNotApplyingLegalStatement());
        assertEquals(new ArrayList<>(), caseData.getExecutorsApplyingLegalStatement());
    }

    @Test
    public void shouldSetLegalStatementFieldsWithApplyingExecutorInfo_PrimaryApplicantApplying()
            throws JsonProcessingException {
        caseDataBuilder
            .primaryApplicantForenames(EXEC_FIRST_NAME)
            .primaryApplicantSurname(EXEC_SURNAME)
            .primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES)
            .primaryApplicantAddress(EXEC_ADDRESS)
            .primaryApplicantIsApplying(YES);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.mapFromPrimaryApplicantToApplyingExecutor(
                caseDetailsMock.getData())).thenReturn(new CollectionMember<>(EXEC_ID, ADDITIONAL_EXECUTOR_APPLYING));

        CaseData caseData = caseDetailsMock.getData();
        solicitorApplicationCompletionTransformerMock.mapSolicitorExecutorFieldsOnCompletion(caseData);

        assertEquals(additionalExecutorApplying, caseData.getExecutorsApplyingLegalStatement());
        assertEquals(new ArrayList<>(), caseData.getExecutorsNotApplyingLegalStatement());
    }

    @Test
    public void shouldSetLegalStatementFieldsWithApplyingExecutorInfo_PrimaryApplicantNotApplying()
            throws JsonProcessingException {
        caseDataBuilder
                .primaryApplicantIsApplying(NO)
                .solsSolicitorIsApplying(NO)
                .solsSolicitorIsExec(YES);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.mapFromPrimaryApplicantToNotApplyingExecutor(
                caseDetailsMock.getData())).thenReturn(new CollectionMember<>(EXEC_ID,
                    ADDITIONAL_EXECUTOR_NOT_APPLYING));

        CaseData caseData = caseDetailsMock.getData();
        solicitorApplicationCompletionTransformerMock.mapSolicitorExecutorFieldsOnCompletion(caseData);

        assertEquals(additionalExecutorNotApplying, caseData.getExecutorsNotApplyingLegalStatement());
        assertEquals(new ArrayList<>(), caseData.getExecutorsApplyingLegalStatement());
    }

}
