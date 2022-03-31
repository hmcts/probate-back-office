package uk.gov.hmcts.probate.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI validationApi() {
        return new OpenAPI()
                .components(new Components()
                        .addHeaders("Authorization",
                                new Header()
                                        .description("User authorization header")
                                        .required(true)
                                        .schema(new StringSchema()))
                        .addHeaders("ServiceAuthorization",
                                new Header()
                                        .description("Service authorization header")
                                        .required(true)
                                        .schema(new StringSchema())))
                .tags(List.of(new Tag().name("tag1").description("Tag 1 description."),
                        new Tag().name("tag2").description("Tag 2 description."),
                        new Tag().name("tag3").description("Tag 3 description.")))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("Solicitor CCD service")
            .description("Provides data validation and other services")
            .version("1.0");
    }
}
