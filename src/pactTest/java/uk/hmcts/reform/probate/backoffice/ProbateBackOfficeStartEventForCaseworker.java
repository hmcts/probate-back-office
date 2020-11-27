package uk.hmcts.reform.probate.backoffice;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.JurisdictionId;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "ccd", port = "8891")
@PactFolder("pacts")
@SpringBootTest({
        "core_case_data.api.url : localhost:8891"
})
public class ProbateBackOfficeStartEventForCaseworker {

    public static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    private static final Long CASE_ID = 2000L;

    @Autowired
    private CoreCaseDataApi coreCaseDataApi;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${ccd.jurisdictionid}")
    String jurisdictionId;

    @Value("${ccd.casetype}")
    String caseType;

    CaseDataContent caseDataContent;

    @Value("${ccd.bulk.eventid.create}")
    private String createEventId;

    private static final String USER_ID = "123456";
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    Map<String, Object> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);


    @BeforeEach
    public void setUpEachTest() throws InterruptedException {
        Thread.sleep(2000);
    }

    @After
    public void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(state = "Submit event for citizen", provider = "ccd", consumer = "probate_backoffice")
    RequestResponsePact submitEventForCitizen(PactDslWithProvider builder) throws IOException, JSONException {
        // @formatter:off
        return builder
                .given("A submit request for citizen is requested")
                .uponReceiving("a request for a valid submit event")
                .path("/citizens/" + USER_ID + "/jurisdictions/"
                        + JurisdictionId.PROBATE.name() + "/case-types/"
                        + CaseType.GRANT_OF_REPRESENTATION.getName()
                        + "/cases/" + CASE_ID
                        + "/events")
                .method("POST")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN,
                        SERVICE_AUTHORIZATION, SOME_SERVICE_AUTHORIZATION_TOKEN)
                .matchQuery("ignore-warning", Boolean.TRUE.toString())
                //.body(createJsonObject(caseDataContent))
                .body("")
                .willRespondWith()
                .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
                .status(201)
                //.body(createJsonObject(caseDetails))
                .body("")
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "submitEventForCitizen")
    public void verifySubmitEventForCitizen() throws IOException, JSONException {

        CaseDetails caseDetails = coreCaseDataApi.submitEventForCitizen(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID.toString(), JurisdictionId.PROBATE.name(),
                CaseType.GRANT_OF_REPRESENTATION.getName(), CASE_ID.toString(), Boolean.TRUE, caseDataContent);

        assertThat(caseDetails.getId(), equalTo(CASE_ID));

    }
}