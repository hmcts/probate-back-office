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
    public void shouldTransformAppCreatedSolDtlsStateCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/01-appCreatedSolDtls.json", TASKLIST_UPDATE_URL,
                "/application-created-sol-dtls-html.txt", getSolicitorDtlsUrl());
    }

    @Test
    public void shouldTransformAppCreatedDeceasedDtlsStateCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/01a-appCreatedDeceasedDtls.json", TASKLIST_UPDATE_URL,
                "/application-created-dcsd-dtls-html.txt", getDeceasedDtlsUrl());
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
    public void shouldTransformCaseCorrectlyWhenFailQa() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/06-caseStopped.json",
            CASE_FAIL_QA_URL, "/case-stopped-html.txt");
    }

    @Test
    public void shouldRenderSendDocumentsIntestacy() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/04-caseCreated.json", TASKLIST_UPDATE_URL,
            "/intestacy/04-caseCreated");
    }


    @Test
    public void shouldRenderSendDocumentsWithIht217() {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/04a-caseCreated.json", TASKLIST_UPDATE_URL,
            "/intestacy/04a-caseCreatedIHT217");
    }

    private String getAddApplicationDetailsUrl() {
        return UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_INTESTACY.replaceFirst("<CASE_ID>",
            "1528365719153338");
    }
}