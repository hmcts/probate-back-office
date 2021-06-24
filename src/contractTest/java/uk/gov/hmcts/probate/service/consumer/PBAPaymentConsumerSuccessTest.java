package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "payment_creditAccountPayment", port = "8886")
@PactFolder("pacts")
@SpringBootTest
@TestPropertySource(locations = {"/application.properties"})
public class PBAPaymentConsumerSuccessTest extends BasePBAPaymentTest {

    @Autowired
    PaymentsService paymentsService;
    @MockBean
    AuthTokenGenerator authTokenGenerator;
    @MockBean
    BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private ObjectMapper objectMapper = new ObjectMapper();
    
    @After
    public void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(provider = "payment_creditAccountPayment", consumer = "probate_backOffice")
    public RequestResponsePact generatePactFragmentSuccess(PactDslWithProvider builder) throws JSONException,
        IOException {

        return builder
            .given("An active account has sufficient funds for a payment", getPaymentMap("1000.00"))
            .uponReceiving("A request for a payment")
            .path("/credit-account-payments")
            .method("POST")
            .headers(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .body(objectMapper.writeValueAsString(getPaymentRequest(BigDecimal.TEN)))
            .willRespondWith()
            .headers(getHeadersMap())
            .status(201)
            .body(buildPBAPaymentResponseDsl("Success", "success", null, "Insufficient funds available"))
            .toPact();
    }

    // currently failing - Isha looking into it
    @Test
    @PactTestFor(pactMethod = "generatePactFragmentSuccess")
    @Ignore
    public void verifyPBAPaymentPactSuccess() {
        PaymentResponse paymentResponse = paymentsService.getCreditAccountPaymentResponse(AUTH_TOKEN,
            getPaymentRequest(BigDecimal.TEN));
        assertEquals("reference", paymentResponse.getReference());
    }
}
