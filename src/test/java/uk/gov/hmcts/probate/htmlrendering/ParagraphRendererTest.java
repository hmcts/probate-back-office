package uk.gov.hmcts.probate.htmlrendering;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParagraphRendererTest {

    @Test
    void shouldRenderParagraphCorrectly() {
        String expectedValue = "<p class=\"govuk-body-s\">This is a paragraph.</p>";
        String result = ParagraphRenderer.renderByReplace("<p>This is a paragraph.</p>");
        assertEquals(expectedValue, result);
    }
}
