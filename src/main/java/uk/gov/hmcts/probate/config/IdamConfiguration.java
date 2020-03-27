package uk.gov.hmcts.probate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Client;
import feign.Logger;
import feign.codec.Decoder;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class IdamConfiguration {

    @Bean
    public Client getFeignHttpClient() {
        return new ApacheHttpClient(getHttpClient());
    }

    private CloseableHttpClient getHttpClient() {
        int timeout = 10000;
        RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(timeout)
            .setConnectionRequestTimeout(timeout)
            .setSocketTimeout(timeout)
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
