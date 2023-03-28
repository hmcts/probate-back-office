package uk.gov.hmcts.probate.functional.payments;


import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServicePBAPaymentTests extends IntegrationTestBase {

    @Test
    public void shouldValidateForGrantPaymentCallback() throws IOException {
        //@TODO 500 here because we dont have a case that will exist in this state
        validatePutSuccess("solicitorPaymentCallbackPayload.json", "/payment/gor-payment-request-update", 500);
    }

    @Test
    public void shouldValidateForCaveatCallback() throws IOException {
        //@TODO 500 here because we dont have a case that will exist in this state
        validatePutSuccess("solicitorPaymentCallbackPayload.json", "/payment/caveat-payment-request-update", 500);
    }
}
