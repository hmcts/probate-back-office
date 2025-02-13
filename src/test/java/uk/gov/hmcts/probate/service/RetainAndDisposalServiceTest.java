package uk.gov.hmcts.probate.service;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.service.notify.NotificationClientException;

@ExtendWith(MockitoExtension.class)
class RetainAndDisposalServiceTest {

    @Mock
    private CaseQueryService caseQueryService;

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

    private ReturnedCaseDetails caseDetails;

    @BeforeEach
    void setUp() {
        caseDetails = mock(ReturnedCaseDetails.class);
    }

    @Test
    void shouldSendEmailsForInactiveCasesSuccessfully() throws NotificationClientException {
        List<ReturnedCaseDetails> cases = List.of(caseDetails);
        when(caseQueryService.findInactiveCaseForDisposalReminder(any(), any())).thenReturn(cases);

        retainAndDisposalService.sendEmailForInactiveCase(SWITCH_DATE, RUN_DATE, NOTIFICATION_INACTIVE_PERIOD);

        verify(notificationService, times(1)).sendDisposalReminderEmail(caseDetails);
        verify(caseQueryService, times(1)).findInactiveCaseForDisposalReminder(any(), any());
    }

    @Test
    void shouldHandleEmptyCaseListForEmailSending() throws NotificationClientException {
        when(caseQueryService.findInactiveCaseForDisposalReminder(any(), any())).thenReturn(Collections.emptyList());

        retainAndDisposalService.sendEmailForInactiveCase(SWITCH_DATE, RUN_DATE, NOTIFICATION_INACTIVE_PERIOD);

        verify(notificationService, never()).sendDisposalReminderEmail(any());
        verify(caseQueryService, times(1)).findInactiveCaseForDisposalReminder(any(), any());
    }

    @Test
    void shouldHandleNotificationExceptionGracefully() throws NotificationClientException {
        List<ReturnedCaseDetails> cases = List.of(caseDetails);
        when(caseQueryService.findInactiveCaseForDisposalReminder(any(), any())).thenReturn(cases);
        doThrow(new NotificationClientException("Email Failure"))
                .when(notificationService).sendDisposalReminderEmail(any());

        assertDoesNotThrow(() -> retainAndDisposalService
                .sendEmailForInactiveCase(SWITCH_DATE, RUN_DATE, NOTIFICATION_INACTIVE_PERIOD));

        verify(notificationService, times(1)).sendDisposalReminderEmail(caseDetails);
    }

    @Test
    void shouldDisposeInactiveCasesSuccessfully() {
        List<ReturnedCaseDetails> cases = List.of(caseDetails);
        when(caseQueryService.findDeletedGOPCaseForDisposal(any(), any())).thenReturn(cases);
        when(caseQueryService.findInactiveGOPCaseForDisposal(any(), any())).thenReturn(cases);
        when(caseQueryService.findInactiveCaveatCaseForDisposal(any(), any())).thenReturn(cases);
        String runDate = "2024-06-07";
        retainAndDisposalService
                .disposeInactiveCase(SWITCH_DATE, runDate, START_DATE,
                        NOTIFICATION_INACTIVE_PERIOD, DISPOSAL_GRACE_PERIOD);

        verify(disposalCCDService, times(2)).disposeGOPCase(eq(caseDetails), any(), any());
        verify(disposalCCDService, times(1)).disposeCaveatCase(eq(caseDetails), any(), any());
    }

    @Test
    void shouldHandleEmptyCaseListForDisposal() {
        when(caseQueryService.findDeletedGOPCaseForDisposal(any(), any())).thenReturn(Collections.emptyList());
        when(caseQueryService.findInactiveGOPCaseForDisposal(any(), any())).thenReturn(Collections.emptyList());
        when(caseQueryService.findInactiveCaveatCaseForDisposal(any(), any())).thenReturn(Collections.emptyList());
        String runDate = "2024-06-07";
        retainAndDisposalService
                .disposeInactiveCase(SWITCH_DATE, runDate, START_DATE,
                        NOTIFICATION_INACTIVE_PERIOD, DISPOSAL_GRACE_PERIOD);

        verify(disposalCCDService, never()).disposeGOPCase(any(), any(), any());
        verify(disposalCCDService, never()).disposeCaveatCase(any(), any(), any());
    }

    @Test
    void shouldHandleDisposalExceptionGracefully() {
        List<ReturnedCaseDetails> cases = List.of(caseDetails);
        when(caseQueryService.findDeletedGOPCaseForDisposal(any(), any())).thenReturn(cases);
        when(caseQueryService.findInactiveGOPCaseForDisposal(any(), any())).thenReturn(cases);
        when(caseQueryService.findInactiveCaveatCaseForDisposal(any(), any())).thenReturn(cases);
        doThrow(new RuntimeException("Disposal GOP Failure"))
                .when(disposalCCDService).disposeGOPCase(any(), any(), any());
        doThrow(new RuntimeException("Disposal Caveat Failure"))
                .when(disposalCCDService).disposeCaveatCase(any(), any(), any());
        String runDate = "2024-06-07";
        assertDoesNotThrow(() -> retainAndDisposalService
                .disposeInactiveCase(SWITCH_DATE, runDate, START_DATE,
                        NOTIFICATION_INACTIVE_PERIOD, DISPOSAL_GRACE_PERIOD));

        verify(disposalCCDService, times(2)).disposeGOPCase(eq(caseDetails), any(), any());
        verify(disposalCCDService, times(1)).disposeCaveatCase(eq(caseDetails), any(), any());
    }
}
