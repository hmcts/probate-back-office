package uk.gov.hmcts.probate.htmlrendering;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubheadingRendererTest {

    @Test
    public void shouldRenderSubheadingCorrectly() {
        String expectedValue = "<h3 class=\"govuk-heading-m\">This is a heading</h3>";
        String result = SubheadingRenderer.render("This is a heading");
        assertEquals(expectedValue, result);
    }
}
