package uk.gov.hmcts.probate;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:src/test/resources/application.properties")
public class SmokeTestConfiguration {
}
