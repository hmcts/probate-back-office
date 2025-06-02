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

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private Clock fixedClock;

    private static final String LAST_MODIFIED_DATE_FOR_DORMANT = "lastModifiedDateForDormant";

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);

        fixedClock = Clock.fixed(
                LocalDateTime.of(2025,5,27,12,0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        underTest = new FirstStopReminderNotification(notificationService);
        underTest.setReferenceDate(LocalDate.now(fixedClock));
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void shouldSendFirstStopReminderEmail() throws NotificationClientException {
        when(notificationService.sendStopReminderEmail(caseDetails, true)).thenReturn(mockDocument);

        Document result = underTest.sendEmail(caseDetails);

        verify(notificationService, times(1)).sendStopReminderEmail(caseDetails, true);
        assertEquals(mockDocument, result);
    }

    @Test
    void acceptsNullOrNoDataReturnsFalse() {
        assertFalse(underTest.accepts().test(null));

        CaseDetails noData = CaseDetails.builder()
                .state("BOCaseStopped")
                .data(null)
                .lastModified(LocalDateTime.now(fixedClock))
                .build();
        assertFalse(underTest.accepts().test(noData));
    }

    @Test
    void acceptsWrongStateReturnsFalse() {
        CaseDetails wrong = CaseDetails.builder()
                .state("OtherState")
                .data(Map.of(LAST_MODIFIED_DATE_FOR_DORMANT, LocalDateTime.of(2025,5,24,11,0)))
                .lastModified(LocalDateTime.of(2025,5,24,11,0))
                .build();
        assertFalse(underTest.accepts().test(wrong));
    }

    @Test
    void acceptsInvalidLastModifiedReturnsFalse() {
        LocalDateTime after = LocalDateTime.of(2025,5,28,12,1);
        CaseDetails cd = CaseDetails.builder()
                .state("BOCaseStopped")
                .data(Map.of(LAST_MODIFIED_DATE_FOR_DORMANT, after))
                .lastModified(after)
                .build();
        assertFalse(underTest.accepts().test(cd));
    }

    @Test
    void acceptsReturnsTrue() {
        LocalDateTime before = LocalDateTime.of(2025,5,23,23,59);
        CaseDetails beforeCase = CaseDetails.builder()
                .state("BOCaseStopped")
                .data(Map.of(LAST_MODIFIED_DATE_FOR_DORMANT, before))
                .lastModified(before)
                .build();
        assertTrue(underTest.accepts().test(beforeCase));
    }
}