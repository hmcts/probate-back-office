package uk.gov.hmcts.probate.service.consumer.ccd;


import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.probate.service.consumer.util.AssertionHelper.assertBackOfficeCaseData;
import static uk.gov.hmcts.probate.service.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildCaseDetailsDsl;


public class ReadForCaseworkerConsumerTest extends AbstractCcdConsumerTest {

    @Pact(provider = "ccdDataStoreAPI_Cases", consumer = "probate_backOffice")
    public RequestResponsePact readForCaseworkerFragment(PactDslWithProvider builder) throws Exception {
        return builder
            .given("A Read for a Caseworker is requested", setUpStateMapForProviderWithCaseData(createEventId))
            .uponReceiving("A Read For a CaseWorker")
            .path("/caseworkers/"
                + USER_ID
                + "/jurisdictions/"
                + jurisdictionId
                + "/case-types/"
                + caseType
                + "/cases/"
                + CASE_ID)
            .method("GET")
            .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION,
                SOME_SERVICE_AUTHORIZATION_TOKEN)
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .willRespondWith()
            .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .status(200)
            .body(buildCaseDetailsDsl(CASE_ID, false, false))
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "readForCaseworkerFragment")
    public void verifyReadForCaseworkerPact() throws JSONException {

        CaseDetails caseDetailsReponse = coreCaseDataApi.readForCaseWorker(SOME_AUTHORIZATION_TOKEN,
            SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,
            caseType, String.valueOf(CASE_ID));

        assertNotNull(caseDetailsReponse);

        assertCaseDetails(caseDetailsReponse);

        assertBackOfficeCaseData(caseDetailsReponse);

    }

}
