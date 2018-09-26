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
public class SolCcdServiceWillUpdateHardStopTests extends IntegrationTestBase {

    @Test
    public void validateWillUpdateWithoutHardStop() {
        given().relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("success.willUpdate.json"))
                .post("/case/validate").then().statusCode(200)
                .and().body("data.willExists", equalToIgnoringCase("Yes"))
                .and().body("data.willAccessOriginal", equalToIgnoringCase("Yes"));
    }

    @Test
    public void validateHardStopForWillUpdate() {
        given().relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("hardStop.noWillNotExists.json"))
                .post("/case/validate").then().statusCode(200)
                .and().body("data.state", equalToIgnoringCase("Stopped"))
                .and().body("data.willExists", equalToIgnoringCase("No"))
                .and().body("data.willAccessOriginal", equalToIgnoringCase("Yes"));
    }

    @Test
    public void validateHardStopWithNoWillAccessOriginal() {
        given().relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("hardStop.noWillAccessOriginal.json"))
                .post("/case/validate").then().statusCode(200)
                .and().body("data.state", equalToIgnoringCase("Stopped"))
                .and().body("data.willExists", equalToIgnoringCase("Yes"))
                .and().body("data.willAccessOriginal", equalToIgnoringCase("No"));
    }

    @Test
    public void validateHardStopWithNoWillExistsAndNoWillAccessOriginal() {
        given().relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("hardStop.noWillExists.noWillAccessOriginal.json"))
                .post("/case/validate").then().statusCode(200)
                .and().body("data.state", equalToIgnoringCase("Stopped"))
                .and().body("data.willExists", equalToIgnoringCase("No"))
                .and().body("data.willAccessOriginal", equalToIgnoringCase("No"));
    }

    @Test
    public void validateHardMessageWithNoWillExists() {
        Response response = given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("hardStop.noWillNotExists.json"))
                .post("/case/stopConfirmation");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("You can't currently use this service if the person who died did not leave a will.\\n\\nFollow your existing process for applying for probate for this client.\\n"));
    }

    @Test
    public void validateHardMessageWithNoOriginalWill() {
        Response response = given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("hardStop.noWillAccessOriginal.json"))
                .post("/case/stopConfirmation");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("You can't currently use this service if you do not have the original will.\\n\\nFollow your existing process for applying for probate for this client.\\n"));
    }

    @Test
    public void validateHardStopMessageWithNoOriginalWillAndNoWillExists() {
        Response response = given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("hardStop.noWillExists.noWillAccessOriginal.json"))
                .post("/case/stopConfirmation");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("You can't currently use this service if the person who died did not leave a will.\\n\\nFollow your existing process for applying for probate for this client.\\n") || response.getBody().asString().contains("You can't currently use this service if you do not have the original will.\\n\\nFollow your existing process for applying for probate for this client.\\n"));
    }
}
