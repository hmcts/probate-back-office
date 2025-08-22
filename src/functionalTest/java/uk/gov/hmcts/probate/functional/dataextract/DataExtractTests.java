package uk.gov.hmcts.probate.functional.dataextract;

import java.math.BigDecimal;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.model.ccd.raw.BigDecimalSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.LocalDateTimeSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SerenityJUnit5Extension.class)
public class DataExtractTests extends IntegrationTestBase {
    private static final String IRONMOUNTAIN_URL = "/data-extract/iron-mountain";
    private static final String RESEND_IRONMOUNTAIN_URL = "/data-extract/resend-iron-mountain";
    private static final String EXELA_URL = "/data-extract/exela";
    private static final String SMEE_AND_FORD_URL = "/data-extract/smee-and-ford";
    private static final String HMRC_URL = "/data-extract/hmrc";
    private static final String NFI_URL = "/data-extract/nfi";

    private static final String HMRC_DATA_EXTRACT_COMPLETION_MESSAGE = "Perform HMRC data extract finished";

    @Value("${probate.caseworker.email}")
    private String email;

    @Value("${probate.caseworker.password}")
    private String password;

    @Value("${probate.caseworker.id}")
    private Integer id;

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void verifyValidDateRequestReturnsAcceptedStatusForIronMountain() {
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
    void verifyValidDateRequestReturnsAcceptedStatusForResendIronMountain() throws JsonProcessingException {

        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(new LocalDateTimeSerializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        objectMapper.registerModule(javaTimeModule);

        CaseDetails caseDetails = new CaseDetails(CaseData.builder()
            .resendDate("2023-01-01").registryLocation("bristol").build(), null, null);
        CallbackRequest request = new CallbackRequest(caseDetails);

        String bodyText = objectMapper.writeValueAsString(request);

        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email,
                password, id))
            .body(bodyText)
            .when()
            .post(RESEND_IRONMOUNTAIN_URL)
            .then().assertThat().statusCode(200);
    }

    @Test
    void verifyValidDateRequestReturnsAcceptedStatusForHMRC() {
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

        assertEquals(HMRC_DATA_EXTRACT_COMPLETION_MESSAGE, response);

    }

    @Test
    void verifyNoDateFormatReturnsBadResponseForHMRC() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation().headers(utils.getHeaders(email,
                password, id))
            .when()
            .post(HMRC_URL)
            .then().assertThat().statusCode(400);
    }

    @Test
    void verifyIncorrectDateFormatReturnsBadResponseForHMRC() {
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
    void verifyNoDateFormatReturnsBadRequestForIronMountain() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email, password, id))
            .when()
            .post(IRONMOUNTAIN_URL)
            .then().assertThat().statusCode(400);
    }

    @Test
    void verifyIncorrectDateFormatReturnsBadRequestForIronMountain() {
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
    void verifyValidDateRequestReturnsAcceptedStatusForExela() {
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
    void verifyNoDateFormatReturnsBadRequestForExela() {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders(email, password, id))
            .when()
            .post(EXELA_URL)
            .then().assertThat().statusCode(400);
    }

    @Test
    void verifyIncorrectDateFormatReturnsBadRequestForExela() {
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
    void verifyValidDateRequestReturnsAcceptedStatusForSmeeAndFord() {
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

    @Test
    void verifyValidDateRequestReturnsAcceptedStatusForNFI() {
        RestAssured.given()
                .config(config)
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders(email, password, id))
                .queryParam("fromDate", "2019-02-03")
                .queryParam("toDate", "2019-02-03")
                .when()
                .post(NFI_URL)
                .then().assertThat().statusCode(202);
    }
}
