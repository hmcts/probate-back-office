package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.probate.service.migration.CaveatMigrationHandler;
import uk.gov.hmcts.probate.service.migration.Dtspb5005CaveatRollback;
import uk.gov.hmcts.probate.service.migration.Dtspb5005GorRollback;
import uk.gov.hmcts.probate.service.migration.Dtspb5064CaveatMigrationHandler;
import uk.gov.hmcts.probate.service.migration.Dtspb5064CaveatRollback;
import uk.gov.hmcts.probate.service.migration.Dtspb5113GorMigration;
import uk.gov.hmcts.probate.service.migration.Dtspb5113GorRollback;
import uk.gov.hmcts.probate.service.migration.GorMigrationHandler;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112CloseExistingExpiredCaveatMigration;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112CloseExistingExpiredCaveatRollback;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112CloseNewlyExpiredCaveatMigration;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112CloseNewlyExpiredCaveatRollback;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112PaAppCreatedCaveatMigration;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112PaAppCreatedCaveatRollback;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112SetExpiryCaveatRollback;

import java.util.Map;

@Configuration
public class DataMigrationConfiguration {

    @Bean
    public Map<String, GorMigrationHandler> gorMigrationHandlers(
            final Dtspb5005GorRollback dtspb5005GorRollback,
            final Dtspb5113GorMigration dtspb5113GorMigration,
            final Dtspb5113GorRollback dtspb5113GorRollback) {
        return Map.of(
                "DTSPB-5005_rollback", dtspb5005GorRollback,
                "DTSPB-5113", dtspb5113GorMigration,
                "DTSPB-5113_rollback", dtspb5113GorRollback
                );
    }

    @Bean
    public Map<String, CaveatMigrationHandler> caveatMigrationHandlers(
            final Dtspb5005CaveatRollback dtspb5005CaveatRollback,
            final Dtspb5064CaveatMigrationHandler dtspb5064CaveatMigrationHandler,
            final Dtspb5064CaveatRollback dtspb5064CaveatRollback,
            final Dtspb5112PaAppCreatedCaveatMigration dtspb5112PaAppCreatedCaveatMigration,
            final Dtspb5112PaAppCreatedCaveatRollback dtspb5112PaAppCreatedCaveatRollback,
            final Dtspb5112SetExpiryCaveatRollback dtspb5112SetExpiryCaveatRollback,
            final Dtspb5112CloseNewlyExpiredCaveatMigration dtspb5112CloseNewlyExpiredCaveatMigration,
            final Dtspb5112CloseNewlyExpiredCaveatRollback dtspb5112CloseNewlyExpiredCaveatRollback,
            final Dtspb5112CloseExistingExpiredCaveatMigration dtspb5112CloseExistingExpiredCaveatMigration,
            final Dtspb5112CloseExistingExpiredCaveatRollback dtspb5112CloseExistingExpiredCaveatRollback) {
        return Map.of(
                "DTSPB-5005_rollback", dtspb5005CaveatRollback,
                "DTSPB-5064", dtspb5064CaveatMigrationHandler,
                "DTSPB-5064_rollback", dtspb5064CaveatRollback,
                "DTSPB-5112-pa-app-created", dtspb5112PaAppCreatedCaveatMigration,
                "DTSPB-5112-pa-app-created-rollback", dtspb5112PaAppCreatedCaveatRollback,
                "DTSPB-5112-set-expiry-rollback", dtspb5112SetExpiryCaveatRollback,
                "DTSPB-5112-close-newly-expired", dtspb5112CloseNewlyExpiredCaveatMigration,
                "DTSPB-5112-close-newly-expired-rollback", dtspb5112CloseNewlyExpiredCaveatRollback,
                "DTSPB-5112-close-existing-expired", dtspb5112CloseExistingExpiredCaveatMigration,
                "DTSPB-5112-close-existing-expired-rollback", dtspb5112CloseExistingExpiredCaveatRollback);
    }

}
