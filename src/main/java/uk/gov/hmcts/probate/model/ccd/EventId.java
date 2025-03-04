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
    SERVICE_REQUEST_PAYMENT_SUCCESS("serviceRequestPaymentSuccess"),
    SERVICE_REQUEST_PAYMENT_FAILED("serviceRequestPaymentFailed"),
    GRANT_STOPPED_DATE("grantStoppedDate"),
    GRANT_RESOLVED("grantResolved"),
    DEATH_RECORD_VERIFIED("deathRecordVerified"),
    DEATH_RECORD_VERIFICATION_FAILED("deathRecordVerificationFailed"),
    MAKE_CASE_DORMANT("makeCaseDormant"),
    REACTIVATE_DORMANT_CASE("reactivateDormantCase"),
    RESEND_DATA("dataExtractResendData"),
    CITIZEN_HUB_RESPONSE("citizenHubResponse"),
    CITIZEN_HUB_RESPONSE_DRAFT("citizenHubResponseDraft"),
    DISPOSE_CASE("disposeCase"),
    GOP_CREATE_DRAFT("createDraft");
    @Getter
    private final String name;

}
