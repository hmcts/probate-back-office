package uk.gov.hmcts.probate.functional.checkyouranswers;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static net.serenitybdd.rest.SerenityRest.given;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@RunWith(SerenityRunner.class)
public class SolCcdServiceCheckYourAnswersTests extends IntegrationTestBase {

    @Test
    public void verifyFirstNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("TestPrimaryExecutorFirstName");
    }

    @Test
    public void verifyLastNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("TestPrimaryExecutorLastName");
    }

    @Test
    public void verifyAddressInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("Test AddressLine1, Test " +
                "\nAddressLine2, Test AddressLine3, Hounslow, Middlesex, TW3 3DB, United Kingdom");
    }

    @Test
    public void verifyDeceasedNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("deceasedFirstName \ndeceasedLastName");
    }

    @Test
    public void verifyDeceasedDobInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("01/01\n/1987");
    }

    @Test
    public void verifyDeceasedDodInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("01/01/2018");
    }

    @Test
    public void verifyPrimaryExecutorAliasNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("TestPrimaryExecutorAliasName");
    }

    @Test
    public void verifyLegalStatementAcceptInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("We confirm that the information we have provided is correct to the best of our knowledge.");
    }

    @Test
    public void verifyLegalStatementSolicitorsDeclarationInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("The executors believe that all the information stated in the legal statement is true. They have authorised \n" +
                "SolicitorFirmName to sign a statement of truth on their behalf.");
    }

    @Test
    public void verifyDeclarationAcceptInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("We authorise SolicitorFirmName, as our appointed firm, to submit " +
                "\nthis application on our behalf.");
    }

    @Test
    public void verifyIhtGrossInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("1000.01");
    }

    @Test
    public void verifyIhtNetInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("900.09");
    }

    @Test
    public void verifySolicitorFirmNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("SolicitorFirmName");
    }

    @Test
    public void verifyAdditionalExecutor1NameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("AdditionalExecutor1FirstName AdditionalExecutor1LastName");
    }

    @Test
    public void verifyAdditionalExecutor1AliasNameInTheReturnedPDF() {
        validatePostRequestSuccessForLegalStatement("AdditionalExecutor1 willname");
    }

//     @Test
//     public void verifyIncorrectInputReturns400() {
//         given().relaxedHTTPSValidation()
//                 .headers(utils.getHeaders())
//                 .body(utils.getJsonFromFile("incorrectInput.checkYourAnswersPayload.json")).
//                 when().post("/case/validate").then().statusCode(400);
//     }

    @Test
    public void verifyEmptyFirstNameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"primaryApplicantForenames\": \"TestPrimaryExecutorFirstName\"", "\"primaryApplicantForenames\": \"\"", "caseDetails.data.primaryApplicantForenames");
    }

    @Test
    public void verifyEmptyLastNameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"primaryApplicantSurname\": \"TestPrimaryExecutorLastName\"", "\"primaryApplicantSurname\": \"\"", "caseDetails.data.primaryApplicantSurname");
    }

    @Test
    public void verifyMissingDeceasedDodReturnsError() {
        validatePostRequestFailureForLegalStatement("\"deceasedDateOfDeath\": \"2018-01-01\"", "\"deceasedDateOfDeath\": \"\"", "caseDetails.data.deceasedDateOfDeath");
    }

    @Test
    public void verifyMissingDeceasedDobReturnsError() {
        validatePostRequestFailureForLegalStatement("\"deceasedDateOfBirth\": \"1987-01-01\"", "\"deceasedDateOfBirth\": \"\"", "caseDetails.data.deceasedDateOfBirth");
    }

    @Test
    public void validatePostRequestSuccessCYAForBeforeSignSOT() {
        Response response = given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("success.beforeSignSOT.checkYourAnswersPayload.json")).
                        when().post("/nextsteps/validate");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void verifyStateChangeFromCYABeforeLegalStatement() {
        given().relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("success.stateChange.checkYourAnswersPayload.json"))
                .when().post("/nextsteps/validate")
                .then().statusCode(200)
                .and().body("data.state", equalToIgnoringCase("SolAppCreated"));
    }

    @Test
    public void verifyStateChangeFromCYABeforeSigningSOT() {
        given().relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("success.stateChange.beforeSOTcheckYourAnswersPayload.json"))
                .when().post("/nextsteps/validate")
                .then().statusCode(200)
                .and().body("data.state", equalToIgnoringCase("SolAppCreated"));
    }

    private String replaceString(String oldJson, String newJson) {
        return utils.getJsonFromFile("success.beforeLegalStatement.checkYourAnswersPayload.json").replace(oldJson, newJson);
    }

    private String textContentOf(byte[] pdfData) throws IOException {
        PDDocument pdfDocument = PDDocument.load(new ByteArrayInputStream(pdfData));
        try {
            return new PDFTextStripper().getText(pdfDocument);
        } finally {
            pdfDocument.close();
        }
    }

    private void validatePostRequestSuccessForLegalStatement(String validationString) {
        Response response = given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("success.beforeLegalStatement.checkYourAnswersPayload.json")).
                        when().post("/case/validate");
        assertEquals(200, response.getStatusCode());

        downloadPdfAndVerifyString(extractDocumentId(response), validationString);
    }


    private void validatePostRequestFailureForLegalStatement(String oldString, String replacingString, String errorMsg) {
        given().relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body(replaceString(oldString, replacingString))
                .when().post("/case/validate").then().statusCode(400)
                .and().body("fieldErrors[0].field", equalToIgnoringCase(errorMsg))
                .and().body("message", equalToIgnoringCase("Invalid payload"));
    }

    private String extractDocumentId(Response response) {
        String bodyString = response.body().asString();
        JsonPath jsonPath = JsonPath.from(bodyString);
        String url_node = jsonPath.get("data.solsLegalStatementDocument.document_url");
        String[] url = url_node.split("/");
        return url[4];
    }

    private void downloadPdfAndVerifyString(String documentId, String validationString) {
        IntegrationTestBase.setEvidenceManagementUrlAsBaseUri();
        try {
            ValidatableResponse response2 = given()
                    .relaxedHTTPSValidation()
                    .headers(utils.getHeadersWithUserId())
                    .when().get("/documents/" + documentId + "/binary")
                    .then().assertThat().statusCode(200);

            String textContent = textContentOf(response2.extract().body().asByteArray());
            assertTrue(textContent.contains(validationString));
            String contentType = response2.extract().contentType();
            assertEquals(contentType, "application/pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
