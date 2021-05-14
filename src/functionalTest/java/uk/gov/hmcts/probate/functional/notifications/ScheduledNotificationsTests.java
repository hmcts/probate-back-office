package uk.gov.hmcts.probate.functional.notifications;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.Pending;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static junit.framework.TestCase.assertTrue;
import static uk.gov.hmcts.probate.functional.util.FunctionalTestUtils.TOKEN_PARM;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class ScheduledNotificationsTests extends IntegrationTestBase {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String APPLY_FOR_GRANT_PAYLOAD = "applyforGrantPaperApplicationManPayload.json";
    private static final String GRANT_DELAY_RESPONSE = "grantDelayEmailExpectedResponse.txt";
    private static final String AWAITING_DOCS_RESPONSE = "awaitingDocsEmailExpectedResponse.txt";
    private static final String GRANT_DELAYED = "/notify/grant-delayed-scheduled";
    private static final String GRANT_AWAITING_DOCUMENTATION = "/notify/grant-awaiting-documents-scheduled";
    private static final String EVENT_PARM = "EVENT_PARM";
    private static final String RESPONSE_CASE_NUM_PARM = "XXXXXXXXXXXXXXXX";
    private static final long ES_DELAY = 20000L;
    private static final String EVENT_APPLY = "applyforGrantPaperApplicationMan";
    private static final String EVENT_PRINT_CASE = "boPrintCase";
    private static final String EVENT_MARK_AS_READY_FOR_EXAMINATION = "boMarkAsReadyForExamination";
    private static final String DOC_INDEX = "DOC_INDEX";
    private static final String GRANT_SCHEDULE_EMAIL_NOTIFICATION_URL =
        "case_data.probateNotificationsGenerated[" + DOC_INDEX + "].value.DocumentLink.document_binary_url";
    private static final String ATTRIBUTE_GRANT_DELAYED_NOTIFICATION_DATE = "grantDelayedNotificationDate";
    @Value("${notifications.grantAwaitingDocumentationNotificationPeriodDays}")
    private String grantAwaitingDocumentationNotificationPeriodDays;

    @Test
    @Pending
    public void createCaseAndVerifyGrantDelayed() throws InterruptedException {
        final String delayedDate = DATE_FORMAT.format(LocalDate.now());

        final String baseCaseJson = utils.getJsonFromFile(APPLY_FOR_GRANT_PAYLOAD);
        final String grantDelayCaseJson = utils.replaceAttribute(baseCaseJson, EVENT_PARM, EVENT_APPLY);

        final String applyforGrantPaperApplicationManResponse = utils.createCaseAsCaseworker(grantDelayCaseJson,
                EVENT_APPLY);
        final JsonPath jsonPathApply = JsonPath.from(applyforGrantPaperApplicationManResponse);
        final String caseId = jsonPathApply.get("id").toString();

        final String printCaseStartResponseToken = utils.startUpdateCaseAsCaseworker(caseId, EVENT_PRINT_CASE);
        String printCaseUpdateJson = utils.replaceAttribute(baseCaseJson, TOKEN_PARM, printCaseStartResponseToken);
        printCaseUpdateJson = utils.replaceAttribute(printCaseUpdateJson, EVENT_PARM, EVENT_PRINT_CASE);
        printCaseUpdateJson =
            utils.addAttribute(printCaseUpdateJson, ATTRIBUTE_GRANT_DELAYED_NOTIFICATION_DATE, delayedDate);

        utils.continueUpdateCaseAsCaseworker(printCaseUpdateJson, caseId);

        final String markAsReadyForExaminationStartResponseToken =
            utils.startUpdateCaseAsCaseworker(caseId, EVENT_MARK_AS_READY_FOR_EXAMINATION);
        String markAsReadyForExaminationUpdateJson = utils
            .replaceAttribute(printCaseUpdateJson, printCaseStartResponseToken,
                markAsReadyForExaminationStartResponseToken);
        markAsReadyForExaminationUpdateJson = utils
            .replaceAttribute(markAsReadyForExaminationUpdateJson, EVENT_PRINT_CASE,
                EVENT_MARK_AS_READY_FOR_EXAMINATION);

        utils.continueUpdateCaseAsCaseworker(markAsReadyForExaminationUpdateJson, caseId);

        postAndAssertAsScheduler(GRANT_DELAYED, delayedDate, caseId);

        final String expectedText = utils.getJsonFromFile(GRANT_DELAY_RESPONSE).replaceAll(RESPONSE_CASE_NUM_PARM,
                caseId);
        final String delayedCase = utils.findCaseAsCaseworker(caseId);
        final JsonPath delayedCaseJson = JsonPath.from(delayedCase);
        final String documentUrl = delayedCaseJson.get(GRANT_SCHEDULE_EMAIL_NOTIFICATION_URL.replaceAll(DOC_INDEX,
                "2"));
        final String emailDocText = removeCrLfs(utils.downloadPdfAndParseToStringForScheduler(documentUrl));
        assertTrue(emailDocText.contains(expectedText));
    }

    @Pending
    @Test
    public void createCaseAndVerifyGrantAwaitingDocumentation() throws InterruptedException {
        final String baseCaseJson = utils.getJsonFromFile(APPLY_FOR_GRANT_PAYLOAD);
        final String grantDocCaseJson = utils.replaceAttribute(baseCaseJson, EVENT_PARM, EVENT_APPLY);
        final String applyforGrantPaperApplicationManResponse = utils.createCaseAsCaseworker(grantDocCaseJson,
                EVENT_APPLY);
        final JsonPath jsonPathApply = JsonPath.from(applyforGrantPaperApplicationManResponse);
        final String caseId = jsonPathApply.get("id").toString();

        final String updateGrantDocCaseJson = utils.replaceAttribute(baseCaseJson, EVENT_PARM, EVENT_PRINT_CASE);
        utils.updateCaseAsCaseworker(updateGrantDocCaseJson, EVENT_PRINT_CASE, caseId);

        final String docDate = DATE_FORMAT
            .format(LocalDate.now().plusDays(Integer.valueOf(grantAwaitingDocumentationNotificationPeriodDays)));
        postAndAssertAsScheduler(GRANT_AWAITING_DOCUMENTATION, docDate, caseId);

        final String expectedText =
            utils.replaceAttribute(utils.getJsonFromFile(AWAITING_DOCS_RESPONSE), RESPONSE_CASE_NUM_PARM, caseId);
        final String docCase = utils.findCaseAsCaseworker(caseId);
        final JsonPath docCaseJson = JsonPath.from(docCase);
        final String documentAtIndex = utils.replaceAttribute(GRANT_SCHEDULE_EMAIL_NOTIFICATION_URL, DOC_INDEX,
                "1");
        final String documentUrl = docCaseJson.get(documentAtIndex);
        final String emailDocText = removeCrLfs(utils.downloadPdfAndParseToStringForScheduler(documentUrl));
        assertTrue(emailDocText.contains(expectedText));
    }

    private void postAndAssertAsScheduler(String path, String date, String caseId) throws InterruptedException {
        //pause to enable ccd logstash/ES to index the case update
        Thread.sleep(ES_DELAY);
        final Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithSchedulerCaseworkerUser())
            .when().post(path + "?date=" + date)
            .andReturn();

        response.then().assertThat().statusCode(200);

        final String delayResponse = response.getBody().asString();
        log.info("delayResponse:" + delayResponse);
        assertTrue(delayResponse.contains(caseId));
    }
}