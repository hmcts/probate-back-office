package uk.gov.hmcts.probate.functional.documents;

import io.restassured.path.json.JsonPath;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP;

@RunWith(SpringIntegrationSerenityRunner.class)
public class CoversheetGenerationTests extends DocumentGenerationTestBase {
    
    public static final String VALIDATE_PROBATE_URL = "/case/sols-validate-probate";
    public static final String VALIDATE_INTESTACY_URL = "/case/sols-validate-intestacy";
    public static final String VALIDATE_ADMON_URL = "/case/sols-validate-admon";

    @Test
    public void verifyGenerateSolsCoverSheetGopRenouncingExecutors() {
        String payload = "/caseprogress/04a-caseCreated.json";
        String response = getDocumentTextAtPath(payload, VALIDATE_PROBATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogress/expectedDocumentText/04a-caseCreatedRenouncingExecutors");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetGopWillHasCodicils() {
        String payload = "/caseprogress/04b-caseCreated.json";
        String response = getDocumentTextAtPath(payload, VALIDATE_PROBATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogress/expectedDocumentText/04b-caseCreatedWillHasCodicils");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetGopIht217() {
        String payload = "/caseprogress/04c-caseCreated.json";
        String response = getDocumentTextAtPath(payload, VALIDATE_PROBATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogress/expectedDocumentText/04c-caseCreatedIHT217");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetGopPA17Form() {
        String payload = "/caseprogress/04d-caseCreated.json";
        String response = getDocumentTextAtPath(payload, VALIDATE_PROBATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogress/expectedDocumentText/04d-caseCreatedPA17");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetIntestacy() {
        String payload = "/caseprogressintestacy/04-caseCreated.json";
        String response = getDocumentTextAtPath(payload, VALIDATE_INTESTACY_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogressintestacy/expectedDocumentText/04-caseCreated");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetIntestacyPA16Form() {
        String payload = "/caseprogressintestacy/04b-caseCreated.json";
        String response = getDocumentTextAtPath(payload, VALIDATE_INTESTACY_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogressintestacy/expectedDocumentText/04b-caseCreated");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetAdmonWill() {
        String payload = "/caseprogressadmonwill/04-caseCreated.json";
        String response = getDocumentTextAtPath(payload, VALIDATE_ADMON_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogressadmonwill/expectedDocumentText/04-caseCreated");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsGopExpectedEstatesBeforeSwitchDate() {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesBeforeSwitchDate
        String dir = "/exceptedEstates/ihtEstateBeforeSwitchDate/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, VALIDATE_PROBATE_URL);
        String caseProgressExpectedText = utils.getJsonFromFile(dir + "expectedCaseProgress.txt");
        assertEquals(caseProgressExpectedText, jsonPath.get("data.taskList"));

        String coversheetText = getDocumentText(jsonPath, "solsCoversheetDocument");
        String coversheetExpectedText = utils.getJsonFromFile(dir + "expectedCoversheet.txt");
        assertEquals(coversheetExpectedText, coversheetText);

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");
        assertEquals(legalStatementExpectedText, legalStatementText);
    }

    @Test
    public void verifyGenerateSolsGopExpectedEstatesNo() {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesNo
        String dir = "/exceptedEstates/ihtEstateCompletedNo/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, VALIDATE_PROBATE_URL);
        String caseProgressExpectedText = utils.getJsonFromFile(dir + "expectedCaseProgress.txt");
        assertEquals(caseProgressExpectedText, jsonPath.get("data.taskList"));

        String coversheetText = getDocumentText(jsonPath, "solsCoversheetDocument");
        String coversheetExpectedText = utils.getJsonFromFile(dir + "expectedCoversheet.txt");
        assertEquals(coversheetExpectedText, coversheetText);

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");
        assertEquals(legalStatementExpectedText, legalStatementText);
    }

    @Test
    public void verifyGenerateSolsGopExpectedEstatesCompletedYes207() {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesCompletedYes207
        String dir = "/exceptedEstates/ihtEstateCompletedYes207/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, VALIDATE_PROBATE_URL);
        String caseProgressExpectedText = utils.getJsonFromFile(dir + "expectedCaseProgress.txt");
        assertEquals(caseProgressExpectedText, jsonPath.get("data.taskList"));

        String coversheetText = getDocumentText(jsonPath, "solsCoversheetDocument");
        String coversheetExpectedText = utils.getJsonFromFile(dir + "expectedCoversheet.txt");
        assertEquals(coversheetExpectedText, coversheetText);

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");
        assertEquals(legalStatementExpectedText, legalStatementText);
    }

    @Test
    public void verifyGenerateSolsGopExpectedEstatesCompletedYes400421() {
        //confirmation page for this at SolCcd....verifyGenerateSolsGopExpectedEstatesCompletedYes400421
        String dir = "/exceptedEstates/ihtEstateCompletedYes400421/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, VALIDATE_PROBATE_URL);
        String caseProgressExpectedText = utils.getJsonFromFile(dir + "expectedCaseProgress.txt");
        assertEquals(caseProgressExpectedText, jsonPath.get("data.taskList"));

        String coversheetText = getDocumentText(jsonPath, "solsCoversheetDocument");
        String coversheetExpectedText = utils.getJsonFromFile(dir + "expectedCoversheet.txt");
        assertEquals(coversheetExpectedText, coversheetText);

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");
        assertEquals(legalStatementExpectedText, legalStatementText);
    }

    @Test
    public void verifyGenerateSolsCoverSheetGopTcResolutionLodgedWithinApplication() {
        String payload = "/caseprogress/04e-caseCreated.json";
        String response = getDocumentTextAtPath(payload, VALIDATE_PROBATE_URL, "solsCoversheetDocument");
        assertTrue(response.contains(TC_RESOLUTION_LODGED_WITH_APP));

    }
}
