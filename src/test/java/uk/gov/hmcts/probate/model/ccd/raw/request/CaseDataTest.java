package uk.gov.hmcts.probate.model.ccd.raw.request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class    CaseDataTest {

    private static final String PRIMARY_APPLICANT_FIRST_NAME = "fName";
    private static final String PRIMARY_APPLICANT_SURNAME = "sName";
    private static final SolsAddress PRIMARY_APPLICANT_ADDRESS = mock(SolsAddress.class);
    private static final String PRIMARY_APPLICANT_NAME_ON_WILL = "willName";
    private static final String DECEASED_FIRST_NAME = "Name";
    private static final String DECEASED_SURNAME = "Surname";
    private static final String NOT_APPLYING_REASON = "not applying reason";
    private static final LocalDate LOCAL_DATE = LocalDate.of(2000,01,01);
    private static final String WILL_TYPE_PROBATE = "WillLeft";

    @Mock
    private AdditionalExecutor additionalExecutor1Mock;
    @Mock
    private AdditionalExecutor additionalExecutor2Mock;
    @Mock
    private AdditionalExecutor additionalExecutor3Mock;

    @Mock
    private AdditionalExecutorApplying additionalExecutorApplying1Mock;
    @Mock
    private AdditionalExecutorApplying additionalExecutorApplying2Mock;
    @Mock
    private AdditionalExecutorApplying additionalExecutorApplying3Mock;

    @Mock
    private AdditionalExecutorNotApplying additionalExecutorNotApplying1Mock;
    @Mock
    private AdditionalExecutorNotApplying additionalExecutorNotApplying2Mock;
    @Mock
    private AdditionalExecutorNotApplying additionalExecutorNotApplying3Mock;

    @Mock
    private CollectionMember<AdditionalExecutor> additionalExecutors1Mock;
    @Mock
    private CollectionMember<AdditionalExecutor> additionalExecutors2Mock;
    @Mock
    private CollectionMember<AdditionalExecutor> additionalExecutors3Mock;

    @Mock
    private CollectionMember<AdditionalExecutorApplying> additionalExecutorsApplying1Mock;
    @Mock
    private CollectionMember<AdditionalExecutorApplying> additionalExecutorsApplying2Mock;
    @Mock
    private CollectionMember<AdditionalExecutorApplying> additionalExecutorsApplying3Mock;

    @Mock
    private CollectionMember<AdditionalExecutorNotApplying> additionalExecutorsNotApplying1Mock;
    @Mock
    private CollectionMember<AdditionalExecutorNotApplying> additionalExecutorsNotApplying2Mock;
    @Mock
    private CollectionMember<AdditionalExecutorNotApplying> additionalExecutorsNotApplying3Mock;

    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplyingList;

    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplyingList;

    private CaseData underTest;

    @Before
    public void setup() {

        initMocks(this);

        when(additionalExecutors1Mock.getValue()).thenReturn(additionalExecutor1Mock);
        when(additionalExecutors2Mock.getValue()).thenReturn(additionalExecutor2Mock);
        when(additionalExecutors3Mock.getValue()).thenReturn(additionalExecutor3Mock);

        when(additionalExecutorsApplying1Mock.getValue()).thenReturn(additionalExecutorApplying1Mock);
        when(additionalExecutorsApplying2Mock.getValue()).thenReturn(additionalExecutorApplying2Mock);
        when(additionalExecutorsApplying3Mock.getValue()).thenReturn(additionalExecutorApplying3Mock);

        when(additionalExecutorsNotApplying1Mock.getValue()).thenReturn(additionalExecutorNotApplying1Mock);
        when(additionalExecutorsNotApplying2Mock.getValue()).thenReturn(additionalExecutorNotApplying2Mock);
        when(additionalExecutorsNotApplying3Mock.getValue()).thenReturn(additionalExecutorNotApplying3Mock);

        List<CollectionMember<AdditionalExecutor>> additionalExecutorsList = new ArrayList<>();
        additionalExecutorsList.add(additionalExecutors1Mock);
        additionalExecutorsList.add(additionalExecutors2Mock);
        additionalExecutorsList.add(additionalExecutors3Mock);
        additionalExecutorsList.add(null);

        additionalExecutorsApplyingList = new ArrayList<>();
        additionalExecutorsApplyingList.add(additionalExecutorsApplying1Mock);
        additionalExecutorsApplyingList.add(additionalExecutorsApplying2Mock);
        additionalExecutorsApplyingList.add(additionalExecutorsApplying3Mock);

        additionalExecutorsNotApplyingList = new ArrayList<>();
        additionalExecutorsNotApplyingList.add(additionalExecutorsNotApplying1Mock);
        additionalExecutorsNotApplyingList.add(additionalExecutorsNotApplying2Mock);
        additionalExecutorsNotApplyingList.add(additionalExecutorsNotApplying3Mock);

        underTest = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(additionalExecutorsList)
                .otherExecutorExists(YES)
                .build();
    }

    @Test
    public void shouldMapExecsApplying() {
        when(additionalExecutorApplying1Mock.getApplyingExecutorName()).thenReturn("Applying Name");
        when(additionalExecutorApplying2Mock.getApplyingExecutorName()).thenReturn("Applying Name");
        when(additionalExecutorApplying3Mock.getApplyingExecutorName()).thenReturn("Applying Name");

        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(null)
                .additionalExecutorsApplying(additionalExecutorsApplyingList)
                .build();

        List<CollectionMember<AdditionalExecutor>> applying = caseData.getExecutorsApplyingForLegalStatement();

        assertEquals(4, applying.size());
    }

    @Test
    public void shouldNOTMapExecsApplying() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(null)
                .additionalExecutorsApplying(null)
                .build();

        List<CollectionMember<AdditionalExecutor>> applying = caseData.getExecutorsApplyingForLegalStatement();

        assertEquals(1, applying.size());
    }

    @Test
    public void shouldMapExecsNotApplying() {
        when(additionalExecutorNotApplying1Mock.getNotApplyingExecutorName()).thenReturn("NotApplying Name");
        when(additionalExecutorNotApplying2Mock.getNotApplyingExecutorName()).thenReturn("NotApplying Name");
        when(additionalExecutorNotApplying3Mock.getNotApplyingExecutorName()).thenReturn("NotApplying Name");

        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(NO)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(null)
                .additionalExecutorsNotApplying(additionalExecutorsNotApplyingList)
                .build();

        List<CollectionMember<AdditionalExecutor>> notApplying = caseData.getExecutorsNotApplyingForLegalStatement();

        assertEquals(4, notApplying.size());
    }

    @Test
    public void shouldNOTMapExecsNotApplying() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(NO)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(null)
                .additionalExecutorsNotApplying(null)
                .build();

        List<CollectionMember<AdditionalExecutor>> notApplying = caseData.getExecutorsNotApplyingForLegalStatement();

        assertEquals(1, notApplying.size());
    }

    @Test
    public void shouldNOTMapExecsApplyingWhenSolicitorCreatedGrant() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(WILL_TYPE_PROBATE)
                .additionalExecutorsApplying(additionalExecutorsApplyingList)
                .additionalExecutorsNotApplying(additionalExecutorsNotApplyingList)
                .build();

        List<CollectionMember<AdditionalExecutor>> execs = caseData.getExecutorsApplyingForLegalStatement();

        assertEquals(1, execs.size());
    }

    @Test
    public void shouldNOTMapExecsNotApplyingWhenSolicitorCreatedGrant() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(NO)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(WILL_TYPE_PROBATE)
                .additionalExecutorsApplying(additionalExecutorsApplyingList)
                .additionalExecutorsNotApplying(additionalExecutorsNotApplyingList)
                .build();

        List<CollectionMember<AdditionalExecutor>> execs = caseData.getExecutorsNotApplyingForLegalStatement();

        assertEquals(1, execs.size());
    }

    @Test
    public void shouldGetExecutorsApplying() {
        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor3Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);

        List<CollectionMember<AdditionalExecutor>> applying = underTest.getExecutorsApplyingForLegalStatement();

        assertEquals(3, applying.size());
    }


    @Test
    public void shouldGetExecutorsNotApplying() {
        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor2Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor3Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);

        List<CollectionMember<AdditionalExecutor>> notApplying = underTest.getExecutorsNotApplyingForLegalStatement();

        assertEquals(2, notApplying.size());
    }

    @Test
    public void shouldGetExecutorsCombinationsOfNulls() {
        when(additionalExecutors1Mock.getValue()).thenReturn(null);
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn(null);
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");
        when(additionalExecutor3Mock.getAdditionalExecReasonNotApplying()).thenReturn(NOT_APPLYING_REASON);

        List<CollectionMember<AdditionalExecutor>> notApplying = underTest.getExecutorsNotApplyingForLegalStatement();

        assertEquals(1, notApplying.size());
    }

    @Test
    public void shouldAdditionalExecutorsApplyingWhenPrimaryExecutorIsNotApplying() {
        final CaseData caseData = getCaseDataWhenPrimaryExecutorNotApplying();

        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");

        assertThat(caseData.getExecutorsApplyingForLegalStatement(), hasSize(2));
    }

    @Test
    public void shouldAdditionalExecutorsNotApplyingWhenPrimaryExecutorIsNotApplying() {
        final CaseData caseData = getCaseDataWhenPrimaryExecutorNotApplying();

        when(additionalExecutor1Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor2Mock.getAdditionalApplying()).thenReturn("Yes");
        when(additionalExecutor3Mock.getAdditionalApplying()).thenReturn("No");

        assertThat(caseData.getExecutorsNotApplyingForLegalStatement(), hasSize(2));
    }

    private CaseData getCaseDataWhenPrimaryExecutorNotApplying() {
        List<CollectionMember<AdditionalExecutor>> additionalExecutorsList = new ArrayList<>();
        additionalExecutorsList.add(additionalExecutors1Mock);
        additionalExecutorsList.add(additionalExecutors2Mock);
        additionalExecutorsList.add(additionalExecutors3Mock);
        additionalExecutorsList.add(null);
        return CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantForenames(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(NO)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(additionalExecutorsList)
                .otherExecutorExists(YES)
                .build();
    }

    @Test
    public void shouldReturnPrimaryApplicantFullName() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .build();

        assertEquals(PRIMARY_APPLICANT_FIRST_NAME + " " + PRIMARY_APPLICANT_SURNAME,
                caseData.getPrimaryApplicantFullName());
    }

    @Test
    public void shouldReturnDeceasedFullName() {
        final CaseData caseData = CaseData.builder()
                .deceasedForenames(DECEASED_FIRST_NAME)
                .deceasedSurname(DECEASED_SURNAME)
                .build();

        assertEquals(DECEASED_FIRST_NAME + " " + DECEASED_SURNAME, caseData.getDeceasedFullName());
    }

    @Test
    public void shouldSplitApplyingExecutorNameWhenDoubleBarrelledNames() {
        when(additionalExecutorApplying1Mock.getApplyingExecutorName()).thenReturn("Appl-ying Name");
        when(additionalExecutorApplying2Mock.getApplyingExecutorName()).thenReturn("Applying Na-me");
        when(additionalExecutorApplying3Mock.getApplyingExecutorName()).thenReturn("Appl-ying Na-me");

        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(null)
                .additionalExecutorsApplying(additionalExecutorsApplyingList)
                .build();

        List<CollectionMember<AdditionalExecutor>> applying = caseData.getExecutorsApplyingForLegalStatement();

        assertEquals(4, applying.size());
        assertEquals("Appl-ying", applying.get(1).getValue().getAdditionalExecForenames());
        assertEquals("Name", applying.get(1).getValue().getAdditionalExecLastname());
        assertEquals("Applying", applying.get(2).getValue().getAdditionalExecForenames());
        assertEquals("Na-me", applying.get(2).getValue().getAdditionalExecLastname());
        assertEquals("Appl-ying", applying.get(3).getValue().getAdditionalExecForenames());
        assertEquals("Na-me", applying.get(3).getValue().getAdditionalExecLastname());
    }

    @Test
    public void shouldSplitApplyingExecutorNameWhenSingleName() {
        when(additionalExecutorApplying1Mock.getApplyingExecutorName()).thenReturn("ApplyingName");
        when(additionalExecutorsApplying2Mock.getValue()).thenReturn(null);
        when(additionalExecutorsApplying3Mock.getValue()).thenReturn(null);

        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(YES)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(null)
                .additionalExecutorsApplying(additionalExecutorsApplyingList)
                .build();

        List<CollectionMember<AdditionalExecutor>> applying = caseData.getExecutorsApplyingForLegalStatement();

        assertEquals(2, applying.size());
        assertEquals("ApplyingName", applying.get(1).getValue().getAdditionalExecForenames());
        assertEquals(null, applying.get(1).getValue().getAdditionalExecLastname());
    }

    @Test
    public void shouldSplitNotApplyingExecutorNameWhenDoubleBarrelledNames() {
        when(additionalExecutorNotApplying1Mock.getNotApplyingExecutorName()).thenReturn("NotAppl-ying Name");
        when(additionalExecutorNotApplying2Mock.getNotApplyingExecutorName()).thenReturn("NotApplying Na-me");
        when(additionalExecutorNotApplying3Mock.getNotApplyingExecutorName()).thenReturn("NotAppl-ying Na-me");

        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(NO)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(null)
                .additionalExecutorsNotApplying(additionalExecutorsNotApplyingList)
                .build();

        List<CollectionMember<AdditionalExecutor>> notApplying = caseData.getExecutorsNotApplyingForLegalStatement();

        assertEquals(4, notApplying.size());
        assertEquals("NotAppl-ying", notApplying.get(1).getValue().getAdditionalExecForenames());
        assertEquals("Name", notApplying.get(1).getValue().getAdditionalExecLastname());
        assertEquals("NotApplying", notApplying.get(2).getValue().getAdditionalExecForenames());
        assertEquals("Na-me", notApplying.get(2).getValue().getAdditionalExecLastname());
        assertEquals("NotAppl-ying", notApplying.get(3).getValue().getAdditionalExecForenames());
        assertEquals("Na-me", notApplying.get(3).getValue().getAdditionalExecLastname());
    }

    @Test
    public void shouldSplitNotApplyingExecutorNameWhenSingleName() {
        when(additionalExecutorNotApplying1Mock.getNotApplyingExecutorName()).thenReturn("NotApplyingName");
        when(additionalExecutorsNotApplying2Mock.getValue()).thenReturn(null);
        when(additionalExecutorsNotApplying3Mock.getValue()).thenReturn(null);

        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(NO)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(null)
                .additionalExecutorsNotApplying(additionalExecutorsNotApplyingList)
                .build();

        List<CollectionMember<AdditionalExecutor>> notApplying = caseData.getExecutorsNotApplyingForLegalStatement();

        assertEquals(2, notApplying.size());
        assertEquals("NotApplyingName", notApplying.get(1).getValue().getAdditionalExecForenames());
        assertEquals(null, notApplying.get(1).getValue().getAdditionalExecLastname());
    }

    @Test
    public void shouldReturnDODFormattedWithST() {
        final CaseData caseData = CaseData.builder()
                .deceasedDateOfDeath(LOCAL_DATE)
                .build();

        assertEquals("1st January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    public void shouldReturnDODFormattedWithSTPublic() {
        CaseData caseData = CaseData.builder().build();
        assertEquals("1st January 2000", caseData.convertDate(LOCAL_DATE));
    }

    @Test
    public void shouldReturnDODFormattedWithND() {
        final CaseData caseData = CaseData.builder()
                .deceasedDateOfDeath(LocalDate.of(2000,01,02))
                .build();

        assertEquals("2nd January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    public void shouldReturnDODFormattedWithRD() {
        final CaseData caseData = CaseData.builder()
                .deceasedDateOfDeath(LocalDate.of(2000,01,03))
                .build();

        assertEquals("3rd January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    public void shouldReturnDODFormattedWithTH() {
        final CaseData caseData = CaseData.builder()
                .deceasedDateOfDeath(LocalDate.of(2000,01,04))
                .build();

        assertEquals("4th January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    public void shouldThrowParseException() {
        final CaseData caseData = CaseData.builder()
                .deceasedDateOfDeath(LocalDate.of(300000,01,04))
                .build();

        assertEquals(null, caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    public void isBoEmailRequestInfoNotificationRequestedTrue() {
        final CaseData caseData = CaseData.builder()
                .boEmailRequestInfoNotification(YES)
                .build();

        assertEquals(true, caseData.isBoEmailRequestInfoNotificationRequested());
    }

    @Test
    public void isBoEmailRequestInfoNotificationRequestedFromDefaultTrue() {
        final CaseData caseData = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantEmailAddress("test@test.com")
                .build();

        assertEquals(true, caseData.isBoEmailRequestInfoNotificationRequested());
    }

    @Test
    public void isBoEmailRequestInfoNotificationRequestedFalse() {
        final CaseData caseData = CaseData.builder()
                .boEmailRequestInfoNotification(NO)
                .build();

        assertEquals(false, caseData.isBoEmailRequestInfoNotificationRequested());
    }

    @Test
    public void isBoEmailRequestInfoNotificationRequestedFromDefaultFalse() {
        final CaseData caseData = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantEmailAddress(null)
                .build();

        assertEquals(false, caseData.isBoEmailRequestInfoNotificationRequested());
    }

    @Test
    public void isBoEmailDocsReceivedNotificationTrue() {
        final CaseData caseData = CaseData.builder()
                .boEmailDocsReceivedNotification(YES)
                .build();

        assertEquals(true, caseData.isDocsReceivedEmailNotificationRequested());
    }

    @Test
    public void isBoEmailDocsReceivedNotificationFromDefaultTrue() {
        final CaseData caseData = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantEmailAddress("test@test.com")
                .build();

        assertEquals(true, caseData.isDocsReceivedEmailNotificationRequested());
    }

    @Test
    public void isBoEmailDocsReceivedNotificationFalse() {
        final CaseData caseData = CaseData.builder()
                .boEmailDocsReceivedNotification(NO)
                .build();

        assertEquals(false, caseData.isDocsReceivedEmailNotificationRequested());
    }

    @Test
    public void isBoEmailDocsReceivedNotificationFromDefaultFalse() {
        final CaseData caseData = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantEmailAddress(null)
                .build();

        assertEquals(false, caseData.isDocsReceivedEmailNotificationRequested());
    }

    @Test
    public void isBoEmailGrantIssuedNotificationTrue() {
        final CaseData caseData = CaseData.builder()
                .boEmailGrantIssuedNotification(YES)
                .build();

        assertEquals(true, caseData.isGrantIssuedEmailNotificationRequested());
    }

    @Test
    public void isBoEmailGrantIssuedNotificationFromDefaultTrue() {
        final CaseData caseData = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantEmailAddress("test@test.com")
                .build();

        assertEquals(true, caseData.isGrantIssuedEmailNotificationRequested());
    }

    @Test
    public void isBoEmailGrantIssuedNotificationFalse() {
        final CaseData caseData = CaseData.builder()
                .boEmailGrantIssuedNotification(NO)
                .build();

        assertEquals(false, caseData.isGrantIssuedEmailNotificationRequested());
    }

    @Test
    public void isBoEmailGrantIssuedNotificationFromDefaultFalse() {
        final CaseData caseData = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantEmailAddress(null)
                .build();

        assertEquals(false, caseData.isGrantIssuedEmailNotificationRequested());
    }

    @Test
    public void isBoEmailGrantReissuedNotificationTrue() {
        final CaseData caseData = CaseData.builder()
                .boEmailGrantReissuedNotification(YES)
                .build();

        assertEquals(true, caseData.isGrantReissuedEmailNotificationRequested());
    }

    @Test
    public void isBoEmailGrantReissuedNotificationWithDefaultTrue() {
        final CaseData caseData = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantEmailAddress("test@test.com")
                .build();

        assertEquals(true, caseData.isGrantReissuedEmailNotificationRequested());
    }

    @Test
    public void isBoEmailGrantReissuedNotificationFalse() {
        final CaseData caseData = CaseData.builder()
                .boEmailGrantIssuedNotification(NO)
                .build();

        assertEquals(false, caseData.isGrantReissuedEmailNotificationRequested());
    }

    @Test
    public void isBoEmailGrantReissuedNotificationWithDefaultFalse() {
        final CaseData caseData = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantEmailAddress(null)
                .build();

        assertEquals(false, caseData.isGrantReissuedEmailNotificationRequested());
    }

    @Test
    public void isBoCaveatStopEmailNotificationTrue() {
        final CaseData caseData = CaseData.builder()
                .boCaveatStopEmailNotification(YES)
                .build();

        assertEquals(true, caseData.isCaveatStopEmailNotificationRequested());
    }

    @Test
    public void isBoCaveatStopEmailNotificationWithDefaultTrue() {
        final CaseData caseData = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantEmailAddress("test@test.com")
                .build();

        assertEquals(true, caseData.isCaveatStopEmailNotificationRequested());
    }

    @Test
    public void isBoCaveatStopEmailNotificationFalse() {
        final CaseData caseData = CaseData.builder()
                .boCaveatStopEmailNotification(NO)
                .build();

        assertEquals(false, caseData.isCaveatStopEmailNotificationRequested());
    }

    @Test
    public void isBoCaveatStopEmailNotificationWithDefaultFalse() {
        final CaseData caseData = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantEmailAddress(null)
                .build();

        assertEquals(false, caseData.isCaveatStopEmailNotificationRequested());
    }

    @Test
    public void probateDocumentsGeneratedDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(new ArrayList<>(), caseData.getProbateDocumentsGenerated());
    }

    @Test
    public void probateDocumentsGeneratedIgnoresDefault() {
        List<CollectionMember<Document>> probateDocuments = new ArrayList<>();
        CollectionMember<Document> probateDocument =
                new CollectionMember<>(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE)
                .documentLink(DocumentLink.builder().documentFilename("legalStatementProbate.pdf").build())
                .build());
        probateDocuments.add(probateDocument);

        final CaseData caseData = CaseData.builder()
                .probateDocumentsGenerated(probateDocuments)
                .build();

        assertEquals(probateDocuments, caseData.getProbateDocumentsGenerated());
    }

    @Test
    public void probateNotificationsGeneratedDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(new ArrayList<>(), caseData.getProbateNotificationsGenerated());
    }

    @Test
    public void probateNotificationsGeneratedIgnoresDefault() {
        List<CollectionMember<Document>> probateNotifications = new ArrayList<>();
        CollectionMember<Document> probateNotification =
                new CollectionMember<>(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE)
                .documentLink(DocumentLink.builder().documentFilename("legalStatementProbate.pdf").build())
                .build());
        probateNotifications.add(probateNotification);

        final CaseData caseData = CaseData.builder()
                .probateNotificationsGenerated(probateNotifications)
                .build();

        assertEquals(probateNotifications, caseData.getProbateNotificationsGenerated());
    }

    @Test
    public void probateSotDocumentsGeneratedDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(new ArrayList<>(), caseData.getProbateSotDocumentsGenerated());
    }

    @Test
    public void probateSotDocumentsGeneratedIgnoresDefault() {
        List<CollectionMember<Document>> probateSOTDocuments = new ArrayList<>();
        CollectionMember<Document> probateSOTDocument =
                new CollectionMember<>(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE)
                        .documentLink(DocumentLink.builder().documentFilename("legalStatementProbate.pdf").build())
                        .build());
        probateSOTDocuments.add(probateSOTDocument);

        final CaseData caseData = CaseData.builder()
                .probateSotDocumentsGenerated(probateSOTDocuments)
                .build();

        assertEquals(probateSOTDocuments, caseData.getProbateSotDocumentsGenerated());
    }

    @Test
    public void caseMatchesDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(new ArrayList<>(), caseData.getCaseMatches());
    }

    @Test
    public void caseMatchesIgnoresDefault() {
        List<CollectionMember<CaseMatch>> caseMatches = new ArrayList<>();
        CollectionMember<CaseMatch> caseMatch = new CollectionMember<>(CaseMatch.builder().fullName("Name One").build());
        caseMatches.add(caseMatch);

        final CaseData caseData = CaseData.builder()
                .caseMatches(caseMatches)
                .build();

        assertEquals(caseMatches, caseData.getCaseMatches());
    }

    @Test
    public void boCaveatStopSendToBulkPrintDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(YES, caseData.getBoCaveatStopSendToBulkPrint());
    }

    @Test
    public void boCaveatStopSendToBulkPrintIgnoresDefault() {
        final CaseData caseData = CaseData.builder()
                .boCaveatStopSendToBulkPrint(NO)
                .build();

        assertEquals(NO, caseData.getBoCaveatStopSendToBulkPrint());
    }

    @Test
    public void boGrantReissueSendToBulkPrintDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(YES, caseData.getBoGrantReissueSendToBulkPrint());
    }

    @Test
    public void boGrantReissueSendToBulkPrintIgnoresDefault() {
        final CaseData caseData = CaseData.builder()
                .boGrantReissueSendToBulkPrint(NO)
                .build();

        assertEquals(NO, caseData.getBoGrantReissueSendToBulkPrint());
    }

    @Test
    public void boRequestInfoSendToBulkPrintDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(YES, caseData.getBoRequestInfoSendToBulkPrint());
    }

    @Test
    public void boRequestInfoSendToBulkPrintIgnoresDefault() {
        final CaseData caseData = CaseData.builder()
                .boRequestInfoSendToBulkPrint(NO)
                .build();

        assertEquals(NO, caseData.getBoRequestInfoSendToBulkPrint());
    }

    @Test
    public void boAssembleLetterSendToBulkPrintDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(YES, caseData.getBoAssembleLetterSendToBulkPrint());
    }

    @Test
    public void boAssembleLetterSendToBulkPrintIgnoresDefault() {
        final CaseData caseData = CaseData.builder()
                .boAssembleLetterSendToBulkPrint(NO)
                .build();

        assertEquals(NO, caseData.getBoAssembleLetterSendToBulkPrint());
    }

    @Test
    public void solicitorIsMainApplicantIsYes() {
        final CaseData caseData = CaseData.builder()
                .solsSolicitorIsMainApplicant(YES)
                .build();

        assertEquals(YES, caseData.solicitorIsMainApplicant());
    }

    @Test
    public void solicitorIsMainApplicantIsNoWhenNoMainApplicant() {
        final CaseData caseData = CaseData.builder()
                .solsSolicitorIsMainApplicant(NO)
                .build();

        assertEquals(NO, caseData.solicitorIsMainApplicant());
    }

    @Test
    public void solicitorIsMainApplicantIsNoWhenNullMainApplicant() {
        final CaseData caseData = CaseData.builder()
                .solsSolicitorIsMainApplicant(null)
                .build();

        assertEquals(NO, caseData.solicitorIsMainApplicant());
    }

    @Test
    public void shouldGetDefaultValueForEmailNotificationsWhenPrimaryAppEmailSet() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantEmailAddress("test-primaryApp@test.com")
                .solsSolicitorEmail(null)
                .build();

        final CaseData caseData2 = CaseData.builder()
                .primaryApplicantEmailAddress("test-primaryApp@test.com")
                .solsSolicitorEmail("")
                .build();

        assertEquals(YES, caseData.getDefaultValueForEmailNotifications());
        assertEquals(YES, caseData2.getDefaultValueForEmailNotifications());
    }

    @Test
    public void shouldGetDefaultValueForEmailNotificationsWhenSolicitorEmailSet() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantEmailAddress(null)
                .solsSolicitorEmail("test-solictor@test.com")
                .build();

        final CaseData caseData2 = CaseData.builder()
                .primaryApplicantEmailAddress("")
                .solsSolicitorEmail("test-solictor@test.com")
                .build();

        assertEquals(YES, caseData.getDefaultValueForEmailNotifications());
        assertEquals(YES, caseData2.getDefaultValueForEmailNotifications());
    }

    @Test
    public void shouldGetDefaultValueForEmailNotificationsWhenEmailAddressNotSet() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantEmailAddress("")
                .solsSolicitorEmail(null)
                .build();

        assertEquals(NO, caseData.getDefaultValueForEmailNotifications());
    }
    
    @Test
    public void shouldApplyParentAttributes() {
        DynamicList reprintDocument = DynamicList.builder().value(DynamicListItem.builder().code("reprintDocument").build()).build();
        DynamicList solsAmendLegalStatmentSelect = DynamicList.builder().value(DynamicListItem.builder().code("solsAmendLegalStatmentSelect").build()).build();
        
        final CaseData caseData = CaseData.builder().primaryApplicantForenames("PAFN")
            .reprintDocument(reprintDocument).reprintNumberOfCopies("1").solsAmendLegalStatmentSelect(solsAmendLegalStatmentSelect)
            .declarationCheckbox("Yes")
            .ihtGrossValueField("1000").ihtNetValueField("900")
            .numberOfExecutors(1L).numberOfApplicants(2L)
            .legalDeclarationJson("legalDeclarationJson").checkAnswersSummaryJson("checkAnswersSummaryJson")
            .registryAddress("registryAddress").registryEmailAddress("registryEmailAddress").registrySequenceNumber("registrySequenceNumber")
            .build();

        assertEquals("PAFN", caseData.getPrimaryApplicantForenames());
        assertEquals("reprintDocument", caseData.getReprintDocument().getValue().getCode());
        assertEquals("1", caseData.getReprintNumberOfCopies());
        assertEquals("solsAmendLegalStatmentSelect", caseData.getSolsAmendLegalStatmentSelect().getValue().getCode());
        assertEquals("1000", caseData.getIhtGrossValueField());
        assertEquals("Yes", caseData.getDeclarationCheckbox());
        assertEquals("900", caseData.getIhtNetValueField());
        assertEquals(Long.valueOf(1), caseData.getNumberOfExecutors());
        assertEquals(Long.valueOf(2), caseData.getNumberOfApplicants());
        assertEquals("legalDeclarationJson", caseData.getLegalDeclarationJson());
        assertEquals("checkAnswersSummaryJson", caseData.getCheckAnswersSummaryJson());
        assertEquals("registryAddress", caseData.getRegistryAddress());
        assertEquals("registryEmailAddress", caseData.getRegistryEmailAddress());
        assertEquals("registrySequenceNumber", caseData.getRegistrySequenceNumber());
    }
}
