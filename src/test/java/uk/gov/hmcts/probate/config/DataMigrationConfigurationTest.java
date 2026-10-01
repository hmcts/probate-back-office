package uk.gov.hmcts.probate.config;

import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DataMigrationConfigurationTest {

    private final DataMigrationConfiguration configuration = new DataMigrationConfiguration();

    @Test
    void shouldConfigureGorMigrationHandlers() {
        Dtspb5005GorRollback dtspb5005GorRollback = mock();
        Dtspb5113GorMigration dtspb5113GorMigration = mock();
        Dtspb5113GorRollback dtspb5113GorRollback = mock();

        Map<String, GorMigrationHandler> handlers =
                configuration.gorMigrationHandlers(
                        dtspb5005GorRollback,
                        dtspb5113GorMigration,
                        dtspb5113GorRollback
                );

        Map<String, GorMigrationHandler> expected = Map.of(
                "DTSPB-5005_rollback", dtspb5005GorRollback,
                "DTSPB-5113", dtspb5113GorMigration,
                "DTSPB-5113_rollback", dtspb5113GorRollback
        );

        assertThat(handlers)
                .containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void shouldConfigureCaveatMigrationHandlers() {
        Dtspb5005CaveatRollback dtspb5005CaveatRollback = mock();
        Dtspb5064CaveatMigrationHandler dtspb5064CaveatMigrationHandler = mock();
        Dtspb5064CaveatRollback dtspb5064CaveatRollback = mock();

        Dtspb5112PaAppCreatedCaveatMigration dtspb5112PaAppCreatedCaveatMigration = mock();
        Dtspb5112PaAppCreatedCaveatRollback dtspb5112PaAppCreatedCaveatRollback = mock();
        Dtspb5112SetExpiryCaveatRollback dtspb5112SetExpiryCaveatRollback = mock();
        Dtspb5112CloseNewlyExpiredCaveatMigration dtspb5112CloseNewlyExpiredCaveatMigration = mock();
        Dtspb5112CloseNewlyExpiredCaveatRollback dtspb5112CloseNewlyExpiredCaveatRollback = mock();
        Dtspb5112CloseExistingExpiredCaveatMigration dtspb5112CloseExistingExpiredCaveatMigration = mock();
        Dtspb5112CloseExistingExpiredCaveatRollback dtspb5112CloseExistingExpiredCaveatRollback = mock();

        Map<String, CaveatMigrationHandler> handlers =
            configuration.caveatMigrationHandlers(
                dtspb5005CaveatRollback,
                dtspb5064CaveatMigrationHandler,
                dtspb5064CaveatRollback,
                dtspb5112PaAppCreatedCaveatMigration,
                dtspb5112PaAppCreatedCaveatRollback,
                dtspb5112SetExpiryCaveatRollback,
                dtspb5112CloseNewlyExpiredCaveatMigration,
                dtspb5112CloseNewlyExpiredCaveatRollback,
                dtspb5112CloseExistingExpiredCaveatMigration,
                dtspb5112CloseExistingExpiredCaveatRollback
            );

        Map<String, CaveatMigrationHandler> expected = Map.ofEntries(
                Map.entry("DTSPB-5005_rollback", dtspb5005CaveatRollback),
                Map.entry("DTSPB-5064", dtspb5064CaveatMigrationHandler),
                Map.entry("DTSPB-5064_rollback", dtspb5064CaveatRollback),
                Map.entry("DTSPB-5112-pa-app-created", dtspb5112PaAppCreatedCaveatMigration),
                Map.entry("DTSPB-5112-pa-app-created-rollback", dtspb5112PaAppCreatedCaveatRollback),
                Map.entry("DTSPB-5112-set-expiry-rollback", dtspb5112SetExpiryCaveatRollback),
                Map.entry("DTSPB-5112-close-newly-expired", dtspb5112CloseNewlyExpiredCaveatMigration),
                Map.entry("DTSPB-5112-close-newly-expired-rollback", dtspb5112CloseNewlyExpiredCaveatRollback),
                Map.entry("DTSPB-5112-close-existing-expired", dtspb5112CloseExistingExpiredCaveatMigration),
                Map.entry("DTSPB-5112-close-existing-expired-rollback", dtspb5112CloseExistingExpiredCaveatRollback)
        );

        assertThat(handlers).containsExactlyInAnyOrderEntriesOf(expected);
    }
}