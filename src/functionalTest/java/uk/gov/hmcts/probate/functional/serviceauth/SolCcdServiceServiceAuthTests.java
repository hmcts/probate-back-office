package uk.gov.hmcts.probate.functional.serviceauth;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

import static io.restassured.RestAssured.given;


@ExtendWith(SerenityJUnit5Extension.class)
public class SolCcdServiceServiceAuthTests extends IntegrationTestBase {

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void verifyThatWithCorrectAuthorizationWeReceiveSuccess() throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithCaseworkerUser())
            .body(utils.getJsonFromFile("success.solicitorCorrectAuth.json"))
            .post("/nextsteps/validate")
            .then().assertThat().statusCode(200);
    }

    @Test
    void verifyThatWithInCorrectAuthorizationWeReceive403() throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersForUnauthorisedService())
            .body(utils.getJsonFromFile("success.solicitorCreate.json"))
            .post("/nextsteps/validate")
            .then().assertThat().statusCode(403);
    }

    @Test
    void verifyThatWithEmptyAuthorizationHeaderWeReceive403() throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersForUnauthorisedService())
            .body(utils.getJsonFromFile("success.solicitorCreate.json"))
            .post("/nextsteps/validate")
            .then().assertThat().statusCode(403);
    }
}
