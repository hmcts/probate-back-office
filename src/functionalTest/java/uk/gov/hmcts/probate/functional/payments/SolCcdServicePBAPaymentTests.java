package uk.gov.hmcts.probate.functional.payments;


import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

@Slf4j
@ExtendWith(SerenityJUnit5Extension.class)
public class SolCcdServicePBAPaymentTests extends IntegrationTestBase {

    @Test
    void shouldValidateForGrantPaymentCallback() throws IOException {
        //@TODO 500 here because we dont have a case that will exist in this state
        validatePutSuccess("solicitorPaymentCallbackPayload.json", "/payment/gor-payment-request-update", 403);
    }

    @Test
    void shouldValidateForCaveatCallback() throws IOException {
        //@TODO 500 here because we dont have a case that will exist in this state
        validatePutSuccess("solicitorPaymentCallbackPayload.json", "/payment/caveat-payment-request-update", 403);
    }
}
