package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonalisationValidationRuleTest {

    @InjectMocks
    private PersonalisationValidationRule personalisationValidationRule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnFielNameWithHyperLinkWhereValidatePersonilization() {
        final Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("valid", "Valid text");
        personalisation.put("single_link", "Some text [example](http://example.com)");
        personalisation.put("bypass_using_match_before", "[) [example](http://example.com)");
        personalisation.put("bypass_with_early_rsqb", "[example]](http://example.com)");
        personalisation.put("with_bold", "bold **text**");
        personalisation.put("with_ital", "ital *text*");
        personalisation.put("with_bold_alt", "bold __text__");
        personalisation.put("with_ital_alt", "ital _text_");
        personalisation.put("heading1", "# heading1");
        personalisation.put("heading2", "## heading2");
        personalisation.put("heading1_alt", "heading1\n========");
        personalisation.put("heading2_alt", "heading2\n--------");
        personalisation.put("blockquote", "> blockquote");
        personalisation.put("list", "* list");
        personalisation.put("list_alt", "- list");
        personalisation.put("numlist", "1. list");
        personalisation.put("numlist_alt", "1) list");
        personalisation.put("hrule", "---");
        personalisation.put("hrule_alt", "***");
        personalisation.put("inline_code", "`inline code`");
        personalisation.put("code_block", "```\ncode\nblock\n```");
        personalisation.put("code_block_alt", "    code\n    block");

        final List<String> result = personalisationValidationRule.validatePersonalisation(personalisation);

        final Function<String, Executable> assertContains = (name) -> {
            return () -> assertTrue(result.contains(name), "result did not contain " + name);
        };

        final List<Executable> assertions = new ArrayList<Executable>();
        assertions.add(() -> assertEquals(personalisation.size() - 1, result.size(), "Did not identify all issues"));
        for (String key : personalisation.keySet()) {
            if (!key.equals("valid")) {
                assertions.add(assertContains.apply(key));
            }
        }

        assertAll(assertions);
    }

    @Test
    void shouldRetunEmptyListWhereNoMarkDownLink() {
        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("field1", "Some text");
        personalisation.put("field2", "Another  text");

        List<String> result = personalisationValidationRule.validatePersonalisation(personalisation);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRetunEmptyListForNullValid() {
        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("field1", null);

        List<String> result = personalisationValidationRule.validatePersonalisation(personalisation);

        assertTrue(result.isEmpty());
    }
}