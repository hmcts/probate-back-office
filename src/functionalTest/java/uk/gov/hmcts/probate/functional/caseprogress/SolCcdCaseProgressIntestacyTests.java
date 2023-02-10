package uk.gov.hmcts.probate.functional.caseprogress;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.model.caseprogress.UrlConstants;

import java.io.IOException;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdCaseProgressIntestacyTests extends CaseProgressTestsBase {

    @Before
    public void setUp() {
        initialiseConfig();
    }

    // Note - expected html filenames need to be .txt to stop intellij auto-formatting

    @Test
    public void shouldTransformAppCreatedSolDtlsStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/01-appCreatedSolDtls.json", TASKLIST_UPDATE_URL,
                "/application-created-sol-dtls-html.txt", getSolicitorDtlsUrl());
    }

    @Test
    public void shouldTransformAppCreatedDeceasedDtlsStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/01a-appCreatedDeceasedDtls.json", TASKLIST_UPDATE_URL,
                "/application-created-dcsd-dtls-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    public void shouldTransformAppUpdatedStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/02-appUpdated.json", SOLS_VALIDATE_PROBATE_URL,
            "/application-updated-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    public void shouldTransformAppCreatedStateReenterDetailsCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/02a-appCreated-update-app.json",
            TASKLIST_UPDATE_URL, "/application-created-update-app-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    public void shouldTransformAppUpdatedStateReenterDetailsCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/02b-appUpdated-update-app.json",
            SOLS_VALIDATE_PROBATE_URL, "/application-updated-update-app-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCompletingSolicitorProbatePart() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/03-probateCreated.json", SOLS_VALIDATE_URL,
            "/deceased-dtls-entered-html.txt", getAddApplicationDetailsUrl());
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCompletingSolicitorProbatePartWithPA16Form() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/04b-caseCreated.json", SOLS_VALIDATE_INTESTACY_URL,
            "/deceased-dtls-entered-pa16-html.txt", getAddApplicationDetailsUrl());
    }

    @Test
    public void shouldTransformCaseCreatedStateCorrectlyOnPrinting() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/04-caseCreated.json", CASE_PRINTED_URL,
            "/intestacy/case-created-html.txt");
    }

    @Test
    // NOTE - actual cw state change to
    // BOReadyForExamination doesn't come to back office, we just get docs received notification
    public void shouldTransformCaseCorrectlyWhenMarkingAsReadyForExam() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/05-caseMarkAsReadyForExam.json",
            CASE_DOCS_RECEIVED_URL, "/intestacy/case-ready-for-exam-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenStopped() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/06-caseStopped.json", CASE_STOPPED_URL,
            "/case-stopped-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenEscalated() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/07-caseEscalated.json", CASE_ESCALATED_URL,
            "/case-escalated-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingExamining() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/08-caseMatchingExamining.json",
            CASE_MATCHING_EXAMINING_URL, "/intestacy/case-matching-examining-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenExamining() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/09-examineCase.json", TASKLIST_UPDATE_URL,
            "/intestacy/examine-case-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingReadyToIssue() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/10-caseMatchingReadyToIssue.json",
            CASE_MATCHING_READY_TO_ISSUE_URL,
            "/intestacy/case-matching-ready-to-issue-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenMarkingReadyToIssue() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/11-markReadyToIssue.json",
            CASE_MATCHING_EXAMINING_URL, "/intestacy/case-mark-ready-to-issue-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenIssuingGrant() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogressintestacy/12-issueGrant.json", GENERATE_GRANT_URL,
            "/intestacy/generate-grant-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenFailQa() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/06-caseStopped.json",
            CASE_FAIL_QA_URL, "/case-stopped-html.txt");
    }

    @Test
    public void shouldRenderSendDocumentsIntestacy() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/04-caseCreated.json", TASKLIST_UPDATE_URL,
            "/intestacy/case-created-html.txt");
    }


    @Test
    public void shouldRenderSendDocumentsWithIht217() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/04a-caseCreated.json", TASKLIST_UPDATE_URL,
            "/intestacy/04a-caseCreatedIHT217");
    }

    private String getAddApplicationDetailsUrl() {
        return UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_INTESTACY.replaceFirst("<CASE_ID>",
            "1528365719153338");
    }

    @Test
    public void shouldTransformCaseCreatedStateCorrectlyPACreate() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogressintestacy/13-casePrinted.json", CASE_PA_CREATE,
                "/intestacy/case-printed-html.txt");
    }

    @Test
    public void shouldRenderSendDocumentsIntestacyCasePrinted() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogressintestacy/13-casePrinted.json", TASKLIST_UPDATE_CASEPRINTED_URL,
                "/intestacy/case-printed-html.txt");
    }
}
