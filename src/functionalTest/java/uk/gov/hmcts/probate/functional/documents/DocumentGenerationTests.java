package uk.gov.hmcts.probate.functional.documents;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SerenityJUnit5Extension.class)
public class DocumentGenerationTests extends DocumentGenerationTestBase {

    private static final String ASSEMBLE_LETTER = "/document/assembleLetter";
    private static final String DEFAULT_PRINT_VALUES = "/document/default-reprint-values";
    private static final String GENERATE_LETTER = "/document/generateLetter";
    private static final String PREVIEW_LETTER = "/document/previewLetter";
    private static final String RE_PRINT = "/document/reprint";
    private static final String DEFAULT_WILL_NO_DOCS_PAYLOAD = "willLodgementPayloadNoDocs.json";
    private static final String GENERATE_LETTER_PAYLOAD = "/document/generateLetter.json";
    private static final String NON_PROBATE_DOC_NAME = "documentsGenerated[0].value.DocumentLink";
    private static final String EVIDENCE_ADDED = "/document/evidenceAdded";

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void verifySuccessForWillLodgementForCardiff() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateNonProbateDocument(DEFAULT_WILL_NO_DOCS_PAYLOAD, GENERATE_DEPOSIT_RECEIPT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("willLodgementDepositReceiptResponse.txt"));
        expectedText = expectedText.replace("19th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }


    private String generateNonProbateDocument(String jsonFileName, String path) throws IOException {
        return generateDocument(jsonFileName, path, NON_PROBATE_DOC_NAME);
    }


    @Test
    void verifyAssembleLetterShouldReturnOkResponseCode() throws IOException {
        final ResponseBody response = validatePostSuccess("/document/assembleLetterPayLoad.json",
            ASSEMBLE_LETTER);
        final JsonPath jsonPath = JsonPath.from(response.asString());
        final List paragraphDetails = jsonPath.get("data.paragraphDetails");
        final String templateName = jsonPath.get("data.paragraphDetails[1].value.templateName");

        assertEquals(paragraphDetails.size(), 3);
        assertEquals(templateName, ParagraphCode.MissInfoWill.getTemplateName());
    }

    @Test
    void verifyAssembleLetterShouldReturnIHTReferenceNumber() throws IOException {
        final String jsonAsString = getJsonFromFile("/document/assembleLetterTransform.json");
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(jsonAsString)
            .when().post(ASSEMBLE_LETTER)
            .andReturn();

        final JsonPath jsonPath = JsonPath.from(response.asString());
        response.then().assertThat().statusCode(200);
        assertEquals(jsonPath.get("data.ihtReferenceNumber"), "ONLINE-123434");
    }

    @Test
    void verifyDefaultRePrintValuesReturnsOkResponseCode() throws IOException {
        final ResponseBody response =
            validatePostSuccess("/document/rePrintDefaultGrantOfProbate.json", DEFAULT_PRINT_VALUES);

        final JsonPath jsonPath = JsonPath.from(response.asString());
        assertEquals(jsonPath.get("data.reprintDocument.list_items[0].label"), "Grant");
        assertEquals(jsonPath.get("data.reprintDocument.list_items[0].code"), "WelshGrantFileName");
    }

    @Test
    void verifyDefaultRePrintValuesReturnsIhtReferenceNumber() throws IOException {
        String jsonAsString = getJsonFromFile("/document/rePrintDefaultGrantOfProbate.json");
        jsonAsString = jsonAsString.replaceFirst("\"paperForm\": \"Yes\",", "\"paperForm\": \"No\",");

        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(jsonAsString)
            .when().post(DEFAULT_PRINT_VALUES)
            .andReturn();
        assertEquals(response.getStatusCode(), 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertEquals(jsonPath.get("data.ihtReferenceNumber"), "ONLINE-123434");
    }

    @Test
    void verifySolicitorGenerateLetterReturnOkResponseCode() throws IOException {
        final String response = getFirstProbateDocumentsText(GENERATE_LETTER_PAYLOAD, GENERATE_LETTER);
        assertEquals(getJsonFromFile("/document/assembledLetter.txt"), response);
    }

    @Test
    void verifySolicitorGenerateLetterReturnsIHTReferenceNumber() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccess("/document/generateLetterDefaultLocation.json", GENERATE_LETTER);
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        assertEquals(jsonPath.get("data.ihtFormId"), "IHT205");
        assertNull(jsonPath.get("data.errors"));
    }

    @Test
    void verifySolicitorPreviewLetterReturnsCorrectResponse() throws IOException {
        final Response jsonResponse = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(GENERATE_LETTER_PAYLOAD))
            .when().post(PREVIEW_LETTER).andReturn();
        final JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());
        final String documentUrl = jsonPath.get("data.previewLink.document_binary_url");

        String response = removeCrLfs(utils.downloadPdfAndParseToString(documentUrl));
        assertEquals(response, getJsonFromFile("/document/previewLetterResponse.txt"));
    }

    @Test
    void verifySolicitorPreviewLetterReturnsIHTReferenceNumber() throws IOException {
        final ResponseBody responseBody = validatePostSuccess("/document/generateLetterDefaultLocation.json",
                PREVIEW_LETTER);
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        assertEquals(jsonPath.get("data.ihtFormId"), "IHT205");
        assertNull(jsonPath.get("data.errors"));
    }

    @Test
    void verifyEvidenceAdded() throws IOException {
        final ResponseBody responseBody = validatePostSuccess("/document/generateLetterDefaultLocation.json",
                EVIDENCE_ADDED);
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        assertNotNull(jsonPath.get("data.lastEvidenceAddedDate"));
    }

    @Test
    void verifySolicitorRePrintReturnBadResponseCode() throws IOException {
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersForUnauthorisedServiceAndUser())
            .body(getJsonFromFile("/document/rePrint.json"))
            .when().post(RE_PRINT)
            .andReturn();
        assertEquals(response.statusCode(), 403);

    }

    @Test
    void shouldPostForDocumentRemovalsGrant() throws IOException {
        validatePostSuccess("document/rePrintDefaultGrantOfProbate.json", "/document/setup-for-permanent-removal");
        validatePostSuccess("document/grantDocumentsPayload.json", "/document/permanently-delete-removed");
    }

    @Test
    void shouldPostForDocumentRemovalsWill() throws IOException {
        validatePostSuccess("willLodgementPayload.json", "/document/setup-for-permanent-removal-will");
        validatePostSuccess("willLodgementDocumentsPayload.json", "/document/permanently-delete-removed-will");
    }
}
