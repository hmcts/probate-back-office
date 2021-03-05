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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.BusinessRulesValidationApplication;
import uk.gov.hmcts.probate.service.IdamApi;

import java.io.IOException;
import java.util.Map;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {BusinessRulesValidationApplication.class})
public class SidamConsumerTest {

    @Autowired
    private IdamApi idamApi;
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";


    @Rule
    public PactHttpsProviderRuleMk2 mockProvider =
        new PactHttpsProviderRuleMk2("idamApi_users", "localhost", 8887, this);

    @After
    public void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(provider = "idamApi_users", consumer = "probate_backOffice")
    public RequestResponsePact generatePactFragmentGetUserDetails(PactDslWithProvider builder) throws JSONException,
        IOException {

        return builder
            .given("a valid user exists")
            .uponReceiving("A request for a User")
            .path("/details")
            .method("GET")
            .matchHeader("Authorization", AUTH_TOKEN)
            .willRespondWith()
            .status(200)
            .body(buildIdamDetailsResponseDsl())
            .toPact();
    }

    @Test
    @PactVerification(fragment = "generatePactFragmentGetUserDetails")
    public void verifyIdamUserDetailsRolesPact() {
        ResponseEntity<Map<String, Object>> userMapResponse = idamApi.getUserDetails(AUTH_TOKEN);
        assertEquals("User is not Admin", "joe.bloggs@hmcts.net", userMapResponse.getBody().get("email"));
    }


    private DslPart buildIdamDetailsResponseDsl() {
        return newJsonBody((o) -> {
            o.stringType("id",
                "123432")
                .stringType("forename", "Joe")
                .stringType("surname", "Bloggs")
                .stringType("email", "joe.bloggs@hmcts.net")
                .booleanType("active", true)
                .array("roles", r -> r.stringType("caseworker"))
            ;


        }).build();
    }
}
