package uk.gov.hmcts.probate;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;

import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
public class SmokeTests {

    @Autowired
    private PDFServiceConfiguration pdfServiceConfiguration;

    @Autowired
    private FeeServiceConfiguration feeServiceConfiguration;

    @Value("${idam.service.host}")
    private String idamServiceHost;

    @Value("${idam.user.host}")
    private String idamUserHost;

    @Value("${evidence.management.host}")
    private String evidenceManagementHost;

    @Value("${printservice.host}")
    private String printServiceHost;

    @Value("${probate.sol.ccd.service.url}")
    private String solCcdServiceUrl;

    private RestAssuredConfig config;

    @Before
    public void setUp() {
        config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 20000)
                        .setParam("http.socket.timeout", 20000)
                        .setParam("http.connection-manager.timeout", 20000));
    }

    @Test
    public void shouldGetOkStatusFromHealthEndpointForPdfService() {
        checkHealthEndpoint(pdfServiceConfiguration.getUrl());
    }

    @Test
    public void shouldGetOkStatusFromHealthEndpointForFeeService() {
        checkHealthEndpoint(feeServiceConfiguration.getUrl());
    }

    @Test
    public void shouldGetOkStatusFromHealthEndpointForIdamServiceHost() {
        checkHealthEndpoint(idamServiceHost);
    }

    @Test
    public void shouldGetOkStatusFromHealthEndpointForIdamUserHost() {
        checkHealthEndpoint(idamUserHost);
    }
    
    @Test
    public void shouldGetOkStatusFromHealthEndpointForEvidenceManagement() {
        checkHealthEndpoint(evidenceManagementHost);
    }

    @Test
    public void shouldGetOkStatusFromHealthEndpointForPrintService() {
        checkHealthEndpoint(printServiceHost);
    }

    @Test
    public void shouldGetOkStatusFromHealthEndpointForSolCcdService() {
        checkHealthEndpoint(solCcdServiceUrl);
    }

    private void checkHealthEndpoint(String url) {
        given().config(config)
                .when()
                .get(url + "/health")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Configuration
    @ComponentScan("uk.gov.hmcts.probate.config")
    public class Config {

    }
}
