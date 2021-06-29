package uk.gov.hmcts.probate.functional.caseprogress;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.model.caseprogress.UrlConstants;


@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdCaseProgressAdmonWillTests extends CaseProgressTestsBase {

    @Before
    public void setUp() {
        initialiseConfig();
    }

    // Note - expected html filenames need to be .txt to stop intellij auto-formatting

    @Test
    public void shouldTransformAppCreatedStateCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/01-appCreated.json", TASKLIST_UPDATE_URL,
            "/application-created-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    public void shouldTransformAppUpdatedStateCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/02-appUpdated.json", SOLS_VALIDATE_PROBATE_URL,
            "/application-updated-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    public void shouldTransformAppCreatedStateReenterDetailsCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/02a-appCreated-update-app.json",
            TASKLIST_UPDATE_URL, "/application-created-update-app-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    public void shouldTransformAppUpdatedStateReenterDetailsCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/02b-appUpdated-update-app.json",
            SOLS_VALIDATE_PROBATE_URL, "/application-updated-update-app-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCompletingSolicitorProbatePart() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/03-probateCreated.json", SOLS_VALIDATE_URL,
            "/deceased-dtls-entered-html.txt", getUpdateAdmonWillDtlsUrl());
    }

    @Test
    public void shouldTransformCaseCreatedStateCorrectlyOnPrinting() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/04-caseCreated.json", CASE_PRINTED_URL,
            "/case-created-html.txt");
    }

    @Test
    // NOTE - actual cw state change to
    // BOReadyForExamination doesn't come to back office, we just get docs received notification
    public void shouldTransformCaseCorrectlyWhenMarkingAsReadyForExam() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/05-caseMarkAsReadyForExam.json",
            CASE_DOCS_RECEIVED_URL, "/case-ready-for-exam-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenStopped() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/06-caseStopped.json", CASE_STOPPED_URL,
            "/case-stopped-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenEscalated() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/07-caseEscalated.json", CASE_ESCALATED_URL,
            "/case-escalated-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingExamining() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/08-caseMatchingExamining.json",
            CASE_MATCHING_EXAMINING_URL, "/case-matching-examining-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenExamining() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/09-examineCase.json", TASKLIST_UPDATE_URL,
            "/examine-case-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingReadyToIssue() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/10-caseMatchingReadyToIssue.json",
            CASE_MATCHING_READY_TO_ISSUE_URL, "/case-matching-ready-to-issue-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenMarkingReadyToIssue() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/11-markReadyToIssue.json",
            CASE_MATCHING_EXAMINING_URL, "/case-mark-ready-to-issue-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenIssuingGrant() {
        verifyCaseProgressHtmlCwPost("caseprogressadmonwill/12-issueGrant.json", GENERATE_GRANT_URL,
            "/generate-grant-html.txt");
    }

    @Test
    public void shouldRenderSendDocumentsAdmonWill() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/04-caseCreated.json", TASKLIST_UPDATE_URL,
            "/admonwill/04-caseCreated");
    }

    @Test
    public void shouldRenderSendDocumentsWithCodicils() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/04a-caseCreated.json", TASKLIST_UPDATE_URL,
            "/admonwill/04a-caseCreatedWillHasCodicils");
    }

    @Test
    public void shouldRenderSendDocumentsWithIht217() {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/04b-caseCreated.json", TASKLIST_UPDATE_URL,
            "/admonwill/04b-caseCreatedIHT217");
    }

    private String getUpdateAdmonWillDtlsUrl() {
        return UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_ADMON_WILL.replaceFirst("<CASE_ID>",
            "1528365719153338");
    }
}
