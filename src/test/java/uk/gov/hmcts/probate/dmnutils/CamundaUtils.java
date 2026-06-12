package uk.gov.hmcts.probate.dmnutils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CamundaUtils {
    public static void resultsMatchUsingNameKey(List<Map<String, Object>> results,
                                                List<Map<String, Object>> expectation) {
        assertThat(results.size(), is(expectation.size()));

        for (Map<String, Object> expectedEntry : expectation) {
            String expectedName = (String) expectedEntry.get("name");
            Map<String, Object> resultEntry = results.stream()
                    .filter(result -> expectedName.equals(result.get("name")))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("No result found for name: " + expectedName));

            for (String key : expectedEntry.keySet()) {
                assertEquals(expectedEntry.get(key), resultEntry.get(key),
                        "Mismatch for key: " + key + " in entry with name: " + expectedName);
            }
        }
    }

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

        CamundaUtils.resultsMatchUsingNameKey(results, expectation);
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

        CamundaUtils.resultsMatchUsingNameKey(results, expectation);
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

        assertThrows(AssertionError.class, () -> CamundaUtils.resultsMatchUsingNameKey(results, expectation));
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

        assertThrows(AssertionError.class, () -> CamundaUtils.resultsMatchUsingNameKey(results, expectation));
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

        assertThrows(AssertionError.class, () -> CamundaUtils.resultsMatchUsingNameKey(results, expectation));
    }
}
