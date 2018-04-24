package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;

import static java.util.Collections.singletonList;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket validationApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .globalOperationParameters(singletonList(
                new ParameterBuilder()
                    .name("ServiceAuthorization")
                    .description("User authorization header")
                    .required(true)
                    .parameterType("header")
                    .modelRef(new ModelRef("string"))
                    .build()
            ))
            .directModelSubstitute(LocalDate.class, java.sql.Date.class)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("uk.gov.hmcts.probate.controller"))
            .paths(PathSelectors.any())
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
