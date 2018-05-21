package uk.gov.hmcts.probate.functional.serviceauth;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static io.restassured.RestAssured.given;

@RunWith(SerenityRunner.class)
public class SolCcdServiceServiceAuthTests extends IntegrationTestBase {

    @Test
    public void verifyThatWithCorrectAuthorizationWeReceiveSuccess() {
        given().headers(utils.getHeaders())
                .body(utils.getJsonFromFile("success.solicitorCreate.json"))
                .post("/nextsteps/validate")
                .then().assertThat().statusCode(200);
    }

    @Test
    public void verifyThatWithInCorrectAuthorizationWeReceive403() {
        given().headers(utils.getHeaders("InvalidToken"))
                .body(utils.getJsonFromFile("success.solicitorCreate.json"))
                .post("/validate/addDeceasedDetails")
                .then().assertThat().statusCode(403);
    }

    @Test
    public void verifyThatWithEmptyAuthorizationHeaderWeReceive403() {
        given().headers(utils.getHeaders(""))
                .body(utils.getJsonFromFile("success.solicitorCreate.json"))
                .post("/validate/addDeceasedDetails")
                .then().assertThat().statusCode(403);
    }
}
