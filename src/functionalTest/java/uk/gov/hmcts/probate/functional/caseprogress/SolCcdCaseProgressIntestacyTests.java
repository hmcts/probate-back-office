package uk.gov.hmcts.probate.functional.caseprogress;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import junit.framework.TestCase;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.model.caseprogress.TaskState;
import uk.gov.hmcts.probate.model.caseprogress.UrlConstants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdCaseProgressIntestacyTests extends IntegrationTestBase  {

    private static final String TASKLIST_UPDATE_URL = "/tasklist/update";
    private static final String CASE_PRINTED_URL = "/case/casePrinted";
    private static final String CASE_DOCS_RECEIVED_URL = "/notify/documents-received";
    private static final String SOLS_VALIDATE_URL = "/case/sols-validate";
    private static final String SOLS_VALIDATE_PROBATE_URL = "/case/sols-validate-probate";

    private static final String CASE_STOPPED_URL = "/case/case-stopped";
    private static final String CASE_ESCALATED_URL = "/case/case-escalated";
    private static final String CASE_MATCHING_EXAMINING_URL = "/case-matching/import-legacy-from-grant-flow";
    private static final String CASE_MATCHING_READY_TO_ISSUE_URL = "/case/validateCheckListDetails"; // Case Id: 1605609421859344"
    private static final String GENERATE_GRANT_URL = "/document/generate-grant";
    private static final String todaysDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

    @Test
    public void shouldTransformAppCreatedStateCorrectly() {
        final String response = postSolJson("caseprogressintestacy/01-appCreated.json", TASKLIST_UPDATE_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        final String expected = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
                + "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Add solicitor details</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>"
                + "\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><a href=\""
                + UrlConstants.DECEASED_DETAILS_URL_TEMPLATE.replaceFirst("<CASE_ID>", "1528365719153338")
                + "\" class=\"govuk-link\">Add deceased details</a></p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" /></p>\n</div></div>"
                + "\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Add application details</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m "
                + "govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Review and sign legal statement and submit application</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "The legal statement is generated. You can review, change any details, then sign and submit your application."
                + "</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Send documents<br/></p>"
                + "</div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts."
                + "</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Authenticate documents</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We will authenticate your documents and match them with your application.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m "
                + "govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Examine application</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it against other "
                + "cases or caveats. After the review we prepare the grant.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;"
                + "</div></div>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by our team:</font>"
                + "</p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<ul class=\"govuk-list govuk-list--bullet\">\n"
                + "<li>Examining</li>\n<li>Case Matching</li>\n<li>Case selected for Quality Assurance</li>\n<li>Ready to issue</li>\n</ul>"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<h2 class=\"govuk-heading-l\">"
                + "4. Grant of representation</h2>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">This step is completed by HM Courts and Tribunals Service staff.</font>"
                + "</p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Issue grant of representation</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "The grant will be delivered in the post a few days after issuing.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n</div>\n</div>\n";

        assertEquals(expected, taskList); // make sure tasklist controller update in db works when called separately, which happens prior to first state change
    }

    @Test
    public void shouldTransformAppUpdatedStateCorrectly() {
        final String response = postSolJson("caseprogressintestacy/02-appUpdated.json", SOLS_VALIDATE_PROBATE_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        final String expected = "<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\">\n<h2 class=\"govuk-heading-l\">"
                + "1. Enter application details</h2>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the legal professional.</font>"
                + "</p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add solicitor details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add deceased details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add application details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be "
                + "completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><a href=\""
                + UrlConstants.REVIEW_OR_SUBMIT_URL_TEMPLATE.replaceFirst("<CASE_ID>", "1528365719153338")
                + "\" class=\"govuk-link\">Review and sign legal statement and submit application</a></p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The legal statement is generated. You can review, change any details, then sign "
                + "and submit your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Send documents<br/></p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n<hr class=\"govuk-section-break govuk-section-break--m "
                + "govuk-section-break--visible\">\n\n<br/>\n<h2 class=\"govuk-heading-l\">3. Review application</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks "
                + "before the review starts.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Authenticate documents</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We will authenticate your documents and match them with your application.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Examine application</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it against "
                + "other cases or caveats. After the review we prepare the grant.</font></p></div><div class=\"govuk-grid-column-one-third\">"
                + "&nbsp;</div></div>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by our team:</font>"
                + "</p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<ul class=\"govuk-list govuk-list--bullet\">\n<li>"
                + "Examining</li>\n<li>Case Matching</li>\n<li>Case selected for Quality Assurance</li>\n<li>Ready to issue</li>\n</ul>"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<h2 class=\"govuk-heading-l\">"
                + "4. Grant of representation</h2>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">This step is completed by HM Courts and Tribunals Service staff.</font>"
                + "</p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Issue grant of representation</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "The grant will be delivered in the post a few days after issuing.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m "
                + "govuk-section-break--visible\">\n\n</div>\n</div>\n";

        assertEquals(expected, taskList);
    }

    @Test
    public void shouldTransformAppCreatedStateReenterDetailsCorrectly() {
        final String response = postSolJson("caseprogressintestacy/02a-appCreated-update-app.json", TASKLIST_UPDATE_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        final String expectedHtml = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
                + "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps "
                + "are to be completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div>"
                + "</div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Add solicitor details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" "
                + "height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><a href=\""
                + UrlConstants.DECEASED_DETAILS_URL_TEMPLATE.replaceFirst("<CASE_ID>", "1528365719153338")
                + "\" class=\"govuk-link\">Add deceased details</a></p></div><div class=\"govuk-grid-column-one-third\"><p>"
                + "<img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n"
                + "</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add application details"
                + "</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/"
                + "statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n"
                + "</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Review and sign legal "
                + "statement and submit application</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" "
                + "width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n"
                + "</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The legal statement is generated. You can review, change any details, then sign and submit "
                + "your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Send documents<br/></p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks "
                + "before the review starts.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
                "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
                "\n" +
                "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Authenticate documents"
                + "</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We will authenticate your documents and match them with your application.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Examine application</p>"
                + "</div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it against other "
                + "cases or caveats. After the review we prepare the grant.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;"
                + "</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by our team:</font>"
                + "</p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<ul class=\"govuk-list govuk-list--bullet\">\n"
                + "<li>Examining</li>\n"
                + "<li>Case Matching</li>\n"
                + "<li>Case selected for Quality Assurance</li>\n"
                + "<li>Ready to issue</li>\n"
                + "</ul><hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">This step is completed by HM Courts and Tribunals Service staff.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Issue grant of representation</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The grant will be delivered in the post a few days after issuing.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "</div>\n"
                + "</div>\n";
        assertEquals(expectedHtml, taskList); // make sure tasklist controller update in db works when called separately, which happens prior to first state change
    }

    @Test
    public void shouldTransformAppUpdatedStateReenterDetailsCorrectly() {
        final String response = postSolJson("caseprogressintestacy/02b-appUpdated-update-app.json", SOLS_VALIDATE_PROBATE_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        final String expectedHtml = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
                + "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Add solicitor details</p>"
                + "</div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add deceased details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div>"
                + "</div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Add application details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" "
                + "height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><a href=\""
                + UrlConstants.REVIEW_OR_SUBMIT_URL_TEMPLATE.replaceFirst("<CASE_ID>", "1528365719153338")
                + "\" class=\"govuk-link\">Review and sign legal statement and submit application</a></p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n"
                + "</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The legal statement is generated. You can review, change any details, then sign and submit "
                + "your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Send documents<br/></p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">These steps are completed by HM Courts and Tribunals Service staff. "
                + "It can take a few weeks before the review starts.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n" + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Authenticate documents</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">We will authenticate your documents and match them with your "
                + "application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Examine application</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it "
                + "against other cases or caveats. After the review we prepare the grant.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "Your application will update through any of these case states as it is reviewed by our team:</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<ul class=\"govuk-list govuk-list--bullet\">\n"
                + "<li>Examining</li>\n"
                + "<li>Case Matching</li>\n"
                + "<li>Case selected for Quality Assurance</li>\n"
                + "<li>Ready to issue</li>\n"
                + "</ul><hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">This step is completed by HM Courts and Tribunals Service staff.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Issue grant of representation</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The grant will be delivered in the post a few days after issuing.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "</div>\n"
                + "</div>\n";

        assertEquals(expectedHtml, taskList);
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCompletingSolicitorProbatePart() {
        final String response = postCwJson("caseprogressadmonwill/03-probateCreated.json", SOLS_VALIDATE_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");

        final String expectedHtml = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
                + "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add solicitor details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add deceased details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><a href=\""
                + UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_ADMON_WILL.replaceFirst("<CASE_ID>", "1528365719153338")
                + "\" class=\"govuk-link\">Add application details</a></p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH + "/src/main/resources/statusImages/not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" />"
                + "</p>\n</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are to be completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">"
                + "&nbsp;</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Review and sign legal statement and submit application</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The legal statement is generated. You can review, change any details, "
                + "then sign and submit your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Send documents<br/></p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts."
                + "</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Authenticate documents</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "We will authenticate your documents and match them with your application.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Examine application</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "We review your application for incomplete information or problems and validate it against other cases or caveats. "
                + "After the review we prepare the grant.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by our team:"
                + "</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<ul class=\"govuk-list govuk-list--bullet\">\n<li>Examining</li>\n<li>Case Matching</li>\n"
                + "<li>Case selected for Quality Assurance</li>\n<li>Ready to issue</li>\n</ul>"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">This step is completed by HM Courts and Tribunals Service staff.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Issue grant of representation</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "The grant will be delivered in the post a few days after issuing.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n</div>\n</div>\n";

        assertEquals(expectedHtml, taskList);
    }

    @Test
    public void shouldTransformCaseCreatedStateCorrectlyOnPrinting() {
        final String response = postCwJson("caseprogressadmonwill/04-caseCreated.json", CASE_PRINTED_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        final String expected = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
                + "<h2 class=\"govuk-heading-l\">"
                + "1. Enter application details</h2>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add solicitor details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add deceased details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div>"
                + "</div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add application details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be "
                + "completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Review and sign legal statement and submit "
                + "application</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The legal statement is generated. You can review, change any details, then sign and submit "
                + "your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Send documents<br/><details class=\"govuk-details\" data-module=\"govuk-details\">\n  "
                + "<summary class=\"govuk-details__summary\">\n    <span class=\"govuk-details__summary-text\">\n      "
                + "View the documents needed by HM Courts and Tribunal Service\n    </span>\n  </summary>\n  "
                + "<div class=\"govuk-details__text\">\n    "
                + "You now need to send us<br/><ul><li>your reference number 1528365719153338 written on a piece of paper</li>"
                + "<li>the stamped (receipted) IHT 421 with this application</li><li>a photocopy of the signed legal statement "
                + "and declaration</li></ul>\n  </div>\n</details></p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts."
                + "</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Authenticate documents</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "We will authenticate your documents and match them with your application.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Examine application</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it "
                + "against other cases or caveats. After the review we prepare the grant.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "Your application will update through any of these case states as it is reviewed by our team:</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<ul class=\"govuk-list govuk-list--bullet\">\n"
                + "<li>Examining</li>\n<li>Case Matching</li>\n<li>Case selected for Quality Assurance</li>\n<li>Ready to issue</li>\n</ul>"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "This step is completed by HM Courts and Tribunals Service staff.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Issue grant of representation</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The grant will be delivered in the post a few days after issuing.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n</div>\n</div>\n";

        assertEquals(expected, taskList);
    }

    @Test
    // NOTE - actual cw state change to BOReadyForExamination doesn't come to back office, we just get docs received notification
    public void shouldTransformCaseCorrectlyWhenMarkingAsReadyForExam() {
        final String response = postCwJson("caseprogressadmonwill/05-caseMarkAsReadyForExam.json", CASE_DOCS_RECEIVED_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        String expectedHtml = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
                + "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Add solicitor details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Add deceased details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" "
                + "height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Add application details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" "
                + "width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are to be completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">"
                + "&nbsp;</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Review and "
                + "sign legal statement and submit application</p></div><div class=\"govuk-grid-column-one-third\"><p>"
                + "<img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The legal statement is generated. You can review, change any details, then sign "
                + "and submit your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Send documents<br/><details class=\"govuk-details\" data-module=\"govuk-details\">\n  "
                + "<summary class=\"govuk-details__summary\">\n    "
                + "<span class=\"govuk-details__summary-text\">\n      View the documents needed by HM Courts and Tribunal Service\n    "
                + "</span>\n  </summary>\n  <div class=\"govuk-details__text\">\n    You now need to send us<br/><ul><li>your reference "
                + "number 1528365719153338 written on a piece of paper</li><li>the stamped (receipted) IHT 421 with this application</li>"
                + "<li>a photocopy of the signed legal statement and declaration</li></ul>\n  </div>\n</details></p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
                + "</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are completed "
                + "by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Authenticate documents</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
                + "</div></div>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>"
                + "Authenticated on <today/></strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We will authenticate your documents and match them with your application.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Examine application</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it against "
                + "other cases or caveats. After the review we prepare the grant.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by our team:"
                + "</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<ul class=\"govuk-list govuk-list--bullet\">\n<li>Examining</li>\n<li>Case Matching</li>\n"
                + "<li>Case selected for Quality Assurance</li>\n<li>Ready to issue</li>\n</ul>"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "This step is completed by HM Courts and Tribunals Service staff.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Issue grant of representation</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "The grant will be delivered in the post a few days after issuing.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n</div>\n</div>\n";

        expectedHtml = expectedHtml.replaceAll(Pattern.quote("<today/>"), this.todaysDate);

        assertEquals(expectedHtml, taskList);
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenStopped() {
        final String response = postCwJson("caseprogressadmonwill/06-caseStopped.json", CASE_STOPPED_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");

        String expectedHtml = "<div class=\"width-50\">\n\n<h2 class=\"govuk-heading-l\">Case progress</h2>\n\n"
                + "<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">Case stopped</div>\n\n"
                + "<h2 class=\"govuk-heading-l\">What happens next</h2>\n\n<p class=\"govuk-body-s\">"
                + "The case was stopped on <today/> for one of two reasons:</p>\n"
                + "<ul class=\"govuk-list govuk-list--bullet\">\n<li>an internal review is needed</li>\n"
                + "<li>further information from the applicant or solicitor is needed</li>\n</ul>\n\n"
                + "<p class=\"govuk-body-s\">You will be notified by email if we need any information from you to progress the case.</p>\n"
                + "<p class=\"govuk-body-s\">Only contact the CTSC staff if your case has been stopped for 4 weeks or more and you have not "
                + "received any communication since then.</p>\n\n<h2 class=\"govuk-heading-l\">"
                + "Get help with your application</h2>\n\n<h3 class=\"govuk-heading-m\">Telephone</h3>\n\n"
                + "<p class=\"govuk-body-s\">You will need the case reference or the deceased's full name when you call.</p><br/>"
                + "<p class=\"govuk-body-s\">Telephone: 0300 303 0648</p><p class=\"govuk-body-s\">"
                + "Monday to Friday 8am to 6pm, Saturday 8am to 2pm (except public holidays)</p><br/>"
                + "<p class=\"govuk-body-s\">Welsh language: 0300 303 0654</p><p class=\"govuk-body-s\">"
                + "Monday to Friday, 8am to 5pm (except public holidays)</p><br/>\n\n"
                + "<a href=\"https://www.gov.uk/call-charges\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">"
                + "Find out about call charges</a>\n\n<h3 class=\"govuk-heading-m\">Email</h3>\n\n"
                + "<a href=\"mailto:contactprobate@justice.gov.uk\" class=\"govuk-link\">contactprobate@justice.gov.uk</a>"
                + "<p class=\"govuk-body-s\">We aim to respond within 10 working days</p>\n\n</div>";

        expectedHtml = expectedHtml.replaceAll(Pattern.quote("<today/>"), this.todaysDate);

        assertEquals(expectedHtml, taskList);
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenEscalated() {
        final String response = postCwJson("caseprogressadmonwill/07-caseEscalated.json", CASE_ESCALATED_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");

        String expectedHtml = "<div class=\"width-50\">\n\n<h2 class=\"govuk-heading-l\">Case progress</h2>\n\n"
                + "<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">Case escalated to a Registrar</div>\n\n"
                + "<h2 class=\"govuk-heading-l\">What happens next</h2>\n\n<p class=\"govuk-body-s\">The case was escalated on <today/>.</p>\n"
                + "<p class=\"govuk-body-s\">The case will be reviewed by a Registrar and you will be notified by email if we need any "
                + "information from you to progress the case.</p>\n"
                + "<p class=\"govuk-body-s\">Only contact the CTSC staff if your case has been escalated for 6 weeks or more and you have "
                + "not received any communication since then.</p>\n\n\n"
                + "<h2 class=\"govuk-heading-l\">Get help with your application</h2>\n\n"
                + "<h3 class=\"govuk-heading-m\">Telephone</h3>\n\n<p class=\"govuk-body-s\">"
                + "You will need the case reference or the deceased's full name when you call.</p><br/>"
                + "<p class=\"govuk-body-s\">Telephone: 0300 303 0648</p><p class=\"govuk-body-s\">"
                + "Monday to Friday 8am to 6pm, Saturday 8am to 2pm (except public holidays)</p><br/>"
                + "<p class=\"govuk-body-s\">Welsh language: 0300 303 0654</p><p class=\"govuk-body-s\">"
                + "Monday to Friday, 8am to 5pm (except public holidays)</p><br/>\n\n<a href=\"https://www.gov.uk/call-charges\" "
                + "target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">Find out about call charges</a>\n\n"
                + "<h3 class=\"govuk-heading-m\">Email</h3>\n\n<a href=\"mailto:contactprobate@justice.gov.uk\" class=\"govuk-link\">"
                + "contactprobate@justice.gov.uk</a><p class=\"govuk-body-s\">We aim to respond within 10 working days</p>\n\n</div>";
        expectedHtml = expectedHtml.replaceAll(Pattern.quote("<today/>"), this.todaysDate);

        assertEquals(expectedHtml, taskList);
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingExamining() {
        final String response = postCwJson("caseprogressadmonwill/08-caseMatchingExamining.json", CASE_MATCHING_EXAMINING_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        final String expected = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
                + "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Add solicitor details</p></div><div class=\"govuk-grid-column-one-third\"><p>"
                + "<img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Add deceased details</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Add application details</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Review and sign legal statement and submit application</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The legal statement is generated. You can review, change any details, "
                + "then sign and submit your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Send documents<br/>"
                + "<details class=\"govuk-details\" data-module=\"govuk-details\">\n  <summary class=\"govuk-details__summary\">\n    "
                + "<span class=\"govuk-details__summary-text\">\n      View the documents needed by HM Courts and Tribunal Service\n    "
                + "</span>\n  </summary>\n  <div class=\"govuk-details__text\">\n    You now need to send us<br/><ul><li>"
                + "your reference number 1528365719153338 written on a piece of paper</li><li>the stamped (receipted) IHT 421 "
                + "with this application</li><li>a photocopy of the signed legal statement and declaration</li></ul>\n  </div>\n</details>"
                + "</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts."
                + "</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Authenticate documents</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">We will authenticate your documents and match them with your application."
                + "</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Examine application</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it against "
                + "other cases or caveats. After the review we prepare the grant.</font></p></div><div class=\"govuk-grid-column-one-third\">"
                + "&nbsp;</div></div>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by our team:</font>"
                + "</p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<ul class=\"govuk-list govuk-list--bullet\">\n"
                + "<li>Examining</li>\n<li>Case Matching</li>\n<li>Case selected for Quality Assurance</li>\n<li>Ready to issue</li>\n</ul>"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<h2 class=\"govuk-heading-l\">"
                + "4. Grant of representation</h2>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">This step is completed by HM Courts and Tribunals Service staff.</font>"
                + "</p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Issue grant of representation</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">The grant will be delivered "
                + "in the post a few days after issuing.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n</div>\n</div>\n";

        assertEquals(expected, taskList);
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenExamining() {
        final String response = postCwJson("caseprogressadmonwill/09-examineCase.json", TASKLIST_UPDATE_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        final String expected = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
                + "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Add solicitor details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" "
                + "width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Add deceased details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" "
                + "width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add application details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Review and sign legal statement "
                + "and submit application</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" "
                + "width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The legal statement is generated. You can review, change any details, then sign "
                + "and submit your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Send documents<br/>"
                + "<details class=\"govuk-details\" data-module=\"govuk-details\">\n  <summary class=\"govuk-details__summary\">\n    "
                + "<span class=\"govuk-details__summary-text\">\n      View the documents needed by HM Courts and Tribunal Service\n    </span>"
                + "\n  </summary>\n  <div class=\"govuk-details__text\">\n    You now need to send us<br/><ul><li>your reference number "
                + "1528365719153338 written on a piece of paper</li><li>the stamped (receipted) IHT 421 with this application</li>"
                + "<li>a photocopy of the signed legal statement and declaration</li></ul>\n  </div>\n</details></p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are completed "
                + "by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Authenticate documents</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We will authenticate your documents and match them with your application.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Examine application</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it against "
                + "other cases or caveats. After the review we prepare the grant.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "Your application will update through any of these case states as it is reviewed by our team:</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<ul class=\"govuk-list govuk-list--bullet\">\n"
                + "<li>Examining</li>\n<li>Case Matching</li>\n<li>Case selected for Quality Assurance</li>\n<li>Ready to issue</li>\n</ul>"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "This step is completed by HM Courts and Tribunals Service staff.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Issue grant of representation</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The grant will be delivered in the post a few days after issuing.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n</div>\n</div>\n";

        assertEquals(expected, taskList);
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenCaseMatchingReadyToIssue() {
        final String response = postCwJson("caseprogressadmonwill/10-caseMatchingReadyToIssue.json", CASE_MATCHING_READY_TO_ISSUE_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        final String expected = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n<h2 class=\"govuk-heading-l\">"
                + "1. Enter application details</h2>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the legal professional.</font>"
                + "</p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Add solicitor details</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Add deceased details</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Add application details</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n" +
                "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are to be completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">"
                + "&nbsp;</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Review and sign legal statement and submit application</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The legal statement is generated. You can review, change any details, then sign and "
                + "submit your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Send documents<br/>"
                + "<details class=\"govuk-details\" data-module=\"govuk-details\">\n  <summary class=\"govuk-details__summary\">\n    "
                + "<span class=\"govuk-details__summary-text\">\n      View the documents needed by HM Courts and Tribunal Service\n    "
                + "</span>\n  </summary>\n  <div class=\"govuk-details__text\">\n    You now need to send us<br/>"
                + "<ul><li>your reference number 1528365719153338 written on a piece of paper</li><li>the stamped (receipted) "
                + "IHT 421 with this application</li><li>a photocopy of the signed legal statement and declaration</li></ul>\n  </div>\n"
                + "</details></p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">These steps are completed by HM Courts and Tribunals Service staff. "
                + "It can take a few weeks before the review starts.</font></p></div><div class=\"govuk-grid-column-one-third\">"
                + "&nbsp;</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Authenticate documents</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We will authenticate your documents and match them with your application.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Examine application</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "We review your application for incomplete information or problems and validate it against other cases or caveats. "
                + "After the review we prepare the grant.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "Your application will update through any of these case states as it is reviewed by our team:</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<ul class=\"govuk-list govuk-list--bullet\">\n"
                + "<li>Examining</li>\n<li>Case Matching</li>\n<li>Case selected for Quality Assurance</li>\n<li>Ready to issue</li>\n"
                + "</ul><hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "This step is completed by HM Courts and Tribunals Service staff.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Issue grant of representation</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"></div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "The grant will be delivered in the post a few days after issuing.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n</div>\n</div>\n";

        assertEquals(expected, taskList);
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenMarkingReadyToIssue() {
        final String response = postCwJson("caseprogressadmonwill/11-markReadyToIssue.json", CASE_MATCHING_EXAMINING_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        final String expected = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n<h2 class=\"govuk-heading-l\">"
                + "1. Enter application details</h2>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add solicitor details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p>"
                + "<img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Add deceased details</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Add application details</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are to be completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">"
                + "&nbsp;</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Review and sign legal statement and submit application</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">The legal statement is generated. You can review, change any details, then sign "
                + "and submit your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Send documents<br/>"
                + "<details class=\"govuk-details\" data-module=\"govuk-details\">\n  <summary class=\"govuk-details__summary\">\n    "
                + "<span class=\"govuk-details__summary-text\">\n      View the documents needed by HM Courts and Tribunal Service\n    "
                + "</span>\n  </summary>\n  <div class=\"govuk-details__text\">\n    You now need to send us<br/><ul>"
                + "<li>your reference number 1528365719153338 written on a piece of paper</li><li>the stamped (receipted) IHT 421 with this "
                + "application</li><li>a photocopy of the signed legal statement and declaration</li></ul>\n  </div>\n</details></p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">These steps are completed by HM Courts and Tribunals Service staff. "
                + "It can take a few weeks before the review starts.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div>"
                + "</div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Authenticate documents</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" "
                + "width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We will authenticate your documents and match them with your application.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Examine application</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it against "
                + "other cases or caveats. After the review we prepare the grant.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by our team:"
                + "</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<ul class=\"govuk-list "
                + "govuk-list--bullet\">\n<li>Examining</li>\n<li>Case Matching</li>\n<li>Case selected for Quality Assurance</li>\n"
                + "<li>Ready to issue</li>\n</ul><hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">"
                + "\n\n<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "This step is completed by HM Courts and Tribunals Service staff.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Issue grant of representation</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" "
                + "width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n"
                + "</div></div>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">The grant will be delivered in the post a few days after issuing.</font>"
                + "</p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n</div>\n</div>\n";

        assertEquals(expected, taskList);
    }

    @Test
    public void shouldTransformCaseCorrectlyWhenIssuingGrant() {
        final String response = postCwJson("caseprogressadmonwill/12-issueGrant.json", GENERATE_GRANT_URL);
        final JsonPath jsonPath = JsonPath.from(response);
        final String taskList = jsonPath.get("data.taskList");
        String expectedHtml = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
                + "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Add solicitor details</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" "
                + "title=\"COMPLETED\" /></p>\n</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add deceased details</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>"
                + "\n</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "Add application details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" "
                + "width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Review and sign legal statement and submit application</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "The legal statement is generated. You can review, change any details, then sign and submit your application.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Send documents<br/><details class=\"govuk-details\" data-module=\"govuk-details\">"
                + "\n  <summary class=\"govuk-details__summary\">\n    <span class=\"govuk-details__summary-text\">"
                + "\n      View the documents needed by HM Courts and Tribunal Service\n    </span>\n  </summary>"
                + "\n  <div class=\"govuk-details__text\">\n    You now need to send us<br/><ul><li>your reference number "
                + "1528365719153338 written on a piece of paper</li><li>the stamped (receipted) IHT 421 with this application</li>"
                + "<li>a photocopy of the signed legal statement and declaration</li></ul>\n  </div>\n</details></p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">These steps are completed by HM Courts and Tribunals Service staff. "
                + "It can take a few weeks before the review starts.</font></p></div><div class=\"govuk-grid-column-one-third\">"
                + "&nbsp;</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">"
                + "\n\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Authenticate documents</p></div><div class=\"govuk-grid-column-one-third\">"
                + "<p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p><strong>Authenticated on <today/></strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;"
                + "</div></div>\n<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We will authenticate your documents and match them with your application.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<hr class=\"govuk-section-break "
                + "govuk-section-break--m govuk-section-break--visible\">\n\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Examine application</p></div>"
                + "<div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it "
                + "against other cases or caveats. After the review we prepare the grant.</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "Your application will update through any of these case states as it is reviewed by our team:</font></p></div>"
                + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n<ul class=\"govuk-list govuk-list--bullet\">\n"
                + "<li>Examining</li>\n<li>Case Matching</li>\n<li>Case selected for Quality Assurance</li>\n"
                + "<li>Ready to issue</li>\n</ul><hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n<div class=\"govuk-grid-row\">"
                + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">This step is "
                + "completed by HM Courts and Tribunals Service staff.</font></p></div><div class=\"govuk-grid-column-one-third\">"
                + "&nbsp;</div></div>\n<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\">Issue grant of representation</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">The grant will be delivered in the post a few days after issuing.</font>"
                + "</p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n</div>\n</div>\n";

        expectedHtml = expectedHtml.replaceAll(Pattern.quote("<today/>"), this.todaysDate);

        assertEquals(expectedHtml, taskList);
    }

    private String postCwJson(String jsonFileName, String path) {

        Response jsonResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path)
                .andReturn();

        return jsonResponse.getBody().asString();
    }

    private String postSolJson(String jsonFileName, String path) {

        Response jsonResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getSolicitorHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path)
                .andReturn();

        return jsonResponse.getBody().asString();
    }

    public void validatePostRequestSuccessCYAForBeforeSignSOT() {
        Response response = given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("success.beforeSignSOT.checkYourAnswersPayload.json")).
                        when().post("/nextsteps/validate");

        TestCase.assertEquals(200, response.getStatusCode());
    }
}
