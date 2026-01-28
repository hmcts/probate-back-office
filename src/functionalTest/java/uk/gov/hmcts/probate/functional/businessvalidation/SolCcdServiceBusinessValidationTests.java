package uk.gov.hmcts.probate.functional.businessvalidation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.functional.util.FunctionalTestUtils;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.validator.IHTFourHundredDateValidationRule;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@ExtendWith(SerenityJUnit5Extension.class)
public class SolCcdServiceBusinessValidationTests extends IntegrationTestBase {

    public static final String NOTIFICATION_DOCUMENT_BINARY_URL =
        "data.probateNotificationsGenerated[0].value.DocumentLink.document_binary_url";
    private static final String VALIDATE_CASE_AMEND_URL = "/case/validateCaseDetails";
    private static final String VALIDATE_URL = "/case/sols-validate";
    private static final String VALIDATE_IHT_400_DATE = "/case/sols-validate-iht400";
    private static final String TRANSFORM_URL = "/case/casePrinted";
    private static final String PAPER_FORM_URL = "/case/paperForm";
    private static final String INIT_PAPER_FORM_URL = "/case/initPaperForm";
    private static final String RESOLVE_STOP_URL = "/case/resolveStop";
    private static final String CHANGE_CASE_STATE_URL = "/case/changeCaseState";
    private static final String ESCALATE_TO_REGISTRAR_URL = "/case/case-escalated";
    private static final String REDEC_COMPLETE = "/case/redeclarationComplete";
    private static final String CASE_STOPPED_URL = "/case/case-stopped";
    private static final String CASE_CREATE_VALIDATE_URL = "/case/sols-create-validate";
    private static final String REDECLARATION_SOT = "/case/redeclarationSot";
    private static final String DEFAULT_SOLS_NEXT_STEP = "/case/default-sols-next-steps";
    private static final String SOLS_VALIDATE_IHT_ESTATE = "/case/validate-iht-estate";
    private static final String SOLS_TRANSFORM_RELATIONSHIP_TO_DECEASED = "/case/transformRelationshipToDeceased";
    private static final String DEFAULT_SOLS_IHT_ESTATE = "/case/default-iht-estate";
    private static final String SOL_VALIDATE_MAX_EXECUTORS_URL = "/case/sols-validate-executors";
    private static final String SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL = "/case/sols-validate-will-and-codicil-dates";
    private static final String TODAY_YYYY_MM_DD = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    private static final String VALIDATE_PROBATE_URL = "/case/sols-validate-probate";
    private static final String SOLS_CREATED_URL = "/case/sols-created";
    private static final String SOLS_ACCESS_URL = "/case/sols-access";
    private static final String REACTIVATE_CASE = "/case/reactivate-case";
    private static final String CASE_WORKER_ESCALATED = "/case/case-worker-escalated";
    private static final String CASE_WORKER_RESOLVED_ESCALATED = "/case/resolve-case-worker-escalated";
    private static final String SOLS_CASE_CREATION_PAYLOAD = "solsCaseCreationDefaultPayload.json";
    private static final String SOLS_CASE_CREATE_EVENT_ID = "solicitorCreateApplication";
    private static final String EVENT_PARM = "EVENT_PARM";


    @Autowired
    protected FunctionalTestUtils utils;

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void verifyRequestWithDobBeforeDod() throws IOException {
        validatePostSuccess("success.solicitorCreate.json",
            VALIDATE_URL);
    }

    @Test
    void verifySolicitorCreateRequestWithDodSameAsDob() throws IOException {
        String payload = utils.getJsonFromFile("success.solicitorCreate.json");
        payload = replaceAllInString(payload, "\"deceasedDateOfBirth\": \"1987-01-01\",",
                "\"deceasedDateOfBirth\": \"2018-01-01\",");
        validatePostSuccessAndCheckValue(payload, VALIDATE_URL, "deceasedDateOfBirth", "2018-01-01");
    }

    @Test
    void verifySolicitorAmendRequestWithDodSameAsDob() throws IOException {
        String payload = utils.getJsonFromFile("success.solicitorCreate.json");
        payload = replaceAllInString(payload, "\"deceasedDateOfBirth\": \"1987-01-01\",",
                "\"deceasedDateOfBirth\": \"2018-01-01\",");
        validatePostSuccessAndCheckValue(payload, VALIDATE_CASE_AMEND_URL, "deceasedDateOfBirth", "2018-01-01");
    }

    @Test
    void verifyRequestWithDobNullReturnsError() throws IOException {
        validatePostFailureForSolicitorCreateAndCaseAmend("failure.dobIsNull.json",
            "Date of birth cannot be empty", 400);
    }

    @Test
    void verifyRequestWithDodNullReturnsError() throws IOException {
        validatePostFailureForSolicitorCreateAndCaseAmend("failure.dodIsNull.json",
            "Date of death cannot be empty", 400);
    }

    @Test
    void verifyRequestWithDodBeforeDobReturnsError() throws IOException {
        validatePostFailureForSolicitorCreateAndCaseAmend("failure.dobIsAfterDod.json",
            "Date of death cannot be before date of birth", 200);
    }

    @Test
    void verifyRequestWithDobInFutureReturnsError() throws IOException {
        validatePostFailureForSolicitorCreateAndCaseAmend("failure.dobIsInTheFuture.json",
            "Date of birth cannot be in the future", 200);
    }

    @Test
    void verifyRequestWithDodInFutureReturnsError() throws IOException {
        validatePostFailureForSolicitorCreateAndCaseAmend("failure.dodIsInTheFuture.json",
            "Date of death cannot be in the future", 200);
    }

    @Test
    void verifyRequestWithIhtNetLessThanGross() throws IOException {
        validatePostSuccess("success.SolicitorAddDeceasedEstateDetails.json", VALIDATE_URL);
    }

    @Test
    void verifyRequestWithIhtNetGreaterThanGrossReturnsError() throws IOException {
        validatePostFailure("failure.ihtNetIsGreaterThanGross.json",
                "The gross probate value cannot be less than the net probate value", 200, SOLS_VALIDATE_IHT_ESTATE);

    }

    @Test
    void verifyRequestWithNegativeIhtNetReturnsError() throws IOException {
        validatePostFailureForSolicitorAddDeceasedEstateDetails("failure.ihtNetIsNegative.json",
            "Net IHT cannot be negative", 400);
    }

    @Test
    void verifyRequestWithNegativeIhtGrossReturnsError() throws IOException {
        validatePostFailureForSolicitorAddDeceasedEstateDetails("failure.ihtGrossIsNegative.json",
            "Gross IHT cannot be negative", 400);
    }

    @Test
    void verifyRequestWithIhtDateIsValid() throws IOException {
        String payload = utils.getJsonFromFile("success.solicitorAppWithIHT400Date.json");
        payload = replaceAllInString(payload, "\"solsIHT400Date\": \"2019-12-01\",",
            "\"solsIHT400Date\": \""
                + IHTFourHundredDateValidationRule.minusBusinessDays(LocalDate.now(), 20) + "\",");
        validatePostSuccessForPayload(payload, VALIDATE_IHT_400_DATE);
    }

    @Test
    void verifyRequestWithIhtDateIsInFutureReturnsError() throws IOException {
        String payload = utils.getJsonFromFile("success.solicitorAppWithIHT400Date.json");
        payload = replaceAllInString(payload, "\"solsIHT400Date\": \"2019-12-01\",",
            "\"solsIHT400Date\": \"" + LocalDate.now().plusDays(10) + "\",");
        validatePostFailureWithPayload(payload,
            "The date you sent the IHT400 and IHT421 to HMRC must be in the past",
            200, VALIDATE_IHT_400_DATE);
    }

    @Test
    void verifyRequestWithIhtDateIsAfter20DaysBeforeCurrentDateReturnsError() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final LocalDate solsIHT400Date = IHTFourHundredDateValidationRule.minusBusinessDays(LocalDate.now(), 5);
        String payload = utils.getJsonFromFile("success.solicitorAppWithIHT400Date.json");
        payload = replaceAllInString(payload, "\"solsIHT400Date\": \"2019-12-01\",",
            "\"solsIHT400Date\": \"" + solsIHT400Date + "\",");
        String errorMessage1 = "You must wait until 20 working days after submitting the IHT 400 and 421 to HMRC "
            + "before you apply for probate.";
        String errorMessage2 = "Based on what you've told us about when you submitted the IHT 400 and 421, you"
            + " can submit this case on "
            + caseData.convertDate(IHTFourHundredDateValidationRule.addBusinessDays(solsIHT400Date, 20))
            + ".";
        String errorMessage3 = "You should not try to continue with the application by entering a false date, as "
            + "this may delay this case.";

        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(payload)
            .when().post(VALIDATE_IHT_400_DATE)
            .thenReturn();

        response.then().assertThat().statusCode(200)
            .and().body("errors", hasSize(equalTo(6)))
            .and().body("errors", hasItem(containsString(errorMessage1)))
            .and().body("errors", hasItem(containsString(errorMessage2)))
            .and().body("errors", hasItem(containsString(errorMessage3)));

    }

    @Test
    void verifyRequestWithoutDeceasedAddressReturnsError() throws IOException {
        validatePostFailureForSolicitorAddDeceasedEstateDetails("failure.missingDeceasedAddress.json",
            "The deceased address line 1 cannot be empty", 200);
    }

    @Test
    void verifyRequestWithoutDeceasedPostcodeReturnsError() throws IOException {
        validatePostFailureForSolicitorAddDeceasedEstateDetails("failure.missingDeceasedPostcode.json",
            "The deceased postcode cannot be empty", 200);
    }

    @Test
    void verifyRequestWithoutExecutorAddressReturnsError() throws IOException {
        validatePostFailureForSolicitorExecutorDetails("failure.missingExecutorAddress.json",
            "The executor address line 1 cannot be empty");
        validatePostFailureForCaseAmend("failure.missingExecutorAddress.json",
            "The executor address line 1 cannot be empty");
    }

    @Test
    void verifyRequestWithoutExecutorPostcodeReturnsError() throws IOException {
        validatePostFailureForSolicitorExecutorDetails("failure.missingExecutorPostcode.json",
            "The executor postcode cannot be empty");
    }

    @Test
    void verifyRequestWithoutSolicitorPostcodeReturnsError() throws IOException {
        validatePostFailureForSolicitorPostcode("failure.missingPostcodeSolicitorCreate.json",
                "Enter your firm's postcode, for example, 'SW1H 9AJ'");
    }

    @Test
    void verifyRequestWithSolicitorPostcodeReturnsSuccess() throws IOException {
        validatePostSuccess("success.solicitorCreate.json", CASE_CREATE_VALIDATE_URL);
    }

    @Test
    void verifyRequestWithoutExecutorAddressWhileNotApplyingReturnsNoError() throws IOException {
        validatePostSuccess("success.missingExecutorAddressWhileNotApplying.json", VALIDATE_URL);
        validatePostSuccess("success.missingExecutorAddressWhileNotApplying.json", VALIDATE_CASE_AMEND_URL);
    }

    @Test
    void verifyEmptyRequestReturnsError() {
        RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeadersWithCaseworkerUser())
            .config(config)
            .contentType(ContentType.JSON)
            .body("")
            .when().post(VALIDATE_URL)
            .then().assertThat().statusCode(400);
    }

    @Test
    void verifyNoOfApplyingExecutorsLessThanFour() throws IOException {
        validatePostSuccess("success.LessThanFourExecutors.json", VALIDATE_URL);
        validatePostSuccess("success.LessThanFourExecutors.json", VALIDATE_CASE_AMEND_URL);
    }

    @Test
    void verifyNoOfApplyingExecutorsEqualToFour() throws IOException {
        validatePostSuccess("success.equalToFourExecutors.json", VALIDATE_URL);
        validatePostSuccess("success.equalToFourExecutors.json", VALIDATE_CASE_AMEND_URL);
    }

    @Test
    void verifyErrorMessageSuccAllRenouncing() throws IOException {
        validatePostFailure("failure.practitionerExecAndApplyingSuccAllRenouncing.json",
                "Probate practitioner cannot be applying if "
                        + "part of a group which is all renouncing", 200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    void verifyErrorMessageAllRenouncing() throws IOException {
        validatePostFailure("failure.practitionerExecAndApplyingAllRenouncing.json",
            "Probate practitioner cannot be applying if "
                    + "part of a group which is all renouncing", 200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    void verifyErrorMessageNoneOfThese() throws IOException {
        validatePostFailure("failure.practitionerExecAndApplyingTCTNoT.json",
            "If you have selected 'none of these' because the title and clearing is not covered by the "
                    + "options above, you will not be able to continue making this application online. Please apply "
                    + "with a paper form.", 200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    void verifyErrorMessageNoPartnersAdded() throws IOException {
        validatePostFailure("failure.practitionerNotAnExecNotApplyingNoPartnersAdded.json",
            "You need to add at least 1 other partner that acts as an executor",
            200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    void verifyErrorMessageNoPartnersAddedTrustCorp() throws IOException {
        validatePostFailure("failure.practitionerNotAnExecNotApplyingNoPartnersTrustCorp.json",
            "You need to add at least 1 other partner that acts on behalf of the trust corporation",
                200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    void verifyErrorMessageNoPositionInTrustTrustCorp() throws IOException {
        validatePostFailure("failure.practitionerNoPositionInTrust.json",
            "You must specify the probate pactitioner's position within the trust corporation as per the "
                    + "resolution if they are acting as an executor", 200, SOL_VALIDATE_MAX_EXECUTORS_URL);
    }

    @Test
    void shouldPassOriginalWillAndCodicilDateValidationWithValidDates() throws IOException {
        validatePostSuccess("success.validWillAndCodicilDates.json", VALIDATE_URL);
        validatePostSuccess("success.validWillAndCodicilDates.json",
            SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    void shouldFailOriginalWillAndCodicilDateValidationWithInvalidWillDate() throws IOException {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload, "\"originalWillSignedDate\": \"2017-10-10\",",
            "\"originalWillSignedDate\": \"" + TODAY_YYYY_MM_DD + "\",");

        validatePostFailureWithPayload(payload, "Original will signed date must be in the past",
            200, VALIDATE_URL);

        validatePostFailureWithPayload(payload, "Original will signed date must be in the past",
            200, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    void shouldFailOriginalWillAndCodicilDateValidationWithInvalidCodicilDate() throws IOException {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload, "\"dateCodicilAdded\": \"2020-10-11\"",
            "\"dateCodicilAdded\": \"" + TODAY_YYYY_MM_DD + "\"");

        validatePostFailureWithPayload(payload, "Codicil date must be in the past", 200,
                VALIDATE_URL);

        validatePostFailureWithPayload(payload, "Codicil date must be in the past", 200,
                SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    void shouldFailOriginalWillAndCodicilDateValidationWhenWillDateIsAfterDeathDate() throws IOException {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload, "\"originalWillSignedDate\": \"2017-10-10\",",
            "\"originalWillSignedDate\": \"2018-01-02\",");

        validatePostFailureWithPayload(payload, "The will must be signed and dated before the date of "
                        + "death", 200, VALIDATE_URL);

        validatePostFailureWithPayload(payload, "The will must be signed and dated before the date of "
                        + "death", 200, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    void shouldPassOriginalWillAndCodicilDateValidationWhenWillDateIsBeforeDeathDate() throws IOException {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload, "\"originalWillSignedDate\": \"2017-10-10\",",
            "\"originalWillSignedDate\": \"2017-12-31\",");

        validatePostSuccessForPayload(payload, VALIDATE_URL);
        validatePostSuccessForPayload(payload, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }


    @Test
    void shouldFailOriginalWillAndCodicilDateValidationWithCodicilDateBeforeWillDate() throws IOException {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload, "\"dateCodicilAdded\": \"2020-10-11\"",
            "\"dateCodicilAdded\": \"2017-10-09\"");

        validatePostFailureWithPayload(payload, "A codicil cannot be made before the will was signed",
                200, VALIDATE_URL);

        validatePostFailureWithPayload(payload, "A codicil cannot be made before the will was signed",
                200, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    void shouldPassOriginalWillAndCodicilDateValidationWithCodicilDateOneDayAfterWillDate() throws IOException {
        String payload = utils.getJsonFromFile("success.validWillAndCodicilDates.json");

        payload = replaceAllInString(payload, "\"dateCodicilAdded\": \"2020-10-11\"",
            "\"dateCodicilAdded\": \"2017-10-11\"");

        validatePostSuccessForPayload(payload, VALIDATE_URL);
        validatePostSuccessForPayload(payload, SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL);
    }

    @Test
    void verifyNegativeCopiesValues() throws IOException {
        validatePostFailure("failure.negativeUKCopies.json",
            "Uk Grant copies cannot be negative", 400, VALIDATE_CASE_AMEND_URL);
        validatePostFailure("failure.negativeOverseasCopies.json",
            "Overseas Grant copies cannot be negative", 400, VALIDATE_CASE_AMEND_URL);
    }

    @Test
    void verifySuccessPaperFormYes() throws IOException {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "paperForm", "Yes");
    }

    @Test
    void verifySuccessPaperFormNo() throws IOException {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "paperForm", "No");
    }

    @Test
    void verifySchemaVersionNullWhenPaperFormNoForIntestacy() throws IOException {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");
        payload = replaceAllInString(payload,
            "\"applicationType\": \"Personal\",", "\"applicationType\": \"Solicitor\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", null);
    }

    @Test
    void verifySchemaVersionNullWhenPaperFormNoForAdmonWill() throws IOException {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");
        payload = replaceAllInString(payload,
            "\"caseType\": \"intestacy\",", "\"caseType\": \"admonWill\",");
        payload = replaceAllInString(payload,
            "\"applicationType\": \"Personal\",", "\"applicationType\": \"Solicitor\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", null);
    }

    @Test
    void verifySchemaVersionPaperFormNull() throws IOException {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,
            "\"caseType\": \"intestacy\",", "\"caseType\": \"gop\",");
        payload = replaceAllInString(payload,
            "\"applicationType\": \"Personal\",", "\"applicationType\": \"Solicitor\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", "2.0.0");
    }

    @Test
    void verifySchemaVersionPaperFormYes() throws IOException {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");
        payload = replaceAllInString(payload,
            "\"caseType\": \"intestacy\",", "\"caseType\": \"gop\",");
        payload = replaceAllInString(payload,
            "\"applicationType\": \"Personal\",", "\"applicationType\": \"Solicitor\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", null);
    }

    @Test
    void verifySchemaVersionPaperFormNo() throws IOException {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");
        payload = replaceAllInString(payload,
            "\"caseType\": \"intestacy\",", "\"caseType\": \"gop\",");
        payload = replaceAllInString(payload,
            "\"applicationType\": \"Personal\",", "\"applicationType\": \"Solicitor\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", "2.0.0");
    }

    @Test
    void verifySchemaVersionPaperFormNoPersonalApplication() throws IOException {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");
        payload = replaceAllInString(payload,
            "\"caseType\": \"intestacy\",", "\"caseType\": \"gop\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "schemaVersion", null);
    }

    @Test
    void verifyCaseworkerCreatedPersonalApplicationPaperFormYesWithoutEmail() throws IOException {
        String payload = getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,
            "\"primaryApplicantEmailAddress\": \"primary@probate-test.com\",",
            "\"primaryApplicantEmailAddress\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"channelChoice\": \"PaperForm\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    void verifyCaseworkerCreatedPersonalApplicationPaperFormNoWithoutEmail() throws IOException {
        String payload = getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload,
            "\"primaryApplicantEmailAddress\": \"primary@probate-test.com\",",
            "\"primaryApplicantEmailAddress\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"channelChoice\": \"Digital\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    void verifyCaseworkerCreatedPersonalApplicationPaperFormYesWithEmail() throws IOException {
        String payload = getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"channelChoice\": \"PaperForm\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContents("caseworkerCreatedPersonalEmailPaperFormYesResponse.txt",
            NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    void verifySuccessCaseworkerCreatedPersonalApplicationSameDobAndDod() throws IOException {
        String payload = utils.getJsonFromFile("success.paperForm.json");
        payload = replaceAllInString(payload, "\"deceasedDateOfBirth\": \"1960-01-01\",",
                "\"deceasedDateOfBirth\": \"2018-01-01\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "deceasedDateOfBirth", "2018-01-01");
    }

    @Test
    void verifySuccessCaseworkerCreatedSolicitorApplicationSameDobAndDod() throws IOException {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"deceasedDateOfBirth\": \"1900-01-01\",",
                "\"deceasedDateOfBirth\": \"2000-01-01\",");
        validatePostSuccessAndCheckValue(payload, PAPER_FORM_URL, "deceasedDateOfBirth", "2000-01-01");
    }

    @Test
    void verifyCaseworkerCreatedSolicitorApplicationPaperFormYesWithoutEmail() throws IOException {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"solsSolicitorEmail\": \"solicitor@probate-test.com\",",
            "\"solsSolicitorEmail\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    void verifyCaseworkerCreatedSolicitorApplicationPaperFormNoWithoutEmail() throws IOException {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"solsSolicitorEmail\": \"solicitor@probate-test.com\",",
            "\"solsSolicitorEmail\": null,");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"Yes\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContentsMissing(NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    void verifyCaseworkerCreatedSolicitorApplicationPaperFormYesWithEmail() throws IOException {
        String payload = getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"channelChoice\": \"PaperForm\",");

        final ResponseBody responseBody = validatePostSuccessForPayload(payload, PAPER_FORM_URL);
        assertExpectedContents("caseworkerCreatedSolicitorEmailPaperFormYesResponse.txt",
            NOTIFICATION_DOCUMENT_BINARY_URL, responseBody);
    }

    @Test
    void verifyCaseworkerCreatedSolicitorApplicationTcSchema_NotTrustCorp() throws IOException {
        String payload = getJsonFromFile("solicitorPayloadTrustCorpsSchema.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");

        validatePostSuccessForPayload(payload, PAPER_FORM_URL);
    }

    @Test
    void verifyCaseworkerCreatedSolicitorApplicationTcSchema_TrustCorps() throws IOException {
        String payload = getJsonFromFile("solicitorPayloadTrustCorpsSchema.json");
        payload = replaceAllInString(payload, "\"paperForm\": null,", "\"paperForm\": \"No\",");
        payload = replaceAllInString(payload, "\"titleAndClearingType\": \"TCTTrustCorpResWithApp\",",
            "\"titleAndClearingType\": \"TCTPartSuccPowerRes\","
                + "\n\"whoSharesInCompanyProfits\" : [\"Partners\", \"Members\"],");

        validatePostSuccessForPayload(payload, PAPER_FORM_URL);
    }

    @Test
    void verifyCaseworkerDefaultDateOfDeathType() throws IOException {
        validatePostSuccessAndCheckValue("{\"case_details\":{}}", INIT_PAPER_FORM_URL, "dateOfDeathType", "diedOn");
    }

    @Test
    void verifyNoOfApplyingExecutorsLessThanFourTransformCase() throws IOException {
        validatePostSuccess("success.LessThanFourExecutors.json", TRANSFORM_URL);
    }

    @Test
    void verifyNoOfApplyingExecutorsEqualToFourTransformCase() throws IOException {
        validatePostSuccess("success.equalToFourExecutors.json", TRANSFORM_URL);
    }

    @Test
    void verifyRequestWithDobBeforeDodTransformCase() throws IOException {
        validatePostSuccess("success.solicitorCreate.json", TRANSFORM_URL);
    }

    @Test
    void verifyRequestWithIhtNetLessThanGrossTransformCase() throws IOException {
        validatePostSuccess("success.SolicitorAddDeceasedEstateDetails.json", TRANSFORM_URL);
    }

    @Test
    void verifyRequestWithoutExecutorAddressWhileNotApplyingReturnsNoErrorTransformCase() throws IOException {
        validatePostSuccess("success.missingExecutorAddressWhileNotApplying.json", TRANSFORM_URL);
    }

    @Test
    void verifyRequestSuccessForCaseStopped() throws IOException {
        final String payload = utils.getJsonFromFile("solicitorExecutorsCaseStopped.json");
        final ResponseBody result = validatePostSuccessForPayload(payload, CASE_STOPPED_URL);
        final JsonPath jsonPath = JsonPath.from(result.prettyPrint());
        final String grantStoppedDate = jsonPath.get("data.grantStoppedDate");
        assertNotNull(grantStoppedDate);
    }

    @Test
    void verifyRequestSuccessForResolveStop() throws IOException {
        validatePostSuccess("solicitorPayloadResolveStop.json", RESOLVE_STOP_URL);
    }

    @Test
    void verifyRequestSuccessForChangeCaseState() throws IOException {
        validatePostSuccess("solicitorPayloadChangeCaseState.json", CHANGE_CASE_STATE_URL);
    }

    @Test
    void verifyRequestSuccessForEscalateToRegistrar() throws IOException {
        final String payload = utils.getJsonFromFile("solicitorExecutorsCaseStopped.json");
        final ResponseBody result = validatePostSuccessForPayload(payload, ESCALATE_TO_REGISTRAR_URL);
        final JsonPath jsonPath = JsonPath.from(result.prettyPrint());
        final String escalatedDate = jsonPath.get("data.escalatedDate");
        assertEquals(escalatedDate, TODAY_YYYY_MM_DD);
    }

    @Test
    void verifyRequestSuccessForRedeclarationCompleteWithStateChange() throws IOException {
        validatePostSuccess("personalPayloadNotifications.json", REDEC_COMPLETE);
    }

    @Test
    void verifyRequestSuccessForCaseWorkerEscalation() throws IOException {
        final ResponseBody responseBody = validatePostSuccess("solicitorPayloadCaseWorkerEscalation.json",
                CASE_WORKER_ESCALATED);
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String caseWorkerEscalationDate = jsonPath.get("data.caseWorkerEscalationDate");
        assertEquals(caseWorkerEscalationDate, TODAY_YYYY_MM_DD);
    }

    @Test
    void verifyRequestSuccessForCaseWorkerResolveEscalation() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(
                "solicitorPayloadCaseWorkerResolveEscalation.json", CASE_WORKER_RESOLVED_ESCALATED);
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String resolveCaseWorkerEscalationDate = jsonPath.get("data.resolveCaseWorkerEscalationDate");
        assertEquals(resolveCaseWorkerEscalationDate, TODAY_YYYY_MM_DD);
    }

    @Test
    void verifyRequestSuccessForRedeclarationSOTForDigitalCase() throws IOException {
        final ResponseBody responseBody = validatePostSuccess("successRedeclarationnSOT.json",
            REDECLARATION_SOT);
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        final String errors = jsonPath.get("data.errors");
        final String paperForm = jsonPath.get("data.paperForm");
        final String channelChoice = jsonPath.get("data.channelChoice");
        assertNull(errors);
        assertEquals(paperForm, "No");
        assertEquals(channelChoice, "Digital");
    }

    @Test
    void verifyRequestValidationsErrorForRedeclarationSOTForPaperFormCase() throws IOException {
        final ResponseBody responseBody = validatePostSuccess("redeclarationSOTPaperForm.json",
            REDECLARATION_SOT);
        Assertions.assertTrue(responseBody.asString().contains("You can only use this event for digital cases."));
    }

    @Test
    void verifyRequestSuccessForRedeclarationCompleteWithoutStateChange() throws IOException {
        final ResponseBody body = validatePostSuccess("payloadWithResponseRecorded.json", REDEC_COMPLETE);
        final JsonPath jsonPath = JsonPath.from(body.asString());


        Assertions.assertNull(jsonPath.get("data.errors"));
        Assertions.assertEquals(jsonPath.get("data.solsSOTNeedToUpdate"), "No");
    }

    @Test
    void verifyTitleAndClearingListsReset() throws IOException {
        ResponseBody body = validatePostSuccess("solicitorAmendTitleAndClearingMultipleExecutors.json",
            VALIDATE_PROBATE_URL);

        JsonPath jsonPath = JsonPath.from(body.asString());
        String powerReservedExecs = jsonPath.get("data.dispenseWithNoticeOtherExecsList");
        String trustCorpExecs = jsonPath.get("data.additionalExecutorsTrustCorpList");

        assertNull(powerReservedExecs);
        assertNull(trustCorpExecs);
    }

    @Test
    void verifyRequestInTestacySuccessForUpdatePage2() throws IOException {
        final ResponseBody body = validatePostSuccessForPayload(utils.getJsonFromFile(
                "solicitorPDFPayloadIntestacy.json"), SOLS_TRANSFORM_RELATIONSHIP_TO_DECEASED,
                utils.getHeadersWithCaseworkerUser());

        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String errors = jsonPath.get("data.errors");
        assertNull(errors);
    }

    @Test
    void verifyRequestInTestacyFailureForUpdatePage2() throws IOException {
        String payload = utils.getJsonFromFile("solicitorPDFPayloadIntestacy.json");
        payload = replaceAllInString(payload, "\"deceasedMaritalStatus\": \"marriedCivilPartnership\",",
                "\"deceasedMaritalStatus\": \"divorcedCivilPartnership\",");
        validatePostFailureWithPayload(payload, "The selected marital status is not possible if the "
                        + "applicant is the deceased's husband, wife or civil partner.",
                200, SOLS_TRANSFORM_RELATIONSHIP_TO_DECEASED);
    }

    @Test
    void verifyRequestInTestacyFailureOtherExecutorExistsForUpdatePage2() throws IOException {
        String payload = utils.getJsonFromFile("solicitorPDFPayloadIntestacy.json");
        payload = replaceAllInString(payload, "\"deceasedMaritalStatus\": \"marriedCivilPartnership\",",
                "\"marriedCivilPartnership\": \"marriedCivilPartnership\","
                        + "\n\"otherExecutorExists\" : \"Yes\",");
        validatePostFailureWithPayload(payload, "A joint application is not possible if the main "
                        + "applicant is the deceased's husband, wife or civil partner.\n"
                        + "In some cases, the deceased's child can be a joint "
                        + "applicant. Use Form PA1A to apply by post instead.",
                200, SOLS_TRANSFORM_RELATIONSHIP_TO_DECEASED);
    }

    @Test
    void verifyRequestInTestacySuccessForDefaultNext() throws IOException {
        final ResponseBody body = validatePostSuccessForPayload(utils.getJsonFromFile("solicitorPDFPayloadIntestacy"
            + ".json"), DEFAULT_SOLS_NEXT_STEP, utils.getHeadersWithCaseworkerUser());

        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String willExist = jsonPath.get("data.willExists");
        final String errors = jsonPath.get("data.errors");

        assertEquals(willExist, "No");
        assertNull(errors);
    }

    @Test
    void verifyRequestSuccessForDefaultIhtEstate() throws IOException {
        //adjust with app yml iht-estate.switch-date
        String json = utils.getJsonFromFile("solicitorPayloadIhtEstateDefault.json");
        json = json.replaceAll("<DOD-DATE>", "2022-01-01");
        final ResponseBody body = validatePostSuccessForPayload(json, DEFAULT_SOLS_IHT_ESTATE,
            utils.getHeadersWithSolicitorUser());

        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String dateOfDeathAfterEstateSwitch = jsonPath.get("data.dateOfDeathAfterEstateSwitch");
        final String errors = jsonPath.get("data.errors");

        assertEquals("Yes", dateOfDeathAfterEstateSwitch);
        assertNull(errors);
    }

    @Test
    void verifyRequestSuccessForDefaultIhtEstateNo() throws IOException {
        //adjust with app yml iht-estate.switch-date
        String json = utils.getJsonFromFile("solicitorPayloadIhtEstateDefault.json");
        json = json.replaceAll("<DOD-DATE>", "2021-12-31");
        final ResponseBody body = validatePostSuccessForPayload(json, DEFAULT_SOLS_IHT_ESTATE,
            utils.getHeadersWithSolicitorUser());

        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String dateOfDeathAfterEstateSwitch = jsonPath.get("data.dateOfDeathAfterEstateSwitch");
        final String errors = jsonPath.get("data.errors");

        assertEquals("No", dateOfDeathAfterEstateSwitch);
        assertNull(errors);
    }

    @Test
    void verifyRequestSuccessForValidateIhtEstate() throws IOException {
        //adjust with app yml iht-estate.switch-date
        String json = utils.getJsonFromFile("solicitorPayloadIhtEstateValidate.json");
        json = json.replaceAll("<DOD-DATE>", "2022-01-01");
        json = json.replaceAll("<NET_QUALIFYING_VALUE>", "10000000");
        validatePostSuccessForPayload(json, SOLS_VALIDATE_IHT_ESTATE,
            utils.getHeadersWithSolicitorUser());
    }

    @Test
    void verifySuccessForDefaultNextStepsWithProbateSingleExecutorPayload() throws IOException {
        final ResponseBody body = validatePostSuccessForPayload(
            utils.getJsonFromFile("solicitorPDFPayloadProbateSingleExecutor.json"),
            DEFAULT_SOLS_NEXT_STEP, utils.getHeadersWithCaseworkerUser());
        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String willExist = jsonPath.get("data.willExists");
        final String errors = jsonPath.get("data.errors");

        assertEquals(willExist, "Yes");
        assertNull(errors);
    }

    @Test
    void verifySuccessForDefaultNextStepsWithProbateMultipleExecutorPayload() throws IOException {
        ResponseBody response = validatePostSuccess("solicitorPDFPayloadProbateMultipleExecutors.json",
            DEFAULT_SOLS_NEXT_STEP);

        JsonPath jsonPath = JsonPath.from(response.asString());
        String willExist = jsonPath.get("data.willExists");
        String errors = jsonPath.get("data.errors");

        assertEquals(willExist, "Yes");
        assertNull(errors);
    }

    @Test
    void verifyCaseHandedOffToLegacySite() throws IOException {
        final ResponseBody body = validatePostSuccess("success.caseHandedOffToLegacySite.json",
            VALIDATE_CASE_AMEND_URL);
        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String caseHasBeenHandedOffToLegacySite = jsonPath.get("data.caseHandedOffToLegacySite");
        assertEquals(caseHasBeenHandedOffToLegacySite, "Yes");
    }

    @Test
    void verifyCaseNotHandedToLegacySite() throws IOException {
        final ResponseBody body = validatePostSuccess("success.caseNotHandedOffToLegacySite.json",
            VALIDATE_CASE_AMEND_URL);
        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String caseHasBeenHandedOffToLegacySite = jsonPath.get("data.caseHandedOffToLegacySite");
        assertEquals(caseHasBeenHandedOffToLegacySite, "No");
    }

    @Test
    void shouldTransformCasePADeceasedAliasOneField() throws IOException {
        final String response = transformCase("personalPayloadNotifications.json", TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        final String alias = jsonPath.get("data.solsDeceasedAliasNamesList[0].value.SolsAliasname");

        assertEquals("Giacomo Terrel", alias);
    }

    @Test
    void shouldTransformCaseWithScannedDocuments() throws IOException {
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
    void shouldTransformCaseWithIht217Attributes() throws IOException {
        final String response = transformCase("success.iht217Saved.json", TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        final String iht217 = jsonPath.get("data.iht217");

        assertEquals("Yes", iht217);
    }

    @Test
    void shouldTransformCaseWithCitizenAttributes() throws IOException {
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
    void shouldTransformSolicitorInfoAttributes() throws IOException {
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
    void shouldTransformCaseWithTrustCorpAttributes() throws IOException {
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
    void shouldTransformCaseWithPartnerAttributes() throws IOException {
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
    void shouldTransformCaseWithFurtherEvidenceForApplication() throws IOException {
        final String response = transformCase("success.solicitorFurtherEvidenceForApplication.json",
            TRANSFORM_URL);

        final JsonPath jsonPath = JsonPath.from(response);
        assertEquals("Further Evidence", jsonPath.get("data.furtherEvidenceForApplication"));
    }

    @Test
    void shouldTransformCaseWithApplicantOrganisationPolicy() throws IOException {
        String json = utils.getJsonFromFile("success.solsCreateShareACase.json");
        final ResponseBody body = validatePostSuccessForPayload(json, SOLS_CREATED_URL,
                utils.getHeadersWithSolicitorUser());

        final String response = body.asString();
        final JsonPath jsonPath = JsonPath.from(response);
        assertNotNull(jsonPath.get("data.applicantOrganisationPolicy"));
        assertNotNull(jsonPath.get("data.applicantOrganisationPolicy.Organisation.OrganisationID"));
        assertEquals("Probate Test Org",
            jsonPath.get("data.applicantOrganisationPolicy.Organisation.OrganisationName"));
        assertEquals("[APPLICANTSOLICITOR]",
            jsonPath.get("data.applicantOrganisationPolicy.OrgPolicyCaseAssignedRole"));
    }

    @Test
    void shouldReturnSuccessReactivateCase() throws IOException {
        validatePostSuccessForPayload(utils.getJsonFromFile("success.paperForm.json"),
            REACTIVATE_CASE, utils.getHeadersWithUserId());
    }

    private String transformCase(String jsonFileName, String path) throws IOException {
        final Response jsonResponse = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(jsonFileName))
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
                                                                   Integer statusCode) throws IOException {
        validatePostFailure(jsonFileName, errorMessage, statusCode, VALIDATE_URL);
        validatePostFailure(jsonFileName, errorMessage, statusCode, VALIDATE_CASE_AMEND_URL);
    }

    private void validatePostFailureForSolicitorAddDeceasedEstateDetails(String jsonFileName, String errorMessage,
                                                                         Integer statusCode) throws IOException {
        validatePostFailure(jsonFileName, errorMessage, statusCode, VALIDATE_URL);
    }

    private void validatePostFailureForSolicitorExecutorDetails(String jsonFileName, String errorMessage)
        throws IOException {
        validatePostFailure(jsonFileName, errorMessage, 200, VALIDATE_URL);
    }

    private void validatePostFailureForSolicitorPostcode(String jsonFileName, String errorMessage)
            throws IOException {
        validatePostFailure(jsonFileName, errorMessage, 200, CASE_CREATE_VALIDATE_URL);
    }

    private void validatePostFailureForCaseAmend(String jsonFileName, String errorMessage) throws IOException {
        validatePostFailure(jsonFileName, errorMessage, 200, VALIDATE_CASE_AMEND_URL);
    }

    private void validatePostFailure(String jsonFileName, String errorMessage, Integer statusCode, String url)
        throws IOException {
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
