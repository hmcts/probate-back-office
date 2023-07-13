package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.payments.CreditAccountPayment;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.service.CaveatNotificationService;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.PrepareNocCaveatService;
import uk.gov.hmcts.probate.service.RegistrarDirectionService;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.service.payments.CreditAccountPaymentTransformer;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatDataTransformer;
import uk.gov.hmcts.probate.validator.CaveatsEmailValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsExpiryValidationRule;
import uk.gov.hmcts.probate.validator.CreditAccountPaymentValidationRule;
import uk.gov.hmcts.probate.validator.NocEmailAddressNotifyValidationRule;
import uk.gov.hmcts.probate.validator.SolicitorPaymentMethodValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.ID;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.LAST_MODIFIED;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;

class CaveatControllerUnitTest {

    private CaveatController underTest;

    @Mock
    private List<CaveatsEmailValidationRule> validationRuleCaveats;
    @Mock
    private List<CaveatsExpiryValidationRule> validationRuleCaveatsExpiry;
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
    @Mock
    private CreditAccountPaymentTransformer creditAccountPaymentTransformer;
    @Mock
    private CreditAccountPaymentValidationRule creditAccountPaymentValidationRule;

    private static final String AUTH = "Auth";

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
    private CreditAccountPayment creditAccountPaymentMock;
    @Mock
    private SolicitorPaymentMethodValidationRule solicitorPaymentMethodValidationRuleMock;
    @Mock
    private PaymentResponse paymentResponseMock;
    @Mock
    private RegistrarDirectionService registrarDirectionService;
    @Mock
    private DocumentGeneratorService documentGeneratorService;
    @Mock
    private PrepareNocCaveatService prepareNocCaveatService;
    @Mock
    private NocEmailAddressNotifyValidationRule nocEmailAddressNotifyValidationRule;
    private Document document;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        underTest = new CaveatController(validationRuleCaveats, validationRuleCaveatsExpiry, caveatDataTransformer,
            caveatCallbackResponseTransformer, eventValidationService, notificationService, caveatNotificationService,
            confirmationResponseService, paymentsService, feeService, creditAccountPaymentTransformer,
            creditAccountPaymentValidationRule, solicitorPaymentMethodValidationRuleMock,
            registrarDirectionService, documentGeneratorService, prepareNocCaveatService, nocEmailAddressNotifyValidationRule);

    }

    @Test
    void shouldValidateWithNoErrors() throws NotificationClientException {
        when(feeService.getCaveatFeesData()).thenReturn(feeResponseMock);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(creditAccountPaymentTransformer.transform(caveatDetailsMock, feeResponseMock))
            .thenReturn(creditAccountPaymentMock);
        when(paymentsService.getCreditAccountPaymentResponse(AUTH, creditAccountPaymentMock))
            .thenReturn(paymentResponseMock);
        when(eventValidationService.validateCaveatPaymentResponse(any(), any(), any()))
            .thenReturn(caveatCallbackResponse);
        when(caveatNotificationService.solsCaveatRaise(caveatCallbackRequest, paymentResponseMock))
            .thenReturn(caveatCallbackResponse);
        ResponseEntity<CaveatCallbackResponse> response = underTest.validate(AUTH, caveatCallbackRequest,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(caveatCallbackResponse));
    }

    @Test
    void shouldValidateWithPaymentMethodErrors() throws NotificationClientException {
        assertThrows(BusinessValidationException.class, () -> {
            when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);
            doThrow(BusinessValidationException.class).when(solicitorPaymentMethodValidationRuleMock)
                    .validate(caveatDetailsMock);
            underTest.validate(AUTH, caveatCallbackRequest, bindingResultMock);
        });
    }

    @Test
    void shouldValidateWithPaymentErrors() throws NotificationClientException {
        when(feeService.getCaveatFeesData()).thenReturn(feeResponseMock);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(creditAccountPaymentTransformer.transform(caveatDetailsMock, feeResponseMock))
            .thenReturn(creditAccountPaymentMock);
        when(paymentsService.getCreditAccountPaymentResponse(AUTH, creditAccountPaymentMock))
            .thenReturn(paymentResponseMock);
        when(eventValidationService.validateCaveatPaymentResponse(any(), any(), any()))
            .thenReturn(caveatCallbackResponse);
        when(caveatCallbackResponse.getErrors()).thenReturn(Arrays.asList("Error"));
        ResponseEntity<CaveatCallbackResponse> response = underTest.validate(AUTH, caveatCallbackRequest,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(caveatCallbackResponse));
        assertThat(response.getBody().getErrors().size(), is(1));
    }

    @Test
    void shouldDefaultSolsPBA() {
        when(caveatCallbackResponseTransformer.transformCaseForSolicitorPBANumbers(caveatCallbackRequest, AUTH))
            .thenReturn(caveatCallbackResponse);
        ResponseEntity<CaveatCallbackResponse> response = underTest.defaulsSolicitorNextStepsForPBANumbers(AUTH,
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
    void shouldTransformNoc() {
        when(caveatDetailsMock.getData()).thenReturn(caveatDataMock);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);

        ResponseEntity<CaveatCallbackResponse> response =
                underTest.prepareCaseForNoc(caveatCallbackRequest);
        verify(prepareNocCaveatService, times(1)).setRemovedRepresentative(caveatDataMock);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldSendNocEmail() throws NotificationClientException {
        CaveatDetails caveatDetails = new CaveatDetails(CaveatData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Manchester")
                .solsSolicitorAppReference("1234-5678-9012")
                .languagePreferenceWelsh("No")
                .removedRepresentative(RemovedRepresentative.builder()
                        .solicitorEmail("solicitor@gmail.com")
                        .solicitorFirstName("FirstName")
                        .solicitorLastName("LastName").build())
                .build(), LAST_MODIFIED, ID);
        caveatCallbackRequest  = new CaveatCallbackRequest(caveatDetails);
        document = Document.builder()
                .documentDateAdded(LocalDate.now())
                .documentFileName("fileName")
                .documentGeneratedBy("generatedBy")
                .documentLink(
                        DocumentLink.builder().documentUrl("url").documentFilename("file")
                                .documentBinaryUrl("binary").build())
                .documentType(DocumentType.SENT_EMAIL)
                .build();
        caveatCallbackResponse = CaveatCallbackResponse.builder().errors(Collections.EMPTY_LIST).build();
        when(eventValidationService.validateCaveatNocEmail(any(), any())).thenReturn(caveatCallbackResponse);
        when(notificationService.sendCaveatNocEmail(any(), any())).thenReturn(document);

        ResponseEntity<CaveatCallbackResponse> response =
                underTest.sendNOCEmailNotification(caveatCallbackRequest);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
