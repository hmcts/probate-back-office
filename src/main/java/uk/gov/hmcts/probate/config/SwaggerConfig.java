package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalDate;
import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket validationApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .globalOperationParameters(Arrays.asList(serviceAuthorizationParameterBuilder(),
                authorizationParameterBuilder()))
            .directModelSubstitute(LocalDate.class, java.sql.Date.class)
            .apiInfo(apiInfo())
            .tags(new Tag("tag1", "Tag 1 description."),
                new Tag("tag2", "Tag 2 description."),
                new Tag("tag2", "Tag 3 description."))
            .select()
            .apis(RequestHandlerSelectors.basePackage("uk.gov.hmcts.probate.controller"))
            .paths(PathSelectors.any())
            .build();
    }

    private Parameter serviceAuthorizationParameterBuilder() {
        return new ParameterBuilder()
            .name("ServiceAuthorization")
            .description("User authorization header")
            .required(true)
            .parameterType("header")
            .modelRef(new ModelRef("string"))
            .build();
    }

    private Parameter authorizationParameterBuilder() {
        return new ParameterBuilder()
            .name("Authorization")
            .description("Authorization header")
            .required(true)
            .parameterType("header")
            .modelRef(new ModelRef("string"))
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Solicitor CCD service")
            .description("Provides data validation and other services")
            .license("MIT License")
            .version("1.0")
            .build();
    }
}
