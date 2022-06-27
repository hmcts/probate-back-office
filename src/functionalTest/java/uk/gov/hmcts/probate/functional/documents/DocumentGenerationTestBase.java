package uk.gov.hmcts.probate.functional.documents;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

public abstract class DocumentGenerationTestBase extends IntegrationTestBase {
    protected static final String GENERATE_GRANT = "/document/generate-grant";
    protected static final String GENERATE_GRANT_REISSUE = "/document/generate-grant-reissue";
    protected static final String GENERATE_GRANT_DRAFT = "/document/generate-grant-draft";
    protected static final String GENERATE_DEPOSIT_RECEIPT = "/document/generate-deposit-receipt";
    protected static final String GENERATE_GRANT_DRAFT_REISSUE = "/document/generate-grant-draft-reissue";
    protected static final String DEFAULT_SOLS_PAYLOAD = "solicitorPayloadNotifications.json";
    protected static final String TRUST_CORPS_GOP_PAYLOAD = "solicitorPayloadTrustCorpsTransformed.json";
    protected static final String GRANT_DOC_NAME = "probateDocumentsGenerated[0].value.DocumentLink";
    protected static final String DEFAULT_PA_PAYLOAD = "personalPayloadNotifications.json";
    protected static final String DEFAULT_REISSUE_PAYLOAD = "personalPayloadReissueDuplicate.json";

    protected String getDocumentTextAtPath(String jsonFileName, String path, String documentName) throws IOException {
        final Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(path).andReturn();

        jsonResponse.then().assertThat().statusCode(200);

        final JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());

        final String documentUrl =
            jsonPath.get("data." + documentName + ".document_binary_url");
        String response = utils.downloadPdfAndParseToString(documentUrl);
        response = response.replace("\n", "").replace("\r", "");
        return response;
    }

    protected JsonPath postAndGetJsonPathResponse(String jsonFileName, String path) throws IOException {

        final Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(path).andReturn();

        jsonResponse.then().assertThat().statusCode(200);

        return JsonPath.from(jsonResponse.getBody().asString());
    }

    protected String getFirstProbateDocumentsText(String jsonFileName, String path) throws IOException {
        return getDocumentTextAtPath(jsonFileName, path, "probateDocumentsGenerated[0].value.DocumentLink");
    }

    protected String getProbateDocumentsGeneratedTextAtIndex(String jsonFileName, String path, String index)
        throws IOException {
        return getDocumentTextAtPath(jsonFileName, path, "probateDocumentsGenerated[" + index + "].value.DocumentLink");
    }

    protected String getDocumentText(JsonPath jsonPath, String documentName) {
        final String documentUrl =
            jsonPath.get("data." + documentName + ".document_binary_url");
        String response = utils.downloadPdfAndParseToString(documentUrl);
        response = response.replace("\n", "").replace("\r", "");
        return response;

    }

    protected String generateDocumentFromPayload(String payload, String path, String documentName) {

        Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(payload)
            .when().post(path).andReturn();

        JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());

        final String documentUrl =
            jsonPath.get("data." + documentName + ".document_binary_url");
        final String response = utils.downloadPdfAndParseToString(documentUrl);
        return removeCrLfs(response);
    }

    protected String generateDocument(String jsonFileName, String path, String documentName) throws IOException {
        return generateDocumentFromPayload(utils.getJsonFromFile(jsonFileName), path, documentName);
    }


}
