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
public class BulkScanPA1PFormV2Tests extends IntegrationTestBase {

    private static final String SUCCESS = "SUCCESS";
    private static final String VALIDATE_OCR_DATA = "/forms/%s/validate-ocr";
    private static final String PA1P = "PA1P";
    private static final String WARNINGS = "WARNINGS";
    private static final String NQV_MISSING = "net qualifying value of the estate " 
        + "(ihtEstateNetQualifyingValue) is mandatory.";

    @Before
    public void setUp() {
        initialiseConfig();
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
