package uk.gov.hmcts.probate.service.solicitorexecutor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FormattingServiceTest {
    @Test
    public void shouldCapitaliseONeillCorrectlyLowercaseM() {
        final String result = FormattingService.capitaliseEachWord("martin O'Neill");

        assertEquals("Martin O'Neill", result);
    }

    @Test
    public void shouldCapitaliseONeillCorrectlyLowercaseO() {
        final String result = FormattingService.capitaliseEachWord("martin o'Neill");

        assertEquals("Martin O'Neill", result);
    }

    @Test
    public void shouldCapitaliseONeillCorrectlyLowercaseNO() {
        final String result = FormattingService.capitaliseEachWord("martin o'neill");

        assertEquals("Martin O'neill", result);
    }
}
