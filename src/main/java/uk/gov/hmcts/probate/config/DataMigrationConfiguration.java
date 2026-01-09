package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.probate.service.migration.CaveatMigrationHandler;
import uk.gov.hmcts.probate.service.migration.GorMigrationHandler;

import java.util.Map;

@Configuration
public class DataMigrationConfiguration {

    @Bean
    public Map<String, GorMigrationHandler> gorMigrationHandlers() {
        return Map.of();
    }

    @Bean
    public Map<String, CaveatMigrationHandler> caveatMigrationHandlers() {
        return Map.of();
    }

}
