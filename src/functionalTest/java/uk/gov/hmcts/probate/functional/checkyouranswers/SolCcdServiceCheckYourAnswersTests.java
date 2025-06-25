package uk.gov.hmcts.probate.functional.checkyouranswers;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import java.io.ByteArrayInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.IOException;

import static io.restassured.RestAssured.given;


import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SerenityJUnit5Extension.class)
public class SolCcdServiceCheckYourAnswersTests extends IntegrationTestBase {

    private static final String VALIDATE_URL = "/case/sols-validate";
    private static final String VALIDATE_PROBATE_URL = "/case/sols-validate-probate";
    private static final String VALIDATE_INTESTACY_URL = "/case/sols-validate-intestacy";
    private static final String VALIDATE_ADMON_URL = "/case/sols-validate-admon";
    private static final String DOC_NAME = "success.beforeLegalStatement.checkYourAnswersPayload.json";

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void verifyFirstNameInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("TestPrimaryExecutorFirstName", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyLastNameInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("TestPrimaryExecutorLastName", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyAddressInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("Test AddressLine1, Test "
                + "AddressLine2, Test AddressLine3, Hounslow, Middlesex, TW3 3DB, United Kingdom", DOC_NAME,
            VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyDeceasedNameInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("DeceasedFirstName DeceasedLastName", DOC_NAME,
            VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyDeceasedDobInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("01/01/1987", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyDeceasedDodInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("01/01/2018", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyPrimaryExecutorAliasNameInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("TestPrimaryExecutorAliasName", DOC_NAME,
                VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyLegalStatementAcceptInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement(
            "We confirm that the information we have provided is correct to the best of our knowledge.",
            DOC_NAME,
            VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyLegalStatementSolicitorsDeclarationInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement(
            "The executors believe that all the information stated in the legal statement is true.",
            DOC_NAME,
            VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyDeclarationAcceptInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("They have authorised \nSolicitorFirmName "
                + "to sign a statement of truth on their behalf.",
                DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyIhtGrossInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("1000", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyIhtNetInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("900", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    void verifySolicitorFirmNameInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("SolicitorFirmName", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyAdditionalExecutor1NameInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("AdditionalExecutor1FirstName AdditionalExecutor1LastName",
            DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyAdditionalExecutor1AliasNameInTheReturnedPDF() throws IOException {
        validatePostRequestSuccessForLegalStatement("AdditionalExecutor1 willname", DOC_NAME, VALIDATE_PROBATE_URL);
    }

    @Test
    void verifyIncorrectInputReturns400() throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithSolicitorUser())
            .body(utils.getJsonFromFile("incorrectInput.checkYourAnswersPayload.json"))
            .when().post(VALIDATE_URL).then().statusCode(400);
    }

    @Test
    void verifyMissingDeceasedDodReturnsError() throws IOException {
        validatePostRequestFailureForLegalStatement("\"deceasedDateOfDeath\": \"2018-01-01\"",
            "\"deceasedDateOfDeath\": \"\"", "caseDetails.data.deceasedDateOfDeath", VALIDATE_URL);
    }

    @Test
    void verifyMissingDeceasedDobReturnsError() throws IOException {
        validatePostRequestFailureForLegalStatement("\"deceasedDateOfBirth\": \"1987-01-01\"",
            "\"deceasedDateOfBirth\": \"\"", "caseDetails.data.deceasedDateOfBirth", VALIDATE_URL);
    }

    @Test
    void validatePostRequestSuccessCYAForBeforeSignSOT() throws IOException {
        final Response response = given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("success.beforeSignSOT.checkYourAnswersPayload.json"))
            .when().post("/nextsteps/validate");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void validatePostRequestSolicitorValidateIntestacySuccess() throws IOException {
        validatePostRequestSuccessForLegalStatement(
            "I authorise Firm Name, as my appointed firm to submit this application on my behalf.",
            "solicitorPDFPayloadIntestacy.json", VALIDATE_INTESTACY_URL);
    }

    @Test
    void validatePostRequestSolicitorValidateAdmonSuccess() throws IOException {
        validatePostRequestSuccessForLegalStatement(
            "The administrator believes that all the information stated in the legal statement is true.",
            "solicitorPDFPayloadAdmonWill.json", VALIDATE_ADMON_URL);
    }

    @Test
    void validateSuccessForAdmonWillWithWillAndOneCodicilAdded() throws IOException {
        final Response response = given()
                .config(config)
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("solicitorPDFPayloadAdmonWillWithOneCodicil.json"))
                .when().post(VALIDATE_ADMON_URL);

        assertEquals(200, response.getStatusCode());
        downloadPdfAndVerifyString(extractDocumentId(response), "and not by the will and codicil");
    }

    @Test
    void validateSuccessForAdmonWillWithWillAndMultipleCodicilAdded() throws IOException {
        final Response response = given()
                .config(config)
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("solicitorPDFPayloadAdmonWillWithMultipleCodicils.json"))
                .when().post(VALIDATE_ADMON_URL);

        assertEquals(200, response.getStatusCode());
        downloadPdfAndVerifyString(extractDocumentId(response), "and not by the will and codicils");
    }

    @Test
    void validateSuccessForAdmonWillWithWillAndNoCodicilAdded() throws IOException {
        final Response response = given()
                .config(config)
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("solicitorPDFPayloadAdmonWill.json"))
                .when().post(VALIDATE_ADMON_URL);

        assertEquals(200, response.getStatusCode());
        downloadPdfAndVerifyString(extractDocumentId(response), "and not by the will");
    }


    @Test
    void verifyEmptyForeNamesSolicitorValidateIntestacyReturnsError() throws IOException {
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
    void verifyEmptyForeNamesSolicitorValidateAdmonReturnsError() throws IOException {
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
    void verifyStateChangeFromCYABeforeLegalStatement() throws IOException {
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
    void verifyStateChangeFromCYABeforeSigningSOT() throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("success.stateChange.beforeSOTcheckYourAnswersPayload.json"))
            .when().post("/nextsteps/validate")
            .then().statusCode(200)
            .and().body("data.state", equalToIgnoringCase("SolAppCreatedDeceasedDtls"));
    }

    private String replaceStringInCheckYourAnswersPayload(String oldJson, String newJson) throws IOException {
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

    private void validatePostRequestSuccessForLegalStatement(String validationString, String fileName, String url)
        throws IOException {
        final Response response = given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithSolicitorUser())
            .body(utils.getJsonFromFile(fileName))
            .when().post(url);
        assertEquals(200, response.getStatusCode());

        downloadPdfAndVerifyString(extractDocumentId(response), validationString);
    }

    private void validatePostRequestFailureForLegalStatement(String oldString, String replacingString, String errorMsg,
                                                             String postURL) throws IOException {
        given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithSolicitorUser())
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
        try {
            final Response response = utils.getDocumentResponseFromId(documentId, utils.getHeadersWithUserId());

            final String textContent = removeCrLfs(textContentOf(response.getBody().asByteArray()));
            validationString = removeCrLfs(validationString);
            assertTrue(textContent.contains(validationString));
            assertEquals("application/pdf", response.contentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
