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
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;


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
    private static final Pattern EXPIRY_DATE_WELSH_PATTERN =
            Pattern.compile("Daw eich cafeat i ben ar:\\s+\\S+\\s+\\S+\\s+[0-9]+");

    private static final Pattern EXPIRY_DATE_ENGLISH_PATTERN =
            Pattern.compile("Your caveat expiry date is:\\s+\\S+\\s+\\S+\\s+[0-9]+");

    private static final String EXPIRY_DATE_PLACEHOLDER =
            "Your caveat expiry date is: {{EXPIRY_DATE}}";

    private static final Pattern SENT_ON_PATTERN =
            Pattern.compile("\\ASent on:.*?(?=From:)", Pattern.DOTALL);

    private static final Pattern UNICODE_SPACES =
            Pattern.compile("[\\s\\p{Z}\\u00A0\\u2007\\u202F]+");

    private static final Pattern INVISIBLE_CHARACTERS =
            Pattern.compile("[\\u200B\\u200C\\u200D\\u2060\\uFEFF]");

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

    protected String replaceAllInString(
            String request,
            String originalAttr,
            String updatedAttr
    ) {
        return request.replaceAll(
                Pattern.quote(originalAttr),
                Matcher.quoteReplacement(updatedAttr)
        );
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

    private String normalizePdfText(String text) {
        if (text == null) {
            return "";
        }

        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFKC);

        // Remove PDF-related invisible characters.
        normalizedText = INVISIBLE_CHARACTERS.matcher(normalizedText).replaceAll("");

        // Remove the dynamic "Sent on:" section while preserving "From:".
        normalizedText = SENT_ON_PATTERN.matcher(normalizedText).replaceFirst("");

        // Replace dynamic expiry dates.
        normalizedText = EXPIRY_DATE_WELSH_PATTERN.matcher(normalizedText)
                .replaceAll(Matcher.quoteReplacement(EXPIRY_DATE_PLACEHOLDER));

        normalizedText = EXPIRY_DATE_ENGLISH_PATTERN.matcher(normalizedText)
                .replaceAll(Matcher.quoteReplacement(EXPIRY_DATE_PLACEHOLDER));

        // Convert ordinary and Unicode whitespace to one regular space.
        return UNICODE_SPACES.matcher(normalizedText)
                .replaceAll(" ")
                .trim();
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


    protected final ResponseBody validatePostSuccessWithAttributeUpdate(String jsonFileName,
                                                                        String path,
                                                                        String originalAttr,
                                                                        String updatedAttr) throws IOException {
        String request = getJsonFromFile(jsonFileName);
        request = replaceAllInString(request, originalAttr, updatedAttr);
        return validatePostSuccessForPayload(request, path);
    }

    protected void assertExpectedContents(String expectedResponseFile,
                                          String responseDocumentUrl,
                                          ResponseBody responseBody) throws IOException {
        String expectedText = normalizePdfText(
                getJsonFromFile(expectedResponseFile)
        );

        JsonPath jsonPath = JsonPath.from(responseBody.asString());
        String documentUrl = jsonPath.getString(responseDocumentUrl);

        String response = normalizePdfText(
                utils.downloadPdfAndParseToString(documentUrl)
        );

        assertContainsNormalizedText(expectedText, response);
    }

    private void assertContainsNormalizedText(String expected, String actual) {
        if (actual.contains(expected)) {
            return;
        }

        String difference = describeFirstDifference(expected, actual);

        throw new AssertionError(
                "Actual response does not contain expected content."
                        + System.lineSeparator()
                        + difference
                        + System.lineSeparator()
                        + "Expected length: " + expected.length()
                        + System.lineSeparator()
                        + "Actual length: " + actual.length()
                        + System.lineSeparator()
                        + "Expected: [" + expected + "]"
                        + System.lineSeparator()
                        + "Actual: [" + actual + "]"
        );
    }

    private String describeFirstDifference(String expected, String actual) {
        int commonLength = Math.min(expected.length(), actual.length());

        for (int index = 0; index < commonLength; index++) {
            char expectedCharacter = expected.charAt(index);
            char actualCharacter = actual.charAt(index);

            if (expectedCharacter != actualCharacter) {
                return String.format(
                        "First difference at index %d: "
                                + "expected '%s' (U+%04X), actual '%s' (U+%04X)",
                        index,
                        printable(expectedCharacter),
                        (int) expectedCharacter,
                        printable(actualCharacter),
                        (int) actualCharacter
                );
            }
        }



        if (expected.length() != actual.length()) {
            return "The strings match for " + commonLength
                    + " characters but have different lengths.";
        }

        return "No character-level difference was found.";
    }

    private String printable(char character) {
        return switch (character) {
            case ' ' -> "<space>";
            case '\r' -> "\\r";
            case '\n' -> "\\n";
            case '\t' -> "\\t";
            default -> Character.isISOControl(character)
                    ? String.format("\\u%04X", (int) character)
                    : Character.toString(character);
        };
    }

    protected void assertExpectedContentsWithExpectedReplacement(
            String expectedResponseFile,
            String responseDocumentUrl,
            ResponseBody responseBody,
            Map<String, String> expectedKeyValueReplacements
    ) throws IOException {
        String expectedText = getJsonFromFile(expectedResponseFile);
        for (Map.Entry<String, String> entry : expectedKeyValueReplacements.entrySet()) {
            expectedText = expectedText.replace(entry.getKey(), entry.getValue());
        }

        JsonPath jsonPath = JsonPath.from(responseBody.asString());
        String documentUrl = jsonPath.getString(responseDocumentUrl);

        String response = utils.downloadPdfAndParseToString(documentUrl);

        expectedText = normalizePdfText(expectedText);
        response = normalizePdfText(response);

        assertContainsNormalizedText(expectedText, response);
    }

    protected void assertExpectedContentsMissing(String expectedContentMissing, ResponseBody responseBody) {
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String documentUrl = jsonPath.get(expectedContentMissing);
        assertTrue(documentUrl == null);
    }
}
