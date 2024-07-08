package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonalisationValidationRuleTest {

    @InjectMocks
    private PersonalisationValidationRule personalisationValidationRule;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnFielNameWithHyperLinkWhereValidatePersonilization() {
        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("field1", "Some text [example](http://example.com)");
        personalisation.put("field2", "Valid text");

        List<String> result = personalisationValidationRule.validatePersonalisation(personalisation);

        assertEquals(1, result.size());
        assertTrue(result.contains("field1"));
    }

    @Test
    public void shouldRetunEmptyListWhereNoMarkDownLink() {
        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("field1", "Some text");
        personalisation.put("field2", "Another  text");

        List<String> result = personalisationValidationRule.validatePersonalisation(personalisation);

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldRetunEmptyListForNullValid() {
        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("field1", null);

        List<String> result = personalisationValidationRule.validatePersonalisation(personalisation);

        assertTrue(result.isEmpty());
    }
}