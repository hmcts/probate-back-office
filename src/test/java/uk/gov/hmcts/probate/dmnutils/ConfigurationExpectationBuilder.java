package uk.gov.hmcts.probate.dmnutils;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ADDITIONAL_PROPERTIES_ROLE_ASSIGNMENT_ID;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.CASE_MANAGEMENT_CATEGORY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.CASE_NAME;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DEFAULT_CASE_MANAGEMENT_CATEGORY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DEFAULT_DUE_DATE_NON_WORKING_CALENDAR;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DEFAULT_DUE_DATE_WORKING_DAYS_OF_WEEK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DEFAULT_LOCATION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DEFAULT_LOCATION_NAME;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DEFAULT_MAJOR_PRIORITY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DEFAULT_MINOR_PRIORITY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DEFAULT_REGION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DESCRIPTION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_INTERVAL_DAYS;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_NON_WORKING_CALENDAR;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_ORIGIN;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_TIME;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.DUE_DATE_WORKING_DAYS_OF_WEEK;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.LOCATION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.LOCATION_NAME;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.MAJOR_PRIORITY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.MINOR_PRIORITY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.PRIORITY_DATE_ORIGIN_REF;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.REGION;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_ADMIN;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROUTINE_WORK_TYPE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.WORK_TYPE;

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
        builder.expectedValue(CASE_NAME, "Joe Blogs", true);
        builder.expectedValue(CASE_MANAGEMENT_CATEGORY, DEFAULT_CASE_MANAGEMENT_CATEGORY, true);
        builder.expectedValue(REGION, DEFAULT_REGION, true);
        builder.expectedValue(LOCATION, DEFAULT_LOCATION, true);
        builder.expectedValue(LOCATION_NAME, DEFAULT_LOCATION_NAME, true);
        builder.expectedValue(DUE_DATE_NON_WORKING_CALENDAR, DEFAULT_DUE_DATE_NON_WORKING_CALENDAR, true);
        builder.expectedValue(DUE_DATE_WORKING_DAYS_OF_WEEK, DEFAULT_DUE_DATE_WORKING_DAYS_OF_WEEK, false);
        builder.expectedValue(WORK_TYPE, ROUTINE_WORK_TYPE, true);
        builder.expectedValue(ROLE_CATEGORY, ROLE_CATEGORY_ADMIN, true);
        builder.expectedValue(MINOR_PRIORITY, DEFAULT_MINOR_PRIORITY, true);
        builder.expectedValue(MAJOR_PRIORITY, DEFAULT_MAJOR_PRIORITY, true);
        builder.expectedValue(DESCRIPTION, "[Orders: Send order]", true);
        builder.expectedValue(PRIORITY_DATE_ORIGIN_REF, LocalDate.now(), true);
        builder.expectedValue(DUE_DATE_ORIGIN, ZonedDateTime.now(), false);
        builder.expectedValue(DUE_DATE_TIME, "17:00", false);
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
