package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.json.JSONException;
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
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;

import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static uk.gov.hmcts.probate.service.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildCaseDetailsDsl;

//@PactTestFor(providerName = "ccdDataStoreAPI_Cases", port = "4453")
@RunWith(SpringRunner.class)
@SpringBootTest({
    "core_case_data.api.url : localhost:4453"
})
public class ProbateBackOfficeReadForCaseworker extends AbstractBackOfficePact {

    @Rule
    public PactHttpsProviderRuleMk2 provider = new PactHttpsProviderRuleMk2("ccdDataStoreAPI_Cases", "localhost", 4453, this);

    protected static final Long CASE_ID = 108L;
    protected static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    protected static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    private static final String USER_ID = "123456";
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
        //Thread.sleep(2000);
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
    public RequestResponsePact readForCaseworkerFragment(PactDslWithProvider builder) throws JSONException {
        return builder
            .given("A Read For Caseworker is requested", getCaseDataContentAsMap(caseDataContent))
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
            .body(buildCaseDetailsDsl(CASE_ID, "emailAddress@email.com", false, false, false))
            .toPact();
    }

    @Test
    @PactVerification(fragment = "readForCaseworkerFragment")
    public void verifyReadForCaseworkerPact() throws JSONException {

        CaseDetails caseDetailsReponse = coreCaseDataApi.readForCaseWorker(SOME_AUTHORIZATION_TOKEN,
            SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,
            caseType, String.valueOf(CASE_ID));

        assertNotNull(caseDetailsReponse);

        assertCaseDetails(caseDetailsReponse, false, false);

    }

}
