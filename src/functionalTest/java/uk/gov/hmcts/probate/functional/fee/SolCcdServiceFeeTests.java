package uk.gov.hmcts.probate.functional.fee;


import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceFeeTests extends IntegrationTestBase {

    @Test
    public void verifyNetValue10000() {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", "applicationFee", "15500");
    }

    @Test
    public void verifyFeeForUkCopies() {
        validatePostRequestSuccessForFee("success.feeForUKCopies.json", "feeForUkCopies", "150");
    }

    @Test
    public void verifyFeeForNonUkCopies() {
        validatePostRequestSuccessForFee("success.feeForNonUKCopies.json", "feeForNonUkCopies", "150");
    }

    @Test
    public void verifyTotal() {
        validatePostRequestSuccessForFee("success.feeTotal.json", "totalFee", "15800");
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

        given().headers(utils.getHeadersWithCaseworkerUser())
            .relaxedHTTPSValidation()
            .body(utils.getJsonFromFile(fileName))
            .contentType(JSON)
            .when().post("/nextsteps/validate")
            .then().assertThat()
            .statusCode(200)
            .and().body("data." + param, equalToIgnoringCase(expectedValue));
    }

    private void verifyIncorrectPostRequestReturns400(String fileName, String errorMessage) {
        given().headers(utils.getHeadersWithCaseworkerUser())
            .relaxedHTTPSValidation()
            .body(utils.getJsonFromFile(fileName))
            .when().post("/nextsteps/validate").then()
            .statusCode(400)
            .and().body(containsString(errorMessage));
    }
}
