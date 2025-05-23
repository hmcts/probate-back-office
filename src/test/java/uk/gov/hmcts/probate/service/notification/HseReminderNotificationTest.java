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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void matchesTypeShouldReturnTrueWhenTypeIsHseReminder() {
        boolean result = underTest.matchesType(HSE_REMINDER);

        assertEquals(true, result);
    }

    @Test
    void shouldHseReminderEmail() throws NotificationClientException {
        when(notificationService.sendHseReminderEmail(caseDetails)).thenReturn(mockDocument);

        Document result = underTest.sendEmail(caseDetails);

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

        assertEquals(EventId.AUTOMATED_NOTIFICATION, result);
    }

    @Test
    void returnsNotificationType() {
        NotificationType result = underTest.getType();

        assertEquals(HSE_REMINDER, result);
    }
}