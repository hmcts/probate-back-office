package uk.gov.hmcts.probate.htmlrendering;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HeadingRendererTest {

    @Test
    public void shouldRenderHeaderCorrectly() {
        String expectedValue = "<h2 class=\"govuk-heading-l\">This is a heading</h2>";
        String result = HeadingRenderer.render("This is a heading");
        assertEquals(expectedValue, result);
    }
}
