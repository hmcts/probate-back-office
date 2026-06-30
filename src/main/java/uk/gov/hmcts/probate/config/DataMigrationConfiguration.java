package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.probate.service.migration.CaveatMigrationHandler;
import uk.gov.hmcts.probate.service.migration.Dtspb5005CaveatRollback;
import uk.gov.hmcts.probate.service.migration.Dtspb5005GorRollback;
import uk.gov.hmcts.probate.service.migration.GorMigrationHandler;
import uk.gov.hmcts.probate.service.migration.Dtspb5539GORMigration;
import uk.gov.hmcts.probate.service.migration.Dtspb5539CaveatMigration;

import java.util.Map;

@Configuration
public class DataMigrationConfiguration {

    @Bean
    public Map<String, GorMigrationHandler> gorMigrationHandlers(
            final Dtspb5005GorRollback dtspb5005GorRollback,
            final Dtspb5539GORMigration dtsp5539GORMigration) {
        return Map.of(
                "DTSPB-5005_rollback", dtspb5005GorRollback,
                "DTSPB-5539", dtsp5539GORMigration);
    }

    @Bean
    public Map<String, CaveatMigrationHandler> caveatMigrationHandlers(
            final Dtspb5005CaveatRollback dtspb5005CaveatRollback,
            final Dtspb5539CaveatMigration dtspb5539CaveatMigration) {
        return Map.of(
                "DTSPB-5005_rollback", dtspb5005CaveatRollback,
                "DTSPB-5539", dtspb5539CaveatMigration);
    }

}
