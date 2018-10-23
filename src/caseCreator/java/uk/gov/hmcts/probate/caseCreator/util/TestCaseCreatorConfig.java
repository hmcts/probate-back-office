package uk.gov.hmcts.probate.caseCreator.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("application.properties")
public class TestCaseCreatorConfig {

    @Bean
    public RelaxedServiceAuthTokenGenerator relaxedServiceAuthTokenGenerator(@Value("${service.auth.provider.base.url}") String s2sUrl,
                                                                             @Value("${s2s-auth.totp_secret}") String secret,
                                                                             @Value("${service.name}") String microservice) {
        return new RelaxedServiceAuthTokenGenerator(secret, microservice, s2sUrl);
    }
}
