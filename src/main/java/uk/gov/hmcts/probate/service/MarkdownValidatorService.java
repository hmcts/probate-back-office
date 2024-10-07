package uk.gov.hmcts.probate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomBlock;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.LinkReferenceDefinition;
import org.commonmark.node.ListItem;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MarkdownValidatorService {

    public NontextVisitor getNontextVisitor(final String key) {
        return new NontextVisitor(key);
    }

    @Slf4j
    @RequiredArgsConstructor
    public static class NontextVisitor extends AbstractVisitor {
        @Getter
        private boolean invalid = false;

        private final String key;

        @Override
        public void visit(BlockQuote blockQuote) {
            log.debug("{}: visit BlockQuote", key);
            invalid = true;
        }

        @Override
        public void visit(BulletList bulletList) {
            log.debug("{}: visit BulletList", key);
            invalid = true;
        }

        @Override
        public void visit(Code code) {
            log.debug("{}: visit Code", key);
            invalid = true;
        }

        @Override
        public void visit(Document document) {
            log.debug("{}: visit Document", key);
            visitChildren(document);
        }

        @Override
        public void visit(Emphasis emphasis) {
            log.debug("{}: visit Emphasis", key);
            invalid = true;
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            log.debug("{}: visit FencedCodeBlock", key);
            invalid = true;
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            log.debug("{}: visit HardLineBreak", key);
            invalid = true;
        }

        @Override
        public void visit(Heading heading) {
            log.debug("{}: visit Heading", key);
            invalid = true;
        }

        @Override
        public void visit(ThematicBreak thematicBreak) {
            log.debug("{}: visit ThematicBreak", key);
            invalid = true;
        }

        @Override
        public void visit(HtmlInline htmlInline) {
            log.debug("{}: visit HtmlInline", key);
            invalid = true;
        }

        @Override
        public void visit(HtmlBlock htmlBlock) {
            log.debug("{}: visit HtmlBlock", key);
            invalid = true;
        }

        @Override
        public void visit(Image image) {
            log.debug("{}: visit Image", key);
            invalid = true;
        }

        @Override
        public void visit(IndentedCodeBlock indentedCodeBlock) {
            log.debug("{}: visit IndentedCodeBlock", key);
            invalid = true;
        }

        @Override
        public void visit(Link link) {
            log.debug("{}: visit Link", key);
            invalid = true;
        }

        @Override
        public void visit(ListItem listItem) {
            log.debug("{}: visit ListItem", key);
            invalid = true;
        }

        @Override
        public void visit(OrderedList orderedList) {
            log.debug("{}: visit OrderedList", key);
            invalid = true;
        }

        @Override
        public void visit(Paragraph paragraph) {
            log.debug("{}: visit Paragraph", key);
            visitChildren(paragraph);
        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            log.debug("{}: visit SoftLineBreak", key);
            invalid = true;
        }

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            log.debug("{}: visit StrongEmphasis", key);
            invalid = true;
        }

        @Override
        public void visit(Text text) {
            log.debug("{}: visit Text", key);
            visitChildren(text);
        }

        @Override
        public void visit(LinkReferenceDefinition linkReferenceDefinition) {
            log.debug("{}: visit LinkReferenceDefinition", key);
            invalid = true;
        }

        @Override
        public void visit(CustomBlock customBlock) {
            log.debug("{}: visit CustomBlock", key);
            invalid = true;
        }

        @Override
        public void visit(CustomNode customNode) {
            log.debug("{}: visit CustomNode", key);
            invalid = true;
        }
    }
}
