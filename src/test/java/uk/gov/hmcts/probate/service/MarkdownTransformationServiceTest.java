package uk.gov.hmcts.probate.service;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

public class MarkdownTransformationServiceTest {

    @InjectMocks
    private MarkdownTransformationService markdownTransformationService;

    @Mock
    private Parser parser;

    @Mock
    private HtmlRenderer renderer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void toHtml() {
        String html = markdownTransformationService.toHtml("test");

        verify(parser).parse("test");
        verify(renderer).render(isNull());
    }
}
