package uk.gov.hmcts.probate.service;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.payments.PaymentDto;
import uk.gov.hmcts.probate.model.payments.PaymentsResponse;
import uk.gov.hmcts.probate.repositories.ElasticSearchRepository;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.service.payments.ServiceRequestClient;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.service.notify.NotificationClientException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.CAVEAT;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

class FetchDraftCaseServiceTest {

    @InjectMocks
    private FetchDraftCaseService fetchDraftCaseService;

    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    @Mock
    private CaveatQueryService caveatQueryService;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private ServiceRequestClient serviceRequestClient;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PaymentsService paymentsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
    }

    @Test
    void fetchCasesWithSuccessfulPayment() throws NotificationClientException {
        CaseDetails caseDetails = mock(CaseDetails.class);
        when(caseDetails.getId()).thenReturn(1L);
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder()
                        .total(1)
                        .cases(List.of(caseDetails))
                        .build());
        when(paymentsService.hasSuccessfulPaymentByCaseId("1")).thenReturn(true);
        PaymentsResponse paymentsResponse = mock(PaymentsResponse.class);
        when(paymentsResponse.getPayments()).thenReturn(List.of(PaymentDto.builder().status("success").build()));
        when(serviceRequestClient.retrievePayments(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(paymentsResponse);

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases("2023-01-01", "2023-01-31",CAVEAT));

        verify(notificationService, times(1)).sendEmailForDraftSuccessfulPayment(anyList(),
                anyString(), anyString(),any());
    }

    @Test
    void fetchCaveatCasesWithSuccessfulPayment() throws NotificationClientException {
        CaseDetails caseDetails = mock(CaseDetails.class);
        when(caseDetails.getId()).thenReturn(1L);
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder()
                        .total(1)
                        .cases(List.of(caseDetails))
                        .build());

        when(paymentsService.hasSuccessfulPaymentByCaseId("1")).thenReturn(true);
        PaymentsResponse paymentsResponse = mock(PaymentsResponse.class);
        when(paymentsResponse.getPayments()).thenReturn(List.of(PaymentDto.builder().status("success").build()));
        when(serviceRequestClient.retrievePayments(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(paymentsResponse);

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases("2023-01-01", "2023-01-31", CAVEAT));

        verify(notificationService, times(1)).sendEmailForDraftSuccessfulPayment(anyList(),
                anyString(), anyString(),any());
    }

    @Test
    void fetchCasesWithUnsuccessfulPayment() throws NotificationClientException {
        CaseDetails caseDetails = mock(CaseDetails.class);
        when(caseDetails.getId()).thenReturn(1L);
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder()
                        .total(1)
                        .cases(List.of(caseDetails))
                        .build());

        PaymentsResponse paymentsResponse = mock(PaymentsResponse.class);
        when(paymentsResponse.getPayments()).thenReturn(List.of(PaymentDto.builder().status("failed").build()));
        when(serviceRequestClient.retrievePayments(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(paymentsResponse);

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases(
                "2023-01-01", "2023-01-31", GRANT_OF_REPRESENTATION));

        verify(notificationService, never()).sendEmailForDraftSuccessfulPayment(anyList(), anyString(),
                anyString(),any());
    }

    @Test
    void fetchDraftCasesProcessesNextPageWithSuccessfulPayment() throws NotificationClientException {
        CaseDetails firstCase = mock(CaseDetails.class);
        when(firstCase.getId()).thenReturn(1L);
        CaseDetails nextCase = mock(CaseDetails.class);
        when(nextCase.getId()).thenReturn(2L);

        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder()
                        .total(1)
                        .cases(List.of(firstCase))
                        .build());

        when(elasticSearchRepository.fetchNextPage(any(), any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder()
                        .total(1)
                        .cases(List.of(nextCase))
                        .build())
                .thenReturn(SearchResult.builder()
                        .total(0)
                        .cases(Collections.emptyList())
                        .build());
        when(paymentsService.hasSuccessfulPaymentByCaseId("1")).thenReturn(true);
        when(paymentsService.hasSuccessfulPaymentByCaseId("2")).thenReturn(true);
        PaymentsResponse paymentsResponse = mock(PaymentsResponse.class);
        when(paymentsResponse.getPayments()).thenReturn(List.of(PaymentDto.builder().status("success").build()));
        when(serviceRequestClient.retrievePayments(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(paymentsResponse);

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases("2023-01-01",
                "2023-01-31", GRANT_OF_REPRESENTATION));
        verify(elasticSearchRepository, times(1))
                .fetchFirstPage(any(), any(), any(), any(), any());
        verify(elasticSearchRepository, times(2))
                .fetchNextPage(any(), any(), any(), any(), any(), any());
        verify(notificationService, times(1))
                .sendEmailForDraftSuccessfulPayment(anyList(), anyString(), anyString(), any());
    }

    @Test
    void fetchCasesContinuesAfterPaymentCheckExceptionAndSendsOnlySuccessfulCases() throws NotificationClientException {
        CaseDetails firstCase = mock(CaseDetails.class);
        when(firstCase.getId()).thenReturn(1L);
        CaseDetails secondCase = mock(CaseDetails.class);
        when(secondCase.getId()).thenReturn(2L);
        CaseDetails thirdCase = mock(CaseDetails.class);
        when(thirdCase.getId()).thenReturn(3L);

        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder()
                        .total(3)
                        .cases(List.of(firstCase, secondCase, thirdCase))
                        .build());

        when(paymentsService.hasSuccessfulPaymentByCaseId("1")).thenReturn(true);
        when(paymentsService.hasSuccessfulPaymentByCaseId("2")).thenThrow(feignException(504, "Gateway Timeout"));
        when(paymentsService.hasSuccessfulPaymentByCaseId("3")).thenReturn(true);

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases("2023-01-01",
                "2023-01-31", GRANT_OF_REPRESENTATION));

        verify(paymentsService, times(1)).hasSuccessfulPaymentByCaseId("1");
        verify(paymentsService, times(1)).hasSuccessfulPaymentByCaseId("2");
        verify(paymentsService, times(1)).hasSuccessfulPaymentByCaseId("3");

        verify(notificationService, times(1))
                .sendEmailForDraftSuccessfulPayment(
                        argThat(cases -> cases.size() == 2
                                && Long.valueOf(1L).equals(cases.get(0).getId())
                                && Long.valueOf(3L).equals(cases.get(1).getId())),
                        anyString(),
                        anyString(),
                        any());
    }

    @Test
    void fetchCasesWithNoDraftCases() throws NotificationClientException {
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder()
                        .total(0)
                        .cases(Collections.emptyList())
                        .build());
        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases(
                "2023-01-01", "2023-01-31", GRANT_OF_REPRESENTATION));

        verify(notificationService, never()).sendEmailForDraftSuccessfulPayment(anyList(), anyString(),
                anyString(),any());
    }

    @Test
    void fetchCasesWithNotificationException() throws NotificationClientException {
        CaseDetails caseDetails = mock(CaseDetails.class);
        when(caseDetails.getId()).thenReturn(1L);
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder()
                        .total(1)
                        .cases(List.of(caseDetails))
                        .build());

        when(paymentsService.hasSuccessfulPaymentByCaseId("1")).thenReturn(true);
        PaymentsResponse paymentsResponse = mock(PaymentsResponse.class);
        when(paymentsResponse.getPayments()).thenReturn(List.of(PaymentDto.builder().status("success").build()));
        when(serviceRequestClient.retrievePayments(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(paymentsResponse);

        doThrow(new NotificationClientException("Error")).when(notificationService)
                .sendEmailForDraftSuccessfulPayment(anyList(), anyString(), anyString(),any());

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases(
                "2023-01-01", "2023-01-31",GRANT_OF_REPRESENTATION));

        verify(notificationService, times(1))
                .sendEmailForDraftSuccessfulPayment(anyList(), anyString(), anyString(), any());
    }

    private static FeignException feignException(int status, String reason) {
        return FeignException.errorStatus(
                "hasSuccessfulPaymentByCaseId",
                Response.builder()
                        .status(status)
                        .reason(reason)
                        .request(Request.create(
                                Request.HttpMethod.GET,
                                "/payments",
                                Map.of(),
                                null,
                                StandardCharsets.UTF_8,
                                null
                        ))
                        .build()
        );
    }
}