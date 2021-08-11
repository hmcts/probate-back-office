package uk.gov.hmcts.probate.htmlrendering;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UnorderedListRendererTest {
    
    @Test
    public void shouldRenderUnorderedListCorrectly() {
        List<String> testListItems = List.of("Test list item 1", "Test list item 2");
        String expectedValue = "<ul class=\"govuk-list govuk-list--bullet\">\n"
            + "<li>Test list item 1</li>\n<li>Test list item 2</li>\n</ul>\n";
        String result = UnorderedListRenderer.render(testListItems);
        assertEquals(expectedValue, result);
    }
}
