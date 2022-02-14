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
    public void shouldSetIhtFormCompletedOnline() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT205_Online.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormCompletedOnline","Yes");
    }

    @Test
    public void shouldSetIhtFormId() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT205.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormId","IHT205");
    }
    
    @Test
    public void shouldSetDeceasedHadLateSpouseOrCivilPartnerYes() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/widowed.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.deceasedHadLateSpouseOrCivilPartner","Yes");
    }
    
    @Test
    public void shouldSetDeceasedHadLateSpouseOrCivilPartnerNo() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/neverMarried.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.deceasedHadLateSpouseOrCivilPartner","No");
    }
    
    @Test
    public void shouldSetihtFormId400421() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT400421_PRE_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormId","IHT400421");
    }

    @Test
    public void shouldSetihtFormEstate400421() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT400421_POST_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormEstate","IHT400421");
    }
    
    @Test
    public void shouldSetihtFormId207() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT207_PRE_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormId","IHT207");
    }

    @Test
    public void shouldSetihtFormEstate207() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT207_POST_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormEstate","IHT207");
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
