package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.fee.FeeService;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "feeRegister_lookUp", port = "4411")
@PactFolder("pacts")
@SpringBootTest
@TestPropertySource(locations = {"/application.properties"})
public class FeesRegisterConsumerTest {

    @Autowired
    FeeService feeService;

    @MockBean
    FeatureToggleService featureToggleServiceMock;

    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    private static final String USER_ID = "user-id";
    private static final String DOCUMENT_ID = "12345";

    @BeforeEach
    public void setUpTest() {
        when(featureToggleServiceMock.isNewFeeRegisterCodeEnabled()).thenReturn(Boolean.TRUE);
    }

    @Pact(provider = "feeRegister_lookUp", consumer = "probate_backOffice")
    public RequestResponsePact createApplicationFeeFragmentSA(PactDslWithProvider builder) {
        return builder
            .given("Fees exist for Probate")
            .uponReceiving("a request for Probate fees")
            .path("/fees-register/fees/lookup")
            .method("GET")
            .matchQuery("service", "probate", "probate")
            .matchQuery("jurisdiction1", "family", "family")
            .matchQuery("jurisdiction2", "probate registry", "probate registry")
            .matchQuery("channel", "default", "default")
            .matchQuery("applicant_type", "all", "all")
            .matchQuery("event", "issue", "issue")
            .matchQuery("amount_or_volume", "250000.00", "250000.00")
            .matchQuery("keyword", "SA", "SA")
            .willRespondWith()
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(new PactDslJsonBody()
                .decimalType("fee_amount", 200.00))
            .status(HttpStatus.SC_OK)
            .toPact();
    }

    @Pact(provider = "feeRegister_lookUp", consumer = "probate_backOffice")
    public RequestResponsePact createCopiesFeeFragment(PactDslWithProvider builder) {
        return builder
            .given("Copies fee exist for Probate")
            .uponReceiving("a request for Probate copies fees")
            .path("/fees-register/fees/lookup")
            .method("GET")
            .matchQuery("service", "probate", "probate")
            .matchQuery("jurisdiction1", "family", "family")
            .matchQuery("jurisdiction2", "probate registry", "probate registry")
            .matchQuery("channel", "default", "default")
            .matchQuery("applicant_type", "all", "all")
            .matchQuery("event", "copies", "copies")
            .matchQuery("amount_or_volume", "3", "3")
            .matchQuery("keyword", "GrantWill", "GrantWill")
            .willRespondWith()
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(new PactDslJsonBody()
                .decimalType("fee_amount", 3.50))
            .status(HttpStatus.SC_OK)
            .toPact();
    }

    @Pact(provider = "feeRegister_lookUp", consumer = "probate_backOffice")
    public RequestResponsePact createCopiesNoFeeFragment(PactDslWithProvider builder) {
        return builder
            .given("Copies fee exist for Probate")
            .uponReceiving("a request for Probate copies fees")
            .path("/fees-register/fees/lookup")
            .method("GET")
            .matchQuery("service", "probate", "probate")
            .matchQuery("jurisdiction1", "family", "family")
            .matchQuery("jurisdiction2", "probate registry", "probate registry")
            .matchQuery("channel", "default", "default")
            .matchQuery("applicant_type", "all", "all")
            .matchQuery("event", "copies", "copies")
            .matchQuery("amount_or_volume", "0", "0")
            .matchQuery("keyword", "GrantWill", "GrantWill")
            .willRespondWith()
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .status(HttpStatus.SC_OK)
            .body(new PactDslJsonBody()
                .stringType("code", "FEE0544")
                .stringType("description", "Copy of a document (for each copy)")
                .numberType("version", 7)
                .numberType("fee_amount", 0)
            )
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createApplicationFeeFragmentSA")
    public void verifyApplicationFeeServicePact() throws JSONException, SocketTimeoutException {

        FeeResponse result = feeService.getApplicationFeeResponse(new BigDecimal("250000.00"));
        assertTrue(new BigDecimal("200").equals(result.getFeeAmount()));

    }

    @Test
    @PactTestFor(pactMethod = "createCopiesFeeFragment")
    public void verifyCopiesFeeServicePact() throws JSONException, SocketTimeoutException {
        FeeResponse result = feeService.getCopiesFeeResponse(3L);
        assertTrue(new BigDecimal("3.5").equals(result.getFeeAmount()));

    }

    @Test
    @PactTestFor(pactMethod = "createCopiesNoFeeFragment")
    public void verifyCopiesNoFeeServicePact() throws JSONException, SocketTimeoutException {
        FeeResponse result = feeService.getCopiesFeeResponse(0L);
        assertTrue(new BigDecimal("0").equals(result.getFeeAmount()));
    }

}
