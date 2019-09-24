package uk.gov.hmcts.probate.functional.bulkscanning;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

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

    private String jsonFile;

    private void validatePostSuccess(String bodyText, String containsText,
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

    @Test
    public void testAllMandatoryFieldsPresentReturnNoWarnings() {
        jsonFile = utils.getJsonFromFile("expectedOCRDataAllMandatoryFields.json");
        validatePostSuccess(jsonFile, SUCCESS, null, 0, 0);
    }

    @Test
    public void testMissingMandatoryFieldsReturnWarnings() {
        jsonFile = utils.getJsonFromFile("expectedOCRDataMissingMandatoryFields.json");
        validatePostSuccess(jsonFile, WARNINGS, DOB_MISSING, 2, 0);
        validatePostSuccess(jsonFile, WARNINGS, DOD_MISSING, 2, 1);
    }
}
