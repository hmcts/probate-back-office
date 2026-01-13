package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.probate.service.migration.CaveatMigrationHandler;
import uk.gov.hmcts.probate.service.migration.Dtspb5005CaveatRollback;
import uk.gov.hmcts.probate.service.migration.Dtspb5005GorRollback;
import uk.gov.hmcts.probate.service.migration.GorMigrationHandler;

import java.util.Map;

@Configuration
public class DataMigrationConfiguration {

    @Bean
    public Map<String, GorMigrationHandler> gorMigrationHandlers(
            final Dtspb5005GorRollback dtspb5005GorRollback) {
        return Map.of(
                "DTSPB-5005_rollback", dtspb5005GorRollback);
    }

    @Bean
    public Map<String, CaveatMigrationHandler> caveatMigrationHandlers(
            final Dtspb5005CaveatRollback dtspb5005CaveatRollback) {
        return Map.of(
                "DTSPB-5005_rollback", dtspb5005CaveatRollback);
    }

}
