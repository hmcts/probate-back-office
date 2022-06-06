package uk.gov.hmcts.probate.htmlrendering;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class EmailRendererTest {

    @Test
    void shouldRenderParagraphCorrectly() {
        String expectedValue = "<p><a href=\"mailto:test@test.com\" class=\"govuk-link\" target=\"_blank\">test@test.com</a></p>";
        String result = EmailAddressRenderer.renderByReplace("<p><test@test.com/></p>", "test@test.com");
        assertEquals(expectedValue, result);
    }
}
