package uk.gov.hmcts.probate.config;

import feign.Feign;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

@Configuration
public class ServiceTokenGeneratorConfiguration {
    @Bean
    public ServiceAuthTokenGenerator serviceAuthTokenGenerator(
            @Value("${auth.provider.service.client.baseUrl}") String s2sUrl,
            @Value("${auth.provider.service.client.key}") String secret,
            @Value("${auth.provider.service.client.microservice}") String microservice) {

        final ServiceAuthorisationApi serviceAuthorisationApi = Feign.builder()
                .encoder(new JacksonEncoder())
                .contract(new SpringMvcContract())
                .target(ServiceAuthorisationApi.class, s2sUrl);
        return new ServiceAuthTokenGenerator(secret, microservice, serviceAuthorisationApi);
    }
}