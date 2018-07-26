package uk.gov.hmcts.probate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.model.ccd.raw.BigDecimalSerializer;

import java.math.BigDecimal;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public ResourceBundleMessageSource validationMessageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("ValidationMessages");
        return source;
    }

    @Bean
    public ResourceBundleMessageSource resourceMessageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("ResourceMessages");
        return source;
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Primary
    @Bean
    public ObjectMapper objectMapper(ObjectMapper objectMapper) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
