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
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.hmcts.probate.functional.util.FunctionalTestUtils;

import java.util.HashMap;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
public abstract class IntegrationTestBase {

    protected RestAssuredConfig config;

    @Autowired
    protected SolCCDServiceAuthTokenGenerator serviceAuthTokenGenerator;

    @Rule
    public SpringIntegrationMethodRule springIntegration;

    private String solCcdServiceUrl;
    public static String evidenceManagementUrl;

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
                        .setParam("http.connection.timeout", 60000)
                        .setParam("http.socket.timeout", 60000)
                        .setParam("http.connection-manager.timeout", 60000));
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

    protected String getJsonFromFile(String jsonFileName) {
        return utils.getJsonFromFile(jsonFileName);
    }

    protected ResponseBody validatePostSuccessForPayload(String payload, String path) {

        return validatePostSuccessForPayload(payload, path, utils.getHeadersWithUserId());
    }

    protected ResponseBody validatePostSuccessForPayload(String payload, String path, Headers headers) {
        Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(headers)
            .body(payload)
            .when().post(path)
            .andReturn();

        response.then().assertThat().statusCode(200);

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

    protected final ResponseBody validatePostSuccess(String jsonFileName, String path) {
        return validatePostSuccessForPayload(utils.getJsonFromFile(jsonFileName), path);
    }

    protected final ResponseBody validatePostSuccessWithAttributeUpdate(String jsonFileName, String path,
                                                                        String originalAttr,
                                                                  String updatedAttr) {
        String request = getJsonFromFile(jsonFileName);
        request = replaceAllInString(request, originalAttr, updatedAttr);
        return validatePostSuccessForPayload(request, path);
    }

    protected void assertExpectedContents(String expectedResponseFile, String responseDocumentUrl,
                                          ResponseBody responseBody) {
        final String expectedText = removeCrLfs(getJsonFromFile(expectedResponseFile));

        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String documentUrl = jsonPath.get(responseDocumentUrl);
        final String response = removeCrLfs(utils.downloadPdfAndParseToString(documentUrl));
        assertTrue(response.contains(expectedText));
    }

    protected void assertExpectedContentsForHeaders(String expectedResponseFile, String responseDocumentUrl,
                                                    ResponseBody responseBody, Headers headers) {
        final String expectedText = removeCrLfs(getJsonFromFile(expectedResponseFile));

        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String documentUrl = jsonPath.get(responseDocumentUrl);
        final String response = removeCrLfs(utils.downloadPdfAndParseToStringForHeaders(documentUrl, headers));
        assertTrue(response.contains(expectedText));
    }

    protected void assertExpectedContentsWithExpectedReplacement(String expectedResponseFile,
        String responseDocumentUrl, ResponseBody responseBody, HashMap<String, String> expectedKeyValuerelacements) {
        String expectedText = removeCrLfs(getJsonFromFile(expectedResponseFile));
        for (String key : expectedKeyValuerelacements.keySet()) {
            expectedText = expectedText.replace(key, expectedKeyValuerelacements.get(key));
        }

        log.info("assertExpectedContentsWithExpectedReplacement.responseBody.asString():" + responseBody.asString());
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String documentUrl = jsonPath.get(responseDocumentUrl);
        final String response = removeCrLfs(utils.downloadPdfAndParseToString(documentUrl));
        assertTrue(response.contains(expectedText));
    }

    protected void assertExpectedContentsMissing(String expectedContentMissing, ResponseBody responseBody) {
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String documentUrl = jsonPath.get(expectedContentMissing);
        assertTrue(documentUrl == null);
    }
}
