package uk.gov.hmcts.probate.functional.bulkscanning;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;

import java.io.IOException;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SerenityRunner.class)
public class SolBaCcdServiceBulkScanningTests extends IntegrationTestBase {

    private static final String SUCCESS = "SUCCESS";
    private static final String WARNINGS = "WARNINGS";
    private static final String ERRORS = "ERRORS";
    private static final String DOB_MISSING = "Deceased date of birth (deceasedDateOfBirth) is mandatory.";
    private static final String DOD_MISSING = "Deceased date of death (deceasedDateOfDeath) is mandatory.";
    private static final String FORM_TYPE_MISSING = "Form type not found or invalid";

    private static final String VALIDATE_OCR_DATA = "/forms/PA1P/validate-ocr";
    private static final String VALIDATE_OCR_DATA_UNKNOWN_FORM_TYPE = "/forms/XZY/validate-ocr";
    private static final String TRANSFORM_EXCEPTON_RECORD = "/transform-exception-record";
    private static final String UPDATE_CASE_FROM_EXCEPTON_RECORD = "/update-case";

    private String jsonRequest;
    private String jsonResponse;

    private void validateOCRDataPostSuccess(String bodyText, String containsText,
                                            String warningMessage, int warningSize, int warningItem) {
        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body(bodyText)
                .when().post(VALIDATE_OCR_DATA)
                .then().assertThat().statusCode(200)
                .and().body("warnings", hasSize(warningSize))
                .and().body("warnings[" + warningItem + "]", equalTo(warningMessage))
                .and().content(containsString(containsText));
    }

    private void validateOCRDataPostError(String bodyText, String containsText,
                                            String errorMessage, int errorSize, int errorItem) {
        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body(bodyText)
                .when().post(VALIDATE_OCR_DATA_UNKNOWN_FORM_TYPE)
                .then().assertThat().statusCode(200)
                .and().body("errors", hasSize(errorSize))
                .and().body("errors[" + errorItem + "]", equalTo(errorMessage))
                .and().content(containsString(containsText));
    }

    private void transformExceptionPostSuccess(String bodyText, String containsText) {
        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body(bodyText)
                .when().post(TRANSFORM_EXCEPTON_RECORD)
                .then().assertThat().statusCode(200)
                .and().content(containsString(containsText));
    }

    private void updateCaseFromExceptionPostSuccess(String bodyText, String containsText) {
        ValidatableResponse response = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body(bodyText)
                .when().post(UPDATE_CASE_FROM_EXCEPTON_RECORD)
                .then().assertThat().statusCode(200)
                .and().content(containsString(containsText));
    }

    private JsonPath fetchJsonPathUpdatedCaveatDetailsFromCaseFromException(String bodyText) throws IOException {
        ValidatableResponse response = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body(bodyText)
                .when().post(UPDATE_CASE_FROM_EXCEPTON_RECORD)
                .then().assertThat().statusCode(200);
        return response.extract().body().jsonPath();
    }

    @Test
    public void testAllMandatoryFieldsPresentReturnNoWarnings() {
        jsonRequest = utils.getJsonFromFile("expectedOCRDataAllMandatoryFields.json");
        validateOCRDataPostSuccess(jsonRequest, SUCCESS, null, 0, 0);
    }

    @Test
    public void testMissingMandatoryFieldsReturnWarnings() {
        jsonRequest = utils.getJsonFromFile("expectedOCRDataMissingMandatoryFields.json");
        validateOCRDataPostSuccess(jsonRequest, WARNINGS, DOB_MISSING, 2, 0);
        validateOCRDataPostSuccess(jsonRequest, WARNINGS, DOD_MISSING, 2, 1);
    }

    @Test
    public void testInvalidFormTypeReturnError() {
        jsonRequest = utils.getJsonFromFile("expectedOCRDataAllMandatoryFields.json");
        validateOCRDataPostError(jsonRequest, ERRORS, FORM_TYPE_MISSING, 1, 0);
    }

    @Test
    public void testTransformPA8AReturnSuccessfulJSON() {
        String currentDate = LocalDate.now().format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        String applicationSubmittedDate = "\"applicationSubmittedDate\":\"" + currentDate + "\"";
        jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordPA8A.json");
        jsonResponse = utils.getJsonFromFile("expectedBulkScanTransformExceptionRecordOutputPA8A.json");
        jsonResponse = jsonResponse.replaceAll("\"applicationSubmittedDate\":\"[0-9-]+\"", applicationSubmittedDate);
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    public void testTransformCombinedCitizenPA8AReturnSuccessfulJSON() {
        String currentDate = LocalDate.now().format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        String applicationSubmittedDate = "\"applicationSubmittedDate\":\"" + currentDate + "\"";
        jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordCombCitizenPA8A.json");
        jsonResponse = utils.getJsonFromFile("expectedBulkScanTransformExceptionRecordOutputCombCitizenPA8A.json");
        jsonResponse = jsonResponse.replaceAll("\"applicationSubmittedDate\":\"[0-9-]+\"", applicationSubmittedDate);
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    public void testTransformCombinedSolicitorPA8AReturnSuccessfulJSON() {
        String currentDate = LocalDate.now().format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        String applicationSubmittedDate = "\"applicationSubmittedDate\":\"" + currentDate + "\"";
        jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordCombSolicitorPA8A.json");
        jsonResponse = utils.getJsonFromFile("expectedBulkScanTransformExceptionRecordOutputCombSolicitorPA8A.json");
        jsonResponse = jsonResponse.replaceAll("\"applicationSubmittedDate\":\"[0-9-]+\"", applicationSubmittedDate);
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    public void testTransformPA1PReturnSuccessfulJSON() {
        String currentDate = LocalDate.now().format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        String applicationSubmittedDate = "\"applicationSubmittedDate\":\"" + currentDate + "\"";
        jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordPA1P.json");
        jsonResponse = utils.getJsonFromFile("expectedBulkScanTransformExceptionRecordOutputPA1P.json");
        jsonResponse = jsonResponse.replaceAll("\"applicationSubmittedDate\":\"[0-9-]+\"", applicationSubmittedDate);
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    public void testTransformPA1AReturnSuccessfulJSON() {
        String currentDate = LocalDate.now().format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        String applicationSubmittedDate = "\"applicationSubmittedDate\":\"" + currentDate + "\"";
        jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordPA1A.json");
        jsonResponse = utils.getJsonFromFile("expectedBulkScanTransformExceptionRecordOutputPA1A.json");
        jsonResponse = jsonResponse.replaceAll("\"applicationSubmittedDate\":\"[0-9-]+\"", applicationSubmittedDate);
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    public void testUpdateCaseExtendCaveatPA8AReturnSuccessfulJSON() throws IOException {
        String currentDate = LocalDate.now().format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        String expiryDate7DaysFromNow = LocalDate.now().plusDays(7).format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        String expectedExpiryDate6MonthsFromNow = LocalDate.now().plusDays(7).plusMonths(6).format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        String expiryDate = "\"expiryDate\":\"" + expiryDate7DaysFromNow + "\"";
        jsonRequest = utils.getJsonFromFile("bulkScanUpdateCaseExceptionRecordExtendExpiryPA8A.json");
        jsonRequest = jsonRequest.replaceAll("\"expiryDate\":\"[0-9-]+\"", expiryDate);
        JsonPath jsonPath = fetchJsonPathUpdatedCaveatDetailsFromCaseFromException(jsonRequest);

        // Unable to use static file as documents are generated in the response, picking out specific values instead.
        Assert.assertEquals("Correct applicationType", "Personal", jsonPath.get("case_update_details.applicationType"));
        Assert.assertEquals("Correct paperForm", "Yes", jsonPath.get("case_update_details.paperForm"));
        Assert.assertEquals("Correct expiry date", expectedExpiryDate6MonthsFromNow, jsonPath.get("case_update_details.expiryDate"));
        Assert.assertEquals("Correct registry", "ctsc", jsonPath.get("case_update_details.registryLocation"));

        // Checked Scanned Documents
        Assert.assertEquals("Correct number scanned docs", 2, jsonPath.getList("case_update_details.scannedDocuments").size());
        Assert.assertEquals("Correct DCN Scan Doc 1", "19365040100100002", jsonPath.get("case_update_details.scannedDocuments[0].value.controlNumber"));
        Assert.assertEquals("Correct DCN Scan Doc 2", "123135453645", jsonPath.get("case_update_details.scannedDocuments[1].value.controlNumber"));

        // Checked Generated Notification Documents
        Assert.assertEquals("Correct number generated notifications", 2, jsonPath.getList("case_update_details.notificationsGenerated").size());
        Assert.assertEquals("Correct DocumentType Doc 1", "sentEmail", jsonPath.get("case_update_details.notificationsGenerated[0].value.DocumentType"));
        Assert.assertEquals("Correct DocumentType Doc 2", "sentEmail", jsonPath.get("case_update_details.notificationsGenerated[1].value.DocumentType"));
    }

    @Test
    public void testUpdateCaseExtendCaveatPA8AReturnExpiredErrorJSON() {
        jsonRequest = utils.getJsonFromFile("bulkScanUpdateCaseExceptionRecordExtendExpiryPA8A.json");
        jsonResponse = utils.getJsonFromFile("expectedBulkScanUpdateCaseExceptionRecordExpiredCaveatErrorPA8A.json");
        updateCaseFromExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    public void testUpdateCaseExtendCaveatPA8AReturnOutsideOneMonthExpiryErrorJSON() {
        String expiryDate3MonthsFromNow = LocalDate.now().plusMonths(3).format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        String expireDate = "\"expiryDate\":\"" + expiryDate3MonthsFromNow + "\"";
        jsonRequest = utils.getJsonFromFile("bulkScanUpdateCaseExceptionRecordExtendExpiryPA8A.json");
        jsonRequest = jsonRequest.replaceAll("\"expiryDate\":\"[0-9-]+\"", expireDate);
        jsonResponse = utils.getJsonFromFile("expectedBulkScanUpdateCaseExceptionRecordExpiryOutsideOneMonthErrorPA8A.json");
        updateCaseFromExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    public void testTransformPA8AReturnTransformErrorJSON() {
        jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordError.json");
        jsonResponse = utils.getJsonFromFile("expectedBulkScanTransformExceptionRecordOutputError.json");
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }
}
