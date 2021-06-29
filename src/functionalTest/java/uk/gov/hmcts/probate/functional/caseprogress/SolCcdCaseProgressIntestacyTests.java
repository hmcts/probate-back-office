package uk.gov.hmcts.probate.functional.caseprogress;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.model.caseprogress.UrlConstants;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdCaseProgressIntestacyTests extends CaseProgressTestsBase {

    @Before
    public void setUp() {
        initialiseConfig();
    }

    // Note - expected html filenames need to be .txt to stop intellij auto-formatting

    @Test
    public void shouldTransformAppCreatedStateCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/01-appCreated.json", TASKLIST_UPDATE_URL,
            "/application-created-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    public void shouldTransformAppUpdatedStateCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/02-appUpdated.json", SOLS_VALIDATE_PROBATE_URL,
            "/application-updated-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    public void shouldTransformAppCreatedStateReenterDetailsCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/02a-appCreated-update-app.json",
            TASKLIST_UPDATE_URL, "/application-created-update-app-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    public void shouldTransformAppUpdatedStateReenterDetailsCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/02b-appUpdated-update-app.json",
            SOLS_VALIDATE_PROBATE_URL, "/application-updated-update-app-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCompletingSolicitorProbatePart() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/03-probateCreated.json", SOLS_VALIDATE_URL,
             "/deceased-dtls-entered-html.txt", getAddApplicationDetailsUrl());
    }

    @Test
    public void shouldTransformCaseCreatedStateCorrectlyOnPrinting() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/04-caseCreated.json", CASE_PRINTED_URL,
            "/case-created-html.txt");
    }

    @Test
    // NOTE - actual cw state change to
    // BOReadyForExamination doesn't come to back office, we just get docs received notification
    public void shouldTransformCaseCorrectlyWhenMarkingAsReadyForExam() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/05-caseMarkAsReadyForExam.json",
            CASE_DOCS_RECEIVED_URL, "/case-ready-for-exam-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenStopped() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/06-caseStopped.json", CASE_STOPPED_URL,
            "/case-stopped-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenEscalated() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/07-caseEscalated.json", CASE_ESCALATED_URL,
            "/case-escalated-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingExamining() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/08-caseMatchingExamining.json",
            CASE_MATCHING_EXAMINING_URL, "/case-matching-examining-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenExamining() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/09-examineCase.json", TASKLIST_UPDATE_URL,
            "/examine-case-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingReadyToIssue() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/10-caseMatchingReadyToIssue.json",
            CASE_MATCHING_READY_TO_ISSUE_URL, "/case-matching-ready-to-issue-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenMarkingReadyToIssue() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/11-markReadyToIssue.json",
            CASE_MATCHING_EXAMINING_URL, "/case-mark-ready-to-issue-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenIssuingGrant() {
        verifyCaseProgressHtmlCwPost("caseprogressintestacy/12-issueGrant.json", GENERATE_GRANT_URL,
            "/generate-grant-html.txt");
    }

    @Test
    public void shouldRenderSendDocumentsIntestacy() {
        final String response = postCwJson("caseprogressintestacy/04-caseCreated.json", TASKLIST_UPDATE_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        String expected = fileSystemResourceService.getFileFromResourceAsString(
            "json/caseprogressintestacy/expectedHTML/04-caseCreated");
        expected = expected.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);
        assertEquals(expected, taskList);
    }


    @Test
    public void shouldRenderSendDocumentsWithIht217() {
        final String response = postCwJson("caseprogressintestacy/04a-caseCreated.json", TASKLIST_UPDATE_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        String expected = fileSystemResourceService.getFileFromResourceAsString(
            "json/caseprogressintestacy/expectedHTML/04a-caseCreatedIHT217");
        expected = expected
            .replaceAll("<BRANCH/>", TaskState.CODE_BRANCH)
            .replaceAll("<ihtForm/>", "the inheritance tax form IHT205 and IHT217");
        assertEquals(expected, taskList);
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

    private String getAddApplicationDetailsUrl() {
        return UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_INTESTACY.replaceFirst("<CASE_ID>",
                "1528365719153338");
    }
}
