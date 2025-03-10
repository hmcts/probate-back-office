package uk.gov.hmcts.probate.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.nio.charset.Charset;

import static java.util.Arrays.asList;

@Component
public class EvidenceManagementRestTemplate extends RestTemplate {
    private static final MediaType MEDIA_TYPE_HAL_JSON =
            new MediaType("application", "vnd.uk.gov.hmcts.dm.document-collection.v1+hal+json",
                    Charset.defaultCharset());
    private static final MediaType MEDIA_TYPE_DOC = new MediaType("application",
            "vnd.uk.gov.hmcts.dm.document.v1+hal+json");


    @Value("${http.connect.timeout}")
    private int httpConnectTimeout;

    @Value("${http.connect.request.timeout}")
    private int httpConnectRequestTimeout;

    public EvidenceManagementRestTemplate() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        objectMapper.registerModule(new Jackson2HalModule());

        MappingJackson2HttpMessageConverter jackson2HttpConverter = new MappingJackson2HttpMessageConverter();
        jackson2HttpConverter.setObjectMapper(objectMapper);
        jackson2HttpConverter.setSupportedMediaTypes(ImmutableList.of(MEDIA_TYPE_HAL_JSON, MEDIA_TYPE_DOC));

        HttpMessageConverter<Resource> resourceHttpMessageConverter = new ResourceHttpMessageConverter();

        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        formHttpMessageConverter.addPartConverter(jackson2HttpConverter);
        formHttpMessageConverter.addPartConverter(resourceHttpMessageConverter);

        this.setMessageConverters(asList(jackson2HttpConverter,
                resourceHttpMessageConverter,
                formHttpMessageConverter,
                new StringHttpMessageConverter()));
        this.setRequestFactory(getClientHttpRequestFactory());
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofDays(httpConnectTimeout))
                .setConnectionRequestTimeout(Timeout.ofDays(httpConnectRequestTimeout))
                .build();

        CloseableHttpClient client = HttpClientBuilder
                .create()
                .useSystemProperties()
                .setDefaultRequestConfig(config)
                .build();

        return new HttpComponentsClientHttpRequestFactory(client);
    }
}
