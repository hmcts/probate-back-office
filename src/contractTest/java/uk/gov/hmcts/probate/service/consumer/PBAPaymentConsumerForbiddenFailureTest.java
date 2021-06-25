package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Executor;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "payment_creditAccountPayment", port = "8889")
@PactFolder("pacts")
@SpringBootTest
@TestPropertySource(locations = {"/application.properties"})
public class PBAPaymentConsumerForbiddenFailureTest extends BasePBAPaymentTest {

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
    public RequestResponsePact generatePactFragmentFail(PactDslWithProvider builder) throws IOException {
        return builder
            .given("An active account has insufficient funds for a payment", getPaymentMap("1000.00"))
            .uponReceiving("A request for a payment")
            .path("/credit-account-payments")
            .method("POST")
            .headers(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .body(objectMapper.writeValueAsString(getPaymentRequest(BigDecimal.valueOf(1500))))
            .willRespondWith()
            .headers(getHeadersMap())
            .status(403)
            .body(buildPBAPaymentResponseDsl("Fail", "failed", "CA-E0001", "Insufficient funds available"))
            .toPact();
    }


    @Test
    @PactTestFor(pactMethod = "generatePactFragmentFail")
    public void verifyPBAPaymentPactFail() {
        verifyForbiddenRequest(BigDecimal.valueOf(1500));
    }

    private void verifyForbiddenRequest(BigDecimal amount) {
        assertThrows(Exception.class, () -> {
            paymentsService.getCreditAccountPaymentResponse(AUTH_TOKEN, getPaymentRequest(amount));
        });
    }
}
