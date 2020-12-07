package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.probate.service.consumer.util.AssertionHelper.assertBackOfficeCaseData;
import static uk.gov.hmcts.probate.service.consumer.util.PactDslFixtureHelper.getCaseDataContent;
import static uk.gov.hmcts.reform.probate.pact.dsl.ObjectMapperTestUtil.convertObjectToJsonString;
import static uk.gov.hmcts.reform.probate.pact.dsl.PactDslBuilderForCaseDetailsList.buildCaseDetailsDsl;

@RunWith(SpringRunner.class)
@SpringBootTest({
    "core_case_data.api.url: localhost:4457"
})
public class ProbateBackofficeSubmitEventForCaseworker extends AbstractBackOfficePact {

    @Rule
    public PactHttpsProviderRuleMk2 provider = new PactHttpsProviderRuleMk2("ccdDataStoreAPI_Cases", "localhost", 4457, this);

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
            .eventToken("Bearer UserAuthToken")
            .event(
                Event.builder()
                    .id(createEventId)
                    .summary("PROBATE")
                    .description("PROBATE DESC")
                    .build()
            ).data(caseDetailsMap.get("case_data"))
            .build();
    }

    @Pact(consumer = "probate_backOfficeService")
    public RequestResponsePact submitEventForCaseWorkerFragment(PactDslWithProvider builder) throws Exception {
        return builder
                .given("A Submit Event for a Caseworker is requested", getCaseDataContentAsMap(caseDataContent))
                .uponReceiving("A Submit Event for a Caseworker")
                .path("/caseworkers/" + USER_ID
                        + "/jurisdictions/" + jurisdictionId
                        + "/case-types/" + caseType
                        + "/cases/" + CASE_ID
                        + "/events"
                )
                .query("ignore-warning=true")
                .method("POST")
                .headers(HttpHeaders.AUTHORIZATION, SOME_AUTHORIZATION_TOKEN, SERVICE_AUTHORIZATION, SOME_SERVICE_AUTHORIZATION_TOKEN)
                .body(convertObjectToJsonString(getCaseDataContent()))
                .matchHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .willRespondWith()
                .status(201)
                .body(buildCaseDetailsDsl(CASE_ID, "email@mailnator.com", false, false,false))
                .toPact();
    }

    @Test
    @PactVerification(fragment = "submitEventForCaseWorkerFragment")
    public void submitEventForCaseWorker() throws Exception {

        caseDataContent = getCaseDataContent();

        final CaseDetails caseDetails = coreCaseDataApi.submitEventForCaseWorker(SOME_AUTHORIZATION_TOKEN,
                SOME_SERVICE_AUTHORIZATION_TOKEN, USER_ID, jurisdictionId, caseType, CASE_ID.toString(), true, caseDataContent);

        assertNotNull(caseDetails);
        assertBackOfficeCaseData(caseDetails);

    }
}