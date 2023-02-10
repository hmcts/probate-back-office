package uk.gov.hmcts.probate.functional.serviceauth;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

import static io.restassured.RestAssured.given;


@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceServiceAuthTests extends IntegrationTestBase {

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void verifyThatWithCorrectAuthorizationWeReceiveSuccess() throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithCaseworkerUser())
            .body(utils.getJsonFromFile("success.solicitorCorrectAuth.json"))
            .post("/nextsteps/validate")
            .then().assertThat().statusCode(200);
    }

    @Test
    public void verifyThatWithInCorrectAuthorizationWeReceive403() throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders("InvalidToken"))
            .body(utils.getJsonFromFile("success.solicitorCreate.json"))
            .post("/nextsteps/validate")
            .then().assertThat().statusCode(403);
    }

    @Test
    public void verifyThatWithEmptyAuthorizationHeaderWeReceive403() throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(""))
            .body(utils.getJsonFromFile("success.solicitorCreate.json"))
            .post("/nextsteps/validate")
            .then().assertThat().statusCode(403);
    }
}
