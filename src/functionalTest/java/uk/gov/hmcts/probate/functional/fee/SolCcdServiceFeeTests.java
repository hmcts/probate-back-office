package uk.gov.hmcts.probate.functional.fee;


import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceFeeTests extends IntegrationTestBase {

    private final static String WIREMOCK_STUB_PAYMENT_RESPONSE_SUCCESS = "{\"reference\": \"RC-1590-6786-1063-9996\", \"date_created\": " +
        "\"2020-05-28T15:10:10.694+0000\", \"status\": \"Success\", \"payment_group_reference\": \"2020-1590678609071\", " +
        "\"status_histories\": [{\"status\": \"success\", \"date_created\": \"2020-05-28T15:10:10.700+0000\", \"date_updated\": " +
        "\"2020-05-28T15:10:10.700+0000\"}]}";
    private static WireMockServer wireMockServer;
    
    @BeforeClass
    public static void setup() {
        wireMockServer = new WireMockServer(options().port(8991));
        wireMockServer.start();
        wireMockServer.resetRequests();
        stubCreditAccountPayment(WIREMOCK_STUB_PAYMENT_RESPONSE_SUCCESS);
    }

    @AfterClass
    public static void cleanup() {
        wireMockServer.stop();
    }

    @Before
    public void setupPerTest() {
    }

    @Test
    public void verifyNetValue10000() {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", "applicationFee", "15500");
    }

    @Test
    public void verifyFeeForUkCopies() {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", "feeForUkCopies", "150");
    }

    @Test
    public void verifyFeeForNonUkCopies() {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", "feeForNonUkCopies", "150");
    }

    @Test
    public void verifyTotal() {
        validatePostRequestSuccessForFee("success.feeNetValue10000.json", "totalFee", "15800");
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

    private static void stubCreditAccountPayment(String response) {
        wireMockServer.stubFor(post(urlEqualTo("/credit-account-payments"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(response)));
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
