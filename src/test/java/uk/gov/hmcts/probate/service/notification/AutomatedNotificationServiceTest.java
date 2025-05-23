package uk.gov.hmcts.probate.service.notification;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.NotificationType.FIRST_STOP_REMINDER;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.repositories.ElasticSearchRepository;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.service.notify.NotificationClientException;

@ExtendWith(SpringExtension.class)
class AutomatedNotificationServiceTest {

    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    @Mock
    private AutomatedNotificationCCDService automatedNotificationCCDService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private FirstStopReminderNotification firstStopReminderNotification;

    @Mock
    private SecondStopReminderNotification secondStopReminderNotification;

    @Mock
    private StartEventResponse startEventResponse;

    @InjectMocks
    private AutomatedNotificationService automatedNotificationService;


    private static final String JOB_DATE = "2025-02-07";

    private CaseDetails mockCaseDetails;
    private SecurityDTO mockSecurityDTO;

    @BeforeEach
    void setUp() throws NotificationClientException {
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
        when(elasticSearchRepository.fetchNextPage(any(), any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder()
                        .total(0)
                        .cases(Collections.emptyList())
                        .build());

        when(firstStopReminderNotification.sendEmail(any())).thenReturn(mock(Document.class));
        when(firstStopReminderNotification.matchesType(FIRST_STOP_REMINDER)).thenReturn(true);

        when(automatedNotificationCCDService
                .startEvent(eq("123"), eq(mockSecurityDTO), eq(firstStopReminderNotification)))
                .thenReturn(startEventResponse);
        when(startEventResponse.getCaseDetails()).thenReturn(mockCaseDetails);

        Predicate<CaseDetails> accepts = cd -> true;
        when(firstStopReminderNotification.accepts()).thenReturn(accepts);

        automatedNotificationService = new AutomatedNotificationService(
                List.of(firstStopReminderNotification, secondStopReminderNotification),
                securityUtils,
                elasticSearchRepository,
                automatedNotificationCCDService);
    }

    @Test
    void shouldSendFirstStopReminderSuccessfully() throws NotificationClientException {
        automatedNotificationService.sendNotification(JOB_DATE, FIRST_STOP_REMINDER);

        verify(firstStopReminderNotification, times(1)).sendEmail(mockCaseDetails);
        verify(elasticSearchRepository, times(1))
                .fetchFirstPage(any(), any(), any(), any(), any());
        verify(elasticSearchRepository, times(1))
                .fetchNextPage(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldHandleNotificationExceptionGracefully() throws NotificationClientException {
        doThrow(new NotificationClientException("Email failed")).when(firstStopReminderNotification)
                .sendEmail(any());

        assertDoesNotThrow(() -> automatedNotificationService
                .sendNotification(JOB_DATE, FIRST_STOP_REMINDER));
        verify(firstStopReminderNotification, times(1)).sendEmail(any());
    }

    @Test
    void shouldHandleExceptionDuringFetch() {
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("ElasticSearch error"));
        assertDoesNotThrow(() -> automatedNotificationService
                .sendNotification(JOB_DATE, FIRST_STOP_REMINDER));
        verify(automatedNotificationCCDService, never()).saveNotification(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldHandleEmptyCaseListForEmailSending() throws NotificationClientException {
        SearchResult emptySearchResult = SearchResult.builder().total(0).cases(Collections.emptyList()).build();
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any())).thenReturn(emptySearchResult);

        automatedNotificationService.sendNotification(JOB_DATE, FIRST_STOP_REMINDER);

        verify(firstStopReminderNotification, never()).sendEmail(any());
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

        when(elasticSearchRepository.fetchNextPage(any(), any(), any(), any(), any(), any()))
                .thenReturn(firstNextPage)
                .thenReturn(emptyNextPage);

        StartEventResponse secondResponse = mock(StartEventResponse.class);
        when(automatedNotificationCCDService.startEvent(
                eq("456"), eq(mockSecurityDTO), eq(firstStopReminderNotification)))
                .thenReturn(secondResponse);
        when(secondResponse.getCaseDetails()).thenReturn(secondCase);

        automatedNotificationService.sendNotification(JOB_DATE, FIRST_STOP_REMINDER);

        verify(firstStopReminderNotification, times(1)).sendEmail(mockCaseDetails);
        verify(firstStopReminderNotification, times(1)).sendEmail(secondCase);
        verify(automatedNotificationCCDService, times(2)).saveNotification(any(), any(), any(), any(), any(), any());
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

        StartEventResponse secondResponse = mock(StartEventResponse.class);
        when(automatedNotificationCCDService.startEvent(
                eq("789"), eq(mockSecurityDTO), eq(firstStopReminderNotification)))
                .thenReturn(secondResponse);
        when(secondResponse.getCaseDetails()).thenReturn(secondCase);

        when(firstStopReminderNotification.sendEmail(secondCase))
                .thenThrow(new NotificationClientException("fail"));

        assertDoesNotThrow(() -> automatedNotificationService.sendNotification(JOB_DATE, FIRST_STOP_REMINDER));

        verify(firstStopReminderNotification, times(1)).sendEmail(mockCaseDetails);
        verify(firstStopReminderNotification, times(1)).sendEmail(secondCase);
    }


    @Test
    void shouldStopPaginationWhenSubsequentSearchReturnsEmpty() throws NotificationClientException {
        when(elasticSearchRepository.fetchNextPage(any(), any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder().total(0).cases(Collections.emptyList()).build());

        automatedNotificationService.sendNotification(JOB_DATE, FIRST_STOP_REMINDER);

        verify(firstStopReminderNotification, times(1)).sendEmail(mockCaseDetails);
        verify(firstStopReminderNotification, times(1)).sendEmail(any());
    }

    @Test
    void shouldSaveFailedNotificationWhenSendEmailReturnsNull() throws NotificationClientException {
        when(firstStopReminderNotification.sendEmail(mockCaseDetails)).thenReturn(null);

        when(elasticSearchRepository.fetchNextPage(any(), any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder().total(0).cases(Collections.emptyList()).build());

        automatedNotificationService.sendNotification(JOB_DATE, FIRST_STOP_REMINDER);

        verify(automatedNotificationCCDService, times(1))
                .saveFailedNotification(eq("123"), any(SecurityDTO.class),
                        eq(firstStopReminderNotification), eq(startEventResponse));
        verify(automatedNotificationCCDService, never())
                .saveNotification(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldSaveFailedNotificationWhenSendEmailThrows() throws NotificationClientException {
        when(firstStopReminderNotification.sendEmail(mockCaseDetails))
                .thenThrow(new NotificationClientException("notification error"));

        when(elasticSearchRepository.fetchNextPage(any(), any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder().total(0).cases(Collections.emptyList()).build());

        automatedNotificationService.sendNotification(JOB_DATE, FIRST_STOP_REMINDER);

        verify(automatedNotificationCCDService, times(1))
                .saveFailedNotification(eq("123"), any(SecurityDTO.class),
                        eq(firstStopReminderNotification), eq(startEventResponse));
        verify(automatedNotificationCCDService, never())
                .saveNotification(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldHandleExceptionInSaveNotificationGracefully() throws NotificationClientException {
        Document doc = mock(Document.class);
        when(firstStopReminderNotification.sendEmail(mockCaseDetails)).thenReturn(doc);

        doThrow(new RuntimeException("ccd fail"))
                .when(automatedNotificationCCDService)
                .saveNotification(eq(mockCaseDetails), eq("123"), any(SecurityDTO.class), eq(doc),
                        eq(firstStopReminderNotification), eq(startEventResponse));

        when(elasticSearchRepository.fetchNextPage(any(), any(), any(), any(), any(), any()))
                .thenReturn(SearchResult.builder().total(0).cases(Collections.emptyList()).build());

        assertDoesNotThrow(() ->
                automatedNotificationService.sendNotification(JOB_DATE, FIRST_STOP_REMINDER)
        );

        verify(automatedNotificationCCDService, never())
                .saveFailedNotification(any(), any(), any(), any());
        verify(automatedNotificationCCDService, times(1))
                .saveNotification(eq(mockCaseDetails), eq("123"), any(SecurityDTO.class), eq(doc),
                        eq(firstStopReminderNotification), eq(startEventResponse));
    }
}
