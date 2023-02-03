package uk.gov.hmcts.probate.functional.payments;


import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServicePBAPaymentTests extends IntegrationTestBase {

    @Test
    public void shouldValidateForGrantPaymentCallback() throws IOException {
        //500 here because we dont have a case that will exist in this state
        validatePutSuccess("solicitorPaymentCallbackPayload.json", "/payment/gor-payment-request-update", 500);
    }

    @Test
    public void shouldValidateForCaveatCallback() throws IOException {
        validatePutSuccess("solicitorPaymentCallbackPayload.json", "/payment/caveat-payment-request-update", 500);
    }

    private String validatePostRequestSuccessForPBAs(String path, String fileName, String... expectedValues)
        throws IOException {

        String body = given().headers(utils.getHeadersWithSolicitorUser())
            .relaxedHTTPSValidation()
            .body(utils.getJsonFromFile(fileName))
            .contentType(JSON)
            .when().post(path).getBody().asString();
        for (String expectedValue : expectedValues) {
            assertThat(body, containsString(expectedValue));
        }
        return body;
    }

}
