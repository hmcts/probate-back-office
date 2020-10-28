package uk.gov.hmcts.probate.htmlRendering;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HeaderRendererTest {
    private HeaderRenderer hdrRenderer;

    @Before
    public void setup() throws Exception {
        hdrRenderer = new HeaderRenderer();
    }

    @Test
    public void shouldRenderHeaderCorrectly() {
        String expectedValue = "## This is a header";
        String result = hdrRenderer.render("This is a header");
        assertTrue(result.equals(expectedValue));
    }
}
