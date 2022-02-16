package uk.gov.hmcts.probate.service;

public enum UserRole {
    REGISTRAR("caseworker-probate-registrar"),
    SCHEDULER("caseworker-probate-scheduler"),
    SOLICITOR("caseworker-probate-solicitor"),
    CHARITY("caseworker-probate-charity"),
    SUPER_USER("caseworker-probate-superuser"),
    SYSTEM_USER("caseworker-probate-systemupdate"),
    CASE_ADMIN("caseworker-probate-caseadmin"),
    CASE_OFFICER("caseworker-probate-caseofficer"),
    ISSUER("caseworker-probate-issuer"),
    CITIZEN("citizen");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
