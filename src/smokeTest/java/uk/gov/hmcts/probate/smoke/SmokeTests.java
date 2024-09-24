package uk.gov.hmcts.probate.smoke;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsNull.notNullValue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SmokeTestConfiguration.class})
public class SmokeTests {

    @Value("${test.instance.uri}")
    private String url;

    private RestAssuredConfig config;

    @BeforeEach
    public void setUp() {
        RestAssured.useRelaxedHTTPSValidation();
        config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 60000)
                        .setParam("http.socket.timeout", 60000)
                        .setParam("http.connection-manager.timeout", 60000));
    }

    @Test
    void shouldGetOkStatusFromHealthEndpointForSolCcdService() {
        given().config(config)
                .when()
                .get(url + "/health")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldGetOkStatusFromInfoEndpointForSolCcdService() {
        given().config(config)
                .when()
                .get(url + "/info")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("git.commit.id", notNullValue())
                .body("git.commit.time", notNullValue());
    }
}
