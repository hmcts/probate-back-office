package uk.gov.hmcts.probate.functional.notifications;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

@RunWith(SerenityRunner.class)
public class SolBaCcdServiceNotificationTests extends IntegrationTestBase {

    @Test
    public void verifySolicitorDocumentsReceivedShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotifications.json", "/notify/documents-received");
    }

    @Test
    public void verifyPersonalApplicantDocumentsReceivedShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", "/notify/documents-received");
    }

    @Test
    public void verifySolicitorGrantIssuedShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotifications.json", "/document/generate-grant");
    }

    @Test
    public void verifyPersonalApplicantGrantIssuedShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", "/document/generate-grant");
    }

    @Test
    public void verifySolicitorCaseStoppedShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotifications.json", "/notify/case-stopped");
    }

    @Test
    public void verifyPersonalApplicantCaseStoppedShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", "/notify/case-stopped");
    }

    private void validatePostSuccess(String jsonFileName, String path) {
        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path)
                .then().assertThat().statusCode(200);
    }
}
