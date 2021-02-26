package uk.gov.hmcts.probate.functional.payments;


import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.functional.util.FunctionalTestUtils;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServicePBAPaymentTests extends IntegrationTestBase {

    @Autowired
    protected FunctionalTestUtils utils;

    @Test
    public void shouldValidateDefaultPBAs() {
        validatePostRequestSuccessForPBAs("/case/default-sols-pba", "solicitorPDFPayloadProbate.json",
            "{\"code\":\"PBA0082126\",\"label\":\"PBA0082126\"},{\"code\":\"PBA0083372\",\"label\":\"PBA0083372\"},"
                + "{\"code\":\"PBA0083374\",\"label\":\"PBA0083374\"}");
    }

    @Test
    public void shouldValidateDefaultPBAPayments() {
        validatePostRequestSuccessForPBAs("/case/default-sols-pba", "solicitorPDFPayloadProbate.json",
            "\"solsNeedsPBAPayment\":\"Yes\"");
    }

    @Test
    public void shouldValidatePaymentAountOnHold() {
        validatePostRequestSuccessForPBAs("/nextsteps/validate",
            "solicitorPDFPayloadProbateAccountOnHold.json",
            "Your account is on hold");
    }

    @Test
    public void shouldValidatePaymentAccountDeleted() {
        validatePostRequestSuccessForPBAs("/nextsteps/validate",
            "solicitorPDFPayloadProbateAccountDeleted.json",
            "Your account is deleted");
    }

    @Test
    public void shouldValidatePaymentInsufficientFunds() {
        validatePostRequestSuccessForPBAs("/nextsteps/validate",
            "solicitorPDFPayloadProbateCopiesForInsufficientFunds.json",
            "Your account has insufficient funds");
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
