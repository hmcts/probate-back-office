package uk.gov.hmcts.probate.functional;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("uk.gov.hmcts.probate.functional")
@PropertySource("classpath:functional-application.yml")
public class TestContextConfiguration {
}
