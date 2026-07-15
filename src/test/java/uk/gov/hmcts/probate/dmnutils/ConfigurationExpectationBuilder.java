package uk.gov.hmcts.probate.dmnutils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.*;

public class ConfigurationExpectationBuilder {

    private static final List<String> EXPECTED_PROPERTIES = Arrays.asList(
            CASE_NAME, CASE_MANAGEMENT_CATEGORY, REGION, LOCATION, LOCATION_NAME, MAJOR_PRIORITY, MINOR_PRIORITY,
            DUE_DATE_NON_WORKING_CALENDAR, DUE_DATE_WORKING_DAYS_OF_WEEK, WORK_TYPE, ROLE_CATEGORY,
            DUE_DATE_INTERVAL_DAYS,
            ADDITIONAL_PROPERTIES_ROLE_ASSIGNMENT_ID, DESCRIPTION, PRIORITY_DATE_ORIGIN_REF, DUE_DATE_ORIGIN,
            DUE_DATE_TIME
    );

    private final Map<String, Map<String, Object>> expectations = new HashMap<>();

    public static ConfigurationExpectationBuilder defaultExpectations() {
        return new ConfigurationExpectationBuilder();
    }

    public static ConfigurationExpectationBuilder defaultExamineDigitalCaseExpectations() {
        ConfigurationExpectationBuilder builder = new ConfigurationExpectationBuilder();
        builder.expectedValue(DESCRIPTION, DESCRIPTION_EXAMINE_DIGITAL_CASE_PROBATE_DEFAULT_VALUE, true)
                .expectedValue(WORK_TYPE, DECISION_WORK_TYPE_PROBATE, true)
                .expectedValue(CASE_MANAGEMENT_CATEGORY, "Probate", true)
                .expectedValue(CASE_NAME, REFERENCE_VALUE, true)
                .expectedValue(REGION, "DUMMY_PLACEHOLDER_REGION", true)
                .expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_CTSC, true)
                .expectedValue(LOCATION, REGISTRY_LOCATION_VALUE, true)
                .expectedValue(LOCATION_NAME, REGISTRY_LOCATION_VALUE, true)
                .expectedValue(DUE_DATE_NON_WORKING_CALENDAR, DUE_DATE_NON_WORKING_CALENDAR_VALUE, true)
                .expectedValue(DUE_DATE_TIME, DUE_DATE_TIME_VALUE, true)
                .expectedValue(DUE_DATE_INTERVAL_DAYS, DUE_DATE_INTERVAL_DAYS_VALUE, true)
                .expectedValue(DUE_DATE_NON_WORKING_DAYS_OF_WEEK,
                        DUE_DATE_NON_WORKING_DAYS_OF_WEEK_VALUE, true)
                .expectedValue(PRIORITY_DATE_ORIGIN_REF, PRIORITY_DATE_ORIGIN_REF_VALUE, true);
        return builder;
    }

    public List<Map<String, Object>> build() {
        return EXPECTED_PROPERTIES.stream()
                .filter(expectations::containsKey)
                .map(expectations::get)
                .collect(Collectors.toList());
    }

    public ConfigurationExpectationBuilder expectedValue(String name, Object value, boolean canReconfigure) {
        expectations.put(name, Map.of(
                "name", name,
                "value", value,
                "canReconfigure", canReconfigure
        ));
        return this;
    }
}
