package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.NotificationType.HSE_REMINDER;

class HseReminderNotificationTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private Document mockDocument;

    private HseReminderNotification underTest;

    AutoCloseable closeableMocks;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);
        underTest = new HseReminderNotification(notificationService);

        when(caseDetails.getState()).thenReturn("CasePrinted");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void returnsCorrectQueryTemplatePath() {
        String result = underTest.getQueryTemplate();

        assertEquals("templates/elasticsearch/caseMatching/hse_reminder_query.json", result);
    }

    @Test
    void matchesTypeShouldReturnTrueWhenTypeIsHseReminder() {
        boolean result = underTest.matchesType(HSE_REMINDER);

        assertTrue(result);
    }

    @Test
    void shouldHseReminderEmail() throws NotificationClientException {
        when(notificationService.sendHseReminderEmail(caseDetails)).thenReturn(mockDocument);

        Document result = underTest.sendNotification(caseDetails);

        verify(notificationService, times(1)).sendHseReminderEmail(caseDetails);
        assertEquals(mockDocument, result);
    }

    @Test
    void returnsEventSummary() {
        String result = underTest.getEventSummary();

        assertEquals("HSE Reminder", result);
    }

    @Test
    void returnsEventDescription() {
        String result = underTest.getEventDescription();

        assertEquals("HSE Reminder", result);
    }

    @Test
    void returnsFailureEventDescription() {
        String result = underTest.getFailureEventDescription();

        assertEquals("Failed to send HSE reminder", result);
    }

    @Test
    void returnsFailureEventSummary() {
        String result = underTest.getFailureEventSummary();

        assertEquals("Failed to send HSE reminder", result);
    }

    @Test
    void returnsEventId() {
        EventId result = underTest.getEventId();

        assertEquals(EventId.AUTO_NOTIFICATION_HSE_REMINDER, result);
    }

    @Test
    void returnsNotificationType() {
        NotificationType result = underTest.getType();

        assertEquals(HSE_REMINDER, result);
    }

    @Test
    void acceptsShouldReturnFalseWhenEvidenceHandledIsNotYes() {
        when(caseDetails.getData()).thenReturn(Map.of("evidenceHandled", "No"));

        boolean result = underTest.accepts().test(caseDetails);

        assertFalse(result);
    }

    @Test
    void acceptsShouldReturnFalseWhenEvidenceHandledDateIsInvalid() {
        when(caseDetails.getData()).thenReturn(Map.of("evidenceHandled", "Yes", "evidenceHandledDate", "invalid-date"));

        boolean result = underTest.accepts().test(caseDetails);

        assertFalse(result);
    }

    @Test
    void acceptsShouldReturnFalseWhenEvidenceHandledDateDoesNotMatchReferenceDate() {
        underTest.setReferenceDate(LocalDate.of(2023, 10, 01));
        when(caseDetails.getData()).thenReturn(Map.of("evidenceHandled", "Yes", "evidenceHandledDate", "2023-09-30"));

        boolean result = underTest.accepts().test(caseDetails);

        assertFalse(result);
    }

    @Test
    void acceptsShouldReturnTrueWhenEvidenceHandledIsYesAndDatesMatch() {
        underTest.setReferenceDate(LocalDate.of(2023, 10, 01));
        when(caseDetails.getData()).thenReturn(Map.of("evidenceHandled", "Yes", "evidenceHandledDate", "2023-10-01"));

        boolean result = underTest.accepts().test(caseDetails);

        assertTrue(result);
    }

    @Test
    void returnsFalseWhenStateIsBOGrantIssued() {
        underTest.setReferenceDate(LocalDate.of(2023, 10, 01));
        when(caseDetails.getData()).thenReturn(Map.of("evidenceHandled", "Yes", "evidenceHandledDate", "2023-10-01"));
        when(caseDetails.getState()).thenReturn("BOGrantIssued");

        boolean result = underTest.accepts().test(caseDetails);

        assertFalse(result);
    }

    @Test
    void returnsFalseWhenStateIsCaseClosed() {
        underTest.setReferenceDate(LocalDate.of(2023, 10, 01));
        when(caseDetails.getData()).thenReturn(Map.of("evidenceHandled", "Yes", "evidenceHandledDate", "2023-10-01"));
        when(caseDetails.getState()).thenReturn("BOCaseClosed");

        boolean result = underTest.accepts().test(caseDetails);

        assertFalse(result);
    }

    @Test
    void returnsTrueWhenStateIsBOCaseStopped() {
        underTest.setReferenceDate(LocalDate.of(2023, 10, 01));
        when(caseDetails.getData()).thenReturn(Map.of("evidenceHandled", "Yes", "evidenceHandledDate", "2023-10-01"));
        when(caseDetails.getState()).thenReturn("BOCaseStopped");

        boolean result = underTest.accepts().test(caseDetails);

        assertTrue(result);
    }
}