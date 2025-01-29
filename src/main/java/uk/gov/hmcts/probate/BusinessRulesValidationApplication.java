package uk.gov.hmcts.probate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.applicationinsights.attach.ApplicationInsights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.gov.hmcts.probate.model.ccd.raw.BigDecimalSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.LocalDateTimeSerializer;
import uk.gov.hmcts.probate.service.task.ScheduledTaskRunner;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataClientAutoConfiguration;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@SpringBootApplication(exclude = {CoreCaseDataClientAutoConfiguration.class})
@EnableFeignClients(basePackages = {"uk.gov.hmcts.reform.idam", "uk.gov.hmcts.reform.ccd",
    "uk.gov.hmcts.reform.sendletter",
    "uk.gov.hmcts.probate.service"})
@EnableScheduling
public class BusinessRulesValidationApplication implements CommandLineRunner {

    public static final String TASK_NAME = "TASK_NAME";

    @Value("#{'${authorised.services}'.split(',\\s*')}")
    private List<String> authorisedServices;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScheduledTaskRunner taskRunner;

    public static void main(final String[] args) {
        ApplicationInsights.attach();
        final var application = new SpringApplication(BusinessRulesValidationApplication.class);
        final var instance = application.run(args);

        if (System.getenv(TASK_NAME) != null) {
            instance.close();
        }
    }

    @Override
    public void run(String... args) {
        if (System.getenv(TASK_NAME) != null) {
            taskRunner.run(System.getenv(TASK_NAME));
        }
    }

    @Bean
    @Qualifier(value = "authorizedServiceExtractor")
    public Function<HttpServletRequest, Collection<String>> authorizedServicesExtractor() {
        return request -> authorisedServices;
    }

    @PostConstruct
    public void setUp() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        objectMapper.registerModule(module);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(new LocalDateTimeSerializer());
        objectMapper.registerModule(javaTimeModule);
    }
}
