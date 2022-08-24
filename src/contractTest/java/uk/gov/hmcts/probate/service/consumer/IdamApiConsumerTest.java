package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.probate.BusinessRulesValidationApplication;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.probate.model.idam.TokenRequest;
import uk.gov.hmcts.reform.probate.model.idam.TokenResponse;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(SpringExtension.class)
@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactFolder("pacts")
@PactTestFor(providerName = "idamApi_oidc", port = "8891")
@SpringBootTest({"auth.provider.client.user: http://localhost:8891"})
@ContextConfiguration(classes = {BusinessRulesValidationApplication.class})
@TestPropertySource(locations = {"/application.properties"}, properties = {"idam.api.url=localhost:8891"})
public class IdamApiConsumerTest {
    @Autowired
    IdamApi idamApi;
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";

    @BeforeEach
    public void prepareTest() throws Exception {
        Thread.sleep(2000);
    }

    @AfterEach
    void teardown() {
        Executor.closeIdleConnections();
    }


    @Pact(provider = "idamApi_oidc", consumer = "probate_backOffice")
    public RequestResponsePact generatePactFragmentUserInfo(PactDslWithProvider builder) {

        return builder
                .given("userinfo is requested")
                .uponReceiving("A request for a UserInfo")
                .path("/o/userinfo")
                .method(HttpMethod.GET.toString())
                .matchHeader(AUTHORIZATION, AUTH_TOKEN)
                .willRespondWith()
                .status(200)
                .body(createUserDetailsResponse())
                .toPact();
    }

    @Pact(provider = "idamApi_oidc", consumer = "probate_backOffice")
    public RequestResponsePact generatePactFragmentToken(PactDslWithProvider builder) throws JSONException {
        Map<String, String> responseheaders = ImmutableMap.<String, String>builder()
                .put("Content-Type", "application/json")
                .build();
        return builder
                .given("a token is requested")
                .uponReceiving("Provider receives a POST /o/token request from an Probate API")
                .path("/o/token")
                .method(HttpMethod.POST.toString())
                .body("redirect_uri=http%3A%2F%2Fwww.dummy-pact-service.com%2Fcallback"
                                + "&client_id=pact&grant_type=password"
                                + "&username=ia-caseofficer@fake.hmcts.net"
                                + "&password=London01"
                                + "&client_secret=pactsecret"
                                + "&scope=openid profile roles",
                        "application/x-www-form-urlencoded")
                .willRespondWith()
                .status(200)
                .headers(responseheaders)
                .body(createAuthResponse())
                .toPact();
    }

    private PactDslJsonBody createUserDetailsResponse() {
        return new PactDslJsonBody()
                .stringType("uid", "1111-2222-3333-4567")
                .stringValue("sub", "ia-caseofficer@fake.hmcts.net")
                .stringValue("givenName", "Case")
                .stringValue("familyName", "Officer")
                .minArrayLike("roles", 1, PactDslJsonRootValue.stringType("caseworker-ia-legalrep-solicitor"), 1)
                .stringType("IDAM_ADMIN_USER", "idamAdminUser");
    }

    private PactDslJsonBody createAuthResponse() {
        return new PactDslJsonBody()
                .stringType("access_token", "eyJ0eXAiOiJKV1QiLCJraWQiOiJiL082T3ZWdjEre")
                .stringType("scope", "openid roles profile")
                .stringType("expires_in","28798");
    }

    @Test
    @PactTestFor(pactMethod = "generatePactFragmentUserInfo")
    public void verifyIdamPactUserInfo() {
        UserInfo userInfo = idamApi.retrieveUserInfo(AUTH_TOKEN);
        assertNotNull(userInfo.getUid());
        assertNotNull(userInfo.getSub());
        assertNotNull(userInfo.getGivenName());
        assertNotNull(userInfo.getFamilyName());
        assertNotNull(userInfo.getRoles());
    }

    @Test
    @PactTestFor(pactMethod = "generatePactFragmentToken")
    public void verifyIdamUPactToken() {

        TokenResponse token = idamApi.generateOpenIdToken(buildTokenRequest());
        assertNotNull(token.accessToken);
    }

    private TokenRequest buildTokenRequest() {
        return new TokenRequest(
                "pact",
                "pactsecret",
                "password",
                "http://www.dummy-pact-service.com/callback",
                "ia-caseofficer@fake.hmcts.net",
                "London01",
                "openid profile roles",
                null, null);
    }
}
