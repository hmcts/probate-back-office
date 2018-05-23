package uk.gov.hmcts.probate.functional.printservice;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


@RunWith(SerenityRunner.class)
public class SolCcdServicePrintServiceTests extends IntegrationTestBase {

    @Test
    public void verifySuccessForGetPrintTemplateDocuments() {
        Response response = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("success.printCaseDetails.json")).
                        when().post("/template/documents");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("/probate/sol"));
        assertTrue(response.getBody().asString().contains("jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases"));
    }


    @Test
    public void verifySolsTemplateDetails() {
        Response response = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .when().get("/template/case-details/sol");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Case number:"));
        assertTrue(response.getBody().asString().contains("Jurisdiction:"));
        assertTrue(response.getBody().asString().contains("Record type:"));
        assertTrue(response.getBody().asString().contains("SOLS"));
        assertTrue(response.getBody().asString().contains("Date digital application submitted:"));
        assertTrue(response.getBody().asString().contains("Solicitor information"));
        assertTrue(response.getBody().asString().contains("Solicitor firm email:"));
        assertTrue(response.getBody().asString().contains("Solicitor firm phone number:"));
        assertTrue(response.getBody().asString().contains("Solicitor name:"));
        assertTrue(response.getBody().asString().contains("Solicitor job title:"));
        assertTrue(response.getBody().asString().contains("Deceased details"));
        assertTrue(response.getBody().asString().contains("Deceased first name(s):"));
        assertTrue(response.getBody().asString().contains("Deceased last name:"));
        assertTrue(response.getBody().asString().contains("Deceased address"));
        assertTrue(response.getBody().asString().contains("Deceased date of birth:"));
        assertTrue(response.getBody().asString().contains("Deceased date of death:"));
        assertTrue(response.getBody().asString().contains("Deceased was domiciled in England or Wales:"));
        assertTrue(response.getBody().asString().contains("Number of executors:"));
        assertTrue(response.getBody().asString().contains("Executors not applying"));
        assertTrue(response.getBody().asString().contains("Not applying reason:"));
        assertTrue(response.getBody().asString().contains("Additional information"));

    }


    @Test
    public void verifyPaTemplateDetails() {
        Response response = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .when().get("/template/case-details/pa");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Case Data Probate Printout"));
        assertTrue(response.getBody().asString().contains("Case Number:"));
        assertTrue(response.getBody().asString().contains("Jurisdiction:"));
        assertTrue(response.getBody().asString().contains("Case Type:"));
        assertTrue(response.getBody().asString().contains("Date digital application submitted:"));
        assertTrue(response.getBody().asString().contains("Type of applicant"));
        assertTrue(response.getBody().asString().contains("Personal"));
        assertTrue(response.getBody().asString().contains("Is the applicant an executor:"));
        assertTrue(response.getBody().asString().contains("Applicant forename(s):"));
        assertTrue(response.getBody().asString().contains("Applicant surname:"));
        assertTrue(response.getBody().asString().contains("Applicant address:"));
        assertTrue(response.getBody().asString().contains("Applicant phone number:"));
        assertTrue(response.getBody().asString().contains("Applicant email address:"));
        assertTrue(response.getBody().asString().contains("Deceased forename(s):"));
        assertTrue(response.getBody().asString().contains("Deceased surname:"));
        assertTrue(response.getBody().asString().contains("At the time of their death the person who died (domiciled in England or Wales?):"));
        assertTrue(response.getBody().asString().contains("Deceased address:"));
        assertTrue(response.getBody().asString().contains("Number of applicants:"));
        assertTrue(response.getBody().asString().contains("Deceased date of birth:"));
        assertTrue(response.getBody().asString().contains("Deceased date of death:"));
        assertTrue(response.getBody().asString().contains("Assets in an alias name:"));

    }
}
