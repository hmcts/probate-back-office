package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class DateFormatterServiceTest {

    @InjectMocks
    private DateFormatterService dateFormatterService;

    @Test
    void shouldReturnDateFormattedWithStSuffix() {
        assertEquals("1st January 2000", dateFormatterService.formatDate(LocalDate.of(2000, 01, 01)));
    }

    @Test
    void shouldReturnDateFormattedWithNdSuffix() {
        assertEquals("2nd January 2000", dateFormatterService.formatDate(LocalDate.of(2000, 01, 02)));
    }

    @Test
    void shouldReturnDateFormattedWithRdSuffix() {
        assertEquals("3rd January 2000", dateFormatterService.formatDate(LocalDate.of(2000, 01, 03)));
    }

    @Test
    void shouldReturnDateFormattedWithThSuffix() {
        assertEquals("4th January 2000", dateFormatterService.formatDate(LocalDate.of(2000, 01, 04)));
    }

    @Test
    void shouldThrowParseException() {
        assertEquals(null, dateFormatterService.formatDate(LocalDate.of(30000, 01, 01)));
    }

    @Test
    void shouldReturnNullFormatDate() {
        assertEquals(null, dateFormatterService.formatDate(null));
    }

    @Test
    void shouldReturnCavetExpiryDateFormatted() {
        assertEquals("1st January 2019", dateFormatterService.formatCaveatExpiryDate(LocalDate.of(2019, 01, 01)));
        assertEquals("22nd February 2019", dateFormatterService.formatCaveatExpiryDate(LocalDate.of(2019, 02, 22)));
        assertEquals("13th March 2019", dateFormatterService.formatCaveatExpiryDate(LocalDate.of(2019, 03, 13)));
        assertEquals("23rd April 2019", dateFormatterService.formatCaveatExpiryDate(LocalDate.of(2019, 04, 23)));
    }

    @Test
    void shouldFormatDateSafelyFromStringSuccessfully() {
        assertEquals("01 Jan 2020",
                dateFormatterService.formatDateSafely("2020-01-01", DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }

    @Test
    void shouldReturnInvalidDateWhenStringDateIsBlank() {
        assertEquals("INVALID DATE",
                dateFormatterService.formatDateSafely("", DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }

    @Test
    void shouldReturnInvalidDateWhenStringDateIsNull() {
        assertEquals("INVALID DATE",
                dateFormatterService.formatDateSafely((String) null, DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }

    @Test
    void shouldReturnInvalidDateWhenStringDateIsUnparseable() {
        assertEquals("INVALID DATE",
                dateFormatterService.formatDateSafely("23-one-32011", DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }

    @Test
    void shouldFormatDateSafelyFromLocalDateSuccessfully() {
        assertEquals("01 Jan 2020",
                dateFormatterService.formatDateSafely(LocalDate.of(2020, 1, 1),
                        DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }

    @Test
    void shouldReturnInvalidDateWhenLocalDateIsNull() {
        assertEquals("INVALID DATE",
                dateFormatterService.formatDateSafely((LocalDate) null, DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }
}
