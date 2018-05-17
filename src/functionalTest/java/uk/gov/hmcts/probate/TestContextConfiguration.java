package uk.gov.hmcts.probate;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("uk.gov.hmcts.probate")
@PropertySource("classpath:application.properties")
public class TestContextConfiguration {
}
