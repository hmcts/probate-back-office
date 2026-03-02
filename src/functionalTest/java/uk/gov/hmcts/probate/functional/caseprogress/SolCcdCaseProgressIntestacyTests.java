package uk.gov.hmcts.probate.functional.caseprogress;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.model.caseprogress.UrlConstants;

import java.io.IOException;

import static uk.gov.hmcts.probate.model.CaseType.GRANT_OF_REPRESENTATION;

@ExtendWith(SerenityJUnit5Extension.class)
public class SolCcdCaseProgressIntestacyTests extends CaseProgressTestsBase {

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    // Note - expected html filenames need to be .txt to stop intellij auto-formatting

    @Test
    void shouldTransformAppCreatedSolDtlsStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/01-appCreatedSolDtls.json", TASKLIST_UPDATE_URL,
                "/application-created-sol-dtls-html.txt", getSolicitorDtlsUrl());
    }

    @Test
    void shouldTransformAppCreatedDeceasedDtlsStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/01a-appCreatedDeceasedDtls.json", TASKLIST_UPDATE_URL,
                "/application-created-dcsd-dtls-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    void shouldTransformAppUpdatedStateCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/02-appUpdated.json", SOLS_VALIDATE_PROBATE_URL,
            "/application-updated-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    void shouldTransformAppCreatedStateReenterDetailsCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/02a-appCreated-update-app.json",
            TASKLIST_UPDATE_URL, "/application-created-update-app-html.txt", getDeceasedDtlsUrl());
    }

    @Test
    void shouldTransformAppUpdatedStateReenterDetailsCorrectly() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/02b-appUpdated-update-app.json",
            SOLS_VALIDATE_PROBATE_URL, "/application-updated-update-app-html.txt", getReviewOrSubmitUrl());
    }

    @Test
    void shouldTransformCaseCorrectlyWhenCompletingSolicitorProbatePart() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/03-probateCreated.json", SOLS_VALIDATE_URL,
            "/deceased-dtls-entered-html.txt", getAddApplicationDetailsUrl());
    }

    @Test
    void shouldTransformCaseCorrectlyWhenCompletingSolicitorProbatePartWithPA16Form() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/04b-caseCreated.json", SOLS_VALIDATE_INTESTACY_URL,
            "/deceased-dtls-entered-pa16-html.txt", getAddApplicationDetailsUrl());
    }

    @Test
    void shouldTransformCaseCreatedStateCorrectlyOnPrinting() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/04-caseCreated.json", CASE_PRINTED_URL,
            "/intestacy/case-created-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenStopped() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/06-caseStopped.json", CASE_STOPPED_URL,
            "/case-stopped-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenEscalated() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/07-caseEscalated.json", CASE_ESCALATED_URL,
            "/case-escalated-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenCaseMatchingExamining() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/08-caseMatchingExamining.json",
            CASE_MATCHING_EXAMINING_URL, "/intestacy/case-matching-examining-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenMarkingReadyToIssue() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/11-markReadyToIssue.json",
            CASE_MATCHING_EXAMINING_URL, "/intestacy/case-mark-ready-to-issue-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenIssuingGrant() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogressintestacy/12-issueGrant.json", GENERATE_GRANT_URL,
            "/intestacy/generate-grant-html.txt");
    }

    @Test
    void shouldTransformCaseCorrectlyWhenFailQa() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/06-caseStopped.json",
            CASE_FAIL_QA_URL, "/case-stopped-html.txt");
    }

    @Test
    void shouldRenderSendDocumentsIntestacy() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/04-caseCreated.json", TASKLIST_UPDATE_URL,
            "/intestacy/case-created-html.txt");
    }


    @Test
    void shouldRenderSendDocumentsWithIht217() throws IOException {
        verifyCaseProgressHtmlSolPost("caseprogressintestacy/04a-caseCreated.json", TASKLIST_UPDATE_URL,
            "/intestacy/04a-caseCreatedIHT217");
    }

    private String getAddApplicationDetailsUrl() {
        return StringUtils.replaceEach(
            UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_INTESTACY,
            new String[]{"<CASE_ID>", "<CASE_TYPE>"},
            new String[]{"1528365719153338", GRANT_OF_REPRESENTATION.getCode()}
        );
    }

    @Test
    void shouldTransformCaseCreatedStateCorrectlyPACreate() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogressintestacy/13-casePrinted.json", CASE_PA_CREATE,
                "/intestacy/case-printed-html.txt");
    }

    @Test
    void shouldRenderSendDocumentsIntestacyCasePrinted() throws IOException {
        verifyCaseProgressHtmlCwPost("caseprogressintestacy/13-casePrinted.json", TASKLIST_UPDATE_CASEPRINTED_URL,
                "/intestacy/case-printed-html.txt");
    }
}
