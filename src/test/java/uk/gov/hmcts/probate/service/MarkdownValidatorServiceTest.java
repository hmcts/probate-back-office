package uk.gov.hmcts.probate.service;


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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.service.MarkdownValidatorService.NontextVisitor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class MarkdownValidatorServiceTest {

    @InjectMocks
    private MarkdownValidatorService markdownValidatorService;

    AutoCloseable closableMocks;

    @BeforeEach
    void setUp() {
        closableMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closableMocks.close();
    }

    @Test
    void givenService_whenVisitorRequested_thenIsNotInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");

        assertFalse(visitor.isInvalid(), "Prior to visiting any nodes should not be invalid");
    }

    @Test
    void givenVisitor_whenVisitDocument_thenIsNotInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final Document node = mock(Document.class);

        visitor.visit(node);

        assertFalse(visitor.isInvalid(), "After visiting Document should not be invalid");
    }

    @Test
    void givenVisitor_whenVisitBulletList_thenIsNotInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final BulletList node = mock(BulletList.class);

        visitor.visit(node);

        assertFalse(visitor.isInvalid(), "After visiting BulletList should not be invalid");
    }

    @Test
    void givenVisitor_whenVisitHardLineBreak_thenIsNotInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final HardLineBreak node = mock(HardLineBreak.class);

        visitor.visit(node);

        assertFalse(visitor.isInvalid(), "After visiting HardLineBreak should not be invalid");
    }

    @Test
    void givenVisitor_whenVisitHeading_thenIsNotInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final Heading node = mock(Heading.class);

        visitor.visit(node);

        assertFalse(visitor.isInvalid(), "After visiting Heading should not be invalid");
    }

    @Test
    void givenVisitor_whenVisitThematicBreak_thenIsNotInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final ThematicBreak node = mock(ThematicBreak.class);

        visitor.visit(node);

        assertFalse(visitor.isInvalid(), "After visiting ThematicBreak should not be invalid");
    }

    @Test
    void givenVisitor_whenVisitListItem_thenIsNotInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final ListItem node = mock(ListItem.class);

        visitor.visit(node);

        assertFalse(visitor.isInvalid(), "After visiting ListItem should not be invalid");
    }

    @Test
    void givenVisitor_whenVisitOrderedList_thenIsNotInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final OrderedList node = mock(OrderedList.class);

        visitor.visit(node);

        assertFalse(visitor.isInvalid(), "After visiting OrderedList should not be invalid");
    }

    @Test
    void givenVisitor_whenVisitParagraph_thenIsNotInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final Paragraph node = mock(Paragraph.class);

        visitor.visit(node);

        assertFalse(visitor.isInvalid(), "After visiting Paragraph should not be invalid");
    }

    @Test
    void givenVisitor_whenVisitSoftLineBreak_thenIsNotInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final SoftLineBreak node = mock(SoftLineBreak.class);

        visitor.visit(node);

        assertFalse(visitor.isInvalid(), "After visiting SoftLineBreak should not be invalid");
    }

    @Test
    void givenVisitor_whenVisitText_thenIsNotInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final Text node = mock(Text.class);

        visitor.visit(node);

        assertFalse(visitor.isInvalid(), "After visiting Text should not be invalid");
    }

    @Test
    void givenVisitor_whenVisitBlockQuote_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final BlockQuote node = mock(BlockQuote.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting BlockQuote should be invalid");
    }

    @Test
    void givenVisitor_whenVisitCode_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final Code node = mock(Code.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting Code should be invalid");
    }

    @Test
    void givenVisitor_whenVisitEmphasis_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final Emphasis node = mock(Emphasis.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting Emphasis should be invalid");
    }

    @Test
    void givenVisitor_whenVisitFencedCodeBlock_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final FencedCodeBlock node = mock(FencedCodeBlock.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting FencedCodeBlock should be invalid");
    }

    @Test
    void givenVisitor_whenVisitHtmlInline_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final HtmlInline node = mock(HtmlInline.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting HtmlInline should be invalid");
    }

    @Test
    void givenVisitor_whenVisitHtmlBlock_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final HtmlBlock node = mock(HtmlBlock.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting HtmlBlock should be invalid");
    }

    @Test
    void givenVisitor_whenVisitImage_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final Image node = mock(Image.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting Image should be invalid");
    }

    @Test
    void givenVisitor_whenVisitIndentedCodeBlock_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final IndentedCodeBlock node = mock(IndentedCodeBlock.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting IndentedCodeBlock should be invalid");
    }

    @Test
    void givenVisitor_whenVisitLink_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final Link node = mock(Link.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting Link should be invalid");
    }

    @Test
    void givenVisitor_whenVisitStrongEmphasis_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final StrongEmphasis node = mock(StrongEmphasis.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting StrongEmphasis should be invalid");
    }

    @Test
    void givenVisitor_whenVisitLinkReferenceDefinition_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final LinkReferenceDefinition node = mock(LinkReferenceDefinition.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting LinkReferenceDefinition should be invalid");
    }

    @Test
    void givenVisitor_whenVisitCustomBlock_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final CustomBlock node = mock(CustomBlock.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting CustomBlock should be invalid");
    }

    @Test
    void givenVisitor_whenVisitCustomNode_thenIsInvalid() {
        final NontextVisitor visitor = markdownValidatorService.getNontextVisitor("key");
        final CustomNode node = mock(CustomNode.class);

        visitor.visit(node);

        assertTrue(visitor.isInvalid(), "After visiting CustomNode should be invalid");
    }
}
