package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PBAPaymentConsumerSuccessTest extends BasePBAPaymentTest {

    @Autowired
    PaymentsService paymentsService;
    @MockBean
    AuthTokenGenerator authTokenGenerator;
    @MockBean
    BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Rule
    public PactHttpsProviderRuleMk2 mockProvider =
        new PactHttpsProviderRuleMk2("payment_creditAccountPayment", "localhost", 8886, this);

    @After
    public void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(provider = "payment_creditAccountPayment", consumer = "probate_backOffice")
    public RequestResponsePact generatePactFragmentSuccess(PactDslWithProvider builder) throws JSONException,
        IOException {

        Map<String, Object> paymentMap = new HashMap<>();
        paymentMap.put("accountNumber", "test.account");
        paymentMap.put("availableBalance", "1000.00");
        paymentMap.put("accountName", "test.account.name");

        return builder
            .given("An active account has sufficient funds for a payment", paymentMap)
            .uponReceiving("A request for a payment")
            .path("/credit-account-payments")
            .method("POST")
            .headers(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .body(objectMapper.writeValueAsString(getPaymentRequest(BigDecimal.TEN)))
            .willRespondWith()
            .status(201)
            .body(buildPBAPaymentResponseDsl("Success", "success", null, "Insufficient funds available"))
            .toPact();
    }

    @Test
    @PactVerification(fragment = "generatePactFragmentSuccess")
    public void verifyPBAPaymentPactSuccess() {
        PaymentResponse paymentResponse = paymentsService.getCreditAccountPaymentResponse(AUTH_TOKEN,
            getPaymentRequest(BigDecimal.TEN));
        assertEquals("reference", paymentResponse.getReference());
    }
}
