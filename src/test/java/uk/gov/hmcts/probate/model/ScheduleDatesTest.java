package uk.gov.hmcts.probate.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ScheduleDatesTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2025, 4, 22);

    @Mock
    private Clock clock;

    private AutoCloseable closeableMocks;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);

        final Clock fixedClock = Clock.fixed(
                TEST_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        when(clock.instant())
                .thenReturn(fixedClock.instant());
        when(clock.getZone())
                .thenReturn(fixedClock.getZone());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void testNullFromNoValue() {
        final ScheduleDates scheduleDates = new ScheduleDates(clock, null, null);

        final boolean actual = scheduleDates.hasValue();

        assertFalse(actual, "null from date should have no value");
    }

    @Test
    void testEmptyFromNoValue() {
        final ScheduleDates scheduleDates = new ScheduleDates(clock, "", null);

        final boolean actual = scheduleDates.hasValue();

        assertFalse(actual, "empty from date should have no value");
    }

    @Test
    void testBlankFromNoValue() {
        final ScheduleDates scheduleDates = new ScheduleDates(clock, " ", null);

        final boolean actual = scheduleDates.hasValue();

        assertFalse(actual, "blank from date should have no value");
    }

    @Test
    void testNonblankFromHasValue() {
        final ScheduleDates scheduleDates = new ScheduleDates(clock, "x", null);

        final boolean actual = scheduleDates.hasValue();

        assertTrue(actual, "nonblank from date should have no value");
    }

    @Test
    void testNonblankFromGivesSameForFromDate() {
        final String expected = "x";
        final ScheduleDates scheduleDates = new ScheduleDates(clock, expected, null);

        final String actual = scheduleDates.getFromDate();

        assertSame(expected, actual, "fromDate should return expected object");
    }

    @Test
    void testNonblankFromNullToGivesSameForToDate() {
        final String fromDate = "x";
        final ScheduleDates scheduleDates = new ScheduleDates(clock, fromDate, null);

        final String actual = scheduleDates.getToDate();

        assertSame(fromDate, actual, "toDate should return fromDate when null toDate");
    }

    @Test
    void testNonblankFromEmptyToGivesSameForToDate() {
        final String fromDate = "x";
        final ScheduleDates scheduleDates = new ScheduleDates(clock, fromDate, "");

        final String actual = scheduleDates.getToDate();

        assertSame(fromDate, actual, "toDate should return fromDate when empty toDate");
    }

    @Test
    void testNonblankFromBlankToGivesSameForToDate() {
        final String fromDate = "x";
        final ScheduleDates scheduleDates = new ScheduleDates(clock, fromDate, " ");

        final String actual = scheduleDates.getToDate();

        assertSame(fromDate, actual, "toDate should return fromDate when blank toDate");
    }

    @Test
    void testNonblankFromNonblankToGivesToDate() {
        final String fromDate = "x";
        final String toDate = "y";
        final ScheduleDates scheduleDates = new ScheduleDates(clock, fromDate, toDate);

        final String actual = scheduleDates.getToDate();

        assertSame(toDate, actual, "toDate should return fromDate when blank toDate");
    }

    @Test
    void testYesterdayGivesCorrectDate() {
        final ScheduleDates scheduleDates = new ScheduleDates(clock, null, null);

        final String actual = scheduleDates.getYesterday();
        final String expected = "2025-04-21";

        assertEquals(expected, actual, "Expected date to match");
    }
}
