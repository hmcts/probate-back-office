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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.fee.FeeService;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeesRegisterConsumerTest {

    @Rule
    public PactHttpsProviderRuleMk2 mockProvider =
        new PactHttpsProviderRuleMk2("feeRegister_lookUp", "localhost", 4411, this);

    @Autowired
    FeeService feeService;

    @MockBean
    AppInsights appInsights;

    @MockBean
    FeatureToggleService featureToggleServiceMock;

    private static final String USER_ID = "user-id";
    private static final String DOCUMENT_ID = "12345";

    @Before
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
            .status(HttpStatus.SC_NO_CONTENT)
            .toPact();
    }


    @Test
    @PactVerification(fragment = "createApplicationFeeFragmentSA")
    public void verifyApplicationFeeServicePact() throws JSONException {

        BigDecimal result = feeService.getApplicationFee(new BigDecimal("250000.00"));
        Assert.assertTrue(new BigDecimal("200").equals(result));

    }

    @Test
    @PactVerification(fragment = "createCopiesFeeFragment")
    public void verifyCopiesFeeServicePact() throws JSONException {
        BigDecimal result = feeService.getCopiesFee(3L);
        Assert.assertTrue(new BigDecimal("3.5").equals(result));

    }

    @Test
    @PactVerification(fragment = "createCopiesNoFeeFragment")
    public void verifyCopiesNoFeeServicePact() throws JSONException {
        BigDecimal result = feeService.getCopiesFee(0L);
        Assert.assertTrue(new BigDecimal("0").equals(result));
    }

}
