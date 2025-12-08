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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.NotificationType.DORMANT_REMINDER;

class DormantReminderNotificationTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private Document mockDocument;

    private DormantReminderNotification underTest;

    AutoCloseable closeableMocks;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);
        underTest = new DormantReminderNotification(notificationService);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void returnsCorrectQueryTemplatePath() {
        String result = underTest.getQueryTemplate();

        assertEquals("templates/elasticsearch/caseMatching/dormant_reminder_query.json", result);
    }

    @Test
    void matchesTypeShouldReturnTrueWhenTypeIsHseReminder() {
        boolean result = underTest.matchesType(DORMANT_REMINDER);

        assertEquals(true, result);
    }

    @Test
    void shouldDormantReminderEmail() throws NotificationClientException {
        when(notificationService.sendDormantReminder(caseDetails)).thenReturn(mockDocument);

        Document result = underTest.sendNotification(caseDetails);

        verify(notificationService, times(1)).sendDormantReminder(caseDetails);
        assertEquals(mockDocument, result);
    }

    @Test
    void returnsEventSummary() {
        String result = underTest.getEventSummary();

        assertEquals("Dormant 12-month Reminder (AN) sent", result);
    }

    @Test
    void returnsEventDescription() {
        String result = underTest.getEventDescription();

        assertEquals("Dormant 12-month Reminder (AN) sent", result);
    }

    @Test
    void returnsFailureEventDescription() {
        String result = underTest.getFailureEventDescription();

        assertEquals("Failed to send Dormant 12-month Reminder (AN)", result);
    }

    @Test
    void returnsFailureEventSummary() {
        String result = underTest.getFailureEventSummary();

        assertEquals("Failed to send Dormant 12-month Reminder (AN)", result);
    }

    @Test
    void shouldSendDormantReminder() throws NotificationClientException {
        when(notificationService.sendDormantReminder(caseDetails)).thenReturn(mockDocument);

        Document result = underTest.sendNotification(caseDetails);

        verify(notificationService, times(1)).sendDormantReminder(caseDetails);
        assertEquals(mockDocument, result);
    }

    @Test
    void returnsEventId() {
        EventId result = underTest.getEventId();

        assertEquals(EventId.AUTO_NOTIFICATION_DORMANT_REMINDER, result);
    }

    @Test
    void returnsNotificationType() {
        NotificationType result = underTest.getType();

        assertEquals(DORMANT_REMINDER, result);
    }

    @Test
    void acceptsShouldReturnFalseWhenTheStateNoDormant() {
        when(caseDetails.getState()).thenReturn("xxxx");

        boolean result = underTest.accepts().test(caseDetails);

        assertEquals(false, result);
    }

    @Test
    void acceptsShouldReturnFalseWhenMoveToDormantDateTimeIsInvalid() {
        when(caseDetails.getState()).thenReturn("Dormant");
        when(caseDetails.getData()).thenReturn(Map.of("lastModifiedDateForDormant", "invalid-date"));

        boolean result = underTest.accepts().test(caseDetails);

        assertEquals(false, result);
    }

    @Test
    void acceptsShouldReturnFalseWhenMoveToDormantDateTimeIsAfterReferenceDatePlusOneDay() {
        underTest.setReferenceDate(LocalDate.of(2023, 10, 01));
        when(caseDetails.getState()).thenReturn("Dormant");
        when(caseDetails.getData()).thenReturn(Map.of("lastModifiedDateForDormant", "2023-10-03T00:00:00"));

        boolean result = underTest.accepts().test(caseDetails);

        assertEquals(false, result);
    }


    @Test
    void acceptsShouldReturnFalseWhenStateIsNotDormant() {
        underTest.setReferenceDate(LocalDate.of(2023, 10, 01));
        when(caseDetails.getState()).thenReturn("BOGrantIssued");
        when(caseDetails.getData()).thenReturn(Map.of("lastModifiedDateForDormant", "2023-09-30T00:00:00"));

        boolean result = underTest.accepts().test(caseDetails);

        assertEquals(false, result);
    }

    @Test
    void acceptsShouldReturnTrueWhenCaseDetailsAreValid() {
        underTest.setReferenceDate(LocalDate.of(2023, 10, 01));
        when(caseDetails.getState()).thenReturn("Dormant");
        when(caseDetails.getData()).thenReturn(Map.of("lastModifiedDateForDormant", "2023-09-30T00:00:00"));

        boolean result = underTest.accepts().test(caseDetails);

        assertEquals(true, result);
    }
}