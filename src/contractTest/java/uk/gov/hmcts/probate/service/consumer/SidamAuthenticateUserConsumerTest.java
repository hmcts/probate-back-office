package uk.gov.hmcts.probate.service.consumer;


import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
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
import uk.gov.hmcts.probate.model.AuthenticateUserResponse;
import uk.gov.hmcts.probate.service.IdamApi;

import java.io.IOException;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.junit.Assert.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "idamApi_users", port = "8861")
@PactFolder("pacts")
@SpringBootTest({"auth.provider.client.user: http://localhost:8861"})
@TestPropertySource(locations = {"/application.properties"})
@ContextConfiguration(classes = {BusinessRulesValidationApplication.class})
public class SidamAuthenticateUserConsumerTest {

    @Autowired
    private IdamApi idamApi;
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    private static final String RESPONSE_TYPE = "code";
    private static final String CLIENT_ID = "auth.provider.client.id";
    private static final String REDIRECT_URL = "auth.provider.client.redirect";


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

    // currently failing - Isha looking into it
    @Test
    @PactTestFor(pactMethod = "generatePactFragmentGetOAuth2Token")
    public void verifyIdamAuthoriseUserPact() {
        AuthenticateUserResponse authenticateUserResponse = idamApi.authenticateUser(AUTH_TOKEN,
            RESPONSE_TYPE, CLIENT_ID,
            REDIRECT_URL);
        assertEquals("User is not Authorised", "123432", authenticateUserResponse.getCode());
    }

    private DslPart buildIdamAuthenticateResponseDsl() {
        return newJsonBody((o) -> {
            o.stringType("code",
                "123432");


        }).build();
    }
}
