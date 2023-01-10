package uk.gov.hmcts.probate;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.rse.ccd.lib.api.CFTLib;
import uk.gov.hmcts.rse.ccd.lib.api.CFTLibConfigurer;

import java.io.File;

@Component
public class CftLibConfig implements CFTLibConfigurer {
    @Override
    public void configure(CFTLib lib) throws Exception {
        lib.createProfile("probatesolicitortestorgtest1@gmail.com", "PROBATE", "GrantOfRepresentation", "Pending");
        lib.createProfile("probatesolicitortestorgtest1@gmail.com", "PROBATE", "Caveat", "Pending");
        lib.createProfile("probatecaseworker@gmail.com", "PROBATE", "GrantOfRepresentation", "Pending");
        lib.createProfile("probatecaseworker@gmail.com", "PROBATE", "Caveat", "Pending");
        lib.createProfile("probatecaseworker@gmail.com", "PROBATE", "GrantOfRepresentation", "Pending");
        lib.createProfile("probatecaseworker@gmail.com", "PROBATE", "Caveat", "Pending");

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
        lib.importDefinition(new File("jsonToXLS/CCD_Probate_Backoffice.xlsx"));
        lib.importDefinition(new File("jsonToXLS/CCD_Probate_Caveat.xlsx"));
        lib.importDefinition(new File("jsonToXLS/CCD_Probate_Legacy_Cases.xlsx"));
        lib.importDefinition(new File("jsonToXLS/CCD_Probate_Legacy_Search.xlsx"));
        lib.importDefinition(new File("jsonToXLS/CCD_Probate_Will_Lodgement.xlsx"));
        lib.importDefinition(new File("jsonToXLS/CCD_Probate_Standing_Search.xlsx"));
    }
}
