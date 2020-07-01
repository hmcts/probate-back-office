package uk.gov.hmcts.probate.functional.businessvalidation;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceBusinessValidationTests extends IntegrationTestBase {

    private static final String VALIDATE_CASE_AMEND_URL = "/case/validateCaseDetails";
    private static final String VALIDATE_URL = "/case/sols-validate";
    private static final String TRANSFORM_URL = "/case/casePrinted";
    private static final String CHECKLIST_URL = "/case/validateCheckListDetails";
    private static final String PAPER_FORM_URL = "/case/paperForm";
    private static final String RESOLVE_STOP_URL = "/case/resolveStop";
    private static final String REDEC_COMPLETE = "/case/redeclarationComplete";


    @Test
    public void verifyRequestWithDobBeforeDod() {
        validatePostSuccess("success.solicitorCreate.json", VALIDATE_URL);
    }

    @Test
    public void verifyRequestWithDobNullReturnsError() {
        validatePostFailureForSolicitorCreateAndCaseAmend("failure.dobIsNull.json",
                "Date of birth cannot be empty", 400);
    }

    @Test
    public void verifyRequestWithDodNullReturnsError() {
        validatePostFailureForSolicitorCreateAndCaseAmend("failure.dodIsNull.json",
                "Date of death cannot be empty", 400);
    }

    @Test
    public void verifyRequestWithDodBeforeDobReturnsError() {
        validatePostFailureForSolicitorCreateAndCaseAmend("failure.dobIsAfterDod.json",
                "Date of death cannot be before date of birth", 200);
    }

    @Test
    public void verifyRequestWithDodSameAsDobReturnsError() {
        validatePostFailureForSolicitorCreateAndCaseAmend("failure.dodIsSameAsDob.json",
                "Date of death cannot be the same as date of birth", 200);
    }

    @Test
    public void verifyRequestWithDobInFutureReturnsError() {
        validatePostFailureForSolicitorCreateAndCaseAmend("failure.dobIsInTheFuture.json",
                "Date of birth cannot be in the future", 200);
    }

    @Test
    public void verifyRequestWithDodInFutureReturnsError() {
        validatePostFailureForSolicitorCreateAndCaseAmend("failure.dodIsInTheFuture.json",
                "Date of death cannot be in the future", 200);
    }

    @Test
    public void verifyRequestWithIhtNetLessThanGross() {
        validatePostSuccess("success.SolicitorAddDeceasedEstateDetails.json", VALIDATE_URL);
    }

    @Test
    public void verifyRequestWithEmptyIhtGrossReturnsError() {
        validatePostFailureForSolicitorAddDeceasedEstateDetails("failure.ihtGrossIsEmpty.json",
                "Gross IHT value cannot be empty", 400);
    }

    @Test
    public void verifyRequestWithEmptyIhtNetReturnsError() {
        validatePostFailureForSolicitorAddDeceasedEstateDetails("failure.ihtNetIsEmpty.json",
                "Net IHT value cannot be empty", 400);
    }

    @Test
    public void verifyRequestWithIhtNetGreaterThanGrossReturnsError() {
        validatePostFailureForSolicitorAddDeceasedEstateDetails("failure.ihtNetIsGreaterThanGross.json",
                "Net IHT value cannot be greater than the Gross value", 200);
    }

    @Test
    public void verifyRequestWithNegativeIhtNetReturnsError() {
        validatePostFailureForSolicitorAddDeceasedEstateDetails("failure.ihtNetIsNegative.json",
                "Net IHT cannot be negative", 400);
    }

    @Test
    public void verifyRequestWithNegativeIhtGrossReturnsError() {
        validatePostFailureForSolicitorAddDeceasedEstateDetails("failure.ihtGrossIsNegative.json",
                "Gross IHT cannot be negative", 400);
    }

    @Test
    public void verifyRequestWithoutDeceasedAddressReturnsError() {
        validatePostFailureForSolicitorAddDeceasedEstateDetails("failure.missingDeceasedAddress.json",
                "The deceased address line 1 cannot be empty", 200);
    }

    @Test
    public void verifyRequestWithoutDeceasedPostcodeReturnsError() {
        validatePostFailureForSolicitorAddDeceasedEstateDetails("failure.missingDeceasedPostcode.json",
                "The deceased postcode cannot be empty", 200);
    }

    @Test
    public void verifyRequestWithoutExecutorAddressReturnsError() {
        validatePostFailureForSolicitorExecutorDetails("failure.missingExecutorAddress.json",
                "The executor address line 1 cannot be empty");
        validatePostFailureForCaseAmend("failure.missingExecutorAddress.json",
                "The executor address line 1 cannot be empty");
    }

    @Test
    public void verifyRequestWithoutExecutorPostcodeReturnsError() {
        validatePostFailureForSolicitorExecutorDetails("failure.missingExecutorPostcode.json",
                "The executor postcode cannot be empty");
    }

    @Test
    public void verifyRequestCheckListAnswerEqualsYes() {
        validatePostSuccess("solicitorPayloadNotifications.json", CHECKLIST_URL);
    }

    @Test
    public void verifyRequestCheckListAnswerEqualsNo() {
        validatePostFailureForCheckList("failure.checkList.json",
                "Ensure all checks have been completed, cancel to return to the examining state");
    }

    @Test
    public void verifyRequestWithoutExecutorAddressWhileNotApplyingReturnsNoError() {
        validatePostSuccess("success.missingExecutorAddressWhileNotApplying.json", VALIDATE_URL);
        validatePostSuccess("success.missingExecutorAddressWhileNotApplying.json", VALIDATE_CASE_AMEND_URL);
    }

    @Test
    public void verifyEmptyRequestReturnsError() {
        SerenityRest.given().relaxedHTTPSValidation().headers(utils.getHeaders())
                .contentType(ContentType.JSON)
                .body("")
                .when().post(VALIDATE_URL)
                .then().assertThat().statusCode(400);
    }

    @Test
    public void verifyNoOfApplyingExecutorsLessThanFour() {
        validatePostSuccess("success.LessThanFourExecutors.json", VALIDATE_URL);
        validatePostSuccess("success.LessThanFourExecutors.json", VALIDATE_CASE_AMEND_URL);
    }


    @Test
    public void verifyNoOfApplyingExecutorsEqualToFour() {
        validatePostSuccess("success.equalToFourExecutors.json", VALIDATE_URL);
        validatePostSuccess("success.equalToFourExecutors.json", VALIDATE_CASE_AMEND_URL);
    }

    @Test
    public void verifyNoOfApplyingExecutorsMoreThanFour() {
        validatePostFailure("failure.moreThanFourExecutors.json",
                "The total number executors applying cannot exceed 4", 200, VALIDATE_URL);
        validatePostFailure("failure.moreThanFourExecutors.json",
                "The total number executors applying cannot exceed 4", 200, VALIDATE_CASE_AMEND_URL);
    }

    @Test
    public void verifySuccessPaperForm() {
        validatePostSuccess("success.paperForm.json", PAPER_FORM_URL);
    }

    @Test
    public void verifyNoOfApplyingExecutorsLessThanFourTransformCase() {
        validatePostSuccess("success.LessThanFourExecutors.json", TRANSFORM_URL);
    }


    @Test
    public void verifyNoOfApplyingExecutorsEqualToFourTransformCase() {
        validatePostSuccess("success.equalToFourExecutors.json", TRANSFORM_URL);
    }

    @Test
    public void verifyRequestWithDobBeforeDodTransformCase() {
        validatePostSuccess("success.solicitorCreate.json", TRANSFORM_URL);
    }


    @Test
    public void verifyRequestWithIhtNetLessThanGrossTransformCase() {
        validatePostSuccess("success.SolicitorAddDeceasedEstateDetails.json", TRANSFORM_URL);
    }


    @Test
    public void verifyRequestWithoutExecutorAddressWhileNotApplyingReturnsNoErrorTransformCase() {
        validatePostSuccess("success.missingExecutorAddressWhileNotApplying.json", TRANSFORM_URL);
    }

    @Test
    public void verifyRequestSuccessForResolveStop() {
        validatePostSuccess("solicitorPayloadResolveStop.json", RESOLVE_STOP_URL);
    }

    @Ignore
    @Test
    public void verifyRequestSuccessForRedeclarationCompleteWithStateChange() {
        validatePostSuccess("personalPayloadNotifications.json", REDEC_COMPLETE);
    }

    @Ignore
    @Test
    public void verifyRequestSuccessForRedeclarationCompleteWithoutStateChange() {
        validatePostSuccess("payloadWithResponseRecorded.json", REDEC_COMPLETE);
    }

    @Test
    public void shouldTransformCasePADeceasedAliasOneField() {
        String response = transformCase("personalPayloadNotifications.json", TRANSFORM_URL);

        JsonPath jsonPath = JsonPath.from(response);
        String alias = jsonPath.get("data.solsDeceasedAliasNamesList[0].value.SolsAliasname");

        assertEquals("Giacomo Terrel", alias);

    }

    @Test
    public void shouldTransformCaseWithScannedDocuments() {
        String response = transformCase("success.scannedDocuments.json", TRANSFORM_URL);

        JsonPath jsonPath = JsonPath.from(response);
        String controlNumber = jsonPath.get("data.scannedDocuments[0].value.controlNumber");
        String fileName = jsonPath.get("data.scannedDocuments[0].value.fileName");
        String type = jsonPath.get("data.scannedDocuments[0].value.type");
        String subtype = jsonPath.get("data.scannedDocuments[0].value.subtype");
        String documentUrl = jsonPath.get("data.scannedDocuments[0].value.url.document_url");
        String documentBinaryUrl = jsonPath.get("data.scannedDocuments[0].value.url.document_binary_url");
        String documentFilename = jsonPath.get("data.scannedDocuments[0].value.url.document_filename");
        String exceptionRecordReference = jsonPath.get("data.scannedDocuments[0].value.exceptionRecordReference");

        assertEquals("1234", controlNumber);
        assertEquals("scanneddocument.pdf", fileName);
        assertEquals("other", type);
        assertEquals("will", subtype);
        assertEquals("http://somedoc", documentUrl);
        assertEquals("http://somedoc.pdf/binary", documentBinaryUrl);
        assertEquals("somedoc.pdf", documentFilename);
        assertEquals("EX-REF-REC-001", exceptionRecordReference);

    }

    @Test
    public void shouldTransformCaseSOLSAdditionalExec() {
        String response = transformCase("solicitorPayloadNotificationsAddExecs.json", TRANSFORM_URL);

        JsonPath jsonPath = JsonPath.from(response);
        String notApplyingName = jsonPath.get("data.executorsNotApplying[0].value.notApplyingExecutorName");
        String notApplyingReason = jsonPath.get("data.executorsNotApplying[0].value.notApplyingExecutorReason");
        String notApplyingAlias = jsonPath.get("data.executorsNotApplying[0].value.notApplyingExecutorNameOnWill");

        String applyingName = jsonPath.get("data.executorsApplying[0].value.applyingExecutorName");
        String applyingAlias = jsonPath.get("data.executorsApplying[0].value.applyingExecutorOtherNames");
        String addressLine1 = jsonPath.get("data.executorsApplying[0].value.applyingExecutorAddress.AddressLine1");
        String addressLine2 = jsonPath.get("data.executorsApplying[0].value.applyingExecutorAddress.AddressLine2");
        String addressLine3 = jsonPath.get("data.executorsApplying[0].value.applyingExecutorAddress.AddressLine3");
        String postTown = jsonPath.get("data.executorsApplying[0].value.applyingExecutorAddress.PostTown");
        String postCode = jsonPath.get("data.executorsApplying[0].value.applyingExecutorAddress.PostCode");
        String county = jsonPath.get("data.executorsApplying[0].value.applyingExecutorAddress.County");
        String country = jsonPath.get("data.executorsApplying[0].value.applyingExecutorAddress.Country");

        String applyingNameExec2 = jsonPath.get("data.executorsApplying[1].value.applyingExecutorName");
        String applyingAliasExec2 = jsonPath.get("data.executorsApplying[1].value.applyingExecutorOtherNames");
        String addressLine1Exec2 = jsonPath.get("data.executorsApplying[1].value.applyingExecutorAddress.AddressLine1");
        String addressLine2Exec2 = jsonPath.get("data.executorsApplying[1].value.applyingExecutorAddress.AddressLine2");
        String addressLine3Exec2 = jsonPath.get("data.executorsApplying[1].value.applyingExecutorAddress.AddressLine3");
        String postTownExec2 = jsonPath.get("data.executorsApplying[1].value.applyingExecutorAddress.PostTown");
        String postCodeExec2 = jsonPath.get("data.executorsApplying[1].value.applyingExecutorAddress.PostCode");
        String countyExec2 = jsonPath.get("data.executorsApplying[1].value.applyingExecutorAddress.County");
        String countryExec2 = jsonPath.get("data.executorsApplying[1].value.applyingExecutorAddress.Country");


        assertEquals("exfn2 exln2", notApplyingName);
        assertEquals("DiedBefore", notApplyingReason);
        assertEquals("alias name", notApplyingAlias);

        assertEquals("exfn1 exln1", applyingName);
        assertEquals("Alias name exfn1", applyingAlias);
        assertEquals("addressline 1", addressLine1);
        assertEquals("addressline 2", addressLine2);
        assertEquals("addressline 3", addressLine3);
        assertEquals("posttown", postTown);
        assertEquals("postcode", postCode);
        assertEquals("country", country);
        assertEquals("county", county);


        assertEquals("ex3fn ex3ln", applyingNameExec2);
        assertEquals(null, applyingAliasExec2);
        assertEquals("addressline 1", addressLine1Exec2);
        assertEquals(null, addressLine2Exec2);
        assertEquals(null, addressLine3Exec2);
        assertEquals(null, postTownExec2);
        assertEquals("postcode", postCodeExec2);
        assertEquals(null, countryExec2);
        assertEquals(null, countyExec2);

    }


    private String transformCase(String jsonFileName, String path) {

        Response jsonResponse = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path).andReturn();

        return jsonResponse.getBody().asString();
    }

    private void validatePostSuccess(String jsonFileName, String URL) {
        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(URL)
                .then().assertThat().statusCode(200);
    }

    private void validatePostFailureForSolicitorCreateAndCaseAmend(String jsonFileName, String errorMessage, Integer statusCode) {
        validatePostFailure(jsonFileName, errorMessage, statusCode, VALIDATE_URL);
        validatePostFailure(jsonFileName, errorMessage, statusCode, VALIDATE_CASE_AMEND_URL);
    }

    private void validatePostFailureForSolicitorAddDeceasedEstateDetails(String jsonFileName, String errorMessage, Integer statusCode) {
        validatePostFailure(jsonFileName, errorMessage, statusCode, VALIDATE_URL);
    }

    private void validatePostFailureForSolicitorExecutorDetails(String jsonFileName, String errorMessage) {
        validatePostFailure(jsonFileName, errorMessage, 200, VALIDATE_URL);
    }

    private void validatePostFailureForCaseAmend(String jsonFileName, String errorMessage) {
        validatePostFailure(jsonFileName, errorMessage, 200, VALIDATE_CASE_AMEND_URL);
    }
    private void validatePostFailureForCheckList(String jsonFileName, String errorMessage) {
        validatePostFailure(jsonFileName, errorMessage, 200, CHECKLIST_URL);
    }

    private void validatePostFailure(String jsonFileName, String errorMessage, Integer statusCode, String URL) {
        Response response = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(URL)
                .thenReturn();

        if (statusCode == 200) {
            response.then().assertThat().statusCode(statusCode)
                    .and().body("errors", hasSize(greaterThanOrEqualTo(1)))
                    .and().body("errors", hasItem(containsString(errorMessage)));
        } else if (statusCode == 400) {
            response.then().assertThat().statusCode(statusCode)
                    .and().body("error", equalTo("Invalid Request"))
                    .and().body("fieldErrors", hasSize(greaterThanOrEqualTo(1)))
                    .and().body("fieldErrors[0].message", equalTo(errorMessage));
        } else {
            assert false;
        }
    }

}
