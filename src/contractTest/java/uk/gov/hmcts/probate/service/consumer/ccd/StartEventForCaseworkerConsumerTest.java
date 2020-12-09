package uk.gov.hmcts.probate.service.consumer.ccd;

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

import static java.lang.String.valueOf;
import static uk.gov.hmcts.probate.service.consumer.util.AssertionHelper.assertBackOfficeCaseData;
import static uk.gov.hmcts.probate.service.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildStartEventReponse;

@RunWith(SpringRunner.class)
@SpringBootTest({
    "core_case_data.api.url: localhost:4454"
})
public class StartEventForCaseworkerConsumerTest extends AbstractCcdConsumerTest {

    @Rule
    public PactHttpsProviderRuleMk2 provider = new PactHttpsProviderRuleMk2("ccdDataStoreAPI_Cases", "localhost", 4454, this);


    @Pact(provider = "ccdDataStoreAPI_Cases", consumer = "probate_backOffice")
    public RequestResponsePact startEventForCaseworkerFragment(PactDslWithProvider builder) throws Exception {
        // @formatter:off
        return builder
            .given("A Start Event for a Caseworker is  requested", setUpStateMapForProviderWithCaseData(createEventId))
            .uponReceiving("A Start Event for a Caseworker")
            .path("/caseworkers/" + caseworkerUsername + "/jurisdictions/"
                + jurisdictionId + "/case-types/"
                + caseType
                + "/cases/"
                + CASE_ID
                + "/event-triggers/"
                + CREATE_APPLICATION_EVENT
                + "/token")
            .method("GET")
            .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                SOME_SERVICE_AUTHORIZATION_TOKEN)
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .willRespondWith()
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .status(200)
            .body(buildStartEventReponse(CREATE_APPLICATION_EVENT, "testServiceToken", false, false))
            .toPact();
    }

    @Test
    @PactVerification(fragment = "startEventForCaseworkerFragment")
    public void verifyStartEventForCaseworkerPact() {

        final StartEventResponse startEventResponse = coreCaseDataApi
            .startEventForCaseWorker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN,
                caseworkerUsername,
                jurisdictionId,
                caseType,
                valueOf(CASE_ID), CREATE_APPLICATION_EVENT);

        assertCaseDetails(startEventResponse.getCaseDetails());
        assertBackOfficeCaseData(startEventResponse.getCaseDetails());

    }

    @Override
    protected Map<String, Object> setUpStateMapForProviderWithCaseData(String eventId) throws Exception {
        Map<String, Object> caseDataContentMap = super.setUpStateMapForProviderWithCaseData(eventId);
        caseDataContentMap.put(EVENT_ID, CREATE_APPLICATION_EVENT);
        return caseDataContentMap;
    }

}