package uk.gov.hmcts.probate.functional.bulkscanning;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;

import java.io.IOException;
import java.time.LocalDate;

import static java.lang.String.format;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SerenityJUnit5Extension.class)
public class SolBaCcdServiceBulkScanningTests extends IntegrationTestBase {

    protected static final String S_S_DOES_NOT_APPEAR_TO_BE_A_VALID_EMAIL_ADDRESS =
        "%s (%s) does not appear to be a valid email address";
    private static final String SUCCESS = "SUCCESS";
    private static final String WARNINGS = "WARNINGS";
    private static final String DOB_MISSING = "Deceased date of birth (deceasedDateOfBirth) is mandatory.";
    private static final String DOD_MISSING = "Deceased date of death (deceasedDateOfDeath) is mandatory.";
    private static final String SOLICITOR_EMAIL_MISSING = "Solictor email address (solsSolicitorEmail) is mandatory.";
    private static final String VALIDATE_OCR_DATA = "/forms/%s/validate-ocr";
    private static final String PA1A = "PA1A";
    private static final String PA1P = "PA1P";

    private static final String VALIDATE_OCR_DATA_UNKNOWN_FORM_TYPE = "/forms/XZY/validate-ocr";
    private static final String TRANSFORM_EXCEPTON_RECORD = "/transform-scanned-data";
    private static final String UPDATE_CASE_FROM_EXCEPTON_RECORD = "/update-case";
    public static final String TRANSFORM_EXCEPTION_RECORD_PA_8_A_JSON = "bulkScanTransformExceptionRecordPA8A.json";
    public static final String EXPIRY_DATE = "\"expiryDate\":\"";
    public static final String EXCEPTION_RECORD_EXTEND_EXPIRY_PA_8_A_JSON =
        "bulkScanUpdateCaseExceptionRecordExtendExpiryPA8A.json";
    public static final String EXPIRY_DATE_0_9 = "\"expiryDate\":\"[0-9-]+\"";
    public static final String SENT_EMAIL = "sentEmail";
    public static final String EXCEPTION_RECORD_OUTPUT_ERROR_JSON =
        "expectedBulkScanTransformExceptionRecordOutputError.json";
    public static final String EXCEPTION_RECORD_EXTEND_EXPIRY_PA8A_AUTOMATED_JSON =
        "bulkScanUpdateCaseExceptionRecordExtendExpiryPA8AAutomated.json";
    public static final String EXCEPTION_RECORD_COMB_SOLICITOR_PA1A_AUTOMATED_JSON =
        "bulkScanTransformExceptionRecordCombSolicitorPA1AAutomated.json";

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    private void validateOCRDataPostSuccess(String formName, String bodyText, String containsText,
                                            String warningMessage, int warningSize, int warningItem) {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithCaseworkerUser())
            .body(bodyText)
            .when().post(String.format(VALIDATE_OCR_DATA, formName))
            .then().assertThat().statusCode(200)
            .and().body("warnings", hasSize(warningSize))
            .and().body("warnings[" + warningItem + "]", equalTo(warningMessage))
            .and().body(containsString(containsText));
    }

    private void validateOCRDataPostError(String bodyText) {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithCaseworkerUser())
            .body(bodyText)
            .when().post(VALIDATE_OCR_DATA_UNKNOWN_FORM_TYPE)
            .then().assertThat().statusCode(404);
    }

    private void transformExceptionPostSuccess(String bodyText, String containsText) {
        String actualResponse = RestAssured.given()
                .config(config)
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithCaseworkerUser())
                .body(bodyText)
                .when().post(TRANSFORM_EXCEPTON_RECORD)
                .then().assertThat().statusCode(200)
                .and().extract().body().asPrettyString();
        JSONAssert.assertEquals(containsText, actualResponse, JSONCompareMode.STRICT);
    }

    private void updateCaseFromExceptionPostSuccess(String bodyText, String containsText) {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithCaseworkerUser())
            .body(bodyText)
            .when().post(UPDATE_CASE_FROM_EXCEPTON_RECORD)
            .then().assertThat().statusCode(422)
            .and().body(containsString(containsText));
    }

    private JsonPath fetchJsonPathUpdatedCaveatDetailsFromCaseFromException(String bodyText) {
        final ValidatableResponse response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithCaseworkerUser())
            .body(bodyText)
            .when().post(UPDATE_CASE_FROM_EXCEPTON_RECORD)
            .then().assertThat().statusCode(200);
        return response.extract().body().jsonPath();
    }

    @Test
    void testAllMandatoryFieldsPresentReturnNoWarnings() throws IOException {
        String jsonRequest = utils.getJsonFromFile("expectedOCRDataAllMandatoryFields.json");
        validateOCRDataPostSuccess(PA1P, jsonRequest, SUCCESS, null, 0, 0);
    }

    /*
    @Test
    void testMissingMandatoryFieldsReturnWarnings() throws IOException {
        String jsonRequest = utils.getJsonFromFile("expectedOCRDataMissingMandatoryFields.json");
        validateOCRDataPostSuccess(PA1P, jsonRequest, WARNINGS, DOB_MISSING, 2, 0);
        validateOCRDataPostSuccess(PA1P, jsonRequest, WARNINGS, DOD_MISSING, 2, 1);
    }
     */

    /*
    @Test
    void testMissingSolicitorEmailPA1AReturnsWarning() throws IOException {
        String jsonRequest = utils.getJsonFromFile("expectedOCRDataMissingMandatoryFieldsSolPA1.json");
        validateOCRDataPostSuccess(PA1A, jsonRequest, WARNINGS, SOLICITOR_EMAIL_MISSING, 1, 0);
    }
     */

    /*
    @Test
    void testMissingSolicitorEmailPA1PReturnsWarning() throws IOException {
        String jsonRequest = utils.getJsonFromFile("expectedOCRDataMissingMandatoryFieldsSolPA1.json");
        validateOCRDataPostSuccess(PA1P, jsonRequest, WARNINGS, SOLICITOR_EMAIL_MISSING, 1, 0);
    }
     */

    @Test
    void testInvalidEmailFieldsReturnWarnings() throws IOException {
        String jsonRequest = utils.getJsonFromFile("expectedOCRDataAllInvalidEmailAddress.json");
        validateOCRDataPostSuccess(PA1P, jsonRequest, WARNINGS,
            format(S_S_DOES_NOT_APPEAR_TO_BE_A_VALID_EMAIL_ADDRESS, "Primary applicant email address",
                "primaryApplicantEmailAddress"), 3, 0);
        validateOCRDataPostSuccess(PA1P, jsonRequest, WARNINGS,
            format(S_S_DOES_NOT_APPEAR_TO_BE_A_VALID_EMAIL_ADDRESS, "Caveator email address", "caveatorEmailAddress"),
            3, 1);
        validateOCRDataPostSuccess(PA1P, jsonRequest, WARNINGS,
            format(S_S_DOES_NOT_APPEAR_TO_BE_A_VALID_EMAIL_ADDRESS, "Solicitor email address", "solsSolicitorEmail"),
                3, 2);
    }

    @Test
    void testInvalidFormTypeReturnError() throws IOException {
        String jsonRequest = utils.getJsonFromFile("expectedOCRDataAllMandatoryFields.json");
        validateOCRDataPostError(jsonRequest);
    }

    @Test
    void testTransformPA8AReturnSuccessfulJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile(TRANSFORM_EXCEPTION_RECORD_PA_8_A_JSON);
        String jsonResponse = utils.getJsonFromFile("expectedBulkScanTransformExceptionRecordOutputPA8A.json");
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformPA8Av2ReturnSuccessfulJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordPA8Av2.json");
        String jsonResponse = utils.getJsonFromFile("expectedBulkScanTransformExceptionRecordOutputPA8Av2.json");
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformCombinedCitizenPA8Av2ReturnSuccessfulJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordCombCitizenPA8Av2.json");
        String jsonResponse = utils.getJsonFromFile(
                "expectedBulkScanTransformExceptionRecordOutputCombCitizenPA8Av2.json");
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformCombinedSolicitorPA8AReturnSuccessfulJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordCombSolicitorPA8A.json");
        String jsonResponse = utils.getJsonFromFile(
                "expectedBulkScanTransformExceptionRecordOutputCombSolicitorPA8A.json");
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformCitizenPA1PReturnUnprocessedJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordPA1PUnprocessed.json");
        String jsonResponse = utils.getJsonFromFile(
                "expectedBulkScanTransformExceptionRecordOutputPA1PUnprocessed.json");
        transformExceptionPostUnprocessed(jsonRequest, jsonResponse);
    }

    @Test
    void testUpdateCaseExtendCaveatPA8AReturnSuccessfulJSON() throws IOException {
        final String expiryDate7DaysFromNow =
            LocalDate.now().plusDays(7).format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        final String expectedExpiryDate6MonthsFromNow =
            LocalDate.now().plusDays(7).plusMonths(6).format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        final String expiryDate = EXPIRY_DATE + expiryDate7DaysFromNow + "\"";
        String jsonRequest = utils.getJsonFromFile(EXCEPTION_RECORD_EXTEND_EXPIRY_PA_8_A_JSON);
        jsonRequest = jsonRequest.replaceAll(EXPIRY_DATE_0_9, expiryDate);
        final JsonPath jsonPath = fetchJsonPathUpdatedCaveatDetailsFromCaseFromException(jsonRequest);

        // Unable to use static file as documents are generated in the response, picking out specific values instead.
        assertEquals("Personal", jsonPath.get("case_update_details.case_data.applicationType"),
                "Correct applicationType");
        assertEquals("Yes", jsonPath.get("case_update_details.case_data.paperForm"),
                "Correct paperForm");
        assertEquals(expectedExpiryDate6MonthsFromNow, jsonPath.get("case_update_details.case_data.expiryDate"),
                "Correct expiry date");
        assertEquals("ctsc", jsonPath.get("case_update_details.case_data.registryLocation"),
                "Correct registry");

        // Checked Scanned Documents
        assertEquals(2, jsonPath.getList("case_update_details.case_data.scannedDocuments").size(),
                "Correct number scanned docs");
        assertEquals("19365040100100002",
                jsonPath.get("case_update_details.case_data.scannedDocuments[0].value.controlNumber"),
                "Correct DCN Scan Doc 1");
        assertEquals("123135453645",
                jsonPath.get("case_update_details.case_data.scannedDocuments[1].value.controlNumber"),
                "Correct DCN Scan Doc 2");

        // Checked Generated Notification Documents
        assertEquals(2, jsonPath.getList("case_update_details.case_data.notificationsGenerated").size(),
                "Correct number generated notifications");
        assertEquals(SENT_EMAIL,
                jsonPath.get("case_update_details.case_data.notificationsGenerated[0].value.DocumentType"),
                "Correct DocumentType Doc 1");
        assertEquals(SENT_EMAIL,
                jsonPath.get("case_update_details.case_data.notificationsGenerated[1].value.DocumentType"),
                "Correct DocumentType Doc 2");
    }

    @Test
    void testUpdateCaseExtendCaveatPA8AReturnExpiredErrorJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile(EXCEPTION_RECORD_EXTEND_EXPIRY_PA_8_A_JSON);
        String jsonResponse = utils.getJsonFromFile(
                "expectedBulkScanUpdateCaseExceptionRecordExpiredCaveatErrorPA8A.json");
        updateCaseFromExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    void testUpdateCaseExtendCaveatPA8AReturnOutsideOneMonthExpiryErrorJSON() throws IOException {
        final String expiryDate3MonthsFromNow =
            LocalDate.now().plusMonths(3).format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        final String expireDate = EXPIRY_DATE + expiryDate3MonthsFromNow + "\"";
        String jsonRequest = utils.getJsonFromFile(EXCEPTION_RECORD_EXTEND_EXPIRY_PA_8_A_JSON);
        jsonRequest = jsonRequest.replaceAll(EXPIRY_DATE_0_9, expireDate);
        String jsonResponse = utils.getJsonFromFile(
                    "expectedBulkScanUpdateCaseExceptionRecordExpiryOutsideOneMonthErrorPA8A.json");
        updateCaseFromExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformPA8AReturnTransformErrorJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordError.json");
        String jsonResponse = utils.getJsonFromFile(EXCEPTION_RECORD_OUTPUT_ERROR_JSON);
        transformExceptionPostUnprocessed(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformSolicitorPA8AReturnTransformErrorJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile("bulkScanTransformSolicitorExceptionRecordError.json");
        String jsonResponse = utils.getJsonFromFile(EXCEPTION_RECORD_OUTPUT_ERROR_JSON);
        transformExceptionPostUnprocessed(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformSolicitorPA8AReturnTransformErrorAutomatedJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile(
                "bulkScanTransformSolicitorExceptionRecordErrorAutomated.json");
        String jsonResponse = utils.getJsonFromFile(EXCEPTION_RECORD_OUTPUT_ERROR_JSON);
        transformExceptionPostUnprocessed(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformPA8AReturnSuccessfulAutomatedJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordPA8AAutomated.json");
        String jsonResponse = utils.getJsonFromFile("expectedBulkScanTransformExceptionRecordOutputPA8A.json");
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformCombinedCitizenPA8Av2ReturnSuccessfulAutomatedJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordCombCitizenPA8Av2Automated.json");
        String jsonResponse = utils.getJsonFromFile(
                "expectedBulkScanTransformExceptionRecordOutputCombCitizenPA8Av2.json");
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformCombinedSolicitorPA8AReturnSuccessfulAutomatedJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordCombSolicitorPA8AAutomated.json");
        String jsonResponse = utils.getJsonFromFile(
                "expectedBulkScanTransformExceptionRecordOutputCombSolicitorPA8A.json");
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    void testUpdateCaseExtendCaveatPA8AReturnSuccessfulAutomatedJSON() throws IOException {
        final String expiryDate7DaysFromNow = LocalDate.now().plusDays(7).format(
            CaveatCallbackResponseTransformer.dateTimeFormatter);
        final String expiryDate = EXPIRY_DATE + expiryDate7DaysFromNow + "\"";
        String jsonRequest = utils.getJsonFromFile(EXCEPTION_RECORD_EXTEND_EXPIRY_PA8A_AUTOMATED_JSON);
        jsonRequest = jsonRequest.replaceAll(EXPIRY_DATE_0_9, expiryDate);
        JsonPath jsonPath = fetchJsonPathUpdatedCaveatDetailsFromCaseFromException(jsonRequest);
        String expectedExpiryDate6MonthsFromNow = LocalDate.now().plusDays(7).plusMonths(6).format(
                CaveatCallbackResponseTransformer.dateTimeFormatter);
        // Unable to use static file as documents are generated in the response, picking out specific values instead.
        assertEquals("Personal", jsonPath.get("case_update_details.case_data.applicationType"),
                "Correct applicationType");
        assertEquals("Yes", jsonPath.get("case_update_details.case_data.paperForm"),
                "Correct paperForm");
        assertEquals(expectedExpiryDate6MonthsFromNow, jsonPath.get("case_update_details.case_data.expiryDate"),
                "Correct expiry date");
        assertEquals("ctsc", jsonPath.get("case_update_details.case_data.registryLocation"),
                "Correct registry");

        // Checked Scanned Documents
        assertEquals(2, jsonPath.getList("case_update_details.case_data.scannedDocuments").size(),
                "Correct number scanned docs");
        assertEquals("19365040100100002",
                jsonPath.get("case_update_details.case_data.scannedDocuments[0].value.controlNumber"),
                "Correct DCN Scan Doc 1");
        assertEquals("123135453645",
                jsonPath.get("case_update_details.case_data.scannedDocuments[1].value.controlNumber"),
                "Correct DCN Scan Doc 2");

        // Checked Generated Notification Documents
        assertEquals(2,
                jsonPath.getList("case_update_details.case_data.notificationsGenerated").size(),
                "Correct number generated notifications");
        assertEquals(SENT_EMAIL,
                jsonPath.get("case_update_details.case_data.notificationsGenerated[0].value.DocumentType"),
                "Correct DocumentType Doc 1");
        assertEquals(SENT_EMAIL,
                jsonPath.get("case_update_details.case_data.notificationsGenerated[1].value.DocumentType"),
                "Correct DocumentType Doc 2");
    }

    @Test
    void testUpdateCaseExtendCaveatPA8AReturnExpiredErrorAutomatedJSON() throws IOException {
        String jsonRequest =
            utils.getJsonFromFile(EXCEPTION_RECORD_EXTEND_EXPIRY_PA8A_AUTOMATED_JSON);
        String jsonResponse =
            utils.getJsonFromFile("expectedBulkScanUpdateCaseExceptionRecordExpiredCaveatErrorPA8A.json");
        updateCaseFromExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    void testUpdateCaseExtendCaveatPA8AReturnOutsideOneMonthExpiryErrorAutomatedJSON() throws IOException {
        final String expiryDate3MonthsFromNow = LocalDate.now().plusMonths(3).format(
            CaveatCallbackResponseTransformer.dateTimeFormatter);
        final String expireDate = EXPIRY_DATE + expiryDate3MonthsFromNow + "\"";
        String jsonRequest =
            utils.getJsonFromFile(EXCEPTION_RECORD_EXTEND_EXPIRY_PA8A_AUTOMATED_JSON);
        jsonRequest =
            jsonRequest.replaceAll(EXPIRY_DATE_0_9, expireDate);
        String jsonResponse =
            utils.getJsonFromFile(
                "expectedBulkScanUpdateCaseExceptionRecordExpiryOutsideOneMonthErrorPA8A.json");
        updateCaseFromExceptionPostSuccess(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformPA8AReturnTransformErrorAutomatedJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordErrorAutomated.json");
        String jsonResponse = utils.getJsonFromFile(EXCEPTION_RECORD_OUTPUT_ERROR_JSON);
        transformExceptionPostUnprocessed(jsonRequest, jsonResponse);
    }

    @Test
    void testTransformPA1AReturnTransformForbiddenJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile(EXCEPTION_RECORD_COMB_SOLICITOR_PA1A_AUTOMATED_JSON);
        transformExceptionPostForbidden(jsonRequest);
    }

    @Test
    void testUpdatePA1AReturnTransformForbiddenJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile(EXCEPTION_RECORD_COMB_SOLICITOR_PA1A_AUTOMATED_JSON);
        updateExceptionPostForbidden(jsonRequest);
    }

    @Test
    void testTransformPA8AReturnTransformForbiddenJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile(TRANSFORM_EXCEPTION_RECORD_PA_8_A_JSON);
        transformExceptionPostForbidden(jsonRequest);
    }

    @Test
    void testUpdatePA8AReturnTransformForbiddenJSON() throws IOException {
        String jsonRequest = utils.getJsonFromFile(TRANSFORM_EXCEPTION_RECORD_PA_8_A_JSON);
        updateExceptionPostForbidden(jsonRequest);
    }

    private void transformExceptionPostUnprocessed(String bodyText, String containsText) {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithCaseworkerUser())
            .body(bodyText)
            .when().post(TRANSFORM_EXCEPTON_RECORD)
            .then().assertThat().statusCode(422)
            .and().body(containsString(containsText));
    }

    private void transformExceptionPostForbidden(String bodyText) {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersForUnauthorisedService())
            .body(bodyText)
            .when().post(TRANSFORM_EXCEPTON_RECORD)
            .then().assertThat().statusCode(403);
    }


    private void updateExceptionPostForbidden(String bodyText) {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersForUnauthorisedService())
            .body(bodyText)
            .when().post(UPDATE_CASE_FROM_EXCEPTON_RECORD)
            .then().assertThat().statusCode(403);
    }
}
