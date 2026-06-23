package uk.gov.hmcts.probate.dmnutils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CamundaVerifierTest {

    @Test
    void shouldMatchResultsWhenAllEntriesMatchByName() {
        List<Map<String, Object>> results = List.of(
                Map.of("name", "task1", "value", "value1", "canReconfigure", true),
                Map.of("name", "task2", "value", "value2", "canReconfigure", false)
        );

        List<Map<String, Object>> expectation = List.of(
                Map.of("name", "task1", "value", "value1", "canReconfigure", true),
                Map.of("name", "task2", "value", "value2", "canReconfigure", false)
        );

        CamundaVerifier.resultsMatchUsingNameKey(results, expectation);
    }

    @Test
    void shouldMatchResultsWhenAllEntriesMatchByNameOutOfOrder() {
        List<Map<String, Object>> results = List.of(
                Map.of("name", "task2", "value", "value2", "canReconfigure", false),
                Map.of("name", "task1", "value", "value1", "canReconfigure", true)
        );

        List<Map<String, Object>> expectation = List.of(
                Map.of("name", "task1", "value", "value1", "canReconfigure", true),
                Map.of("name", "task2", "value", "value2", "canReconfigure", false)
        );

        CamundaVerifier.resultsMatchUsingNameKey(results, expectation);
    }

    @Test
    void shouldThrowErrorWhenResultIsMissingForExpectedName() {
        List<Map<String, Object>> results = List.of(
                Map.of("name", "task1", "value", "value1", "canReconfigure", true)
        );

        List<Map<String, Object>> expectation = List.of(
                Map.of("name", "task1", "value", "value1", "canReconfigure", true),
                Map.of("name", "task2", "value", "value2", "canReconfigure", false)
        );

        assertThrows(AssertionError.class, () -> CamundaVerifier.resultsMatchUsingNameKey(results, expectation));
    }

    @Test
    void shouldThrowErrorWhenValuesDoNotMatchForSameName() {
        List<Map<String, Object>> results = List.of(
                Map.of("name", "task1", "value", "value1", "canReconfigure", true),
                Map.of("name", "task2", "value", "wrongValue", "canReconfigure", false)
        );

        List<Map<String, Object>> expectation = List.of(
                Map.of("name", "task1", "value", "value1", "canReconfigure", true),
                Map.of("name", "task2", "value", "value2", "canReconfigure", false)
        );

        assertThrows(AssertionError.class, () -> CamundaVerifier.resultsMatchUsingNameKey(results, expectation));
    }

    @Test
    void shouldThrowErrorWhenResultsSizeDoesNotMatchExpectationSize() {
        List<Map<String, Object>> results = List.of(
                Map.of("name", "task1", "value", "value1", "canReconfigure", true)
        );

        List<Map<String, Object>> expectation = List.of(
                Map.of("name", "task1", "value", "value1", "canReconfigure", true),
                Map.of("name", "task2", "value", "value2", "canReconfigure", false)
        );

        assertThrows(AssertionError.class, () -> CamundaVerifier.resultsMatchUsingNameKey(results, expectation));
    }

    @Test
    void shouldReturnMappedAdditionalDataWhenJsonIsValid() {
        String validJson = "{ \"key1\": \"value1\", \"key2\": 2 }";

        Map<String, Object> result = CamundaVerifier.mapAdditionalData(validJson);

        assertNotNull(result);
        assertTrue(result.containsKey("additionalData"));
        Map<String, Object> additionalData = (Map<String, Object>) result.get("additionalData");
        assertEquals("value1", additionalData.get("key1"));
        assertEquals(2, additionalData.get("key2"));
    }

    @Test
    void shouldReturnNullWhenJsonIsInvalid() {
        String invalidJson = "{ \"key1\": \"value1\", ";

        Map<String, Object> result = CamundaVerifier.mapAdditionalData(invalidJson);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        Map<String, Object> result = CamundaVerifier.mapAdditionalData(null);

        assertNull(result);
    }
}