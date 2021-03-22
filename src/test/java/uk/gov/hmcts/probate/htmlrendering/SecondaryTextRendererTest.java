package uk.gov.hmcts.probate.htmlrendering;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SecondaryTextRendererTest {
    @Test
    public void shouldRenderDetailsCorrectly() {
        String expectedValue = "<div><font color=\"#505a5f\">Hello</font></div>";
        String result = SecondaryTextRenderer.renderByReplace("<div><secText>Hello</secText></div>");
        assertEquals(expectedValue, result);
    }
}
