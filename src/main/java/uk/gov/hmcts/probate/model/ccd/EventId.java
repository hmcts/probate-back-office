package uk.gov.hmcts.probate.model.ccd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventId {

    IMPORT_GOR_CASE("boImportGrant"),
    IMPORT_CAVEAT("importCaveat"),
    IMPORT_STANDING_SEARCH("importSS"),
    IMPORT_WILL_LODGEMENT("importWill"),
    EXCEPTION_RECORD_GOR_CASE("createCase"),
    EXCEPTION_RECORD_CAVEAT("createCase"),
    SCHEDULED_START_GRANT_DELAY_NOTIFICATION_PERIOD("scheduledStartGrantDelayNotificationPeriod"),
    SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_IDENTIFIED("scheduledUpdateGrantDelayNotificationIdentified"),
    SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_SENT("scheduledUpdateGrantDelayNotificationSent"),
    SCHEDULED_UPDATE_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_SENT("scheduledUpdateGrantAwaitingDocsNotificationSent"),
    GRANT_STOPPED_DATE("grantStoppedDate"),
    GRANT_RESOLVED("grantResolved"),
    DEATH_RECORD_VERIFIED("deathRecordVerified"),
    DEATH_RECORD_VERIFICATION_FAILED("deathRecordVerificationFailed");
    @Getter
    private final String name;

}
