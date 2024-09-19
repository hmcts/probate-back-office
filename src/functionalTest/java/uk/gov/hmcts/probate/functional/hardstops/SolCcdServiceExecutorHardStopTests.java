package uk.gov.hmcts.probate.functional.hardstops;

import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceExecutorHardStopTests extends IntegrationTestBase {

    public static final String VALIDATE_URL = "/case/sols-validate-probate";

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void validateDeceasedDetailWithoutDomicileHardStop() throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("success.executorWithoutHardStop.json"))
            .when().post(VALIDATE_URL).then().statusCode(200);
    }

    @Test
    public void validateHardStopForDomicile() throws IOException {
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
    public void validateHardStopMessageForNoDomicile() throws IOException {
        final Response response = given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("hardStop.executor.json"))
            .when().post("/case/stopConfirmation");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains(
            "You can't currently use this service if the primary executor is not applying and there are no other "
                    + "executors applying.\\n\\nNi allwch ddefnyddio'r gwasanaeth hwn ar hyn o bryd os nad yw'r prif "
                    + "ysgutor yn gwneud cais ac nad oes ysgutorion eraill yn gwneud cais. \\n\\nFollow your existing "
                    + "process for applying for probate for this client.\\n\\nDilynwch eich proses bresennol i wneud "
                    + "cais am brofiant ar gyfer y cleient hwn.\\n"));
    }
}
