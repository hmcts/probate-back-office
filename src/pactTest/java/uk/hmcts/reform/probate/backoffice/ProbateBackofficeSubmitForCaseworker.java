package uk.hmcts.reform.probate.backoffice;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import org.apache.http.client.fluent.Executor;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildCaseDetailsDsl;
import static uk.hmcts.reform.probate.backoffice.util.AssertionHelper.assertCaseDetails;
import static uk.hmcts.reform.probate.backoffice.util.ObjectMapperTestUtil.convertObjectToJsonString;
import static uk.hmcts.reform.probate.backoffice.util.PactDslFixtureHelper.getCaseDataContent;

public class ProbateBackofficeSubmitForCaseworker extends AbstractBackOfficePact {

    public static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";

    @Autowired
    private CoreCaseDataApi coreCaseDataApi;

    CaseDataContent caseDataContent;

    @Value("${ccd.jurisdictionid}")
    String jurisdictionId;

    @Value("${ccd.casetype}")
    String caseType;

    @Value("${ccd.eventid.create}")
    private String createEventId;

    private static final String USER_ID = "123456";
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @BeforeEach
    public void setUpEachTest() throws InterruptedException {
        Thread.sleep(2000);
    }

    @After
    void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(provider = "ccd", consumer = "probate_backoffice_caseworker")
    RequestResponsePact submitForCaseWorker(PactDslWithProvider builder) throws Exception {
        // @formatter:off
        return builder
                .given("Submit for caseworker is triggered", getCaseDataContentAsMap(caseDataContent))
                .uponReceiving("A Submit For Caseworker")
                .path("/caseworkers/"
                        + USER_ID
                        + "/jurisdictions/"
                        + jurisdictionId
                        + "/case-types/"
                        + caseType
                        + "/cases")
                .query("ignore-warning=true")
                .method("POST")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION, SOME_SERVICE_AUTHORIZATION_TOKEN)
                .body(convertObjectToJsonString(getCaseDataContent()))
                .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .willRespondWith()
                .status(200)
                .body(buildCaseDetailsDsl(100L, "someemailaddress.com", false, false,false))
                .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "submitForCaseWorker")
    public void submitForCaseWorker() throws Exception {

        caseDataContent = getCaseDataContent();

        CaseDetails caseDetailsReponse = coreCaseDataApi.submitForCaseworker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,
                caseType, true, caseDataContent);

        assertNotNull(caseDetailsReponse);
        assertNotNull(caseDetailsReponse.getCaseTypeId());
        assertEquals(caseDetailsReponse.getJurisdiction(), "DIVORCE");

        assertCaseDetails(caseDetailsReponse,false,false);

    }

}