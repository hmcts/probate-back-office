package uk.hmcts.reform.probate.backoffice;

import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildStartEventReponse;

@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "ccd", port = "8891")
@PactFolder("pacts")
@SpringBootTest({
        "core_case_data.api.url : localhost:8891"
})
public class ProbateBackOfficeStartEventForCaseworker extends AbstractBackOfficePact{

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

    private Map<String, Object> caseDetailsMap;

    @Value("${ccd.bulk.eventid.create}")
    private String createEventId;

    private static final String USER_ID = "123456";
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";


    Map<String, Object> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @BeforeAll
    public void setUp() throws Exception {
        caseDetailsMap = getCaseDetailsAsMap("backoffice-case.json");
        caseDataContent = CaseDataContent.builder()
                .eventToken("someEventToken")
                .event(
                        Event.builder()
                                .id(createEventId)
                                .summary(DIVORCE_CASE_SUBMISSION_EVENT_SUMMARY)
                                .description(DIVORCE_CASE_SUBMISSION_EVENT_DESCRIPTION)
                                .build()
                ).data(caseDetailsMap.get("case_data"))
                .build();
    }
    @BeforeEach
    public void setUpEachTest() throws InterruptedException {
        Thread.sleep(2000);
    }

    @After
    public void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(state = "Submit event for caseworkder", provider = "ccd", consumer = "probate_backoffice")
    RequestResponsePact submitEventForCaseworker(PactDslWithProvider builder) throws IOException, JSONException {
        // @formatter:off
        return builder
                .given("A SubmitEvent for Caseworker is  requested", getCaseDataContentAsMap(caseDataContent))
                .uponReceiving("A SubmitEvent for a caseworker is received.")
                .path("/caseworkers/" + USER_ID + "/jurisdictions/"
                        + jurisdictionId + "/case-types/"
                        + caseType
                        + "/cases/"
                        +  CASE_ID
                        + "/event-triggers/"
                        + createEventId
                        + "/token")
                .method("GET")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION, SOME_SERVICE_AUTHORIZATION_TOKEN)
                .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .willRespondWith()
                .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
                .status(200)
                .body(buildStartEventReponse("100", "testServiceToken" , "emailAddress@email.com", false,false,false))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "submitEventForCaseworker")
    public void verifySubmitEventForCaseworker() throws IOException, JSONException {

        final StartEventResponse startEventResponse = coreCaseDataApi.startEventForCaseWorker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,caseType,String.valueOf(CASE_ID),createEventId);

        assertThat(startEventResponse.getEventId(), is("100"));
        assertThat(startEventResponse.getToken(), is("testServiceToken"));

      //  assertCaseDetails(startEventResponse.getCaseDetails());


    }
}