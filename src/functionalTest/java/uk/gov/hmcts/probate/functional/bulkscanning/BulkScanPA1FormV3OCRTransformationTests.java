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
    private static final String IHT_FORM_ID = "case_creation_details.case_data.ihtFormId";
    private static final String IHT_FORM_ESTATE = "case_creation_details.case_data.ihtFormEstate";
    private String jsonRequest;

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void shouldSetIht400AfterSwitchDate() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT400_POST_EE.json");
        transformExceptionPostSuccess(jsonRequest, IHT_FORM_ESTATE,"IHT400");
    }

    @Test
    public void shouldSetIht205BeforeSwitchDate() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT205_PRE_EE.json");
        transformExceptionPostSuccess(jsonRequest, IHT_FORM_ID,"IHT205");
    }

    @Test
    public void shouldSetIht207BeforeSwitchDate() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT207_PRE_EE.json");
        transformExceptionPostSuccess(jsonRequest, IHT_FORM_ID,"IHT207");
    }

    @Test
    public void shouldSetIht207AfterSwitchDate() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT207_POST_EE.json");
        transformExceptionPostSuccess(jsonRequest, IHT_FORM_ESTATE,"IHT207");
    }

    @Test
    public void shouldSetIht400421BeforeSwitchDate() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT400421_PRE_EE.json");
        transformExceptionPostSuccess(jsonRequest, IHT_FORM_ID,"IHT400421");
    }

    @Test
    public void shouldSetIht400421AfterSwitchDate() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT400421_POST_EE.json");
        transformExceptionPostSuccess(jsonRequest, IHT_FORM_ESTATE,"IHT400421");
    }

    @Test
    public void shouldSetNullNotSubmittedFormAfterSwitchDate() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_Excepted_Estates.json");
        transformExceptionPostSuccess(jsonRequest, IHT_FORM_ID,null);
    }

    @Test
    public void shouldSetHmrcLetter() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT400_POST_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.hmrcLetterId","Yes");
    }

    @Test
    public void shouldSetUniqueProbateCode() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version3/transformation/PA1P_IHT400_POST_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.uniqueProbateCodeId","CTS_CODE");
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
