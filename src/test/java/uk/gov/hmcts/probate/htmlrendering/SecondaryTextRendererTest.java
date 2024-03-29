package uk.gov.hmcts.probate.htmlrendering;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecondaryTextRendererTest {
    @Test
    void shouldRenderDetailsCorrectly() {
        String expectedValue = "<div><font color=\"#505a5f\">Hello</font></div>";
        String result = SecondaryTextRenderer.renderByReplace("<div><secText>Hello</secText></div>");
        assertEquals(expectedValue, result);
    }
}
