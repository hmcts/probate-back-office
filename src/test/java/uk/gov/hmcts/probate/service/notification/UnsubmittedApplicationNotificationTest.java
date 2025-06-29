package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_DORMANT;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_PENDING;

class UnsubmittedApplicationNotificationTest {
    @Mock
    private NotificationService notificationService;

    @Mock
    private CaseDetails caseDetails;

    private UnsubmittedApplicationNotification underTest;

    AutoCloseable closeableMocks;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);

        fixedClock = Clock.fixed(
                LocalDateTime.of(2025,5,27,12,0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        underTest = new UnsubmittedApplicationNotification(notificationService);
        underTest.setReferenceDate(LocalDate.now(fixedClock));
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }


    @Test
    void shouldSendUnsubmittedApplicationEmail() throws NotificationClientException {
        underTest.sendEmail(caseDetails);

        verify(notificationService, times(1)).sendUnsubmittedApplicationEmail(caseDetails);
    }

    @Test
    void acceptsNullOrNoDataReturnsFalse() {
        assertFalse(underTest.accepts().test(null));

        CaseDetails noData = CaseDetails.builder()
                .state(STATE_BO_CASE_STOPPED)
                .data(null)
                .lastModified(LocalDateTime.now(fixedClock))
                .build();
        assertFalse(underTest.accepts().test(noData));
    }

    @Test
    void acceptsWrongStateReturnsFalse() {
        CaseDetails wrong = CaseDetails.builder()
                .state(STATE_DORMANT)
                .lastModified(LocalDateTime.of(2025,5,24,11,0))
                .build();
        assertFalse(underTest.accepts().test(wrong));
    }

    @Test
    void acceptsInvalidLastModifiedReturnsFalse() {
        LocalDateTime after = LocalDateTime.of(2025,5,28,12,1);
        CaseDetails cd = CaseDetails.builder()
                .state(STATE_PENDING)
                .lastModified(after)
                .build();
        assertFalse(underTest.accepts().test(cd));
    }

    @Test
    void acceptsReturnsTrue() {
        LocalDateTime before = LocalDateTime.of(2025,5,23,23,59);
        CaseDetails beforeCase = CaseDetails.builder()
                .state(STATE_PENDING)
                .data(Map.of())
                .lastModified(before)
                .build();
        assertTrue(underTest.accepts().test(beforeCase));
    }
}
