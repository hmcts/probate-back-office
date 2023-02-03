package uk.gov.hmcts.probate.service.payments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestDto;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestPaymentResponseDto;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestUpdateResponseDto;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.CaveatNotificationService;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.DocumentTransformer;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.PaymentStatus;
import uk.gov.hmcts.reform.probate.model.cases.CasePayment;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.service.notify.NotificationClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.CAVEAT;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.STANDING_SEARCH;

class PaymentsServiceTest {
    @InjectMocks
    private PaymentsService paymentsService;

    @Mock
    private ServiceRequestClient serviceRequestClient;
    @Mock
    private SecurityUtils securityUtilsMock;
    @Mock
    private CcdClientApi ccdClientApi;
    @Mock
    private IdamApi idamApi;
    @Mock
    private CasePaymentBuilder casePaymentBuilder;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PDFManagementService pdfManagementService;
    @Mock
    private DocumentTransformer documentTransformer;
    @Mock
    private CaveatNotificationService caveatNotificationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateServiceRequest() {
        ServiceRequestDto serviceDto = ServiceRequestDto.builder().build();
        SecurityDTO securityDTO = SecurityDTO.builder()
                .userId("userId")
                .authorisation("auth")
                .serviceAuthorisation("serviceAuth")
                .build();
        when(securityUtilsMock.getSecurityDTO()).thenReturn(securityDTO);
        when(serviceRequestClient.createServiceRequest(any(), any(), any()))
                .thenReturn("{\"service_request_reference\":\"abcdef123456\"}");
        String request = paymentsService.createServiceRequest(serviceDto);

        assertEquals("abcdef123456", request);
    }

    @Test
    void shouldRetrieveAndUpdateGrantDataWithPaymentResponse() throws NotificationClientException {
        setupIdamUserResponse();

        HashMap<String, Object> caseData = new HashMap();
        caseData.put("registryLocation", "ctsc");
        caseData.put("languagePreferenceWelsh", "No");
        caseData.put("applicationType", "Solicitor");
        caseData.put("solsSolicitorEmail", "solsSolicitorEmail@probate-test.com");
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = CaseDetails.builder()
                .id(0L)
                .data(caseData)
                .build();
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);
        Document sentEmail = Document.builder()
                .documentLink(DocumentLink.builder().build())
                .documentType(DocumentType.EMAIL)
                .build();
        when(notificationService.sendEmail(any(), any())).thenReturn(sentEmail);
        Document coversheet = Document.builder()
                .documentLink(DocumentLink.builder().build())
                .documentType(DocumentType.BLANK)
                .build();
        when(pdfManagementService
                .generateAndUpload(any(CallbackRequest.class), any())).thenReturn(coversheet);
        when(casePaymentBuilder.getPaymentStatusByServiceRequestStatus("paymentStatus"))
                .thenReturn(PaymentStatus.SUCCESS.getName());
        when(casePaymentBuilder.parseDate(any())).thenReturn(LocalDate.now(), LocalDate.now());
        List<CollectionMember<CasePayment>> payments = Arrays.asList(new CollectionMember(null,
                CasePayment.builder()
                        .status(PaymentStatus.SUCCESS)
                        .build()));
        when(casePaymentBuilder.addPaymentFromServiceRequestResponse(any(), any())).thenReturn(payments);
        ServiceRequestUpdateResponseDto responseDto = getServiceRequestUpdateResponseDto();
        paymentsService.updateCaseFromServiceRequest(responseDto, GRANT_OF_REPRESENTATION);

        verify(ccdClientApi).updateCaseAsCaseworker(any(), any(),
                any(), any(),
                any(), any(), any());
        verify(documentTransformer).addDocument(any(), any(), any());
    }

    @Test
    void shouldRetrieveAndUpdateGrantDataWithPaymentResponseEvidenecHandled()
            throws NotificationClientException {
        setupIdamUserResponse();

        HashMap<String, Object> caseData = new HashMap();
        caseData.put("registryLocation", "ctsc");
        caseData.put("languagePreferenceWelsh", "No");
        caseData.put("applicationType", "Solicitor");
        caseData.put("solsSolicitorEmail", "solsSolicitorEmail@probate-test.com");
        caseData.put("evidenceHandled", "Yes");
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = CaseDetails.builder()
                .id(0L)
                .data(caseData)
                .build();
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);
        Document sentEmail = Document.builder()
                .documentLink(DocumentLink.builder().build())
                .documentType(DocumentType.EMAIL)
                .build();
        when(notificationService.sendEmail(any(), any())).thenReturn(sentEmail);
        Document coversheet = Document.builder()
                .documentLink(DocumentLink.builder().build())
                .documentType(DocumentType.BLANK)
                .build();
        when(pdfManagementService
                .generateAndUpload(any(CallbackRequest.class), any())).thenReturn(coversheet);
        when(casePaymentBuilder.getPaymentStatusByServiceRequestStatus("paymentStatus"))
                .thenReturn(PaymentStatus.SUCCESS.getName());
        when(casePaymentBuilder.parseDate(any())).thenReturn(LocalDate.now(), LocalDate.now());
        List<CollectionMember<CasePayment>> payments = Arrays.asList(new CollectionMember(null,
                CasePayment.builder()
                        .status(PaymentStatus.SUCCESS)
                        .build()));
        when(casePaymentBuilder.addPaymentFromServiceRequestResponse(any(), any())).thenReturn(payments);
        ServiceRequestUpdateResponseDto responseDto = getServiceRequestUpdateResponseDto();
        paymentsService.updateCaseFromServiceRequest(responseDto, GRANT_OF_REPRESENTATION);

        verify(ccdClientApi).updateCaseAsCaseworker(any(), any(),
                any(), any(),
                any(), any(), any());
        verify(documentTransformer).addDocument(any(), any(), any());
    }

    @Test
    void shouldRetrieveAndUpdateCaveatDataWithPaymentResponse() throws NotificationClientException {
        setupIdamUserResponse();

        HashMap<String, Object> caseData = new HashMap();
        caseData.put("registryLocation", "ctsc");
        caseData.put("languagePreferenceWelsh", "No");
        caseData.put("applicationType", "Solicitor");
        caseData.put("caveatorEmailAddress", "solsSolicitorEmail@probate-test.com");
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = CaseDetails.builder()
                .id(0L)
                .data(caseData)
                .build();
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);
        CaveatCallbackResponse response = CaveatCallbackResponse.builder()
                .caveatData(ResponseCaveatData.builder()
                        .notificationsGenerated(new ArrayList<>())
                        .build())
                .build();
        when(caveatNotificationService.solsCaveatRaise(any())).thenReturn(response);
        when(casePaymentBuilder.getPaymentStatusByServiceRequestStatus("paymentStatus"))
                .thenReturn(PaymentStatus.SUCCESS.getName());
        when(casePaymentBuilder.parseDate(any())).thenReturn(LocalDate.now(), LocalDate.now());
        List<CollectionMember<CasePayment>> payments = Arrays.asList(new CollectionMember(null,
                CasePayment.builder()
                        .status(PaymentStatus.SUCCESS)
                        .build()));
        when(casePaymentBuilder.addPaymentFromServiceRequestResponse(any(), any())).thenReturn(payments);
        ServiceRequestUpdateResponseDto responseDto = getServiceRequestUpdateResponseDto();
        paymentsService.updateCaseFromServiceRequest(responseDto, CAVEAT);

        verify(ccdClientApi).updateCaseAsCaseworker(any(), any(),
                any(), any(),
                any(), any(), any());
    }

    @Test
    void shouldNotUpdateCaveatDataWhenNotificationFailure() {
        assertThrows(RuntimeException.class, () -> {
            setupIdamUserResponse();

            HashMap<String, Object> caseData = new HashMap();
            caseData.put("registryLocation", "ctsc");
            caseData.put("languagePreferenceWelsh", "No");
            caseData.put("applicationType", "Solicitor");
            caseData.put("caveatorEmailAddress", "solsSolicitorEmail@probate-test.com");
            uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = CaseDetails.builder()
                    .id(0L)
                    .data(caseData)
                    .build();
            when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);
            when(caveatNotificationService.solsCaveatRaise(any())).thenThrow(NotificationClientException.class);
            when(casePaymentBuilder.getPaymentStatusByServiceRequestStatus("paymentStatus"))
                    .thenReturn(PaymentStatus.SUCCESS.getName());
            when(casePaymentBuilder.parseDate(any())).thenReturn(LocalDate.now(), LocalDate.now());
            List<CollectionMember<CasePayment>> payments = Arrays.asList(new CollectionMember(null,
                    CasePayment.builder()
                            .status(PaymentStatus.SUCCESS)
                            .build()));
            when(casePaymentBuilder.addPaymentFromServiceRequestResponse(any(), any())).thenReturn(payments);
            ServiceRequestUpdateResponseDto responseDto = getServiceRequestUpdateResponseDto();
            paymentsService.updateCaseFromServiceRequest(responseDto, CAVEAT);

            verify(ccdClientApi, times(0)).updateCaseAsCaseworker(any(), any(),
                    any(), any(),
                    any(), any(), any());
        });
    }

    @Test
    void shouldNotUpdateCaveatDataWhenInvalidCaseType() {
        assertThrows(IllegalArgumentException.class, () -> {
            setupIdamUserResponse();

            HashMap<String, Object> caseData = new HashMap();
            caseData.put("registryLocation", "ctsc");
            caseData.put("languagePreferenceWelsh", "No");
            caseData.put("applicationType", "Solicitor");
            caseData.put("caveatorEmailAddress", "solsSolicitorEmail@probate-test.com");
            uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = CaseDetails.builder()
                    .id(0L)
                    .data(caseData)
                    .build();
            when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);
            when(caveatNotificationService.solsCaveatRaise(any())).thenThrow(NotificationClientException.class);
            when(casePaymentBuilder.getPaymentStatusByServiceRequestStatus("paymentStatus"))
                    .thenReturn(PaymentStatus.SUCCESS.getName());
            when(casePaymentBuilder.parseDate(any())).thenReturn(LocalDate.now(), LocalDate.now());
            List<CollectionMember<CasePayment>> payments = Arrays.asList(new CollectionMember(null,
                    CasePayment.builder()
                            .status(PaymentStatus.SUCCESS)
                            .build()));
            when(casePaymentBuilder.addPaymentFromServiceRequestResponse(any(), any())).thenReturn(payments);
            ServiceRequestUpdateResponseDto responseDto = getServiceRequestUpdateResponseDto();
            paymentsService.updateCaseFromServiceRequest(responseDto, STANDING_SEARCH);

            verify(ccdClientApi, times(0)).updateCaseAsCaseworker(any(), any(),
                    any(), any(),
                    any(), any(), any());
        });
    }

    private void setupIdamUserResponse() {
        HashMap<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("id", "Value");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(stringObjectMap, HttpStatus.CONTINUE);
        when(idamApi.getUserDetails(any())).thenReturn(responseEntity);
    }

    private ServiceRequestUpdateResponseDto getServiceRequestUpdateResponseDto() {
        ServiceRequestUpdateResponseDto responseDto = ServiceRequestUpdateResponseDto.builder()
                .serviceRequestReference("2020-1599477846961")
                .ccdCaseNumber("1661448513999408")
                .serviceRequestAmount(BigDecimal.valueOf(50.00))
                .serviceRequestStatus("Paid")
                .serviceRequestPaymentResponseDto(ServiceRequestPaymentResponseDto.builder()
                        .paymentAmount(BigDecimal.valueOf(50.00))
                        .paymentReference("RC-1234")
                        .paymentMethod("payment by account")
                        .caseReference("example of case ref")
                        .accountNumber("PBA123")
                        .build())
                .build();
        return responseDto;
    }
}
