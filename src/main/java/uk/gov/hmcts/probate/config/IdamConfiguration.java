package uk.gov.hmcts.probate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Client;
import feign.Logger;
import feign.codec.Decoder;
import feign.hc5.ApacheHttp5Client;
import feign.jackson.JacksonDecoder;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

public class IdamConfiguration {

    @Bean
    public Client getFeignHttpClient() {
        return new ApacheHttp5Client(getHttpClient());
    }

    private CloseableHttpClient getHttpClient() {
        int timeout = 10000;
        RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(timeout, TimeUnit.MILLISECONDS)
            .setConnectionRequestTimeout(timeout, TimeUnit.MILLISECONDS)
            .build();

        return HttpClientBuilder
            .create()
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
