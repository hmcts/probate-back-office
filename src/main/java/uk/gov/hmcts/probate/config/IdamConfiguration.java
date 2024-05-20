package uk.gov.hmcts.probate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Client;
import feign.Logger;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.hc5.ApacheHttp5Client;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class IdamConfiguration {

    @Bean
    public Client getFeignHttpClient() {
        return new ApacheHttp5Client(getHttpClient());
    }

    private CloseableHttpClient getHttpClient() {
        int timeout = 10000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(timeout))
                .setResponseTimeout(Timeout.ofMilliseconds(timeout))
                .build();

        return HttpClients.custom()
                .useSystemProperties()
                .disableRedirectHandling()
                .setDefaultRequestConfig(config)
                .build();
    }

    @Bean
    @Primary
    Decoder feignDecoder(ObjectMapper objectMapper) {
        return new JacksonDecoder(objectMapper);
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
