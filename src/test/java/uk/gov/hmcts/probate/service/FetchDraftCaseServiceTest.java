package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.payments.PaymentDto;
import uk.gov.hmcts.probate.model.payments.PaymentsResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.payments.ServiceRequestClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class FetchDraftCaseServiceTest {

    @InjectMocks
    private FetchDraftCaseService fetchDraftCaseService;

    @Mock
    private CaseQueryService caseQueryService;
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
        ReturnedCaseDetails caseDetails = mock(ReturnedCaseDetails.class);
        when(caseDetails.getId()).thenReturn(1L);
        when(caseQueryService.findDraftCases(anyString(), anyString())).thenReturn(List.of(caseDetails));

        PaymentsResponse paymentsResponse = mock(PaymentsResponse.class);
        when(paymentsResponse.getPayments()).thenReturn(List.of(PaymentDto.builder().status("success").build()));
        when(serviceRequestClient.retrievePayments(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(paymentsResponse);

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases("2023-01-01", "2023-01-31",false));

        verify(notificationService, times(1)).sendEmailForDraftSuccessfulPayment(anyList(),
                anyString(), anyString(),anyBoolean());
    }

    @Test
    void fetchCaveatCasesWithSuccessfulPayment() throws NotificationClientException {
        ReturnedCaveatDetails caseDetails = mock(ReturnedCaveatDetails.class);
        when(caseDetails.getId()).thenReturn(1L);
        when(caveatQueryService.findCaveatDraftCases(anyString(), anyString(), any())).thenReturn(List.of(caseDetails));

        PaymentsResponse paymentsResponse = mock(PaymentsResponse.class);
        when(paymentsResponse.getPayments()).thenReturn(List.of(PaymentDto.builder().status("success").build()));
        when(serviceRequestClient.retrievePayments(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(paymentsResponse);

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases("2023-01-01", "2023-01-31", true));

        verify(notificationService, times(1)).sendEmailForDraftSuccessfulPayment(anyList(),
                anyString(), anyString(),anyBoolean());
    }

    @Test
    void fetchCasesWithUnsuccessfulPayment() throws NotificationClientException {
        ReturnedCaseDetails caseDetails = mock(ReturnedCaseDetails.class);
        when(caseDetails.getId()).thenReturn(1L);
        when(caseQueryService.findDraftCases(anyString(), anyString())).thenReturn(List.of(caseDetails));

        PaymentsResponse paymentsResponse = mock(PaymentsResponse.class);
        when(paymentsResponse.getPayments()).thenReturn(List.of(PaymentDto.builder().status("failed").build()));
        when(serviceRequestClient.retrievePayments(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(paymentsResponse);

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases("2023-01-01", "2023-01-31", false));

        verify(notificationService, never()).sendEmailForDraftSuccessfulPayment(anyList(), anyString(),
                anyString(),anyBoolean());
    }

    @Test
    void fetchCasesWithNoDraftCases() throws NotificationClientException {
        when(caseQueryService.findDraftCases(anyString(), anyString())).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases("2023-01-01", "2023-01-31", false));

        verify(notificationService, never()).sendEmailForDraftSuccessfulPayment(anyList(), anyString(),
                anyString(),anyBoolean());
    }

    @Test
    void fetchCasesWithNotificationException() throws NotificationClientException {
        ReturnedCaseDetails caseDetails = mock(ReturnedCaseDetails.class);
        when(caseDetails.getId()).thenReturn(1L);
        when(caseQueryService.findDraftCases(anyString(), anyString())).thenReturn(List.of(caseDetails));

        PaymentsResponse paymentsResponse = mock(PaymentsResponse.class);
        when(paymentsResponse.getPayments()).thenReturn(List.of(PaymentDto.builder().status("success").build()));
        when(serviceRequestClient.retrievePayments(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(paymentsResponse);

        doThrow(new NotificationClientException("Error")).when(notificationService)
                .sendEmailForDraftSuccessfulPayment(anyList(), anyString(), anyString(),anyBoolean());

        assertDoesNotThrow(() -> fetchDraftCaseService.fetchDraftCases("2023-01-01", "2023-01-31",false));

        verify(notificationService, times(1))
                .sendEmailForDraftSuccessfulPayment(anyList(), anyString(), anyString(), anyBoolean());
    }
}