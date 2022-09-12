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
        for (File folder : new File("ccdImports/configFiles").listFiles()) {
            lib.importJsonDefinition(folder);
        }
    }
}
