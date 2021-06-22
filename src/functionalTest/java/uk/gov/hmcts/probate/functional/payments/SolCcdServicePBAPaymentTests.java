package uk.gov.hmcts.probate.functional.payments;


import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.Pending;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.functional.util.FunctionalTestUtils;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServicePBAPaymentTests extends IntegrationTestBase {

    @Autowired
    protected FunctionalTestUtils utils;

    @Test
    public void shouldValidateDefaultPBAs() {
        validatePostRequestSuccessForPBAs("/case/default-sols-pba",
                "solicitorPDFPayloadProbate.json",
            "{\"code\":\"PBA0083372\",\"label\":\"PBA0083372\"}", 
            "{\"code\":\"PBA0082126\",\"label\":\"PBA0082126\"}",
            "\"solsNeedsPBAPayment\":\"Yes\"");
    }

    @Test
    public void shouldValidateDefaultPBAPaymentsNoFee() {
        String responseBody = validatePostRequestSuccessForPBAs("/case/default-sols-pba", 
            "solicitorPDFPayloadProbateNoPaymentFee.json",
            "\"solsNeedsPBAPayment\":\"No\"");
        assertFalse(responseBody.contains("\"payments\": ["));
    }

    // currently failing - come back to it at some point
    // failing in FeeService, getApplicationFeeResponse, restTemplate.getForEntity(uri, FeeResponse.class)
    @Test
    @Ignore
    public void shouldValidatePBAPayment() {
        validatePostRequestSuccessForPBAs("/nextsteps/validate", "solicitorPDFPayloadProbateAccountSuccess.json",
            "\"payments\":[", "\"reference\":\"RC-", "\"method\":\"pba\"");
    }

    @Test
    public void shouldValidatePBAPaymentNoFees() {
        String responseBody = validatePostRequestSuccessForPBAs("/nextsteps/validate",
            "solicitorPDFPayloadProbateAccountSuccessNoFees.json");
        assertFalse(responseBody.contains("\"payments\":["));
    }

    @Pending
    @Test
    public void shouldValidatePaymentAountOnHold() {
        //this test cannot be automated on a deployed env - leaving it for local checking
        validatePostRequestSuccessForPBAsForSolicitor2("/nextsteps/validate",
            "solicitorPDFPayloadProbateAccountOnHold.json",
            "Your account is on hold");
    }

    // currently failing - come back to it at some point
    // failing in FeeService, getApplicationFeeResponse, restTemplate.getForEntity(uri, FeeResponse.class)
    @Test
    @Ignore
    public void shouldValidatePaymentAccountDeleted() {
        validatePostRequestSuccessForPBAs("/nextsteps/validate",
            "solicitorPDFPayloadProbateAccountDeleted.json",
            "Your account is deleted");
    }

    // currently failing - come back to it at some point
    // failing in FeeService, getApplicationFeeResponse, restTemplate.getForEntity(uri, FeeResponse.class)
    @Test
    @Ignore
    public void shouldValidatePaymentInsufficientFunds() {
        validatePostRequestSuccessForPBAs("/nextsteps/validate",
            "solicitorPDFPayloadProbateCopiesForInsufficientFunds.json",
            "have insufficient funds available");
    }

    private String validatePostRequestSuccessForPBAs(String path, String fileName, String... expectedValues) {

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
    
    private void validatePostRequestSuccessForPBAsForSolicitor2(String path, String fileName,
                                                                String... expectedValues) {

        String body = given().headers(utils.getHeadersWithSolicitor2User())
            .relaxedHTTPSValidation()
            .body(utils.getJsonFromFile(fileName))
            .contentType(JSON)
            .when().post(path).getBody().asString();
        for (String expectedValue : expectedValues) {
            assertThat(body, containsString(expectedValue));
        }
    }
}
