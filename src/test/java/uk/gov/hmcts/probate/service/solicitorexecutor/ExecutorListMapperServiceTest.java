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
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.ADDITIONAL_EXECUTOR_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.ADDITIONAL_EXECUTOR_NOT_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.EXECUTOR_NOT_APPLYING_REASON;
import static uk.gov.hmcts.probate.util.CommonVariables.EXECUTOR_TYPE_PROFESSIONAL;
import static uk.gov.hmcts.probate.util.CommonVariables.EXECUTOR_TYPE_TRUST_CORP;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_ADDRESS;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_FIRST_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_ID;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_OTHER_NAMES;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_OTHER_NAMES_REASON;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_TRUST_CORP_POS;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_WILL_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.PARTNER_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.POWER_RESERVED;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_ADDRESS;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_ID;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_NOT_APPLYING_REASON;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_SOT_FORENAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_SOT_FULLNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_SOT_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLS_EXEC_ADDITIONAL_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLS_EXEC_NOT_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.TRUST_CORP_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.EXECUTOR_TYPE_NAMED;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorListMapperServiceTest {

    @InjectMocks
    private ExecutorListMapperService underTest;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private CallbackRequest callbackRequestMock;

    @Mock
    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplyingMock;

    @Mock
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplyingMock;

    @Before
    public void setup() {
        initMocks(this);
        additionalExecutorsApplyingMock = new ArrayList<>();
        additionalExecutorsApplyingMock.add(new CollectionMember<>(EXEC_ID, ADDITIONAL_EXECUTOR_APPLYING));
        additionalExecutorsApplyingMock.add(new CollectionMember<>(SOLICITOR_ID, ADDITIONAL_EXECUTOR_APPLYING));

        additionalExecutorsNotApplyingMock = new ArrayList<>();
        additionalExecutorsNotApplyingMock.add(new CollectionMember<>(EXEC_ID, ADDITIONAL_EXECUTOR_NOT_APPLYING));
        additionalExecutorsNotApplyingMock.add(new CollectionMember<>(SOLICITOR_ID, ADDITIONAL_EXECUTOR_NOT_APPLYING));

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder()
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorAddress(SOLICITOR_ADDRESS)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .primaryApplicantForenames(EXEC_FIRST_NAME)
                .primaryApplicantSurname(EXEC_SURNAME)
                .primaryApplicantAddress(EXEC_ADDRESS)
                .solsExecutorAliasNames(EXEC_OTHER_NAMES)
                .primaryApplicantAliasReason(EXEC_OTHER_NAMES_REASON);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
    }

    @Test
    public void shouldUpdateSolNotApplyingExec() {
        List<CollectionMember<AdditionalExecutorNotApplying>> newExecsNotApplying;
        newExecsNotApplying = underTest
                .addSolicitorToNotApplyingList(callbackRequestMock.getCaseDetails().getData(),
                        additionalExecutorsNotApplyingMock);

        assertEquals(2, newExecsNotApplying.size());
        assertEquals(SOLICITOR_SOT_FULLNAME, newExecsNotApplying.get(1).getValue().getNotApplyingExecutorName());
        assertEquals(SOLICITOR_ID, newExecsNotApplying.get(1).getId());
    }

    @Test
    public void shouldAddSolNotApplyingExec() {
        additionalExecutorsNotApplyingMock.remove(1);
        assertEquals(1, additionalExecutorsNotApplyingMock.size());
        assertEquals(EXEC_ID, additionalExecutorsNotApplyingMock.get(0).getId());

        List<CollectionMember<AdditionalExecutorNotApplying>> newExecsNotApplying;
        newExecsNotApplying = underTest
                .addSolicitorToNotApplyingList(callbackRequestMock.getCaseDetails().getData(),
                        additionalExecutorsNotApplyingMock);

        assertEquals(2, newExecsNotApplying.size());
        assertEquals(SOLICITOR_ID, newExecsNotApplying.get(1).getId());
    }

    @Test
    public void shouldMapAdditionalExecutorsApplyingList() {
        CaseData caseData = CaseData.builder().additionalExecutorsApplying(additionalExecutorsApplyingMock).build();
        List<CollectionMember<AdditionalExecutorApplying>> result = underTest
                .mapAdditionalApplyingExecutors(caseData);

        assertEquals(EXEC_NAME, result.get(0).getValue().getApplyingExecutorName());
        assertEquals(EXEC_NAME, result.get(1).getValue().getApplyingExecutorName());
        assertEquals(2, result.size());
    }


    @Test
    public void shouldRemoveSolApplyingExec() {
        additionalExecutorsApplyingMock.remove(1);
        List<CollectionMember<AdditionalExecutorApplying>> newExecsApplying;
        newExecsApplying = underTest.removeSolicitorFromApplyingList(additionalExecutorsApplyingMock);

        assertEquals(1, newExecsApplying.size());
        assertEquals(EXEC_FIRST_NAME, newExecsApplying.get(0).getValue().getApplyingExecutorFirstName());
        assertEquals(EXEC_SURNAME, newExecsApplying.get(0).getValue().getApplyingExecutorLastName());
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

    @Test
    public void shouldMapFromTrustCorpExecsToApplyingExecs() {
        List<CollectionMember<AdditionalExecutorTrustCorps>> trustCorpsExecutorList = new ArrayList<>();
        trustCorpsExecutorList.add(TRUST_CORP_EXEC);
        CaseData caseData = CaseData.builder()
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .trustCorpAddress(EXEC_ADDRESS)
                .build();

        List<CollectionMember<AdditionalExecutorApplying>> result =
                underTest.mapFromTrustCorpExecutorsToApplyingExecutors(caseData);
        AdditionalExecutorApplying expected = AdditionalExecutorApplying.builder()
                .applyingExecutorFirstName(EXEC_FIRST_NAME)
                .applyingExecutorLastName(EXEC_SURNAME)
                .applyingExecutorName(EXEC_NAME)
                .applyingExecutorType(EXECUTOR_TYPE_TRUST_CORP)
                .applyingExecutorTrustCorpPosition(EXEC_TRUST_CORP_POS)
                .applyingExecutorAddress(EXEC_ADDRESS)
                .build();

        assertEquals(expected, result.get(0).getValue());
        assertEquals(EXEC_ID, result.get(0).getId());
        assertEquals(1, result.size());
    }

    @Test
    public void shouldMapFromPartnerExecsToApplyingExecs() {
        List<CollectionMember<AdditionalExecutorPartners>> partnerExecutorList = new ArrayList<>();
        partnerExecutorList.add(PARTNER_EXEC);
        CaseData caseData = CaseData.builder().otherPartnersApplyingAsExecutors(partnerExecutorList).build();

        List<CollectionMember<AdditionalExecutorApplying>> result =
                underTest.mapFromPartnerExecutorsToApplyingExecutors(caseData);
        AdditionalExecutorApplying expected = AdditionalExecutorApplying.builder()
                .applyingExecutorAddress(EXEC_ADDRESS)
                .applyingExecutorFirstName(EXEC_FIRST_NAME)
                .applyingExecutorLastName(EXEC_SURNAME)
                .applyingExecutorType(EXECUTOR_TYPE_PROFESSIONAL)
                .applyingExecutorName(EXEC_NAME)
                .build();

        assertEquals(expected, result.get(0).getValue());
        assertEquals(EXEC_ID, result.get(0).getId());
        assertEquals(1, result.size());
    }

    @Test
    public void shouldMapFromDispenseWithNoticeExecutorsToNotApplyingExecutors() {
        List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeExecs = new ArrayList<>();
        dispenseWithNoticeExecs.add(DISPENSE_WITH_NOTICE_EXEC);
        CaseData caseData = CaseData.builder().dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecs).build();

        List<CollectionMember<AdditionalExecutorNotApplying>> result =
                underTest.mapFromDispenseWithNoticeExecsToNotApplyingExecutors(caseData);
        AdditionalExecutorNotApplying expected = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(EXEC_NAME)
                .notApplyingExecutorReason(POWER_RESERVED)
                .build();

        assertEquals(expected, result.get(0).getValue());
        assertEquals(EXEC_ID, result.get(0).getId());
        assertEquals(1, result.size());
    }

    @Test
    public void shouldMapFromSolsAdditionalExecToApplyingExecutors() {
        List<CollectionMember<AdditionalExecutor>> solsAdditionalExecs = new ArrayList<>();
        solsAdditionalExecs.add(SOLS_EXEC_ADDITIONAL_APPLYING);
        CaseData caseData = CaseData.builder().solsAdditionalExecutorList(solsAdditionalExecs).build();

        List<CollectionMember<AdditionalExecutorApplying>> result =
                underTest.mapFromSolsAdditionalExecutorListToApplyingExecutors(caseData);
        AdditionalExecutorApplying expected = AdditionalExecutorApplying.builder()
                .applyingExecutorAddress(EXEC_ADDRESS)
                .applyingExecutorFirstName(EXEC_FIRST_NAME)
                .applyingExecutorLastName(EXEC_SURNAME)
                .applyingExecutorName(EXEC_NAME)
                .applyingExecutorType(EXECUTOR_TYPE_NAMED)
                .applyingExecutorOtherNames(EXEC_WILL_NAME)
                .build();

        assertEquals(expected, result.get(0).getValue());
        assertEquals(EXEC_ID, result.get(0).getId());
        assertEquals(1, result.size());
    }

    @Test
    public void shouldMapFromSolsAdditionalExecToNotApplyingExecutors() {
        List<CollectionMember<AdditionalExecutor>> solsAdditionalExecs = new ArrayList<>();
        solsAdditionalExecs.add(SOLS_EXEC_NOT_APPLYING);
        CaseData caseData = CaseData.builder().solsAdditionalExecutorList(solsAdditionalExecs).build();

        List<CollectionMember<AdditionalExecutorNotApplying>> result =
                underTest.mapFromSolsAdditionalExecsToNotApplyingExecutors(caseData);
        AdditionalExecutorNotApplying expected = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(EXEC_NAME)
                .notApplyingExecutorReason(EXECUTOR_NOT_APPLYING_REASON)
                .notApplyingExecutorNameOnWill(EXEC_WILL_NAME)
                .build();

        assertEquals(expected, result.get(0).getValue());
        assertEquals(EXEC_ID, result.get(0).getId());
        assertEquals(1, result.size());
    }

    @Test
    public void shouldMapFromFromSolicitorToApplyingExecutor() {
        CollectionMember<AdditionalExecutorApplying> result =
                underTest.mapFromSolicitorToApplyingExecutor(caseDetailsMock.getData());
        CollectionMember<AdditionalExecutorApplying> expected = new CollectionMember(
                SOLICITOR_ID, AdditionalExecutorApplying.builder()
                .applyingExecutorFirstName(SOLICITOR_SOT_FORENAME)
                .applyingExecutorLastName(SOLICITOR_SOT_SURNAME)
                .applyingExecutorName(SOLICITOR_SOT_FULLNAME)
                .applyingExecutorType(EXECUTOR_TYPE_NAMED)
                .applyingExecutorAddress(SOLICITOR_ADDRESS)
                .build());

        assertEquals(expected.getValue(), result.getValue());
    }

    @Test
    public void shouldMapFromPrimaryApplicantToApplyingExecutor() {
        CollectionMember<AdditionalExecutorApplying> result =
                underTest.mapFromPrimaryApplicantToApplyingExecutor(caseDetailsMock.getData());
        CollectionMember<AdditionalExecutorApplying> expected = new CollectionMember(
                null, AdditionalExecutorApplying.builder()
                .applyingExecutorFirstName(EXEC_FIRST_NAME)
                .applyingExecutorLastName(EXEC_SURNAME)
                .applyingExecutorName(EXEC_NAME)
                .applyingExecutorType(EXECUTOR_TYPE_NAMED)
                .applyingExecutorAddress(EXEC_ADDRESS)
                .applyingExecutorOtherNames(EXEC_OTHER_NAMES)
                .applyingExecutorOtherNamesReason(EXEC_OTHER_NAMES_REASON)
                .build());

        assertEquals(result.getValue(), expected.getValue());
    }

    @Test
    public void shouldMapFromPrimaryApplicantToNotApplyingExecutor() {
        CollectionMember<AdditionalExecutorNotApplying> result =
                underTest.mapFromPrimaryApplicantToNotApplyingExecutor(caseDetailsMock.getData());
        CollectionMember<AdditionalExecutorNotApplying> expected = new CollectionMember(
                null, AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(EXEC_NAME)
                .notApplyingExecutorNameOnWill(EXEC_OTHER_NAMES)
                .build());

        assertEquals(result.getValue(), expected.getValue());
    }


}