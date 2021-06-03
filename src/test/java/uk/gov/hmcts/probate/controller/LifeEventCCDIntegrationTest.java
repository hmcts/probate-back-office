package uk.gov.hmcts.probate.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.util.TestUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource("classpath:LifeEventCCDIntegrationTest.properties")
@AutoConfigureMockMvc
public class LifeEventCCDIntegrationTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @MockBean
    private AppInsights appInsights;
    @Autowired
    private WebApplicationContext webApplicationContext;


    @BeforeClass
    public static void start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(9400));
        wireMockServer.start();
    }

    @AfterClass
    public static void shutDown() {
        wireMockServer.stop();
    }

    @Before
    public void setup() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        wireMockServer.stubFor(post(urlEqualTo("/token"))
            .willReturn(okJson("{ \"access_token\": \"dummyToken\"}")));

        wireMockServer.stubFor(post(urlEqualTo("/idam/lease"))
            .willReturn(ok("idamToken")));

        wireMockServer.stubFor(get(urlPathMatching("/ccd/citizens/jurisdictions/.*"))
            .willReturn(okJson("{ \"token\": \"dummyCcdToken\"}")));

        wireMockServer.stubFor(post(urlPathMatching("/ccd/citizens/.*"))
            .willReturn(aResponse()
                .withStatus(200)
            )
        );
    }

    @After
    public void reset() {
        wireMockServer.resetAll();
    }

    @Test
    public void shouldUpdateCCDIfSingleRecordReturned() throws Exception {
        wireMockServer.stubFor(get(urlPathMatching("/api/.*"))
            .willReturn(okJson(
                "[\n"
                    + "    {\n"
                    + "        \"id\": 500035096,\n"
                    + "        \"deceased\": {\n"
                    + "            \"forenames\": \"John TEST\",\n"
                    + "            \"surname\": \"COOK\",\n"
                    + "            \"dateOfBirth\": \"1901-01-01\",\n"
                    + "            \"dateOfDeath\": \"2006-11-16\"\n"
                    + "        }\n"
                    + "    }\n"
                    + "]"))
        );

        postPayloadToLifeEventEndpoint();

        await()
            .atMost(2, SECONDS)
            .untilAsserted(() ->
                wireMockServer.verify(postRequestedFor(urlEqualTo(
                    "/ccd/citizens/jurisdictions/PROBATE/case-types/GrantOfRepresentation"
                        + "/cases/1621002468661478/events?ignore-warning=false"))));


        wireMockServer.verify(getRequestedFor(urlEqualTo(
            "/ccd/citizens/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/1621002468661478/event"
                + "-triggers/deathRecordVerified/token")));
    }

    @Test
    public void shouldUpdateCCDWithDeathRecordVerificationFailedNoRecordsReturned() throws Exception {
        wireMockServer.stubFor(get(urlPathMatching("/api/.*"))
            .willReturn(okJson(
                "[]"))
        );

        postPayloadToLifeEventEndpoint();

        await()
            .atMost(2, SECONDS)
            .untilAsserted(() ->
                wireMockServer.verify(postRequestedFor(urlEqualTo(
                    "/ccd/citizens/jurisdictions/PROBATE/case-types/GrantOfRepresentation"
                        + "/cases/1621002468661478/events?ignore-warning=false"))));

        wireMockServer.verify(getRequestedFor(urlEqualTo(
            "/ccd/citizens/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/1621002468661478/event"
                + "-triggers/deathRecordVerificationFailed/token")));

    }

    @Test
    public void shouldNotUpdateCCDIfMultipleRecordsReturned() throws Exception {

        wireMockServer.stubFor(get(urlPathMatching("/api/.*"))
            .willReturn(okJson(
                "[\n"
                    + "    {\n"
                    + "        \"id\": 500035096,\n"
                    + "        \"deceased\": {\n"
                    + "            \"forenames\": \"John TEST\",\n"
                    + "            \"surname\": \"COOK\",\n"
                    + "            \"dateOfBirth\": \"1901-01-01\",\n"
                    + "            \"dateOfDeath\": \"2006-11-16\"\n"
                    + "        }\n"
                    + "    },\n"
                    + "    {\n"
                    + "        \"id\": 500035096,\n"
                    + "        \"deceased\": {\n"
                    + "            \"forenames\": \"John Another TEST\",\n"
                    + "            \"surname\": \"COOK\",\n"
                    + "            \"dateOfBirth\": \"1901-01-01\",\n"
                    + "            \"dateOfDeath\": \"2006-11-16\"\n"
                    + "        }\n"
                    + "    }\n"
                    + "]"))
        );

        postPayloadToLifeEventEndpoint();

        await().during(2, SECONDS)
            .untilAsserted(() ->
                wireMockServer.verify(0, postRequestedFor(
                    urlEqualTo("/ccd/citizens/jurisdictions/PROBATE/case-types/GrantOfRepresentation"
                        + "/cases/1621002468661478/events?ignore-warning=false"))));

    }

    private void postPayloadToLifeEventEndpoint() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .post("/lifeevent/update")
            .content(testUtils.getStringFromFile("lifeEventPayload.json"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
