package uk.gov.hmcts.probate.functional.payments;


import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static junit.framework.TestCase.assertFalse;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServicePBAPaymentTests extends IntegrationTestBase {

    @Test
    public void shouldValidateDefaultPBAs() throws IOException {
        validatePostRequestSuccessForPBAs("/case/default-sols-pba",
            "solicitorPDFPayloadProbate.json",
            "{\"code\":\"PBA0083372\",\"label\":\"PBA0083372\"}",
            "{\"code\":\"PBA0082126\",\"label\":\"PBA0082126\"}",
            "\"solsNeedsPBAPayment\":\"Yes\"");
    }

    @Test
    public void shouldValidateDefaultPBAPaymentsNoFee() throws IOException {
        String responseBody = validatePostRequestSuccessForPBAs("/case/default-sols-pba",
            "solicitorPDFPayloadProbateNoPaymentFee.json",
            "\"solsNeedsPBAPayment\":\"No\"");
        assertFalse(responseBody.contains("\"payments\": ["));
    }

    private String validatePostRequestSuccessForPBAs(String path, String fileName, String... expectedValues)
        throws IOException {

        String body = given().headers(utils.getHeadersWithSolicitorUser())
            .relaxedHTTPSValidation()
            .body(utils.getJsonFromFile(fileName))
            .contentType(JSON)
            .when().post(path).getBody().asPrettyString();
        for (String expectedValue : expectedValues) {
            JSONAssert.assertEquals(expectedValue, body, JSONCompareMode.STRICT);
        }
        return body;
    }

}
