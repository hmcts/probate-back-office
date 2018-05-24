package uk.gov.hmcts.probate;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = SmokeTestConfiguration.class)
public class SmokeTests {

    @Value("${test.instance.uri}")
    private String url;

    @Value("${git.commit.id}")
    private String gitCommitIdFull;

    @Value("${git.commit.time}")
    private String gitCommitTime;

    private RestAssuredConfig config;

    @Before
    public void setUp() {
        RestAssured.useRelaxedHTTPSValidation();
        config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 60000)
                        .setParam("http.socket.timeout", 60000)
                        .setParam("http.connection-manager.timeout", 60000));
    }

    @Test
    public void shouldGetOkStatusFromHealthEndpointForSolCcdService() {
        given().config(config)
                .when()
                .get(url + "/health")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void shouldGetOkStatusFromInfoEndpointForSolCcdService() {
        given().config(config)
                .when()
                .get(url + "/info")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void shouldReturnGitCommitIdInfoForSolCcdService() {

        String bodyAsString = getJsonString();
        Assert.assertTrue(bodyAsString.contains(gitCommitIdFull));
    }

    @Test
    public void shouldReturnGitCommitTimeInfoForSolCcdService() {

        String bodyAsString = getJsonString();
        Assert.assertTrue(bodyAsString.contains(gitCommitTime));
    }

    private String getJsonString() {
        RestAssured.baseURI = url;
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.get("/info");
        ResponseBody body = response.getBody();
        return body.asString();
    }

}
