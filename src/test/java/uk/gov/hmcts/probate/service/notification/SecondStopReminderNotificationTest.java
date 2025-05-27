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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecondStopReminderNotificationTest {

    private static final String FIRST_STOP_REMINDER_DATE = "firstStopReminderDate";

    @Mock
    private NotificationService notificationService;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private Document mockDocument;

    private SecondStopReminderNotification underTest;

    AutoCloseable closeableMocks;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);
        underTest = new SecondStopReminderNotification(notificationService);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void shouldSendFirstStopReminderEmail() throws NotificationClientException {
        when(notificationService.sendStopReminderEmail(caseDetails, false)).thenReturn(mockDocument);

        Document result = underTest.sendEmail(caseDetails);

        verify(notificationService, times(1)).sendStopReminderEmail(caseDetails, false);
        assertEquals(mockDocument, result);
    }

    @Test
    void accepts_wrongStateOrMissingKey_returnsFalse() {
        CaseDetails wrongState = CaseDetails.builder()
                .state("SomethingElse")
                .data(Map.of(FIRST_STOP_REMINDER_DATE, "2025-05-25"))
                .build();
        assertFalse(underTest.accepts().test(wrongState));

        CaseDetails missingKey = CaseDetails.builder()
                .state("BOCaseStopped")
                .data(new HashMap<>())
                .build();
        assertFalse(underTest.accepts().test(missingKey));

        Map<String,Object> nullValue = new HashMap<>();
        nullValue.put(FIRST_STOP_REMINDER_DATE, null);
        CaseDetails nullVal = CaseDetails.builder()
                .state("BOCaseStopped")
                .data(nullValue)
                .build();
        assertFalse(underTest.accepts().test(nullVal));
    }

    @Test
    void accepts_stateStoppedAndKeyPresent_returnsTrue() {
        Map<String,Object> data = Map.of(FIRST_STOP_REMINDER_DATE, "2025-05-25");
        CaseDetails ok = CaseDetails.builder()
                .state("BOCaseStopped")
                .data(data)
                .build();
        assertTrue(underTest.accepts().test(ok));
    }
}