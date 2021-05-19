package uk.gov.hmcts.probate.functional.hardstops;


import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceWillUpdateHardStopTests extends IntegrationTestBase {

    public static final String VALIDATE_PROBATE_URL = "/case/sols-validate-probate";
    public static final String VALIDATE_INTESTACY_URL = "/case/sols-validate-intestacy";
    public static final String VALIDATE_ADMON_URL = "/case/sols-validate-admon";
    public static final String VALIDATE_URL = "/case/sols-validate";
    private static final String CASE_STOP_CONFIRMATION = "/case/stopConfirmation";

    @Test
    public void validateWillUpdateProbateWithoutHardStop() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("success.willUpdate.json"))
            .post(VALIDATE_URL).then().statusCode(200)
            .and().body("data.state", equalToIgnoringCase("SolProbateCreated"))
            .and().body("data.willExists", equalToIgnoringCase("Yes"))
            .and().body("data.willAccessOriginal", equalToIgnoringCase("Yes"));
    }

    @Test
    public void validateWillUpdateIntestacyWithoutHardStop() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("success.willUpdateIntestacy.json"))
            .post(VALIDATE_URL).then().statusCode(200)
            .and().body("data.state", equalToIgnoringCase("SolIntestacyCreated"))
            .and().body("data.willExists", equalToIgnoringCase("No"))
            .and().body("data.willAccessOriginal", equalToIgnoringCase("Yes"));
    }

    @Test
    public void validateWillUpdateAnnexedWithoutHardStop() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("success.willUpdateAdmon.json"))
            .post(VALIDATE_URL).then().statusCode(200)
            .and().body("data.state", equalToIgnoringCase("SolAdmonCreated"))
            .and().body("data.willExists", equalToIgnoringCase("Yes"))
            .and().body("data.willAccessOriginal", equalToIgnoringCase("Yes"));
    }

    @Test
    public void validateNoHardStopWithNoWillAccessOriginalProbate() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("noHardStop.noWillAccessOriginalProbate.json"))
            .post(VALIDATE_PROBATE_URL).then().statusCode(200)
            .and().body("data.state", equalToIgnoringCase("SolDraftCase"))
            .and().body("data.willExists", equalToIgnoringCase("Yes"))
            .and().body("data.willAccessOriginal", equalToIgnoringCase("No"));
    }

    @Test
    public void validateNoHardStopWithNoWillAccessOriginalAdmon() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("noHardStop.noWillAccessOriginalAdmon.json"))
            .post(VALIDATE_ADMON_URL).then().statusCode(200)
            .and().body("data.state", equalToIgnoringCase("SolDraftCase"))
            .and().body("data.willExists", equalToIgnoringCase("Yes"))
            .and().body("data.willAccessOriginal", equalToIgnoringCase("No"));
    }

    // We no longer stop these
    @Test
    public void validateNoHardMessageWithNoOriginalWill() {
        Response response = given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("hardStop.noWillAccessOriginalProbate.json"))
            .post(CASE_STOP_CONFIRMATION);
        assertEquals(200, response.getStatusCode());
        assertFalse(response.getBody().asString().contains(
            "You can't currently use this service if you do not have the original will.\\n\\nFollow your existing "
                + "process for applying for probate for this client.\\n"));
    }
}
