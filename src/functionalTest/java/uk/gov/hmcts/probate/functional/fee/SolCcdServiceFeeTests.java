package uk.gov.hmcts.probate.functional.fee;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceFeeTests extends IntegrationTestBase {

    private static final int APP_FEE = 27300; //15500
    private static final int COPIES_FEE = 150;
    private static final double MAX_UK_COPIES = 50;
    private static final double MAX_NON_UK_COPIES = 50;

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void verifyAllFeesAboveThreshold() {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", true);
    }

    @Test
    public void verifyAllFeesBelowThreshold() {
        validatePostRequestSuccessForFee("success.feeNetValue1000.json", false);
    }

    @Test
    public void verifyIncorrectJsonReturns400() {
        verifyIncorrectPostRequestReturns400("failure.fee.json", "Invalid Request");
    }

    @Test
    public void verifyEmptyApplicationFeeReturns400() {
        verifyIncorrectPostRequestReturns400("failure.fee.emptyNetIHT.json", "Net IHT value cannot be empty");
    }

    @Test
    public void verifyNegativeUKCopiesFeeReturns400() {
        verifyIncorrectPostRequestReturns400("failure.negativeUKCopies.json", "Uk Grant copies cannot be negative");
    }

    @Test
    public void verifyNegativeOverseasCopiesFeeReturns400() {
        verifyIncorrectPostRequestReturns400("failure.negativeOverseasCopies.json", "Overseas Grant copies cannot be "
            + "negative");
    }

    private void validatePostRequestSuccessForFee(String fileName, boolean hasApplication) {
        int rndUkCopies = (int) (Math.random() * MAX_UK_COPIES) + 1;
        int rndNonUkCopies = (int) (Math.random() * MAX_NON_UK_COPIES) + 1;
        int applicationFee = hasApplication ? APP_FEE : 0;
        int ukFee = rndUkCopies * COPIES_FEE;
        int nonUkFee = rndNonUkCopies * COPIES_FEE;
        int totalFee = applicationFee + ukFee + nonUkFee;
        String payload = utils.replaceAnyCaseNumberWithRandom(utils.getJsonFromFile(fileName));
        payload = payload.replaceAll("<UK_COPIES>", "" + rndUkCopies);
        payload = payload.replaceAll("<NON_UK_COPIES>", "" + rndNonUkCopies);
        given().headers(utils.getHeadersWithCaseworkerUser())
            .relaxedHTTPSValidation()
            .body(payload)
            .contentType(JSON)
            .when().post("/nextsteps/validate")
            .then().assertThat()
            .statusCode(200)
            .and().body("data.applicationFee", equalTo("" + applicationFee))
            .and().body("data.feeForUkCopies", equalTo("" + ukFee))
            .and().body("data.feeForNonUkCopies", equalTo("" + nonUkFee))
            .and().body("data.totalFee", equalTo("" + totalFee));
        
        
    }

    private void verifyIncorrectPostRequestReturns400(String fileName, String errorMessage) {
        given().headers(utils.getHeadersWithCaseworkerUser())
            .relaxedHTTPSValidation()
            .body(utils.replaceAnyCaseNumberWithRandom(utils.getJsonFromFile(fileName)))
            .when().post("/nextsteps/validate").then()
            .statusCode(400)
            .and().body(containsString(errorMessage));
    }
}
