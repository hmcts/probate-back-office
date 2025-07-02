package uk.gov.hmcts.probate.functional;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.hmcts.probate.functional.util.FunctionalTestUtils;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.nullValue;


@Slf4j
@ExtendWith(SerenityJUnit5Extension.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
public abstract class IntegrationTestBase {

    protected RestAssuredConfig config;

    @Autowired
    protected SolCCDServiceAuthTokenGenerator serviceAuthTokenGenerator;

    public SpringIntegrationMethodRule springIntegration;

    private String solCcdServiceUrl;
    public static String evidenceManagementUrl;
    private static final long ES_DELAY = 20000L;

    @Autowired
    public void solCcdServiceUrl(@Value("${sol.ccd.service.base.url}") String solCcdServiceUrl) {
        this.solCcdServiceUrl = solCcdServiceUrl;
        RestAssured.baseURI = solCcdServiceUrl;
    }

    @Autowired
    public void evidenceManagementUrl(@Value("${evidence.management.url}") String evidenceManagementUrl) {
        this.evidenceManagementUrl = evidenceManagementUrl;

    }

    public static void setEvidenceManagementUrlAsBaseUri() {
        RestAssured.baseURI = evidenceManagementUrl;
    }

    @Autowired
    protected FunctionalTestUtils utils;

    public IntegrationTestBase() {
        this.springIntegration = new SpringIntegrationMethodRule();

    }

    protected void initialiseConfig() {
        RestAssured.useRelaxedHTTPSValidation();
        config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 120000)
                        .setParam("http.socket.timeout", 120000)
                        .setParam("http.connection-manager.timeout", 120000));
    }

    protected String replaceAllInString(String request, String originalAttr, String updatedAttr) {
        return request.replaceAll(Pattern.quote(originalAttr), updatedAttr);
    }

    protected String removeCarriageReturns(String text) {
        return text.replaceAll(Pattern.quote("\r"), "");
    }

    protected String removeLineFeeds(String text) {
        return text.replaceAll(Pattern.quote("\n"), "");
    }

    protected String removeCrLfs(String text) {
        return removeLineFeeds(removeCarriageReturns(text));
    }

    protected String getJsonFromFile(String jsonFileName) throws IOException {
        return utils.getJsonFromFile(jsonFileName);
    }

    protected ResponseBody validatePostSuccessForPayload(String payload, String path) {

        return validatePostSuccessForPayload(payload, path, utils.getHeadersWithUserId());
    }

    protected ResponseBody validatePostSuccessForPayload(String payload, String path, Headers headers) {
        Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(headers)
            .body(payload)
            .when().post(path)
            .andReturn();

        response.then().assertThat().statusCode(200);

        return response.getBody();
    }

    protected ResponseBody validatePutSuccess(String fileName, String path, int statusCode) throws IOException {
        Headers headers = utils.getHeadersNoUser();
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(headers)
                .body(utils.getJsonFromFile(fileName))
                .when().put(path)
                .andReturn();

        response.then().assertThat().statusCode(statusCode);

        return response.getBody();
    }

    protected ResponseBody validatePostSuccessForQueryParms(String path, HashMap<String, String> queryParms) {
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .queryParams(queryParms)
            .when().post(path)
            .andReturn();

        response.then().assertThat().statusCode(200);

        return response.getBody();
    }

    protected final ResponseBody validatePostSuccess(String jsonFileName, String path) throws IOException {
        return validatePostSuccessForPayload(utils.getJsonFromFile(jsonFileName), path);
    }

    protected final ResponseBody validatePostSuccessForCaseStopped(String payload, String path)
            throws InterruptedException {
        Thread.sleep(ES_DELAY);
        return validatePostSuccessForPayload(payload, path);
    }


    protected final ResponseBody validatePostSuccessWithAttributeUpdate(String jsonFileName, String path,
                                                                        String originalAttr,
                                                                  String updatedAttr) throws IOException {
        String request = getJsonFromFile(jsonFileName);
        request = replaceAllInString(request, originalAttr, updatedAttr);
        return validatePostSuccessForPayload(request, path);
    }

    protected void assertExpectedContentsRegex(
            final String expectedResponseFile,
            final String responseDocumentUrl,
            final ResponseBody responseBody) throws IOException {
        final String expectedText = removeCrLfs(getJsonFromFile(expectedResponseFile));

        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String documentUrl = jsonPath.get(responseDocumentUrl);
        final String response = removeCrLfs(utils.downloadPdfAndParseToString(documentUrl));

        assertThat("Matching against file: " + expectedResponseFile, response, matchesRegex(expectedText));
    }

    protected void assertExpectedContents(String expectedResponseFile, String responseDocumentUrl,
                                          ResponseBody responseBody) throws IOException {
        final String expectedText = removeCrLfs(getJsonFromFile(expectedResponseFile));

        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String documentUrl = jsonPath.get(responseDocumentUrl);
        final String response = removeCrLfs(utils.downloadPdfAndParseToString(documentUrl));
        if (!response.contains(expectedText)) {
            log.error("Expected response (from {}) does not contain expected text:\nexpected:\n{}\n\nresponse:\n{}\n\n",
                    expectedResponseFile, expectedText, response);
        }
        assertThat("Expect to contain content from file: " + expectedResponseFile,
                response,
                containsString(expectedText));
    }

    protected void assertExpectedContentsWithExpectedReplacement(String expectedResponseFile,
        String responseDocumentUrl, ResponseBody responseBody, HashMap<String, String> expectedKeyValuerelacements)
        throws IOException {
        String expectedText = removeCrLfs(getJsonFromFile(expectedResponseFile));
        for (Map.Entry<String, String> entry : expectedKeyValuerelacements.entrySet()) {
            expectedText = expectedText.replace(entry.getKey(), entry.getValue());
        }

        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String documentUrl = jsonPath.get(responseDocumentUrl);
        final String response = removeCrLfs(utils.downloadPdfAndParseToString(documentUrl));
        assertThat("Expected to contain content from file: " + expectedResponseFile,
                response, containsString(expectedText));
    }

    protected void assertExpectedContentsMissing(String expectedContentMissing, ResponseBody responseBody) {
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String documentUrl = jsonPath.get(expectedContentMissing);
        assertThat("Expect documentUrl to be null", documentUrl, nullValue());
    }
}
