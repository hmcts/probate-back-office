package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Fee;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.HandOffLegacyTransformer;
import uk.gov.hmcts.probate.transformer.ServiceRequestTransformer;
import uk.gov.service.notify.NotificationClientException;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NextStepsUnitTest {

    private NextStepsController underTest;

    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CCDDataTransformer ccdBeanTransformerMock;
    @Mock
    private ConfirmationResponseService confirmationResponseServiceMock;
    @Mock
    private CallbackResponseTransformer callbackResponseTransformerMock;
    @Mock
    private ObjectMapper objectMapperMock;
    @Mock
    private FeeService feeServiceMock;
    @Mock
    private NotificationService notificationServiceMock;
    @Mock
    private PDFManagementService pdfManagementServiceMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private CCDData ccdDataMock;
    @Mock
    private InheritanceTax inheritanceTaxMock;
    @Mock
    private Fee feeMock;
    @Mock
    private FeesResponse feesResponseMock;
    @Mock
    private CallbackResponse callbackResponseMock;
    @Mock
    private StateChangeService stateChangeServiceMock;
    @Mock
    private PaymentsService paymentsService;
    @Mock
    Document coversheetMock;
    @Mock
    Document sentEmailMock;
    @Mock
    private HandOffLegacyTransformer handOffLegacyTransformerMock;
    @Mock
    private ServiceRequestTransformer serviceRequestTransformer;

    private static final String USER_ID = "User-ID";

    @BeforeEach
    public void setUp() throws NotificationClientException {
        MockitoAnnotations.openMocks(this);

        underTest = new NextStepsController(ccdBeanTransformerMock,
                confirmationResponseServiceMock, callbackResponseTransformerMock, serviceRequestTransformer,
                objectMapperMock, feeServiceMock, stateChangeServiceMock, paymentsService,
                handOffLegacyTransformerMock, pdfManagementServiceMock, notificationServiceMock);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(notificationServiceMock.sendEmail(any(), any())).thenReturn(sentEmailMock);
        when(pdfManagementServiceMock.generateAndUpload(callbackRequestMock, DocumentType.SOLICITOR_COVERSHEET))
                .thenReturn(coversheetMock);
        when(callbackResponseTransformerMock
                .transformForSolicitorComplete(callbackRequestMock, feesResponseMock, sentEmailMock, coversheetMock,
                        USER_ID))
                .thenReturn(callbackResponseMock);
        when(httpServletRequestMock.getHeader("user-id")).thenReturn(USER_ID);
        when(feeServiceMock.getAllFeesData(null, 0L, 0L)).thenReturn(feesResponseMock);
        when(caseDataMock.getEvidenceHandled()).thenReturn(null);
    }

    @Test
    void shouldValidateWithNoErrors() throws NotificationClientException {
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
        when(ccdBeanTransformerMock.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
        when(ccdDataMock.getFee()).thenReturn(feeMock);
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.valueOf(100000));
        CallbackResponse creditPaymentResponseError = Mockito.mock(CallbackResponse.class);
        when(creditPaymentResponseError.getErrors()).thenReturn(Collections.emptyList());
        when(callbackResponseTransformerMock.transformForSolicitorComplete(callbackRequestMock, feesResponseMock,
                null, null, USER_ID)).thenReturn(callbackResponseMock);
        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

    @Test
    void shouldValidateWithNoFeeValueNoErrors() throws NotificationClientException {
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
        when(ccdBeanTransformerMock.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
        when(ccdDataMock.getFee()).thenReturn(feeMock);
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.ZERO);

        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

    @Test
    void shouldValidateWithError() {
        assertThrows(BadRequestException.class, () -> {
            when(caseDetailsMock.getData()).thenReturn(caseDataMock);
            when(bindingResultMock.hasErrors()).thenReturn(true);
            when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());

            underTest.validate(callbackRequestMock, bindingResultMock, httpServletRequestMock);
        });
    }

    @Test
    void shouldValidateWithErrorAndLogRequest() throws JsonProcessingException {
        assertThrows(BadRequestException.class, () -> {
            when(caseDetailsMock.getData()).thenReturn(caseDataMock);
            when(bindingResultMock.hasErrors()).thenReturn(true);
            when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
            when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenThrow(JsonProcessingException.class);

            underTest.validate(callbackRequestMock, bindingResultMock, httpServletRequestMock);
        });
    }

    @Test
    void shouldValidateWithNoErrorsForStateChange() throws NotificationClientException {
        Optional<String> newState = Optional.of("changedState");
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(newState);
        when(callbackResponseTransformerMock
            .transformWithConditionalStateChange(callbackRequestMock, newState, Optional.empty()))
                .thenReturn(callbackResponseMock);


        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

    @Test
    void shouldValidateWithNoErrorsAndTransformEvidenceHandled() throws NotificationClientException {
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
        when(ccdBeanTransformerMock.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
        when(ccdDataMock.getFee()).thenReturn(feeMock);
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.valueOf(100000));
        when(callbackResponseTransformerMock.transformForSolicitorComplete(callbackRequestMock, feesResponseMock,
                null, null, USER_ID)).thenReturn(callbackResponseMock);
        CallbackResponse creditPaymentResponseError = Mockito.mock(CallbackResponse.class);
        when(creditPaymentResponseError.getErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

    @Test
    void shouldValidateWithNoFeeValueNoErrorsAndTransformEvidenceHandled() throws NotificationClientException {
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
        when(ccdBeanTransformerMock.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
        when(ccdDataMock.getFee()).thenReturn(feeMock);
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.ZERO);
        when(callbackResponseTransformerMock.transformForSolicitorComplete(callbackRequestMock, feesResponseMock,
                sentEmailMock, coversheetMock, USER_ID)).thenReturn(callbackResponseMock);

        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

    @Test
    void shouldSendApplicationReceivedEmailForNullEvidenceHandled() throws NotificationClientException {
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
        when(ccdBeanTransformerMock.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
        when(ccdDataMock.getFee()).thenReturn(feeMock);
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.valueOf(100000));
        when(callbackResponseTransformerMock.transformForSolicitorComplete(callbackRequestMock, feesResponseMock,
                null, null, USER_ID)).thenReturn(callbackResponseMock);
        when(caseDataMock.getEvidenceHandled()).thenReturn(null);
        CallbackResponse creditPaymentResponseError = Mockito.mock(CallbackResponse.class);
        when(creditPaymentResponseError.getErrors()).thenReturn(Collections.emptyList());
        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

    @Test
    void shouldSendApplicationReceivedNoDocEmailForNoEvidenceHandled() throws NotificationClientException {
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
        when(ccdBeanTransformerMock.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
        when(ccdDataMock.getFee()).thenReturn(feeMock);
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.valueOf(100000));
        when(callbackResponseTransformerMock.transformForSolicitorComplete(callbackRequestMock, feesResponseMock,
                null, null, USER_ID)).thenReturn(callbackResponseMock);
        when(caseDataMock.getEvidenceHandled()).thenReturn("No");
        CallbackResponse creditPaymentResponseError = Mockito.mock(CallbackResponse.class);
        when(creditPaymentResponseError.getErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

    @Test
    void shouldStartAwaitingDocumentationNotificationPeriodForNullEvidenceHandled() throws NotificationClientException {
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
        when(ccdBeanTransformerMock.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
        when(ccdDataMock.getFee()).thenReturn(feeMock);
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.valueOf(100000));
        when(callbackResponseTransformerMock.transformForSolicitorComplete(callbackRequestMock, feesResponseMock,
                null, null, USER_ID)).thenReturn(callbackResponseMock);
        when(caseDataMock.getEvidenceHandled()).thenReturn(null);
        CallbackResponse creditPaymentResponseError = Mockito.mock(CallbackResponse.class);
        when(creditPaymentResponseError.getErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

    @Test
    void shouldNotStartAwaitingDocumentationNotificationPeriodNoEvidenceHandled() throws NotificationClientException {
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
        when(ccdBeanTransformerMock.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
        when(ccdDataMock.getFee()).thenReturn(feeMock);
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.valueOf(100000));
        when(callbackResponseTransformerMock.transformForSolicitorComplete(callbackRequestMock, feesResponseMock,
                null, null, USER_ID)).thenReturn(callbackResponseMock);
        when(caseDataMock.getEvidenceHandled()).thenReturn("No");
        CallbackResponse creditPaymentResponseError = Mockito.mock(CallbackResponse.class);
        when(creditPaymentResponseError.getErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<CallbackResponse> response = underTest.validate(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);

        verify(notificationServiceMock, times(0)).startAwaitingDocumentationNotificationPeriod(caseDetailsMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }
}
