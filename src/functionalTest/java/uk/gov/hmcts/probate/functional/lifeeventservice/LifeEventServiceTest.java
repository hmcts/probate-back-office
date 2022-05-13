package uk.gov.hmcts.probate.functional.lifeeventservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.List;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringIntegrationSerenityRunner.class)
public class LifeEventServiceTest extends IntegrationTestBase {

    @Test
    public void shouldReturn200() throws IOException {
        final String jsonFromFile = utils.getJsonFromFile("caseprogress/01-appCreatedSolDtls.json");
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(jsonFromFile)
                .when().post("/lifeevent/update");

        assertEquals(200, response.getStatusCode());
    }

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


