package uk.gov.hmcts.probate;
import java.io.File;

import org.springframework.stereotype.Component;

import uk.gov.hmcts.rse.ccd.lib.api.CFTLib;
import uk.gov.hmcts.rse.ccd.lib.api.CFTLibConfigurer;

@Component
public class CftLibConfig implements CFTLibConfigurer {
    @Override
    public void configure(CFTLib lib) throws Exception {

        lib.createIdamUser("testCW@user.com", "caseworker", "caseworker-probate", "caseworker-probate-solicitor");
        lib.createIdamUser("testAdmin@user.com", "caseworker-probate-caseadmin");
        lib.createIdamUser("data.store.idam.system.user@gmail.com","caseworker");
        lib.createRoles(
            "caseworker",
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
            "caseworker-probate-charity",
            "citizen",
            "caseworker-caa"
        );
        lib.importDefinition(new File("jsontoXLS/CCD_Probate_Backoffice.xlsx"));
      //  lib.importDefinition(new File("jsontoXLS/CCD_Probate_Caveat.xlsx"));
      //  lib.importDefinition(new File("jsontoXLS/CCD_Probate_Legacy_Cases.xlsx"));
      //  lib.importDefinition(new File("jsontoXLS/CCD_Probate_Legacy_Search.xlsx"));
       // lib.importDefinition(new File("jsontoXLS/CCD_Probate_Will_Lodgement.xlsx"));
      //  lib.importDefinition(new File("jsontoXLS/CCD_Probate_Standing_Search.xlsx"));
    }
}
