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
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;
import static uk.gov.hmcts.probate.util.CommonVariables.DATE;
import static uk.gov.hmcts.probate.util.CommonVariables.DATE_FORMATTED;
import static uk.gov.hmcts.probate.util.CommonVariables.DECEASED_FORENAME;
import static uk.gov.hmcts.probate.util.CommonVariables.DECEASED_FORENAME_FORMATTED;
import static uk.gov.hmcts.probate.util.CommonVariables.DECEASED_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.DECEASED_SURNAME_FORMATTED;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.EXECUTOR_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.EXECUTOR_NOT_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_ADDRESS;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_FIRST_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_ID;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_TRUST_CORP_POS;
import static uk.gov.hmcts.probate.util.CommonVariables.NO;
import static uk.gov.hmcts.probate.util.CommonVariables.PARTNER_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.PRIMARY_EXEC_ALIAS_NAMES;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_FIRM_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLS_EXEC_ADDITIONAL_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLS_EXEC_NOT_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.YES;

@RunWith(MockitoJUnitRunner.class)
public class LegalStatementExecutorTransformerTest {

    private final CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private DateFormatterService dateFormatterServiceMock;

    @Mock
    private ExecutorListMapperService executorListMapperServiceMock;

    @InjectMocks
    private LegalStatementExecutorTransformer legalStatementExecutorTransformerMock;

    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorApplying;
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorNotApplying;
    private List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList;
    private List<CollectionMember<AdditionalExecutorTrustCorps>> trustCorpsExecutorList;
    private List<CollectionMember<AdditionalExecutorPartners>> partnerExecutorList;
    private List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeExecList;

    @Before
    public void setUp() {
        additionalExecutorApplying = new ArrayList<>();
        additionalExecutorApplying.add(new CollectionMember<>(EXEC_ID, EXECUTOR_APPLYING));

        additionalExecutorNotApplying = new ArrayList<>();
        additionalExecutorNotApplying.add(new CollectionMember<>(EXEC_ID, EXECUTOR_NOT_APPLYING));

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
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = new ArrayList<>();
        execsApplying.add(new CollectionMember<>(EXEC_ID, EXECUTOR_APPLYING));
        execsApplying.add(new CollectionMember<>(EXEC_ID, EXECUTOR_APPLYING));

        caseDataBuilder
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsApplying(YES)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP)
                .anyOtherApplyingPartnersTrustCorp(YES)
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .solsAdditionalExecutorList(solsAdditionalExecutorList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.mapFromTrustCorpExecutorsToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.mapFromSolsAdditionalExecutorListToApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorApplying);
        when(executorListMapperServiceMock.addSolicitorToApplyingList(
                caseDetailsMock.getData(), execsApplying)).thenReturn(execsApplying);

        legalStatementExecutorTransformerMock.mapSolicitorExecutorFieldsToLegalStatementExecutorFields(
                caseDetailsMock.getData());


        List<CollectionMember<AdditionalExecutorApplying>> legalStatementExecutors = new ArrayList<>();
        legalStatementExecutors.addAll(additionalExecutorApplying);
        legalStatementExecutors.addAll(additionalExecutorApplying);

        CaseData caseData = caseDetailsMock.getData();
        assertEquals(legalStatementExecutors, caseData.getExecutorsApplyingLegalStatement());
        assertEquals(new ArrayList<>(), caseData.getExecutorsNotApplyingLegalStatement());
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

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.mapFromDispenseWithNoticeExecsToNotApplyingExecutors(
                caseDetailsMock.getData())).thenReturn(additionalExecutorNotApplying);
        when(executorListMapperServiceMock.addSolicitorToNotApplyingList(
                caseDetailsMock.getData(), additionalExecutorNotApplying)).thenReturn(additionalExecutorNotApplying);

        legalStatementExecutorTransformerMock.mapSolicitorExecutorFieldsToLegalStatementExecutorFields(
                caseDetailsMock.getData());

        List<CollectionMember<AdditionalExecutorNotApplying>> legalStatementExecutors = new ArrayList<>();
        legalStatementExecutors.addAll(additionalExecutorNotApplying);

        CaseData caseData = caseDetailsMock.getData();
        assertEquals(legalStatementExecutors, caseData.getExecutorsNotApplyingLegalStatement());
        assertEquals(new ArrayList<>(), caseData.getExecutorsApplyingLegalStatement());
    }

    @Test
    public void shouldSetLegalStatementFieldsWithApplyingExecutorInfo_PrimaryApplicantApplying() {
        caseDataBuilder
                .primaryApplicantForenames(EXEC_FIRST_NAME)
                .primaryApplicantSurname(EXEC_SURNAME)
                .primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES)
                .primaryApplicantAddress(EXEC_ADDRESS)
                .primaryApplicantIsApplying(YES);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.mapFromPrimaryApplicantToApplyingExecutor(
                caseDetailsMock.getData())).thenReturn(new CollectionMember<>(EXEC_ID, EXECUTOR_APPLYING));

        legalStatementExecutorTransformerMock.mapSolicitorExecutorFieldsToLegalStatementExecutorFields(
                caseDetailsMock.getData());

        CaseData caseData = caseDetailsMock.getData();
        assertEquals(additionalExecutorApplying, caseData.getExecutorsApplyingLegalStatement());
        assertEquals(new ArrayList<>(), caseData.getExecutorsNotApplyingLegalStatement());
    }

    @Test
    public void shouldSetLegalStatementFieldsWithApplyingExecutorInfo_PrimaryApplicantNotApplying() {
        caseDataBuilder
                .primaryApplicantIsApplying(NO)
                .solsSolicitorIsApplying(NO)
                .solsSolicitorIsExec(YES);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(executorListMapperServiceMock.mapFromPrimaryApplicantToNotApplyingExecutor(
                caseDetailsMock.getData())).thenReturn(new CollectionMember<>(EXEC_ID, EXECUTOR_NOT_APPLYING));

        legalStatementExecutorTransformerMock.mapSolicitorExecutorFieldsToLegalStatementExecutorFields(
                caseDetailsMock.getData());

        CaseData caseData = caseDetailsMock.getData();
        assertEquals(additionalExecutorNotApplying, caseData.getExecutorsNotApplyingLegalStatement());
        assertEquals(new ArrayList<>(), caseData.getExecutorsApplyingLegalStatement());
    }

    @Test
    public void shouldFormatCaseDataForLegalStatement() {
        List<CollectionMember<CodicilAddedDate>> codicilAddedDate = new ArrayList<>();
        codicilAddedDate.add(new CollectionMember<>(CodicilAddedDate.builder().dateCodicilAdded(DATE).build()));

        caseDataBuilder
                .dispenseWithNoticeLeaveGivenDate(DATE)
                .codicilAddedDateList(codicilAddedDate)
                .deceasedForenames(DECEASED_FORENAME)
                .deceasedSurname(DECEASED_SURNAME)
                .solsSolicitorFirmName(SOLICITOR_FIRM_NAME);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(dateFormatterServiceMock.formatDate(
                DATE)).thenReturn(DATE_FORMATTED);

        legalStatementExecutorTransformerMock.formatFields(
                caseDetailsMock.getData());

        List<CollectionMember<String>> formattedCodicilDateList = new ArrayList<>();
        formattedCodicilDateList.add(new CollectionMember<>(DATE_FORMATTED));

        CaseData caseData = caseDetailsMock.getData();
        assertEquals(DECEASED_FORENAME_FORMATTED, caseData.getDeceasedForenames());
        assertEquals(DECEASED_SURNAME_FORMATTED, caseData.getDeceasedSurname());
        assertEquals(SOLICITOR_FIRM_NAME, caseData.getSolsSolicitorFirmName());
        assertEquals(DATE_FORMATTED, caseData.getDispenseWithNoticeLeaveGivenDateFormatted());
        assertEquals(formattedCodicilDateList, caseData.getCodicilAddedFormattedDateList());
    }

    @Test
    public void shouldOutputCorrectSingularWhoSharesInProfitText() {
        final List<String> companyProfits = new ArrayList<>(Arrays.asList("Partners", "Shareholders"));

        final CaseData caseData = CaseData.builder()
                .whoSharesInCompanyProfits(companyProfits)
                .build();

        legalStatementExecutorTransformerMock.formatFields(caseData);

        assertEquals("partner and shareholder", caseData.getSingularProfitSharingTextForLegalStatement());
    }

    @Test
    public void shouldOutputCorrectPluralWhoSharesInProfitText() {
        final List<String> companyProfits = new ArrayList<>(Arrays.asList("Partners", "Shareholders"));

        final CaseData caseData = CaseData.builder()
                .whoSharesInCompanyProfits(companyProfits)
                .build();

        legalStatementExecutorTransformerMock.formatFields(caseData);

        assertEquals("partners and shareholders", caseData.getPluralProfitSharingTextForLegalStatement());
    }

    @Test
    public void shouldOutputCorrectSingularWhoSharesInProfitText_SingularValue() {
        final List<String> companyProfits = new ArrayList<>(Arrays.asList("Partner", "Shareholder"));

        final CaseData caseData = CaseData.builder()
                .whoSharesInCompanyProfits(companyProfits)
                .build();

        legalStatementExecutorTransformerMock.formatFields(caseData);

        assertEquals("partner and shareholder", caseData.getSingularProfitSharingTextForLegalStatement());
    }

    @Test
    public void shouldOutputCorrectPluralWhoSharesInProfitText_SingularValue() {
        final List<String> companyProfits = new ArrayList<>(Arrays.asList("Partner", "Shareholder"));

        final CaseData caseData = CaseData.builder()
                .whoSharesInCompanyProfits(companyProfits)
                .build();

        legalStatementExecutorTransformerMock.formatFields(caseData);

        assertEquals("partners and shareholders", caseData.getPluralProfitSharingTextForLegalStatement());
    }
}