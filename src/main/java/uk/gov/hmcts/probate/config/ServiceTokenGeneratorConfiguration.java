package uk.gov.hmcts.probate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGeneratorFactory;

@Configuration
public class ServiceTokenGeneratorConfiguration {
    @Bean
    public AuthTokenGenerator serviceAuthTokenGenerator(
        final ServiceAuthorisationApi serviceAuthorisationApi,
        @Value("${idam.s2s-auth.totp_secret}") String secret,
        @Value("${idam.s2s-auth.microservice}") String microservice) {

        return AuthTokenGeneratorFactory.createDefaultGenerator(secret, microservice, serviceAuthorisationApi);
    }
}