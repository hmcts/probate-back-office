package uk.gov.hmcts.probate.functional.notifications;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static junit.framework.TestCase.assertTrue;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class ScheduledNotificationsTests extends IntegrationTestBase {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String APPLY_FOR_GRANT_PAYLOAD = "applyforGrantPaperApplicationManPayload.json";
    private static final String AWAITING_DOCS_RESPONSE = "awaitingDocsEmailExpectedResponse.txt";
    private static final String GRANT_DELAYED = "/notify/grant-delayed-scheduled";
    private static final String GRANT_AWAITING_DOCUMENTATION = "/notify/grant-awaiting-documents-scheduled";
    private static final String TOKEN_PARM = "TOKEN_PARM";
    private static final String EVENT_PARM = "EVENT_PARM";
    private static final String RESPONSE_CASE_NUM_PARM = "XXXXXXXXXXXXXXXX";
    private static final long ES_DELAY = 10000l;
    
    private static final String EVENT_APPLY = "applyforGrantPaperApplicationMan";
    private static final String EVENT_PRINT_CASE = "boPrintCase";
    private static final String EVENT_MARK_AS_READY_FOR_EXAMINATION = "boMarkAsReadyForExamination";

    private static final String DOC_INDEX = "DOC_INDEX";
    private static final String GRANT_SCHEDULE_EMAIL_NOTIFICATION_URL = "case_data.probateNotificationsGenerated["+DOC_INDEX+"].value.DocumentLink.document_binary_url";

    @Test
    public void createCaseAndVerifyGrantDelayed() throws InterruptedException {
        String delayedDate = DATE_FORMAT.format(LocalDate.now());

        String grantDelayCaseJson = utils.getJsonFromFile(APPLY_FOR_GRANT_PAYLOAD);
        grantDelayCaseJson = grantDelayCaseJson.replaceAll(EVENT_PARM, EVENT_APPLY);
        
        String applyforGrantPaperApplicationManResponse = utils.createCaseAsCaseworker(grantDelayCaseJson);
        JsonPath jsonPathApply = JsonPath.from(applyforGrantPaperApplicationManResponse);
        String caseId = jsonPathApply.get("id").toString();

        String printCaseStartResponseToken = utils.startUpdateCaseAsCaseworker(caseId, EVENT_PRINT_CASE);
        String printCaseUpdateJson = grantDelayCaseJson.replaceAll(TOKEN_PARM, printCaseStartResponseToken);
        printCaseUpdateJson = printCaseUpdateJson.replaceAll(EVENT_PARM, EVENT_PRINT_CASE);
        printCaseUpdateJson = printCaseUpdateJson.replaceAll("\"applicationID\": \"603\",", "\"applicationID\": \"603\",\"grantDelayedNotificationDate\": \"" + delayedDate + "\",");
        String printCaseUpdateResponse = utils.updateCaseAsCaseworker(printCaseUpdateJson, caseId);

        String markAsReadyForExaminationStartResponseToken = utils.startUpdateCaseAsCaseworker(caseId, EVENT_MARK_AS_READY_FOR_EXAMINATION);
        String markAsReadyForExaminationUpdateJson = printCaseUpdateJson.replaceAll(printCaseStartResponseToken, markAsReadyForExaminationStartResponseToken);
        markAsReadyForExaminationUpdateJson = markAsReadyForExaminationUpdateJson.replaceAll(EVENT_PRINT_CASE, EVENT_MARK_AS_READY_FOR_EXAMINATION);
        String markAsReadyForExaminationUpdateResponse = utils.updateCaseAsCaseworker(markAsReadyForExaminationUpdateJson, caseId);

        //pause to enable ccd logstash/ES to index the case update
        Thread.sleep(ES_DELAY);
        Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithSchedulerCaseworkerUser())
            .when().post(GRANT_DELAYED + "?date=" + delayedDate)
            .andReturn();

        response.then().assertThat().statusCode(200);

        String delayResponse = response.getBody().asString();
        assertTrue(delayResponse.contains(caseId));

        String expectedText = utils.getJsonFromFile(AWAITING_DOCS_RESPONSE).replaceAll(RESPONSE_CASE_NUM_PARM, caseId);
        String delayedCase = utils.findCaseAsCaseworker(caseId);
        JsonPath delayedCaseJson = JsonPath.from(delayedCase);
        String documentUrl = delayedCaseJson.get(GRANT_SCHEDULE_EMAIL_NOTIFICATION_URL.replaceAll(DOC_INDEX, "1"));
        String emailDocText = utils.downloadPdfAndParseToStringWithUserId(documentUrl, utils.getSchedulerCaseworkerUserId());
        emailDocText = emailDocText.replace("\n", "").replace("\r", "");
        assertTrue(emailDocText.contains(expectedText));

    }

    @Test
    public void createCaseAndVerifyGrantAwaitingDocumentation() throws InterruptedException {
        String docDate = DATE_FORMAT.format(LocalDate.now().plusDays(21));

        String grantDocCaseJson = utils.getJsonFromFile(APPLY_FOR_GRANT_PAYLOAD);
        grantDocCaseJson = grantDocCaseJson.replaceAll(EVENT_PARM, EVENT_APPLY);
        String applyforGrantPaperApplicationManResponse = utils.createCaseAsCaseworker(grantDocCaseJson);
        JsonPath jsonPathApply = JsonPath.from(applyforGrantPaperApplicationManResponse);
        String caseId = jsonPathApply.get("id").toString();

        String printCaseStartResponseToken = utils.startUpdateCaseAsCaseworker(caseId, EVENT_PRINT_CASE);
        String printCaseUpdateJson = grantDocCaseJson.replaceAll(TOKEN_PARM, printCaseStartResponseToken);
        printCaseUpdateJson = printCaseUpdateJson.replaceAll(EVENT_PARM, EVENT_PRINT_CASE);
        String printCaseUpdateResponse = utils.updateCaseAsCaseworker(printCaseUpdateJson, caseId);

        //pause to enable ccd logstash/ES to index the case update
        Thread.sleep(ES_DELAY);
        Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithSchedulerCaseworkerUser())
            .when().post(GRANT_AWAITING_DOCUMENTATION + "?date=" + docDate)
            .andReturn();

        response.then().assertThat().statusCode(200);

        String docResponse = response.getBody().asString();
        assertTrue(docResponse.contains(caseId));

        String expectedText = utils.getJsonFromFile(AWAITING_DOCS_RESPONSE).replaceAll(RESPONSE_CASE_NUM_PARM, caseId);
        String docCase = utils.findCaseAsCaseworker(caseId);
        JsonPath docCaseJson = JsonPath.from(docCase);
        String documentUrl = docCaseJson.get(GRANT_SCHEDULE_EMAIL_NOTIFICATION_URL.replaceAll(DOC_INDEX, "0"));
        String emailDocText = utils.downloadPdfAndParseToStringWithUserId(documentUrl, utils.getSchedulerCaseworkerUserId());
        emailDocText = emailDocText.replace("\n", "").replace("\r", "");
        assertTrue(emailDocText.contains(expectedText));

    }
}