//package uk.gov.hmcts.probate.functional.dataextract;
//
//import net.serenitybdd.junit.runners.SerenityRunner;
//import net.serenitybdd.rest.SerenityRest;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Value;
//import uk.gov.hmcts.probate.functional.IntegrationTestBase;
//
//@RunWith(SerenityRunner.class)
//public class DataExtractTests extends IntegrationTestBase {
//    private static final String IRONMOUNTAIN_URL = "/data-extract/iron-mountain";
//    private static final String EXCELA_URL = "/data-extract/excela";
//
//    @Value("${probate.caseworker.email}")
//    private String email;
//
//    @Value("${probate.caseworker.password}")
//    private String password;
//
//    @Value("${probate.caseworker.id}")
//    private Integer id;
//
//    @Test
//    public void verifyValidRequestReturnsOkStatusForIronMountain() {
//        SerenityRest.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
//                password, id))
//                .when()
//                .post(IRONMOUNTAIN_URL)
//                .then().assertThat().statusCode(200);
//    }
//
//    @Test
//    public void verifyValidDateRequestReturnsOkStatusForIronMountain() {
//        SerenityRest.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
//                password, id))
//                .when()
//                .post(IRONMOUNTAIN_URL + "/2019-02-03")
//                .then().assertThat().statusCode(200);
//    }
//
//    @Test
//    public void verifyIncorrectDateFormatReturnsBadRequestForIronMountain() {
//        SerenityRest.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
//                password, id))
//                .when()
//                .post(IRONMOUNTAIN_URL + "/2019-2-2")
//                .then().assertThat().statusCode(400);
//    }
//
//    @Test
//    public void verifyValidRequestReturnsOkStatusForExcela() {
//        SerenityRest.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
//                password, id))
//                .when()
//                .post(EXCELA_URL)
//                .then().assertThat().statusCode(200);
//    }
//
//    @Test
//    public void verifyValidDateRequestReturnsOkStatusForExcela() {
//        SerenityRest.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
//                password, id))
//                .when()
//                .post(EXCELA_URL + "/2019-02-03")
//                .then().assertThat().statusCode(200);
//    }
//
//    @Test
//    public void verifyIncorrectDateFormatReturnsBadRequestForExcela() {
//        SerenityRest.given().relaxedHTTPSValidation().headers(utils.getHeaders(email,
//                password, id))
//                .when()
//                .post(EXCELA_URL + "/2019-2-2")
//                .then().assertThat().statusCode(400);
//    }
//}
