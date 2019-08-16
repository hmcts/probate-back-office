package uk.gov.hmcts.probate.functional.fee;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static io.restassured.http.ContentType.JSON;
import static net.serenitybdd.rest.SerenityRest.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalToIgnoringCase;


@RunWith(SerenityRunner.class)
public class SolCcdServiceFeeTests extends IntegrationTestBase {

    @Test
    public void verifyNetValue10000() {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", "applicationFee", "15500");
    }

    @Test
    public void verifyFeeForUkCopies() {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", "feeForUkCopies", "50");
    }

    @Test
    public void verifyFeeForNonUkCopies() {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", "feeForNonUkCopies", "50");
    }

    @Test
    public void verifyTotal() {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", "totalFee", "15600");
    }

    @Test
    public void verifyNetValue5000() {
        validatePostRequestSuccessForFee("success.feeNetValue5000.json", "applicationFee", "0");
    }

    @Test
    public void verifyNetValue1000() {
        validatePostRequestSuccessForFee("success.feeNetValue1000.json", "applicationFee", "0");
    }

    @Test
    public void verifyIncorrectJsonReturns400() {
        verifyIncorrectPostRequestReturns400("failure.fee.json", "Invalid Request");
    }

    @Test
    public void verifyEmptyApplicationFeeReturns400() {
        verifyIncorrectPostRequestReturns400("failure.fee.emptyNetIHT.json", "Net IHT value cannot be empty");
    }

    private void validatePostRequestSuccessForFee(String fileName, String param, String expectedValue) {
        given().headers(utils.getHeaders())
                .relaxedHTTPSValidation()
                .body(utils.getJsonFromFile(fileName))
                .contentType(JSON)
                .when().post("/nextsteps/validate")
                .then().assertThat()
                .statusCode(200)
                .and().body("data." + param, equalToIgnoringCase(expectedValue));
    }

    private void verifyIncorrectPostRequestReturns400(String fileName, String errorMessage) {
        given().headers(utils.getHeaders())
                .relaxedHTTPSValidation()
                .body(utils.getJsonFromFile(fileName))
                .when().post("/nextsteps/validate").then()
                .statusCode(400)
                .and().body(containsString(errorMessage));
    }
}
