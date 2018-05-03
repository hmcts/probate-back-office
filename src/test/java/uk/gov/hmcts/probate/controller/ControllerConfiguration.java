package uk.gov.hmcts.probate.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@TestConfiguration
@ComponentScan(value = "uk.gov.hmcts.probate",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = TestConfiguration.class)})
public class ControllerConfiguration {
}
