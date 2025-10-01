package uk.gov.hmcts.probate.service;

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
import uk.gov.hmcts.probate.service.payments.ServiceRequestClient;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.service.notify.NotificationClientException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
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

        PaymentsResponse paymentsResponse = mock(PaymentsResponse.class);
        when(paymentsResponse.getPayments()).thenReturn(List.of(PaymentDto.builder().status("success").build()));
        when(serviceRequestClient.retrievePayments(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(paymentsResponse);

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases("2023-01-01",
                "2023-01-31", GRANT_OF_REPRESENTATION));

        verify(notificationService, times(1))
                .sendEmailForDraftSuccessfulPayment(anyList(), anyString(), anyString(), any());
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
}