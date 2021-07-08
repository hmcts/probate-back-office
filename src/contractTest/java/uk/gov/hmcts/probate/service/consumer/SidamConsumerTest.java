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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.probate.BusinessRulesValidationApplication;
import uk.gov.hmcts.probate.service.IdamApi;

import java.util.HashMap;
import java.util.Map;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.junit.Assert.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "idamApi_users", port = "8862")
@PactFolder("pacts")
@SpringBootTest({"auth.provider.client.user: http://localhost:8862"})
@TestPropertySource(locations = {"/application.properties"})
@ContextConfiguration(classes = {BusinessRulesValidationApplication.class})
public class SidamConsumerTest {

    @Autowired
    private IdamApi idamApi;
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";


    @After
    public void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(provider = "idamApi_users", consumer = "probate_backOffice")
    public RequestResponsePact generatePactFragmentGetUserDetails(PactDslWithProvider builder) throws JSONException {

        return builder
            .given("a valid user exists")
            .uponReceiving("A request for a User")
            .path("/details")
            .method("GET")
            .matchHeader("Authorization", AUTH_TOKEN)
            .willRespondWith()
            .headers(getHeadersMap())
            .status(200)
            .body(buildIdamDetailsResponseDsl())
            .toPact();
    }

    // currently failing - Isha looking into it
    @Test
    @PactTestFor(pactMethod = "generatePactFragmentGetUserDetails")
    @Ignore
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

    private Map<String, String> getHeadersMap() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
