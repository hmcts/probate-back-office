package uk.gov.hmcts.probate.functional.dataextract;

import io.restassured.RestAssured;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.Pending;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

@RunWith(SpringIntegrationSerenityRunner.class)
public class DataExtractTests extends IntegrationTestBase {
    private static final String IRONMOUNTAIN_URL = "/data-extract/iron-mountain";
    private static final String EXCELA_URL = "/data-extract/exela";

    @Value("${probate.caseworker.email}")
    private String email;

    @Value("${probate.caseworker.password}")
    private String password;

    @Value("${probate.caseworker.id}")
    private Integer id;

    @Test
    @Pending
    public void verifyValidDateRequestReturnsOkStatusForIronMountain() {
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id)).queryParam("date", "2019-02-03")
                .when()
                .post(IRONMOUNTAIN_URL)
                .then().assertThat().statusCode(200);
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
    @Pending
    public void verifyValidDateRequestReturnsOkStatusForExcela() {
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id)).queryParam("date", "2019-02-03")
                .when()
                .post(EXCELA_URL)
                .then().assertThat().statusCode(200);
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
