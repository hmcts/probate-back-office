package uk.gov.hmcts.probate.service.notification;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.repositories.ElasticSearchRepository;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.service.notify.NotificationClientException;

@ExtendWith(MockitoExtension.class)
class AutomatedNotificationServiceTest {

    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AutomatedNotificationCCDService automatedNotificationCCDService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private CaseData caseDataMock;

    @InjectMocks
    private AutomatedNotificationService automatedNotificationService;


    private static final String JOB_DATE = "2025-02-07";

    private SecurityDTO mockSecurityDTO;
    private CaseDetails mockCaseDetails;

    @BeforeEach
    void setUp() {
        mockCaseDetails = mock(CaseDetails.class);
        mockSecurityDTO = mock(SecurityDTO.class);

        mockCaseDetails = CaseDetails.builder()
                .id(123L)
                .state("BOCaseStopped")
                .data(Map.of("applicationType", "Solicitor"))
                .build();

        when(securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO()).thenReturn(mockSecurityDTO);
        when(mockSecurityDTO.getAuthorisation()).thenReturn("authToken");
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder()
                        .total(1)
                        .cases(List.of(mockCaseDetails))
                        .build());
    }

    @Test
    void shouldSendFirstStopReminderSuccessfully() throws NotificationClientException {
        automatedNotificationService.sendStopReminder(JOB_DATE, true);

        verify(notificationService, times(1)).sendStopReminderEmail(mockCaseDetails, true);
        verify(elasticSearchRepository, times(1))
                .fetchFirstPage(any(), any(), any(), any(), any());
        verify(elasticSearchRepository, times(1))
                .fetchNextPage(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldHandleNotificationExceptionGracefully() throws NotificationClientException {
        doThrow(new NotificationClientException("Email failed")).when(notificationService)
                .sendStopReminderEmail(any(), anyBoolean());

        assertDoesNotThrow(() -> automatedNotificationService
                .sendStopReminder(JOB_DATE, true));
        verify(notificationService, times(1)).sendStopReminderEmail(any(), anyBoolean());
    }

    @Test
    void shouldHandleExceptionDuringFetch() {
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("ElasticSearch error"));
        assertDoesNotThrow(() -> automatedNotificationService
                .sendStopReminder(JOB_DATE, true));
        verify(automatedNotificationCCDService, never()).saveNotification(any(), any(), any(), any(), anyBoolean());
    }

    @Test
    void shouldHandleEmptyCaseListForEmailSending() throws NotificationClientException {
        SearchResult emptySearchResult = SearchResult.builder().total(0).cases(Collections.emptyList()).build();
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any())).thenReturn(emptySearchResult);

        automatedNotificationService.sendStopReminder(JOB_DATE, true);

        verify(notificationService, never()).sendStopReminderEmail(any(), anyBoolean());
        verify(elasticSearchRepository, times(1))
                .fetchFirstPage(any(), any(), any(), any(), any());
    }

    @Test
    void shouldContinueProcessingIfSubsequentSearchReturnsCases() throws NotificationClientException {
        CaseDetails secondCase = CaseDetails.builder()
                .id(456L)
                .state("BOCaseStopped")
                .data(Map.of("applicationType", "Solicitor"))
                .build();

        SearchResult firstNextPage = SearchResult.builder()
                .total(1)
                .cases(List.of(secondCase))
                .build();

        SearchResult emptyNextPage = SearchResult.builder()
                .total(0)
                .cases(Collections.emptyList())
                .build();

        // Chain: first returns a page, second ends the loop
        when(elasticSearchRepository.fetchNextPage(any(), any(), any(), any(), any(), any()))
                .thenReturn(firstNextPage)
                .thenReturn(emptyNextPage);

        automatedNotificationService.sendStopReminder(JOB_DATE, true);

        verify(notificationService, times(1)).sendStopReminderEmail(mockCaseDetails, true);
        verify(notificationService, times(1)).sendStopReminderEmail(secondCase, true);
        verify(automatedNotificationCCDService, times(2)).saveNotification(any(), any(), any(), any(), eq(true));
    }


    @Test
    void shouldHandleExceptionInSubsequentPageEmailSending() throws NotificationClientException {
        CaseDetails secondCase = CaseDetails.builder()
                .id(789L)
                .state("BOCaseStopped")
                .data(Map.of("applicationType", "Solicitor"))
                .build();

        SearchResult firstNextPage = SearchResult.builder()
                .total(1)
                .cases(List.of(secondCase))
                .build();

        SearchResult emptyNextPage = SearchResult.builder()
                .total(0)
                .cases(Collections.emptyList())
                .build();

        when(elasticSearchRepository.fetchNextPage(any(), any(), any(), any(), any(), any()))
                .thenReturn(firstNextPage)
                .thenReturn(emptyNextPage);

        when(notificationService.sendStopReminderEmail(eq(secondCase), anyBoolean()))
                .thenThrow(new NotificationClientException("fail"));

        assertDoesNotThrow(() -> automatedNotificationService.sendStopReminder(JOB_DATE, true));

        verify(notificationService, times(1)).sendStopReminderEmail(mockCaseDetails, true);
        verify(notificationService, times(1)).sendStopReminderEmail(secondCase, true);
    }


    @Test
    void shouldStopPaginationWhenSubsequentSearchReturnsEmpty() throws NotificationClientException {
        when(elasticSearchRepository.fetchNextPage(any(), any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder().total(0).cases(Collections.emptyList()).build());

        automatedNotificationService.sendStopReminder(JOB_DATE, true);

        verify(notificationService, times(1)).sendStopReminderEmail(mockCaseDetails, true);
        verify(notificationService, times(1)).sendStopReminderEmail(any(), anyBoolean());
    }
}
