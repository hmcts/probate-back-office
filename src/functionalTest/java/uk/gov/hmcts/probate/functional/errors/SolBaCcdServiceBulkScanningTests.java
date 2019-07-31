package uk.gov.hmcts.probate.functional.errors;

import io.restassured.path.json.*;
import io.restassured.response.*;
import net.serenitybdd.junit.runners.*;
import net.serenitybdd.rest.*;
import org.junit.*;
import org.junit.runner.*;
import uk.gov.hmcts.probate.functional.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;


@RunWith(SerenityRunner.class)
public class SolBaCcdServiceBulkScanningTests extends IntegrationTestBase {

    private static final String ATTACH_SCAN_DOC_FROM_UI_ERROR = "You cannot attach a document to a case using this event. Please use Upload Documents instead.";

    private static final String UI_ATTACH_SCANNED_DOCS_ERROR = "/bulk-scanning/attach-scanned-docs-error";

    private static final String BASIC_CASE_PAYLOAD = "{\"case_details\": {\"id\": 1528365719153338} }";

    private void validatePostSuccess(String bodyText, String path, String containsText) {
        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body(bodyText)
                .when().post(path)
                .then().assertThat().statusCode(200)
                .and().body("errors", hasSize(1))
                .and().body("errors[0]", equalTo(containsText));
    }

    @Test
    public void verifyAttachScannedDocsErrorMessage() {
        validatePostSuccess(BASIC_CASE_PAYLOAD, UI_ATTACH_SCANNED_DOCS_ERROR, ATTACH_SCAN_DOC_FROM_UI_ERROR);
    }
}
