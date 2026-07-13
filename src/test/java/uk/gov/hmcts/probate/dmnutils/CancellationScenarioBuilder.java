package uk.gov.hmcts.probate.dmnutils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CancellationScenarioBuilder {

    private final String event;
    private final List<Map<String,String>> results = new ArrayList<>();

    private CancellationScenarioBuilder(String event) {
        this.event = event;
    }

    public static CancellationScenarioBuilder event(String event) {
        return new CancellationScenarioBuilder(event);
    }

    public CancellationScenarioBuilder cancel(String processCategories) {
        results.add(Map.of(
            "action", "Cancel",
            "processCategories", processCategories
        ));
        return this;
    }

    public CancellationScenarioBuilder cancelAll() {
        results.add(Map.of(
            "action", "Cancel"
        ));
        return this;
    }

    public Arguments build() {
        return Arguments.of(
            null,
            event,
            null,
            results
        );
    }
}
