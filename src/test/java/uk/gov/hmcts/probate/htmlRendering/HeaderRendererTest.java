package uk.gov.hmcts.probate.htmlRendering;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HeaderRendererTest {
    private HeadingRenderer hdrRenderer;
    @Test
    public void shouldRenderHeaderCorrectly() {
        String expectedValue = "<h2 class=\"govuk-heading-l\">This is a header</h2>";
        String result = HeadingRenderer.render("This is a header");
        assertTrue(result.equals(expectedValue));
    }
}
