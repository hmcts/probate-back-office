package uk.gov.hmcts.probate.service;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class MarkdownTransformationServiceTest {

    @InjectMocks
    private MarkdownTransformationService markdownTransformationService;

    @Mock
    private Parser parser;

    @Mock
    private HtmlRenderer renderer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void toHtml() {
        String html = markdownTransformationService.toHtml("test");

        verify(parser).parse("test");
        verify(renderer).render(any(Node.class));
    }
}