package uk.gov.hmcts.probate.htmlrendering;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GridRendererTest {
    @Test
    public void shouldRenderGridCorrectly() {
        final String testValue = "<h>A title</h>\n" +
                "<gridRow><gridCol-two-thirds><p><secText>Some text</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third></gridRow>\n" +
                "<gridRowSeparator/>\n" +
                "<gridRow><gridCol-two-thirds><p>Some more text</p></gridCol-two-thirds><gridCol-one-third>xyz</gridCol-one-third></gridRow>\n" +
                "<gridRowSeparator/>\n";

        final String expectedValue = "<h>A title</h>\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><secText>Some text</secText></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p>Some more text</p></div><div class=\"govuk-grid-column-one-third\">xyz</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n";

        String result = GridRenderer.renderByReplace(testValue);
        assertEquals(expectedValue, result);
    }
}
