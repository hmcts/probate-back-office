package uk.gov.hmcts.probate.functional.documents;

import io.restassured.path.json.JsonPath;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP;

@RunWith(SpringIntegrationSerenityRunner.class)
public class CoversheetGenerationTests extends DocumentGenerationTestBase {

    public static final String NEXTSTEPS_VALIDATE_URL = "/nextsteps/validate";

    @Test
    public void verifyGenerateSolsCoverSheetGopRenouncingExecutors() throws IOException {
        String payload = "/coversheet/caseprogress/04a-caseCreated.json";
        String response = getDocumentTextAtPath(payload, NEXTSTEPS_VALIDATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/coversheet/caseprogress/expectedDocumentText/04a-caseCreatedRenouncingExecutors");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetGopWillHasCodicils() throws IOException {
        String payload = "/coversheet/caseprogress/04b-caseCreated.json";
        String response = getDocumentTextAtPath(payload, NEXTSTEPS_VALIDATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/coversheet/caseprogress/expectedDocumentText/04b-caseCreatedWillHasCodicils");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetGopIht217() throws IOException {
        String payload = "/coversheet/caseprogress/04c-caseCreated.json";
        String response = getDocumentTextAtPath(payload, NEXTSTEPS_VALIDATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/coversheet/caseprogress/expectedDocumentText/04c-caseCreatedIHT217");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetGopPA17Form() throws IOException {
        String payload = "/coversheet/caseprogress/04d-caseCreated.json";
        String response = getDocumentTextAtPath(payload, NEXTSTEPS_VALIDATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/coversheet/caseprogress/expectedDocumentText/04d-caseCreatedPA17");
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyGenerateSolsCoverSheetIntestacy() throws IOException {
        String payload = "/coversheet/caseprogressintestacy/04-caseCreated.json";
        String response = getDocumentTextAtPath(payload, NEXTSTEPS_VALIDATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/coversheet/caseprogressintestacy/expectedDocumentText/04-caseCreated");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetIntestacyPA16Form() throws IOException {
        String payload = "/coversheet/caseprogressintestacy/04b-caseCreated.json";
        String response = getDocumentTextAtPath(payload, NEXTSTEPS_VALIDATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/coversheet/caseprogressintestacy/expectedDocumentText/04b-caseCreated");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetAdmonWill() throws IOException {
        String payload = "/coversheet/caseprogressadmonwill/04-caseCreated.json";
        String response = getDocumentTextAtPath(payload, NEXTSTEPS_VALIDATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/coversheet/caseprogressadmonwill/expectedDocumentText/04-caseCreated");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesBeforeSwitchDate() throws IOException {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesBeforeSwitchDate
        String dir = "/coversheet/exceptedEstates/ihtEstateBeforeSwitchDate/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, NEXTSTEPS_VALIDATE_URL);

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");
        assertEquals(legalStatementExpectedText, legalStatementText);
    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesBeforeSwitchDate() throws IOException {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesBeforeSwitchDate
        String dir = "/coversheet/exceptedEstates/ihtEstateBeforeSwitchDate/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, NEXTSTEPS_VALIDATE_URL);

        String coversheetText = getDocumentText(jsonPath, "solsCoversheetDocument");
        String coversheetExpectedText = utils.getJsonFromFile(dir + "expectedCoversheet.txt");
        assertEquals(coversheetExpectedText, coversheetText);
    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesNo() throws IOException {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesNo
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedNo/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, NEXTSTEPS_VALIDATE_URL);

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");
        assertEquals(legalStatementExpectedText, legalStatementText);
    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesNo() throws IOException {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesNo
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedNo/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, NEXTSTEPS_VALIDATE_URL);

        String coversheetText = getDocumentText(jsonPath, "solsCoversheetDocument");
        String coversheetExpectedText = utils.getJsonFromFile(dir + "expectedCoversheet.txt");
        assertEquals(coversheetExpectedText, coversheetText);
    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesCompletedYes207() throws IOException {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesCompletedYes207
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedYes207/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, NEXTSTEPS_VALIDATE_URL);

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");
        assertEquals(legalStatementExpectedText, legalStatementText);
    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesCompletedYes207() throws IOException {
        //confirmation page for this at SolCcdServiceNextStepsTests.verifyGenerateSolsGopExpectedEstatesCompletedYes207
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedYes207/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, NEXTSTEPS_VALIDATE_URL);

        String coversheetText = getDocumentText(jsonPath, "solsCoversheetDocument");
        String coversheetExpectedText = utils.getJsonFromFile(dir + "expectedCoversheet.txt");
        assertEquals(coversheetExpectedText, coversheetText);
    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesCompletedYes400421() throws IOException {
        //confirmation page for this at SolCcd....verifyGenerateSolsGopExpectedEstatesCompletedYes400421
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedYes400421/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, NEXTSTEPS_VALIDATE_URL);

        String legalStatementText = getDocumentText(jsonPath, "solsLegalStatementDocument");
        String legalStatementExpectedText = utils.getJsonFromFile(dir + "expectedLegalStatement.txt");
        assertEquals(legalStatementExpectedText, legalStatementText);
    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesCompletedYes400421() throws IOException {
        //confirmation page for this at SolCcd....verifyGenerateSolsGopExpectedEstatesCompletedYes400421
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedYes400421/";
        String payload = dir + "caseCreate.json";
        JsonPath jsonPath = postAndGetJsonPathResponse(payload, NEXTSTEPS_VALIDATE_URL);

        String coversheetText = getDocumentText(jsonPath, "solsCoversheetDocument");
        String coversheetExpectedText = utils.getJsonFromFile(dir + "expectedCoversheet.txt");
        assertEquals(coversheetExpectedText, coversheetText);
    }

    @Test
    public void verifyGenerateSolsCoverSheetGopTcResolutionLodgedWithinApplication() throws IOException {
        String payload = "/coversheet/caseprogress/04e-caseCreated.json";
        String response = getDocumentTextAtPath(payload, NEXTSTEPS_VALIDATE_URL, "solsCoversheetDocument");
        assertTrue(response.contains(TC_RESOLUTION_LODGED_WITH_APP));
    }

    @Test
    public void verifyGenerateSolsCoverSheetGopNotarialWill() throws IOException {
        String payload = "/coversheet/caseprogress/04-caseCreated-notarial.json";
        String response = getDocumentTextAtPath(payload, NEXTSTEPS_VALIDATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/coversheet/caseprogress/expectedDocumentText/04b-caseCreatedWillNotarial");
        assertTrue(response.contains(expectedText));
    }
}
