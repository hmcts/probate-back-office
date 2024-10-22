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
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
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
        public void visitChildren(Node parent) {
            Node node = parent.getFirstChild();
            while (node != null) {
                // If we have seen any failure we do not need to continue searching the Node tree, so short circuit
                if (invalid) {
                    log.trace("{}: has been rejected, short circuit", key);
                    return;
                }

                // A subclass of this visitor might modify the node, resulting in getNext returning a different node or
                // no node after visiting it. So get the next node before visiting.
                final Node next = node.getNext();
                node.accept(this);
                node = next;
            }
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            log.trace("{}: reject BlockQuote", key);
            invalid = true;
        }

        @Override
        public void visit(BulletList bulletList) {
            log.trace("{}: visit BulletList", key);
            visitChildren(bulletList);
        }

        @Override
        public void visit(Code code) {
            log.trace("{}: reject Code", key);
            invalid = true;
        }

        @Override
        public void visit(Document document) {
            log.trace("{}: visit Document", key);
            visitChildren(document);
        }

        @Override
        public void visit(Emphasis emphasis) {
            log.trace("{}: reject Emphasis", key);
            invalid = true;
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            log.trace("{}: reject FencedCodeBlock", key);
            invalid = true;
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            log.trace("{}: visit HardLineBreak", key);
            visitChildren(hardLineBreak);
        }

        @Override
        public void visit(Heading heading) {
            log.trace("{}: visit Heading", key);
            visitChildren(heading);
        }

        @Override
        public void visit(ThematicBreak thematicBreak) {
            log.trace("{}: visit ThematicBreak", key);
            visitChildren(thematicBreak);
        }

        @Override
        public void visit(HtmlInline htmlInline) {
            log.trace("{}: reject HtmlInline", key);
            invalid = true;
        }

        @Override
        public void visit(HtmlBlock htmlBlock) {
            log.trace("{}: reject HtmlBlock", key);
            invalid = true;
        }

        @Override
        public void visit(Image image) {
            log.trace("{}: reject Image", key);
            invalid = true;
        }

        @Override
        public void visit(IndentedCodeBlock indentedCodeBlock) {
            log.trace("{}: reject IndentedCodeBlock", key);
            invalid = true;
        }

        @Override
        public void visit(Link link) {
            log.trace("{}: reject Link", key);
            invalid = true;
        }

        @Override
        public void visit(ListItem listItem) {
            log.trace("{}: visit ListItem", key);
            visitChildren(listItem);
        }

        @Override
        public void visit(OrderedList orderedList) {
            log.trace("{}: visit OrderedList", key);
            visitChildren(orderedList);
        }

        @Override
        public void visit(Paragraph paragraph) {
            log.trace("{}: visit Paragraph", key);
            visitChildren(paragraph);
        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            log.trace("{}: visit SoftLineBreak", key);
            visitChildren(softLineBreak);
        }

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            log.trace("{}: reject StrongEmphasis", key);
            invalid = true;
        }

        @Override
        public void visit(Text text) {
            log.trace("{}: visit Text", key);
            visitChildren(text);
        }

        @Override
        public void visit(LinkReferenceDefinition linkReferenceDefinition) {
            log.trace("{}: reject LinkReferenceDefinition", key);
            invalid = true;
        }

        @Override
        public void visit(CustomBlock customBlock) {
            log.trace("{}: reject CustomBlock", key);
            invalid = true;
        }

        @Override
        public void visit(CustomNode customNode) {
            log.trace("{}: reject CustomNode", key);
            invalid = true;
        }
    }
}
