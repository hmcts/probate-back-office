package uk.gov.hmcts.probate.functional.bulkscanning;

import io.restassured.RestAssured;
import java.time.format.DateTimeFormatter;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import static org.hamcrest.Matchers.equalTo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;

@RunWith(SpringIntegrationSerenityRunner.class)
public class BulkScanPA1FormV2OCRTransformationTests extends IntegrationTestBase {

    private static final String TRANSFORM_EXCEPTON_RECORD = "/transform-scanned-data";
    private static final DateTimeFormatter CCD_DATE_FORMAT = CaveatCallbackResponseTransformer.dateTimeFormatter;
    private String jsonRequest;
    private String jsonResponse;

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void shouldSetIhtFormId() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT205.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormId","IHT205");
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
