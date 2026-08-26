package uk.gov.hmcts.probate.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestDto;
import uk.gov.hmcts.probate.service.CaveatNotificationService;
import uk.gov.hmcts.probate.service.CcdSupplementaryDataService;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RegistrarDirectionService;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatDataTransformer;
import uk.gov.hmcts.probate.transformer.ServiceRequestTransformer;
import uk.gov.hmcts.probate.validator.CaveatAcknowledgementValidationRule;
import uk.gov.hmcts.probate.validator.CaveatChangeSubmissionDateValidationRule;
import uk.gov.hmcts.probate.validator.CaveatDodValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsEmailValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsExpiryValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CaveatControllerUnitTest {

    private CaveatController underTest;

    @Mock
    private List<CaveatsEmailValidationRule> validationRuleCaveats;
    @Mock
    private List<CaveatsExpiryValidationRule> validationRuleCaveatsExpiry;

    @Mock
    private CaveatDodValidationRule caveatDodValidationRule;
    @Mock
    private CaveatDataTransformer caveatDataTransformer;
    @Mock
    private CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;
    @Mock
    private EventValidationService eventValidationService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private CaveatNotificationService caveatNotificationService;
    @Mock
    private ConfirmationResponseService confirmationResponseService;
    @Mock
    private PaymentsService paymentsService;
    @Mock
    private FeeService feeService;

    private static final String SERVICE_REQUEST_REFERENCE = "Service Request Ref";
    private static final String USER_ID = "User-ID";

    @Mock
    private CaveatCallbackRequest caveatCallbackRequest;
    @Mock
    private CaveatDetails caveatDetailsMock;
    @Mock
    private CaveatData caveatDataMock;
    @Mock
    private FeeResponse feeResponseMock;
    @Mock
    private CaveatCallbackResponse caveatCallbackResponse;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private ServiceRequestTransformer serviceRequestTransformer;
    @Mock
    private ServiceRequestDto serviceRequestDtoMock;
    @Mock
    private RegistrarDirectionService registrarDirectionService;
    @Mock
    private DocumentGeneratorService documentGeneratorService;
    @Mock
    private CaveatAcknowledgementValidationRule caveatAcknowledgementValidationRule;
    @Mock
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private CcdSupplementaryDataService ccdSupplementaryDataService;
    @Mock
    private CaveatChangeSubmissionDateValidationRule caveatChangeSubmissionDateValidationRule;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        underTest = new CaveatController(validationRuleCaveats, validationRuleCaveatsExpiry, caveatDodValidationRule,
            caveatDataTransformer, caveatCallbackResponseTransformer, serviceRequestTransformer, eventValidationService,
            notificationService, caveatNotificationService, confirmationResponseService, paymentsService, feeService,
            registrarDirectionService, documentGeneratorService, caveatAcknowledgementValidationRule,
                ccdSupplementaryDataService, caveatChangeSubmissionDateValidationRule);
    }

    @Test
    void shouldValidateWithNoErrors() throws NotificationClientException {
        when(feeService.getCaveatFeesData()).thenReturn(feeResponseMock);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(serviceRequestTransformer.buildServiceRequest(caveatDetailsMock, feeResponseMock))
                .thenReturn(serviceRequestDtoMock);
        when(paymentsService.createServiceRequest(serviceRequestDtoMock))
            .thenReturn(SERVICE_REQUEST_REFERENCE);
        when(httpServletRequestMock.getHeader("user-id")).thenReturn(USER_ID);
        when(caveatCallbackResponseTransformer.transformResponseWithServiceRequest(caveatCallbackRequest,
                USER_ID)).thenReturn(caveatCallbackResponse);
        ResponseEntity<CaveatCallbackResponse> response = underTest.solsCompleteApplication(caveatCallbackRequest,
            bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(caveatCallbackResponse));
    }

    @Test
    void shouldDefaultSolsPBA() {
        when(caveatCallbackResponseTransformer.transformCaseForSolicitorPayment(caveatCallbackRequest))
            .thenReturn(caveatCallbackResponse);
        ResponseEntity<CaveatCallbackResponse> response = underTest.defaultSolicitorNextStepsForPayment(
                caveatCallbackRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(caveatCallbackResponse));
    }

    @Test
    void shouldInvokeRegistrarsDecision() {
        when(caveatDetailsMock.getData()).thenReturn(caveatDataMock);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);

        ResponseEntity<CaveatCallbackResponse> response =
                underTest.registrarsDecision(caveatCallbackRequest);
        verify(registrarDirectionService, times(1)).addAndOrderDirectionsToCaveat(caveatDataMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldSetupDeleteDocuments() {
        when(caveatDetailsMock.getData()).thenReturn(caveatDataMock);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);

        ResponseEntity<CaveatCallbackResponse> response =
                underTest.setupForPermanentRemovalCaveat(caveatCallbackRequest);
        verify(caveatCallbackResponseTransformer, times(1)).setupOriginalDocumentsForRemoval(caveatCallbackRequest);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldDeleteDocuments() {
        when(caveatDetailsMock.getData()).thenReturn(caveatDataMock);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);

        ResponseEntity<CaveatCallbackResponse> response =
                underTest.permanentlyDeleteRemovedCaveat(caveatCallbackRequest);
        verify(documentGeneratorService, times(1)).permanentlyDeleteRemovedDocumentsForCaveat(caveatCallbackRequest);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldRollback() {
        when(caveatDetailsMock.getData()).thenReturn(caveatDataMock);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);

        ResponseEntity<CaveatCallbackResponse> response =
                underTest.rollbackDataMigration(caveatCallbackRequest);
        verify(caveatCallbackResponseTransformer, times(1))
                .rollback(caveatCallbackRequest);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldValidatePaymentAcknowledgement() {
        when(caveatDetailsMock.getData()).thenReturn(caveatDataMock);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);

        ResponseEntity<CaveatCallbackResponse> response = underTest.validateAcknowledgement(
                caveatCallbackRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        verify(caveatCallbackResponseTransformer, times(1))
                .transformResponseWithNoChanges(caveatCallbackRequest);
    }

    @Test
    void shouldSetSupplementaryData() {
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getId()).thenReturn(1000L);
        ResponseEntity<CaveatCallbackResponse> response = underTest.setCaveatSupplementaryData(caveatCallbackRequest);

        verify(ccdSupplementaryDataService).submitSupplementaryDataToCcd(anyString());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldThrowExceptionWhenCaseDetailsIsNull() {
        when(caveatCallbackRequest.getCaseDetails())
                .thenReturn(null);
        assertThrows(
                NullPointerException.class,
                () -> underTest.setCaveatSupplementaryData(caveatCallbackRequest)
        );
        verifyNoInteractions(ccdSupplementaryDataService);
    }

    @Test
    void shouldChangeSubmissionDateAndRecalculateExpiryDate() {
        CaveatData caveatData = CaveatData.builder()
                .deceasedDateOfDeath(LocalDate.of(2024, 1, 1))
                .applicationSubmittedDate(LocalDate.of(2024, 2, 1))
                .expiryDate(LocalDate.of(2024, 3, 1))
                .build();
        CaveatDetails caveatDetails = new CaveatDetails(caveatData, new String[0], 1000L);
        CaveatCallbackRequest request = new CaveatCallbackRequest(caveatDetails);

        when(caveatChangeSubmissionDateValidationRule.validate(caveatDetails)).thenReturn(List.of());
        when(caveatCallbackResponseTransformer.changeSubmissionDate(request)).thenReturn(caveatCallbackResponse);

        ResponseEntity<CaveatCallbackResponse> response = underTest.changeSubmissionDate(request);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(caveatCallbackResponse));
        verify(caveatNotificationService).recalculateSubmissionExpiryDate(caveatData);
        verifyNoInteractions(paymentsService, notificationService);
    }

    @Test
    void shouldReturnValidationErrorsForChangeSubmissionDate() {
        CaveatData caveatData = CaveatData.builder().build();
        CaveatDetails caveatDetails = new CaveatDetails(caveatData, new String[0], 1000L);
        CaveatCallbackRequest request = new CaveatCallbackRequest(caveatDetails);

        when(paymentsService.isPaymentSuccessByCaseId("1000")).thenReturn(true);
        List<FieldErrorResponse> errors = List.of(
                FieldErrorResponse.builder().message("error-1").build(),
                FieldErrorResponse.builder().message("error-2").build()
        );
        when(caveatChangeSubmissionDateValidationRule.validate(caveatDetails))
                .thenReturn(errors);

        ResponseEntity<CaveatCallbackResponse> response = underTest.changeSubmissionDate(request);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getErrors(), is(List.of("error-1", "error-2")));
        verifyNoInteractions(caveatCallbackResponseTransformer, caveatNotificationService, paymentsService,
                notificationService);
    }
}
