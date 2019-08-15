package uk.gov.hmcts.probate.transformer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.Reissue;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdoptedRelative;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.AttorneyApplyingOnBehalfOf;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.EstateItem;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.service.StateChangeService;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.CTSC;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_STOPPED;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;

@RunWith(MockitoJUnitRunner.class)
public class CallbackResponseTransformerTest {

    private static final String[] LAST_MODIFIED_STR = {"2018", "1", "2", "0", "0", "0", "0"};
    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final String WILL_MESSAGE = "Will message";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String CASE_TYPE_GRANT_OF_PROBATE = "gop";
    private static final String CASE_TYPE_INTESTACY = "intestacy";
    private static final String WILL_TYPE_PROBATE = "WillLeft";
    private static final String WILL_TYPE_INTESTACY = "NoWill";
    private static final String WILL_TYPE_ADMON = "WillLeftAnnexed";

    private static final ApplicationType APPLICATION_TYPE = SOLICITOR;
    private static final String REGISTRY_LOCATION = CTSC;

    private static final String SOLICITOR_FIRM_NAME = "Sol Firm Name";
    private static final String SOLICITOR_FIRM_LINE1 = "Sols Add Line 1";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW13 6EA";
    private static final String SOLICITOR_FIRM_EMAIL = "sol@email.com";
    private static final String SOLICITOR_FIRM_PHONE = "0123456789";
    private static final String SOLICITOR_SOT_NAME = "Andy Test";
    private static final String SOLICITOR_SOT_JOB_TITLE = "Lawyer";

    private static final String DECEASED_FIRSTNAME = "Firstname";
    private static final String DECEASED_LASTNAME = "Lastname";
    private static final String DECEASED_DATE_OF_DEATH_TYPE = "diedOnOrSince";
    private static final LocalDate DOB = LocalDate.parse("2016-12-31", dateTimeFormatter);
    private static final LocalDate DOD = LocalDate.parse("2017-12-31", dateTimeFormatter);
    private static final String NUM_CODICILS = "9";

    private static final String IHT_FORM_ID = "IHT205";
    private static final BigDecimal IHT_GROSS = BigDecimal.valueOf(10000f);
    private static final BigDecimal IHT_NET = BigDecimal.valueOf(9000f);

    private static final String SOL_PAY_METHODS_FEE = "fee account";
    private static final String SOL_PAY_METHODS_CHEQUE = "cheque";
    private static final String FEE_ACCT_NUMBER = "FEE ACCT 1";
    private static final String PAY_REF_FEE = "Fee account PBA-FEE ACCT 1";
    private static final String PAY_REF_CHEQUE = "Cheque (payable to ‘HM Courts & Tribunals Service’)";

    private static final BigDecimal feeForNonUkCopies = new BigDecimal(11);
    private static final BigDecimal feeForUkCopies = new BigDecimal(22);
    private static final BigDecimal applicationFee = new BigDecimal(33);
    private static final BigDecimal totalFee = new BigDecimal(66);
    private static final String DOC_BINARY_URL = "docBinaryUrl";
    private static final String DOC_URL = "docUrl";
    private static final String DOC_NAME = "docName";
    private static final String APPLICANT_FORENAME = "applicant forename";
    private static final String APPLICANT_SURNAME = "applicant surname";
    private static final String APPLICANT_EMAIL_ADDRESS = "pa@email.com";
    private static final String PRIMARY_EXEC_APPLYING = YES;
    private static final String APPLICANT_HAS_ALIAS = YES;
    private static final String OTHER_EXECS_EXIST = NO;
    private static final String PRIMARY_EXEC_ALIAS_NAMES = "Alias names";
    private static final List<CollectionMember<AdditionalExecutor>> ADDITIONAL_EXEC_LIST = emptyList();
    private static final List<CollectionMember<AdditionalExecutorApplying>> ADDITIONAL_EXEC_LIST_APP = emptyList();
    private static final List<CollectionMember<AdditionalExecutorNotApplying>> ADDITIONAL_EXEC_LIST_NOT_APP = emptyList();
    private static final List<CollectionMember<AliasName>> DECEASED_ALIAS_NAMES_LIST = emptyList();
    private static final SolsAddress DECEASED_ADDRESS = Mockito.mock(SolsAddress.class);
    private static final SolsAddress EXEC_ADDRESS = Mockito.mock(SolsAddress.class);
    private static final List<CollectionMember<AliasName>> ALIAS_NAMES = emptyList();
    private static final String APP_REF = "app ref";
    private static final String ADDITIONAL_INFO = "additional info";
    private static final String IHT_REFERENCE = "123456789abcde";
    private static final String IHT_ONLINE = "Yes";

    private static final String EXEC_FIRST_NAME = "ExFName";
    private static final String EXEC_NAME = "ExName";
    private static final String EXEC_NAME_DIFF = "Ex name difference comment";
    private static final String EXEC_WILL_NAME = "Ex will name";
    private static final String EXEC_SURNAME = "EXSName";
    private static final String EXEC_OTHER_NAMES = EXEC_WILL_NAME;
    private static final String EXEC_PHONE = "010101010101";
    private static final String EXEC_EMAIL = "exEmail@abc.com";
    private static final String EXEC_APPEAR = YES;
    private static final String EXEC_NOTIFIED = YES;

    private static final String BO_BULK_PRINT = YES;
    private static final String BO_EMAIL_GRANT_ISSUED = YES;
    private static final String BO_DOCS_RECEIVED = YES;
    private static final String CASE_PRINT = YES;
    private static final String CAVEAT_STOP_NOTIFICATION = YES;
    private static final String CASE_STOP_CAVEAT_ID = "1234567812345678";
    private static final String CAVEAT_STOP_EMAIL_NOTIFICATION = YES;
    private static final String CAVEAT_STOP_SEND_TO_BULK_PRINT = YES;

    private static final List<CollectionMember<StopReason>> STOP_REASONS_LIST = emptyList();
    private static final String STOP_REASON = "Some reason";
    private static final String ALIAS_FORENAME = "AliasFN";
    private static final String ALIAS_SURNAME = "AliasSN";
    private static final String SOLS_ALIAS_NAME = "AliasFN AliasSN";
    private static final String STOP_DETAILS = "";

    private static final Optional<String> ORIGINAL_STATE = Optional.empty();
    private static final Optional<String> CHANGED_STATE = Optional.of("Changed");

    private static final String DECEASED_TITLE = "Deceased Title";
    private static final String DECEASED_HONOURS = "Deceased Honours";

    private static final String LIMITATION_TEXT = "Limitation Text";
    private static final String EXECUTOR_LIMITATION = "Executor Limitation";
    private static final String ADMIN_CLAUSE_LIMITATION = "Admin Clause Limitation";
    private static final String TOTAL_FEE = "6600";

    private static final String RECORD_ID = "12345";
    private static final String LEGACY_CASE_URL = "someUrl";
    private static final String LEGACY_CASE_TYPE = "someCaseType";
    private static final String ORDER_NEEDED = YES;
    private static final List<CollectionMember<Reissue>> REISSUE_REASON = emptyList();
    private static final String REISSUE_DATE = "2019-01-01";
    private static final String REISSUE_NOTATION = "duplicate";

    private static final String DECEASED_DIVORCED_IN_ENGLAND_OR_WALES = YES;
    private static final String PRIMARY_APPLICANT_ADOPTION_IN_ENGLAND_OR_WALES = NO;
    private static final String DECEASED_SPOUSE_NOT_APPLYING_REASON = "notApplyingReason";
    private static final String DECEASED_OTHER_CHILDREN = YES;
    private static final String ALL_DECEASED_CHILDREN_OVER_EIGHTEEN = YES;
    private static final String ANY_DECEASED_CHILDREN_DIE_BEFORE_DECEASED = NO;
    private static final String ANY_DECEASED_GRANDCHILDREN_UNDER_EIGHTEEN = YES;
    private static final String DECEASED_ANY_CHILDREN = NO;
    private static final String DECEASED_HAS_ASSETS_OUTSIDE_UK = YES;
    private static final DocumentLink SOT = DocumentLink.builder().documentFilename("SOT.pdf").build();

    private static final LocalDateTime scannedDate = LocalDateTime.parse("2018-01-01T12:34:56.123");
    private static final List<CollectionMember<Payment>> PAYMENTS_LIST = Arrays.asList(
            new CollectionMember<Payment>("id",
                    Payment.builder()
                            .amount("100")
                            .date("20/09/2018")
                            .method("online")
                            .reference("Reference-123")
                            .status("Success")
                            .siteId("SiteId-123")
                            .transactionId("TransactionId-123")
                            .build()));

    private static final DocumentLink SCANNED_DOCUMENT_URL = DocumentLink.builder()
            .documentBinaryUrl("http://somedoc")
            .documentFilename("somedoc.pdf")
            .documentUrl("http://somedoc/location")
            .build();

    private static final List<CollectionMember<ScannedDocument>> SCANNED_DOCUMENTS_LIST = Arrays.asList(
            new CollectionMember<ScannedDocument>("id",
                    ScannedDocument.builder()
                            .fileName("scanneddocument.pdf")
                            .controlNumber("1234")
                            .scannedDate(scannedDate)
                            .type("other")
                            .subtype("will")
                            .url(SCANNED_DOCUMENT_URL)
                            .build()));

    @InjectMocks
    private CallbackResponseTransformer underTest;

    @Mock
    private StateChangeService stateChangeServiceMock;

    @Mock
    private CallbackRequest callbackRequestMock;

    @Mock
    private CaseDetails caseDetailsMock;

    private CaseData.CaseDataBuilder caseDataBuilder;


    @Mock
    private FeeServiceResponse feeServiceResponseMock;

    @Mock
    private DocumentLink documentLinkMock;

    @Mock
    private UploadDocument uploadDocumentMock;

    @Spy
    private DocumentTransformer documentTransformer;

    @Before
    public void setup() {

        caseDataBuilder = CaseData.builder()
                .solsSolicitorFirmName(SOLICITOR_FIRM_NAME)
                .solsSolicitorAddress(SolsAddress.builder().addressLine1(SOLICITOR_FIRM_LINE1).postCode(SOLICITOR_FIRM_POSTCODE).build())
                .solsSolicitorEmail(SOLICITOR_FIRM_EMAIL)
                .solsSolicitorPhoneNumber(SOLICITOR_FIRM_PHONE)
                .solsSOTName(SOLICITOR_SOT_NAME)
                .solsSOTJobTitle(SOLICITOR_SOT_JOB_TITLE)
                .deceasedForenames(DECEASED_FIRSTNAME)
                .deceasedSurname(DECEASED_LASTNAME)
                .deceasedDateOfBirth(DOB)
                .deceasedDateOfDeath(DOD)
                .willNumberOfCodicils(NUM_CODICILS)
                .ihtFormId(IHT_FORM_ID)
                .ihtGrossValue(IHT_GROSS)
                .ihtNetValue(IHT_NET)
                .primaryApplicantForenames(APPLICANT_FORENAME)
                .primaryApplicantSurname(APPLICANT_SURNAME)
                .primaryApplicantEmailAddress(APPLICANT_EMAIL_ADDRESS)
                .primaryApplicantIsApplying(PRIMARY_EXEC_APPLYING)
                .primaryApplicantHasAlias(APPLICANT_HAS_ALIAS)
                .otherExecutorExists(OTHER_EXECS_EXIST)
                .primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES)
                .solsExecutorAliasNames(PRIMARY_EXEC_ALIAS_NAMES)
                .solsAdditionalExecutorList(ADDITIONAL_EXEC_LIST)
                .deceasedAddress(DECEASED_ADDRESS)
                .deceasedAnyOtherNames(YES)
                .solsDeceasedAliasNamesList(DECEASED_ALIAS_NAMES_LIST)
                .primaryApplicantAddress(EXEC_ADDRESS)
                .solsDeceasedAliasNamesList(ALIAS_NAMES)
                .solsSolicitorAppReference(APP_REF)
                .solsAdditionalInfo(ADDITIONAL_INFO)
                .boEmailGrantIssuedNotificationRequested(BO_EMAIL_GRANT_ISSUED)
                .boEmailDocsReceivedNotificationRequested(BO_DOCS_RECEIVED)
                .boSendToBulkPrintRequested(BO_BULK_PRINT)
                .casePrinted(CASE_PRINT)
                .boCaveatStopNotificationRequested(CAVEAT_STOP_NOTIFICATION)
                .boCaveatStopNotification(CAVEAT_STOP_NOTIFICATION)
                .boCaseStopCaveatId(CASE_STOP_CAVEAT_ID)
                .boCaveatStopEmailNotificationRequested(CAVEAT_STOP_EMAIL_NOTIFICATION)
                .boCaveatStopSendToBulkPrintRequested(CAVEAT_STOP_SEND_TO_BULK_PRINT)
                .boCaseStopReasonList(STOP_REASONS_LIST)
                .boStopDetails(STOP_DETAILS)
                .solsWillType(WILL_TYPE_PROBATE)
                .willExists(YES)
                .additionalExecutorsApplying(ADDITIONAL_EXEC_LIST_APP)
                .additionalExecutorsNotApplying(ADDITIONAL_EXEC_LIST_NOT_APP)
                .boDeceasedTitle(DECEASED_TITLE)
                .boDeceasedHonours(DECEASED_HONOURS)
                .boWillMessage(WILL_MESSAGE)
                .boExecutorLimitation(EXECUTOR_LIMITATION)
                .boAdminClauseLimitation(ADMIN_CLAUSE_LIMITATION)
                .boLimitationText(LIMITATION_TEXT)
                .ihtReferenceNumber(IHT_REFERENCE)
                .ihtFormCompletedOnline(IHT_ONLINE)
                .payments(PAYMENTS_LIST)
                .boExaminationChecklistQ1(YES)
                .boExaminationChecklistQ2(YES)
                .boExaminationChecklistRequestQA(YES)
                .scannedDocuments(SCANNED_DOCUMENTS_LIST)
                .recordId(RECORD_ID)
                .legacyType(LEGACY_CASE_TYPE)
                .orderNeeded(ORDER_NEEDED)
                .reissueReason(REISSUE_REASON)
                .reissueDate(REISSUE_DATE)
                .reissueReasonNotation(REISSUE_NOTATION)
                .legacyCaseViewUrl(LEGACY_CASE_URL)
                .deceasedDivorcedInEnglandOrWales(DECEASED_DIVORCED_IN_ENGLAND_OR_WALES)
                .primaryApplicantAdoptionInEnglandOrWales(PRIMARY_APPLICANT_ADOPTION_IN_ENGLAND_OR_WALES)
                .deceasedSpouseNotApplyingReason(DECEASED_SPOUSE_NOT_APPLYING_REASON)
                .deceasedOtherChildren(DECEASED_OTHER_CHILDREN)
                .allDeceasedChildrenOverEighteen(ALL_DECEASED_CHILDREN_OVER_EIGHTEEN)
                .anyDeceasedChildrenDieBeforeDeceased(ANY_DECEASED_CHILDREN_DIE_BEFORE_DECEASED)
                .anyDeceasedGrandChildrenUnderEighteen(ANY_DECEASED_GRANDCHILDREN_UNDER_EIGHTEEN)
                .deceasedAnyChildren(DECEASED_ANY_CHILDREN)
                .deceasedHasAssetsOutsideUK(DECEASED_HAS_ASSETS_OUTSIDE_UK)
                .statementOfTruthDocument(SOT);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
    }

    @Test
    public void shouldConvertRequestToDataBeanForWithStateChange() {
        CallbackResponse callbackResponse = underTest.transformWithConditionalStateChange(callbackRequestMock, CHANGED_STATE);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertTrue(CHANGED_STATE.isPresent());
        assertEquals(CHANGED_STATE.get(), callbackResponse.getData().getState());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithNoStateChange() {
        CallbackResponse callbackResponse = underTest.transformWithConditionalStateChange(callbackRequestMock, ORIGINAL_STATE);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertNull(callbackResponse.getData().getState());
    }

    @Test
    public void shouldConvertRequestToDataBeanForPaymentWithExecutorDetails() {

        when(documentLinkMock.getDocumentBinaryUrl()).thenReturn(DOC_BINARY_URL);
        when(documentLinkMock.getDocumentUrl()).thenReturn(DOC_URL);
        when(documentLinkMock.getDocumentFilename()).thenReturn(DOC_NAME);
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(LEGAL_STATEMENT_PROBATE)
                .build();

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock, document);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(DOC_BINARY_URL, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentBinaryUrl());
        assertEquals(DOC_URL, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentUrl());
        assertEquals(DOC_NAME, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentFilename());
        assertNull(callbackResponse.getData().getSolsSOTNeedToUpdate());
    }

    @Test
    public void shouldConvertRequestToDataBeanForPaymentWithLegalStatementDocNullWhenPdfServiceTemplateIsNull() {
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .build();
        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock, document);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertNull(callbackResponse.getData().getSolsLegalStatementDocument());
    }

    @Test
    public void shouldAddDigitalGrantDraftToGeneratedDocuments() {
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(DIGITAL_GRANT_DRAFT)
                .build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock, Arrays.asList(document), null, null);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldConvertRequestToDataBeanForPaymentWithFeeAccount() {
        CaseData caseData = caseDataBuilder.solsPaymentMethods(SOL_PAY_METHODS_FEE)
                .solsFeeAccountNumber(FEE_ACCT_NUMBER)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);

        when(feeServiceResponseMock.getFeeForNonUkCopies()).thenReturn(feeForNonUkCopies);
        when(feeServiceResponseMock.getFeeForUkCopies()).thenReturn(feeForUkCopies);
        when(feeServiceResponseMock.getApplicationFee()).thenReturn(applicationFee);
        when(feeServiceResponseMock.getTotal()).thenReturn(totalFee);

        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feeServiceResponseMock);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(TOTAL_FEE, callbackResponse.getData().getTotalFee());
        assertEquals(SOL_PAY_METHODS_FEE, callbackResponse.getData().getSolsPaymentMethods());
        assertEquals(FEE_ACCT_NUMBER, callbackResponse.getData().getSolsFeeAccountNumber());
        assertEquals(PAY_REF_FEE, callbackResponse.getData().getPaymentReferenceNumber());
    }

    @Test
    public void shouldTestForNullDOB() {
        CaseData caseData = caseDataBuilder.deceasedDateOfBirth(null)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);

        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feeServiceResponseMock);

        assertEquals(null, callbackResponse.getData().getDeceasedDateOfBirth());
    }

    @Test
    public void shouldTestForNullDOD() {
        CaseData caseData = caseDataBuilder.deceasedDateOfDeath(null)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);

        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feeServiceResponseMock);

        assertEquals(null, callbackResponse.getData().getDeceasedDateOfDeath());
    }

    @Test
    public void shouldConvertRequestToDataBeanForPaymentWithCheque() {
        CaseData caseData = caseDataBuilder.solsPaymentMethods(SOL_PAY_METHODS_CHEQUE)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);

        when(feeServiceResponseMock.getFeeForNonUkCopies()).thenReturn(feeForNonUkCopies);
        when(feeServiceResponseMock.getFeeForUkCopies()).thenReturn(feeForUkCopies);
        when(feeServiceResponseMock.getApplicationFee()).thenReturn(applicationFee);
        when(feeServiceResponseMock.getTotal()).thenReturn(totalFee);

        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feeServiceResponseMock);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(TOTAL_FEE, callbackResponse.getData().getTotalFee());
        assertEquals(SOL_PAY_METHODS_CHEQUE, callbackResponse.getData().getSolsPaymentMethods());
        assertNull(callbackResponse.getData().getSolsFeeAccountNumber());
        assertEquals(PAY_REF_CHEQUE, callbackResponse.getData().getPaymentReferenceNumber());
    }

    @Test
    public void shouldAddDocumentsToProbateDocumentsAndNotificationsGenerated() {
        Document grantDocument = Document.builder().documentType(DIGITAL_GRANT).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
                Arrays.asList(grantDocument, grantIssuedSentEmail), null, null);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());

        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail, callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
    }

    @Test
    public void shouldSetSendLetterIdAndPdfSize() {
        Document grantDocument = Document.builder().documentType(DIGITAL_GRANT).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
                Arrays.asList(grantDocument, grantIssuedSentEmail), "abc123", "2");

        assertCommon(callbackResponse);

        assertEquals("2", callbackResponse.getData().getBulkPrintPdfSize());
        assertEquals("abc123", callbackResponse.getData().getBulkPrintSendLetterId());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail, callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());

    }

    @Test
    public void shouldAddDocumentToProbateNotificationsGenerated() {
        Document documentsReceivedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
                Arrays.asList(documentsReceivedSentEmail), null, null);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(documentsReceivedSentEmail, callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
    }

    @Test
    public void shouldAddNoDocumentButSetNotificationRequested() {
        List<Document> documents = new ArrayList<>();

        CaseData caseData = caseDataBuilder
                .solsSolicitorEmail(null)
                .primaryApplicantEmailAddress(null)
                .boEmailDocsReceivedNotificationRequested(NO)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock, documents, null, null);

        assertEquals("No", callbackResponse.getData().getBoEmailDocsReceivedNotification());
    }

    @Test
    public void shouldAddMatches() {
        CaseMatch caseMatch = CaseMatch.builder().build();

        CallbackResponse response = underTest.addMatches(callbackRequestMock, Collections.singletonList(caseMatch));

        assertCommon(response);
        assertLegacyInfo(response);

        assertEquals(1, response.getData().getCaseMatches().size());
        assertEquals(caseMatch, response.getData().getCaseMatches().get(0).getValue());
    }

    @Test
    public void shouldSelectForQA() {
        CallbackResponse response = underTest.selectForQA(callbackRequestMock);

        assertCommon(response);
        assertLegacyInfo(response);

        assertEquals(CallbackResponseTransformer.QA_CASE_STATE, response.getData().getState());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithStopDetailsChange() {
        List<Document> documents = new ArrayList<>();
        documents.add(Document.builder().documentType(CAVEAT_STOPPED).build());

        CallbackResponse callbackResponse = underTest.caseStopped(callbackRequestMock, documents, "123");

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertTrue(callbackResponse.getData().getBoStopDetails().isEmpty());
    }

    @Test
    public void shouldNotIncludeBulkPrintIdWithOtherDocType() {
        List<Document> documents = new ArrayList<>();
        documents.add(Document.builder().documentType(DIGITAL_GRANT).build());

        CallbackResponse callbackResponse = underTest.caseStopped(callbackRequestMock, documents, "123");

        assertThat(callbackResponse.getData().getBulkPrintId(), is(EMPTY_LIST));
    }

    @Test
    public void shouldTransformCallbackRequestToCallbackResponse() {
        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock);

        assertCommon(callbackResponse);
    }

    @Test
    public void shouldTransformPersonalCaseForDeceasedAliasNamesExist() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        List<CollectionMember<ProbateAliasName>> deceasedAliasNamesList = new ArrayList<>();
        deceasedAliasNamesList.add(createdDeceasedAliasName("0", ALIAS_FORENAME, ALIAS_SURNAME, YES));

        caseDataBuilder.deceasedAliasNameList(deceasedAliasNamesList);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.PERSONAL);
        assertEquals(NO, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(1, callbackResponse.getData().getSolsDeceasedAliasNamesList().size());
    }

    @Test
    public void shouldTransformPersonalCaseForEmptyDeceasedNames() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        List<CollectionMember<ProbateAliasName>> deceasedAliasNamesList = new ArrayList<>();

        caseDataBuilder.deceasedAliasNameList(deceasedAliasNamesList);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.PERSONAL);
        assertEquals(NO, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(0, callbackResponse.getData().getSolsDeceasedAliasNamesList().size());
    }

    @Test
    public void shouldTransformPersonalCaseForSolsAdditionalExecsExist() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        List<CollectionMember<AdditionalExecutor>> additionalExecsList = new ArrayList<>();
        additionalExecsList.add(createSolsAdditionalExecutor("0", YES, ""));
        additionalExecsList.add(createSolsAdditionalExecutor("1", NO, STOP_REASON));
        caseDataBuilder.solsAdditionalExecutorList(additionalExecsList);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.PERSONAL);
        assertEquals(NO, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsApplying().size());
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsNotApplying().size());
        assertEquals(2, callbackResponse.getData().getSolsAdditionalExecutorList().size());
        assertEquals(YES, callbackResponse.getData().getSolsAdditionalExecutorList().get(0).getValue().getAdditionalApplying());
        assertEquals(NO, callbackResponse.getData().getSolsAdditionalExecutorList().get(1).getValue().getAdditionalApplying());

    }

    @Test
    public void shouldTransformPersonalCaseForEmptySolsAdditionalExecs() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        caseDataBuilder.solsAdditionalExecutorList(EMPTY_LIST);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.PERSONAL);
        assertEquals(NO, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsApplying().size());
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsNotApplying().size());
        assertEquals(0, callbackResponse.getData().getSolsAdditionalExecutorList().size());
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

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.PERSONAL);
        assertEquals(NO, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(1, callbackResponse.getData().getAdditionalExecutorsApplying().size());
        assertApplyingExecutorDetails(callbackResponse.getData().getAdditionalExecutorsApplying().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getAdditionalExecutorsNotApplying().size());
        assertNotApplyingExecutorDetails(callbackResponse.getData().getAdditionalExecutorsNotApplying().get(0).getValue());
        assertEquals(0, callbackResponse.getData().getSolsAdditionalExecutorList().size());
        assertEquals(YES, callbackResponse.getData().getOtherExecutorExists());
    }

    @Test
    public void shouldTransformCaseForSolicitorWithDeceasedAliasNames() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);

        List<CollectionMember<AliasName>> deceasedAliasNamesList = new ArrayList<>();
        AliasName an11 = AliasName.builder().solsAliasname(SOLS_ALIAS_NAME).build();
        CollectionMember<AliasName> an1 = new CollectionMember<>("0", an11);
        deceasedAliasNamesList.add(an1);
        caseDataBuilder.solsDeceasedAliasNamesList(deceasedAliasNamesList);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(1, callbackResponse.getData().getSolsDeceasedAliasNamesList().size());
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldTransformCaseForSolicitorWithSolsExecsExists() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.recordId(null);
        caseDataBuilder.paperForm("No");

        List<CollectionMember<AdditionalExecutor>> additionalExecsList = new ArrayList<>();
        additionalExecsList.add(createSolsAdditionalExecutor("0", NO, STOP_REASON));
        additionalExecsList.add(createSolsAdditionalExecutor("1", YES, ""));
        additionalExecsList.add(createSolsAdditionalExecutor("2", YES, ""));
        caseDataBuilder.solsAdditionalExecutorList(additionalExecsList);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertEquals(2, callbackResponse.getData().getAdditionalExecutorsApplying().size());
        assertApplyingExecutorDetailsFromSols(callbackResponse.getData().getAdditionalExecutorsApplying().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getAdditionalExecutorsNotApplying().size());
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldTransformCaseForSolicitorWithSolsExecsDontExist() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsAdditionalExecutorList(EMPTY_LIST);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsApplying().size());
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsNotApplying().size());
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldTransformCaseForSolicitorWithPaperFormIsNo() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsAdditionalExecutorList(EMPTY_LIST);
        caseDataBuilder.paperForm(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsApplying().size());
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsNotApplying().size());
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldTransformCaseForSolicitorWithPaperFormIsNull() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsAdditionalExecutorList(EMPTY_LIST);
        caseDataBuilder.paperForm(null);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsApplying().size());
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsNotApplying().size());
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldTransformCaseForSolicitorWithProbate() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsWillType(WILL_TYPE_PROBATE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(YES, callbackResponse.getData().getWillExists());
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldTransformCaseForSolicitorWithIntestacy() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsWillType(WILL_TYPE_INTESTACY);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(NO, callbackResponse.getData().getWillExists());
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldTransformCaseForSolicitorWithAdmon() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsWillType(WILL_TYPE_ADMON);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(YES, callbackResponse.getData().getWillExists());
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldTransformCaseForSolicitorWithCaseTypeIsGOP() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsAdditionalExecutorList(EMPTY_LIST);
        caseDataBuilder.caseType("gop");

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsApplying().size());
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsNotApplying().size());
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldTransformCaseForSolicitorWithCaseTypeIsNull() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsAdditionalExecutorList(EMPTY_LIST);
        caseDataBuilder.caseType(null);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsApplying().size());
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsNotApplying().size());
        assertSolsDetails(callbackResponse);
    }


    @Test
    public void shouldTransformCaseForPAWithIHTOnlineYes() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(YES);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(IHT_REFERENCE, callbackResponse.getData().getIhtReferenceNumber());
    }

    @Test
    public void shouldTransformCaseForPAWithPrimaryApplicantAlias() {
        caseDataBuilder.primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES);
        caseDataBuilder.primaryApplicantSameWillName(YES);
        caseDataBuilder.primaryApplicantAliasReason("Other");
        caseDataBuilder.primaryApplicantOtherReason("Married");

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(YES, callbackResponse.getData().getPrimaryApplicantSameWillName());
        assertEquals(PRIMARY_EXEC_ALIAS_NAMES, callbackResponse.getData().getPrimaryApplicantAlias());
        assertEquals("Other", callbackResponse.getData().getPrimaryApplicantAliasReason());
        assertEquals("Married", callbackResponse.getData().getPrimaryApplicantOtherReason());
    }

    @Test
    public void shouldTransformCaseForPAWithPrimaryApplicantAliasOtherToBeNull() {
        caseDataBuilder.primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES);
        caseDataBuilder.primaryApplicantSameWillName(YES);
        caseDataBuilder.primaryApplicantAliasReason("Marriage");
        caseDataBuilder.primaryApplicantOtherReason("Married");

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(YES, callbackResponse.getData().getPrimaryApplicantSameWillName());
        assertEquals(PRIMARY_EXEC_ALIAS_NAMES, callbackResponse.getData().getPrimaryApplicantAlias());
        assertEquals("Marriage", callbackResponse.getData().getPrimaryApplicantAliasReason());
        assertNull(callbackResponse.getData().getPrimaryApplicantOtherReason());
    }

    @Test
    public void shouldTransformCaseForPAWithApplyExecAlias() {
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecsList = new ArrayList<>();
        additionalExecsList.add(createAdditionalExecutorApplying("0"));
        additionalExecsList.add(createAdditionalExecutorApplying("1"));
        caseDataBuilder.additionalExecutorsApplying(additionalExecsList);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertEquals(2, callbackResponse.getData().getAdditionalExecutorsApplying().size());
    }

    @Test
    public void shouldTransformCaseForPAWithIHTOnlineNo() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(NO);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertNull(callbackResponse.getData().getIhtReferenceNumber());
    }

    @Test
    public void shouldTransformCaseForWhenPaperFormIsNO() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(NO);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);
        caseDataBuilder.paperForm(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getIhtReferenceNumber());
    }

    @Test
    public void shouldTransformCaseForWhenPaperFormIsNull() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(NO);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);
        caseDataBuilder.paperForm(null);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getIhtReferenceNumber());
    }

    @Test
    public void shouldTransformCaseForWhenCaseTypeIsGOP() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(NO);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);
        caseDataBuilder.caseType(CASE_TYPE_GRANT_OF_PROBATE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getIhtReferenceNumber());
        assertEquals(CASE_TYPE_GRANT_OF_PROBATE, callbackResponse.getData().getCaseType());
    }

    @Test
    public void shouldTransformCaseForWhenCaseTypeIsNull() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(NO);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);
        caseDataBuilder.caseType(null);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getIhtReferenceNumber());
    }

    @Test
    public void shouldGetUploadedDocuments() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        List<CollectionMember<UploadDocument>> documents = new ArrayList<>();
        documents.add(createUploadDocuments("0"));
        caseDataBuilder.boDocumentsUploaded(documents);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.SOLICITOR);
        assertLegacyInfo(callbackResponse);
        assertEquals(1, callbackResponse.getData().getBoDocumentsUploaded().size());
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldGetPaperApplication() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        List<CollectionMember<EstateItem>> estate = new ArrayList<>();
        estate.add(createEstateItems("0"));
        List<CollectionMember<AttorneyApplyingOnBehalfOf>> attorneyList = new ArrayList<>();
        attorneyList.add(createAttorneyApplyingList("0"));
        List<CollectionMember<AdoptedRelative>> adoptedRelatives = new ArrayList<>();
        adoptedRelatives.add(createAdoptiveRelativeList("0"));
        caseDataBuilder
                .primaryApplicantSecondPhoneNumber(EXEC_PHONE)
                .primaryApplicantRelationshipToDeceased("other")
                .paRelationshipToDeceasedOther("cousin")
                .deceasedMaritalStatus("neverMarried")
                .willDatedBeforeApril(YES)
                .deceasedEnterMarriageOrCP(NO)
                .dateOfMarriageOrCP(null)
                .dateOfDivorcedCPJudicially(null)
                .willsOutsideOfUK(YES)
                .courtOfDecree("Random Court Name")
                .willGiftUnderEighteen(NO)
                .applyingAsAnAttorney(YES)
                .attorneyOnBehalfOfNameAndAddress(null)
                .mentalCapacity(YES)
                .courtOfProtection(YES)
                .epaOrLpa(NO)
                .epaRegistered(NO)
                .domicilityCountry("Spain")
                .ukEstateItems(estate)
                .attorneyOnBehalfOfNameAndAddress(attorneyList)
                .adopted(YES)
                .adoptiveRelatives(adoptedRelatives)
                .domicilityIHTCert(YES)
                .entitledToApply(YES)
                .entitledToApplyOther(YES)
                .notifiedApplicants(YES)
                .foreignAsset(YES)
                .foreignAssetEstateValue("123")
                .caseType(CASE_TYPE_INTESTACY)
                .spouseOrPartner(NO)
                .childrenSurvived(YES)
                .childrenOverEighteenSurvived(NUM_CODICILS)
                .childrenUnderEighteenSurvived(NUM_CODICILS)
                .childrenDied(YES)
                .childrenDiedOverEighteen(NUM_CODICILS)
                .childrenDiedUnderEighteen(NUM_CODICILS)
                .grandChildrenSurvived(YES)
                .grandChildrenSurvivedOverEighteen(NUM_CODICILS)
                .grandChildrenSurvivedUnderEighteen(NUM_CODICILS)
                .parentsExistSurvived(YES)
                .parentsExistOverEighteenSurvived(NUM_CODICILS)
                .parentsExistUnderEighteenSurvived(NUM_CODICILS)
                .wholeBloodSiblingsSurvived(YES)
                .wholeBloodSiblingsSurvivedOverEighteen(NUM_CODICILS)
                .wholeBloodSiblingsSurvivedUnderEighteen(NUM_CODICILS)
                .wholeBloodSiblingsDied(YES)
                .wholeBloodSiblingsDiedOverEighteen(NUM_CODICILS)
                .wholeBloodSiblingsDiedUnderEighteen(NUM_CODICILS)
                .wholeBloodNeicesAndNephews(YES)
                .wholeBloodNeicesAndNephewsOverEighteen(NUM_CODICILS)
                .wholeBloodNeicesAndNephewsUnderEighteen(NUM_CODICILS)
                .halfBloodSiblingsSurvived(YES)
                .halfBloodSiblingsSurvivedOverEighteen(NUM_CODICILS)
                .halfBloodSiblingsSurvivedUnderEighteen(NUM_CODICILS)
                .halfBloodSiblingsDied(YES)
                .halfBloodSiblingsDiedOverEighteen(NUM_CODICILS)
                .halfBloodSiblingsDiedUnderEighteen(NUM_CODICILS)
                .halfBloodNeicesAndNephews(YES)
                .halfBloodNeicesAndNephewsOverEighteen(NUM_CODICILS)
                .halfBloodNeicesAndNephewsUnderEighteen(NUM_CODICILS)
                .grandparentsDied(YES)
                .grandparentsDiedOverEighteen(NUM_CODICILS)
                .grandparentsDiedUnderEighteen(NUM_CODICILS)
                .wholeBloodUnclesAndAuntsSurvived(YES)
                .wholeBloodUnclesAndAuntsSurvivedOverEighteen(NUM_CODICILS)
                .wholeBloodUnclesAndAuntsSurvivedUnderEighteen(NUM_CODICILS)
                .wholeBloodUnclesAndAuntsDied(YES)
                .wholeBloodUnclesAndAuntsDiedOverEighteen(NUM_CODICILS)
                .wholeBloodUnclesAndAuntsDiedUnderEighteen(NUM_CODICILS)
                .wholeBloodCousinsSurvived(YES)
                .wholeBloodCousinsSurvivedOverEighteen(NUM_CODICILS)
                .wholeBloodCousinsSurvivedUnderEighteen(NUM_CODICILS)
                .halfBloodUnclesAndAuntsSurvived(YES)
                .halfBloodUnclesAndAuntsSurvivedOverEighteen(NUM_CODICILS)
                .halfBloodUnclesAndAuntsSurvivedUnderEighteen(NUM_CODICILS)
                .halfBloodUnclesAndAuntsDied(YES)
                .halfBloodUnclesAndAuntsDiedOverEighteen(NUM_CODICILS)
                .halfBloodUnclesAndAuntsDiedUnderEighteen(NUM_CODICILS)
                .halfBloodCousinsSurvived(YES)
                .halfBloodCousinsSurvivedOverEighteen(NUM_CODICILS)
                .halfBloodCousinsSurvivedUnderEighteen(NUM_CODICILS)
                .applicationFeePaperForm("0")
                .feeForCopiesPaperForm("0")
                .totalFeePaperForm("0")
                .scannedDocuments(SCANNED_DOCUMENTS_LIST)
                .paperPaymentMethod("debitOrCredit")
                .paymentReferenceNumberPaperform(IHT_REFERENCE)
                .paperForm(YES)
                .dateOfDeathType(DECEASED_DATE_OF_DEATH_TYPE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock);
        assertEquals(1, callbackResponse.getData().getUkEstateItems().size());
        assertEquals(1, callbackResponse.getData().getAttorneyOnBehalfOfNameAndAddress().size());
        assertEquals(1, callbackResponse.getData().getScannedDocuments().size());
        assertEquals(1, callbackResponse.getData().getAdoptiveRelatives().size());

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertCommonPaperForm(callbackResponse);
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldTransoformCaseWithScannedDocuments() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.scannedDocuments(SCANNED_DOCUMENTS_LIST);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);
        assertEquals(1, callbackResponse.getData().getScannedDocuments().size());
        assertEquals(SCANNED_DOCUMENTS_LIST, callbackResponse.getData().getScannedDocuments());
    }

    @Test
    public void shouldDefualtYesToBulkPrint() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock);
        assertEquals("Yes", callbackResponse.getData().getBoSendToBulkPrintRequested());
        assertEquals("Yes", callbackResponse.getData().getBoSendToBulkPrint());
    }

    @Test
    public void shouldDefualtSolicitorsInfoToNull() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock);
        assertEquals(null, callbackResponse.getData().getSolsSolicitorAppReference());
        assertEquals(null, callbackResponse.getData().getSolsSolicitorEmail());
        assertEquals(null, callbackResponse.getData().getSolsSOTJobTitle());
        assertEquals(null, callbackResponse.getData().getSolsSOTName());
        assertEquals(null, callbackResponse.getData().getSolsSolicitorAddress());
        assertEquals(null, callbackResponse.getData().getSolsSolicitorFirmName());
        assertEquals(null, callbackResponse.getData().getSolsSolicitorPhoneNumber());
    }

    @Test
    public void shouldSetSolicitorsInfoWhenApplicationTypeIsNull() {
        caseDataBuilder.applicationType(null);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock);
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldSetSolicitorsInfoWhenApplicationTypeIht() {
        caseDataBuilder.ihtReferenceNumber("123456");

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock);
        assertEquals(IHT_FORM_ID, callbackResponse.getData().getIhtFormId());
        assertSolsDetails(callbackResponse);
    }

    @Test
    public void shouldSetSolicitorsInfoWhenApplicationTypeIhtIsNull() {
        CaseData.CaseDataBuilder caseDataBuilder2;
        caseDataBuilder2 = CaseData.builder().ihtReferenceNumber(null);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder2.build(), LAST_MODIFIED_STR, 1L);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        CallbackResponse callbackResponse = underTest.paperForm(callbackRequest);
        assertNull(callbackResponse.getData().getIhtFormId());
    }

    @Test
    public void shouldSetSolicitorsInfoWhenApplicationTypeIhtIsEmpty() {
        CaseData.CaseDataBuilder caseDataBuilder2;
        caseDataBuilder2 = CaseData.builder().ihtReferenceNumber("");

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder2.build(), LAST_MODIFIED_STR, 1L);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        CallbackResponse callbackResponse = underTest.paperForm(callbackRequest);
        assertNull(callbackResponse.getData().getIhtFormId());
    }

    @Test
    public void shouldSetGrantIssuedDate() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(DIGITAL_GRANT)
                .build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        DateFormat targetFormat = new SimpleDateFormat(DATE_FORMAT);
        String grantIssuedDate = targetFormat.format(new Date());
        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock, Arrays.asList(document), null, null);
        assertEquals(grantIssuedDate, callbackResponse.getData().getGrantIssuedDate());
    }

    @Test
    public void shouldSetGrantReissuedDateAtReissue() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(DIGITAL_GRANT_REISSUE)
                .build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        DateFormat targetFormat = new SimpleDateFormat(DATE_FORMAT);
        String latestReissueDate = targetFormat.format(new Date());
        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock, Arrays.asList(document), null, null);
        assertEquals(latestReissueDate, callbackResponse.getData().getLatestGrantReissueDate());
    }

    @Test
    public void shouldSetDateOfDeathType() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock);
        assertEquals("diedOn", callbackResponse.getData().getDateOfDeathType());
    }

    @Test
    public void shouldSetSOT() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock);
        assertEquals("SOT.pdf", callbackResponse.getData().getStatementOfTruthDocument().getDocumentFilename());
    }

    private CollectionMember<ProbateAliasName> createdDeceasedAliasName(String id, String forename, String lastname, String onGrant) {
        ProbateAliasName pan = ProbateAliasName.builder()
                .appearOnGrant(onGrant)
                .forenames(forename)
                .lastName(lastname)
                .build();
        return new CollectionMember<>(id, pan);
    }

    private CollectionMember<UploadDocument> createUploadDocuments(String id) {
        DocumentLink docLink = DocumentLink.builder()
                .documentBinaryUrl("")
                .documentFilename("")
                .documentUrl("")
                .build();

        UploadDocument doc = UploadDocument.builder()
                .comment("comment")
                .documentLink(docLink)
                .documentType(DocumentType.IHT).build();
        return new CollectionMember<>(id, doc);
    }

    private CollectionMember<EstateItem> createEstateItems(String id) {
        EstateItem items = EstateItem.builder()
                .item("")
                .value("")
                .build();

        return new CollectionMember<>(id, items);
    }

    private CollectionMember<AdoptedRelative> createAdoptiveRelativeList(String id) {
        AdoptedRelative relatives = AdoptedRelative.builder()
                .adoptedInOrOut("IN")
                .name("Jane Doe")
                .relationship("Sister")
                .build();
        return new CollectionMember<>(id, relatives);
    }

    private CollectionMember<AttorneyApplyingOnBehalfOf> createAttorneyApplyingList(String id) {
        SolsAddress address = SolsAddress.builder()
                .addressLine1("")
                .addressLine2("")
                .addressLine3("")
                .postTown("")
                .postCode("")
                .county("")
                .country("")
                .build();

        AttorneyApplyingOnBehalfOf list = AttorneyApplyingOnBehalfOf.builder()
                .address(address)
                .name("")
                .build();
        return new CollectionMember<>(id, list);
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

    private void assertCommon(CallbackResponse callbackResponse) {
        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertCommonAdditionalExecutors(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.SOLICITOR);
        assertEquals(APPLICANT_HAS_ALIAS, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(OTHER_EXECS_EXIST, callbackResponse.getData().getOtherExecutorExists());
    }

    private void assertSolsDetails(CallbackResponse callbackResponse) {
        assertEquals(SOLICITOR_FIRM_NAME, callbackResponse.getData().getSolsSolicitorFirmName());
        assertEquals(SOLICITOR_FIRM_LINE1, callbackResponse.getData().getSolsSolicitorAddress().getAddressLine1());
        assertEquals(SOLICITOR_FIRM_POSTCODE, callbackResponse.getData().getSolsSolicitorAddress().getPostCode());
        assertEquals(SOLICITOR_FIRM_EMAIL, callbackResponse.getData().getSolsSolicitorEmail());
        assertEquals(SOLICITOR_FIRM_PHONE, callbackResponse.getData().getSolsSolicitorPhoneNumber());
        assertEquals(SOLICITOR_SOT_NAME, callbackResponse.getData().getSolsSOTName());
        assertEquals(SOLICITOR_SOT_JOB_TITLE, callbackResponse.getData().getSolsSOTJobTitle());
        assertEquals(APP_REF, callbackResponse.getData().getSolsSolicitorAppReference());

    }

    private void assertCommonDetails(CallbackResponse callbackResponse) {
        assertEquals(REGISTRY_LOCATION, callbackResponse.getData().getRegistryLocation());

        assertEquals(DECEASED_FIRSTNAME, callbackResponse.getData().getDeceasedForenames());
        assertEquals(DECEASED_LASTNAME, callbackResponse.getData().getDeceasedSurname());
        assertEquals("2016-12-31", callbackResponse.getData().getDeceasedDateOfBirth());
        assertEquals("2017-12-31", callbackResponse.getData().getDeceasedDateOfDeath());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWillNumberOfCodicils());

        assertEquals(IHT_FORM_ID, callbackResponse.getData().getIhtFormId());
        Assert.assertThat(new BigDecimal("10000"), comparesEqualTo(callbackResponse.getData().getIhtGrossValue()));
        Assert.assertThat(new BigDecimal("9000"), comparesEqualTo(callbackResponse.getData().getIhtNetValue()));

        assertEquals(APPLICANT_FORENAME, callbackResponse.getData().getPrimaryApplicantForenames());
        assertEquals(APPLICANT_SURNAME, callbackResponse.getData().getPrimaryApplicantSurname());
        assertEquals(APPLICANT_EMAIL_ADDRESS, callbackResponse.getData().getPrimaryApplicantEmailAddress());
        assertEquals(PRIMARY_EXEC_APPLYING, callbackResponse.getData().getPrimaryApplicantIsApplying());
        assertEquals(PRIMARY_EXEC_ALIAS_NAMES, callbackResponse.getData().getPrimaryApplicantAlias());
        assertEquals(DECEASED_ADDRESS, callbackResponse.getData().getDeceasedAddress());
        assertEquals(EXEC_ADDRESS, callbackResponse.getData().getPrimaryApplicantAddress());
        assertEquals(ADDITIONAL_INFO, callbackResponse.getData().getSolsAdditionalInfo());

        assertEquals(BO_DOCS_RECEIVED, callbackResponse.getData().getBoEmailDocsReceivedNotificationRequested());
        assertEquals(BO_EMAIL_GRANT_ISSUED, callbackResponse.getData().getBoEmailGrantIssuedNotificationRequested());
        assertEquals(CASE_PRINT, callbackResponse.getData().getCasePrinted());
        assertEquals(CAVEAT_STOP_NOTIFICATION, callbackResponse.getData().getBoCaveatStopNotificationRequested());
        assertEquals(CAVEAT_STOP_NOTIFICATION, callbackResponse.getData().getBoCaveatStopEmailNotification());
        assertEquals(CASE_STOP_CAVEAT_ID, callbackResponse.getData().getBoCaseStopCaveatId());
        assertEquals(CAVEAT_STOP_EMAIL_NOTIFICATION, callbackResponse.getData().getBoCaveatStopEmailNotificationRequested());
        assertEquals(CAVEAT_STOP_SEND_TO_BULK_PRINT, callbackResponse.getData().getBoCaveatStopSendToBulkPrintRequested());
        assertEquals(STOP_REASONS_LIST, callbackResponse.getData().getBoCaseStopReasonList());
        assertEquals(STOP_DETAILS, callbackResponse.getData().getBoStopDetails());

        assertEquals(DECEASED_TITLE, callbackResponse.getData().getBoDeceasedTitle());
        assertEquals(DECEASED_HONOURS, callbackResponse.getData().getBoDeceasedHonours());

        assertEquals(WILL_MESSAGE, callbackResponse.getData().getBoWillMessage());
        assertEquals(EXECUTOR_LIMITATION, callbackResponse.getData().getBoExecutorLimitation());
        assertEquals(ADMIN_CLAUSE_LIMITATION, callbackResponse.getData().getBoAdminClauseLimitation());
        assertEquals(LIMITATION_TEXT, callbackResponse.getData().getBoLimitationText());

        assertEquals(IHT_REFERENCE, callbackResponse.getData().getIhtReferenceNumber());
        assertEquals(IHT_ONLINE, callbackResponse.getData().getIhtFormCompletedOnline());

        assertEquals(PAYMENTS_LIST, callbackResponse.getData().getPayments());
        assertEquals(YES, callbackResponse.getData().getBoExaminationChecklistQ1());
        assertEquals(YES, callbackResponse.getData().getBoExaminationChecklistQ2());
        assertEquals(YES, callbackResponse.getData().getBoExaminationChecklistRequestQA());
        assertEquals(ORDER_NEEDED, callbackResponse.getData().getOrderNeeded());
        assertEquals(REISSUE_REASON, callbackResponse.getData().getReissueReason());
        assertEquals(REISSUE_DATE, callbackResponse.getData().getReissueDate());
        assertEquals(REISSUE_NOTATION, callbackResponse.getData().getReissueReasonNotation());

        assertEquals(SCANNED_DOCUMENTS_LIST, callbackResponse.getData().getScannedDocuments());
    }

    private void assertLegacyInfo(CallbackResponse callbackResponse) {
        assertEquals(RECORD_ID, callbackResponse.getData().getRecordId());
        assertEquals(LEGACY_CASE_TYPE, callbackResponse.getData().getLegacyType());
        assertEquals(LEGACY_CASE_URL, callbackResponse.getData().getLegacyCaseViewUrl());
    }

    private void assertApplicationType(CallbackResponse callbackResponse, ApplicationType applicationType) {
        assertEquals(applicationType, callbackResponse.getData().getApplicationType());
    }

    private void assertCommonAdditionalExecutors(CallbackResponse callbackResponse) {
        assertEquals(emptyList(), callbackResponse.getData().getSolsAdditionalExecutorList());
        assertEquals(emptyList(), callbackResponse.getData().getAdditionalExecutorsApplying());
        assertEquals(emptyList(), callbackResponse.getData().getAdditionalExecutorsNotApplying());
    }

    private void assertCommonPaperForm(CallbackResponse callbackResponse) {
        assertEquals(EXEC_PHONE, callbackResponse.getData().getPrimaryApplicantSecondPhoneNumber());
        assertEquals("other", callbackResponse.getData().getPrimaryApplicantRelationshipToDeceased());
        assertEquals("cousin", callbackResponse.getData().getPaRelationshipToDeceasedOther());
        assertEquals("neverMarried", callbackResponse.getData().getDeceasedMaritalStatus());

        assertEquals(YES, callbackResponse.getData().getWillDatedBeforeApril());
        assertEquals(NO, callbackResponse.getData().getDeceasedEnterMarriageOrCP());
        assertEquals(null, callbackResponse.getData().getDateOfMarriageOrCP());
        assertEquals(null, callbackResponse.getData().getDateOfDivorcedCPJudicially());
        assertEquals(YES, callbackResponse.getData().getWillsOutsideOfUK());
        assertEquals("Random Court Name", callbackResponse.getData().getCourtOfDecree());
        assertEquals(NO, callbackResponse.getData().getWillGiftUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getApplyingAsAnAttorney());
        assertEquals(YES, callbackResponse.getData().getMentalCapacity());
        assertEquals(YES, callbackResponse.getData().getCourtOfProtection());
        assertEquals(NO, callbackResponse.getData().getEpaOrLpa());

        assertEquals(NO, callbackResponse.getData().getEpaRegistered());
        assertEquals("Spain", callbackResponse.getData().getDomicilityCountry());
        assertEquals(NO, callbackResponse.getData().getSpouseOrPartner());

        assertEquals(YES, callbackResponse.getData().getChildrenSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getChildrenOverEighteenSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getChildrenUnderEighteenSurvived());
        assertEquals(YES, callbackResponse.getData().getChildrenDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getChildrenDiedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getChildrenDiedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getGrandChildrenSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getGrandChildrenSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getGrandChildrenSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getParentsExistSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getParentsExistOverEighteenSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getParentsExistUnderEighteenSurvived());
        assertEquals(YES, callbackResponse.getData().getWholeBloodSiblingsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodSiblingsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodSiblingsSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getWholeBloodSiblingsDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodSiblingsDiedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodSiblingsDiedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getWholeBloodNeicesAndNephews());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodNeicesAndNephewsOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodNeicesAndNephewsUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodSiblingsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodSiblingsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodSiblingsSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodSiblingsDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodSiblingsDiedUnderEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodSiblingsDiedOverEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodNeicesAndNephews());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodNeicesAndNephewsUnderEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodNeicesAndNephewsOverEighteen());
        assertEquals(YES, callbackResponse.getData().getGrandparentsDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getGrandparentsDiedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getGrandparentsDiedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getWholeBloodUnclesAndAuntsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodUnclesAndAuntsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodUnclesAndAuntsSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getWholeBloodUnclesAndAuntsDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodUnclesAndAuntsDiedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodUnclesAndAuntsDiedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getWholeBloodCousinsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodCousinsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodCousinsSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodUnclesAndAuntsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodUnclesAndAuntsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodUnclesAndAuntsSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodUnclesAndAuntsDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodUnclesAndAuntsSurvivedUnderEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodUnclesAndAuntsSurvivedOverEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodCousinsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodCousinsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodCousinsSurvivedUnderEighteen());

        assertEquals(YES, callbackResponse.getData().getPaperForm());
        assertEquals(IHT_REFERENCE, callbackResponse.getData().getPaymentReferenceNumberPaperform());
        assertEquals("debitOrCredit", callbackResponse.getData().getPaperPaymentMethod());
        assertEquals("0", callbackResponse.getData().getApplicationFeePaperForm());
        assertEquals("0", callbackResponse.getData().getFeeForCopiesPaperForm());
        assertEquals("0", callbackResponse.getData().getTotalFeePaperForm());
        assertEquals(YES, callbackResponse.getData().getAdopted());
        assertEquals(YES, callbackResponse.getData().getDomicilityIHTCert());
        assertEquals(YES, callbackResponse.getData().getEntitledToApply());
        assertEquals(YES, callbackResponse.getData().getEntitledToApplyOther());
        assertEquals(YES, callbackResponse.getData().getNotifiedApplicants());
        assertEquals(YES, callbackResponse.getData().getForeignAsset());
        assertEquals("123", callbackResponse.getData().getForeignAssetEstateValue());
        assertEquals(CASE_TYPE_INTESTACY, callbackResponse.getData().getCaseType());
        assertEquals(DECEASED_DATE_OF_DEATH_TYPE, callbackResponse.getData().getDateOfDeathType());

        assertEquals(DECEASED_DIVORCED_IN_ENGLAND_OR_WALES, callbackResponse.getData().getDeceasedDivorcedInEnglandOrWales());
        assertEquals(PRIMARY_APPLICANT_ADOPTION_IN_ENGLAND_OR_WALES,
                callbackResponse.getData().getPrimaryApplicantAdoptionInEnglandOrWales());
        assertEquals(DECEASED_SPOUSE_NOT_APPLYING_REASON, callbackResponse.getData().getDeceasedSpouseNotApplyingReason());
        assertEquals(DECEASED_OTHER_CHILDREN, callbackResponse.getData().getDeceasedOtherChildren());
        assertEquals(ALL_DECEASED_CHILDREN_OVER_EIGHTEEN, callbackResponse.getData().getAllDeceasedChildrenOverEighteen());
        assertEquals(ANY_DECEASED_GRANDCHILDREN_UNDER_EIGHTEEN,
                callbackResponse.getData().getAnyDeceasedGrandChildrenUnderEighteen());
        assertEquals(ANY_DECEASED_CHILDREN_DIE_BEFORE_DECEASED,
                callbackResponse.getData().getAnyDeceasedChildrenDieBeforeDeceased());
        assertEquals(DECEASED_ANY_CHILDREN, callbackResponse.getData().getDeceasedAnyChildren());
        assertEquals(DECEASED_HAS_ASSETS_OUTSIDE_UK, callbackResponse.getData().getDeceasedHasAssetsOutsideUK());

    }

}
