package uk.gov.hmcts.probate.functional.bulkscanning;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SerenityRunner.class)
public class SolBaCcdServiceBulkScanningTests extends IntegrationTestBase {

    private static final String SUCCESS = "SUCCESS";
    private static final String WARNINGS = "WARNINGS";
    private static final String DOB_MISSING = "Deceased date of birth (deceasedDateOfBirth) is mandatory.";
    private static final String DOD_MISSING = "Deceased date of death (deceasedDateOfDeath) is mandatory.";

    private static final String VALIDATE_OCR_DATA = "/forms/PA1P/validate-ocr";
    private static final String TRANSFORM_EXCEPTON_RECORD = "/transform-exception-record";

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

    private void transformExceptionPostSuccess(String bodyText, String containsText) {
        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body(bodyText)
                .when().post(TRANSFORM_EXCEPTON_RECORD)
                .then().assertThat().statusCode(200)
                .and().content(containsString(containsText));
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
    public void testTransformPA8AReturnSuccessfulJSON() {
        String currentDate = LocalDate.now().format(CaveatCallbackResponseTransformer.dateTimeFormatter);
        String applicationSubmittedDate = "\"applicationSubmittedDate\":\"" + currentDate + "\"";
        jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordPA8A.json");
        jsonResponse = utils.getJsonFromFile("expectedBulkScanTransformExceptionRecordOutputPA8A.json");
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
    public void testTransformPA8AReturnTransformErrorJSON() {
        jsonRequest = utils.getJsonFromFile("bulkScanTransformExceptionRecordError.json");
        jsonResponse = utils.getJsonFromFile("expectedBulkScanTransformExceptionRecordOutputError.json");
        transformExceptionPostSuccess(jsonRequest, jsonResponse);
    }
}
