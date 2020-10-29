package uk.gov.hmcts.probate.htmlRendering;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LinkRendererTest {

    @Test
    public void shouldRenderLinkCorrectly() {
        String expectedValue = "<a href=\"test.com\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">Link</a>";
        String result = LinkRenderer.render("Link", "test.com");
        assertEquals(expectedValue, result);
    }
}
