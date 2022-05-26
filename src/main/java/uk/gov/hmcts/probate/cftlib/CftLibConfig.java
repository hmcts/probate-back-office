package uk.gov.hmcts.probate.cftlib;

import java.io.File;

import org.springframework.stereotype.Component;

import uk.gov.hmcts.rse.ccd.lib.api.CFTLib;
import uk.gov.hmcts.rse.ccd.lib.api.CFTLibConfigurer;

@Component
public class CftLibConfig implements CFTLibConfigurer {
    @Override
    public void configure(CFTLib lib) throws Exception {
        lib.createIdamUser("test@user.com", "caseworker", "caseworker-probate", "caseworker-probate-solicitor");
        lib.createRoles(
            "caseworker-probate",
            "caseworker-probate-superuser",
            "caseworker-probate-registrar",
            "caseworker-probate-issuer",
            "caseworker-probate-pcqextractor",
            "caseworker-probate-solicitor",
            "caseworker-probate-caseadmin",
            "caseworker-probate-caseofficer",
            "caseworker-probate-systemupdate",
            "caseworker-probate-scheduler",
            "caseworker-probate-charity"
        );
        lib.importDefinition(new File("jsontoXLS/CCD_Probate_Backoffice.xlsx"));
    }
}