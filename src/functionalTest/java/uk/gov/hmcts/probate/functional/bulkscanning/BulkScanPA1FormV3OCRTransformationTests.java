package uk.gov.hmcts.probate.functional.bulkscanning;

import io.restassured.RestAssured;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;

import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringIntegrationSerenityRunner.class)
public class BulkScanPA1FormV3OCRTransformationTests extends IntegrationTestBase {

    private static final String TRANSFORM_EXCEPTON_RECORD = "/transform-scanned-data";
    private static final DateTimeFormatter CCD_DATE_FORMAT = CaveatCallbackResponseTransformer.dateTimeFormatter;
    private String jsonRequest;
    private String jsonResponse;

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void shouldSetIht400CompletedBeforeSwitchDate() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT400_PRE_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.iht400completed","Yes");
    }

    @Test
    public void shouldSetIht400CompletedAfterSwitchDate() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT400_POST_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.iht400completed","Yes");
    }

    @Test
    public void shouldSetIht205Completed() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT205_PRE_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.iht205completed","Yes");
    }

    @Test
    public void shouldSetIht207Completed() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT207.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.iht207completed","Yes");
    }

    @Test
    public void shouldSetIht400421Completed() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT400421.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.iht400421completed","Yes");
    }

    @Test
    public void shouldSetExceptedEstates() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_Excepted_Estates.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.exceptedEstate","Yes");
    }

    @Test
    public void shouldSetIht400Process() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT400_Process.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.iht400process","Yes");
    }

    @Test
    public void shouldSetIht400Code() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHTCode.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtCode","Yes");
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
