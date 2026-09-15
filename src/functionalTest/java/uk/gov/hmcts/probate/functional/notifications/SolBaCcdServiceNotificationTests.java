package uk.gov.hmcts.probate.functional.notifications;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@Slf4j
@ExtendWith(SerenityJUnit5Extension.class)
public class SolBaCcdServiceNotificationTests extends IntegrationTestBase {

    private static final String SOLS_STOP_DETAILS = "SOLS stop details";

    private static final String DOCUMENTS_RECEIVED = "/notify/documents-received";
    private static final String GRANT_ISSUED = "/document/generate-grant";
    private static final String GRANT_REISSUED = "/document/generate-grant-reissue";
    private static final String CASE_STOPPED = "/notify/case-stopped";
    private static final String INFORMATION_REQUEST_DEFAULT_VALUES = "/notify/request-information-default-values";
    private static final String INFORMATION_REQUEST = "/notify/stopped-information-request";
    private static final String GRANT_RAISED = "/notify/grant-received";
    private static final String START_GRANT_DELAYED = "/notify/start-grant-delayed-notify-period";
    private static final String APPLICATION_RECEIVED = "/notify/application-received";
    private static final String PAPER_FORM = "/case/paperForm";

    private static final String BIRMINGHAM_NO = "0300 303 0648";
    private static final String REGISTRY_NO = " 0300 303 0648";

    private static final String EMAIL_NOTIFICATION_URL =
        "data.probateNotificationsGenerated[0].value.DocumentLink.document_binary_url";
    private static final String GENERATED_DOCUMENT_URL =
        "data.probateDocumentsGenerated[0].value.DocumentLink.document_binary_url";
    private static final String EMAIL_NOTIFICATION_DOCUMENT_URL = "DocumentLink.document_binary_url";
    private static final String CAVEAT_JSON = "/caveat/createCaveatSolicitor.json";
    private static final String EVENT_PARAMETER = "EVENT_PARM";
    private static final String CAVEAT_RAISE_EVENT = "raiseCaveat";

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void verifyCitizenPaperApplicationReceivedByCaseworkerNotificationSent() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "paperApplicationRecievedCitizenFromCaseworkerPayload.json",
                PAPER_FORM,
                EMAIL_NOTIFICATION_URL);
    }

    @Test
    @Disabled
    void verifyGrantReissueDocument() throws IOException {
        validatePostSuccess("personalPayloadGrantReissued.json", GRANT_REISSUED);
    }

    @Test
    @Disabled
    void verifyIntestacyReissueDocument() throws IOException {
        validatePostSuccess("personalPayloadIntestacyReissued.json", GRANT_REISSUED);
    }

    @Test
    @Disabled
    void verifyAdmonWillReissueDocument() throws IOException {
        validatePostSuccess("personalPayloadAdmonWillReissued.json", GRANT_REISSUED);
    }

    @Test
    @Disabled
    void verifyGrantReissueDocumentAppNameWithApostrophe() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "personalPayloadGrantReissuedNameWithApostrophe.json",
                GRANT_REISSUED,
                GENERATED_DOCUMENT_URL);
    }

    @Test
    @Disabled
    void verifyGrantReissueDocumentAppNameDoubleBarrelled() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "personalPayloadGrantReissuedNameDoubleBarrelled.json",
                GRANT_REISSUED,
                GENERATED_DOCUMENT_URL);
    }

    @Test
    void verifyWelshGrantReissueDocument() throws IOException {
        validatePostSuccess("personalPayloadWelshGrantReissued.json", GRANT_REISSUED);
    }

    @Test
    @Disabled
    void verifyWelshIntestacyReissueDocument() throws IOException {
        validatePostSuccess("personalPayloadWelshIntestacyReissued.json", GRANT_REISSUED);
    }

    @Test
    @Disabled
    void verifyWelshAdmonWillReissueDocument() throws IOException {
        validatePostSuccess("personalPayloadWelshAdmonWillReissued.json", GRANT_REISSUED);
    }

    @Test
    void verifyWelshGrantReissueDocumentAppNameWithApostrophe() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "personalPayloadWelshGrantReissuedNameWithApostrophe.json",
                GRANT_REISSUED,
                GENERATED_DOCUMENT_URL);
    }

    @Test
    @Disabled
    void verifyWelshGrantReissueDocumentAppNameDoubleBarrelled() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "personalPayloadWelshGrantReissuedNameDoubleBarrelled.json",
                GRANT_REISSUED,
                GENERATED_DOCUMENT_URL);
    }

    @Test
    void verifyDigitalGOPApplicationReceivedNotificationEmailText() throws IOException {
        final ResponseBody responseBody = validatePostSuccess("digitalApplicationRecievedPayload.json",
                APPLICATION_RECEIVED);
    }

    @Test
    void verifyDigitalIntestacyApplicationReceivedNotificationSent() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("digitalApplicationRecievedPayload.json",
                    APPLICATION_RECEIVED,"\"caseType\":\"gop\"",
                "\"caseType\":\"intestacy\"");
        assertTrue(responseBody.asString().contains("DocumentLink"));
    }

    @Test
    void verifyPaperApplicationReceivedNotificationSentForNullInPaperForm() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccess("paperApplicationRecievedPayloadForCitizen.json", APPLICATION_RECEIVED);
        assertTrue(responseBody.asString().contains("DocumentLink"));
    }

    @Test
    void verifyPaperApplicationReceivedNotificationNotSent() throws IOException {
        final ResponseBody responseBody = validatePostSuccess("paperApplicationRecievedPayload.json",
                APPLICATION_RECEIVED);
        assertTrue(!responseBody.asString().contains("DocumentLink"));
    }

    @Test
    void verifyDigitalPaperFormGOPApplicationReceivedNotificationEmailTextSolicitorWelsh() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "digitalApplicationRecievedPayloadSolicitorWelsh.json",
                APPLICATION_RECEIVED,
                EMAIL_NOTIFICATION_DOCUMENT_URL);
    }

    @Test
    void verifyPersonalApplicantDocumentsReceivedShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("personalPayloadNotifications.json", DOCUMENTS_RECEIVED);
    }

    @Disabled // tech decision to be made if have these conditional on launch darkly toggle or remove permantently
    @Test
    void verifyPersonalApplicantDocumentReceivedContentIsOk() throws IOException {
        validatePostSuccess("personalPayloadNotifications.json", DOCUMENTS_RECEIVED);
    }

    @Disabled // tech decision to be made if have these conditional on launch darkly toggle or remove permantently
    @Test
    void verifySolicitorApplicantDocumentReceivedContentIsOk() throws IOException {
        validatePostSuccess("solicitorPayloadNotificationsBirmingham.json", DOCUMENTS_RECEIVED);
    }

    @Test
    void verifyPersonalApplicantGrantIssuedShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("personalPayloadNotifications.json", GRANT_ISSUED);
    }

    @Test
    void verifySolicitorGrantRaisedShouldReturnOkResponseCode() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "solicitorPayloadNotifications.json",
                GRANT_RAISED,
                EMAIL_NOTIFICATION_URL);
    }

    @Test
    void verifySolicitorGrantRaisedIntestacyShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_RAISED,
                "\"caseType\":\"gop\"", "\"caseType\":\"intestacy\"");
    }

    @Test
    void verifySolicitorGrantRaisedAdmonWillShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_RAISED,
                "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
    }

    @Disabled // tech decision to be made if have these conditional on launch darkly toggle or remove permantently
    @Test
    void verifySolicitorDocumentsReceivedShouldReturnOkResponseCode() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "solicitorPayloadNotifications.json",
                DOCUMENTS_RECEIVED,
                EMAIL_NOTIFICATION_URL);
    }

    @Disabled
    @Test
    void verifySolicitorDocumentsReceivedIntestacyShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", DOCUMENTS_RECEIVED,
                "\"caseType\":\"gop\"", "\"caseType\":\"intestacy\"");
    }

    @Disabled
    @Test
    void verifySolicitorDocumentsReceivedAdmonWillShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", DOCUMENTS_RECEIVED,
                "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
    }

    @Test
    void verifySolicitorGrantIssuedShouldReturnOkResponseCode() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "solicitorPayloadNotifications.json",
                GRANT_ISSUED,
                EMAIL_NOTIFICATION_URL);
    }

    @Test
    void verifySolicitorGrantIssuedIntestacyShouldReturnOkResponseCode() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "solicitorPayloadNotificationsIntestacy.json",
                GRANT_ISSUED,
                EMAIL_NOTIFICATION_URL);
    }

    @Test
    void verifySolicitorGrantIssuedAdmonWillShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_ISSUED,
                "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
    }

    @Test
    @Disabled
    void verifySolicitorGrantReissuedShouldReturnOkResponseCode() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "solicitorPayloadNotifications.json",
                GRANT_REISSUED,
                EMAIL_NOTIFICATION_URL);
    }

    @Test
    void verifySolicitorGrantReissuedIntestacyShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_REISSUED,
                "\"caseType\":\"gop\"", "\"caseType\":\"intestacy\"");
    }

    @Test
    @Disabled
    void verifySolicitorGrantReissuedAdmonWillShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_REISSUED,
                "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
    }

    @Test
    void verifyPersonalApplicantGrantReissuedShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("personalPayloadNotifications.json", GRANT_REISSUED);
    }

    @Test
    void verifyPersonalApplicantGrantRaisedWithEmailShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("personalRaiseGrantWithEmailNotifications.json", GRANT_RAISED);
    }

    @Test
    void verifyPersonalApplicantGrantRaisedWithoutEmailShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("personalRaiseGrantWithoutEmailNotifications.json", GRANT_RAISED);
    }

    @Test
    void verifyBulkScanPaperFormGOPGrantReceivedNotificationEmailText() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "grantRaisedPaperBulkScanPayload.json",
                GRANT_RAISED,
                EMAIL_NOTIFICATION_URL);
    }

    @Test
    void verifySolicitorBulkScanPaperFormGOPGrantReceivedNotificationEmailText() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "grantRaisedPaperBulkScanSolicitorPayload.json",
                GRANT_RAISED,
                EMAIL_NOTIFICATION_URL);
    }

    @Test
    void verifyBulkScanPaperFormGOPGrantReceivedNotificationEmailTextWelsh() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "grantRaisedPaperBulkScanPayloadWelsh.json",
                GRANT_RAISED,
                EMAIL_NOTIFICATION_URL);
    }

    @Test
    void verifySolicitorBulkScanPaperFormGOPGrantReceivedNotificationEmailTextWelsh() throws IOException {
        assertResponseContainsValueAtJsonPath(
                "grantRaisedPaperBulkScanSolicitorPayloadWelsh.json",
                GRANT_RAISED,
                EMAIL_NOTIFICATION_URL);
    }

    @Test
    void verifyPersonalApplicantGrantReceivedContentIsOk() throws IOException {
        validatePostSuccess("personalRaiseGrantWithEmailNotifications.json", GRANT_RAISED);
    }

    @Test
    void verifySolicitorApplicantGrantIssuedContentIsOk() throws IOException {
        validatePostSuccess("solicitorPayloadNotificationsBirmingham.json", GRANT_ISSUED);
    }

    @Test
    void verifySolicitorCaseStoppedShouldReturnOkResponseCode() throws IOException, InterruptedException {
        String caseId = createCase();
        String payload = utils.getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"boCaseStopCaveatId\": \"1691481848274878\",",
                "\"boCaseStopCaveatId\": \"" + caseId + "\",");
        validatePostSuccessForCaseStopped(payload, CASE_STOPPED);
    }

    @Test
    void verifyPersonalApplicantCaseStoppedContentIsOk() throws IOException, InterruptedException {
        String caseId = createCase();
        String payload = utils.getJsonFromFile("personalPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"boCaseStopCaveatId\": \"1691481848274878\",",
                "\"boCaseStopCaveatId\": \"" + caseId + "\",");
        validatePostSuccessForCaseStopped(payload, CASE_STOPPED);
    }

    @Test
    void verifySpecialCharacterEncodingIsOk() throws IOException, InterruptedException {
        String caseId = createCase();
        String payload = utils.getJsonFromFile("personalPayloadNotificationsSpecialCharacters.json");
        payload = replaceAllInString(payload, "\"boCaseStopCaveatId\": \"1691481848274878\",",
                "\"boCaseStopCaveatId\": \"" + caseId + "\",");
        validatePostSuccessForCaseStopped(payload, CASE_STOPPED);
    }

    @Test
    void verifyPersonalApplicantRequestInformationEmailContentIsOk() throws IOException {
        validatePostSuccess("personalPayloadNotifications.json", INFORMATION_REQUEST);
    }

    @Test
    void verifyPersonalApplicantRequestInformationDefaultValuesIsOk() throws IOException {
        validatePostSuccess("personalPayloadNotifications.json", INFORMATION_REQUEST_DEFAULT_VALUES);
    }

    @Test
    void verifyStartGrantDelayed() throws IOException {
        final ResponseBody responseBody = validatePostSuccess("personalRaiseGrantWithEvidenceHandledNo.json",
                START_GRANT_DELAYED);
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        assertNotNull(jsonPath.get("data.lastEvidenceAddedDate"));
        assertNotNull(jsonPath.get("data.grantDelayedNotificationDate"));
        assertNull(jsonPath.get("data.grantAwaitingDocumentationNotificationDate"));
    }

    public String createCase() throws IOException {
        //Create Case
        final String baseCaseJson = utils.getJsonFromFile(CAVEAT_JSON);
        final String applyForCaveatCaseJson = utils.replaceAttribute(baseCaseJson, EVENT_PARAMETER, CAVEAT_RAISE_EVENT);
        final String applyForGrantCase = utils.createCaveatCaseAsCaseworker(applyForCaveatCaseJson, CAVEAT_RAISE_EVENT);
        final JsonPath jsonPathApply = JsonPath.from(applyForGrantCase);
        final String caseId = jsonPathApply.get("id").toString();
        log.info("createCase : caseId {} ", caseId);
        return caseId;
    }

    private void assertResponseContainsValueAtJsonPath(
            final String payloadFileName,
            final String apiPath,
            final String atJsonPath)
            throws IOException {
        final ResponseBody responseBody = validatePostSuccess(
                payloadFileName,
                apiPath);

        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String documentUrl = jsonPath.get(atJsonPath);

        assertNotNull(documentUrl);
    }
}
