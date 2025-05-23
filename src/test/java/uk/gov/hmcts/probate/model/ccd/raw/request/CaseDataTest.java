package uk.gov.hmcts.probate.model.ccd.raw.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.reform.probate.model.cases.CombinedName;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.Damage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.ANSWER_NO;

class CaseDataTest {

    private static final String PRIMARY_APPLICANT_FIRST_NAME = "fName";
    private static final String PRIMARY_APPLICANT_SURNAME = "sName";
    private static final SolsAddress PRIMARY_APPLICANT_ADDRESS = mock(SolsAddress.class);
    private static final String PRIMARY_APPLICANT_NAME_ON_WILL = "willName";
    private static final String DECEASED_FIRST_NAME = "Name";
    private static final String DECEASED_SURNAME = "Surname";
    private static final String NOT_APPLYING_REASON = "not applying reason";
    private static final LocalDate LOCAL_DATE = LocalDate.of(2000, 01, 01);
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

    private AutoCloseable closeableMocks;

    @BeforeEach
    void setup() {

        closeableMocks = openMocks(this);

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

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void shouldReturnPrimaryApplicantFullName() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .build();

        assertEquals(PRIMARY_APPLICANT_FIRST_NAME + " " + PRIMARY_APPLICANT_SURNAME,
            caseData.getPrimaryApplicantFullName());
    }

    @Test
    void shouldReturnDeceasedFullName() {
        final CaseData caseData = CaseData.builder()
            .deceasedForenames(DECEASED_FIRST_NAME)
            .deceasedSurname(DECEASED_SURNAME)
            .build();

        assertEquals(DECEASED_FIRST_NAME + " " + DECEASED_SURNAME, caseData.getDeceasedFullName());
    }

    //    @Test
    //    public void shouldSplitApplyingExecutorNameWhenDoubleBarrelledNames() {
    //        when(additionalExecutorApplying1Mock.getApplyingExecutorName()).thenReturn("Appl-ying Name");
    //        when(additionalExecutorApplying2Mock.getApplyingExecutorName()).thenReturn("Applying Na-me");
    //        when(additionalExecutorApplying3Mock.getApplyingExecutorName()).thenReturn("Appl-ying Na-me");
    //
    //        final CaseData caseData = CaseData.builder()
    //            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
    //            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
    //            .primaryApplicantIsApplying(YES)
    //            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
    //            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
    //            .solsAdditionalExecutorList(null)
    //            .solsWillType(null)
    //            .additionalExecutorsApplying(additionalExecutorsApplyingList)
    //            .build();
    //
    //        List<CollectionMember<AdditionalExecutorApplying>> applying =
    //        caseData.getExecutorsApplyingLegalStatement();
    //
    //        assertEquals(4, applying.size());
    //        assertEquals("Appl-ying", applying.get(1).getValue().getApplyingExecutorFirstName());
    //        assertEquals("Name", applying.get(1).getValue().getApplyingExecutorLastName());
    //        assertEquals("Applying", applying.get(2).getValue().getApplyingExecutorFirstName());
    //        assertEquals("Na-me", applying.get(2).getValue().getApplyingExecutorLastName());
    //        assertEquals("Appl-ying", applying.get(3).getValue().getApplyingExecutorFirstName());
    //        assertEquals("Na-me", applying.get(3).getValue().getApplyingExecutorLastName());
    //    }

    //    @Test
    //    public void shouldSplitApplyingExecutorNameWhenSingleName() {
    //        when(additionalExecutorApplying1Mock.getApplyingExecutorName()).thenReturn("ApplyingName");
    //        when(additionalExecutorsApplying2Mock.getValue()).thenReturn(null);
    //        when(additionalExecutorsApplying3Mock.getValue()).thenReturn(null);
    //
    //        final CaseData caseData = CaseData.builder()
    //            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
    //            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
    //            .primaryApplicantIsApplying(YES)
    //            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
    //            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
    //            .solsAdditionalExecutorList(null)
    //            .solsWillType(null)
    //            .additionalExecutorsApplying(additionalExecutorsApplyingList)
    //            .build();
    //
    //        List<CollectionMember<AdditionalExecutorApplying>> applying
    //        = caseData.getExecutorsApplyingLegalStatement();
    //
    //        assertEquals(2, applying.size());
    //        assertEquals("ApplyingName", applying.get(1).getValue().getApplyingExecutorFirstName());
    //        assertEquals(null, applying.get(1).getValue().getApplyingExecutorLastName());
    //    }

    //    @Test
    //    public void shouldSplitNotApplyingExecutorNameWhenDoubleBarrelledNames() {
    //        when(additionalExecutorNotApplying1Mock.getNotApplyingExecutorName()).thenReturn("NotAppl-ying Name");
    //        when(additionalExecutorNotApplying2Mock.getNotApplyingExecutorName()).thenReturn("NotApplying Na-me");
    //        when(additionalExecutorNotApplying3Mock.getNotApplyingExecutorName()).thenReturn("NotAppl-ying Na-me");
    //
    //        final CaseData caseData = CaseData.builder()
    //            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
    //            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
    //            .primaryApplicantIsApplying(NO)
    //            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
    //            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
    //            .solsAdditionalExecutorList(null)
    //            .solsWillType(null)
    //            .additionalExecutorsNotApplying(additionalExecutorsNotApplyingList)
    //            .build();
    //
    //        List<CollectionMember<AdditionalExecutorNotApplying>> notApplying =
    //                caseData.getExecutorsNotApplyingLegalStatement();
    //
    //        assertEquals(4, notApplying.size());
    //        assertEquals("NotAppl-ying Name", notApplying.get(1).getValue().getNotApplyingExecutorName());
    //        assertEquals("NotApplying Na-me", notApplying.get(2).getValue().getNotApplyingExecutorName());
    //        assertEquals("NotAppl-ying Na-me", notApplying.get(3).getValue().getNotApplyingExecutorName());
    //    }

    //    @Test
    //    public void shouldSplitNotApplyingExecutorNameWhenSingleName() {
    //        when(additionalExecutorNotApplying1Mock.getNotApplyingExecutorName()).thenReturn("NotApplyingName");
    //        when(additionalExecutorsNotApplying2Mock.getValue()).thenReturn(null);
    //        when(additionalExecutorsNotApplying3Mock.getValue()).thenReturn(null);
    //
    //        final CaseData caseData = CaseData.builder()
    //            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
    //            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
    //            .primaryApplicantIsApplying(NO)
    //            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
    //            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
    //            .solsAdditionalExecutorList(null)
    //            .solsWillType(null)
    //            .additionalExecutorsNotApplying(additionalExecutorsNotApplyingList)
    //            .build();
    //
    //        List<CollectionMember<AdditionalExecutorNotApplying>> notApplying =
    //                caseData.getExecutorsNotApplyingLegalStatement();
    //
    //        assertEquals(2, notApplying.size());
    //        assertEquals("NotApplyingName", notApplying.get(1).getValue().getNotApplyingExecutorName());
    //    }

    @Test
    void shouldReturnDODFormattedWithST() {
        final CaseData caseData = CaseData.builder()
            .deceasedDateOfDeath(LOCAL_DATE)
            .build();

        assertEquals("1st January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    void shouldReturnDODFormattedWithSTPublic() {
        CaseData caseData = CaseData.builder().build();
        assertEquals("1st January 2000", caseData.convertDate(LOCAL_DATE));
    }

    @Test
    void shouldReturnDODFormattedWithND() {
        final CaseData caseData = CaseData.builder()
            .deceasedDateOfDeath(LocalDate.of(2000, 01, 02))
            .build();

        assertEquals("2nd January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    void shouldReturnDODFormattedWithRD() {
        final CaseData caseData = CaseData.builder()
            .deceasedDateOfDeath(LocalDate.of(2000, 01, 03))
            .build();

        assertEquals("3rd January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    void shouldReturnDODFormattedWithTH() {
        final CaseData caseData = CaseData.builder()
            .deceasedDateOfDeath(LocalDate.of(2000, 01, 04))
            .build();

        assertEquals("4th January 2000", caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    void shouldThrowParseException() {
        final CaseData caseData = CaseData.builder()
            .deceasedDateOfDeath(LocalDate.of(300000, 01, 04))
            .build();

        assertEquals(null, caseData.getDeceasedDateOfDeathFormatted());
    }

    @Test
    void isBoEmailRequestInfoNotificationRequestedTrue() {
        final CaseData caseData = CaseData.builder()
            .boEmailRequestInfoNotification(YES)
            .build();

        assertEquals(true, caseData.isBoEmailRequestInfoNotificationRequested());
    }

    @Test
    void isBoEmailRequestInfoNotificationRequestedFromDefaultTrue() {
        final CaseData caseData = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .build();

        assertEquals(true, caseData.isBoEmailRequestInfoNotificationRequested());
    }

    @Test
    void isBoEmailRequestInfoNotificationRequestedFalse() {
        final CaseData caseData = CaseData.builder()
            .boEmailRequestInfoNotification(NO)
            .build();

        assertEquals(false, caseData.isBoEmailRequestInfoNotificationRequested());
    }

    @Test
    void isBoEmailRequestInfoNotificationRequestedFromDefaultFalse() {
        final CaseData caseData = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .primaryApplicantEmailAddress(null)
            .build();

        assertEquals(false, caseData.isBoEmailRequestInfoNotificationRequested());
    }

    @Test
    void isBoEmailDocsReceivedNotificationTrue() {
        final CaseData caseData = CaseData.builder()
            .boEmailDocsReceivedNotification(YES)
            .build();

        assertEquals(true, caseData.isDocsReceivedEmailNotificationRequested());
    }

    @Test
    void isBoEmailDocsReceivedNotificationFromDefaultTrue() {
        final CaseData caseData = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .build();

        assertEquals(true, caseData.isDocsReceivedEmailNotificationRequested());
    }

    @Test
    void isBoEmailDocsReceivedNotificationFalse() {
        final CaseData caseData = CaseData.builder()
            .boEmailDocsReceivedNotification(NO)
            .build();

        assertEquals(false, caseData.isDocsReceivedEmailNotificationRequested());
    }

    @Test
    void isBoEmailDocsReceivedNotificationFromDefaultFalse() {
        final CaseData caseData = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .primaryApplicantEmailAddress(null)
            .build();

        assertEquals(false, caseData.isDocsReceivedEmailNotificationRequested());
    }

    @Test
    void isBoEmailGrantIssuedNotificationTrue() {
        final CaseData caseData = CaseData.builder()
            .boEmailGrantIssuedNotification(YES)
            .build();

        assertEquals(true, caseData.isGrantIssuedEmailNotificationRequested());
    }

    @Test
    void isBoEmailGrantIssuedNotificationFromDefaultTrue() {
        final CaseData caseData = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .build();

        assertEquals(true, caseData.isGrantIssuedEmailNotificationRequested());
    }

    @Test
    void isBoEmailGrantIssuedNotificationFalse() {
        final CaseData caseData = CaseData.builder()
            .boEmailGrantIssuedNotification(NO)
            .build();

        assertEquals(false, caseData.isGrantIssuedEmailNotificationRequested());
    }

    @Test
    void isBoEmailGrantIssuedNotificationFromDefaultFalse() {
        final CaseData caseData = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .primaryApplicantEmailAddress(null)
            .build();

        assertEquals(false, caseData.isGrantIssuedEmailNotificationRequested());
    }

    @Test
    void isBoEmailGrantReissuedNotificationTrue() {
        final CaseData caseData = CaseData.builder()
            .boEmailGrantReissuedNotification(YES)
            .build();

        assertEquals(true, caseData.isGrantReissuedEmailNotificationRequested());
    }

    @Test
    void isBoEmailGrantReissuedNotificationWithDefaultTrue() {
        final CaseData caseData = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .build();

        assertEquals(true, caseData.isGrantReissuedEmailNotificationRequested());
    }

    @Test
    void isBoEmailGrantReissuedNotificationFalse() {
        final CaseData caseData = CaseData.builder()
            .boEmailGrantIssuedNotification(NO)
            .build();

        assertEquals(false, caseData.isGrantReissuedEmailNotificationRequested());
    }

    @Test
    void isBoEmailGrantReissuedNotificationWithDefaultFalse() {
        final CaseData caseData = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .primaryApplicantEmailAddress(null)
            .build();

        assertEquals(false, caseData.isGrantReissuedEmailNotificationRequested());
    }

    @Test
    void isBoCaveatStopEmailNotificationTrue() {
        final CaseData caseData = CaseData.builder()
            .boCaveatStopEmailNotification(YES)
            .build();

        assertEquals(true, caseData.isCaveatStopEmailNotificationRequested());
    }

    @Test
    void isBoCaveatStopEmailNotificationWithDefaultTrue() {
        final CaseData caseData = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .build();

        assertEquals(true, caseData.isCaveatStopEmailNotificationRequested());
    }

    @Test
    void isBoCaveatStopEmailNotificationFalse() {
        final CaseData caseData = CaseData.builder()
            .boCaveatStopEmailNotification(NO)
            .build();

        assertEquals(false, caseData.isCaveatStopEmailNotificationRequested());
    }

    @Test
    void isBoCaveatStopEmailNotificationWithDefaultFalse() {
        final CaseData caseData = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .primaryApplicantEmailAddress(null)
            .build();

        assertEquals(false, caseData.isCaveatStopEmailNotificationRequested());
    }

    @Test
    void probateDocumentsGeneratedDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(new ArrayList<>(), caseData.getProbateDocumentsGenerated());
    }

    @Test
    void probateDocumentsGeneratedIgnoresDefault() {
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
    void probateNotificationsGeneratedDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(new ArrayList<>(), caseData.getProbateNotificationsGenerated());
    }

    @Test
    void probateNotificationsGeneratedIgnoresDefault() {
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
    void probateSotDocumentsGeneratedDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(new ArrayList<>(), caseData.getProbateSotDocumentsGenerated());
    }

    @Test
    void probateSotDocumentsGeneratedIgnoresDefault() {
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
    void caseMatchesDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(new ArrayList<>(), caseData.getCaseMatches());
    }

    @Test
    void caseMatchesIgnoresDefault() {
        List<CollectionMember<CaseMatch>> caseMatches = new ArrayList<>();
        CollectionMember<CaseMatch> caseMatch =
            new CollectionMember<>(CaseMatch.builder().fullName("Name One").build());
        caseMatches.add(caseMatch);

        final CaseData caseData = CaseData.builder()
            .caseMatches(caseMatches)
            .build();

        assertEquals(caseMatches, caseData.getCaseMatches());
    }

    @Test
    void boCaveatStopSendToBulkPrintDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(YES, caseData.getBoCaveatStopSendToBulkPrint());
    }

    @Test
    void boCaveatStopSendToBulkPrintIgnoresDefault() {
        final CaseData caseData = CaseData.builder()
            .boCaveatStopSendToBulkPrint(NO)
            .build();

        assertEquals(NO, caseData.getBoCaveatStopSendToBulkPrint());
    }

    @Test
    void boGrantReissueSendToBulkPrintDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(YES, caseData.getBoGrantReissueSendToBulkPrint());
    }

    @Test
    void boGrantReissueSendToBulkPrintIgnoresDefault() {
        final CaseData caseData = CaseData.builder()
            .boGrantReissueSendToBulkPrint(NO)
            .build();

        assertEquals(NO, caseData.getBoGrantReissueSendToBulkPrint());
    }

    @Test
    void boRequestInfoSendToBulkPrintDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(YES, caseData.getBoRequestInfoSendToBulkPrint());
    }

    @Test
    void boRequestInfoSendToBulkPrintIgnoresDefault() {
        final CaseData caseData = CaseData.builder()
            .boRequestInfoSendToBulkPrint(NO)
            .build();

        assertEquals(NO, caseData.getBoRequestInfoSendToBulkPrint());
    }

    @Test
    void boAssembleLetterSendToBulkPrintDefaultsCorrectly() {
        final CaseData caseData = CaseData.builder().build();

        assertEquals(YES, caseData.getBoAssembleLetterSendToBulkPrint());
    }

    @Test
    void boAssembleLetterSendToBulkPrintIgnoresDefault() {
        final CaseData caseData = CaseData.builder()
            .boAssembleLetterSendToBulkPrint(NO)
            .build();

        assertEquals(NO, caseData.getBoAssembleLetterSendToBulkPrint());
    }

    @Test
    void shouldGetDefaultValueForEmailNotificationsWhenPrimaryAppEmailSet() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .solsSolicitorEmail(null)
            .build();

        final CaseData caseData2 = CaseData.builder()
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .solsSolicitorEmail("")
            .build();

        assertEquals(YES, caseData.getDefaultValueForEmailNotifications());
        assertEquals(YES, caseData2.getDefaultValueForEmailNotifications());
    }

    @Test
    void shouldGetDefaultValueForEmailNotificationsWhenSolicitorEmailSet() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantEmailAddress(null)
            .solsSolicitorEmail("solicitor@probate-test.com")
            .build();

        final CaseData caseData2 = CaseData.builder()
            .primaryApplicantEmailAddress("")
            .solsSolicitorEmail("solicitor@probate-test.com")
            .build();

        assertEquals(YES, caseData.getDefaultValueForEmailNotifications());
        assertEquals(YES, caseData2.getDefaultValueForEmailNotifications());
    }

    @Test
    void shouldGetDefaultValueForEmailNotificationsWhenEmailAddressNotSet() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantEmailAddress("")
            .solsSolicitorEmail(null)
            .build();

        assertEquals(NO, caseData.getDefaultValueForEmailNotifications());
    }

    @Test
    void shouldApplyParentAttributes() {
        DynamicList reprintDocument =
            DynamicList.builder().value(DynamicListItem.builder().code("reprintDocument").build()).build();
        DynamicList solsAmendLegalStatmentSelect =
            DynamicList.builder().value(DynamicListItem.builder().code("solsAmendLegalStatmentSelect").build()).build();

        final CaseData caseData = CaseData.builder().primaryApplicantForenames("PAFN")
            .reprintDocument(reprintDocument).reprintNumberOfCopies("1")
            .solsAmendLegalStatmentSelect(solsAmendLegalStatmentSelect)
            .declarationCheckbox("Yes")
            .ihtGrossValueField("1000").ihtNetValueField("900")
            .numberOfExecutors(1L).numberOfApplicants(2L)
            .legalDeclarationJson("legalDeclarationJson").checkAnswersSummaryJson("checkAnswersSummaryJson")
            .registryAddress("registryAddress").registryEmailAddress("registryEmailAddress")
            .registrySequenceNumber("registrySequenceNumber")
            .dispenseWithNotice("Yes")
            .isSolThePrimaryApplicant("Yes")
            .originalWillSignedDateFormatted("1st January 2020")
            .titleAndClearingType("TCTTrustCorpResWithApp")
            .registrySequenceNumber("registrySequenceNumber")
            .iht217("Yes")
            .build();

        assertEquals("PAFN", caseData.getPrimaryApplicantForenames());
        assertEquals("reprintDocument", caseData.getReprintDocument().getValue().getCode());
        assertEquals("1", caseData.getReprintNumberOfCopies());
        assertEquals("solsAmendLegalStatmentSelect", caseData.getSolsAmendLegalStatmentSelect().getValue().getCode());
        assertEquals("1000", caseData.getIhtGrossValueField());
        assertEquals("Yes", caseData.getDeclarationCheckbox());
        assertEquals("Yes", caseData.getIsSolThePrimaryApplicant());
        assertEquals("900", caseData.getIhtNetValueField());
        assertEquals(Long.valueOf(1), caseData.getNumberOfExecutors());
        assertEquals(Long.valueOf(2), caseData.getNumberOfApplicants());
        assertEquals("legalDeclarationJson", caseData.getLegalDeclarationJson());
        assertEquals("checkAnswersSummaryJson", caseData.getCheckAnswersSummaryJson());
        assertEquals("registryAddress", caseData.getRegistryAddress());
        assertEquals("registryEmailAddress", caseData.getRegistryEmailAddress());
        assertEquals("registrySequenceNumber", caseData.getRegistrySequenceNumber());
        assertEquals("1st January 2020", caseData.getOriginalWillSignedDateFormatted());
        assertEquals("Yes", caseData.getIht217());
    }

    @Test
    void shouldApplySolicitorInfoAttributes() {
        final CaseData caseData = CaseData.builder()
                .solsForenames("Solicitor Forename")
                .solsSurname("Solicitor Surname")
                .solsSolicitorWillSignSOT("Yes")
                .build();

        assertEquals("Solicitor Forename", caseData.getSolsForenames());
        assertEquals("Solicitor Surname", caseData.getSolsSurname());
        assertEquals("Yes", caseData.getSolsSolicitorWillSignSOT());
    }

    @Test
    void shouldApplyTrustCorpAttributes() {
        CollectionMember<AdditionalExecutorTrustCorps> additionalExecutorTrustCorp = new CollectionMember<>(
                new AdditionalExecutorTrustCorps(
                        "Executor forename",
                        "Executor surname",
                        "Solicitor"
                ));
        List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList = new ArrayList<>();
        additionalExecutorsTrustCorpList.add(additionalExecutorTrustCorp);

        SolsAddress trustCorpAddress = new SolsAddress(
                "Address Line 1",
                "",
                "",
                "",
                "",
                "POSTCODE",
                "");

        SolsAddress addressOfSucceededFirm = new SolsAddress(
            "Address Line 1",
            "",
            "",
            "",
            "",
            "POSTCODE",
            "");

        SolsAddress addressOfFirmNamedInWill = new SolsAddress(
            "Address Line 1",
            "",
            "",
            "",
            "",
            "POSTCODE",
            "");

        final CaseData caseData = CaseData.builder()
                .dispenseWithNotice("Yes")
                .dispenseWithNoticeLeaveGiven("No")
                .dispenseWithNoticeOverview("Overview")
                .dispenseWithNoticeSupportingDocs("Supporting docs")
                .titleAndClearingType("TCTTrustCorpResWithApp")
                .trustCorpName("Trust corp name")
                .trustCorpAddress(trustCorpAddress)
                .addressOfSucceededFirm(addressOfSucceededFirm)
                .addressOfFirmNamedInWill(addressOfFirmNamedInWill)
                .additionalExecutorsTrustCorpList(additionalExecutorsTrustCorpList)
                .furtherEvidenceForApplication("Further evidence")
                .lodgementAddress("London")
                .lodgementDate(LOCAL_DATE)
                .build();

        assertEquals("Yes", caseData.getDispenseWithNotice());
        assertEquals("No", caseData.getDispenseWithNoticeLeaveGiven());
        assertEquals("Overview", caseData.getDispenseWithNoticeOverview());
        assertEquals("Supporting docs", caseData.getDispenseWithNoticeSupportingDocs());
        assertEquals("TCTTrustCorpResWithApp", caseData.getTitleAndClearingType());
        assertEquals("Trust corp name", caseData.getTrustCorpName());
        assertEquals(trustCorpAddress, caseData.getTrustCorpAddress());
        assertEquals(addressOfSucceededFirm, caseData.getAddressOfSucceededFirm());
        assertEquals(addressOfFirmNamedInWill, caseData.getAddressOfFirmNamedInWill());
        assertEquals("Further evidence", caseData.getFurtherEvidenceForApplication());
        assertEquals(additionalExecutorsTrustCorpList, caseData.getAdditionalExecutorsTrustCorpList());
        assertEquals("London", caseData.getLodgementAddress());
        assertEquals(LOCAL_DATE, caseData.getLodgementDate());
    }

    @Test

    public void shouldApplyNonTrustCorpOptionAttributes() {
        CollectionMember<AdditionalExecutorPartners> otherPartner = new CollectionMember<>(
                new AdditionalExecutorPartners(
                        "Executor forename",
                        "Executor surname",
                        mock(SolsAddress.class)));
        List<CollectionMember<AdditionalExecutorPartners>> otherPartnersList = new ArrayList<>();
        otherPartnersList.add(otherPartner);

        final CaseData caseData = CaseData.builder()
                .dispenseWithNotice("Yes")
                .dispenseWithNoticeLeaveGiven("No")
                .dispenseWithNoticeOverview("Overview")
                .dispenseWithNoticeSupportingDocs("Supporting docs")
                .titleAndClearingType("TCTPartSuccPowerRes")
                .nameOfFirmNamedInWill("Test Solicitor Ltd")
                .otherPartnersApplyingAsExecutors(otherPartnersList)
                .nameOfSucceededFirm("New Firm Ltd")
                .whoSharesInCompanyProfits(Arrays.asList(new String[]{"Partners", "Members"}))
                .morePartnersHoldingPowerReserved("No")
                .build();

        assertEquals("Yes", caseData.getDispenseWithNotice());
        assertEquals("No", caseData.getDispenseWithNoticeLeaveGiven());
        assertEquals("Overview", caseData.getDispenseWithNoticeOverview());
        assertEquals("Supporting docs", caseData.getDispenseWithNoticeSupportingDocs());
        assertEquals("TCTPartSuccPowerRes", caseData.getTitleAndClearingType());
        assertEquals("Test Solicitor Ltd", caseData.getNameOfFirmNamedInWill());
        assertEquals(otherPartnersList, caseData.getOtherPartnersApplyingAsExecutors());
        assertEquals("New Firm Ltd", caseData.getNameOfSucceededFirm());
        assertEquals("Partners", caseData.getWhoSharesInCompanyProfits().get(0));
        assertEquals("Members", caseData.getWhoSharesInCompanyProfits().get(1));
        assertEquals("No", caseData.getMorePartnersHoldingPowerReserved());
    }

    @Test
    void shouldApplyTrustCorpNoneOfTheseAttributes() {
        CollectionMember<AdditionalExecutorTrustCorps> additionalExecutorTrustCorp = new CollectionMember<>(
                new AdditionalExecutorTrustCorps(
                        "Executor forename",
                        "Executor surname",
                        "Solicitor"
                ));
        List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList = new ArrayList<>();
        additionalExecutorsTrustCorpList.add(additionalExecutorTrustCorp);

        final CaseData caseData = CaseData.builder()
                .titleAndClearingType("TCTNoT")
                .build();

        assertEquals("TCTNoT", caseData.getTitleAndClearingType());

    }

    @Test
    void shouldApplySolicitorLegalStatementAttributes() {
        final CaseData caseData = CaseData.builder()
                .executorsApplyingLegalStatement(additionalExecutorsApplyingList)
                .executorsNotApplyingLegalStatement(additionalExecutorsNotApplyingList)
                .build();

        assertEquals(additionalExecutorsApplyingList, caseData.getExecutorsApplyingLegalStatement());
        assertEquals(additionalExecutorsNotApplyingList, caseData.getExecutorsNotApplyingLegalStatement());
    }

    @Test
    void shouldApplyWillConditionAttributes() {
        final CaseData caseData = CaseData.builder()
            .willHasVisibleDamage("Yes")
            .willDamage(Damage.builder()
                .damageTypesList(Arrays.asList("Type1", "Type2", "Other"))
                .otherDamageDescription("Other damage desc")
                .build())
            .willDamageReasonKnown("Yes")
            .willDamageReasonDescription("Will damage desc")
            .willDamageCulpritKnown("Yes")
            .willDamageCulpritName(CombinedName.builder()
                .firstName("WillCulpritFN")
                .lastName("WillCulpritLN")
                .build())
            .willDamageDateKnown("Yes")
            .willDamageDate("1/12/2020")
            .codicilsHasVisibleDamage("Yes")
            .codicilsDamage(Damage.builder()
                .damageTypesList(Arrays.asList("Type1C", "Type2C", "Other"))
                .otherDamageDescription("Cod Other desc")
                .build())
            .codicilsDamageReasonKnown("Yes")
            .codicilsDamageReasonDescription("Cod damage desc")
            .codicilsDamageCulpritKnown("Yes")
            .codicilsDamageCulpritName(CombinedName.builder()
                .firstName("CodCulpritFN")
                .lastName("CodCulpritLN")
                .build())
            .codicilsDamageDateKnown("Yes")
            .codicilsDamageDate("9/2021")
            .deceasedWrittenWishes("Yes")
            .build();

        assertEquals("Yes", caseData.getWillHasVisibleDamage());
        assertEquals("Type1", caseData.getWillDamage().getDamageTypesList().get(0));
        assertEquals("Type2", caseData.getWillDamage().getDamageTypesList().get(1));
        assertEquals("Other", caseData.getWillDamage().getDamageTypesList().get(2));
        assertEquals("Other damage desc", caseData.getWillDamage().getOtherDamageDescription());
        assertEquals("Yes", caseData.getWillDamageReasonKnown());
        assertEquals("Will damage desc", caseData.getWillDamageReasonDescription());
        assertEquals("Yes", caseData.getWillDamageCulpritKnown());
        assertEquals("WillCulpritFN", caseData.getWillDamageCulpritName().getFirstName());
        assertEquals("WillCulpritLN", caseData.getWillDamageCulpritName().getLastName());
        assertEquals("Yes", caseData.getWillDamageDateKnown());
        assertEquals("1/12/2020", caseData.getWillDamageDate());


        assertEquals("Yes", caseData.getCodicilsHasVisibleDamage());
        assertEquals("Type1C", caseData.getCodicilsDamage().getDamageTypesList().get(0));
        assertEquals("Type2C", caseData.getCodicilsDamage().getDamageTypesList().get(1));
        assertEquals("Other", caseData.getCodicilsDamage().getDamageTypesList().get(2));
        assertEquals("Cod Other desc", caseData.getCodicilsDamage().getOtherDamageDescription());
        assertEquals("Yes", caseData.getCodicilsDamageReasonKnown());
        assertEquals("Cod damage desc", caseData.getCodicilsDamageReasonDescription());
        assertEquals("Yes", caseData.getCodicilsDamageCulpritKnown());
        assertEquals("CodCulpritFN", caseData.getCodicilsDamageCulpritName().getFirstName());
        assertEquals("CodCulpritLN", caseData.getCodicilsDamageCulpritName().getLastName());
        assertEquals("Yes", caseData.getCodicilsDamageDateKnown());
        assertEquals("9/2021", caseData.getCodicilsDamageDate());

        assertEquals("Yes", caseData.getDeceasedWrittenWishes());
    }

    @Test
    void clearPrimaryApplicantClearsPrimaryApplicant() {
        final SolsAddress nullAddress = SolsAddress.builder().build();

        final CaseData.CaseDataBuilder caseDataBuilder = CaseData.builder()
                .primaryApplicantHasAlias(ANSWER_NO)
                .primaryApplicantAddress(nullAddress);

        final CaseData baseCaseData = caseDataBuilder.build();

        final SolsAddress applAddress = SolsAddress.builder()
                .addressLine1("addr1")
                .addressLine2("addr2")
                .addressLine3("addr3")
                .country("country")
                .county("county")
                .postCode("postcode")
                .postTown("town")
                .build();

        final CaseData applCaseData =  caseDataBuilder
                .primaryApplicantIsApplying("yes")
                .primaryApplicantHasAlias("yes")
                .primaryApplicantForenames("fname")
                .primaryApplicantSurname("sname")
                .primaryApplicantHasAlias("yes")
                .primaryApplicantAlias("alias")
                .primaryApplicantEmailAddress("email")
                .primaryApplicantPhoneNumber("phone")
                .primaryApplicantAddress(applAddress)
                .build();

        applCaseData.clearPrimaryApplicant();

        assertEquals(baseCaseData, applCaseData);
    }

    @Test
    void clearAdditionalExecutorListClearsAdditionalExecutorList() {
        List<CollectionMember<AdditionalExecutor>> additionalExecutorsList = new ArrayList<>();
        additionalExecutorsList.add(additionalExecutors1Mock);
        additionalExecutorsList.add(additionalExecutors2Mock);

        CaseData caseData = CaseData.builder()
                .solsAdditionalExecutorList(additionalExecutorsList)
                .otherExecutorExists(NO)
                .build();

        caseData.clearAdditionalExecutorList();

        assertTrue(caseData.getSolsAdditionalExecutorList().isEmpty());
        assertEquals(NO, caseData.getOtherExecutorExists());
    }
}
