package uk.gov.hmcts.probate.functional.bulkscanning;

import io.restassured.RestAssured;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringIntegrationSerenityRunner.class)
public class BulkScanPA1FormV2Tests extends IntegrationTestBase {

    private static final String SUCCESS = "SUCCESS";
    private static final String VALIDATE_OCR_DATA = "/forms/%s/validate-ocr";
    private static final String PA1P = "PA1P";
    private static final String PA1A = "PA1A";
    private static final String WARNINGS = "WARNINGS";
    private static final String NQV_MISSING = "net qualifying value of the estate " 
        + "(ihtEstateNetQualifyingValue) is mandatory.";

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void testPost2022PA1PAllMandatoryFieldsPresentReturnNoWarning() {
        String jsonRequest = utils.getJsonFromFile("bulkscan/version2/Post2022PA1PAllMandatoryFilled.json");
        validateOCRDataPostSuccess(PA1P, jsonRequest, SUCCESS, null, 0, 0);
    }

    @Test
    public void testPost2022PA1PMissingMandatoryFieldsPresentReturnSomeWarnings() {
        String jsonRequest = utils.getJsonFromFile("bulkscan/version2/Post2022PA1PMissingNVQ.json");
        validateOCRDataPostSuccess(PA1P, jsonRequest, WARNINGS, NQV_MISSING, 1, 0);
    }

    @Test
    public void testPost2022PA1AAllMandatoryFieldsPresentReturnNoWarning() {
        String jsonRequest = utils.getJsonFromFile("bulkscan/version2/Post2022PA1AAllMandatoryFilled.json");
        validateOCRDataPostSuccess(PA1A, jsonRequest, SUCCESS, null, 0, 0);
    }

    @Test
    public void testPost2022PA1AMissingMandatoryFieldsPresentReturnSomeWarnings() {
        String jsonRequest = utils.getJsonFromFile("bulkscan/version2/Post2022PA1AMissingNVQ.json");
        validateOCRDataPostSuccess(PA1A, jsonRequest, WARNINGS, NQV_MISSING, 1, 0);
    }

    private void validateOCRDataPostSuccess(String formName, String bodyText, String containsText,
                                            String warningMessage, int warningSize, int warningItem) {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders())
            .body(bodyText)
            .when().post(String.format(VALIDATE_OCR_DATA, formName))
            .then().assertThat().statusCode(200)
            .and().body("warnings", hasSize(warningSize))
            .and().body("warnings[" + warningItem + "]", equalTo(warningMessage))
            .and().body(containsString(containsText));
    }

}
