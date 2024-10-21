package uk.gov.hmcts.probate.functional.hardstops;

import io.restassured.response.Response;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SerenityJUnit5Extension.class)
public class SolCcdServiceExecutorHardStopTests extends IntegrationTestBase {

    public static final String VALIDATE_URL = "/case/sols-validate-probate";

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void validateDeceasedDetailWithoutDomicileHardStop() throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("success.executorWithoutHardStop.json"))
            .when().post(VALIDATE_URL).then().statusCode(200);
    }

    @Test
    void validateHardStopForDomicile() throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("hardStop.executor.json"))
            .when().post(VALIDATE_URL).then().statusCode(200)
            .and().body("data.state", equalToIgnoringCase("Stopped"))
            .and().body("data.otherExecutorExists", equalToIgnoringCase("No"))
            .and().body("data.primaryApplicantIsApplying", equalToIgnoringCase("No"));
    }

    @Test
    void validateHardStopMessageForNoDomicile() throws IOException {
        final Response response = given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("hardStop.executor.json"))
            .when().post("/case/stopConfirmation");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains(
            "You can't currently use this service if the primary executor is not applying and there are no other "
                    + "executors applying."));
    }
}
