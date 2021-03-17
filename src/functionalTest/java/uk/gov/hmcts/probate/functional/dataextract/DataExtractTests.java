package uk.gov.hmcts.probate.functional.dataextract;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringIntegrationSerenityRunner.class)
public class DataExtractTests extends IntegrationTestBase {
    private static final String IRONMOUNTAIN_URL = "/data-extract/iron-mountain";
    private static final String EXCELA_URL = "/data-extract/exela";
    private static final String HMRC_URL = "/data-extract/hmrc";

    private static final String HMRC_DATA_EXTRACT_COMPLETION_MESSAGE = "Perform HMRC data extract finished";

    @Value("${probate.caseworker.email}")
    private String email;

    @Value("${probate.caseworker.password}")
    private String password;

    @Value("${probate.caseworker.id}")
    private Integer id;

    @Test
    public void verifyValidDateRequestReturnsAcceptedStatusForIronMountain() {
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id)).queryParam("date", "2019-02-03")
                .when()
                .post(IRONMOUNTAIN_URL)
                .then().assertThat().statusCode(202);
    }

    @Test
    public void verifyValidDateRequestReturnsAcceptedStatusForHMRC() {
       String response =  RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id))
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
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id))
                .when()
                .post(HMRC_URL)
                .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyIncorrectDateFormatReturnsBadResponseForHMRC() {
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id))
                .when()
                .queryParam("fromDate", "03-13-2019")
                .queryParam("toDate", "2019-03-13")
                .post(HMRC_URL)
                .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyNoDateFormatReturnsBadRequestForIronMountain() {
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id))
                .when()
                .post(IRONMOUNTAIN_URL)
                .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyIncorrectDateFormatReturnsBadRequestForIronMountain() {
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id)).queryParam("date", "2019-2-2")
                .when()
                .post(IRONMOUNTAIN_URL)
                .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyValidDateRequestReturnsAcceptedStatusForExcela() {
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id)).queryParam("date", "2019-02-03")
                .when()
                .post(EXCELA_URL)
                .then().assertThat().statusCode(202);
    }

    @Test
    public void verifyNoDateFormatReturnsBadRequestForExcela() {
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id))
                .when()
                .post(EXCELA_URL)
                .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyIncorrectDateFormatReturnsBadRequestForExcela() {
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id)).queryParam("date", "2019-2-2")
                .when()
                .post(EXCELA_URL)
                .then().assertThat().statusCode(400);
    }

}
