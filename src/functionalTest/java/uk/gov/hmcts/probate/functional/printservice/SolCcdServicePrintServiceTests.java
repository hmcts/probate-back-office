package uk.gov.hmcts.probate.functional.printservice;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServicePrintServiceTests extends IntegrationTestBase {

    @Test
    public void verifySuccessForGetPrintTemplateDocuments() {
        Response response = RestAssured.given()
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
        Response response = RestAssured.given().relaxedHTTPSValidation()
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
        Response response = RestAssured.given().relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .when().get("/template/case-details/pa");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Case Number:"));
        assertTrue(response.getBody().asString().contains("Jurisdiction:"));
        assertTrue(response.getBody().asString().contains("Case Type:"));
        assertTrue(response.getBody().asString().contains("Record type:"));
        assertTrue(response.getBody().asString().contains("Date digital application submitted:"));
        assertTrue(response.getBody().asString().contains("PA"));
        assertTrue(response.getBody().asString().contains("Applicant forename(s):"));
        assertTrue(response.getBody().asString().contains("Applicant surname:"));
        assertTrue(response.getBody().asString().contains("Applicant address"));
        assertTrue(response.getBody().asString().contains("Applicant phone number"));
        assertTrue(response.getBody().asString().contains("Applicant email address"));
        assertTrue(response.getBody().asString().contains("Deceased first name(s):"));
        assertTrue(response.getBody().asString().contains("Deceased last name:"));
        assertTrue(response.getBody().asString().contains("Deceased date of birth:"));
        assertTrue(response.getBody().asString().contains("Deceased date of death:"));
        assertTrue(response.getBody().asString().contains("Assets in an alias name:"));
        assertTrue(response.getBody().asString().contains("Names used by the deceased for other assets:"));
        assertTrue(response.getBody().asString().contains("Executor details"));
        assertTrue(response.getBody().asString().contains("Number of executors:"));
        assertTrue(response.getBody().asString().contains("Additional information"));

    }

    @Test
    public void verifyprobateManLegacyCaseReturnsOkResponseCode() {
        Response response = RestAssured.given().relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .when().get("/template/probateManLegacyCase");
        response.prettyPrint();

        assertThat(response.statusCode(),is(equalTo(200)));
        assertTrue(response.getBody().asString().contains("Probate Man Legacy Case"));
    }

    @Test
    public void verifyprobateManLegacyCaseReturnsBadResponseCode() {
        Response response = RestAssured.given().relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId("serviceToken","userId"))
                .when().get("/template/probateManLegacyCase");
        response.prettyPrint();

        assertThat(response.statusCode(),is(equalTo(403)));
        assertTrue(response.getBody().asString().contains("Access Denied"));
    }
}


