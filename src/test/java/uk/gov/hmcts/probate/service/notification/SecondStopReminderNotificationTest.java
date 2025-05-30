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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_STOPPED;

class SecondStopReminderNotificationTest {

    private static final String FIRST_STOP_REMINDER_DATE = "firstStopReminderDate";
    private static final String LAST_MODIFIED_DATE_FOR_DORMANT = "lastModifiedDateForDormant";

    @Mock
    private NotificationService notificationService;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private Document mockDocument;

    private SecondStopReminderNotification underTest;

    AutoCloseable closeableMocks;

    private final LocalDate referenceDate = LocalDate.of(2025, 5, 25);

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);
        underTest = new SecondStopReminderNotification(notificationService);
        underTest.setReferenceDate(referenceDate);
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
    void acceptsWrongStateOrMissingKeyReturnsFalse() {
        CaseDetails wrongState = CaseDetails.builder()
                .state("SomethingElse")
                .data(Map.of(FIRST_STOP_REMINDER_DATE, "2025-05-25"))
                .build();
        assertFalse(underTest.accepts().test(wrongState));

        CaseDetails missingKey = CaseDetails.builder()
                .state(STATE_BO_CASE_STOPPED)
                .data(new HashMap<>())
                .build();
        assertFalse(underTest.accepts().test(missingKey));

        Map<String,Object> nullValue = new HashMap<>();
        nullValue.put(FIRST_STOP_REMINDER_DATE, null);
        CaseDetails nullVal = CaseDetails.builder()
                .state(STATE_BO_CASE_STOPPED)
                .data(nullValue)
                .build();
        assertFalse(underTest.accepts().test(nullVal));
    }


    @Test
    void acceptsShouldReturnFalseWhenFirstStopReminderDateDoesNotMatchReferenceDate() {
        underTest.setReferenceDate(LocalDate.of(2025, 5, 25));
        when(caseDetails.getData()).thenReturn(Map.of(FIRST_STOP_REMINDER_DATE, "2025-05-24"));

        boolean result = underTest.accepts().test(caseDetails);

        assertFalse(result);
    }

    @Test
    void acceptsShouldReturnFalseWhenLastModifiedDateForDormantIsNotBeforeReferenceDate() {
        underTest.setReferenceDate(LocalDate.of(2025, 5, 25));
        when(caseDetails.getData()).thenReturn(Map.of(
                FIRST_STOP_REMINDER_DATE, "2025-05-25",
                LAST_MODIFIED_DATE_FOR_DORMANT, "2025-05-25T00:00:00"
        ));

        boolean result = underTest.accepts().test(caseDetails);

        assertFalse(result);
    }

    @Test
    void acceptsShouldReturnTrueWhenAllConditionsAreMet() {
        underTest.setReferenceDate(LocalDate.of(2025, 5, 25));
        when(caseDetails.getState()).thenReturn(STATE_BO_CASE_STOPPED);
        when(caseDetails.getData()).thenReturn(Map.of(
                FIRST_STOP_REMINDER_DATE, "2025-05-25",
                LAST_MODIFIED_DATE_FOR_DORMANT, "2025-05-24T23:59:59"
        ));

        boolean result = underTest.accepts().test(caseDetails);

        assertTrue(result);
    }

}