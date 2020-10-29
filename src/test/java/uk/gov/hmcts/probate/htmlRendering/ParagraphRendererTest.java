package uk.gov.hmcts.probate.htmlRendering;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParagraphRendererTest {

    @Test
    public void shouldRenderParagraphCorrectly() {
        String expectedValue = "<p class=\"govuk-body-s\">This is a paragraph.</p>";
        String result = ParagraphRenderer.render("<p>This is a paragraph.</p>");
        assertEquals(expectedValue, result);
    }
}
