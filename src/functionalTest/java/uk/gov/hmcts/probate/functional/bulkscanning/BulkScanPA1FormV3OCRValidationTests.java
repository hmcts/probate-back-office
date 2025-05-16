package uk.gov.hmcts.probate.functional.bulkscanning;

import io.restassured.RestAssured;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SerenityJUnit5Extension.class)
public class BulkScanPA1FormV3OCRValidationTests extends IntegrationTestBase {

    private static final String VALIDATE_OCR_DATA = "/forms/%s/validate-ocr";
    private static final String PA1P = "PA1P";
    private static final String PA1A = "PA1A";
    private static final String SUCCESS = "SUCCESS";
    private static final String WARNINGS = "WARNINGS";

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void testPost2022PA1PAllMandatoryFieldsPresentReturnNoWarning() {
        String jsonRequest =
            utils.getStringFromFile(
                "/json/bulkscan/version3/validation/requestPayload/Post2022PA1PAllMandatoryFilled.json");
        List<String> expectedWarnings = emptyList();
        validateOCRWarnings(PA1P, jsonRequest, SUCCESS, expectedWarnings);
    }

    /*
    @Test
    void testPost2022PA1PMissingMandatoryFieldsPresentReturnSomeWarnings() {
        String jsonRequest =
            utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/Post2022PA1PMissingNQV.json");
        List<String> expectedWarnings =
            utils.getLinesFromFile("/json/bulkscan/version3/validation/expectedWarnings/missingNQV.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }
     */

    @Test
    void testPost2022PA1AAllMandatoryFieldsPresentReturnNoWarning() {
        String jsonRequest =
            utils.getStringFromFile(
                "/json/bulkscan/version3/validation/requestPayload/Post2022PA1AAllMandatoryFilled.json");
        List<String> expectedWarnings = emptyList();
        validateOCRWarnings(PA1A, jsonRequest, SUCCESS, expectedWarnings);
    }

    @Test
    void testPost2022PA1AMissingMandatoryFieldsPresentReturnSomeWarnings() {
        String jsonRequest =
            utils.getStringFromFile(
                "/json/bulkscan/version3/validation/requestPayload/Post2022PA1AMissingNQV.json");
        List<String> expectedWarnings =
            utils.getLinesFromFile("/json/bulkscan/version3/validation/expectedWarnings/missingNQV.txt");
        validateOCRWarnings(PA1A, jsonRequest, WARNINGS, expectedWarnings);
    }

    /*
    @Test
    void shouldWarnWhenIHT400421Missing() {
        String jsonRequest =
            utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                + "Post2022PA1PMissingIHT421Values.json");
        List<String> expectedWarnings =
            utils.getLinesFromFile(
                "/json/bulkscan/version3/validation/expectedWarnings/missingIHT421Values.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }
     */

    /*
    @Test
    void shouldWarnWhenIHT400ValuesMissing() {
        String jsonRequest =
                utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                        + "Post2022PA1PMissingIHT400Values.json");
        List<String> expectedWarnings =
                utils.getLinesFromFile(
                        "/json/bulkscan/version3/validation/expectedWarnings/missingIHT400Values.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }
     */

    /*
    @Test
    void shouldWarnWhenExceptedEstatesMissing() {
        String jsonRequest =
                utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                        + "Post2022PA1PMissingExceptedEstates.json");
        List<String> expectedWarnings =
                utils.getLinesFromFile(
                        "/json/bulkscan/version3/validation/expectedWarnings/missingExceptedEstates.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }
     */

    /*
    @Test
    void shouldWarnWhenIHT207Missing() {
        String jsonRequest =
            utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                + "Post2022PA1PMissingIHT207Values.json");
        List<String> expectedWarnings =
            utils.getLinesFromFile(
                "/json/bulkscan/version3/validation/expectedWarnings/missingIHT207Values.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }
     */

    /*
    @Test
    void shouldWarnWhenDiedAfterSwitchDateMissing() {
        String jsonRequest =
            utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                + "Post2022PA1PMissingDiedAfterSwitchDate.json");
        List<String> expectedWarnings =
            utils.getLinesFromFile("/json/bulkscan/version3/validation/expectedWarnings"
                + "/missingDiedAfterSwitchDate.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }
     */

    /*
    @Test
    void shouldWarnWhenDiedAfterSwitchDateInconsistentWithDOD() {
        String jsonRequest =
            utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                + "Post2022PA1PDiedAfterSwitchDateWrong.json");
        List<String> expectedWarnings =
            utils.getLinesFromFile("/json/bulkscan/version3/validation/expectedWarnings/wrongDiedAfterSwitchDate.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }
     */

    /*
    @Test
    void shouldWarnForMissingEstateValues() {
        String jsonRequest =
            utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                + "Post2022PA1PMissingEstateValues.json");
        List<String> expectedWarnings =
            utils.getLinesFromFile("/json/bulkscan/version3/validation/expectedWarnings/"
                + "missingEstateValues.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }
     */

    /*
    @Test
    void shouldWarnForMissingIHT205Values() {
        String jsonRequest =
            utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                + "Pre2022PA1PMissingIHT205Values.json");
        List<String> expectedWarnings =
            utils.getLinesFromFile("/json/bulkscan/version3/validation/expectedWarnings/"
                + "missingIHT205Values.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }
     */

    /*
    @Test
    void shouldWarnForMoreThanOneFormSubmitted() {
        String jsonRequest =
                utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                        + "Post2022PA1PMoreThanOneFormSubmitted.json");
        List<String> expectedWarnings =
                utils.getLinesFromFile("/json/bulkscan/version3/validation/expectedWarnings/"
                        + "moreThanOneFormSubmitted.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }
     */

    @Test
    void invalidApplyingExecutor0EmailAddress() {
        String jsonRequest =
                utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                        + "Post2022PA1PInvalidExecutor0EmailAddress.json");
        List<String> expectedWarnings =
                utils.getLinesFromFile("/json/bulkscan/version3/validation/expectedWarnings/"
                        + "invalidApplyingExecutor0EmailAddress.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }

    @Test
    void shouldWarnForMissingIhtUnusedAllowanceClaimed() {
        String jsonRequest =
            utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                + "Post2022PA1AMissingUnusedAllowanceClaimed.json");
        List<String> expectedWarnings =
            utils.getLinesFromFile("/json/bulkscan/version3/validation/expectedWarnings/"
                + "missingUnusedAllowanceClaimed.txt");
        validateOCRWarnings(PA1A, jsonRequest, WARNINGS, expectedWarnings);
    }

    @Test
    void shouldNotWarnForMissingIhtUnusedAllowanceClaimedIfNotWidowed() {
        String jsonRequest =
            utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                + "Post2022PA1AMissingUnusedAllowanceClaimedNotWidowed.json");
        List<String> expectedWarnings = emptyList();
        validateOCRWarnings(PA1A, jsonRequest, SUCCESS, expectedWarnings);
    }

    @Test
    void shouldWarnForMissingIhtUnusedAllowanceClaimedIfNQVOutOfRange() {
        String jsonRequest =
            utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                + "Post2022PA1AMissingUnusedAllowanceClaimedNQVOutOfRange.json");
        List<String> expectedWarnings = emptyList();
        validateOCRWarnings(PA1A, jsonRequest, SUCCESS, expectedWarnings);
    }

    /*
    @Test
    void shouldWarnForIht400Iht400421completedWithNoIht421Values() {
        String jsonRequest =
                utils.getStringFromFile("/json/bulkscan/version3/validation/requestPayload/"
                        + "Post2022PA1PIHT400IHT400421CompletedWithNoIHT421Values.json");
        List<String> expectedWarnings =
                utils.getLinesFromFile(
                "/json/bulkscan/version3/validation/expectedWarnings/missingIHT421ValuesDuplicateForms.txt");
        validateOCRWarnings(PA1P, jsonRequest, WARNINGS, expectedWarnings);
    }
     */

    private void validateOCRWarnings(String formName, String bodyText, String containsText,
                                     List<String> expectedWarnings) {
        List<String> warnings = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders())
            .body(bodyText)
            .when().post(String.format(VALIDATE_OCR_DATA, formName))
            .then().assertThat().statusCode(200)
            .and().body(containsString(containsText)).extract().body().jsonPath().get("warnings");
        assertEquals(expectedWarnings, warnings);
    }
}
