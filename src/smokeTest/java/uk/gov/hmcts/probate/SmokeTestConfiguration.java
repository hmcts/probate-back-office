package uk.gov.hmcts.probate;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("file:src/test/resources/application.properties"),
        @PropertySource("classpath:uk/gov/hmcts/probate/sol/git.properties")})
public class SmokeTestConfiguration {
}
