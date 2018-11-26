package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.CaseworkerAmendValidationRule;
import uk.gov.hmcts.probate.validator.CheckListAmendCaseValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT;

@RunWith(MockitoJUnitRunner.class)
public class BusinessValidationUnitTest {

    @Mock
    private EventValidationService eventValidationServiceMock;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CCDDataTransformer ccdBeanTransformer;
    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private CCDData ccdDataMock;
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
    private Document documentMock;
    @Mock
    private ConfirmationResponseService confirmationResponseServiceMock;
    @Mock
    private AfterSubmitCallbackResponse afterSubmitCallbackResponseMock;
    @Mock
    private StateChangeService stateChangeServiceMock;
    private FieldErrorResponse businessValidationErrorMock;
    @Mock
    private PDFManagementService pdfManagementServiceMock;
    @Mock
    private JsonProcessingException jsonProcessingException;


    private BusinessValidationController underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        businessValidationErrorMock = FieldErrorResponse.builder().build();
        underTest = new BusinessValidationController(eventValidationServiceMock,
                ccdBeanTransformer,
                objectMapper,
                validationRules,
                caseworkerAmendValidationRules,
                checkListAmendCaseValidationRules,
                callbackResponseTransformerMock,
                confirmationResponseServiceMock,
                stateChangeServiceMock, pdfManagementServiceMock);

        when(httpServletRequest.getRequestURI()).thenReturn("/test-uri");
    }

    @Test
    public void shouldValidateWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(ccdBeanTransformer.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(eventValidationServiceMock.validate(ccdDataMock, validationRules)).thenReturn(Collections.emptyList());
        when(stateChangeServiceMock.getChangedStateForCaseUpdate(caseDataMock)).thenReturn(Optional.empty());
        when(pdfManagementServiceMock.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT))
                .thenReturn(documentMock);
        when(callbackResponseTransformerMock.transform(callbackRequestMock, documentMock))
                .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    public void shouldValidateWithNoErrorsWithStateChange() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(ccdBeanTransformer.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(eventValidationServiceMock.validate(ccdDataMock, validationRules)).thenReturn(Collections.emptyList());
        Optional<String> changedState = Optional.of("changedState");
        when(stateChangeServiceMock.getChangedStateForCaseUpdate(caseDataMock)).thenReturn(changedState);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(callbackRequestMock, changedState))
                .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequest);

        assertThat(response.getBody(), is(callbackResponseMock));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test
    public void shouldErrorForLogRequest() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(callbackRequestMock)).thenThrow(jsonProcessingException);

        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(ccdBeanTransformer.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(eventValidationServiceMock.validate(ccdDataMock, validationRules)).thenReturn(Collections.emptyList());
        when(stateChangeServiceMock.getChangedStateForCaseUpdate(caseDataMock)).thenReturn(Optional.empty());

        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test(expected = BadRequestException.class)
    public void shouldValidateWithFieldErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(true);
        when(bindingResultMock.getFieldErrors()).thenReturn(Collections.singletonList(fieldErrorMock));
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);

        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test
    public void shouldValidateWithBusinessErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        List<FieldErrorResponse> businessErrors = Collections.singletonList(businessValidationErrorMock);
        when(eventValidationServiceMock.validate(ccdDataMock, validationRules)).thenReturn(businessErrors);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(ccdBeanTransformer.transform(callbackRequestMock)).thenReturn(ccdDataMock);

        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test
    public void shouldValidateAmendCaseWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(ccdBeanTransformer.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(eventValidationServiceMock.validate(ccdDataMock, caseworkerAmendValidationRules)).thenReturn(Collections.emptyList());

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
        when(eventValidationServiceMock.validate(ccdDataMock, caseworkerAmendValidationRules)).thenReturn(businessErrors);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(ccdBeanTransformer.transform(callbackRequestMock)).thenReturn(ccdDataMock);

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

        ResponseEntity<CallbackResponse> response = underTest.transformCaseDetails(callbackRequestMock,
                bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

    @Test
    public void shouldTransformCaseWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(callbackResponseTransformerMock.transformCase(callbackRequestMock))
                .thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.transformCaseDetails(callbackRequestMock,
                bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(true));
    }

    @Test(expected = BadRequestException.class)
    public void shouldPaperFormWithFieldErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(true);
        when(bindingResultMock.getFieldErrors()).thenReturn(Collections.singletonList(fieldErrorMock));
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(ccdBeanTransformer.transform(callbackRequestMock)).thenReturn(ccdDataMock);

        ResponseEntity<CallbackResponse> response = underTest.paperFormCaseDetails(callbackRequestMock,
                bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors().isEmpty(), is(false));
    }

}
