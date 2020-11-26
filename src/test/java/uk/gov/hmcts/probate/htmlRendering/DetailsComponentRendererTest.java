package uk.gov.hmcts.probate.htmlRendering;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DetailsComponentRendererTest {
    @Test
    public void shouldRenderDetailsCorrectly() {
        String expectedValue = "<details class=\"govuk-details\" data-module=\"govuk-details\">\n" +
                "  <summary class=\"govuk-details__summary\">\n" +
                "    <span class=\"govuk-details__summary-text\">\n" +
                "      Help\n" +
                "    </span>\n" +
                "  </summary>\n" +
                "  <div class=\"govuk-details__text\">\n" +
                "    Bleh de bleh\n" +
                "  </div>\n" +
                "</details>";
        String result = DetailsComponentRenderer.renderByReplace("Help", "Bleh de bleh");
        assertEquals(expectedValue, result);
    }
}
