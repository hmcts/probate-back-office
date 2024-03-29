package uk.gov.hmcts.probate.service.solicitorexecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.util.CommonVariables.ADDITIONAL_EXECUTOR_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.ADDITIONAL_EXECUTOR_NOT_APPLYING;
import static uk.gov.hmcts.probate.util.CommonVariables.DIRECTOR;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.EXECUTOR_NOT_APPLYING_REASON;
import static uk.gov.hmcts.probate.util.CommonVariables.EXECUTOR_TYPE_NAMED;
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

class ExecutorListMapperServiceTest {

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

    @BeforeEach
    public void setup() {
        openMocks(this);
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
    void shouldUpdateSolNotApplyingExec() {
        List<CollectionMember<AdditionalExecutorNotApplying>> newExecsNotApplying;
        newExecsNotApplying = underTest
                .addSolicitorToNotApplyingList(callbackRequestMock.getCaseDetails().getData(),
                        additionalExecutorsNotApplyingMock);

        assertEquals(2, newExecsNotApplying.size());
        assertEquals(SOLICITOR_SOT_FULLNAME, newExecsNotApplying.get(0).getValue().getNotApplyingExecutorName());
        assertEquals(SOLICITOR_ID, newExecsNotApplying.get(0).getId());
    }

    @Test
    void shouldAddSolNotApplyingExec() {
        additionalExecutorsNotApplyingMock.remove(1);
        assertEquals(1, additionalExecutorsNotApplyingMock.size());
        assertEquals(EXEC_ID, additionalExecutorsNotApplyingMock.get(0).getId());

        List<CollectionMember<AdditionalExecutorNotApplying>> newExecsNotApplying;
        newExecsNotApplying = underTest
                .addSolicitorToNotApplyingList(callbackRequestMock.getCaseDetails().getData(),
                        additionalExecutorsNotApplyingMock);

        assertEquals(2, newExecsNotApplying.size());
        assertEquals(SOLICITOR_ID, newExecsNotApplying.get(0).getId());
    }

    @Test
    void shouldMapAdditionalExecutorsApplyingList() {
        CaseData caseData = CaseData.builder().additionalExecutorsApplying(additionalExecutorsApplyingMock).build();
        List<CollectionMember<AdditionalExecutorApplying>> result = underTest
                .mapAdditionalApplyingExecutors(caseData);

        assertEquals(EXEC_NAME, result.get(0).getValue().getApplyingExecutorName());
        assertEquals(EXEC_NAME, result.get(1).getValue().getApplyingExecutorName());
        assertEquals(2, result.size());
    }


    @Test
    void shouldRemoveSolApplyingExec() {
        additionalExecutorsApplyingMock.remove(1);
        List<CollectionMember<AdditionalExecutorApplying>> newExecsApplying;
        newExecsApplying = underTest.removeSolicitorFromApplyingList(additionalExecutorsApplyingMock);

        assertEquals(1, newExecsApplying.size());
        assertEquals(EXEC_FIRST_NAME, newExecsApplying.get(0).getValue().getApplyingExecutorFirstName());
        assertEquals(EXEC_SURNAME, newExecsApplying.get(0).getValue().getApplyingExecutorLastName());
        assertEquals(EXEC_ID, newExecsApplying.get(0).getId());
    }

    @Test
    void shouldRemoveSolNotApplyingExec() {
        additionalExecutorsNotApplyingMock.remove(1);
        List<CollectionMember<AdditionalExecutorNotApplying>> newExecsNotApplying;
        newExecsNotApplying = underTest.removeSolicitorFromNotApplyingList(additionalExecutorsNotApplyingMock);

        assertEquals(1, newExecsNotApplying.size());
        assertEquals(EXEC_NAME, newExecsNotApplying.get(0).getValue().getNotApplyingExecutorName());
        assertEquals(EXEC_ID, newExecsNotApplying.get(0).getId());
    }

    @Test
    void shouldMapFromTrustCorpExecsToApplyingExecs() {
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
    void shouldMapFromPartnerExecsToApplyingExecs() {
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
    void shouldMapFromDispenseWithNoticeExecutorsToNotApplyingExecutors() {
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
    void shouldMapFromSolsAdditionalExecToApplyingExecutors() {
        List<CollectionMember<AdditionalExecutor>> solsAdditionalExecs = new ArrayList<>();
        solsAdditionalExecs.add(SOLS_EXEC_ADDITIONAL_APPLYING);
        CollectionMember<AdditionalExecutor> emptyApplying = new CollectionMember(EXEC_ID,
                AdditionalExecutor.builder()
                        .additionalExecForenames(EXEC_FIRST_NAME)
                        .additionalExecLastname(EXEC_SURNAME)
                        .additionalExecAddress(EXEC_ADDRESS)
                        .additionalExecAliasNameOnWill(EXEC_WILL_NAME)
                        .build());
        CollectionMember<AdditionalExecutor> empty = new CollectionMember(EXEC_ID,
                AdditionalExecutor.builder()
                        .build());
        solsAdditionalExecs.add(emptyApplying);
        solsAdditionalExecs.add(empty);

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
        assertEquals(expected.getApplyingExecutorType(), result.get(0).getValue().getApplyingExecutorType());
        assertEquals(EXEC_ID, result.get(0).getId());
        assertEquals(1, result.size());
    }

    @Test
    void shouldMapFromSolsAdditionalExecToNotApplyingExecutors() {
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
    void shouldMapFromFromSolicitorToApplyingExecutor() {
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
    void shouldMapFromPrimaryApplicantToApplyingExecutor() {
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
    void shouldMapFromPrimaryApplicantToApplyingExecutorTrustCorpPosn() {
        CaseData cd = CaseData.builder()
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorAddress(SOLICITOR_ADDRESS)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .primaryApplicantForenames(EXEC_FIRST_NAME)
                .primaryApplicantSurname(EXEC_SURNAME)
                .primaryApplicantAddress(EXEC_ADDRESS)
                .solsExecutorAliasNames(EXEC_OTHER_NAMES)
                .primaryApplicantAliasReason(EXEC_OTHER_NAMES_REASON)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
                .solsSolicitorIsExec(NO)
                .solsSolicitorIsApplying(YES)
                .probatePractitionersPositionInTrust(DIRECTOR)
                .build();

        CollectionMember<AdditionalExecutorApplying> result =
                underTest.mapFromPrimaryApplicantToApplyingExecutor(cd);

        CollectionMember<AdditionalExecutorApplying> expected = new CollectionMember(
                null, AdditionalExecutorApplying.builder()
                .applyingExecutorFirstName(EXEC_FIRST_NAME)
                .applyingExecutorLastName(EXEC_SURNAME)
                .applyingExecutorName(EXEC_NAME)
                .applyingExecutorType(EXECUTOR_TYPE_TRUST_CORP)
                .applyingExecutorAddress(EXEC_ADDRESS)
                .applyingExecutorOtherNames(EXEC_OTHER_NAMES)
                .applyingExecutorOtherNamesReason(EXEC_OTHER_NAMES_REASON)
                .applyingExecutorTrustCorpPosition(DIRECTOR)
                .build());
        assertEquals(result.getValue(), expected.getValue());
    }

    @Test
    void shouldMapFromPrimaryApplicantToNotApplyingExecutor() {
        CollectionMember<AdditionalExecutorNotApplying> result =
                underTest.mapFromPrimaryApplicantToNotApplyingExecutor(caseDetailsMock.getData());
        CollectionMember<AdditionalExecutorNotApplying> expected = new CollectionMember(
                null, AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(EXEC_NAME)
                .notApplyingExecutorNameOnWill(EXEC_OTHER_NAMES)
                .build());

        assertEquals(result.getValue(), expected.getValue());
    }

    public void shouldMapFromSolicitorToApplyingExecutorTrustCorps() {
        CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder()
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorAddress(SOLICITOR_ADDRESS)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .primaryApplicantForenames(EXEC_FIRST_NAME)
                .primaryApplicantSurname(EXEC_SURNAME)
                .primaryApplicantAddress(EXEC_ADDRESS)
                .solsExecutorAliasNames(EXEC_OTHER_NAMES)
                .primaryApplicantAliasReason(EXEC_OTHER_NAMES_REASON)
                .probatePractitionersPositionInTrust(DIRECTOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
                .solsSolicitorIsExec(NO)
                .solsSolicitorIsApplying(YES);

        CollectionMember<AdditionalExecutorApplying> result =
                underTest.mapFromSolicitorToApplyingExecutor(caseDataBuilder.build());

        CollectionMember<AdditionalExecutorApplying> expected = new CollectionMember(
                SOLICITOR_ID, AdditionalExecutorApplying.builder()
                .applyingExecutorFirstName(SOLICITOR_SOT_FORENAME)
                .applyingExecutorLastName(SOLICITOR_SOT_SURNAME)
                .applyingExecutorName(SOLICITOR_SOT_FULLNAME)
                .applyingExecutorType(EXECUTOR_TYPE_NAMED)
                .applyingExecutorAddress(SOLICITOR_ADDRESS)
                .applyingExecutorTrustCorpPosition(DIRECTOR)
                .build());

        assertEquals(expected.getValue(), result.getValue());
    }

    @Test
    void shouldMapFromPrimaryApplicantToApplyingExecutorTrustCorps() {

        CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder()
                .solsSOTForenames(SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(SOLICITOR_SOT_SURNAME)
                .solsSolicitorAddress(SOLICITOR_ADDRESS)
                .solsSolicitorNotApplyingReason(SOLICITOR_NOT_APPLYING_REASON)
                .primaryApplicantForenames(EXEC_FIRST_NAME)
                .primaryApplicantSurname(EXEC_SURNAME)
                .primaryApplicantAddress(EXEC_ADDRESS)
                .solsExecutorAliasNames(EXEC_OTHER_NAMES)
                .primaryApplicantAliasReason(EXEC_OTHER_NAMES_REASON)
                .probatePractitionersPositionInTrust(DIRECTOR)
                .titleAndClearingType(TITLE_AND_CLEARING_TRUST_CORP_SDJ)
                .solsSolicitorIsExec(NO)
                .solsSolicitorIsApplying(YES);

        CollectionMember<AdditionalExecutorApplying> result =
                underTest.mapFromPrimaryApplicantToApplyingExecutor(caseDataBuilder.build());

        CollectionMember<AdditionalExecutorApplying> expected = new CollectionMember(
                null, AdditionalExecutorApplying.builder()
                .applyingExecutorFirstName(EXEC_FIRST_NAME)
                .applyingExecutorLastName(EXEC_SURNAME)
                .applyingExecutorName(EXEC_NAME)
                .applyingExecutorType(EXECUTOR_TYPE_TRUST_CORP)
                .applyingExecutorAddress(EXEC_ADDRESS)
                .applyingExecutorOtherNames(EXEC_OTHER_NAMES)
                .applyingExecutorOtherNamesReason(EXEC_OTHER_NAMES_REASON)
                .applyingExecutorTrustCorpPosition(DIRECTOR)
                .build());

        assertEquals(result.getValue(), expected.getValue());
    }
}
