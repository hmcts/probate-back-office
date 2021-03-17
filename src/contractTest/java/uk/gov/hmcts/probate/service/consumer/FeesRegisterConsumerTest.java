package uk.gov.hmcts.probate.service.consumer;


import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.service.fee.FeeService;

import java.io.IOException;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeesRegisterConsumerTest {

    @Rule
    public PactHttpsProviderRuleMk2 mockProvider = new PactHttpsProviderRuleMk2("feeRegister_lookUp", "localhost", 4411, this);

    @Autowired
    FeeService feeService;

    @MockBean
    AppInsights appInsights;

    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    private static final String USER_ID = "user-id";
    private static final String DOCUMENT_ID = "12345";

    @Pact(provider = "feeRegister_lookUp", consumer = "probate_backOffice")
    public RequestResponsePact createApplicationFeeFragment(PactDslWithProvider builder) throws IOException {
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
            .willRespondWith()
            .body(new PactDslJsonBody()
                .decimalType("fee_amount", 200.00))
            .status(HttpStatus.SC_OK)
            .toPact();
    }

    @Pact(provider = "feeRegister_lookUp", consumer = "probate_backOffice")
    public RequestResponsePact createCopiesFeeFragment(PactDslWithProvider builder) throws IOException {
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
            .matchQuery("keyword", "NewFee", "NewFee")
            .willRespondWith()
            .body(new PactDslJsonBody()
                .decimalType("fee_amount", 3.50))
            .status(HttpStatus.SC_OK)
            .toPact();
    }

    @Pact(provider = "feeRegister_lookUp", consumer = "probate_backOffice")
    public RequestResponsePact createCopiesNoFeeFragment(PactDslWithProvider builder) throws IOException {
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
            .matchQuery("keyword", "NewFee", "NewFee")
            .willRespondWith()
            .status(HttpStatus.SC_NO_CONTENT)
            .toPact();
    }


    @Test
    @PactVerification(fragment = "createApplicationFeeFragment")
    public void verifyApplicationFeeServicePact() throws IOException, JSONException {

        BigDecimal result = feeService.getApplicationFee(new BigDecimal("250000.00"));
        Assert.assertTrue(new BigDecimal("200").equals(result));

    }

    @Test
    @PactVerification(fragment = "createCopiesFeeFragment")
    public void verifyCopiesFeeServicePact() throws IOException, JSONException {
        BigDecimal result = feeService.getCopiesFee(3L);
        Assert.assertTrue(new BigDecimal("3.5").equals(result));

    }

    @Test
    @PactVerification(fragment = "createCopiesNoFeeFragment")
    public void verifyCopiesNoFeeServicePact() throws IOException, JSONException {
        BigDecimal result = feeService.getCopiesFee(0L);
        Assert.assertTrue(new BigDecimal("0").equals(result));
    }

}
