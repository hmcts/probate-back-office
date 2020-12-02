package uk.hmcts.reform.probate.backoffice;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import org.json.JSONException;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.Map;

import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import org.springframework.http.MediaType;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildStartEventReponse;
import static uk.hmcts.reform.probate.backoffice.util.AssertionHelper.assertCaseDetails;

public class ProbateBackOfficeStartForCaseworker extends AbstractBackOfficePact {

    public static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";

    @Value("${ccd.jurisdictionid}")
    String jurisdictionId;

    @Value("${ccd.casetype}")
    String caseType;

    @Value("${ccd.eventid.create}")
    String createEventId;

    private Map<String, Object> caseDetailsMap;
    private CaseDataContent caseDataContent;

    private static final String USER_ID = "123456";
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @Before
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

    @Pact(provider = "ccdDataStoreAPI_Cases", consumer = "probate_backOfficeService")
    RequestResponsePact startForCaseWorker(PactDslWithProvider builder) throws JSONException {
        // @formatter:off
        return builder
                .given("A Start for Caseworker is requested", getCaseDataContentAsMap(caseDataContent))
                .uponReceiving("A Start for Caseworker")
                .path("/caseworkers/" + USER_ID + "/jurisdictions/"
                        + jurisdictionId + "/case-types/"
                        + caseType
                        + "/event-triggers/"
                        + createEventId
                        + "/token")
                .method("GET")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                        SOME_SERVICE_AUTHORIZATION_TOKEN)
                .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .willRespondWith()
                .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
                .status(200)
                .body(buildStartEventReponse(createEventId , "token","someemailaddress.com", false,false,false))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "startForCaseWorker")
    public void verifyStartForCaseworker() throws IOException, JSONException {

        StartEventResponse startEventResponse = coreCaseDataApi.startForCaseworker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,
                caseType,createEventId);

        assertThat(startEventResponse.getEventId(), equalTo(createEventId));
        assertThat(startEventResponse.getToken(), is("token"));
        assertCaseDetails(startEventResponse.getCaseDetails(), false, false);

    }
}
