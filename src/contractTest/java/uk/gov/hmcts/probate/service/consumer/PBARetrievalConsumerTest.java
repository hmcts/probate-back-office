package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.service.IdamAuthenticateUserService;
import uk.gov.hmcts.probate.service.payments.pba.PBARetrievalService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PBARetrievalConsumerTest {

    public static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    public static final String ORGANISATION_EMAIL = "someemailaddress@organisation.com";
    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public static final String EMAIL_KEY = "UserEmail";

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PBARetrievalService pbaRetrievalService;
    @MockBean
    IdamAuthenticateUserService idamAuthenticateUserService;
    @MockBean
    AuthTokenGenerator authTokenGenerator;
    
    @Rule
    public PactHttpsProviderRuleMk2 mockProvider =
        new PactHttpsProviderRuleMk2("referenceData_organisationalExternalPbas", "localhost", 8887, this);


    @Pact(provider = "referenceData_organisationalExternalPbas", consumer = "probate_backOffice")
    public RequestResponsePact generatePbaRetrievalPactFragment(PactDslWithProvider builder) throws JSONException {
        return builder
            .given("Pbas organisational data exists for identifier " + ORGANISATION_EMAIL)
            .uponReceiving("a request for information for that organisation's pbas")
            .path("/refdata/external/v1/organisations/pbas")
            .method("GET")
            .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                SOME_SERVICE_AUTHORIZATION_TOKEN, EMAIL_KEY, ORGANISATION_EMAIL)
            .willRespondWith()
            .status(200)
            .headers(getHeadersMap())
            .body(buildOrganisationalResponsePactDsl())
            .toPact();
    }

    @Test
    @PactVerification(fragment = "generatePbaRetrievalPactFragment")
    public void verifyPbaRetrievePact() {

        when(idamAuthenticateUserService.getEmail(SOME_AUTHORIZATION_TOKEN)).thenReturn(ORGANISATION_EMAIL);
        when(authTokenGenerator.generate()).thenReturn(SOME_SERVICE_AUTHORIZATION_TOKEN);
        List<String> pbas = pbaRetrievalService.getPBAs(SOME_AUTHORIZATION_TOKEN);
        assertThat(pbas.get(0), equalTo("paymentAccountA1"));
    }

    private DslPart buildOrganisationalResponsePactDsl() {
        return newJsonBody(o -> {
            o.object("organisationEntityResponse", ob -> ob
                .stringType("organisationIdentifier",
                    ORGANISATION_EMAIL)
                .stringMatcher("status",
                    "PENDING|ACTIVE|BLOCKED|DELETED", "ACTIVE")
                .stringType("sraId", "sraId")
                .booleanType("sraRegulated", true)
                .stringType("companyNumber", "123456")
                .stringType("companyUrl", "somecompany@org.com")
                .array("paymentAccount", pa ->
                    pa.stringType("paymentAccountA1"))
                .object("superUser", su -> su
                    .stringType("firstName", "firstName")
                    .stringType("lastName", "lastName")
                    .stringType("email", "emailAddress"))
            );
        }).build();
    }

    private Map<String, String> getHeadersMap() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
