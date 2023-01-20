package uk.gov.hmcts.probate.functional.fee;

import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.Pending;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceFeeTests extends IntegrationTestBase {

    private static final int APP_FEE = 27300; //comment this out for local tests - keep for commits
    // private static final int APP_FEE = 15500;
    private static final int COPIES_FEE = 150;
    private static final double MAX_UK_COPIES = 50;
    private static final double MAX_NON_UK_COPIES = 50;

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void shouldIncludePA17Link() throws IOException {
        Response response = validatePostRequestSuccessForFee("solicitorValidateProbateExecutorsPA17.json",
            true, true, true);
        System.out.println("TEST LOGGING BEGIN");
        System.out.println(response);
        System.out.println("TEST LOGGING END");
        assertTrue(response.getBody().asPrettyString().contains("(PA17)"));
    }

    @Test
    public void shouldTransformSolicitorExecutorFields() throws IOException {
        Response response = validatePostRequestSuccessForFee("solicitorValidateProbateExecutors.json", true, true,
            true);

        final JsonPath jsonPath = JsonPath.from(response.getBody().asPrettyString());

        final HashMap executorNotApplying = jsonPath.get("data.executorsNotApplying[0].value");
        Assert.assertEquals("Exfn Exln", executorNotApplying.get("notApplyingExecutorName"));
        Assert.assertEquals("DiedBefore", executorNotApplying.get("notApplyingExecutorReason"));
        Assert.assertEquals("alias name", executorNotApplying.get("notApplyingExecutorNameOnWill"));

        final HashMap executorApplying1 = jsonPath.get("data.executorsApplying[0].value");
        Assert.assertEquals("Exfn1 Exln1", executorApplying1.get("applyingExecutorName"));

        final HashMap executorApplying2 = jsonPath.get("data.executorsApplying[1].value");
        Assert.assertEquals("Exfn2 Exln2", executorApplying2.get("applyingExecutorName"));
        Assert.assertEquals("Alias name exfn2", executorApplying2.get("applyingExecutorOtherNames"));
        Assert.assertEquals("addressline 1", ((HashMap)executorApplying2.get("applyingExecutorAddress"))
            .get("AddressLine1"));
        Assert.assertEquals("addressline 2", ((HashMap)executorApplying2.get("applyingExecutorAddress"))
            .get("AddressLine2"));
        Assert.assertEquals("addressline 3", ((HashMap)executorApplying2.get("applyingExecutorAddress"))
            .get("AddressLine3"));
        Assert.assertEquals("posttown", ((HashMap)executorApplying2.get("applyingExecutorAddress")).get("PostTown"));
        Assert.assertEquals("postcode", ((HashMap)executorApplying2.get("applyingExecutorAddress")).get("PostCode"));
        Assert.assertEquals("country", ((HashMap)executorApplying2.get("applyingExecutorAddress")).get("Country"));
        Assert.assertEquals("county", ((HashMap)executorApplying2.get("applyingExecutorAddress")).get("County"));
    }

    @Test
    public void verifyAllFeesAboveThreshold() throws IOException {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", true, true, true);
    }

    @Test
    public void verifyAllFeesBelowThreshold() throws IOException {
        validatePostRequestSuccessForFee("success.feeNetValue1000.json", false, true, true);
    }

    @Test
    public void shouldValidatePBAPaymentNoFees() throws IOException {
        validatePostRequestSuccessForFee("success.feeNetValue1000.json", false, false, false);
    }

    @Test
    public void shouldValidatePaymentAccountDeleted() throws IOException {
        validatePostRequestFailureForPBAs(1, "solicitorPDFPayloadProbateAccountDeleted.json",
            "Your account is deleted");
    }

    @Test
    public void shouldValidatePaymentInsufficientFunds() throws IOException {
        validatePostRequestFailureForPBAs(1000, "solicitorPDFPayloadProbateCopiesForInsufficientFunds.json",
            "have insufficient funds available");
    }

    @Pending
    @Test
    public void shouldValidatePaymentAountOnHold() throws IOException {
        //this test cannot be automated on a deployed env - leaving it for local checking
        validatePostRequestFailureForPBAsForSolicitor2(1, "solicitorPDFPayloadProbateAccountOnHold.json",
            "Your account is on hold");
    }

    @Test
    public void verifyIncorrectJsonReturns400() throws IOException {
        verifyIncorrectPostRequestReturns400("failure.fee.json", "Invalid Request");
    }

    @Test
    public void verifyEmptyApplicationFeeReturns400() throws IOException {
        verifyIncorrectPostRequestReturns400("failure.fee.emptyNetIHT.json", "Net IHT value cannot be empty");
    }

    @Test
    public void verifyNegativeUKCopiesFeeReturns400() throws IOException {
        verifyIncorrectPostRequestReturns400("failure.negativeUKCopies.json", "Uk Grant copies cannot be negative");
    }

    @Test
    public void verifyNegativeOverseasCopiesFeeReturns400() throws IOException {
        verifyIncorrectPostRequestReturns400("failure.negativeOverseasCopies.json", "Overseas Grant copies cannot be "
            + "negative");
    }

    private Response validatePostRequestSuccessForFee(String fileName, boolean hasApplication, boolean hasFees,
                                                      boolean hasPayments) throws IOException {
        int rndUkCopies = (int) (Math.random() * MAX_UK_COPIES) + 1;
        int rndNonUkCopies = (int) (Math.random() * MAX_NON_UK_COPIES) + 1;
        int applicationFee = hasApplication ? APP_FEE : 0;
        int ukFee = rndUkCopies * COPIES_FEE;
        int nonUkFee = rndNonUkCopies * COPIES_FEE;
        int totalFee = applicationFee + ukFee + nonUkFee;

        Response response = getResponse(fileName, rndUkCopies, rndNonUkCopies, utils.getHeadersWithSolicitorUser());

        response.then().assertThat().statusCode(200);
        if (hasApplication) {
            response.then().assertThat().body("data.applicationFee", equalTo("" + applicationFee));
        }
        if (hasFees) {
            response.then().assertThat().body("data.feeForUkCopies", equalTo("" + ukFee));
            response.then().assertThat().body("data.feeForNonUkCopies", equalTo("" + nonUkFee));
            response.then().assertThat().body("data.totalFee", equalTo("" + totalFee));
        }
        if (hasPayments) {
            response.then().assertThat().body("data.payments[0].value.status", equalTo("Success"));
            response.then().assertThat().body("data.payments[0].value.reference", containsString("RC-"));
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

    private Response validatePostRequestFailureForPBAs(int copiesMultiplier, String fileName,
                                                       String... expectedValues) throws IOException {

        int rndUkCopies = (int) ((Math.random() * MAX_UK_COPIES) * copiesMultiplier) + 1;
        int rndNonUkCopies = (int) ((Math.random() * MAX_NON_UK_COPIES) * copiesMultiplier) + 1;
        Response response = getResponse(fileName, rndUkCopies, rndNonUkCopies, utils.getHeadersWithSolicitorUser());
        for (String expectedValue : expectedValues) {
            assertThat(response.getBody().asString(), CoreMatchers.containsString(expectedValue));
        }
        return response;
    }

    private void validatePostRequestFailureForPBAsForSolicitor2(int copiesMultiplier, String fileName,
                                                                String... expectedValues) throws IOException {
        int rndUkCopies = (int) ((Math.random() * MAX_UK_COPIES) * copiesMultiplier) + 1;
        int rndNonUkCopies = (int) ((Math.random() * MAX_NON_UK_COPIES) * copiesMultiplier) + 1;
        Response response = getResponse(fileName, rndUkCopies, rndNonUkCopies, utils.getHeadersWithSolicitor2User());

        for (String expectedValue : expectedValues) {
            assertThat(response.getBody().asString(), CoreMatchers.containsString(expectedValue));
        }
    }

}
