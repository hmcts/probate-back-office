package uk.gov.hmcts.probate.functional.caseprogress;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.model.caseprogress.UrlConstants;

import java.io.IOException;


@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdCaseProgressAdmonWillTests extends CaseProgressTestsBase {

    @Before
    public void setUp() {
        initialiseConfig();
    }

    // Note - expected html filenames need to be .txt to stop intellij auto-formatting

    @Test
    public void shouldTransformAppCreatedSolDtlsStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/01-appCreatedSolDtls.json", TASKLIST_UPDATE_URL,
                "/application-created-sol-dtls-html.txt", getSolicitorDtlsUrl());
    }

    @Test
    public void shouldTransformAppCreatedDeceasedDtlsStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/01a-appCreatedDeceasedDtls.json", TASKLIST_UPDATE_URL,
                "/application-created-dcsd-dtls-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    public void shouldTransformAppUpdatedStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/02-appUpdated.json", SOLS_VALIDATE_PROBATE_URL,
            "/application-updated-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    public void shouldTransformAppCreatedStateReenterDetailsCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/02a-appCreated-update-app.json",
            TASKLIST_UPDATE_URL, "/application-created-update-app-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    public void shouldTransformAppUpdatedStateReenterDetailsCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/02b-appUpdated-update-app.json",
            SOLS_VALIDATE_PROBATE_URL, "/application-updated-update-app-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCompletingSolicitorProbatePart() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/03-probateCreated.json", SOLS_VALIDATE_URL,
            "/deceased-dtls-entered-html.txt", getUpdateAdmonWillDtlsUrl());
    }

    @Test
    public void shouldTransformCaseCreatedStateCorrectlyOnPrinting() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/04-caseCreated.json", CASE_PRINTED_URL,
            "/admonwill/case-created-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenStopped() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/06-caseStopped.json", CASE_STOPPED_URL,
            "/case-stopped-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenEscalated() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/07-caseEscalated.json", CASE_ESCALATED_URL,
            "/case-escalated-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingExamining() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/08-caseMatchingExamining.json",
            CASE_MATCHING_EXAMINING_URL, "/admonwill/case-matching-examining-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingReadyToIssue() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/10-caseMatchingReadyToIssue.json",
            CASE_MATCHING_READY_TO_ISSUE_URL, "/admonwill/case-matching-ready-to-issue-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenMarkingReadyToIssue() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/11-markReadyToIssue.json",
            CASE_MATCHING_EXAMINING_URL, "/admonwill/case-mark-ready-to-issue-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenFailQa() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/06-caseStopped.json",
            CASE_FAIL_QA_URL, "/case-stopped-html.txt");
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenIssuingGrant() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogressadmonwill/12-issueGrant.json", GENERATE_GRANT_URL,
            "/admonwill/generate-grant-html.txt");
    }

    @Test
    public void shouldRenderSendDocumentsAdmonWill() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/04-caseCreated.json", TASKLIST_UPDATE_URL,
            "/admonwill/case-created-html.txt");
    }

    @Test
    public void shouldRenderSendDocumentsWithCodicils() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/04a-caseCreated.json", TASKLIST_UPDATE_URL,
            "/admonwill/04a-caseCreatedWillHasCodicils");
    }

    @Test
    public void shouldRenderSendDocumentsWithIht217() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressadmonwill/04b-caseCreated.json", TASKLIST_UPDATE_URL,
            "/admonwill/04b-caseCreatedIHT217");
    }

    private String getUpdateAdmonWillDtlsUrl() {
        return UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_ADMON_WILL.replaceFirst("<CASE_ID>",
            "1528365719153338");
    }

    @Test
    public void shouldTransformCaseCorrectlyPACreate() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogressadmonwill/13-casePrinted.json", CASE_PA_CREATE,
                "/admonwill/case-printed-html.txt");
    }

    @Test
    public void shouldRenderSendDocumentsAdmonWillCasePrinted() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogressadmonwill/13-casePrinted.json", TASKLIST_UPDATE_CASEPRINTED_URL,
                "/admonwill/case-printed-html.txt");
    }
}
