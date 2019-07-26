package uk.gov.hmcts.probate.functional.hardstops;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static net.serenitybdd.rest.SerenityRest.given;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@RunWith(SerenityRunner.class)
public class SolCcdServiceDomicileHardStopTests extends IntegrationTestBase {

    public static final String VALIDATE_URL = "/case/sols-validate";

    @Test
    public void validateDeceasedDetailWithoutDomicileHardStop() {
        given().headers(utils.getHeadersWithUserId())
                .relaxedHTTPSValidation()
                .body(utils.getJsonFromFile("success.deceasedDomicile.json"))
                .when().post(VALIDATE_URL).then().statusCode(200)
                .and().body("data.deceasedDomicileInEngWales", equalToIgnoringCase("Yes"));
    }

    @Test
    public void validateHardStopForDomicile() {
        given().headers(utils.getHeadersWithUserId())
                .relaxedHTTPSValidation()
                .body(utils.getJsonFromFile("hardStop.deceasedDomicile.json"))
                .when().post(VALIDATE_URL).then().statusCode(200)
                .and().body("data.state", equalToIgnoringCase("Stopped"))
                .and().body("data.deceasedDomicileInEngWales", equalToIgnoringCase("No"));
    }

    @Test
    public void validateHardStopMessageForNoDomicile() {
        Response response = given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("hardStop.deceasedDomicile.json"))
                .when().post("/case/stopConfirmation");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("You can't currently use this service if the deceased lived outside England or Wales.\\n\\nFollow your existing process for applying for probate for this client.\\n"));
    }
}
