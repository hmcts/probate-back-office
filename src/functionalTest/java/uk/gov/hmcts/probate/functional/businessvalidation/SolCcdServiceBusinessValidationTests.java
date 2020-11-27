package uk.gov.hmcts.probate.functional.businessvalidation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceBusinessValidationTests extends IntegrationTestBase {

    private static final String VALIDATE_CASE_AMEND_URL = "/case/validateCaseDetails";
    private static final String VALIDATE_URL = "/case/sols-validate";
    private static final String TRANSFORM_URL = "/case/casePrinted";
    private static final String CHECKLIST_URL = "/case/validateCheckListDetails";
    private static final String PAPER_FORM_URL = "/case/paperForm";
    private static final String RESOLVE_STOP_URL = "/case/resolveStop";
    private static final String REDEC_COMPLETE = "/case/redeclarationComplete";
    public static final String NOTIFICATION_DOCUMENT_BINARY_URL = "data.probateNotificationsGenerated[0].value.DocumentLink.document_binary_url";
    private static final String CASE_STOPPED_URL = "/case/case-stopped";
    private static final String REDECLARATION_SOT ="/case/redeclarationSot";
    private static final String SOL_APPLY_AS_EXECUTOR_URL ="/case/sols-apply-as-exec";
    private static final String DEFAULT_SOLS_NEXT_STEP = "/case/default-sols-next-steps";

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
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeaders())
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
    public void verifySuccessPaperFormYes() {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = payload.replaceAll("\"paperForm\": null,", "\"paperForm\": \"Yes\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "paperForm", "Yes");
    }

    @Test
    public void verifySuccessPaperFormNo() {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = payload.replaceAll("\"paperForm\": null,", "\"paperForm\": \"No\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "paperForm", "No");
    }

    @Test
    public void verifyCaseworkerCreatedPersonalApplicationPaperFormYesWithoutEmail() {
        String payload = getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"primaryApplicantEmailAddress\": \"fname@fttest.com\",", "\"primaryApplicantEmailAddress\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedPersonalApplicationPaperFormNoWithoutEmail() {
        String payload = getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"primaryApplicantEmailAddress\": \"fname@fttest.com\",", "\"primaryApplicantEmailAddress\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");

        ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedPersonalApplicationPaperFormYesWithEmail() {
        String payload = getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContents("caseworkerCreatedPersonalEmailPaperFormYesResponse.txt", NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedPersonalApplicationPaperFormNoWithEmail() {
        String payload = getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");

        ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContents("caseworkerCreatedPersonalEmailPaperFormNoResponse.txt", NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedSolicitorApplicationPaperFormYesWithoutEmail() {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"solsSolicitorEmail\": \"probBackosol@gmail.com\",", "\"solsSolicitorEmail\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedSolicitorApplicationPaperFormNoWithoutEmail() {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"solsSolicitorEmail\": \"probBackosol@gmail.com\",", "\"solsSolicitorEmail\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedSolicitorApplicationPaperFormYesWithEmail() {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContents("caseworkerCreatedSolicitorEmailPaperFormYesResponse.txt", NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedSolicitorApplicationPaperFormNoWithEmail() {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");

        ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContents("caseworkerCreatedSolicitorEmailPaperFormNoResponse.txt", NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
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
    public void verifyRequestSuccessForCaseStopped() {
        String payload = utils.getJsonFromFile("solicitorExecutorsCaseStopped.json");
        ResponseBody result = validatePostSuccessForPayload(payload, CASE_STOPPED_URL);
        JsonPath jsonPath = JsonPath.from(result.prettyPrint());
        String grantStoppedDate = jsonPath.get("data.grantStoppedDate");
        assertNotNull(grantStoppedDate);
    }

    @Test
    public void verifyRequestSuccessForResolveStop() {
        validatePostSuccess("solicitorPayloadResolveStop.json", RESOLVE_STOP_URL);
    }

    @Test
    public void verifyRequestSuccessForRedeclarationCompleteWithStateChange() {
        validatePostSuccess("personalPayloadNotifications.json", REDEC_COMPLETE);
    }

    @Test
    public void verifyRequestSuccessForRedeclarationSOTForDigitalCase() {
        ResponseBody responseBody =  validatePostSuccess("successRedeclarationnSOT.json", REDECLARATION_SOT);
        JsonPath jsonPath = JsonPath.from(responseBody.asString());
        String errors = jsonPath.get("data.errors");
        String paperForm = jsonPath.get("data.paperForm");
        assertNull(errors);
        assertEquals(paperForm,"No");
    }

    @Test
    public void verifyRequestValidationsErrorForRedeclarationSOTForPaperFormCase() {
        ResponseBody responseBody = validatePostSuccess("redeclarationSOTPaperForm.json", REDECLARATION_SOT);
        Assert.assertTrue(responseBody.asString().contains("You can only use this event for digital cases"));
    }

    @Test
    public void verifyRequestSuccessSolicitorAsExecutor() {
        ResponseBody responsebody = validatePostSuccess("solicitorPayloadNotificationsMultipleExecutors.json", SOL_APPLY_AS_EXECUTOR_URL);
        JsonPath jsonPath = JsonPath.from(responsebody.asString());
        responsebody.prettyPrint();
        String errors = jsonPath.get("data.errors");
        String solicitoryLegalDoument = jsonPath.get("data.solsLegalStatementDocument.document_filename");
        assertEquals(solicitoryLegalDoument,"legal_statement.pdf");
        assertNull(errors);
    }

     @Test
    public void verifyRequestSuccessForRedeclarationCompleteWithoutStateChange() {
         ResponseBody body = validatePostSuccess("payloadWithResponseRecorded.json", REDEC_COMPLETE);
         body.prettyPrint();
         JsonPath jsonPath = JsonPath.from(body.asString());


         assertNull(jsonPath.get("data.errors"));
         assertEquals(jsonPath.get("data.solsSOTNeedToUpdate"),"No");
    }

    @Test
    public void verifyRequestInTestacySuccessForDefaultNext(){
        ResponseBody body = validatePostSuccess("solicitorPDFPayloadIntestacy.json", DEFAULT_SOLS_NEXT_STEP);

        JsonPath jsonPath = JsonPath.from(body.asString());
        String willExist = jsonPath.get("data.willExists");
        String errors = jsonPath.get("data.errors");

        assertEquals(willExist,"No");
        assertNull(errors);
    }

    @Test
    public void verifyRequestProbateSuccessForDefaultNext(){
        ResponseBody body = validatePostSuccess("solicitorPDFPayloadProbate.json", DEFAULT_SOLS_NEXT_STEP);
        JsonPath jsonPath = JsonPath.from(body.asString());
        String willExist = jsonPath.get("data.willExists");
        String errors = jsonPath.get("data.errors");

        assertEquals(willExist,"Yes");
        assertNull(errors);
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

    @Test
    public void shouldTransformCaseWithCitizenAttributes() {
        String response = transformCase("success.CitizenAttribtesSaved.json", TRANSFORM_URL);

        JsonPath jsonPath = JsonPath.from(response);
        String declarationCheckbox = jsonPath.get("data.declarationCheckbox");
        String ihtGrossValueField = jsonPath.get("data.ihtGrossValueField");
        String ihtNetValueField = jsonPath.get("data.ihtNetValueField");
        int numberOfExecutors = jsonPath.get("data.numberOfExecutors");
        int numberOfApplicants = jsonPath.get("data.numberOfApplicants");
        String legalDeclarationJson = jsonPath.get("data.legalDeclarationJson");
        String checkAnswersSummaryJson = jsonPath.get("data.checkAnswersSummaryJson");
        String registryAddress = jsonPath.get("data.registryAddress");
        String registryEmailAddress = jsonPath.get("data.registryEmailAddress");
        String registrySequenceNumber = jsonPath.get("data.registrySequenceNumber");

        assertEquals("Yes", declarationCheckbox);
        assertEquals("100001.0", ihtGrossValueField);
        assertEquals("90009.0", ihtNetValueField);
        assertEquals(2, numberOfExecutors);
        assertEquals(1, numberOfApplicants);
        assertEquals("some legal declaration json", legalDeclarationJson);
        assertEquals("some check summary json", checkAnswersSummaryJson);
        assertEquals("RegistryAddress", registryAddress);
        assertEquals("RegistryEmail", registryEmailAddress);
        assertEquals("1", registrySequenceNumber);
    }


    private String transformCase(String jsonFileName, String path) {

        Response jsonResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path).andReturn();

        return jsonResponse.getBody().asString();
    }

    private void validatePostSuccessAndCheckValue(String jsonPayload, String URL, String caseDataAttribute, String caseDataValue) {
        Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(jsonPayload)
            .when().post(URL)
            .thenReturn();

            response.then().assertThat().statusCode(200)
                .and().body("data."+caseDataAttribute, equalTo(caseDataValue));
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
        Response response = RestAssured.given()
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
