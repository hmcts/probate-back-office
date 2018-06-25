package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;
import uk.gov.hmcts.probate.model.ccd.raw.AliasNames;
import uk.gov.hmcts.probate.model.ccd.raw.CCDDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReasons;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.model.template.PDFServiceTemplate;
import uk.gov.hmcts.probate.service.StateChangeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallbackResponseTransformerTest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String APPLICATION_TYPE = "Solicitor";
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
    private static final Float IHT_GROSS = 10000f;
    private static final Float IHT_NET = 9000f;

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
    private static final List<AdditionalExecutors> ADDITIONAL_EXEC_LIST = Collections.emptyList();
    private static final List<AliasNames> DECEASED_ALIAS_NAMES_LIST = Collections.emptyList();
    private static final SolsAddress DECEASED_ADDRESS = Mockito.mock(SolsAddress.class);
    private static final SolsAddress EXEC_ADDRESS = Mockito.mock(SolsAddress.class);
    private static final List<AliasNames> ALIAS_NAMES = Collections.emptyList();
    private static final String APP_REF = "app ref";
    private static final String ADDITIONAL_INFO = "additional info";

    private static final String BO_EMAIL_GRANT_ISSUED = "Yes";
    private static final String BO_DOCS_RECEIVED = "Yes";
    private static final String CASE_PRINT = "Yes";
    private static final List<StopReasons> STOP_REASONS_LIST = Collections.emptyList();

    private static final String YES = "Yes";
    private static final Optional<String> ORIGINAL_STATE = Optional.empty();
    private static final Optional<String> CHANGED_STATE = Optional.of("Changed");


    @InjectMocks
    private CallbackResponseTransformer underTest;

    @Mock
    private StateChangeService stateChangeServiceMock;

    @Mock
    private CallbackRequest callbackRequestMock;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private CaseData caseDataMock;

    @Mock
    private FeeServiceResponse feeServiceResponseMock;

    @Mock
    private CCDDocument ccdDocumentMock;

    @Before
    public void setup() {

        initMocks(this);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);

        when(caseDataMock.getSolsSolicitorFirmName()).thenReturn(SOLICITOR_FIRM_NAME);
        when(caseDataMock.getSolsSolicitorFirmPostcode()).thenReturn(SOLICITOR_FIRM_POSTCODE);
        when(caseDataMock.getSolsSolicitorEmail()).thenReturn(SOLICITOR_FIRM_EMAIL);
        when(caseDataMock.getSolsSolicitorPhoneNumber()).thenReturn(SOLICITOR_FIRM_PHONE);
        when(caseDataMock.getSolsSOTName()).thenReturn(SOLICITOR_SOT_NAME);
        when(caseDataMock.getSolsSOTJobTitle()).thenReturn(SOLICITOR_SOT_JOB_TITLE);

        when(caseDataMock.getDeceasedForenames()).thenReturn(DECEASED_FIRSTNAME);
        when(caseDataMock.getDeceasedSurname()).thenReturn(DECEASED_LASTNAME);
        when(caseDataMock.getDeceasedDateOfBirth()).thenReturn(DOB);
        when(caseDataMock.getDeceasedDateOfDeath()).thenReturn(DOD);
        when(caseDataMock.getWillNumberOfCodicils()).thenReturn(NUM_CODICILS);

        when(caseDataMock.getSolsIHTFormId()).thenReturn(IHT_FORM_ID);
        when(caseDataMock.getIhtGrossValue()).thenReturn(IHT_GROSS);
        when(caseDataMock.getIhtNetValue()).thenReturn(IHT_NET);

        when(caseDataMock.getPrimaryApplicantForenames()).thenReturn(APPLICANT_FORENAME);
        when(caseDataMock.getPrimaryApplicantSurname()).thenReturn(APPLICANT_SURNAME);
        when(caseDataMock.getPrimaryApplicantEmailAddress()).thenReturn(APPLICANT_EMAIL_ADDRESS);
        when(caseDataMock.getPrimaryApplicantIsApplying()).thenReturn(PRIMARY_EXEC_APPLYING);
        when(caseDataMock.getPrimaryApplicantHasAlias()).thenReturn(APPLICANT_HAS_ALIAS);
        when(caseDataMock.getOtherExecutorExists()).thenReturn(OTHER_EXECS_EXIST);
        when(caseDataMock.getSolsExecutorAliasNames()).thenReturn(PRIMARY_EXEC_ALIAS_NAMES);
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(ADDITIONAL_EXEC_LIST);
        when(caseDataMock.getDeceasedAddress()).thenReturn(DECEASED_ADDRESS);
        when(caseDataMock.getDeceasedAnyOtherNames()).thenReturn(YES);
        when(caseDataMock.getSolsDeceasedAliasNamesList()).thenReturn(DECEASED_ALIAS_NAMES_LIST);
        when(caseDataMock.getPrimaryApplicantAddress()).thenReturn(EXEC_ADDRESS);
        when(caseDataMock.getSolsDeceasedAliasNamesList()).thenReturn(ALIAS_NAMES);
        when(caseDataMock.getSolsSolicitorAppReference()).thenReturn(APP_REF);
        when(caseDataMock.getSolsAdditionalInfo()).thenReturn(ADDITIONAL_INFO);

        when(caseDataMock.getBoEmailGrantIssuedNotification()).thenReturn(BO_EMAIL_GRANT_ISSUED);
        when(caseDataMock.getBoEmailDocsReceivedNotification()).thenReturn(BO_DOCS_RECEIVED);
        when(caseDataMock.getBoEmailGrantIssuedNotificationOrDefault()).thenReturn(BO_EMAIL_GRANT_ISSUED);
        when(caseDataMock.getBoEmailDocsReceivedNotificationOrDefault()).thenReturn(BO_DOCS_RECEIVED);
        when(caseDataMock.getCasePrinted()).thenReturn(CASE_PRINT);
        when(caseDataMock.getBoCaseStopReasonList()).thenReturn(STOP_REASONS_LIST);

        when(caseDataMock.getWillExists()).thenReturn(YES);

    }

    @Test
    public void shouldConvertRequestToDataBeanForWithStateChange() {
        when(stateChangeServiceMock.getChangedStateForCaseUpdate(caseDataMock)).thenReturn(CHANGED_STATE);

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

        when(ccdDocumentMock.getDocumentBinaryUrl()).thenReturn(DOC_BINARY_URL);
        when(ccdDocumentMock.getDocumentUrl()).thenReturn(DOC_URL);
        when(ccdDocumentMock.getDocumentFilename()).thenReturn(DOC_NAME);
        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock, PDFServiceTemplate.LEGAL_STATEMENT, ccdDocumentMock);

        assertCommon(callbackResponse);

        assertEquals(DOC_BINARY_URL, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentBinaryUrl());
        assertEquals(DOC_URL, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentUrl());
        assertEquals(DOC_NAME, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentFilename());
        assertNull(callbackResponse.getData().getSolsSOTNeedToUpdate());
    }

    @Test
    public void shouldConvertRequestToDataBeanForPaymentWithLegalStatementDocNullWhenPdfServiceTemplateIsNull() {

        when(ccdDocumentMock.getDocumentBinaryUrl()).thenReturn(DOC_BINARY_URL);
        when(ccdDocumentMock.getDocumentUrl()).thenReturn(DOC_URL);
        when(ccdDocumentMock.getDocumentFilename()).thenReturn(DOC_NAME);
        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock, null, ccdDocumentMock);

        assertCommon(callbackResponse);

        assertNull(callbackResponse.getData().getSolsLegalStatementDocument());
    }

    @Test
    public void shouldConvertRequestToDataBeanForPaymentWithFeeAccount() {

        when(caseDataMock.getSolsPaymentMethods()).thenReturn(SOL_PAY_METHODS_FEE);
        when(caseDataMock.getSolsFeeAccountNumber()).thenReturn(FEE_ACCT_NUMBER);


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

        when(caseDataMock.getSolsPaymentMethods()).thenReturn(SOL_PAY_METHODS_CHEQUE);

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
        assertEquals("10000", callbackResponse.getData().getIhtGrossValue());
        assertEquals("9000", callbackResponse.getData().getIhtNetValue());

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

        assertEquals(BO_DOCS_RECEIVED, callbackResponse.getData().getBoEmailDocsReceivedNotification());
        assertEquals(BO_EMAIL_GRANT_ISSUED, callbackResponse.getData().getBoEmailGrantIssuedNotification());
        assertEquals(CASE_PRINT, callbackResponse.getData().getCasePrinted());
        assertEquals(STOP_REASONS_LIST, callbackResponse.getData().getBoCaseStopReasonList());
    }
}
