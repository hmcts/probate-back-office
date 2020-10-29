package uk.gov.hmcts.probate.htmlRendering;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ParagraphRendererTest {
    private ParagraphRenderer paraRenderer;

    @Before
    public void setup() throws Exception {
        paraRenderer = new ParagraphRenderer();
    }

    @Test
    public void shouldRenderParagraphCorrectly() {
        String expectedValue = "<p class=\"govuk-body-s\">This is a paragraph.</p>";
        String result = paraRenderer.render("<p>This is a paragraph.</p>");
        assertTrue(result.equals(expectedValue));
    }
}
