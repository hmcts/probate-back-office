package uk.gov.hmcts.probate.functional.caseprogress;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.model.caseprogress.TaskState;
import uk.gov.hmcts.probate.model.caseprogress.UrlConstants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public abstract class CaseProgressTestsBase extends IntegrationTestBase {

    protected static final String TASKLIST_UPDATE_URL = "/tasklist/update";
    protected static final String CASE_PRINTED_URL = "/case/casePrinted";
    protected static final String CASE_DOCS_RECEIVED_URL = "/notify/documents-received";
    protected static final String SOLS_VALIDATE_URL = "/case/sols-validate";
    protected static final String SOLS_VALIDATE_PROBATE_URL = "/case/sols-validate-probate";

    protected static final String CASE_STOPPED_URL = "/case/case-stopped";
    protected static final String CASE_ESCALATED_URL = "/case/case-escalated";
    protected static final String CASE_MATCHING_EXAMINING_URL = "/case-matching/import-legacy-from-grant-flow";
    protected static final String CASE_MATCHING_READY_TO_ISSUE_URL = "/case/validateCheckListDetails";
    protected static final String GENERATE_GRANT_URL = "/document/generate-grant";

    private static final String todaysDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

    protected void verifyCaseProgressHtmlSolPost(String jsonFile, String postUrl, String expectedHtmlFile) {
        verifyCaseProgressHtml(jsonFile, postUrl, expectedHtmlFile, true, null);
    }

    protected void verifyCaseProgressHtmlSolPost(String jsonFile, String postUrl, String expectedHtmlFile,
                                          String nextStepUrl) {

        verifyCaseProgressHtml(jsonFile, postUrl, expectedHtmlFile, true, nextStepUrl);
    }

    protected void verifyCaseProgressHtmlCwPost(String jsonFile, String postUrl, String expectedHtmlFile) {
        verifyCaseProgressHtml(jsonFile, postUrl, expectedHtmlFile, false, null);
    }

    protected void verifyCaseProgressHtml(String jsonFile, String postUrl, String expectedHtmlFile,
                                        boolean forSolicitorJsonPost,
                                        String nextStepUrl) {
        final var response = forSolicitorJsonPost
                ? postSolJson(jsonFile, postUrl) : postCwJson(jsonFile, postUrl);

        final var jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");

        var expected = utils.getStringFromFile("/expected-html/" + expectedHtmlFile);
        expected = replaceAllInString(expected, "{code-branch}", TaskState.CODE_BRANCH);
        expected = replaceAllInString(expected, "{next-step-url}", nextStepUrl);
        expected = expected.replaceAll(Pattern.quote("<today/>"), this.todaysDate);

        // make sure tasklist controller update in db works when called separately,
        // which happens prior to first state change
        assertEquals(removeCrLfs(expected), removeCrLfs(taskList));
    }

    private String postSolJson(String jsonFileName, String path) {
        final Response jsonResponse = RestAssured.given()
                .config(config)
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithSolicitorUser())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path)
                .andReturn();

        return jsonResponse.getBody().asString();
    }

    private String postCwJson(String jsonFileName, String path) {
        final Response jsonResponse = RestAssured.given()
                .config(config)
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path)
                .andReturn();

        return jsonResponse.getBody().asString();
    }

    protected String getDeceasedDtlsUrl() {
        return UrlConstants.DECEASED_DETAILS_URL_TEMPLATE.replaceFirst("<CASE_ID>", "1528365719153338");
    }

    protected String getReviewOrSubmitUrl() {
        return UrlConstants.REVIEW_OR_SUBMIT_URL_TEMPLATE.replaceFirst("<CASE_ID>", "1528365719153338");
    }
}
