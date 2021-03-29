package uk.gov.hmcts.probate.service.consumer.ccd;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.probate.service.consumer.util.AssertionHelper.assertBackOfficeCaseData;
import static uk.gov.hmcts.reform.probate.pact.dsl.ObjectMapperTestUtil.convertObjectToJsonString;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildCaseDetailsDsl;

@RunWith(SpringRunner.class)
@SpringBootTest({
    "core_case_data.api.url: localhost:4457"
})
public class SubmitEventForCaseworkerConsumerTest extends AbstractCcdConsumerTest {

    private static final String BASECASE_PAYLOAD_PATH = "json/base-case.json";

    @Rule
    public PactHttpsProviderRuleMk2 provider = new PactHttpsProviderRuleMk2("ccdDataStoreAPI_Cases", "localhost", 4457, this);

    @Pact(consumer = "probate_backOffice")
    public RequestResponsePact submitEventForCaseWorkerFragment(PactDslWithProvider builder) throws Exception {
        return builder
            .given("A Submit Event for a Caseworker is requested", setUpStateMapForProviderWithCaseData(APPLY_FOR_GRANT))
            .uponReceiving("A Submit Event for a Caseworker")
            .path("/caseworkers/" + caseworkerUsername
                + "/jurisdictions/" + jurisdictionId
                + "/case-types/" + caseType
                + "/cases/" + CASE_ID
                + "/events"
            )
            .query("ignore-warning=true")
            .method("POST")
            .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION, SOME_SERVICE_AUTHORIZATION_TOKEN)
            .body(convertObjectToJsonString(getCaseDataContent(PAYMENT_SUCCESS_APP, BASECASE_PAYLOAD_PATH)))
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .willRespondWith()
            .status(HttpStatus.SC_CREATED)
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(buildCaseDetailsDsl(CASE_ID, false, false))
            .toPact();
    }

    @Test
    @PactVerification(fragment = "submitEventForCaseWorkerFragment")
    public void submitEventForCaseWorker() throws Exception {

        CaseDataContent caseDataContent = getCaseDataContent(PAYMENT_SUCCESS_APP, BASECASE_PAYLOAD_PATH);

        final CaseDetails caseDetails = coreCaseDataApi.submitEventForCaseWorker(SOME_AUTHORIZATION_TOKEN,
            SOME_SERVICE_AUTHORIZATION_TOKEN, caseworkerUsername, jurisdictionId, caseType, CASE_ID.toString(), true, caseDataContent);

        assertNotNull(caseDetails);
        assertBackOfficeCaseData(caseDetails);

    }

    @Override
    protected Map<String, Object> setUpStateMapForProviderWithCaseData(String eventId) throws Exception {
        Map<String, Object> caseDataContentMap = super.setUpStateMapForProviderWithCaseData(eventId);
        caseDataContentMap.put(EVENT_ID, PAYMENT_SUCCESS_APP);
        return caseDataContentMap;
    }
}