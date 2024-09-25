package uk.gov.hmcts.probate.functional.printservice;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SerenityJUnit5Extension.class)
public class SolCcdServicePrintServiceTests extends IntegrationTestBase {

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void verifySuccessForGetPrintTemplateDocuments() throws IOException {
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("success.printCaseDetails.json"))
            .when().post("/template/documents");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("/probate/sol"));
        assertTrue(
            response.getBody().asString().contains("jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases"));
    }


    @Test
    void verifySolsTemplateDetails() {
        final Response response = RestAssured.given()
            .config(config)
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
    void verifyPaTemplateDetails() {
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
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
    void verifyprobateManLegacyCaseReturnsOkResponseCode() {
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .when().get("/template/probateManLegacyCase");

        assertThat(response.statusCode(), is(equalTo(200)));
        assertTrue(response.getBody().asString().contains("Probate Man Legacy Case"));
    }

    @Test
    void verifyprobateManLegacyCaseReturnsBadResponseCode() {
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersForUnauthorisedServiceAndUser())
            .when().get("/template/probateManLegacyCase");

        assertThat(response.statusCode(), is(equalTo(403)));
    }
}


