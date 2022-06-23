package uk.gov.hmcts.probate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI validationApi() {
        return new OpenAPI()
                .info(new Info().title("Caseworker and Solicitor CCD callback service")
                        .description("Callback handler for CCD")
                        .version("v0.0.1"));
    }
}
