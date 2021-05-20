package uk.gov.hmcts.probate.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.util.TestUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "core_case_data.api.url=localhost:9400/ccd",
        "idam.s2s-auth.url=http://localhost:9400/idam"
})
@AutoConfigureMockMvc
public class LifeEventCCDIntegrationTest {

    private static WireMockServer wireMockServer;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9400);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @MockBean
    private AppInsights appInsights;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Captor
    private ArgumentCaptor<CaseDetails> caseDetailsArgumentCaptor;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        wireMockRule.stubFor(post(urlEqualTo("/token"))
                .willReturn(okJson("{ \"access_token\": \"dummyToken\"}")));
        
        wireMockRule.stubFor(post(urlEqualTo("/idam/lease"))
                .willReturn(ok("idamToken")));
        
        wireMockRule.stubFor(get(urlPathMatching("/ccd/citizens/jurisdictions/.*"))
                .willReturn(okJson("{ \"token\": \"dummyCcdToken\"}")));

        wireMockRule.stubFor(post(urlPathMatching("/ccd/citizens/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                )
        );
    }


    @Test
    public void shouldUpdateCCDIfSingleRecordReturned() throws Exception {

        wireMockRule.stubFor(get(urlPathMatching("/api/.*"))
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

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/lifeevent/update")
                .content(testUtils.getStringFromFile("lifeEventPayload.json"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        await()
                .atMost(2, SECONDS)
                .untilAsserted(() ->
                        verify(postRequestedFor(urlEqualTo(
                                "/ccd/citizens/jurisdictions/PROBATE/case-types/GrantOfRepresentation"
                                       + "/cases/1621002468661478/events?ignore-warning=false"))));

    }

    @Test
    public void shouldNotUpdateCCDIfNoRecordsReturned() throws Exception {

        wireMockRule.stubFor(get(urlPathMatching("/api/.*"))
                .willReturn(okJson(
                        "[]"))
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/lifeevent/update")
                .content(testUtils.getStringFromFile("lifeEventPayload.json"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        await().during(2, SECONDS)
                .untilAsserted(() ->
                        verify(0, postRequestedFor(urlEqualTo(
                                "/ccd/citizens/jurisdictions/PROBATE/case-types/GrantOfRepresentation/"
                                       + "cases/1621002468661478/events?ignore-warning=false"))));

    }

    @Test
    public void shouldNotUpdateCCDIfMultipleRecordsReturned() throws Exception {

        wireMockRule.stubFor(get(urlPathMatching("/api/.*"))
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

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/lifeevent/update")
                .content(testUtils.getStringFromFile("lifeEventPayload.json"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        await().during(2, SECONDS)
                .untilAsserted(() ->
                        verify(0, postRequestedFor(
                                urlEqualTo("/ccd/citizens/jurisdictions/PROBATE/case-types/GrantOfRepresentation"
                                       + "/cases/1621002468661478/events?ignore-warning=false"))));

    }
}
