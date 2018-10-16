package uk.gov.hmcts.probate.transformer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;

@RunWith(MockitoJUnitRunner.class)
public class CallbackResponseTransformerTest {

    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final String WILL_MESSAGE = "Will message";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final ApplicationType APPLICATION_TYPE = SOLICITOR;
    private static final String REGISTRY_LOCATION = "Birmingham";

    private static final String SOLICITOR_FIRM_NAME = "Sol Firm Name";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW13 6EA";
    private static final String SOLICITOR_FIRM_EMAIL = "sol@email.com";
    private static final String SOLICITOR_FIRM_PHONE = "0123456789";
    private static final String SOLICITOR_SOT_NAME = "Andy Test";
    private static final String SOLICITOR_SOT_JOB_TITLE = "Lawyer";

    private static final String DECEASED_FIRSTNAME = "Firstname";
    private static final String DECEASED_LASTNAME = "Lastname";
    private static final LocalDate DOB = LocalDate.parse("2016-12-31", dateTimeFormatter);
    private static final LocalDate DOD = LocalDate.parse("2017-12-31", dateTimeFormatter);
    private static final String NUM_CODICILS = "9";

    private static final String IHT_FORM_ID = "IHT207";
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

    private static final String BO_EMAIL_GRANT_ISSUED = YES;
    private static final String BO_DOCS_RECEIVED = YES;
    private static final String CASE_PRINT = YES;
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

    private static final List<CollectionMember<Payment>> PAYMENTS_LIST = Arrays.asList(
            new CollectionMember("id",
                    Payment.builder()
                            .amount("100")
                            .date("20/09/2018")
                            .method("online")
                            .reference("Reference-123")
                            .status("Success")
                            .siteId("SiteId-123")
                            .transactionId("TransactionId-123")
                            .build()));

    @InjectMocks
    private CallbackResponseTransformer underTest;

    @Mock
    private StateChangeService stateChangeServiceMock;

    @Mock
    private CallbackRequest callbackRequestMock;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private Document document;

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
                .solsSolicitorFirmPostcode(SOLICITOR_FIRM_POSTCODE)
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
                .casePrinted(CASE_PRINT)
                .boCaseStopReasonList(STOP_REASONS_LIST)
                .boStopDetails(STOP_DETAILS)
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
                .boExaminationChecklistRequestQA(YES);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
    }

    @Test
    public void shouldConvertRequestToDataBeanForWithStateChange() {
        when(stateChangeServiceMock.getChangedStateForCaseUpdate(caseDataBuilder.build())).thenReturn(CHANGED_STATE);

        CallbackResponse callbackResponse = underTest.transformWithConditionalStateChange(callbackRequestMock, CHANGED_STATE);

        assertCommon(callbackResponse);

        assertTrue(CHANGED_STATE.isPresent());
        assertEquals(CHANGED_STATE.get(), callbackResponse.getData().getState());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithNoStateChange() {
        CallbackResponse callbackResponse = underTest.transformWithConditionalStateChange(callbackRequestMock, ORIGINAL_STATE);

        assertCommon(callbackResponse);

        assertNull(callbackResponse.getData().getState());
    }

    @Test
    public void shouldConvertRequestToDataBeanForPaymentWithExecutorDetails() {

        when(documentLinkMock.getDocumentBinaryUrl()).thenReturn(DOC_BINARY_URL);
        when(documentLinkMock.getDocumentUrl()).thenReturn(DOC_URL);
        when(documentLinkMock.getDocumentFilename()).thenReturn(DOC_NAME);
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(LEGAL_STATEMENT)
                .build();

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock, document);

        assertCommon(callbackResponse);

        assertEquals(DOC_BINARY_URL, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentBinaryUrl());
        assertEquals(DOC_URL, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentUrl());
        assertEquals(DOC_NAME, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentFilename());
        assertNull(callbackResponse.getData().getSolsSOTNeedToUpdate());
    }

    @Test
    public void shouldConvertRequestToDataBeanForPaymentWithLegalStatementDocNullWhenPdfServiceTemplateIsNull() {

        when(documentLinkMock.getDocumentBinaryUrl()).thenReturn(DOC_BINARY_URL);
        when(documentLinkMock.getDocumentUrl()).thenReturn(DOC_URL);
        when(documentLinkMock.getDocumentFilename()).thenReturn(DOC_NAME);
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .build();
        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock, document);

        assertCommon(callbackResponse);

        assertNull(callbackResponse.getData().getSolsLegalStatementDocument());
    }

    @Test
    public void shouldAddDigitalGrantDraftToGeneratedDocuments() {
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(DIGITAL_GRANT_DRAFT)
                .build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock, Arrays.asList(document));

        assertCommon(callbackResponse);

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

        assertEquals(TOTAL_FEE, callbackResponse.getData().getTotalFee());
        assertEquals(SOL_PAY_METHODS_FEE, callbackResponse.getData().getSolsPaymentMethods());
        assertEquals(FEE_ACCT_NUMBER, callbackResponse.getData().getSolsFeeAccountNumber());
        assertEquals(PAY_REF_FEE, callbackResponse.getData().getPaymentReferenceNumber());
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
                Arrays.asList(grantDocument, grantIssuedSentEmail));

        assertCommon(callbackResponse);

        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());

        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail, callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
    }

    @Test
    public void shouldAddDocumentToProbateNotificationsGenerated() {
        Document documentsReceivedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock, Arrays.asList(documentsReceivedSentEmail));

        assertCommon(callbackResponse);

        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(documentsReceivedSentEmail, callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithStopDetailsChange() {
        CallbackResponse callbackResponse = underTest.caseStopped(callbackRequestMock, document);

        assertCommon(callbackResponse);

        assertTrue(callbackResponse.getData().getBoStopDetails().isEmpty());
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
        assertEquals(1, callbackResponse.getData().getSolsDeceasedAliasNamesList().size());
    }

    @Test
    public void shouldTransformCaseForSolicitorWithSolsExecsExists() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);

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
    }

    @Test
    public void shouldTransformCaseForSolicitorWithSolsExecsDontExist() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsAdditionalExecutorList(EMPTY_LIST);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsApplying().size());
        assertEquals(0, callbackResponse.getData().getAdditionalExecutorsNotApplying().size());
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
        assertEquals(null, callbackResponse.getData().getPrimaryApplicantOtherReason());
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
        assertEquals(1, callbackResponse.getData().getBoDocumentsUploaded().size());
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
        assertEquals("Married",  exec.getApplyingExecutorOtherReason());
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
        assertCommonAdditionalExecutors(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.SOLICITOR);
        assertEquals(APPLICANT_HAS_ALIAS, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(OTHER_EXECS_EXIST, callbackResponse.getData().getOtherExecutorExists());
    }

    private void assertCommonDetails(CallbackResponse callbackResponse) {
        assertEquals(REGISTRY_LOCATION, callbackResponse.getData().getRegistryLocation());

        assertEquals(SOLICITOR_FIRM_NAME, callbackResponse.getData().getSolsSolicitorFirmName());
        assertEquals(SOLICITOR_FIRM_POSTCODE, callbackResponse.getData().getSolsSolicitorFirmPostcode());
        assertEquals(SOLICITOR_FIRM_EMAIL, callbackResponse.getData().getSolsSolicitorEmail());
        assertEquals(SOLICITOR_FIRM_PHONE, callbackResponse.getData().getSolsSolicitorPhoneNumber());
        assertEquals(SOLICITOR_SOT_NAME, callbackResponse.getData().getSolsSOTName());
        assertEquals(SOLICITOR_SOT_JOB_TITLE, callbackResponse.getData().getSolsSOTJobTitle());

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
        assertEquals(APP_REF, callbackResponse.getData().getSolsSolicitorAppReference());
        assertEquals(ADDITIONAL_INFO, callbackResponse.getData().getSolsAdditionalInfo());

        assertEquals(BO_DOCS_RECEIVED, callbackResponse.getData().getBoEmailDocsReceivedNotificationRequested());
        assertEquals(BO_EMAIL_GRANT_ISSUED, callbackResponse.getData().getBoEmailGrantIssuedNotificationRequested());
        assertEquals(CASE_PRINT, callbackResponse.getData().getCasePrinted());
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
    }

    private void assertApplicationType(CallbackResponse callbackResponse, ApplicationType applicationType) {
        assertEquals(applicationType, callbackResponse.getData().getApplicationType());
    }

    private void assertCommonAdditionalExecutors(CallbackResponse callbackResponse) {
        assertEquals(emptyList(), callbackResponse.getData().getSolsAdditionalExecutorList());
        assertEquals(emptyList(), callbackResponse.getData().getAdditionalExecutorsApplying());
        assertEquals(emptyList(), callbackResponse.getData().getAdditionalExecutorsNotApplying());
    }
}
