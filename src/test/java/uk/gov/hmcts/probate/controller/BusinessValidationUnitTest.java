package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.CaseOrigin;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.CaseStoppedService;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.CaseworkerAmendValidationRule;
import uk.gov.hmcts.probate.validator.CheckListAmendCaseValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.hmcts.probate.validator.RedeclarationSoTValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;
import uk.gov.service.notify.NotificationClientException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;

@RunWith(MockitoJUnitRunner.class)
public class BusinessValidationUnitTest {

    @Mock
    private EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;
    @Mock
    private EventValidationService eventValidationServiceMock;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private Document documentMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private FieldError fieldErrorMock;
    @Mock
    private List<ValidationRule> validationRules;
    @Mock
    private List<CaseworkerAmendValidationRule> caseworkerAmendValidationRules;
    @Mock
    private List<CheckListAmendCaseValidationRule> checkListAmendCaseValidationRules;
    @Mock
    private CallbackResponseTransformer callbackResponseTransformerMock;
    @Mock
    private CallbackResponse callbackResponseMock;
    @Mock
    private ConfirmationResponseService confirmationResponseServiceMock;
    @Mock
    private AfterSubmitCallbackResponse afterSubmitCallbackResponseMock;
    @Mock
    private StateChangeService stateChangeServiceMock;
    @Mock
    private RedeclarationSoTValidationRule redeclarationSoTValidationRuleMock;

    private FieldErrorResponse businessValidationErrorMock;
    @Mock
    private PDFManagementService pdfManagementServiceMock;
    @Mock
    private CaseStoppedService caseStoppedServiceMock;


    private BusinessValidationController underTest;

    private static Optional<String> STATE_GRANT_TYPE_PROBATE = Optional.of("SolProbateCreated");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        businessValidationErrorMock = FieldErrorResponse.builder().build();
        underTest = new BusinessValidationController(eventValidationServiceMock,
            notificationService,
            objectMapper,
            validationRules,
            caseworkerAmendValidationRules,
            checkListAmendCaseValidationRules,
            callbackResponseTransformerMock,
            confirmationResponseServiceMock,
            stateChangeServiceMock,
            pdfManagementServiceMock,
            redeclarationSoTValidationRuleMock,
            caseStoppedServiceMock,
            emailAddressNotifyApplicantValidationRule);

        when(httpServletRequest.getRequestURI()).thenReturn("/test-uri");
    }

    @Test
    public void shouldValidateSolsApplyAsExecWithNoErrors() {
        when(callbackResponseTransformerMock.setApplicantFieldsForSolsApplyAsExec(callbackRequestMock))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.setApplicantFieldsForSolsApplyAsExec(callbackRequestMock);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    public void shouldValidateWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules)).thenReturn(callbackResponseMock);
        when(stateChangeServiceMock.getChangedStateForGrantType(caseDataMock)).thenReturn(STATE_GRANT_TYPE_PROBATE);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(callbackRequestMock, STATE_GRANT_TYPE_PROBATE))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidate(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    public void shouldValidateWithNoErrorsWithStateChange() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules)).thenReturn(callbackResponseMock);
        Optional<String> changedState = Optional.of("changedState");
        when(stateChangeServiceMock.getChangedStateForGrantType(caseDataMock)).thenReturn(changedState);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(callbackRequestMock, changedState))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidate(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    public void shouldValidateProbateWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules)).thenReturn(callbackResponseMock);
        when(stateChangeServiceMock.getChangedStateForProbateUpdate(caseDataMock)).thenReturn(Optional.empty());
        when(pdfManagementServiceMock.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE))
            .thenReturn(documentMock);
        when(callbackResponseTransformerMock.transform(callbackRequestMock, documentMock, "gop")).thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateProbate(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    public void shouldValidateProbateWithNoErrorsWithStateChange() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules)).thenReturn(callbackResponseMock);
        Optional<String> changedState = Optional.of("changedState");
        when(stateChangeServiceMock.getChangedStateForProbateUpdate(caseDataMock)).thenReturn(changedState);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(callbackRequestMock, changedState))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateProbate(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    public void shouldValidateIntestacyWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules)).thenReturn(callbackResponseMock);
        when(stateChangeServiceMock.getChangedStateForIntestacyUpdate(caseDataMock)).thenReturn(Optional.empty());
        when(pdfManagementServiceMock.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_INTESTACY))
            .thenReturn(documentMock);
        when(callbackResponseTransformerMock.transform(callbackRequestMock, documentMock, "intestacy")).thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateIntestacy(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    public void shouldValidateIntestacyWithNoErrorsWithStateChange() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules)).thenReturn(callbackResponseMock);
        Optional<String> changedState = Optional.of("changedState");
        when(stateChangeServiceMock.getChangedStateForIntestacyUpdate(caseDataMock)).thenReturn(changedState);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(callbackRequestMock, changedState))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateIntestacy(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    public void shouldValidateAdmonWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules)).thenReturn(callbackResponseMock);
        when(stateChangeServiceMock.getChangedStateForAdmonUpdate(caseDataMock)).thenReturn(Optional.empty());
        when(pdfManagementServiceMock.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_ADMON))
            .thenReturn(documentMock);
        when(callbackResponseTransformerMock.transform(callbackRequestMock, documentMock, "admonWill")).thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateAdmon(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    public void shouldValidateAdmonWithNoErrorsWithStateChange() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules)).thenReturn(callbackResponseMock);
        Optional<String> changedState = Optional.of("changedState");
        when(stateChangeServiceMock.getChangedStateForAdmonUpdate(caseDataMock)).thenReturn(changedState);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(callbackRequestMock, changedState))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidateAdmon(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test(expected = BadRequestException.class)
    public void shouldValidateWithFieldErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(true);
        when(bindingResultMock.getFieldErrors()).thenReturn(Collections.singletonList(fieldErrorMock));
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidate(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test
    public void shouldValidateWithBusinessErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        List<FieldErrorResponse> businessErrors = Collections.singletonList(businessValidationErrorMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, validationRules))
            .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getErrors())
            .thenReturn((businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList())));
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        ResponseEntity<CallbackResponse> response = underTest.solsValidate(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test
    public void shouldValidateAmendCaseWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendValidationRules))
            .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getErrors()).thenReturn(Collections.emptyList());

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        when(callbackResponseTransformerMock.transform(callbackRequestMock))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.validateCaseDetails(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }


    @Test(expected = BadRequestException.class)
    public void shouldValidateAmendCaseWithFieldErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(true);
        when(bindingResultMock.getFieldErrors()).thenReturn(Collections.singletonList(fieldErrorMock));
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        ResponseEntity<CallbackResponse> response = underTest.validateCaseDetails(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test
    public void shouldValidateAmendCaseWithBusinessErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        List<FieldErrorResponse> businessErrors = Collections.singletonList(businessValidationErrorMock);
        when(eventValidationServiceMock.validateRequest(callbackRequestMock, caseworkerAmendValidationRules))
            .thenReturn(callbackResponseMock);
        when(callbackResponseMock.getErrors())
            .thenReturn((businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList())));
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        ResponseEntity<CallbackResponse> response = underTest.validateCaseDetails(callbackRequestMock,
            bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test(expected = BadRequestException.class)
    public void shouldErrorForConfirmation() {
        when(bindingResultMock.hasErrors()).thenReturn(true);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        ResponseEntity<AfterSubmitCallbackResponse> response = underTest.stopWithConfirmation(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldPassConfirmation() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(confirmationResponseServiceMock.getStopConfirmation(Mockito.any(CallbackRequest.class)))
            .thenReturn(afterSubmitCallbackResponseMock);

        ResponseEntity<AfterSubmitCallbackResponse> response = underTest.stopWithConfirmation(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test(expected = BadRequestException.class)
    public void shouldTransformCaseWithFieldErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(true);
        when(bindingResultMock.getFieldErrors()).thenReturn(Collections.singletonList(fieldErrorMock));
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        ResponseEntity<CallbackResponse> response = underTest.casePrinted(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test
    public void shouldTransformCaseWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackResponseTransformerMock.transformCase(callbackRequestMock))
            .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.casePrinted(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test(expected = BadRequestException.class)
    public void shouldPaperFormWithFieldErrors() throws NotificationClientException {
        when(bindingResultMock.hasErrors()).thenReturn(true);
        when(bindingResultMock.getFieldErrors()).thenReturn(Collections.singletonList(fieldErrorMock));
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test
    public void shouldSubmitForPersonalWithEmail() throws NotificationClientException {
        String paperFormValue = "Any";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        Document documentMock = Mockito.mock(Document.class);
        when(notificationService.sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER))).thenReturn(documentMock);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, documentMock)).thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class))).thenReturn(Collections.EMPTY_LIST);
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getPaperForm(), is(paperFormValue));
    }
    
    @Test
    public void shouldSubmitForPersonalWithoutEmail() throws NotificationClientException {
        String paperFormValue = "Any";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, null)).thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class))).thenReturn(Arrays.asList(FieldErrorResponse.builder().build()));
        Document documentMock = Mockito.mock(Document.class);
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getPaperForm(), is(paperFormValue));
        verify(notificationService, times(0)).sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER));
    }

    @Test
    public void shouldSubmitPaperFormNoForPersonal() throws NotificationClientException {
        String paperFormValue = "No";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        Document documentMock = Mockito.mock(Document.class);
        when(notificationService.sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER))).thenReturn(documentMock);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, documentMock)).thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class))).thenReturn(Collections.EMPTY_LIST);
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getPaperForm(), is(paperFormValue));
    }

    @Test
    public void shouldSubmitForSolicitorWithoutEmail() throws NotificationClientException {
        String paperFormValue = "YesNo";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, null)).thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class))).thenReturn(Arrays.asList(FieldErrorResponse.builder().build()));
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
            bindingResultMock);

        verify(notificationService, times(0)).sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getPaperForm(), is(paperFormValue));
    }

    @Test
    public void shouldSubmitForSolicitorWithEmail() throws NotificationClientException {
        String paperFormValue = "YesNo";
        ResponseCaseData responseCaseData = ResponseCaseData.builder().paperForm(paperFormValue).build();
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(callbackResponseMock.getData()).thenReturn(responseCaseData);
        when(callbackResponseTransformerMock.paperForm(callbackRequestMock, null)).thenReturn(callbackResponseMock);
        when(emailAddressNotifyApplicantValidationRule.validate(any(CCDData.class))).thenReturn(Collections.EMPTY_LIST);
        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
            bindingResultMock);

        verify(notificationService, times(1)).sendEmail(APPLICATION_RECEIVED, caseDetailsMock, Optional.of(CaseOrigin.CASEWORKER));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getPaperForm(), is(paperFormValue));
    }
}
