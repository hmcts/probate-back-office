package uk.gov.hmcts.probate.model;

import org.junit.Assert;
import org.junit.Test;

public class UserRoleTest {
    @Test
    public void shouldReturnUserRoleEnum() {
        Assert.assertEquals("caseworker-probate-registrar", UserRole.REGISTRAR.getValue());
        Assert.assertEquals("caseworker-probate-scheduler", UserRole.SCHEDULER.getValue());
        Assert.assertEquals("caseworker-probate-solicitor", UserRole.SOLICITOR.getValue());
        Assert.assertEquals("caseworker-probate-charity", UserRole.CHARITY.getValue());
        Assert.assertEquals("caseworker-probate-superuser", UserRole.SUPER_USER.getValue());
        Assert.assertEquals("caseworker-probate-systemupdate", UserRole.SYSTEM_USER.getValue());
        Assert.assertEquals("caseworker-probate-caseadmin", UserRole.CASE_ADMIN.getValue());
        Assert.assertEquals("caseworker-probate-caseofficer", UserRole.CASE_OFFICER.getValue());
        Assert.assertEquals("caseworker-probate-issuer", UserRole.ISSUER.getValue());
        Assert.assertEquals("citizen", UserRole.CITIZEN.getValue());
    }
}
