package uk.gov.hmcts.probate.service.solicitorexecutor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class FormattingServiceTest {
    @Test
    public void shouldCapitaliseONeillCorrectlyLowercaseM() {
        final String result = FormattingService.capitaliseEachWord("martin O'Neill");

        assertTrue(result.equals("Martin O'Neill"));
    }

    @Test
    public void shouldCapitaliseONeillCorrectlyLowercaseO() {
        final String result = FormattingService.capitaliseEachWord("martin o'Neill");

        assertTrue(result.equals("Martin O'Neill"));
    }

    @Test
    public void shouldCapitaliseONeillCorrectlyLowercaseNO() {
        final String result = FormattingService.capitaliseEachWord("martin o'neill");

        assertTrue(result.equals("Martin O'neill"));
    }
}
