package uk.gov.hmcts.probate.functional.bulkscanning;

import io.restassured.RestAssured;
import java.time.format.DateTimeFormatter;
import static org.hamcrest.Matchers.equalTo;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;

@ExtendWith(SerenityJUnit5Extension.class)
public class BulkScanPA1FormV2OCRTransformationTests extends IntegrationTestBase {

    private static final String TRANSFORM_EXCEPTON_RECORD = "/transform-scanned-data";
    private static final DateTimeFormatter CCD_DATE_FORMAT = CaveatCallbackResponseTransformer.dateTimeFormatter;
    private String jsonRequest;
    private String jsonResponse;

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void shouldSetIhtFormId() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT205.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormId","IHT400");
    }

    private void transformExceptionPostSuccess(String bodyText, String field, String expected) {
        RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders())
            .body(bodyText)
            .when().post(TRANSFORM_EXCEPTON_RECORD)
            .then().assertThat().statusCode(200)
            .body(field, equalTo(expected));
    }

}
