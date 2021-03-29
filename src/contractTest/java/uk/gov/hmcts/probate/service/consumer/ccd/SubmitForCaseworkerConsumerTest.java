package uk.gov.hmcts.probate.service.consumer.ccd;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.probate.service.consumer.util.AssertionHelper.assertBackOfficeCaseData;
import static uk.gov.hmcts.probate.service.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.reform.probate.pact.dsl.ObjectMapperTestUtil.convertObjectToJsonString;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildCaseDetailsDsl;

public class SubmitForCaseworkerConsumerTest extends AbstractCcdConsumerTest {

    private static final String BASECASE_PAYLOAD_PATH = "json/base-case.json";

    @Pact(consumer = "probate_backOffice")
    public RequestResponsePact submitForCaseWorkerFragment(PactDslWithProvider builder) throws Exception {
        // @formatter:off
        return builder
            .given("A Submit for a Caseworker is requested", setUpStateMapForProvider(APPLY_FOR_GRANT))
            .uponReceiving("A Submit for Caseworker")
            .path("/caseworkers/"
                + caseworkerUsername
                + "/jurisdictions/"
                + jurisdictionId
                + "/case-types/"
                + caseType
                + "/cases")
            .query("ignore-warning=true")
            .method("POST")
            .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION, SOME_SERVICE_AUTHORIZATION_TOKEN)
            .body(convertObjectToJsonString(getCaseDataContent(APPLY_FOR_GRANT, BASECASE_PAYLOAD_PATH)))
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .willRespondWith()
            .status(HttpStatus.SC_CREATED)
            .body(buildCaseDetailsDsl(CASE_ID, false, false))
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "submitForCaseWorkerFragment")
    public void submitForCaseWorker() throws Exception {

        CaseDataContent caseDataContent = getCaseDataContent(APPLY_FOR_GRANT, BASECASE_PAYLOAD_PATH);

        CaseDetails caseDetails = coreCaseDataApi.submitForCaseworker(SOME_AUTHORIZATION_TOKEN,
            SOME_SERVICE_AUTHORIZATION_TOKEN, caseworkerUsername, jurisdictionId,
            caseType, true, caseDataContent);

        assertNotNull(caseDetails);
        assertNotNull(caseDetails.getCaseTypeId());
        assertEquals(caseDetails.getJurisdiction(), jurisdictionId);

        assertCaseDetails(caseDetails);
        assertBackOfficeCaseData(caseDetails);
    }

    @Override
    protected Map<String, Object> setUpStateMapForProvider(String eventId) throws Exception {
        Map<String, Object> caseDataContentMap = super.setUpStateMapForProvider(eventId);
        caseDataContentMap.put(EVENT_ID, APPLY_FOR_GRANT);
        return caseDataContentMap;
    }
}