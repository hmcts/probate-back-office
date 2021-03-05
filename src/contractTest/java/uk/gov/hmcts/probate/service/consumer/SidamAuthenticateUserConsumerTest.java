package uk.gov.hmcts.probate.service.consumer;


import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.BusinessRulesValidationApplication;
import uk.gov.hmcts.probate.model.AuthenticateUserResponse;
import uk.gov.hmcts.probate.service.IdamApi;

import java.io.IOException;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {BusinessRulesValidationApplication.class})
public class SidamAuthenticateUserConsumerTest {

    @Autowired
    private IdamApi idamApi;
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    private static final String RESPONSE_TYPE = "code";
    private static final String CLIENT_ID = "auth.provider.client.id";
    private static final String REDIRECT_URL = "auth.provider.client.redirect";


    @Rule
    public PactHttpsProviderRuleMk2 mockProvider =
        new PactHttpsProviderRuleMk2("idamApi_users", "localhost", 8887, this);

    @After
    public void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(provider = "idamApi_users", consumer = "probate_backOffice")
    public RequestResponsePact generatePactFragmentGetOAuth2Token(PactDslWithProvider builder) throws JSONException,
        IOException {

        return builder
            .given("a user exists")
            .uponReceiving("Authorise a User")
            .path("/oauth2/authorize")
            .query("response_type=" + RESPONSE_TYPE + "&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URL)
            .method("POST")
            .matchHeader("Authorization", AUTH_TOKEN)
            .willRespondWith()
            .status(200)
            .body(buildIdamAuthenticateResponseDsl())
            .toPact();
    }

    @Test
    @PactVerification(fragment = "generatePactFragmentGetOAuth2Token")
    public void verifyIdamAuthoriseUserPact() {
        AuthenticateUserResponse authenticateUserResponse = idamApi.authenticateUser(AUTH_TOKEN, RESPONSE_TYPE, 
            CLIENT_ID, REDIRECT_URL);
        assertEquals("User is not Authorised", "123432", authenticateUserResponse.getCode());
    }

    private DslPart buildIdamAuthenticateResponseDsl() {
        return newJsonBody((o) -> {
            o.stringType("code",
                "123432");


        }).build();
    }
}
