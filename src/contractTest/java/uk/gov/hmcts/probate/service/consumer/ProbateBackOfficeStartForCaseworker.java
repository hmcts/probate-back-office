package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildStartEventResponseWithEmptyCaseDetails;

@RunWith(SpringRunner.class)
@SpringBootTest({
    "core_case_data.api.url: localhost:4456"
})
public class ProbateBackOfficeStartForCaseworker extends AbstractBackOfficePact {

    @Rule
    public PactHttpsProviderRuleMk2 provider = new PactHttpsProviderRuleMk2("ccdDataStoreAPI_Cases", "localhost", 4456, this);


    @Pact(provider = "ccdDataStoreAPI_Cases", consumer = "probate_backOfficeService")
    public RequestResponsePact startForCaseWorkerFragment(PactDslWithProvider builder) throws Exception {
        // @formatter:off
        return builder
            .given("A Start for a Caseworker is requested", setUpStateMapForProvider(createEventId))
            .uponReceiving("A Start for a Caseworker")
            .path("/caseworkers/" + caseworkerUsername + "/jurisdictions/"
                + jurisdictionId + "/case-types/"
                + caseType
                + "/event-triggers/"
                + APPLY_FOR_GRANT
                + "/token")
            .method("GET")
            .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                SOME_SERVICE_AUTHORIZATION_TOKEN)
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .willRespondWith()
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .status(200)
            .body(buildStartEventResponseWithEmptyCaseDetails(APPLY_FOR_GRANT))
            .toPact();
    }


    @Test
    @PactVerification(fragment = "startForCaseWorkerFragment")
    public void verifyStartForCaseworker() {
        StartEventResponse startEventResponse = coreCaseDataApi.startForCaseworker(SOME_AUTHORIZATION_TOKEN,
            SOME_SERVICE_AUTHORIZATION_TOKEN, caseworkerUsername, jurisdictionId,
            caseType, APPLY_FOR_GRANT);

        assertThat(startEventResponse.getEventId().equals(APPLY_FOR_GRANT));

    }

    @Override
    protected Map<String, Object> setUpStateMapForProvider(String eventId) throws Exception {
        Map<String, Object> caseDataContentMap = super.setUpStateMapForProvider(eventId);
        caseDataContentMap.put(EVENT_ID, APPLY_FOR_GRANT);
        return caseDataContentMap;
    }


}