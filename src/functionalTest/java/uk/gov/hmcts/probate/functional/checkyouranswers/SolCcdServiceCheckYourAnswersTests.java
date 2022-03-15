package uk.gov.hmcts.probate.functional.checkyouranswers;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceCheckYourAnswersTests extends IntegrationTestBase {

    private static final String VALIDATE_URL = "/case/sols-validate";
    private static final String VALIDATE_PROBATE_URL = "/case/sols-validate-probate";
    private static final String VALIDATE_INTESTACY_URL = "/case/sols-validate-intestacy";
    private static final String VALIDATE_ADMON_URL = "/case/sols-validate-admon";
    private static final String DOC_NAME = "success.beforeLegalStatement.checkYourAnswersPayload.json";

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void verifyFirstNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("TestPrimaryExecutorFirstName", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyLastNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("TestPrimaryExecutorLastName", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyAddressInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("Test AddressLine1, Test "
                + "AddressLine2, Test AddressLine3, Hounslow, Middlesex, TW3 3DB, United Kingdom", DOC_NAME,
            VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyDeceasedNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("DeceasedFirstName DeceasedLastName", DOC_NAME,
            VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyDeceasedDobInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("01/01/1987", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyDeceasedDodInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("01/01/2018", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyPrimaryExecutorAliasNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("TestPrimaryExecutorAliasName", DOC_NAME,
                VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyLegalStatementAcceptInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement(
            "We confirm that the information we have provided is correct to the best of our knowledge.",
            DOC_NAME,
            VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyLegalStatementSolicitorsDeclarationInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement(
            "The executors believe that all the information stated in the legal statement is true.",
            DOC_NAME,
            VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyDeclarationAcceptInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("They have authorised \nSolicitorFirmName "
                + "to sign a statement of truth on their behalf.",
                DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyIhtGrossInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("1000.01", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyIhtNetInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("900.09", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifySolicitorFirmNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("SolicitorFirmName", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyAdditionalExecutor1NameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("AdditionalExecutor1FirstName AdditionalExecutor1LastName",
            DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyAdditionalExecutor1AliasNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("AdditionalExecutor1 willname", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    public void verifyIncorrectInputReturns400() {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders())
            .body(utils.getJsonFromFile("incorrectInput.checkYourAnswersPayload.json"))
            .when().post(VALIDATE_URL).then().statusCode(400);
    }

    @Test
    public void verifyMissingDeceasedDodReturnsError() {
        validatePostRequestFailureForLegalStatement("\"deceasedDateOfDeath\": \"2018-01-01\"",
            "\"deceasedDateOfDeath\": \"\"", "caseDetails.data.deceasedDateOfDeath", VALIDATE_URL);
    }

    @Test
    public void verifyMissingDeceasedDobReturnsError() {
        validatePostRequestFailureForLegalStatement("\"deceasedDateOfBirth\": \"1987-01-01\"",
            "\"deceasedDateOfBirth\": \"\"", "caseDetails.data.deceasedDateOfBirth", VALIDATE_URL);
    }
    
    @Test
    public void validatePostRequestSuccessCYAForBeforeSignSOT() {
        final Response response = given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("success.beforeSignSOT.checkYourAnswersPayload.json"))
            .when().post("/nextsteps/validate");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void validatePostRequestSolicitorValidateIntestacySuccess() {
        validatePostRequestSuccessForLegalStatement(
            "I authorise Firm Name, as my appointed firm to submit this application on my behalf.",
            "solicitorPDFPayloadIntestacy.json", VALIDATE_INTESTACY_URL);
    }

    @Test
    public void validatePostRequestSolicitorValidateAdmonSuccess() {
        validatePostRequestSuccessForLegalStatement(
            "The administrator believes that all the information stated in the legal statement is true.",
            "solicitorPDFPayloadAdmonWill.json", VALIDATE_ADMON_URL);
    }

    @Test
    public void verifyEmptyForeNamesSolicitorValidateIntestacyReturnsError() {
        final Response response = given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("solicitorPDFPayloadIntestacy.json")
                    .replace("primaryApplicantForenames", ""))
            .when()
            .post(VALIDATE_INTESTACY_URL)
            .andReturn();
        final JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());

        assertEquals(400, response.getStatusCode());
        assertEquals(jsonPath.get("fieldErrors[0].message"), "Primary applicant forenames cannot be empty");
        assertEquals(jsonPath.get("message"), "Invalid payload");
    }

    @Test
    public void verifyEmptyForeNamesSolicitorValidateAdmonReturnsError() {
        final Response response = given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("solicitorPDFPayloadAdmonWill.json").replace("Main", ""))
            .when().post(VALIDATE_ADMON_URL)
            .andReturn();

        final JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertEquals(400, response.getStatusCode());
        assertEquals(jsonPath.get("fieldErrors[0].message"), "Primary applicant forenames cannot be empty");
        assertEquals(jsonPath.get("message"), "Invalid payload");
    }

    @Test
    public void verifyStateChangeFromCYABeforeLegalStatement() {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("success.stateChange.checkYourAnswersPayload.json"))
            .when().post("/nextsteps/validate")
            .then().statusCode(200)
            .and().body("data.state", equalToIgnoringCase("SolAppCreatedDeceasedDtls"));
    }

    @Test
    public void verifyStateChangeFromCYABeforeSigningSOT() {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("success.stateChange.beforeSOTcheckYourAnswersPayload.json"))
            .when().post("/nextsteps/validate")
            .then().statusCode(200)
            .and().body("data.state", equalToIgnoringCase("SolAppCreatedDeceasedDtls"));
    }

    private String replaceStringInCheckYourAnswersPayload(String oldJson, String newJson) {
        return utils.getJsonFromFile("success.beforeLegalStatement.checkYourAnswersPayload.json")
            .replace(oldJson, newJson);
    }

    private String textContentOf(byte[] pdfData) throws IOException {
        final PDDocument pdfDocument = PDDocument.load(new ByteArrayInputStream(pdfData));
        try {
            return new PDFTextStripper().getText(pdfDocument);
        } finally {
            pdfDocument.close();
        }
    }

    private void validatePostRequestSuccessForLegalStatement(String validationString, String fileName, String url) {
        final Response response = given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(fileName))
            .when().post(url);
        assertEquals(200, response.getStatusCode());

        downloadPdfAndVerifyString(extractDocumentId(response), validationString);
    }

    private void validatePostRequestFailureForLegalStatement(String oldString, String replacingString, String errorMsg,
                                                             String postURL) {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders())
            .body(replaceStringInCheckYourAnswersPayload(oldString, replacingString))
            .when().post(postURL).then().statusCode(400)
            .and().body("fieldErrors[0].field", equalToIgnoringCase(errorMsg))
            .and().body("message", equalToIgnoringCase("Invalid payload"));
    }

    private String extractDocumentId(Response response) {
        final String bodyString = response.body().asString();
        final JsonPath jsonPath = JsonPath.from(bodyString);
        final String urlNode = jsonPath.get("data.solsLegalStatementDocument.document_url");
        final String[] url = urlNode.split("/");
        return url[4];
    }

    private void downloadPdfAndVerifyString(String documentId, String validationString) {
        IntegrationTestBase.setEvidenceManagementUrlAsBaseUri();
        try {
            final ValidatableResponse response2 = given()
                .config(config)
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .when().get("/documents/" + documentId + "/binary")
                .then().assertThat().statusCode(200);

            final String textContent = removeCrLfs(textContentOf(response2.extract().body().asByteArray()));
            validationString = removeCrLfs(validationString);
            assertTrue(textContent.contains(validationString));
            assertEquals(response2.extract().contentType(), "application/pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
