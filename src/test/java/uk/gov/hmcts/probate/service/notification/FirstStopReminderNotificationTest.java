package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FirstStopReminderNotificationTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private Document mockDocument;

    private FirstStopReminderNotification underTest;

    AutoCloseable closeableMocks;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);
        underTest = new FirstStopReminderNotification(notificationService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void shouldSendFirstStopReminderEmail() throws NotificationClientException {
        when(notificationService.sendStopReminderEmail(caseDetails, true)).thenReturn(mockDocument);

        Document result = underTest.sendEmail(caseDetails);

        verify(notificationService, times(1)).sendStopReminderEmail(caseDetails, true);
        assertEquals(mockDocument, result);
    }
}