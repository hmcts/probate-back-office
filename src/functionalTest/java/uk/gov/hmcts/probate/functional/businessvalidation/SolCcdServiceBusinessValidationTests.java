package uk.gov.hmcts.probate.functional.businessvalidation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.validator.IHTFourHundredDateValidationRule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceBusinessValidationTests extends IntegrationTestBase {

    public static final String NOTIFICATION_DOCUMENT_BINARY_URL =
        "data.probateNotificationsGenerated[0].value.DocumentLink.document_binary_url";
    private static final String VALIDATE_CASE_AMEND_URL = "/case/validateCaseDetails";
    private static final String VALIDATE_URL = "/case/sols-validate";
    private static final String VALIDATE_IHT_400_DATE = "/case/sols-validate-iht400";
    private static final String TRANSFORM_URL = "/case/casePrinted";
    private static final String CHECKLIST_URL = "/case/validateCheckListDetails";
    private static final String PAPER_FORM_URL = "/case/paperForm";
    private static final String RESOLVE_STOP_URL = "/case/resolveStop";
    private static final String REDEC_COMPLETE = "/case/redeclarationComplete";
    private static final String CASE_STOPPED_URL = "/case/case-stopped";
    private static final String REDECLARATION_SOT = "/case/redeclarationSot";
    private static final String DEFAULT_SOLS_NEXT_STEP = "/case/default-sols-next-steps";
    private static final String SOL_VALIDATE_MAX_EXECUTORS_URL = "/case/sols-validate-executors";
    private static final String SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL = "/case/sols-validate-will-and-codicil-dates";
    private static final String TODAY_YYYY_MM_DD = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    private static final String VALIDATE_PROBATE_URL = "/case/sols-validate-probate";

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void verifyRequestWithDobBeforeDod() {
        validatePostSuccess("success.solicitorCreate.json",
                VALIDATE_URL);
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
    public void verifyRequestWithIhtDateIsValid() {
        String payload = utils.getJsonFromFile("success.solicitorAppWithIHT400Date.json");
        payload = replaceAllInString(payload, "\"solsIHT400Date\": \"2019-12-01\",",
            "\"solsIHT400Date\": \""
                    + IHTFourHundredDateValidationRule.minusBusinessDays(LocalDate.now(), 20) + "\",");
        validatePostSuccessForPayload(payload, VALIDATE_IHT_400_DATE);
    }

    @Test
    public void verifyRequestWithIhtDateIsInFutureReturnsError() {
        String payload = utils.getJsonFromFile("success.solicitorAppWithIHT400Date.json");
        payload = replaceAllInString(payload, "\"solsIHT400Date\": \"2019-12-01\",",
            "\"solsIHT400Date\": \"" + LocalDate.now().plusDays(10) + "\",");
        validatePostFailureWithPayload(payload,
            "The date you sent the IHT400 and IHT421 to HMRC must be in the past",
            200, VALIDATE_IHT_400_DATE);
    }

    @Test
    public void verifyRequestWithIhtDateIsAfter20DaysBeforeCurrentDateReturnsError() {
        final CaseData caseData = CaseData.builder().build();
        final LocalDate solsIHT400Date = IHTFourHundredDateValidationRule.minusBusinessDays(LocalDate.now(), 5);
        String payload = utils.getJsonFromFile("success.solicitorAppWithIHT400Date.json");
        payload = replaceAllInString(payload, "\"solsIHT400Date\": \"2019-12-01\",",
            "\"solsIHT400Date\": \"" + solsIHT400Date + "\",");
        validatePostFailureWithPayload(
            payload, "You cannot submit this application until "
                + caseData.convertDate(IHTFourHundredDateValidationRule.addBusinessDays(solsIHT400Date, 20))
                + " (20 working days after sending the IHT400 and IHT421 forms to HMRC)."
                + " Submit this application on or after this date",
            200, VALIDATE_IHT_400_DATE);
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
    public void verifyRequestCheckListAnswerEqualsYesAndCheckQAState() {
        final ResponseBody body = validatePostSuccess("solicitorPayloadNotifications.json", CHECKLIST_URL);
        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String state = jsonPath.get("data.state");

        assertEquals("BOCaseQA", state);
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
            .config(config)
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
    public void verifyErrorMessageSuccAllRenouncing() {
        validatePostFailure("failure.practitionerExecAndApplyingSuccAllRenouncing.json",
            "Probate practitioner cannot be applying if "
                + "part of a group which is all renouncing", 200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    public void verifyErrorMessageAllRenouncing() {
        validatePostFailure("failure.practitionerExecAndApplyingAllRenouncing.json",
            "Probate practitioner cannot be applying if "
                + "part of a group which is all renouncing", 200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    public void verifyErrorMessageNoneOfThese() {
        validatePostFailure("failure.practitionerExecAndApplyingTCTNoT.json",
            "If you have selected none of these because the title and clearing is not "
                + "covered by the options above, you will not be able to continue making this application online. "
                + "Please apply with a paper form.", 200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    public void verifyErrorMessageNoPartnersAdded() {
        validatePostFailure("failure.practitionerNotAnExecNotApplyingNoPartnersAdded.json",
            "You need to add at least 1 other partner that acts as an executor",
            200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    public void verifyErrorMessageNoPartnersAddedTrustCorp() {
        validatePostFailure("failure.practitionerNotAnExecNotApplyingNoPartnersTrustCorp.json",
            "You need to add at least 1 other partner that acts on behalf of the trust corporation",
            200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    public void verifyErrorMessageNoPositionInTrustTrustCorp() {
        validatePostFailure("failure.practitionerNoPositionInTrust.json",
                "You must specify the probate pactitioner's position within the trust corporation "
                            + "as per the resolution if they are acting as an executor",
                200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    public void verifyNoOfApplyingExecutorsMoreThanFour() {
        validatePostFailure("failure.moreThanFourExecutors.json",
            "The total number executors applying cannot exceed 4", 200, VALIDATE_URL);
        validatePostFailure("failure.moreThanFourExecutors.json",
            "The total number executors applying cannot exceed 4", 200, VALIDATE_CASE_AMEND_URL);
        validatePostFailure("failure.moreThanFourExecutors.json",
            "The total number executors applying cannot exceed 4",
            200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    public void shouldPassOriginalWillAndCodicilDateValidationWithValidDates() {
        validatePostSuccess("success.validWillAndCodicilDates.json", VALIDATE_URL);
        validatePostSuccess("success.validWillAndCodicilDates.json",
                SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    public void shouldFailOriginalWillAndCodicilDateValidationWithInvalidWillDate() {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload,"\"originalWillSignedDate\": \"2017-10-10\",",
                "\"originalWillSignedDate\": \"" + TODAY_YYYY_MM_DD + "\",");

        validatePostFailureWithPayload(payload,"Original will signed date must be in the past",
                200, VALIDATE_URL);

        validatePostFailureWithPayload(payload,"Original will signed date must be in the past",
                200, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    public void shouldFailOriginalWillAndCodicilDateValidationWithInvalidCodicilDate() {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload,"\"dateCodicilAdded\": \"2020-10-11\"",
                "\"dateCodicilAdded\": \"" + TODAY_YYYY_MM_DD + "\"");

        validatePostFailureWithPayload(payload,"Codicil date must be in the past",
                200, VALIDATE_URL);

        validatePostFailureWithPayload(payload,"Codicil date must be in the past",
                200, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    public void shouldFailOriginalWillAndCodicilDateValidationWhenWillDateIsAfterDeathDate() {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload,"\"originalWillSignedDate\": \"2017-10-10\",",
                "\"originalWillSignedDate\": \"2018-01-02\",");

        validatePostFailureWithPayload(payload,"The will must be signed and dated before the date of death",
                200, VALIDATE_URL);

        validatePostFailureWithPayload(payload,"The will must be signed and dated before the date of death",
                200, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    public void shouldFailOriginalWillAndCodicilDateValidationWhenWillDateIsOnDeathDate() {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload,"\"originalWillSignedDate\": \"2017-10-10\",",
                "\"originalWillSignedDate\": \"2018-01-01\",");

        validatePostFailureWithPayload(payload,"The will must be signed and dated before the date of death",
                200, VALIDATE_URL);

        validatePostFailureWithPayload(payload,"The will must be signed and dated before the date of death",
                200, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    public void shouldPassOriginalWillAndCodicilDateValidationWhenWillDateIsBeforeDeathDate() {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload,"\"originalWillSignedDate\": \"2017-10-10\",",
                "\"originalWillSignedDate\": \"2017-12-31\",");

        validatePostSuccessForPayload(payload, VALIDATE_URL);
        validatePostSuccessForPayload(payload, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }


    @Test
    public void shouldFailOriginalWillAndCodicilDateValidationWithCodicilDateBeforeWillDate() {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload,"\"dateCodicilAdded\": \"2020-10-11\"",
                "\"dateCodicilAdded\": \"2017-10-09\"");

        validatePostFailureWithPayload(payload,"A codicil cannot be made before the will was signed",
                200, VALIDATE_URL);

        validatePostFailureWithPayload(payload,"A codicil cannot be made before the will was signed",
                200, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    public void shouldFailOriginalWillAndCodicilDateValidationWithCodicilDateSameAsWillDate() {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload,"\"dateCodicilAdded\": \"2020-10-11\"",
                "\"dateCodicilAdded\": \"2017-10-10\"");

        validatePostFailureWithPayload(payload,"A codicil cannot be made before the will was signed",
                200, VALIDATE_URL);

        validatePostFailureWithPayload(payload,"A codicil cannot be made before the will was signed",
                200, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    public void shouldPassOriginalWillAndCodicilDateValidationWithCodicilDateOneDayAfterWillDate() {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload,"\"dateCodicilAdded\": \"2020-10-11\"",
                "\"dateCodicilAdded\": \"2017-10-11\"");

        validatePostSuccessForPayload(payload, VALIDATE_URL);
        validatePostSuccessForPayload(payload, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    public void verifyNegativeCopiesValues() {
        validatePostFailure("failure.negativeUKCopies.json",
            "Uk Grant copies cannot be negative", 400, VALIDATE_CASE_AMEND_URL);
        validatePostFailure("failure.negativeOverseasCopies.json",
            "Overseas Grant copies cannot be negative", 400, VALIDATE_CASE_AMEND_URL);
    }
    
    @Test
    public void verifySuccessPaperFormYes() {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,"\"paperForm\": null,", "\"paperForm\": \"Yes\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "paperForm", "Yes");
    }

    @Test
    public void verifySuccessPaperFormNo() {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,"\"paperForm\": null,", "\"paperForm\": \"No\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "paperForm", "No");
    }

    @Test
    public void verifySchemaVersionNullWhenPaperFormNoForIntestacy() {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,"\"paperForm\": null,", "\"paperForm\": \"No\",");
        payload = replaceAllInString(payload,
                "\"applicationType\": \"Personal\",", "\"applicationType\": \"Solicitor\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", null);
    }

    @Test
    public void verifySchemaVersionNullWhenPaperFormNoForAdmonWill() {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");
        payload = replaceAllInString(payload,
                "\"caseType\": \"intestacy\",", "\"caseType\": \"admonWill\",");
        payload = replaceAllInString(payload,
                "\"applicationType\": \"Personal\",", "\"applicationType\": \"Solicitor\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", null);
    }

    @Test
    public void verifySchemaVersionPaperFormNull() {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,
                "\"caseType\": \"intestacy\",", "\"caseType\": \"gop\",");
        payload = replaceAllInString(payload,
                "\"applicationType\": \"Personal\",", "\"applicationType\": \"Solicitor\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", "2.0.0");
    }

    @Test
    public void verifySchemaVersionPaperFormYes() {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,"\"paperForm\": null,", "\"paperForm\": \"Yes\",");
        payload = replaceAllInString(payload,
                "\"caseType\": \"intestacy\",", "\"caseType\": \"gop\",");
        payload = replaceAllInString(payload,
                "\"applicationType\": \"Personal\",", "\"applicationType\": \"Solicitor\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", null);
    }

    @Test
    public void verifySchemaVersionPaperFormNo() {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,"\"paperForm\": null,", "\"paperForm\": \"No\",");
        payload = replaceAllInString(payload,
                "\"caseType\": \"intestacy\",", "\"caseType\": \"gop\",");
        payload = replaceAllInString(payload,
                "\"applicationType\": \"Personal\",", "\"applicationType\": \"Solicitor\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", "2.0.0");
    }

    @Test
    public void verifySchemaVersionPaperFormNoPersonalApplication() {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,"\"paperForm\": null,", "\"paperForm\": \"No\",");
        payload = replaceAllInString(payload,
                "\"caseType\": \"intestacy\",", "\"caseType\": \"gop\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", null);
    }

    @Test
    public void verifyCaseworkerCreatedPersonalApplicationPaperFormYesWithoutEmail() {
        String payload = getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,
                "\"primaryApplicantEmailAddress\": \"primary@probate-test.com\",",
                "\"primaryApplicantEmailAddress\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedPersonalApplicationPaperFormNoWithoutEmail() {
        String payload = getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,
                "\"primaryApplicantEmailAddress\": \"primary@probate-test.com\",",
            "\"primaryApplicantEmailAddress\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedPersonalApplicationPaperFormYesWithEmail() {
        String payload = getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContents("caseworkerCreatedPersonalEmailPaperFormYesResponse.txt",
            NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedPersonalApplicationPaperFormNoWithEmail() {
        String payload = getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContents("caseworkerCreatedPersonalEmailPaperFormNoResponse.txt",
            NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedSolicitorApplicationPaperFormYesWithoutEmail() {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"solsSolicitorEmail\": \"solicitor@probate-test.com\",",
            "\"solsSolicitorEmail\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedSolicitorApplicationPaperFormNoWithoutEmail() {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"solsSolicitorEmail\": \"solicitor@probate-test.com\",",
            "\"solsSolicitorEmail\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedSolicitorApplicationPaperFormYesWithEmail() {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContents("caseworkerCreatedSolicitorEmailPaperFormYesResponse.txt",
            NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedSolicitorApplicationPaperFormNoWithEmail() {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContents("caseworkerCreatedSolicitorEmailPaperFormNoResponse.txt",
            NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    public void verifyCaseworkerCreatedSolicitorApplicationTcSchema_NotTrustCorp() {
        String payload = getJsonFromFile("solicitorPayloadTrustCorpsSchema.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");

        validatePostSuccessForPayload(payload, PAPER_FORM_URL);
    }

    @Test
    public void verifyCaseworkerCreatedSolicitorApplicationTcSchema_TrustCorps() {
        String payload = getJsonFromFile("solicitorPayloadTrustCorpsSchema.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");
        payload = replaceAllInString(payload, "\"titleAndClearingType\": \"TCTTrustCorpResWithApp\",",
        "\"titleAndClearingType\": \"TCTPartSuccPowerRes\","
                + "\n\"whoSharesInCompanyProfits\" : [\"Partners\", \"Members\"],");

        validatePostSuccessForPayload(payload, PAPER_FORM_URL);
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
        final String payload = utils.getJsonFromFile("solicitorExecutorsCaseStopped.json");
        final ResponseBody result = validatePostSuccessForPayload(payload, CASE_STOPPED_URL);
        final JsonPath jsonPath = JsonPath.from(result.prettyPrint());
        final String grantStoppedDate = jsonPath.get("data.grantStoppedDate");
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
        final ResponseBody responseBody = validatePostSuccess("successRedeclarationnSOT.json",
                REDECLARATION_SOT);
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String errors = jsonPath.get("data.errors");
        final String paperForm = jsonPath.get("data.paperForm");
        assertNull(errors);
        assertEquals(paperForm, "No");
    }

    @Test
    public void verifyRequestValidationsErrorForRedeclarationSOTForPaperFormCase() {
        final ResponseBody responseBody = validatePostSuccess("redeclarationSOTPaperForm.json",
                REDECLARATION_SOT);
        Assert.assertTrue(responseBody.asString().contains("You can only use this event for digital cases"));
    }

    @Test
    public void verifyRequestSuccessForRedeclarationCompleteWithoutStateChange() {
        final ResponseBody body = validatePostSuccess("payloadWithResponseRecorded.json", REDEC_COMPLETE);
        body.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(body.asString());


        assertNull(jsonPath.get("data.errors"));
        assertEquals(jsonPath.get("data.solsSOTNeedToUpdate"), "No");
    }

    @Test
    public void verifyTitleAndClearingListsReset() {
        ResponseBody body = validatePostSuccess("solicitorAmendTitleAndClearingMultipleExecutors.json",
                VALIDATE_PROBATE_URL);

        JsonPath jsonPath = JsonPath.from(body.asString());
        String powerReservedExecs = jsonPath.get("data.dispenseWithNoticeOtherExecsList");
        String trustCorpExecs = jsonPath.get("data.additionalExecutorsTrustCorpList");

        assertNull(powerReservedExecs);
        assertNull(trustCorpExecs);
    }

    @Test
    public void verifyRequestInTestacySuccessForDefaultNext() {
        final ResponseBody body = validatePostSuccessForPayload(utils.getJsonFromFile("solicitorPDFPayloadIntestacy" 
                + ".json"), DEFAULT_SOLS_NEXT_STEP, utils.getHeadersWithCaseworkerUser());

        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String willExist = jsonPath.get("data.willExists");
        final String errors = jsonPath.get("data.errors");

        assertEquals(willExist, "No");
        assertNull(errors);
    }

    @Test
    public void verifySuccessForDefaultNextStepsWithProbateSingleExecutorPayload() {
        final ResponseBody body = validatePostSuccessForPayload(
                utils.getJsonFromFile("solicitorPDFPayloadProbateSingleExecutor.json"),
                DEFAULT_SOLS_NEXT_STEP, utils.getHeadersWithCaseworkerUser());
        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String willExist = jsonPath.get("data.willExists");
        final String errors = jsonPath.get("data.errors");

        assertEquals(willExist, "Yes");
        assertNull(errors);
    }

    @Test()
    public void verifyRequestProbateSuccessForDefaultingPBAsOnNextSteps() {
        final ResponseBody body = validatePostSuccessForPayload(utils.getJsonFromFile(
                "solicitorPDFPayloadProbate.json"),
                DEFAULT_SOLS_NEXT_STEP, utils.getHeadersWithCaseworkerUser());
        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String errors = jsonPath.get("data.errors");
        final HashMap solsPBANumbers = jsonPath.get("data.solsPBANumber");

        assertNull(errors);
        assertNotNull(solsPBANumbers);
        List<HashMap> listItems = ((List<HashMap>)solsPBANumbers.get("list_items"));
        assertEquals(2, ((List)solsPBANumbers.get("list_items")).size());
        String allPBAs = listItems.get(0).get("code") + "," + listItems.get(1).get("code");
        assertThat(allPBAs, containsString("PBA0082126"));
        assertThat(allPBAs, containsString("PBA0083372"));
    }

    @Test
    public void verifySuccessForDefaultNextStepsWithProbateMultipleExecutorPayload() {
        ResponseBody response = validatePostSuccess("solicitorPDFPayloadProbateMultipleExecutors.json",
                DEFAULT_SOLS_NEXT_STEP);

        JsonPath jsonPath = JsonPath.from(response.asString());
        String willExist = jsonPath.get("data.willExists");
        String errors = jsonPath.get("data.errors");

        assertEquals(willExist, "Yes");
        assertNull(errors);
    }
    
    @Test
    public void verifyCaseHandedOffToLegacySite() {
        final ResponseBody body = validatePostSuccess("success.caseHandedOffToLegacySite.json",
            VALIDATE_CASE_AMEND_URL);
        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String caseHasBeenHandedOffToLegacySite = jsonPath.get("data.caseHandedOffToLegacySite");
        assertEquals(caseHasBeenHandedOffToLegacySite, "Yes");
    }

    @Test
    public void verifyCaseNotHandedToLegacySite() {
        final ResponseBody body = validatePostSuccess("success.caseNotHandedOffToLegacySite.json",
            VALIDATE_CASE_AMEND_URL);
        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String caseHasBeenHandedOffToLegacySite = jsonPath.get("data.caseHandedOffToLegacySite");
        assertEquals(caseHasBeenHandedOffToLegacySite, "No");
    }

    @Test
    public void shouldTransformCasePADeceasedAliasOneField() {
        final String response = transformCase("personalPayloadNotifications.json", TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        final String alias = jsonPath.get("data.solsDeceasedAliasNamesList[0].value.SolsAliasname");

        assertEquals("Giacomo Terrel", alias);
    }

    @Test
    public void shouldTransformCaseWithScannedDocuments() {
        final String response = transformCase("success.scannedDocuments.json", TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        final String controlNumber = jsonPath.get("data.scannedDocuments[0].value.controlNumber");
        final String fileName = jsonPath.get("data.scannedDocuments[0].value.fileName");
        final String type = jsonPath.get("data.scannedDocuments[0].value.type");
        final String subtype = jsonPath.get("data.scannedDocuments[0].value.subtype");
        final String documentUrl = jsonPath.get("data.scannedDocuments[0].value.url.document_url");
        final String documentBinaryUrl = jsonPath.get("data.scannedDocuments[0].value.url.document_binary_url");
        final String documentFilename = jsonPath.get("data.scannedDocuments[0].value.url.document_filename");
        final String exceptionRecordReference = jsonPath.get("data.scannedDocuments[0].value.exceptionRecordReference");

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
    public void shouldTransformCaseWithIht217Attributes() {
        final String response = transformCase("success.iht217Saved.json", TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        final String iht217 = jsonPath.get("data.iht217");

        assertEquals("Yes", iht217);
    }

    @Test
    public void shouldTransformCaseWithCitizenAttributes() {
        final String response = transformCase("success.CitizenAttribtesSaved.json", TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        final String declarationCheckbox = jsonPath.get("data.declarationCheckbox");
        final String ihtGrossValueField = jsonPath.get("data.ihtGrossValueField");
        final String ihtNetValueField = jsonPath.get("data.ihtNetValueField");
        final int numberOfExecutors = jsonPath.get("data.numberOfExecutors");
        final int numberOfApplicants = jsonPath.get("data.numberOfApplicants");
        final String legalDeclarationJson = jsonPath.get("data.legalDeclarationJson");
        final String checkAnswersSummaryJson = jsonPath.get("data.checkAnswersSummaryJson");
        final String registryAddress = jsonPath.get("data.registryAddress");
        final String registryEmailAddress = jsonPath.get("data.registryEmailAddress");
        final String registrySequenceNumber = jsonPath.get("data.registrySequenceNumber");

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

    @Test
    public void shouldTransformSolicitorInfoAttributes() {
        final String response = transformCase("success.SolicitorInfoAttributes.json", TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        final String solsForenames = jsonPath.get("data.solsForenames");
        final String solsSurname = jsonPath.get("data.solsSurname");
        final String solsSolicitorWillSignSOT = jsonPath.get("data.solsSolicitorWillSignSOT");

        assertEquals("Solicitor Forenames", solsForenames);
        assertEquals("Solicitor Surname", solsSurname);
        assertEquals("Yes", solsSolicitorWillSignSOT);
    }

    @Test
    public void shouldTransformCaseWithTrustCorpAttributes() {
        String response = transformCase("success.trustCorpAttributesSaved.json", TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        final String dispenseWithNotice = jsonPath.get("data.dispenseWithNotice");
        final String dispenseWithNoticeLeaveGiven = jsonPath.get("data.dispenseWithNoticeLeaveGiven");
        final String dispenseWithNoticeOverview = jsonPath.get("data.dispenseWithNoticeOverview");
        final String dispenseWithNoticeSupportingDocs = jsonPath.get("data.dispenseWithNoticeSupportingDocs");
        final String titleAndClearingType = jsonPath.get("data.titleAndClearingType");
        final String trustCorpName = jsonPath.get("data.trustCorpName");
        final String trustCorpAddressLine1 = jsonPath.get("data.trustCorpAddress.AddressLine1");
        final String lodgementAddress = jsonPath.get("data.lodgementAddress");
        final String lodgementDate = jsonPath.get("data.lodgementDate");
        final String additionalExecForename =
                jsonPath.get("data.additionalExecutorsTrustCorpList[0].value.additionalExecForenames");
        final String additionalExecLastname =
                jsonPath.get("data.additionalExecutorsTrustCorpList[0].value.additionalExecLastname");
        final String additionalExecPosition =
                jsonPath.get("data.additionalExecutorsTrustCorpList[0].value.additionalExecutorTrustCorpPosition");

        assertEquals("Yes", dispenseWithNotice);
        assertEquals("No", dispenseWithNoticeLeaveGiven);
        assertEquals("Overview", dispenseWithNoticeOverview);
        assertEquals("Supporting docs", dispenseWithNoticeSupportingDocs);
        assertEquals("TCTTrustCorpResWithApp", titleAndClearingType);
        assertEquals("Trust Corporation Name", trustCorpName);
        assertEquals("London", lodgementAddress);
        assertEquals("2020-01-01", lodgementDate);
        assertEquals("Exec forename", additionalExecForename);
        assertEquals("Exec lastname", additionalExecLastname);
        assertEquals("Solicitor", additionalExecPosition);
        assertEquals("Trust Address line 1", trustCorpAddressLine1);
    }

    @Test
    public void shouldTransformCaseWithPartnerAttributes() {
        final String response = transformCase("success.nonTrustCorpOptionsSaved.json", TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        assertEquals("Yes", jsonPath.get("data.dispenseWithNotice"));
        assertEquals("No", jsonPath.get("data.dispenseWithNoticeLeaveGiven"));
        assertEquals("Overview", jsonPath.get("data.dispenseWithNoticeOverview"));
        assertEquals("Supporting docs", jsonPath.get("data.dispenseWithNoticeSupportingDocs"));
        assertEquals("TCTPartSuccPowerRes", jsonPath.get("data.titleAndClearingType"));
        assertEquals("Test Solicitor Ltd", jsonPath.get("data.nameOfFirmNamedInWill"));
        assertEquals("New Firm Ltd", jsonPath.get("data.nameOfSucceededFirm"));
        assertEquals("No", jsonPath.get("data.morePartnersHoldingPowerReserved"));
        assertEquals("Exec forename",
                jsonPath.get("data.otherPartnersApplyingAsExecutors[0].value.additionalExecForenames"));
        assertEquals("Exec lastname",
                jsonPath.get("data.otherPartnersApplyingAsExecutors[0].value.additionalExecLastname"));
        assertEquals("Address line 1",
                jsonPath.get("data.otherPartnersApplyingAsExecutors[0].value.additionalExecAddress.AddressLine1"));
        assertEquals("1",
            jsonPath.get("data.addressOfSucceededFirm.AddressLine1"));
        assertEquals("1",
            jsonPath.get("data.addressOfFirmNamedInWill.AddressLine1"));
        assertEquals("Partners", jsonPath.get("data.whoSharesInCompanyProfits[0]"));
    }

    @Test
    public void shouldTransformCaseWithFurtherEvidenceForApplication() {
        final String response = transformCase("success.solicitorFurtherEvidenceForApplication.json",
            TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        assertEquals("Further Evidence", jsonPath.get("data.furtherEvidenceForApplication"));
    }

    @Test
    public void shouldTransformAliasNamesCorrectlyOtherNamesYes() {
        final String response = transformCase("success.SolicitorAddDeceasedEstateDetails.json",
                TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        final String alias = jsonPath.get("data.solsDeceasedAliasNamesList[0].value.SolsAliasname");

        assertEquals("decalias1", alias);
    }

    @Test
    public void shouldTransformAliasNamesCorrectlyOtherNamesNo() {
        String payload = getJsonFromFile("success.SolicitorAddDeceasedEstateDetails.json");
        payload = replaceAllInString(payload, "\"deceasedAnyOtherNames\": \"Yes\",",
                "\"deceasedAnyOtherNames\": \"No\",");

        final String response = transformCaseFromPayload(payload, TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        assertNull(jsonPath.get("data.solsDeceasedAliasNamesList"));
    }


    private String transformCase(String jsonFileName, String path) {
        return transformCaseFromPayload(utils.getJsonFromFile(jsonFileName), path);
    }

    private String transformCaseFromPayload(String jsonPayload, String path) {
        final Response jsonResponse = RestAssured.given()
                .config(config)
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(jsonPayload)
                .when().post(path).andReturn();

        return jsonResponse.getBody().asString();
    }

    private void validatePostSuccessAndCheckValue(String jsonPayload, String url, String caseDataAttribute,
                                                  String caseDataValue) {
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(jsonPayload)
            .when().post(url)
            .thenReturn();

        response.then().assertThat().statusCode(200)
            .and().body("data." + caseDataAttribute, equalTo(caseDataValue));
    }

    private void validatePostFailureForSolicitorCreateAndCaseAmend(String jsonFileName, String errorMessage,
                                                                   Integer statusCode) {
        validatePostFailure(jsonFileName, errorMessage, statusCode, VALIDATE_URL);
        validatePostFailure(jsonFileName, errorMessage, statusCode, VALIDATE_CASE_AMEND_URL);
    }

    private void validatePostFailureForSolicitorAddDeceasedEstateDetails(String jsonFileName, String errorMessage,
                                                                         Integer statusCode) {
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

    private void validatePostFailure(String jsonFileName, String errorMessage, Integer statusCode, String url) {
        final String payload = utils.getJsonFromFile(jsonFileName);
        validatePostFailureWithPayload(payload, errorMessage, statusCode, url);        
    }

    private void validatePostFailureWithPayload(String payload, String errorMessage, Integer statusCode, String url) {
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(payload)
            .when().post(url)
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
