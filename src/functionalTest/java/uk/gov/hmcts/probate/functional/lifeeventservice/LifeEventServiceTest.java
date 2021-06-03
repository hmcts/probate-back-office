package uk.gov.hmcts.probate.functional.lifeeventservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringIntegrationSerenityRunner.class)
public class LifeEventServiceTest extends IntegrationTestBase {

    @Test
    public void shouldReturn200() {
        final String jsonFromFile = utils.getJsonFromFile("caseprogress/01-appCreated.json");

        Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(jsonFromFile)
            .when().post("/lifeevent/update");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void shouldAddDeathRecord() throws JsonProcessingException {
        final String jsonFromFile = utils.getJsonFromFile("lifeEvent/updateWithValidSystemNumber.json");
        ObjectMapper objectMapper = new ObjectMapper();
        final CallbackRequest callbackRequest = objectMapper.readValue(jsonFromFile, CallbackRequest.class);
        final CallbackResponse callbackResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(callbackRequest)
            .when().post("/lifeevent/updateWithSystemNumber")
            .then()
            .statusCode(200)
            .extract()
            .as(CallbackResponse.class);

        final ResponseCaseData caseData = callbackResponse.getData();
        assertEquals(1, caseData.getDeathRecords().size());
        final CollectionMember<DeathRecord> collectionMember = caseData.getDeathRecords().get(0);
        assertEquals(callbackRequest.getCaseDetails().getData().getDeathRecordSystemNumber(),
            collectionMember.getValue().getSystemNumber());
    }

    @Test
    public void shouldReturnError() throws JsonProcessingException {
        final String jsonFromFile = utils.getJsonFromFile("lifeEvent/updateWithNonExistentSystemNumber.json");
        ObjectMapper objectMapper = new ObjectMapper();
        final CallbackRequest callbackRequest = objectMapper.readValue(jsonFromFile, CallbackRequest.class);
        final CallbackResponse callbackResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(callbackRequest)
            .when().post("/lifeevent/updateWithSystemNumber")
            .then()
            .statusCode(200)
            .extract()
            .as(CallbackResponse.class);

        final List<String> errors = callbackResponse.getErrors();
        assertEquals(1, errors.size());
        final Integer deathRecordSystemNumber = callbackRequest.getCaseDetails().getData().getDeathRecordSystemNumber();
        assertEquals("No death record found with system number " + deathRecordSystemNumber, errors.get(0));
    }
}


