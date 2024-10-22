package uk.gov.hmcts.probate.validator;

import org.commonmark.node.Image;
import org.commonmark.parser.Parser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import uk.gov.hmcts.probate.service.MarkdownValidatorService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonalisationValidationRuleTest {

    @Spy
    Parser markdownParserSpy = Parser.builder().build();

    @Spy
    MarkdownValidatorService markdownValidatorService;

    @InjectMocks
    private PersonalisationValidationRule personalisationValidationRule;

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
    void givenPermittedMarkdownInputs_whenValidated_thenPasses() {
        final Map<String, Object> personalisation = new HashMap<>();

        personalisation.put("valid", "Valid text");

        // Documented as supported by Notify https://www.notifications.service.gov.uk/using-notify/formatting
        personalisation.put("heading1", "# heading1");
        personalisation.put("heading2", "## heading2");
        personalisation.put("heading1_alt", "heading1\n========");
        personalisation.put("heading2_alt", "heading2\n--------");
        personalisation.put("list", "* list");
        personalisation.put("list_alt", "- list");
        personalisation.put("numlist", "1. list");
        personalisation.put("numlist_alt", "1) list");
        personalisation.put("hrule", "---");
        personalisation.put("hrule_alt", "***");

        final Set<String> result = personalisationValidationRule.validatePersonalisation(personalisation)
                .invalidFields()
                .keySet();

        final Function<String, Executable> assertNotContains = name -> {
            return () -> assertFalse(result.contains(name), "result should not contain " + name);
        };

        final List<Executable> assertions = new ArrayList<Executable>();
        assertions.add(() -> assertEquals(0, result.size(), "All inputs should pass validation"));
        for (String key : personalisation.keySet()) {
            assertions.add(assertNotContains.apply(key));
        }

        assertAll(assertions);
    }

    @Test
    void givenNonpermittedMarkdownInputs_whenValidated_thenReturnsInvalid() {
        final Map<String, Object> personalisation = new HashMap<>();

        // Documented as unsupported by Notify
        personalisation.put("with_bold", "bold **text**");
        personalisation.put("with_ital", "ital *text*");
        personalisation.put("with_bold_alt", "bold __text__");
        personalisation.put("with_ital_alt", "ital _text_");
        personalisation.put("inline_code", "`inline code`");
        personalisation.put("code_block", "```\ncode\nblock\n```");
        personalisation.put("code_block_alt", "    code\n    block");

        // Not explicitly documented as supported or unsupported (the documentation refers to "inset text"
        // but uses: " ^ inset text" as the example. Unclear if this is just a nonstandard alternative, but
        // a stock markdown implementation will not catch it. This is likely because `>` would be removed
        // by any html filtering.
        personalisation.put("block_quote", "> block_quote");

        // Link handling
        personalisation.put("single_link", "Some text [example](http://example.com)");
        personalisation.put("bypass_using_match_before", "[) [example](http://example.com)");
        personalisation.put("bypass_with_early_rsqb", "[example\\]](http://example.com)");
        personalisation.put("link_with_ref", "Some text [link][1]\n\nMore text\n\n[1]: http://example.com");

        // Images (not mentioned in Notify documentation, but seems like we shouldn't be permitting this
        personalisation.put("single_image", "Some text ![example](http://example.com/img.png)");
        personalisation.put("image_with_ref", "Some text ![link][1]\n\nMore text\n\n[1]: http://example.com/img.png");

        final Set<String> result = personalisationValidationRule.validatePersonalisation(personalisation)
                .invalidFields()
                .keySet();

        final Function<String, Executable> assertContains = name -> {
            return () -> assertTrue(result.contains(name), "result did not contain " + name);
        };

        final List<Executable> assertions = new ArrayList<Executable>();
        assertions.add(() -> assertEquals(personalisation.size(), result.size(), "Did not identify all issues"));
        for (String key : personalisation.keySet()) {
            assertions.add(assertContains.apply(key));
        }

        assertAll(assertions);
    }

    @Test
    void givenInput_whenValidatorFlags_thenRejected() {
        final var key = "key";
        final var personalisation = Map.ofEntries(Map.entry(key, "value"));

        final var visitorMock = mock(MarkdownValidatorService.NontextVisitor.class);
        when(markdownValidatorService.getNontextVisitor(key)).thenReturn(visitorMock);
        when(visitorMock.isInvalid()).thenReturn(true);

        final var result = personalisationValidationRule.validatePersonalisation(personalisation)
                .invalidFields()
                .keySet();

        final List<Executable> assertions = List.of(
                () -> verify(markdownValidatorService).getNontextVisitor(any()),
                () -> verify(visitorMock).isInvalid(),
                () -> assertEquals(1, result.size(), "Expected validation to fail"),
                () -> assertTrue(result.contains(key), "Expected key to be present in failure list")
        );
        assertAll(assertions);
    }

    @Test
    void givenInput_whenValidatorPasses_thenAccepted() {
        final var key = "key";
        final var personalisation = Map.ofEntries(Map.entry(key, "value"));

        final var visitorMock = mock(MarkdownValidatorService.NontextVisitor.class);
        when(markdownValidatorService.getNontextVisitor(key)).thenReturn(visitorMock);
        when(visitorMock.isInvalid()).thenReturn(false);

        final var result = personalisationValidationRule.validatePersonalisation(personalisation).invalidFields();

        final List<Executable> assertions = List.of(
                () -> verify(markdownValidatorService).getNontextVisitor(any()),
                () -> verify(visitorMock).isInvalid(),
                () -> assertEquals(0, result.size(), "Expected validation to pass")
        );
        assertAll(assertions);
    }

    @Test
    void givenNonpermittedMarkdown_whenValidated_thenVisitorShortCircuits() {
        final var builder = new StringBuilder();

        builder.append("first para\n")
                .append("\n")
                .append("second para with [link](http://example.com)\n")
                .append("\n")
                .append("third para with ![img](http://example.com/img.png)\n");
        final var personalisation = Map.ofEntries(Map.entry("multiple_failures", builder.toString()));

        final var visitorSpy = spy(markdownValidatorService.getNontextVisitor("key"));

        when(markdownValidatorService.getNontextVisitor(any())).thenReturn(visitorSpy);

        personalisationValidationRule.validatePersonalisation(personalisation);

        verify(visitorSpy, times(0)).visit((Image) any());
    }

    @Test
    void shouldRetunEmptyListWhereNoMarkDownLink() {
        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("field1", "Some text");
        personalisation.put("field2", "Another  text");

        Set<String> result = personalisationValidationRule.validatePersonalisation(personalisation)
                .invalidFields()
                .keySet();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRetunEmptyListForNullValid() {
        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("field1", null);

        Set<String> result = personalisationValidationRule.validatePersonalisation(personalisation)
                .invalidFields()
                .keySet();

        assertTrue(result.isEmpty());
    }
}