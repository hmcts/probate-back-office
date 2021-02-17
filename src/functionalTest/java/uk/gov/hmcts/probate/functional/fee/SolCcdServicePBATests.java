package uk.gov.hmcts.probate.functional.fee;


import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.functional.util.FunctionalTestUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServicePBATests extends IntegrationTestBase {

    private static WireMockServer wireMockServer;

    @Autowired
    protected FunctionalTestUtils utils;


    @BeforeClass
    public static void setup() {
        wireMockServer = new WireMockServer(options().port(8991));
        wireMockServer.start();
        wireMockServer.resetRequests();
    }

    @AfterClass
    public static void cleanup() {
        wireMockServer.stop();
    }

    @Before
    public void setupPerTest() {
        stubCreditAccountPayment(utils.getJsonFromFile("pbaWiremockResponses.json"));
    }

    @Test
    public void shouldValidateDefaultPBAs() {
        validatePostRequestSuccessForPBAs("/case/default-sols-pba", "solicitorPDFPayloadProbate.json",
            "{\"code\":\"PBA0022222\",\"label\":\"PBA0022222\"},{\"code\":\"PBA0011111\",\"label\":\"PBA0011111\"}");
    }

    @Test
    public void shouldValidateDefaultPBAPayments() {
        validatePostRequestSuccessForPBAs("/case/default-sols-payment", "solicitorPDFPayloadProbate.json",
            "\"solsNeedsPBAPayment\": \"Yes\"");
    }

    private static void stubCreditAccountPayment(String response) {
        wireMockServer.stubFor(get(urlMatching("\\/(refdata\\/external\\/v1\\/organisations\\/pbas\\?email=.+)"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(response)));
    }

    private void validatePostRequestSuccessForPBAs(String path, String fileName, String expectedValue) {

        String body = given().headers(utils.getHeadersWithSolicitorUser())
            .relaxedHTTPSValidation()
            .body(utils.getJsonFromFile(fileName))
            .contentType(JSON)
            .when().post(path).getBody().asString();
        assertThat(body, containsString(expectedValue));
    }
}
