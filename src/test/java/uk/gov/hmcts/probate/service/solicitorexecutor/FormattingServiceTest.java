package uk.gov.hmcts.probate.service.solicitorexecutor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FormattingServiceTest {
    @Test
    void shouldCapitaliseONeillCorrectlyLowercaseM() {
        final String result = FormattingService.capitaliseEachWord("martin O'Neill", "");

        assertEquals("Martin O'Neill", result);
    }

    @Test
    void shouldCapitaliseONeillCorrectlyLowercaseO() {
        final String result = FormattingService.capitaliseEachWord("martin o'Neill", "");

        assertEquals("Martin O'Neill", result);
    }

    @Test
    void shouldCapitaliseONeillCorrectlyLowercaseNO() {
        final String result = FormattingService.capitaliseEachWord("martin o'neill", "");

        assertEquals("Martin O'neill", result);
    }

    @Test
    void shouldThrowIfEmptyString() {
        assertThrows(
                FormattingService.FormattingServiceException.class,
                () -> FormattingService.capitaliseEachWord("", ""));
    }

    @Test
    void shouldReturnEmptyStringIfSpaceString() {
        final String result = FormattingService.capitaliseEachWord(" ", "");
        assertEquals("", result);
    }

    @Test
    void shouldReturnNullStringIfNullString() {
        final String result = FormattingService.capitaliseEachWord(null, "");
        assertEquals(null, result);
    }

    @Test
    void shouldReturnUppercasedStringIfTrailingSpaces() {
        final String result = FormattingService.capitaliseEachWord("first  ", "");

        assertEquals("First", result);
    }

    @Test
    void shouldThrowIfPreceedingSpaces() {
        assertThrows(
                FormattingService.FormattingServiceException.class,
                () -> FormattingService.capitaliseEachWord("  first", ""));
    }

    @Test
    void shouldThrowIfWrappedSpaces() {
        assertThrows(
                FormattingService.FormattingServiceException.class,
                () -> FormattingService.capitaliseEachWord("  first  ", ""));
    }

    @Test
    void shouldReturnSingleSpacesWhenMultipleProvided() {
        final String result = FormattingService.capitaliseEachWord("first  second", "");

        assertEquals("First Second", result);
    }
}
