package uk.gov.hmcts.probate.functional.fee;

import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(SerenityJUnit5Extension.class)
public class SolCcdServiceFeeTests extends IntegrationTestBase {

    private static final int APP_FEE = 30000; //comment this out for local tests - keep for commits
    // private static final int APP_FEE = 15500;
    private static final int COPIES_FEE = 150;
    private static final double MAX_UK_COPIES = 50;
    private static final double MAX_NON_UK_COPIES = 50;

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void shouldIncludePA17Link() throws IOException {
        Response response = validatePostRequestSuccessForFee("solicitorValidateProbateExecutorsPA17.json",
            true, true);
        assertTrue(response.getBody().asPrettyString().contains("(PA17)"));
    }

    @Test
    void shouldTransformSolicitorExecutorFields() throws IOException {
        Response response = validatePostRequestSuccessForFee("solicitorValidateProbateExecutors.json", true,
            true);

        final JsonPath jsonPath = JsonPath.from(response.getBody().asPrettyString());

        final HashMap executorNotApplying = jsonPath.get("data.executorsNotApplying[0].value");
        assertEquals("Exfn Exln", executorNotApplying.get("notApplyingExecutorName"));
        assertEquals("DiedBefore", executorNotApplying.get("notApplyingExecutorReason"));
        assertEquals("alias name", executorNotApplying.get("notApplyingExecutorNameOnWill"));

        final HashMap executorApplying1 = jsonPath.get("data.executorsApplying[0].value");
        assertEquals("Exfn1 Exln1", executorApplying1.get("applyingExecutorName"));

        final HashMap executorApplying2 = jsonPath.get("data.executorsApplying[1].value");
        assertEquals("Exfn2 Exln2", executorApplying2.get("applyingExecutorName"));
        assertEquals("Alias name exfn2", executorApplying2.get("applyingExecutorOtherNames"));
        assertEquals("addressline 1", ((HashMap)executorApplying2.get("applyingExecutorAddress"))
            .get("AddressLine1"));
        assertEquals("addressline 2", ((HashMap)executorApplying2.get("applyingExecutorAddress"))
            .get("AddressLine2"));
        assertEquals("addressline 3", ((HashMap)executorApplying2.get("applyingExecutorAddress"))
            .get("AddressLine3"));
        assertEquals("posttown", ((HashMap)executorApplying2.get("applyingExecutorAddress")).get("PostTown"));
        assertEquals("postcode", ((HashMap)executorApplying2.get("applyingExecutorAddress")).get("PostCode"));
        assertEquals("country", ((HashMap)executorApplying2.get("applyingExecutorAddress")).get("Country"));
        assertEquals("county", ((HashMap)executorApplying2.get("applyingExecutorAddress")).get("County"));
    }

    @Test
    void verifyAllFeesAboveThreshold() throws IOException {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", true, true);
    }

    @Test
    void verifyAllFeesBelowThreshold() throws IOException {
        validatePostRequestSuccessForFee("success.feeNetValue1000.json", false, true);
    }

    @Test
    void shouldValidatePBAPaymentNoFees() throws IOException {
        validatePostRequestSuccessForFee("success.feeNetValue1000.json", false, false);
    }

    @Test
    void verifyIncorrectJsonReturns400() throws IOException {
        verifyIncorrectPostRequestReturns400("failure.fee.json", "Invalid Request");
    }

    @Test
    void verifyNegativeUKCopiesFeeReturns400() throws IOException {
        verifyIncorrectPostRequestReturns400("failure.negativeUKCopies.json", "Uk Grant copies cannot be negative");
    }

    @Test
    void verifyNegativeOverseasCopiesFeeReturns400() throws IOException {
        verifyIncorrectPostRequestReturns400("failure.negativeOverseasCopies.json", "Overseas Grant copies cannot be "
            + "negative");
    }

    private Response validatePostRequestSuccessForFee(String fileName, boolean hasApplication,
                                                      boolean hasCopies) throws IOException {
        int rndUkCopies = 0;
        int rndNonUkCopies = 0;
        int applicationFee = hasApplication ? APP_FEE : 0;
        if (hasCopies) {
            rndUkCopies = (int) ((Math.random() * MAX_UK_COPIES) + 1);
            rndNonUkCopies = (int) (Math.random() * MAX_NON_UK_COPIES) + 1;
        }
        Response response = getResponse(fileName, rndUkCopies, rndNonUkCopies, utils.getHeadersWithSolicitorUser());
        response.then().assertThat().statusCode(200);
        if (hasApplication) {
            response.then().assertThat().body("data.applicationFee", equalTo("" + applicationFee));
        }
        return response;
    }

    private Response getResponse(String fileName, int rndUkCopies, int rndNonUkCopies, Headers headers)
        throws IOException {
        String payload = utils.replaceAnyCaseNumberWithRandom(utils.getJsonFromFile(fileName));
        payload = payload.replaceAll("<UK_COPIES>", "" + rndUkCopies);
        payload = payload.replaceAll("<NON_UK_COPIES>", "" + rndNonUkCopies);

        return given().headers(headers)
            .relaxedHTTPSValidation()
            .body(payload)
            .contentType(JSON)
            .when().post("/nextsteps/validate");
    }

    private void verifyIncorrectPostRequestReturns400(String fileName, String errorMessage) throws IOException {
        given().headers(utils.getHeadersWithCaseworkerUser())
            .relaxedHTTPSValidation()
            .body(utils.replaceAnyCaseNumberWithRandom(utils.getJsonFromFile(fileName)))
            .when().post("/nextsteps/validate").then()
            .statusCode(400)
            .and().body(containsString(errorMessage));
    }

}
