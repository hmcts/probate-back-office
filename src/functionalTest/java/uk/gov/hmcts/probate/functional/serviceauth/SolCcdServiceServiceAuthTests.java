package uk.gov.hmcts.probate.functional.serviceauth;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static io.restassured.RestAssured.given;


@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceServiceAuthTests extends IntegrationTestBase {

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void verifyThatWithCorrectAuthorizationWeReceiveSuccess() {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders())
            .body(utils.getJsonFromFile("success.solicitorCreate.json"))
            .post("/nextsteps/validate")
            .then().assertThat().statusCode(200);
    }

    @Test
    public void verifyThatWithInCorrectAuthorizationWeReceive403() {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders("InvalidToken"))
            .body(utils.getJsonFromFile("success.solicitorCreate.json"))
            .post("/nextsteps/validate")
            .then().assertThat().statusCode(403);
    }

    @Test
    public void verifyThatWithEmptyAuthorizationHeaderWeReceive403() {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(""))
            .body(utils.getJsonFromFile("success.solicitorCreate.json"))
            .post("/nextsteps/validate")
            .then().assertThat().statusCode(403);
    }
}
