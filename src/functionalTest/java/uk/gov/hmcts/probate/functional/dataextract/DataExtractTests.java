package uk.gov.hmcts.probate.functional.dataextract;

import io.restassured.RestAssured;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

@RunWith(SpringIntegrationSerenityRunner.class)
public class DataExtractTests extends IntegrationTestBase {
    private static final String IRONMOUNTAIN_URL = "/data-extract/iron-mountain";
    private static final String EXELA_URL = "/data-extract/exela";
    private static final String SMEE_AND_FORD_URL = "/data-extract/smee-and-ford";
    private static final String HMRC_URL = "/data-extract/hmrc";

    private static final String HMRC_DATA_EXTRACT_COMPLETION_MESSAGE = "Perform HMRC data extract finished";

    @Value("${probate.caseworker.email}")
    private String email;

    @Value("${probate.caseworker.password}")
    private String password;

    @Value("${probate.caseworker.id}")
    private Integer id;

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void verifyValidDateRequestReturnsAcceptedStatusForIronMountain() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email,
                password, id)).queryParam("date", "2019-02-03")
            .when()
            .post(IRONMOUNTAIN_URL)
            .then().assertThat().statusCode(202);
    }

    @Test
    public void verifyValidDateRequestReturnsAcceptedStatusForHMRC() {
        final String response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email, password, id))
            .when()
            .queryParam("fromDate", "2019-03-13")
            .queryParam("toDate", "2019-03-13")
            .post(HMRC_URL)
            .then().assertThat().statusCode(202)
            .extract().response().getBody().prettyPrint();

        Assert.assertEquals(HMRC_DATA_EXTRACT_COMPLETION_MESSAGE, response);

    }

    @Test
    public void verifyNoDateFormatReturnsBadResponseForHMRC() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id))
            .when()
            .post(HMRC_URL)
            .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyIncorrectDateFormatReturnsBadResponseForHMRC() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email,
                password, id))
            .when()
            .queryParam("fromDate", "03-13-2019")
            .queryParam("toDate", "2019-03-13")
            .post(HMRC_URL)
            .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyNoDateFormatReturnsBadRequestForIronMountain() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email, password, id))
            .when()
            .post(IRONMOUNTAIN_URL)
            .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyIncorrectDateFormatReturnsBadRequestForIronMountain() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email, password, id))
            .queryParam("date", "2019-2-2")
            .when()
            .post(IRONMOUNTAIN_URL)
            .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyValidDateRequestReturnsAcceptedStatusForExela() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email, password, id))
            .queryParam("fromDate", "2019-02-03")
            .queryParam("toDate", "2019-02-03")
            .when()
            .post(EXELA_URL)
            .then().assertThat().statusCode(202);
    }

    @Test
    public void verifyNoDateFormatReturnsBadRequestForExela() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email, password, id))
            .when()
            .post(EXELA_URL)
            .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyIncorrectDateFormatReturnsBadRequestForExela() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email, password, id))
            .queryParam("date", "2019-2-2")
            .when()
            .post(EXELA_URL)
            .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyValidDateRequestReturnsAcceptedStatusForSmeeAndFord() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email, password, id))
            .queryParam("fromDate", "2019-02-03")
            .queryParam("toDate", "2019-02-03")
            .when()
            .post(SMEE_AND_FORD_URL)
            .then().assertThat().statusCode(202);
    }
}
