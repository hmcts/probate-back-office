package uk.gov.hmcts.probate.functional.documents;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SerenityJUnit5Extension.class)
public class ExceptedEstateDocumentGenerationTests  extends DocumentGenerationTestBase {
    public static final String VALIDATE_PROBATE_URL = "/case/sols-validate-probate";

    @Test
    void verifyGenerateSolsGopExpectedEstatesBeforeSwitchDate() throws IOException {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesBeforeSwitchDate
        String dir = "/exceptedEstates/ihtEstateBeforeSwitchDate/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, VALIDATE_PROBATE_URL);
        String caseProgressExpectedText = utils.getJsonFromFile(dir + "expectedCaseProgress.txt");
        assertEquals(caseProgressExpectedText, jsonPath.get("data.taskList"));

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");

        assertEquals(legalStatementExpectedText, legalStatementText);

    }

    @Test
    void verifyGenerateSolsGopExpectedEstatesNo() throws IOException {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesNo
        String dir = "/exceptedEstates/ihtEstateCompletedNo/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, VALIDATE_PROBATE_URL);
        String caseProgressExpectedText = utils.getJsonFromFile(dir + "expectedCaseProgress.txt");
        assertEquals(caseProgressExpectedText, jsonPath.get("data.taskList"));

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");
        assertEquals(legalStatementExpectedText, legalStatementText);

    }

    @Test
    void verifyGenerateSolsGopExpectedEstatesCompletedYes207() throws IOException {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesCompletedYes207
        String dir = "/exceptedEstates/ihtEstateCompletedYes207/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, VALIDATE_PROBATE_URL);
        String caseProgressExpectedText = utils.getJsonFromFile(dir + "expectedCaseProgress.txt");
        assertEquals(caseProgressExpectedText, jsonPath.get("data.taskList"));

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");

        assertEquals(legalStatementExpectedText, legalStatementText);
    }


    @Test
    void verifyGenerateSolsGopExpectedEstatesCompletedYes400421() throws IOException {
        //confirmation page for this at SolCcd....verifyGenerateSolsGopExpectedEstatesCompletedYes400421
        String dir = "/exceptedEstates/ihtEstateCompletedYes400421/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, VALIDATE_PROBATE_URL);
        String caseProgressExpectedText = utils.getJsonFromFile(dir + "expectedCaseProgress.txt");
        assertEquals(caseProgressExpectedText, jsonPath.get("data.taskList"));

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");

        assertEquals(legalStatementExpectedText, legalStatementText);
    }

    protected JsonPath postAndGetJsonPathResponse(String jsonFileName, String path) throws IOException {

        final Response jsonResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path).andReturn();

        jsonResponse.then().assertThat().statusCode(200);

        return JsonPath.from(jsonResponse.getBody().asString());
    }
}
