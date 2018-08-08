package uk.gov.hmcts.probate.transformer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.service.StateChangeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT;

@RunWith(MockitoJUnitRunner.class)
public class CallbackResponseTransformerTest {
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
    private static final String PRIMARY_EXEC_APPLYING = "Yes";
    private static final String APPLICANT_HAS_ALIAS = "Yes";
    private static final String OTHER_EXECS_EXIST = "No";
    private static final String PRIMARY_EXEC_ALIAS_NAMES = "Alias names";
    private static final List<CollectionMember<AdditionalExecutor>> ADDITIONAL_EXEC_LIST = Collections.emptyList();
    private static final List<CollectionMember<AdditionalExecutorApplying>> ADDITIONAL_EXEC_LIST_APP = Collections.emptyList();
    private static final List<CollectionMember<AdditionalExecutorNotApplying>> ADDITIONAL_EXEC_LIST_NOT_APP = Collections.emptyList();
    private static final List<CollectionMember<AliasName>> DECEASED_ALIAS_NAMES_LIST = Collections.emptyList();
    private static final SolsAddress DECEASED_ADDRESS = Mockito.mock(SolsAddress.class);
    private static final SolsAddress EXEC_ADDRESS = Mockito.mock(SolsAddress.class);
    private static final List<CollectionMember<AliasName>> ALIAS_NAMES = Collections.emptyList();
    private static final String APP_REF = "app ref";
    private static final String ADDITIONAL_INFO = "additional info";
    private static final String IHT_REFERENCE = "123456789abcde";
    private static final String IHT_ONLINE = "Yes";

    private static final String BO_EMAIL_GRANT_ISSUED = "Yes";
    private static final String BO_DOCS_RECEIVED = "Yes";
    private static final String CASE_PRINT = "Yes";
    private static final List<CollectionMember<StopReason>> STOP_REASONS_LIST = Collections.emptyList();

    private static final String YES = "Yes";
    private static final Optional<String> ORIGINAL_STATE = Optional.empty();
    private static final Optional<String> CHANGED_STATE = Optional.of("Changed");

    private static final String DECEASED_TITLE = "Deceased Title";
    private static final String DECEASED_HONOURS = "Deceased Honours";

    private static final String LIMITATION_TEXT = "Limitation Text";
    private static final String EXECUTOR_LIMITATION = "Executor Limitation";
    private static final String ADMIN_CLAUSE_LIMITATION = "Admin Clause Limitation";

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
                .solsIHTFormId(IHT_FORM_ID)
                .ihtGrossValue(IHT_GROSS)
                .ihtNetValue(IHT_NET)
                .primaryApplicantForenames(APPLICANT_FORENAME)
                .primaryApplicantSurname(APPLICANT_SURNAME)
                .primaryApplicantEmailAddress(APPLICANT_EMAIL_ADDRESS)
                .primaryApplicantIsApplying(PRIMARY_EXEC_APPLYING)
                .primaryApplicantHasAlias(APPLICANT_HAS_ALIAS)
                .otherExecutorExists(OTHER_EXECS_EXIST)
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
                .ihtFormCompletedOnline(IHT_ONLINE);

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

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock, document);

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

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock, feeServiceResponseMock);

        assertCommon(callbackResponse);

        assertEquals("6600", callbackResponse.getData().getTotalFee());
        assertEquals(SOL_PAY_METHODS_FEE, callbackResponse.getData().getSolsPaymentMethods());
        assertEquals(FEE_ACCT_NUMBER, callbackResponse.getData().getSolsFeeAccountNumber());
        assertEquals(PAY_REF_FEE, callbackResponse.getData().getSolsPaymentReferenceNumber());
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

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock, feeServiceResponseMock);

        assertCommon(callbackResponse);

        assertEquals("6600", callbackResponse.getData().getTotalFee());
        assertEquals(SOL_PAY_METHODS_CHEQUE, callbackResponse.getData().getSolsPaymentMethods());
        assertNull(callbackResponse.getData().getSolsFeeAccountNumber());
        assertEquals(PAY_REF_CHEQUE, callbackResponse.getData().getSolsPaymentReferenceNumber());
    }

    @Test
    public void shouldAddDocumentToProbateDocumentsGenerated() {
        Document document = Document.builder().documentType(DIGITAL_GRANT).build();
        CallbackResponse callbackResponse = underTest.grantIssued(callbackRequestMock, document);

        assertCommon(callbackResponse);

        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(document, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
    }

    @Test
    public void shouldTransformCallbackRequestToCallbackResponse() {
        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock);

        assertCommon(callbackResponse);
    }

    private void assertCommon(CallbackResponse callbackResponse) {
        assertEquals(APPLICATION_TYPE, callbackResponse.getData().getApplicationType());
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

        assertEquals(IHT_FORM_ID, callbackResponse.getData().getSolsIHTFormId());
        Assert.assertThat(new BigDecimal("10000"), comparesEqualTo(callbackResponse.getData().getIhtGrossValue()));
        Assert.assertThat(new BigDecimal("9000"), comparesEqualTo(callbackResponse.getData().getIhtNetValue()));

        assertEquals(APPLICANT_FORENAME, callbackResponse.getData().getPrimaryApplicantForenames());
        assertEquals(APPLICANT_SURNAME, callbackResponse.getData().getPrimaryApplicantSurname());
        assertEquals(APPLICANT_EMAIL_ADDRESS, callbackResponse.getData().getPrimaryApplicantEmailAddress());
        assertEquals(PRIMARY_EXEC_APPLYING, callbackResponse.getData().getPrimaryApplicantIsApplying());
        assertEquals(APPLICANT_HAS_ALIAS, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(OTHER_EXECS_EXIST, callbackResponse.getData().getOtherExecutorExists());
        assertEquals(PRIMARY_EXEC_ALIAS_NAMES, callbackResponse.getData().getSolsExecutorAliasNames());
        assertEquals(ADDITIONAL_EXEC_LIST, callbackResponse.getData().getSolsAdditionalExecutorList());
        assertEquals(DECEASED_ADDRESS, callbackResponse.getData().getDeceasedAddress());
        assertEquals(EXEC_ADDRESS, callbackResponse.getData().getPrimaryApplicantAddress());
        assertEquals(ALIAS_NAMES, callbackResponse.getData().getSolsDeceasedAliasNamesList());
        assertEquals(APP_REF, callbackResponse.getData().getSolsSolicitorAppReference());
        assertEquals(ADDITIONAL_INFO, callbackResponse.getData().getSolsAdditionalInfo());

        assertEquals(BO_DOCS_RECEIVED, callbackResponse.getData().getBoEmailDocsReceivedNotificationRequested());
        assertEquals(BO_EMAIL_GRANT_ISSUED, callbackResponse.getData().getBoEmailGrantIssuedNotificationRequested());
        assertEquals(CASE_PRINT, callbackResponse.getData().getCasePrinted());
        assertEquals(STOP_REASONS_LIST, callbackResponse.getData().getBoCaseStopReasonList());

        assertEquals(ADDITIONAL_EXEC_LIST, callbackResponse.getData().getAdditionalExecutorsApplying());
        assertEquals(ADDITIONAL_EXEC_LIST, callbackResponse.getData().getAdditionalExecutorsNotApplying());

        assertEquals(DECEASED_TITLE, callbackResponse.getData().getBoDeceasedTitle());
        assertEquals(DECEASED_HONOURS, callbackResponse.getData().getBoDeceasedHonours());

        assertEquals(WILL_MESSAGE, callbackResponse.getData().getBoWillMessage());
        assertEquals(EXECUTOR_LIMITATION, callbackResponse.getData().getBoExecutorLimitation());
        assertEquals(ADMIN_CLAUSE_LIMITATION, callbackResponse.getData().getBoAdminClauseLimitation());
        assertEquals(LIMITATION_TEXT, callbackResponse.getData().getBoLimitationText());

        assertEquals(IHT_REFERENCE, callbackResponse.getData().getIhtReferenceNumber());
        assertEquals(IHT_ONLINE, callbackResponse.getData().getIhtFormCompletedOnline());
    }
}
