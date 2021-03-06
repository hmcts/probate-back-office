package uk.gov.hmcts.probate.htmlrendering;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LinkRendererTest {

    @Test
    public void shouldRenderLinkCorrectly() {
        String expectedValue = "<a href=\"test.com\" class=\"govuk-link\">Link</a>";
        String result = LinkRenderer.render("Link", "test.com");
        assertEquals(expectedValue, result);
    }

    @Test
    public void shouldRenderLinkInOtherTabCorrectly() {
        String expectedValue = "<a href=\"test.com\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">Link</a>";
        String result = LinkRenderer.renderOutside("Link", "test.com");
        assertEquals(expectedValue, result);
    }
}
