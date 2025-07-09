package uk.gov.hmcts.probate.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.util.TestUtils;

import java.util.List;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource("classpath:LifeEventCCDIntegrationTest.properties")
@AutoConfigureMockMvc
class LifeEventCCDIntegrationIT {

    private static WireMockServer wireMockServer;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @MockitoBean
    private SecurityUtils securityUtils;
    @Autowired
    private WebApplicationContext webApplicationContext;


    @BeforeAll
    public static void start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(9400));
        wireMockServer.start();
    }

    @AfterAll
    public static void shutDown() {
        wireMockServer.stop();
    }

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        wireMockServer.stubFor(post(urlEqualTo("/token"))
            .willReturn(okJson("{ \"access_token\": \"dummyToken\"}")));

        wireMockServer.stubFor(post(urlEqualTo("/idam/lease"))
            .willReturn(ok("idamToken")));

        wireMockServer.stubFor(get(urlPathMatching("/ccd/citizens//jurisdictions/.*"))
            .willReturn(okJson("{ \"token\": \"dummyCcdToken\"}")));

        wireMockServer.stubFor(post(urlPathMatching("/ccd/citizens//jurisdictions/.*"))
            .willReturn(aResponse()
                .withStatus(200)
            )
        );
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH_TOKEN")
                .serviceAuthorisation("serviceAuth")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityUtils.getRoles(anyString())).thenReturn(List.of("citizen"));
    }

    @AfterEach
    public void reset() {
        wireMockServer.resetAll();
    }


    @Test
    void shouldUpdateCCDIfSingleRecordReturned() throws Exception {
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
                    "/ccd/citizens//jurisdictions/PROBATE/case-types/GrantOfRepresentation"
                        + "/cases/1621002468661478/events?ignore-warning=false"))));


        wireMockServer.verify(getRequestedFor(urlEqualTo(
            "/ccd/citizens//jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/1621002468661478/event"
                + "-triggers/deathRecordVerified/token")));
    }

    @Test
    void shouldUpdateCCDWithDeathRecordVerificationFailedNoRecordsReturned() throws Exception {
        wireMockServer.stubFor(get(urlPathMatching("/api/.*"))
            .willReturn(okJson(
                "[]"))
        );

        postPayloadToLifeEventEndpoint();

        await()
            .atMost(2, SECONDS)
            .untilAsserted(() ->
                wireMockServer.verify(postRequestedFor(urlEqualTo(
                    "/ccd/citizens//jurisdictions/PROBATE/case-types/GrantOfRepresentation"
                        + "/cases/1621002468661478/events?ignore-warning=false"))));

        wireMockServer.verify(getRequestedFor(urlEqualTo(
            "/ccd/citizens//jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/1621002468661478/event"
                + "-triggers/deathRecordVerificationFailed/token")));

    }

    @Test
    void shouldUpdateCCDIfMultipleRecordsReturned() throws Exception {

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

        await()
            .atMost(2, SECONDS)
            .untilAsserted(() ->
                wireMockServer.verify(postRequestedFor(urlEqualTo(
                    "/ccd/citizens//jurisdictions/PROBATE/case-types/GrantOfRepresentation"
                        + "/cases/1621002468661478/events?ignore-warning=false"))));

        wireMockServer.verify(getRequestedFor(urlEqualTo(
            "/ccd/citizens//jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/1621002468661478/event"
                + "-triggers/deathRecordVerificationFailed/token")));

    }

    private void postPayloadToLifeEventEndpoint() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .post("/lifeevent/update")
            .content(testUtils.getStringFromFile("lifeEventPayload.json"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
