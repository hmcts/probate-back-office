package uk.gov.hmcts.probate.functional.caseprogress;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.model.caseprogress.UrlConstants;

import java.io.IOException;

@ExtendWith(SerenityJUnit5Extension.class)
public class SolCcdCaseProgressGopTests extends CaseProgressTestsBase {

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    // Note - expected html filenames need to be .txt to stop intellij auto-formatting

    @Test
    void shouldTransformAppCreatedSolDtlsStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/01-appCreatedSolDtls.json", CASE_SOLS_CREATED,
            "/application-created-sol-dtls-html.txt", getSolicitorDtlsUrl());
    }

    @Test
    void shouldTransformAppCreatedDeceasedDtlsStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/01a-appCreatedDeceasedDtls.json", TASKLIST_UPDATE_URL,
                "/application-created-dcsd-dtls-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    void shouldTransformAppUpdatedStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/02-appUpdated.json", SOLS_VALIDATE_PROBATE_URL,
            "/application-updated-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    void shouldTransformAppCreatedStateReenterDetailsCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/02a-appCreated-update-app.json", TASKLIST_UPDATE_URL,
            "/application-created-update-app-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    void shouldTransformAppUpdatedStateReenterDetailsCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/02b-appUpdated-update-app.json", SOLS_VALIDATE_PROBATE_URL,
            "/application-updated-update-app-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    void shouldTransformCaseCorrectlyWhenCompletingSolicitorProbatePart() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/03-probateCreated.json", SOLS_VALIDATE_URL,
            "/deceased-dtls-entered-html.txt", getAddApplicationDetailsUrl());
    }

    @Test
    void shouldTransformCaseCreatedStateCorrectlyOnMakePayment() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/04-caseCreated.json", CASE_PRINTED_URL,
            "/gop/case-created-html.txt");
    }

    @Test
    void shouldTransformCaseCreatedStateCorrectlyForPrinting() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/04-casePrinting.json", CASE_PRINTED_URL,
            "/gop/case-printed-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenStopped() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/06-caseStopped.json", CASE_STOPPED_URL,
            "/case-stopped-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenEscalated() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/07-caseEscalated.json", CASE_ESCALATED_URL,
            "/case-escalated-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenCaseMatchingExamining() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/08-caseMatchingExamining.json", CASE_MATCHING_EXAMINING_URL,
            "/gop/case-matching-examining-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenMarkingReadyToIssue() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/11-markReadyToIssue.json", CASE_MATCHING_EXAMINING_URL,
            "/gop/case-mark-ready-to-issue-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenIssuingGrant() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogress/12-issueGrant.json", GENERATE_GRANT_URL,
            "/gop/generate-grant-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenFailQa() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/06-caseStopped.json",
            CASE_FAIL_QA_URL, "/case-stopped-html.txt");
    }

    @Test
    void shouldRenderSendDocumentsWithRenouncingExecutors() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/04a-caseCreated.json", TASKLIST_UPDATE_URL,
            "/gop/04a-caseCreatedRenouncingExecutors");
    }

    @Test
    void shouldRenderSendDocumentsWithCodicils() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/04b-caseCreated.json", TASKLIST_UPDATE_URL,
            "/gop/04b-caseCreatedWillHasCodicils");
    }

    @Test
    void shouldRenderSendDocumentsWithIht217() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/04c-caseCreated.json", TASKLIST_UPDATE_URL,
            "/gop/04c-caseCreatedIHT217");
    }

    @Test
    void shouldRenderSendDocumentsWithTcResolutionLodgedWithApplication() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogress/04e-caseCreated.json", TASKLIST_UPDATE_URL,
                "/gop/04e-caseCreatedTcResolutionLodgedWithApp");
    }

    private String getAddApplicationDetailsUrl() {
        return UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_GOP.replaceFirst("<CASE_ID>",
                "1528365719153338");
    }

    @Test
    void shouldTransformCaseCorrectlyPACreate() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogress/13-casePrinted.json", CASE_PA_CREATE,
                "/gop/pa-case-printed-html.txt");
    }

    @Test
    void shouldRenderSendDocumentsWithCodicilsCasePrinted() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogress/13-casePrinted.json", TASKLIST_UPDATE_CASEPRINTED_URL,
                "/gop/pa-case-printed-html.txt");
    }
}
