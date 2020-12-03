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
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.hmcts.probate.service.consumer.util.PactDslFixtureHelper;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.probate.service.consumer.util.AssertionHelper.assertCaseDetails;
import static uk.gov.hmcts.probate.service.consumer.util.PactDslFixtureHelper.getCaseDataContent;
import static uk.gov.hmcts.reform.probate.pact.dsl.ObjectMapperTestUtil.convertObjectToJsonString;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildCaseDetailsDsl;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProbateBackofficeSubmitEventForCaseworker extends AbstractBackOfficePact {

    @Rule
    public PactHttpsProviderRuleMk2 provider = new PactHttpsProviderRuleMk2("ccdDataStoreAPI_Cases", "localhost", 4452, this);

    public static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    private Map<String, Object> caseDetailsMap;
    private CaseDataContent caseDataContent;
    private static final String USER_ID = "123456";
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

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
        Thread.sleep(2000);
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

    @After
    public void  teardown() {
        Executor.closeIdleConnections();
    }


    @Pact(consumer = "probate_backOfficeService")
    RequestResponsePact submitForCaseWorkerFragment(PactDslWithProvider builder) throws Exception {
        return builder
                .given("A Submit for Caseworker is received")
                .uponReceiving("A Submit For Caseworker is received.")
                .path("/caseworkers/"   + USER_ID
                        + "/jurisdictions/" + jurisdictionId
                        + "/case-types/"    + caseType
                        + "/cases/"         + CASE_ID
                        + "/events"
                )
                .query("ignore-warning=true")
                .method("POST")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION, SOME_SERVICE_AUTHORIZATION_TOKEN)
                .body(convertObjectToJsonString(getCaseDataContent()))
                .willRespondWith()
                .status(200)
                .body(buildCaseDetailsDsl(100L, "someemailaddress.com", false, false, false))
                .matchHeader(HttpHeaders.CONTENT_TYPE, "\\w+\\/[-+.\\w]+;charset=(utf|UTF)-8")
                .toPact();
    }

    @Test
    @PactVerification(fragment = "submitForCaseWorkerFragment")
    public void submitForCaseWorker() throws Exception {

        CaseDetails caseDetailsReponse = coreCaseDataApi.submitForCaseworker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId,
                caseType, true, caseDataContent);

        assertNotNull(caseDetailsReponse);
        //assertNotNull(caseDetailsReponse.getCaseTypeId());
        //assertEquals(caseDetailsReponse.getJurisdiction(), "PROBATE");

        //assertCaseDetails(caseDetailsReponse,false,false);

    }



}
