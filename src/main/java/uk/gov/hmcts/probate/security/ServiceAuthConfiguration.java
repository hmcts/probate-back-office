package uk.gov.hmcts.probate.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Feign;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;


@Slf4j
@Configuration
public class ServiceAuthConfiguration {

    public ServiceAuthTokenGenerator serviceAuthTokenGenerator(@Value("${idam.s2s-auth.url}")
                                                                   String s2sUrl,
                                                               @Value("${idam.s2s-auth.totp_secret}") String secret,
                                                               @Value("${idam.s2s-auth.microservice}") String microservice) {
        final ServiceAuthorisationApi serviceAuthorisationApi = Feign.builder()
            .encoder(new JacksonEncoder())
            .contract(new SpringMvcContract())
            .target(ServiceAuthorisationApi.class, s2sUrl);
        return new ServiceAuthTokenGenerator(secret, microservice, serviceAuthorisationApi);
    }

    @Bean
    public AuthTokenValidator tokenValidator(ServiceAuthorisationApi s2sApi) {
        return new ServiceAuthTokenValidator(s2sApi);
    }
}
