package uk.hmcts.reform.probate.backoffice;


import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;

import java.util.Map;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;

import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildCaseDetailsDsl;
import static uk.hmcts.reform.probate.backoffice.util.AssertionHelper.assertCaseDetails;

public class ProbateBackOfficeReadForCaseworker extends AbstractBackOfficePact {

    private Map<String, Object> caseDetailsMap;
    private CaseDataContent caseDataContent;

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

    @Pact(provider = "ccdDataStoreAPI_CaseController", consumer = "probate_backOfficeService")
    RequestResponsePact readForCaseworker(PactDslWithProvider builder) throws JSONException{
        // @formatter:off
        return builder
                .given("Read For Caseworker", getCaseDataContentAsMap(caseDataContent))
                .uponReceiving("A Read For CaseWorker is received.")
                .path("/caseworkers/"
                        + USER_ID
                        + "/jurisdictions/"
                        + jurisdictionId
                        + "/case-types/"
                        + caseType
                        + "/cases/"
                        +  CASE_ID)
                .method("GET")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                        SOME_SERVICE_AUTHORIZATION_TOKEN)
                .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .willRespondWith()
                .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .status(200)
                .body(buildCaseDetailsDsl(CASE_ID, "emailAddress@email.com",false, false, false))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "readForCaseworker")
    public void verifyReadForCaseworker() throws  JSONException {

        CaseDetails caseDetailsReponse = coreCaseDataApi.readForCaseWorker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,
                caseType,CASE_ID.toString());

        assertCaseDetails(caseDetailsReponse, false , false);

    }
}
