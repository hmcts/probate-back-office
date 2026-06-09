package uk.gov.hmcts.probate.service;

import static org.mockito.ArgumentMatchers.anyBoolean;
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
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.service.notify.NotificationClientException;

@ExtendWith(MockitoExtension.class)
class RetainAndDisposalServiceTest {

    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private DisposalCCDService disposalCCDService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private CaseData caseDataMock;

    @InjectMocks
    private RetainAndDisposalService retainAndDisposalService;

    private static final String SWITCH_DATE = "2024-01-01";
    private static final String RUN_DATE = "2024-02-07";
    private static final long NOTIFICATION_INACTIVE_PERIOD = 90;
    private static final long DISPOSAL_GRACE_PERIOD = 90;
    private static final String START_DATE = "1900-01-01";

    private SecurityDTO mockSecurityDTO;
    private CaseDetails mockCaseDetails;

    @BeforeEach
    void setUp() {
        mockCaseDetails = mock(CaseDetails.class);
        mockSecurityDTO = mock(SecurityDTO.class);

        mockCaseDetails = CaseDetails.builder()
                .id(123L)
                .state("Draft")
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
    void shouldSendEmailsForInactiveCasesSuccessfully() throws NotificationClientException {
        retainAndDisposalService.sendEmailForInactiveCase(SWITCH_DATE, RUN_DATE, NOTIFICATION_INACTIVE_PERIOD,
                false);

        verify(notificationService, times(1)).sendDisposalReminderEmail(mockCaseDetails,
                false);
        verify(elasticSearchRepository, times(1))
                .fetchFirstPage(any(), any(), any(), any(), any());
        verify(elasticSearchRepository, times(1))
                .fetchNextPage(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldSendEmailsForInactiveCasesSuccessfullyWhenSwitchDateSameAsRunDate() throws NotificationClientException {
        retainAndDisposalService.sendEmailForInactiveCase(RUN_DATE, RUN_DATE, NOTIFICATION_INACTIVE_PERIOD,
                false);

        verify(notificationService, times(1)).sendDisposalReminderEmail(mockCaseDetails,
                false);
        verify(elasticSearchRepository, times(1))
                .fetchFirstPage(any(), any(), any(), any(), any());
        verify(elasticSearchRepository, times(1))
                .fetchNextPage(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldHandleNotificationExceptionGracefully() throws NotificationClientException {
        doThrow(new NotificationClientException("Email failed")).when(notificationService)
                .sendDisposalReminderEmail(any(), anyBoolean());

        assertDoesNotThrow(() -> retainAndDisposalService
                .sendEmailForInactiveCase(SWITCH_DATE, RUN_DATE, NOTIFICATION_INACTIVE_PERIOD, false));
        verify(notificationService, times(1)).sendDisposalReminderEmail(any(), anyBoolean());
    }

    @Test
    void shouldDisposeInactiveCasesSuccessfully() {
        String runDate = "2024-06-07";
        retainAndDisposalService
                .disposeInactiveCase(SWITCH_DATE, runDate, START_DATE,
                        NOTIFICATION_INACTIVE_PERIOD, DISPOSAL_GRACE_PERIOD);

        verify(disposalCCDService, times(2)).disposeGOPCase(any(), any(), any());
        verify(disposalCCDService, times(3)).disposeCaveatCase(any(), any(), any());
    }

    @Test
    void shouldSkipDisposalWhenDraftDisposalConditionFails() {
        retainAndDisposalService.disposeInactiveCase("2024-02-01", "2024-01-01", "2024-01-01",
                NOTIFICATION_INACTIVE_PERIOD, DISPOSAL_GRACE_PERIOD);
        verify(disposalCCDService, times(1)).disposeGOPCase(any(), any(), any());
    }

    @Test
    void shouldHandleExceptionDuringFetch() {
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("ElasticSearch error"));
        String runDate = "2024-06-07";
        assertDoesNotThrow(() -> retainAndDisposalService
                .disposeInactiveCase(SWITCH_DATE, runDate, START_DATE,
                        NOTIFICATION_INACTIVE_PERIOD, DISPOSAL_GRACE_PERIOD));
        verify(disposalCCDService, never()).disposeGOPCase(any(), any(), any());
    }

    @Test
    void shouldHandleEmptyCaseListForEmailSending() throws NotificationClientException {
        SearchResult emptySearchResult = SearchResult.builder().total(0).cases(Collections.emptyList()).build();
        when(elasticSearchRepository.fetchFirstPage(any(), any(), any(), any(), any())).thenReturn(emptySearchResult);

        retainAndDisposalService.sendEmailForInactiveCase(SWITCH_DATE, RUN_DATE, NOTIFICATION_INACTIVE_PERIOD,
                false);

        verify(notificationService, never()).sendDisposalReminderEmail(any(), anyBoolean());
        verify(elasticSearchRepository, times(1))
                .fetchFirstPage(any(), any(), any(), any(), any());
    }
}
