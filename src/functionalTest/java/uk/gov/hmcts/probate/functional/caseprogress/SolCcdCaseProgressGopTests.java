package uk.gov.hmcts.probate.functional.caseprogress;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.model.caseprogress.UrlConstants;


@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdCaseProgressGopTests extends CaseProgressTestsBase {

    @Before
    public void setUp() {
        initialiseConfig();
    }

    // Note - expected html filenames need to be .txt to stop intellij auto-formatting

    @Test
    public void shouldTransformAppCreatedStateCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogress/01-appCreated.json", TASKLIST_UPDATE_URL,
            "/application-created-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    public void shouldTransformAppUpdatedStateCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogress/02-appUpdated.json", SOLS_VALIDATE_PROBATE_URL,
            "/application-updated-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    public void shouldTransformAppCreatedStateReenterDetailsCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogress/02a-appCreated-update-app.json", TASKLIST_UPDATE_URL,
            "/application-created-update-app-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    public void shouldTransformAppUpdatedStateReenterDetailsCorrectly() {
        verifyCaseProgressHtmlSolPost("caseprogress/02b-appUpdated-update-app.json", SOLS_VALIDATE_PROBATE_URL,
            "/application-updated-update-app-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCompletingSolicitorProbatePart() {
        verifyCaseProgressHtmlSolPost("caseprogress/03-probateCreated.json", SOLS_VALIDATE_URL,
            "/deceased-dtls-entered-html.txt", getAddApplicationDetailsUrl());
    }

    @Test
    public void shouldTransformCaseCreatedStateCorrectlyOnPrinting() {
        verifyCaseProgressHtmlSolPost("caseprogress/04-caseCreated.json", CASE_PRINTED_URL,
            "/case-created-html.txt");
    }

    @Test
    // NOTE - actual cw state change to
    // BOReadyForExamination doesn't come to back office, we just get docs received notification
    public void shouldTransformCaseCorrectlyWhenMarkingAsReadyForExam() {
        verifyCaseProgressHtmlSolPost("caseprogress/05-caseMarkAsReadyForExam.json", CASE_DOCS_RECEIVED_URL,
            "/case-ready-for-exam-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenStopped() {
        verifyCaseProgressHtmlSolPost("caseprogress/06-caseStopped.json", CASE_STOPPED_URL,
            "/case-stopped-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenEscalated() {
        verifyCaseProgressHtmlSolPost("caseprogress/07-caseEscalated.json", CASE_ESCALATED_URL,
            "/case-escalated-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingExamining() {
        verifyCaseProgressHtmlSolPost("caseprogress/08-caseMatchingExamining.json", CASE_MATCHING_EXAMINING_URL,
            "/case-matching-examining-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenExamining() {
        verifyCaseProgressHtmlSolPost("caseprogress/09-examineCase.json", TASKLIST_UPDATE_URL,
            "/examine-case-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingReadyToIssue() {
        verifyCaseProgressHtmlSolPost("caseprogress/10-caseMatchingReadyToIssue.json",
            CASE_MATCHING_READY_TO_ISSUE_URL, "/case-matching-ready-to-issue-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenMarkingReadyToIssue() {
        verifyCaseProgressHtmlSolPost("caseprogress/11-markReadyToIssue.json", CASE_MATCHING_EXAMINING_URL,
            "/case-mark-ready-to-issue-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenIssuingGrant() {
        verifyCaseProgressHtmlCwPost("caseprogress/12-issueGrant.json", GENERATE_GRANT_URL,
            "/generate-grant-html.txt");
    }

    @Test
    public void shouldRenderSendDocumentsWithRenouncingExecutors() {
        verifyCaseProgressHtmlSolPost("caseprogress/04a-caseCreated.json", TASKLIST_UPDATE_URL,
            "/gop/04a-caseCreatedRenouncingExecutors");
    }

    @Test
    public void shouldRenderSendDocumentsWithCodicils() {
            verifyCaseProgressHtmlSolPost("caseprogress/04b-caseCreated.json", TASKLIST_UPDATE_URL,
            "/gop/04b-caseCreatedWillHasCodicils");
    }

    @Test
    public void shouldRenderSendDocumentsWithIht217() {
        verifyCaseProgressHtmlSolPost("caseprogress/04c-caseCreated.json", TASKLIST_UPDATE_URL,
            "/gop/04c-caseCreatedIHT217");
    }

    private String getAddApplicationDetailsUrl() {
        return UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_GOP.replaceFirst("<CASE_ID>",
                "1528365719153338");
    }
}
