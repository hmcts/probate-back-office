package uk.gov.hmcts.probate.service.solicitorexecutor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormattingServiceTest {
    @Test
    void shouldCapitaliseONeillCorrectlyLowercaseM() {
        final String result = FormattingService.capitaliseEachWord("martin O'Neill");

        assertEquals("Martin O'Neill", result);
    }

    @Test
    void shouldCapitaliseONeillCorrectlyLowercaseO() {
        final String result = FormattingService.capitaliseEachWord("martin o'Neill");

        assertEquals("Martin O'Neill", result);
    }

    @Test
    void shouldCapitaliseONeillCorrectlyLowercaseNO() {
        final String result = FormattingService.capitaliseEachWord("martin o'neill");

        assertEquals("Martin O'neill", result);
    }
}
