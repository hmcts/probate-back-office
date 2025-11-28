package uk.gov.hmcts.probate.functional.lifeeventservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.List;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SerenityJUnit5Extension.class)
public class LifeEventServiceTest extends IntegrationTestBase {

    @Test
    void shouldReturn200() throws IOException {
        final String jsonFromFile = utils.getJsonFromFile("caseprogress/01-appCreatedSolDtls.json");
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(jsonFromFile)
                .when().post("/lifeevent/update");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void shouldReturn200HandCaseOffToLegacySite() throws IOException {
        final String jsonFromFile = utils.getJsonFromFile("caseprogress/01-appCreatedSolDtls.json");
        Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(jsonFromFile)
            .when().post("/lifeevent/handOffToLegacySite");

        assertEquals(200, response.getStatusCode());
    }

    @Disabled // this test is checking for a record which appears to no longer exist
    @Test
    public void shouldAddDeathRecordWhenManualUpdateAboutToStart() throws IOException {
        final String jsonFromFile = utils.getJsonFromFile("lifeEvent/manualUpdateAboutToStart.json");
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        final CallbackRequest callbackRequest = objectMapper.readValue(jsonFromFile, CallbackRequest.class);
        final CallbackResponse callbackResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(callbackRequest)
                .when().post("/lifeevent/manualUpdateAboutToStart")
                .then()
                .statusCode(200)
                .extract()
                .as(CallbackResponse.class);

        final ResponseCaseData caseData = callbackResponse.getData();
        assertEquals(1, caseData.getDeathRecords().size());
    }

    @Test
    public void shouldReturnErrorManualUpdateAboutToStart() throws IOException {
        final String jsonFromFile = utils.getJsonFromFile("lifeEvent/manualUpdateAboutToStartNonExistent.json");
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        final CallbackRequest callbackRequest = objectMapper.readValue(jsonFromFile, CallbackRequest.class);
        final CallbackResponse callbackResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(callbackRequest)
                .when().post("/lifeevent/manualUpdateAboutToStart")
                .then()
                .statusCode(200)
                .extract()
                .as(CallbackResponse.class);

        final List<String> errors = callbackResponse.getErrors();
        assertEquals(1, errors.size());
        assertEquals("No death records found", errors.get(0));
    }
}


