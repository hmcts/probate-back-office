package uk.gov.hmcts.probate.config;

import feign.Client;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.httpclient.ApacheHttpClient;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FeignFTPConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;
    
    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }
}
