package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import java.util.Map;

import static java.lang.String.valueOf;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildStartEventReponse;

@RunWith(SpringRunner.class)
@SpringBootTest({
    "core_case_data.api.url: localhost:4454"
})
public class ProbateBackOfficeStartEventForCaseworker extends AbstractBackOfficePact {

    @Rule
    public PactHttpsProviderRuleMk2 provider = new PactHttpsProviderRuleMk2("ccdDataStoreAPI_Cases", "localhost", 4454, this);

    protected static final String USER_ID = "123456";
    protected static final Long CASE_ID = 108L;
    protected static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    protected static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    protected static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";

    private Map<String, Object> caseDetailsMap;
    private CaseDataContent caseDataContent;

    @Autowired
    protected CoreCaseDataApi coreCaseDataApi;

    @Value("${ccd.jurisdictionid}")
    protected String jurisdictionId;

    @Value("${ccd.casetype}")
    protected String caseType;

    @Value("${ccd.eventid.create}")
    protected String createEventId;

    @Value("${idam.caseworker.username}")
    protected String caseworkerUsername;

    @Value("${idam.caseworker.password}")
    protected String caseworkerPwd;

    @Before
    public void setUp() throws Exception {
        caseDetailsMap = getCaseDetailsAsMap("backoffice-case.json");
        caseDataContent = CaseDataContent.builder()
            .eventToken("someEventToken")
            .event(
                Event.builder()
                    .id(createEventId)
                    .summary("PROBATE")
                    .description("PROBATE DESC")
                    .build()
            ).data(caseDetailsMap.get("case_data"))
            .build();
    }

    @Pact(provider = "ccdDataStoreAPI_Cases", consumer = "probate_backOffice")
    public RequestResponsePact startEventForCaseworkerFragment(PactDslWithProvider builder) throws JSONException {
        // @formatter:off
        return builder
            .given("A StartEvent  for Caseworker is  requested", getCaseDataContentAsMap(caseDataContent))
            .uponReceiving("A StartEvent for a Caseworker")
            .path("/caseworkers/" + USER_ID + "/jurisdictions/"
                + jurisdictionId + "/case-types/"
                + caseType
                + "/cases/"
                + CASE_ID
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
            .body(buildStartEventReponse("100", "testServiceToken", "emailAddress@email.com", false, false, false))
            .toPact();
    }

    @Test
    @PactVerification(fragment = "startEventForCaseworkerFragment")
    public void verifyStartEventForCaseworkerPact() throws Exception {

        final StartEventResponse startEventResponse = coreCaseDataApi
            .startEventForCaseWorker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN,
                "123456",
                "PROBATE",
                "PROBATE",
                valueOf(CASE_ID), "create");

        Assert.assertNotNull(startEventResponse);

    }

    @After
    public void  teardown() {
        Executor.closeIdleConnections();
    }
}