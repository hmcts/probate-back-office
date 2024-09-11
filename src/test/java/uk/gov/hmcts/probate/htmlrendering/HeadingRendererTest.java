package uk.gov.hmcts.probate.htmlrendering;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeadingRendererTest {

    @Test
    void shouldRenderHeaderCorrectly() {
        String expectedValue = "<h2 class=\"govuk-heading-l\">This is a heading</h2>"
                + "<h2 class=\"govuk-heading-l\">This is a welsh heading</h2>";
        String result = HeadingRenderer.render("This is a heading",
                "This is a welsh heading");
        assertEquals(expectedValue, result);
    }
}
