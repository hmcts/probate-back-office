package uk.gov.hmcts.probate.dmnutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CamundaVerifier {
    public static void resultsMatchUsingNameKey(List<Map<String, Object>> results,
                                                List<Map<String, Object>> expectation) {
        // Create a mutable copy of the results list
        results = new ArrayList<>(results);

        // Remove entries from results where the "name" key contains "dueDateOrigin"
        results.removeIf(result -> result.containsKey("name")
                && result.get("name").toString().contains("dueDateOrigin"));


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

    public static Map<String, Object> mapAdditionalData(String additionalData) {
        if (additionalData == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
            return Map.of("additionalData", mapper.readValue(additionalData, typeRef));
        } catch (IOException exp) {
            return null;
        }
    }
}
