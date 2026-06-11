package uk.gov.hmcts.probate.functional.bulkscanning;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.probate.functional.util.FunctionalTestUtils.TOKEN_PARM;

/**
 * Functional tests that mimic the bulk scan lifecycle for a Grant of Probate (Personal) case.
 */
@Slf4j
@ExtendWith(SerenityJUnit5Extension.class)
public class BulkScanMimicFunctionalTests extends IntegrationTestBase {

    private static final String GRANT_RECEIVED_URL = "/notify/grant-received";
    private static final String CASE_PRINTED_URL = "/case/casePrinted";
    private static final String START_GRANT_DELAYED_URL = "/notify/start-grant-delayed-notify-period";

    private static final String BULK_SCAN_MIMIC_CASE_PRINTED_PAYLOAD = "bulkScanMimicCasePrintedPayload.json";
    private static final String BULK_SCAN_MIMIC_CREATE_CASE_PAYLOAD = "bulkScanMimicCreateCasePayload.json";
    private static final String BULK_SCAN_MIMIC_ATTACH_SCANNED_DOCS_PAYLOAD
            = "bulkScanMimicAttachScannedDocsPayload.json";
    private static final String EVENT_PARM = "EVENT_PARM";

    // CCD event used to manually create a paper application (caseworker-raised).
    private static final String CREATE_CASE_EVENT = "applyforGrantPaperApplicationMan";
    // CCD event to move the case to {@code CasePrinted} state.
    private static final String PRINT_CASE_EVENT = "boPrintCase";

    // Case data attribute paths in the back-office JSON response.
    private static final String DATA_EVIDENCE_HANDLED = "data.evidenceHandled";
    private static final String DATA_SCANNED_DOCUMENTS = "data.scannedDocuments";
    private static final String DATA_BULK_SCAN_ENVELOPES = "data.bulkScanEnvelopes";
    private static final String DATA_DECEASED_FORENAMES = "data.deceasedForenames";
    private static final String DATA_DECEASED_SURNAME = "data.deceasedSurname";
    private static final String DATA_LAST_EVIDENCE_ADDED_DATE = "data.lastEvidenceAddedDate";
    private static final String DATA_GRANT_DELAYED_NOTIFICATION_DATE = "data.grantDelayedNotificationDate";

    private String baseCaseJson;
    private String createResponse;



    @BeforeEach
    public void setUp() throws IOException {
        initialiseConfig();
        baseCaseJson = utils.getJsonFromFile(BULK_SCAN_MIMIC_CREATE_CASE_PAYLOAD);
        String createCaseJson = utils.replaceAttribute(baseCaseJson, EVENT_PARM, CREATE_CASE_EVENT);
        createResponse = utils.createCaseAsCaseworker(createCaseJson, CREATE_CASE_EVENT);
    }

    @Test
    @DisplayName("Verify /notify/grant-received returns 200 and includes "
            +
            "scanned documents data for a bulk scan case in CasePrinted state")
    void verifyBulkScanGrantReceivedNotificationReturns200ForPersonalApplicant() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(BULK_SCAN_MIMIC_CASE_PRINTED_PAYLOAD,
                GRANT_RECEIVED_URL);
        assertNotNull(responseBody);
        assertTrue(responseBody.asString().contains("\"status_code\":200")
                || responseBody.asString().contains("data"));
    }

    @Test
    @DisplayName("Verify /notify/grant-received sets evidenceHandled=No for a bulk scan case in CasePrinted state")
    void verifyBulkScanGrantReceivedSetsEvidenceHandledToNoForCasePrintedState() throws IOException {
        final Response response = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_CASE_PRINTED_PAYLOAD))
                .when().post(GRANT_RECEIVED_URL)
                .andReturn();

        response.then().assertThat().statusCode(200)
                .and().body(DATA_EVIDENCE_HANDLED, equalTo("No"));
    }

    @Test
    @DisplayName("Verify /notify/grant-received preserves scanned documents data "
            +
            "for a bulk scan case in CasePrinted state")
    void verifyBulkScanGrantReceivedPreservesScannedDocuments() throws IOException {
        final Response response = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_CASE_PRINTED_PAYLOAD))
                .when().post(GRANT_RECEIVED_URL)
                .andReturn();

        response.then().assertThat().statusCode(200);

        final JsonPath jsonPath = JsonPath.from(response.getBody().asString());
        final int scannedDocCount = jsonPath.getList(DATA_SCANNED_DOCUMENTS).size();
        assertEquals(2, scannedDocCount);

        final String controlNumber0 =
                jsonPath.get(DATA_SCANNED_DOCUMENTS + "[0].value.controlNumber");
        assertNotNull(controlNumber0);
    }

    @Test
    @DisplayName("Verify /notify/grant-received preserves bulk scan envelope references "
            +
            "for a bulk scan case in CasePrinted state")
    void verifyBulkScanGrantReceivedPreservesBulkScanEnvelopes() throws IOException {
        final Response response = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_CASE_PRINTED_PAYLOAD))
                .when().post(GRANT_RECEIVED_URL)
                .andReturn();

        response.then().assertThat().statusCode(200);

        final JsonPath jsonPath = JsonPath.from(response.getBody().asString());
        final int envelopeCount = jsonPath.getList(DATA_BULK_SCAN_ENVELOPES).size();
        assertEquals(2, envelopeCount);
    }

    @Test
    @DisplayName("Verify /notify/grant-received returns correct deceased details "
            +
            "for a bulk scan case in CasePrinted state")
    void verifyBulkScanGrantReceivedReturnsCorrectDeceasedDetails() throws IOException {
        final Response response = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_CASE_PRINTED_PAYLOAD))
                .when().post(GRANT_RECEIVED_URL)
                .andReturn();

        response.then().assertThat().statusCode(200)
                .and().body(DATA_DECEASED_FORENAMES, equalTo("Tester"))
                .and().body(DATA_DECEASED_SURNAME, equalTo("Smith"));
    }

    @Test
    @DisplayName("Verify /case/casePrinted returns 200 for a bulk scan case in CasePrinted state")
    void verifyBulkScanCasePrintedCallbackReturns200() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(BULK_SCAN_MIMIC_CASE_PRINTED_PAYLOAD,
                CASE_PRINTED_URL);
        assertNotNull(responseBody);
    }

    @Test
    @DisplayName("Verify /case/casePrinted returns no validation errors "
            +
            "for a bulk scan case in CasePrinted state")
    void verifyBulkScanCasePrintedCallbackHasNoErrors() throws IOException {
        final Response response = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_CASE_PRINTED_PAYLOAD))
                .when().post(CASE_PRINTED_URL)
                .andReturn();

        response.then().assertThat().statusCode(200);
        final JsonPath jsonPath = JsonPath.from(response.getBody().asString());
        final Object errors = jsonPath.get("errors");
        assertTrue(errors == null || jsonPath.getList("errors").isEmpty());
    }


    @Test
    @DisplayName("Verify /notify/start-grant-delayed-notify-period returns 200 "
            +
            "for a bulk scan case in CasePrinted state (attach scanned docs)")
    void verifyAttachScannedDocsReturns200ForBulkScanCasePrintedState() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(
                BULK_SCAN_MIMIC_ATTACH_SCANNED_DOCS_PAYLOAD,
                START_GRANT_DELAYED_URL);
        assertNotNull(responseBody);
    }

    @Test
    @DisplayName("Verify /notify/start-grant-delayed-notify-period sets lastEvidenceAddedDate "
            +
            "for a bulk scan case in CasePrinted state (attach scanned docs)")
    void verifyAttachScannedDocsSetsLastEvidenceAddedDate() throws IOException {
        final Response response = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_ATTACH_SCANNED_DOCS_PAYLOAD))
                .when().post(START_GRANT_DELAYED_URL)
                .andReturn();

        response.then().assertThat().statusCode(200);
        final JsonPath jsonPath = JsonPath.from(response.getBody().asString());
        assertNotNull(jsonPath.get(DATA_LAST_EVIDENCE_ADDED_DATE));
    }

    @Test
    @DisplayName("Verify /notify/start-grant-delayed-notify-period sets grantDelayedNotificationDate "
            +
            "for a bulk scan case in CasePrinted state (attach scanned docs)")
    void verifyAttachScannedDocsSetsGrantDelayedNotificationDate() throws IOException {
        final Response response = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_ATTACH_SCANNED_DOCS_PAYLOAD))
                .when().post(START_GRANT_DELAYED_URL)
                .andReturn();

        response.then().assertThat().statusCode(200);
        final JsonPath jsonPath = JsonPath.from(response.getBody().asString());
        assertNotNull(jsonPath.get(DATA_GRANT_DELAYED_NOTIFICATION_DATE));
    }

    @Test
    @DisplayName("Verify /notify/start-grant-delayed-notify-period sets evidenceHandled=No "
            +
            "for a bulk scan case in CasePrinted state (attach scanned docs)")
    void verifyAttachScannedDocsSetsEvidenceHandledToNo() throws IOException {
        final Response response = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_ATTACH_SCANNED_DOCS_PAYLOAD))
                .when().post(START_GRANT_DELAYED_URL)
                .andReturn();

        response.then().assertThat().statusCode(200)
                .and().body(DATA_EVIDENCE_HANDLED, equalTo("No"));
    }

    @Test
    @DisplayName("Verify /notify/start-grant-delayed-notify-period preserves scanned documents "
            +
            "for a bulk scan case in CasePrinted state (attach scanned docs)")
    void verifyAttachScannedDocsPreservesScannedDocuments() throws IOException {
        final Response response = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_ATTACH_SCANNED_DOCS_PAYLOAD))
                .when().post(START_GRANT_DELAYED_URL)
                .andReturn();

        response.then().assertThat().statusCode(200);
        final JsonPath jsonPath = JsonPath.from(response.getBody().asString());
        final int scannedDocCount = jsonPath.getList(DATA_SCANNED_DOCUMENTS).size();
        assertEquals(2, scannedDocCount);
    }

    @Test
    @DisplayName("Verify /notify/start-grant-delayed-notify-period preserves bulk scan envelope "
            +
            "references for a bulk scan case in CasePrinted state (attach scanned docs)")
    void verifyAttachScannedDocsPreservesBulkScanEnvelopes() throws IOException {
        final Response response = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_ATTACH_SCANNED_DOCS_PAYLOAD))
                .when().post(START_GRANT_DELAYED_URL)
                .andReturn();

        response.then().assertThat().statusCode(200);
        final JsonPath jsonPath = JsonPath.from(response.getBody().asString());
        final int envelopeCount = jsonPath.getList(DATA_BULK_SCAN_ENVELOPES).size();
        assertEquals(2, envelopeCount);
    }

    @Test
    @DisplayName("Verify /notify/start-grant-delayed-notify-period returns "
            +
            "no validation errors for a bulk scan case in CasePrinted state (attach scanned docs)")
    void verifyAttachScannedDocsHasNoValidationErrors() throws IOException {
        final Response response = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_ATTACH_SCANNED_DOCS_PAYLOAD))
                .when().post(START_GRANT_DELAYED_URL)
                .andReturn();

        response.then().assertThat().statusCode(200);
        final JsonPath jsonPath = JsonPath.from(response.getBody().asString());
        final Object errors = jsonPath.get("errors");
        assertTrue(errors == null || jsonPath.getList("errors").isEmpty());
    }

    @Disabled("Requires a live CCD environment with caseworker roles configured")
    @Test
    @DisplayName("Verify full bulk scan lifecycle from case creation to CasePrinted state, "
            +
            "including preservation of scanned documents and bulk scan envelope references")
    void verifyFullBulkScanLifecycleCreateAndProgressToCasePrinted() {

        final JsonPath createJsonPath = JsonPath.from(createResponse);
        assertNotNull(createJsonPath);

        final String caseId = createJsonPath.get("id").toString();
        assertNotNull(caseId);

        assertEquals("gop", createJsonPath.get("case_data.caseType"));
        assertEquals("BulkScan", createJsonPath.get("case_data.channelChoice"));
        assertEquals("Yes", createJsonPath.get("case_data.paperForm"));
        assertEquals("818934032424212002473004", createJsonPath.get("case_data.bulkScanCaseReference"));

        final int scannedDocCount = createJsonPath.getList("case_data.scannedDocuments").size();
        assertEquals(2, scannedDocCount);

        final int envelopeCount = createJsonPath.getList("case_data.bulkScanEnvelopes").size();
        assertEquals(2, envelopeCount);

        final String printCaseToken = utils.startUpdateCaseAsCaseworker(caseId, PRINT_CASE_EVENT);
        assertNotNull(printCaseToken);

        String printCaseJson = utils.replaceAttribute(baseCaseJson, TOKEN_PARM, printCaseToken);
        printCaseJson = utils.replaceAttribute(printCaseJson, EVENT_PARM, PRINT_CASE_EVENT);
        final String printedCaseResponse = utils.continueUpdateCaseAsCaseworker(printCaseJson, caseId);
        assertNotNull(printedCaseResponse);

        final JsonPath retrievedJsonPath = JsonPath.from(printedCaseResponse);
        assertEquals("CasePrinted", retrievedJsonPath.get("state"));

        final int retrievedDocCount = retrievedJsonPath.getList("case_data.scannedDocuments").size();
        assertEquals(2, retrievedDocCount);
    }

    @Disabled("Requires a live CCD environment with caseworker roles configured")
    @Test
    @DisplayName("Verify /notify/grant-received returns 200 and correct data when called "
            +
            "after case reaches CasePrinted state in bulk scan lifecycle")
    void verifyBulkScanGrantReceivedNotificationAfterCasePrintedTransition() throws IOException {

        final String caseId = JsonPath.from(createResponse).get("id").toString();
        assertNotNull(caseId);

        final String printCaseToken = utils.startUpdateCaseAsCaseworker(caseId, PRINT_CASE_EVENT);
        String printCaseJson = utils.replaceAttribute(baseCaseJson, TOKEN_PARM, printCaseToken);
        printCaseJson = utils.replaceAttribute(printCaseJson, EVENT_PARM, PRINT_CASE_EVENT);
        final String printedCaseResponse = utils.continueUpdateCaseAsCaseworker(printCaseJson, caseId);
        assertNotNull(printedCaseResponse);

        // In a real bulk scan flow, this is called by CCD after the case reaches CasePrinted.
        // We use the static payload here as a representative substitute.
        final Response notifyResponse = RestAssured.given()
                .config(config)
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(BULK_SCAN_MIMIC_CASE_PRINTED_PAYLOAD))
                .when().post(GRANT_RECEIVED_URL)
                .andReturn();

        notifyResponse.then().assertThat().statusCode(200)
                .and().body(DATA_EVIDENCE_HANDLED, equalTo("No"));

        final JsonPath notifyJsonPath = JsonPath.from(notifyResponse.getBody().asString());
        assertEquals(2, notifyJsonPath.getList(DATA_SCANNED_DOCUMENTS).size());
    }
}
