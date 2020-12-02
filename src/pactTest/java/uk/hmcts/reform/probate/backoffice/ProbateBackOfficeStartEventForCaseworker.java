package uk.hmcts.reform.probate.backoffice;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildStartEventReponse;

public class ProbateBackOfficeStartEventForCaseworker extends AbstractBackOfficePact{

    public static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    private static final Long CASE_ID = 2000L;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${ccd.jurisdictionid}")
    String jurisdictionId;

    @Value("${ccd.casetype}")
    String caseType;

    CaseDataContent caseDataContent;
    private Map<String, Object> caseDetailsMap;

    @Value("${ccd.eventid.create}")
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

    @Pact(provider = "ccdDataStoreAPI_Cases", consumer = "probate_backOfficeService")
    RequestResponsePact startEventForCaseworkder(PactDslWithProvider builder) throws IOException, JSONException {
        // @formatter:off
        return builder
                .given("A StartEvent  for Caseworker is  requested", getCaseDataContentAsMap(caseDataContent))
                .uponReceiving("A StartEvent for a Caseworker")
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
    @PactTestFor(pactMethod = "startEventForCaseworkder")
    public void verifyStartEventForCaseworer() throws IOException, JSONException {

        final StartEventResponse startEventResponse = coreCaseDataApi.startEventForCaseWorker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,caseType,String.valueOf(CASE_ID),createEventId);

        assertThat(startEventResponse.getEventId(), is("100"));
        assertThat(startEventResponse.getToken(), is("testServiceToken"));

      //  assertCaseDetails(startEventResponse.getCaseDetails());


    }
}