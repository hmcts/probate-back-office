package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_STOPPED;

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
    private static final String STOP_REASON_LIST_KEY = "boCaseStopReasonList";


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

        Document result = underTest.sendNotification(caseDetails);

        verify(notificationService, times(1)).sendStopReminderEmail(caseDetails, true);
        assertEquals(mockDocument, result);
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
                .state(STATE_BO_CASE_STOPPED)
                .data(Map.of(LAST_MODIFIED_DATE_FOR_DORMANT, after))
                .lastModified(after)
                .build();
        assertFalse(underTest.accepts().test(cd));
    }

    @Test
    void acceptsReturnsTrue() {
        LocalDateTime before = LocalDateTime.of(2025,5,23,23,59);
        CaseDetails beforeCase = CaseDetails.builder()
                .state(STATE_BO_CASE_STOPPED)
                .data(Map.of(LAST_MODIFIED_DATE_FOR_DORMANT, before))
                .lastModified(before)
                .build();
        assertTrue(underTest.accepts().test(beforeCase));
    }

    @Test
    void acceptsReturnsFalseIfStopListContainsStopReasonMatchingCaveat() {
        StopReason caveatMatch = StopReason.builder()
                .caseStopReason("CaveatMatch")
                .build();
        List<Object> stopList = List.of(caveatMatch);

        LocalDateTime yesterday = LocalDateTime.now(fixedClock).minusDays(1);
        Map<String, Object> dataMap = buildDataMapWithLastModifiedAndStopList(yesterday, stopList);

        CaseDetails cd = buildCaseDetails(dataMap, yesterday);

        assertFalse(underTest.accepts().test(cd));
    }

    @Test
    void acceptsReturnsFalseIfStopListContainsStopReasonPermanentCaveat() {
        StopReason permanentCaveat = StopReason.builder()
                .caseStopReason("Permanent Caveat")
                .build();
        List<Object> stopList = List.of(permanentCaveat);

        LocalDateTime yesterday = LocalDateTime.now(fixedClock).minusDays(1);
        Map<String, Object> dataMap = buildDataMapWithLastModifiedAndStopList(yesterday, stopList);

        CaseDetails cd = buildCaseDetails(dataMap, yesterday);

        assertFalse(underTest.accepts().test(cd));
    }

    @Test
    void acceptsReturnsTrueIfStopListContainsNonCaveatStopReason() {
        StopReason other = StopReason.builder()
                .caseStopReason("some other reason")
                .build();
        List<Object> stopList = List.of(other);

        LocalDateTime yesterday = LocalDateTime.now(fixedClock).minusDays(1);
        Map<String, Object> dataMap = buildDataMapWithLastModifiedAndStopList(yesterday, stopList);

        CaseDetails cd = buildCaseDetails(dataMap, yesterday);

        assertTrue(underTest.accepts().test(cd));
    }

    private CaseDetails buildCaseDetails(
            Map<String, Object> dataMap,
            LocalDateTime lastModified) {

        return CaseDetails.builder()
                .state(uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_STOPPED)
                .data(dataMap)
                .lastModified(lastModified)
                .build();
    }

    private Map<String, Object> buildDataMapWithLastModifiedAndStopList(
            LocalDateTime lastModifiedDateForDormant,
            List<Object> stopList) {

        Map<String, Object> data = new HashMap<>();
        data.put(LAST_MODIFIED_DATE_FOR_DORMANT,
                lastModifiedDateForDormant.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        data.put(STOP_REASON_LIST_KEY, stopList);
        return data;
    }
}