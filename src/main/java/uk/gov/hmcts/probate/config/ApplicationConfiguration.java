package uk.gov.hmcts.probate.config;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;

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

    @Bean
    public Parser parser() {
        return Parser.builder().build();
    }

    @Bean
    public HtmlRenderer htmlRenderer() {
        return HtmlRenderer.builder().build();
    }
}
