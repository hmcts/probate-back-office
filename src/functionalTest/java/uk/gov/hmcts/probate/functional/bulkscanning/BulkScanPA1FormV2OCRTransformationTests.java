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
    void shouldSetIhtFormCompletedOnline() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT205_Online.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormCompletedOnline","Yes");
    }

    @Test
    void shouldSetIhtFormId() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT205.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormId","IHT400");
    }
    
    @Test
    void shouldSetDeceasedHadLateSpouseOrCivilPartnerYes() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/widowed.json");
        transformExceptionPostSuccess(jsonRequest, 
            "case_creation_details.case_data.deceasedHadLateSpouseOrCivilPartner","Yes");
    }
    
    @Test
    void shouldSetDeceasedHadLateSpouseOrCivilPartnerNo() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/neverMarried.json");
        transformExceptionPostSuccess(jsonRequest, 
            "case_creation_details.case_data.deceasedHadLateSpouseOrCivilPartner","No");
    }
    
    @Test
    void shouldSetihtFormId400421() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT400421_PRE_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormId","IHT400421");
    }

    @Test
    void shouldSetihtFormEstate400421() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT400421_POST_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormEstate","IHT400421");
    }
    
    @Test
    void shouldSetihtFormId207() {
        jsonRequest = utils.getStringFromFile("/json/bulkscan/version2/transformation/PA1P_IHT207_PRE_EE.json");
        transformExceptionPostSuccess(jsonRequest, "case_creation_details.case_data.ihtFormId","IHT207");
    }

    @Test
    void shouldSetihtFormEstate207() {
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
