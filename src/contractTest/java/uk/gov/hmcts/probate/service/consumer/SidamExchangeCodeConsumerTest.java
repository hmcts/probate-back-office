package uk.gov.hmcts.probate.service.consumer;


import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.probate.BusinessRulesValidationApplication;
import uk.gov.hmcts.probate.model.TokenExchangeResponse;
import uk.gov.hmcts.probate.service.IdamApi;

import java.util.HashMap;
import java.util.Map;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "idamApi_users", port = "8863")
@PactFolder("pacts")
@SpringBootTest({"auth.provider.client.user: http://localhost:8863"})
@TestPropertySource(locations = {"/application.properties"})
@ContextConfiguration(classes = {BusinessRulesValidationApplication.class})
public class SidamExchangeCodeConsumerTest {

    @Autowired
    private IdamApi idamApi;
    private static final String GRANT_TYPE = "authorization_code";
    private static final String CLIENT_ID = "auth.provider.client.id";
    private static final String CLIENT_SECRET = "auth.provider.client.secret";
    private static final String REDIRECT_URL = "auth.provider.client.redirect";
    private static final String CODE = "someCode";

    @After
    public void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(provider = "idamApi_authorization", consumer = "probate_backOffice")
    public RequestResponsePact generatePactFragmentExchangeCode(PactDslWithProvider builder) throws JSONException {

        return builder
            .given("a valid token is requested")
            .uponReceiving("Get a token")
            .path("/oauth2/token")
            .query("code=" + CODE + "&grant_type=" + GRANT_TYPE + "&client_id=" + CLIENT_ID + "&client_secret="
                + CLIENT_SECRET + "&redirect_uri=" + REDIRECT_URL)
            .headers(HttpHeaders.CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
            .method("POST")
            .willRespondWith()
            .headers(getHeadersMap())
            .status(200)
            .body(buildIdamAuthenticateResponseDsl())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "generatePactFragmentExchangeCode")
    public void verifyIdamExchangeCodePact() {
        TokenExchangeResponse tokenExchangeResponse = idamApi.exchangeCode(CODE, GRANT_TYPE, REDIRECT_URL,
            CLIENT_ID, CLIENT_SECRET);
        assertEquals("Token is not exchanged", "accessToken", tokenExchangeResponse.getAccessToken());
    }

    private DslPart buildIdamAuthenticateResponseDsl() {
        return newJsonBody((o) -> {
            o.stringType("access_token",
                "accessToken");
        }).build();
    }

    private Map<String, String> getHeadersMap() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
