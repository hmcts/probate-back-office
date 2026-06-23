package uk.gov.hmcts.probate.dmnutils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ADDITIONAL_PROPERTIES_ROLE_ASSIGNMENT_ID;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_MANAGEMENT_CATEGORY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DESCRIPTION;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DUE_DATE_INTERVAL_DAYS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DUE_DATE_NON_WORKING_CALENDAR;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DUE_DATE_ORIGIN;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DUE_DATE_TIME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DUE_DATE_WORKING_DAYS_OF_WEEK;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.LOCATION;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.LOCATION_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.MAJOR_PRIORITY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.MINOR_PRIORITY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.PRIORITY_DATE_ORIGIN_REF;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REGION;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ROLE_CATEGORY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.WORK_TYPE;

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
        ConfigurationExpectationBuilder builder = new ConfigurationExpectationBuilder();
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
