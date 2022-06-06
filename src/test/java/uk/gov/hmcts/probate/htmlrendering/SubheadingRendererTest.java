package uk.gov.hmcts.probate.htmlrendering;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class SubheadingRendererTest {

    @Test
    void shouldRenderSubheadingCorrectly() {
        String expectedValue = "<h3 class=\"govuk-heading-m\">This is a heading</h3>";
        String result = SubheadingRenderer.render("This is a heading");
        assertEquals(expectedValue, result);
    }
}
