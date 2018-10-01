package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MarkdownTransformationService {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public String toHtml(String markdown) {
        Node document = parser.parse(markdown);

        return renderer.render(document);
    }
}
