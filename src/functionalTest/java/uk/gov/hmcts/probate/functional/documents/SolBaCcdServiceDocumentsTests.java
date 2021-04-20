package uk.gov.hmcts.probate.functional.documents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(SpringIntegrationSerenityRunner.class)
public class SolBaCcdServiceDocumentsTests extends IntegrationTestBase {

    // Grant fields
    private static final String SOLICITOR_INFO1 =
        "Extracted by Solicitor Firm Name (Ref: 1231-3984-3949-0300) SolAddLn1, SolAddLn2, SolAddLn3, ";
    private static final String SOLICITOR_INFO2 = "SolAddPT, SolAddCounty, KT10 0LA, SolAddCo";
    private static final String SOLICITOR_INFO3 =
        "Extracted by Solicitor Firm Name (Ref: 1231-3984-3949-0300) SolAddLn1, SolAddLn3, SolAddPT, KT10 0LA, "
            + "SolAddCo";
    private static final String REGISTRY_ADDRESS =
        "High Court of Justice England and Wales Birmingham District Probate Registry The Priory Courts33 Bull "
            + "StreetBirminghamB4 6DU0300 303 0648";
    private static final String LONDON_REGISTRY_ADDRESS =
        "High Court of Justice England and WalesPrincipal Registry of the Family DivisionFirst Avenue House42-49 High"
            + " HolbornLondonWC1V 6NP0300 303 0648 ";
    private static final String CTSC_REGISTRY_ADDRESS =
        "High Court of Justice England and Wales Principal Registry of the Family DivisionHMCTS ProbatePO Box "
            + "12625HarlowCM20 9QE0300 303 0648";
    private static final String PA = "Extracted personally";
    private static final String PRIMARY_APPLICANT = "Executor name 1 Executor Last Name 1";
    private static final String WILL_MESSAGE = "with a codicil";
    private static final String ADMIN_MESSAGE = "admin clause limitation message";
    private static final String LIMITATION_MESSAGE = "limitation message";
    private static final String EXECUTOR_LIMITATION_MESSAGE = "executor limitation message";
    private static final String POWER_RESERVED = "Power reserved to other Executors";
    private static final String POWER_RESERVED_SINGLE = "Power reserved to another Executor";
    private static final String TITLE = "Captain";
    private static final String HONOURS = "OBE";
    private static final String ADD_EXEC_ONE = "and Add Ex First Name 1 Add Ex Last Name 1";
    private static final String ADD_EXEC_ONE_PRIMARY_APPLICANT = "Add Ex First Name 1 Add Ex Last Name 1";
    private static final String ADD_EXEC_TWO = "and Add Ex First Name 2 Add Ex Last Name 2";
    private static final String DOD = "1st January 2000";
    private static final String IHT_NET = "8,000.00";
    private static final String IHT_GROSS = "10,000.00";
    private static final String IHT_NET_PENCE = "8,123.50";
    private static final String IHT_GROSS_PENCE = "10,234.92";
    private static final String GOP = "Grant of Probate";
    private static final String DIED_ON = "Died on";
    private static final String DIED_ON_OR_SINCE = "Died on or since";
    private static final String DIED_ON_OR_ABOUT = "Died on or about";
    private static final String DIED_ON_OR_BEFORE = "Died on or before";
    private static final String PRESUMED_DIED_ON = "Presumed died on";
    private static final String UK = "United Kingdom";
    private static final String ENGLAND_AND_WALES = "England and Wales";
    private static final String SPAIN = "Spain";
    private static final String CASE_REFERENCE = "Case Reference: 1528365719153338";
    private static final String DECEASED_DETAILS = "Deceased First Name Deceased Last Name of 1 2";
    private static final String DECEASED_DOD = "Died on 1st January 2000";
    private static final String WATERMARK = "DRAFT COPY - NOT FOR CIRCULATION";
    private static final String POSTCODE = "CM20 9QE";
    // Legal statement fields
    private static final String DECLARATION_CIVIL_WORDING =
        "proceedings for contempt of court may be brought against the undersigned if it is found that the evidence "
            + "provided is deliberately untruthful or dishonest, as well as revocation of the grant";
    private static final String DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC =
        "criminal proceedings for fraud may be brought against me if I am found to have been deliberately untruthful "
            + "or dishonest";
    private static final String LEGAL_STATEMENT = "Legal statement";
    private static final String AUTHORISED_SOLICITOR =
        "They have authorised Firm Name to sign a statement of truth on their behalf.";
    private static final String LEGAL_STATEMENT_DIED_ON = "died on";
    private static final String LEGAL_STATEMENT_GOP = "grant of probate";
    private static final String PRIMARY_APPLICANT_STATEMENT =
        "I, Firstname Lastname of 123 Street, Town, Postcode, make the following statement";
    private static final String APPLYING_EXECUTOR_STATEMENT =
            "We, FirstName LastName of 123 Street, Town, Postcode, UK and Exfn3 Exln3 of addressline 1, "
                    + "addressline 2, addressline 3, posttown, county, postcode, country and FirstName3 LastName3"
                    + " of addressline 1, addressline 2, addressline 3, posttown, county, postcode, country";
    private static final String LEGAL_STATEMENT_INTESTATE = "intestate";
    private static final String LEGAL_STATEMENT_ADMON_WILL =
        "Administrators Applying for Letters of Administration (with will annexed)";
    private static final String HMCTS_VALUE = "HMCTS";
    private static final String GENERATE_GRANT = "/document/generate-grant";
    private static final String GENERATE_GRANT_DRAFT = "/document/generate-grant-draft";
    private static final String GENERATE_DEPOSIT_RECEIPT = "/document/generate-deposit-receipt";
    private static final String GENERATE_GRANT_DRAFT_REISSUE = "/document/generate-grant-draft-reissue";

    private static final String GENERATE_LEGAL_STATEMENT = "/document/generate-sot";
    private static final String VALIDATE_PROBATE_URL = "/case/sols-validate-probate";


    private static final String ASSEMBLE_LETTER = "/document/assembleLetter";
    private static final String DEFAULT_PRINT_VALUES = "/document/default-reprint-values";
    private static final String GENERATE_LETTER = "/document/generateLetter";
    private static final String PREVIEW_LETTER = "/document/previewLetter";
    private static final String RE_PRINT = "/document/reprint";

    private static final String DEFAULT_SOLS_PAYLOAD = "solicitorPayloadNotifications.json";
    private static final String DEFAULT_SOLS_PDF_PROBATE_PAYLOAD = "solicitorPDFPayloadProbateSingleExecutor.json";
    private static final String MULTIPLE_EXEC_SOLS_PDF_PROBATE_PAYLOAD =
            "solicitorPDFPayloadProbateMultipleExecutors.json";
    private static final String EMPTY_REQUEST = "emptyRequest.json";

    private static final String DEFAULT_SOLS_PDF_INTESTACY_PAYLOAD = "solicitorPDFPayloadIntestacy.json";
    private static final String DEFAULT_SOLS_PDF_ADMON_PAYLOAD = "solicitorPDFPayloadAdmonWill.json";
    private static final String DEFAULT_PA_PAYLOAD = "personalPayloadNotifications.json";
    private static final String DEFAULT_WILL_PAYLOAD = "willLodgementPayload.json";
    private static final String DEFAULT_REISSUE_PAYLOAD = "personalPayloadReissueDuplicate.json";
    private static final String DEFAULT_ADMON_CARDIFF_PAYLOAD = "solicitorPayloadNotificationsAdmonWillCardiff.json";
    private static final String DEFAULT_INTESTACY_CARDIFF_PAYLOAD =
        "solicitorPayloadNotificationsIntestacyCardiff.json";
    private static final String DEFAULT_GOP_CARDIFF_PAYLOAD = "solicitorPayloadNotificationsGopCardiff.json";
    private static final String DEFAULT_WILL_NO_DOCS_PAYLOAD = "willLodgementPayloadNoDocs.json";
    private static final String OXFORD_GOP_PAYLOAD = "solicitorPayloadNotificationsGopOxford.json";
    private static final String MANCHESTER_GOP_PAYLOAD = "solicitorPayloadNotificationsGopManchester.json";
    private static final String LEEDS_GOP_PAYLOAD = "solicitorPayloadNotificationsGopLeeds.json";
    private static final String LIVERPOOL_GOP_PAYLOAD = "solicitorPayloadNotificationsGopLiverpool.json";
    private static final String BRIGHTON_GOP_PAYLOAD = "solicitorPayloadNotificationsGopBrighton.json";
    private static final String LONDON_GOP_PAYLOAD = "solicitorPayloadNotificationsGopLondon.json";
    private static final String NEWCASTLE_GOP_PAYLOAD = "solicitorPayloadNotificationsGopNewcastle.json";
    private static final String WINCHESTER_GOP_PAYLOAD = "solicitorPayloadNotificationsGopWinchester.json";
    private static final String BRISTOL_GOP_PAYLOAD = "solicitorPayloadNotificationsGopBristol.json";
    private static final String TRUST_CORPS_GOP_PAYLOAD = "solicitorPayloadTrustCorpsTransformed.json";
    private static final String GENERATE_LETTER_PAYLOAD = "/document/generateLetter.json";
    private static final String VALIDATE_URL = "/nextsteps/validate";


    @Test
    public void verifySolicitorGenerateGrantShouldReturnOkResponseCode() {
        validatePostSuccess(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);
    }

    @Test
    public void verifySolicitorGenerateGrantDraftShouldReturnOkResponseCode() {
        validatePostSuccess(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);
    }

    @Test
    public void verifySolicitorGenerateIntestacyGrantShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotificationsIntestacy.json", GENERATE_GRANT);
    }

    @Test
    public void verifySolicitorGenerateIntestacyGrantDraftShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotificationsIntestacy.json", GENERATE_GRANT_DRAFT);
    }


    @Test
    public void verifySolicitorGenerateAdmonWillGrantShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotificationsAdmonWill.json", GENERATE_GRANT);
    }

    @Test
    public void verifySolicitorGenerateAdmonWillGrantDraftShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotificationsAdmonWill.json", GENERATE_GRANT_DRAFT);
    }


    @Test
    public void verifyPersonalApplicantGenerateGrantShouldReturnOkResponseCode() {
        validatePostSuccess(DEFAULT_PA_PAYLOAD, GENERATE_GRANT);
    }

    @Test
    public void verifyPersonalApplicantGenerateGrantDraftShouldReturnOkResponseCode() {
        validatePostSuccess(DEFAULT_PA_PAYLOAD, GENERATE_GRANT_DRAFT);
    }

    @Test
    public void verifyGenerateGrantDraftReissueShouldReturnOkResponseCode() {
        validatePostSuccess(DEFAULT_REISSUE_PAYLOAD, GENERATE_GRANT_DRAFT_REISSUE);
    }

    @Test
    public void verifyTrustCorpsShouldReturnOkResponseCode() {
        validatePostSuccess(TRUST_CORPS_GOP_PAYLOAD, GENERATE_GRANT);
    }

    private String generateDocument(String jsonFileName, String path) {

        Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(path).andReturn();

        JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());
        String documentUrl = jsonPath.get("data.probateDocumentsGenerated[0].value.DocumentLink.document_binary_url");
        String response = utils.downloadPdfAndParseToString(documentUrl);
        response = response.replace("\n", "").replace("\r", "");
        return response;
    }

    private String generateNonProbateDocument(String jsonFileName, String path) {

        Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(path).andReturn();

        JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());
        String documentUrl = jsonPath.get("data.documentsGenerated[0].value.DocumentLink.document_binary_url");
        String response = utils.downloadPdfAndParseToString(documentUrl);
        response = response.replace("\n", "").replace("\r", "");
        return response;
    }

    private String generatePdfDocument(String jsonFileName, String path) {

        return generatePdfDocumentFromPayload(utils.getJsonFromFile(jsonFileName), path);
    }

    private String generatePdfDocumentFromPayload(String payload, String path) {

        Response jsonResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(payload)
                .when().post(path).andReturn();

        JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());

        String documentUrl =
                jsonPath.get("data.probateSotDocumentsGenerated[0].value.DocumentLink.document_binary_url");

        String response = utils.downloadPdfAndParseToString(documentUrl);
        response = response.replace("\n", "").replace("\r", "");
        return response;
    }

    @Test
    public void verifySuccessForGetAdmonWillGrantForCardiff() {
        CaseData caseData = CaseData.builder().build();

        String response = generateDocument(DEFAULT_ADMON_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("admonWillGrantForCardiffResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifySuccessForGetAdmonWillGrantDraftForCardiff() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(DEFAULT_ADMON_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

        String expectedText = utils.getJsonFromFile("admonWillGrantDraftForCardiffResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetIntestacyGrantForCardiff() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(DEFAULT_INTESTACY_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("intestacyGrantForCardiffResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetIntestacyGrantDraftForCardiff() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(DEFAULT_INTESTACY_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

        String expectedText = utils.getJsonFromFile("intestacyGrantDraftForCardiffResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetGopGrantForCardiff() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(DEFAULT_GOP_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("gopGrantForCardiffResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetGopGrantDraftForCardiff() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(DEFAULT_GOP_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

        String expectedText = utils.getJsonFromFile("gopGrantDraftForCardiffResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForWillLodgementForCardiff() {
        CaseData caseData = CaseData.builder().build();
        String response = generateNonProbateDocument(DEFAULT_WILL_NO_DOCS_PAYLOAD, GENERATE_DEPOSIT_RECEIPT);

        String expectedText = utils.getJsonFromFile("willLodgementDepositReceiptResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("19th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForOxfordGopGenerateGrant() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(OXFORD_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("oxfordGopGenerateGrantResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("3rd December 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForManchesterGopGenerateGrant() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(MANCHESTER_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("manchesterGopGenerateGrantResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("3rd December 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLeedsGopGenerateGrant() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(LEEDS_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("leedsGopGenerateGrantResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("3rd December 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLiverpoolGopGenerateGrant() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(LIVERPOOL_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("liverpoolGopGenerateGrantResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("3rd December 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForBrightonGopGenerateGrant() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(BRIGHTON_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("brightonGopGenerateGrantResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("3rd December 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLondonGopGenerateGrant() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(LONDON_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("londonGopGenerateGrantResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("3rd December 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForNewcastleGopGenerateGrant() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(NEWCASTLE_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("newcastleGopGenerateGrantResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("3rd December 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForWinchesterGopGenerateGrant() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(WINCHESTER_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("winchesterGopGenerateGrantResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("3rd December 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForBristolGopGenerateGrant() {
        CaseData caseData = CaseData.builder().build();
        String response = generateDocument(BRISTOL_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = utils.getJsonFromFile("bristolGopGenerateGrantResponse.txt");
        expectedText = expectedText.replace("\n", "").replace("\r", "");
        expectedText = expectedText.replaceAll("3rd December 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetPdfLegalStatementProbateWithSingleExecutorSols() {
        String response = generatePdfDocument(DEFAULT_SOLS_PDF_PROBATE_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(!response.contains(AUTHORISED_SOLICITOR));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(LEGAL_STATEMENT_GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT_STATEMENT));

        assertTrue(!response.contains(DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC));
    }

    @Test
    public void verifySuccessForGetPdfLegalStatementProbateWithMultipleExecutorSols() throws JsonProcessingException {

        ResponseBody body = validatePostSuccess(MULTIPLE_EXEC_SOLS_PDF_PROBATE_PAYLOAD,
                VALIDATE_PROBATE_URL);

        JsonPath jsonPath = JsonPath.from(body.asString());
        Map<String, String> hm = jsonPath.get("data");
        hm.remove("taskList");

        final ObjectMapper objectMapper = new ObjectMapper();

        String transformedData = objectMapper.writeValueAsString(hm);

        String newRequest = utils.getJsonFromFile(EMPTY_REQUEST);
        newRequest = newRequest.replaceFirst(Pattern.quote("\"state\": \"CaseCreated\""),
                "\"state\": \"SolAppUpdated\"");
        newRequest = newRequest.replaceFirst(Pattern.quote("\"case_data\": {}"), "\"case_data\": "
            + transformedData.substring(0, transformedData.length() - 1) + ", \"solsSOTNeedToUpdate\": \"No\"}");

        body = validatePostSuccessForPayload(newRequest, VALIDATE_URL);

        jsonPath = JsonPath.from(body.asString());
        hm = jsonPath.get("data");
        hm.remove("taskList");
        transformedData = objectMapper.writeValueAsString(hm);
        newRequest = utils.getJsonFromFile(EMPTY_REQUEST);
        newRequest = newRequest.replaceFirst(Pattern.quote("\"state\": \"CaseCreated\""),
                "\"state\": \"SolAppUpdated\"");
        newRequest = newRequest.replaceFirst(Pattern.quote("\"case_data\": {}"), "\"case_data\": "
                + transformedData);


        String response = generatePdfDocumentFromPayload(newRequest, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(!response.contains(AUTHORISED_SOLICITOR));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(LEGAL_STATEMENT_GOP));
        assertTrue(response.contains(APPLYING_EXECUTOR_STATEMENT));

        assertTrue(!response.contains(DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC));
    }

    @Test
    public void verifySuccessForGetPdfLegalStatementIntestacyWithSingleExecutorSols() {
        String response = generatePdfDocument(DEFAULT_SOLS_PDF_INTESTACY_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(!response.contains(AUTHORISED_SOLICITOR));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(PRIMARY_APPLICANT_STATEMENT));
        assertTrue(response.contains(LEGAL_STATEMENT_INTESTATE));

        assertTrue(!response.contains(DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC));
    }

    @Test
    public void verifySuccessForGetPdfLegalStatementAdmonWillSols() {
        String response = generatePdfDocument(DEFAULT_SOLS_PDF_ADMON_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(!response.contains(AUTHORISED_SOLICITOR));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(LEGAL_STATEMENT_ADMON_WILL));

        assertTrue(!response.contains(DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithSingleExecutorSols() {
        String response = generateDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(DIED_ON_OR_SINCE));

        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithSingleExecutorPA() {
        String response = generateDocument(DEFAULT_PA_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PA));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(PRESUMED_DIED_ON));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantWithMultipleExecutorsSOls() {
        String response = generateDocument("solicitorPayloadNotificationsMultipleExecutors.json", GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON_OR_ABOUT));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantWithPowerReservedMultipleSOls() {
        String response = generateDocument("solicitorPayloadNotificationsPowerReservedMultiple.json", GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithPowerReservedSingleSOls() {
        String response = generateDocument("solicitorPayloadNotificationsPowerReserved.json", GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantWithGrantInfoSOls() {
        String response = generateDocument("solicitorPayloadNotificationsGrantInfo.json", GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(WILL_MESSAGE));
        assertTrue(response.contains(ADMIN_MESSAGE));
        assertTrue(response.contains(LIMITATION_MESSAGE));
        assertTrue(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(TITLE));
        assertTrue(response.contains(HONOURS));
        assertTrue(response.contains(DIED_ON));

        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(PA));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithSingleExecutorSols() {
        String response = generateDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(DIED_ON_OR_SINCE));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithSingleExecutorPA() {
        String response = generateDocument(DEFAULT_PA_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PA));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(PRESUMED_DIED_ON));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithMultipleExecutorsSOls() {
        String response = generateDocument("solicitorPayloadNotificationsMultipleExecutors.json", GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON_OR_ABOUT));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithPowerReservedMultipleSOls() {
        String response =
            generateDocument("solicitorPayloadNotificationsPowerReservedMultiple.json", GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithPowerReservedSingleSOls() {
        String response = generateDocument("solicitorPayloadNotificationsPowerReserved.json", GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithGrantInfoSOls() {
        String response = generateDocument("solicitorPayloadNotificationsGrantInfo.json", GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(WILL_MESSAGE));
        assertTrue(response.contains(ADMIN_MESSAGE));
        assertTrue(response.contains(LIMITATION_MESSAGE));
        assertTrue(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(TITLE));
        assertTrue(response.contains(HONOURS));
        assertTrue(response.contains(DIED_ON));

        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(PA));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftDateFormat() {
        String response = generateDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(DOD));
        assertTrue(response.contains(GOP));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDateFormat() {
        String response = generateDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(DOD));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftMoneyFormat() {
        String response = generateDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(IHT_GROSS));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(IHT_NET));
        assertTrue(response.contains(GOP));

    }

    @Test
    public void verifySuccessForGetDigitalGrantMoneyFormat() {
        String response = generateDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(IHT_GROSS));
        assertTrue(response.contains(IHT_NET));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantMoneyFormatWithPence() {
        String response = generateDocument("solicitorPayloadNotificationsIHTCurrencyFormat.json", GENERATE_GRANT);

        assertTrue(response.contains(IHT_GROSS_PENCE));
        assertTrue(response.contains(IHT_NET_PENCE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftMoneyFormatWithPence() {
        String response = generateDocument("solicitorPayloadNotificationsIHTCurrencyFormat.json", GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(IHT_GROSS_PENCE));
        assertTrue(response.contains(IHT_NET_PENCE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantNotApplying() {
        String response =
            generateDocument("solicitorPayloadNotificationsMultipleExsPANotApplying.json", GENERATE_GRANT_DRAFT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));
        assertTrue(!response.contains(ADD_EXEC_ONE));

        assertTrue(response.contains(DIED_ON_OR_BEFORE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantPrimaryApplicantNotApplying() {
        String response =
            generateDocument("solicitorPayloadNotificationsMultipleExsPANotApplying.json", GENERATE_GRANT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));
        assertTrue(!response.contains(ADD_EXEC_ONE));

        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantNotApplyingPowerReserved() {
        String response = generateDocument("solicitorPayloadNotificationsMultipleExsPANotApplyingPowerReserved.json",
            GENERATE_GRANT_DRAFT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));
        assertTrue(!response.contains(ADD_EXEC_ONE));
        assertTrue(!response.contains(POWER_RESERVED));

        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantPrimaryApplicantNotApplyingPowerReserved() {
        String response =
            generateDocument("solicitorPayloadNotificationsMultipleExsPANotApplyingPowerReserved.json", GENERATE_GRANT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));
        assertTrue(!response.contains(ADD_EXEC_ONE));
        assertTrue(!response.contains(POWER_RESERVED));

        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantNotApplyingPowerReservedMultiple() {
        String response = generateDocument("solicitorPayloadNotificationsPANotApplyingPowerReservedMultiple.json",
            GENERATE_GRANT_DRAFT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));
        assertTrue(!response.contains(ADD_EXEC_ONE));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));

        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(LONDON_REGISTRY_ADDRESS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantPrimaryApplicantNotApplyingPowerReservedMultiple() {
        String response =
            generateDocument("solicitorPayloadNotificationsPANotApplyingPowerReservedMultiple.json", GENERATE_GRANT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));
        assertTrue(!response.contains(ADD_EXEC_ONE));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));

        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(LONDON_REGISTRY_ADDRESS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantPartialSolsAddress() {
        String response = generateDocument("solicitorPayloadNotificationsPartialAddress.json", GENERATE_GRANT);

        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(SOLICITOR_INFO3));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(DIED_ON_OR_SINCE));

        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDomiciledUK() {
        String response = generateDocument("solicitorPayloadNotificationsPartialAddress.json", GENERATE_GRANT);

        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(SOLICITOR_INFO3));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(DIED_ON_OR_SINCE));
        assertTrue(response.contains(UK));
        assertTrue(response.contains(ENGLAND_AND_WALES));

        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }


    @Test
    public void verifySuccessForGetDigitalGrantDomiciledForeignDomicile() {
        String response = generateDocument("solicitorPayloadNotificationsForeignDomicile.json", GENERATE_GRANT);

        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(SOLICITOR_INFO3));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(DIED_ON_OR_SINCE));
        assertTrue(response.contains(ENGLAND_AND_WALES));
        assertTrue(response.contains(SPAIN));

        assertTrue(!response.contains(UK));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifyWillLodgementDepositReceiptShouldReturnOkResponseCode() {
        validatePostSuccess(DEFAULT_WILL_PAYLOAD, GENERATE_DEPOSIT_RECEIPT);
    }

    //Commented out due to Docmosis not allowing screen readers as images overlay all text
    @Test
    public void verifySuccessForDigitalGrantDraftReissueForDuplicateNotation() {
        String response = generateDocument(DEFAULT_REISSUE_PAYLOAD, GENERATE_GRANT_DRAFT_REISSUE);
        assertTrue(response.contains(ENGLAND_AND_WALES));
        assertTrue(response.contains(CASE_REFERENCE));
        assertTrue(response.contains(DECEASED_DETAILS));
        assertTrue(response.contains(DECEASED_DOD));
        assertTrue(response.contains(HMCTS_VALUE));
        assertTrue(response.contains(POSTCODE));
    }

    @Test
    public void verifyAssembleLetterShouldReturnOkResponseCode() {
        ResponseBody response = validatePostSuccess("/document/assembleLetterPayLoad.json", ASSEMBLE_LETTER);
        JsonPath jsonPath = JsonPath.from(response.asString());
        List paragraphDetails = jsonPath.get("data.paragraphDetails");
        String templateName = jsonPath.get("data.paragraphDetails[1].value.templateName");
        response.prettyPrint();

        assertThat(paragraphDetails.size(), is(3));
        assertThat(templateName, is(equalTo(ParagraphCode.MissInfoWill.getTemplateName())));
    }

    @Test
    public void verifyAssembleLetterShouldReturnIHTReferenceNumber() {
        String jsonAsString = getJsonFromFile("/document/assembleLetterTransform.json");
        Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(jsonAsString)
            .when().post(ASSEMBLE_LETTER)
            .andReturn();

        JsonPath jsonPath = JsonPath.from(response.asString());
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data.ihtReferenceNumber"), is(equalTo("ONLINE-123434")));
    }

    @Test
    public void verifyDefaultRePrintValuesReturnsOkResponseCode() {
        String jsonAsString = getJsonFromFile("/document/rePrintDefaultGrantOfProbate.json");

        ResponseBody response =
            validatePostSuccess("/document/rePrintDefaultGrantOfProbate.json", DEFAULT_PRINT_VALUES);

        response.prettyPrint();
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertThat(jsonPath.get("data.reprintDocument.list_items[0].label"), is(equalTo("Grant")));
        assertThat(jsonPath.get("data.reprintDocument.list_items[0].code"), is(equalTo("WelshGrantFileName")));
    }

    @Test
    public void verifyDefaultRePrintValuesReturnsIhtReferenceNumber() {
        String jsonAsString = getJsonFromFile("/document/rePrintDefaultGrantOfProbate.json");
        jsonAsString = jsonAsString.replaceFirst("\"paperForm\": \"Yes\",", "\"paperForm\": \"No\",");

        Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(jsonAsString)
            .when().post(DEFAULT_PRINT_VALUES)
            .andReturn();
        assertThat(response.getStatusCode(), is(equalTo(200)));
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertThat(jsonPath.get("data.ihtReferenceNumber"), is(equalTo("ONLINE-123434")));
    }

    @Test
    public void verifySolicitorGenerateLetterReturnOkResponseCode() {
        String response = generateDocument(GENERATE_LETTER_PAYLOAD, GENERATE_LETTER);
        assertThat(getJsonFromFile("/document/assembledLetter.txt"), is(equalTo(response)));
    }

    @Test
    public void verifySolicitorGenerateLetterReturnsIHTReferenceNumber() {
        ResponseBody responseBody =
            validatePostSuccess("/document/generateLetterDefaultLocation.json", GENERATE_LETTER);
        responseBody.prettyPrint();
        JsonPath jsonPath = JsonPath.from(responseBody.asString());
        assertThat(jsonPath.get("data.ihtFormId"), is(equalTo("IHT205")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
    }

    @Test
    public void verifySolicitorPreviewLetterReturnsCorrectResponse() {
        Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("/document/generateLetter.json"))
            .when().post(PREVIEW_LETTER).andReturn();
        jsonResponse.prettyPrint();
        JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());
        String documentUrl = jsonPath.get("data.previewLink.document_binary_url");

        String response = utils.downloadPdfAndParseToString(documentUrl);
        response = response.replace("\n", "").replace("\r", "");

        assertThat(response, is(equalTo(getJsonFromFile("/document/previewLetterResponse.txt"))));
    }

    @Test
    public void verifySolicitorPreviewLetterReturnsIHTReferenceNumber() {
        ResponseBody responseBody = validatePostSuccess("/document/generateLetterDefaultLocation.json", PREVIEW_LETTER);
        responseBody.prettyPrint();
        JsonPath jsonPath = JsonPath.from(responseBody.asString());
        assertThat(jsonPath.get("data.ihtFormId"), is(equalTo("IHT205")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
    }

    @Test
    public void verifySolicitorRePrintReturnBadResponseCode() {
        Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId("serviceToken", "userId"))
            .body(getJsonFromFile("/document/rePrint.json"))
            .when().post(RE_PRINT)
            .andReturn();
        assertThat(response.statusCode(), is(equalTo(403)));
        assertTrue(response.getBody().asString().contains("Forbidden"));

    }

    @Test
    public void verifySoTDomiciledInEnglandAndWales() {
        String response = generatePdfDocument(DEFAULT_SOLS_PDF_ADMON_PAYLOAD, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Main Applicant of Test, Test, A1 2BC, UK make the following" +
                " statement:The person who diedDe Ceased, of Test, Test, Test, A1 2BC, was born on" +
                " 23/01/1998 and died on 23/01/2020, domiciled in England and Wales."));
    }

    @Test
    public void verifySoTIndividualExecutorPowerReserved() {
        String response = generatePdfDocument("solicitorExecutorsNotApplyingReasons.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("executor4_name, another executor named in the will," +
                " is not making this application but reserves power to do so at a later date."));
    }

    @Test
    public void verifySoTIndividualExecutorRenunciation() {
        String response = generatePdfDocument("solicitorExecutorsNotApplyingReasons.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("executor3_name, another executor named in the will, has renounced probate and letters " +
                "of administration with will annexed"));
    }

    @Test
    public void verifySoTExecutorDiedBeforeAndAfterDeceased() {
        String response = generatePdfDocument("solicitorExecutorsNotApplyingReasons.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("executor1_name, another executor named in the will, has died in the lifetime of the deceased."));
        assertTrue(response.contains("executor2_name, another executor named in the will, has survived the deceased and died since."));
    }

    @Test
    public void verifySoTExecutorLacksMentalCapacity() {
        String response = generatePdfDocument("solicitorExecutorsNotApplyingReasons.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("executor5_name, another executor named in the will, lacks capacity to manage their" +
                " affairs under the Mental Capacity Act 2005 and is unable to act as an executor."));
    }

    @Test
    public void verifySoTExecutorPowerReservedAndNoticeDispenseGiven() {
        String response = generatePdfDocument("solicitorPayloadDispenseNotGiven.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Notice of this application has on the 10th October 2010 " +
                "been dispensed with under Rule 27(3) of the Non-Contentious Probate Rules " +
                "1987 to executor1_name to whom power is to be reserved."));
    }

    @Test
    public void verifySoTExecutorConcurrentApplication() {
        String response = generatePdfDocument("solicitorExecutorsNotApplyingReasons.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("We are concurrently applying for notice of this application" +
                " to be dispensed with under Rule 27(3) of the Non-Contentious Probate Rules" +
                " 1987 to executor6_name to whom power is to be reserved."));
    }

    @Test
    public void verifySoTFirstParagraphPersonWhoDiedForClearingOne() {
        String response = generatePdfDocument("solicitorPayloadSuccessorFirmLegalStatement.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The person who diedDeceased Name, of Chapter Of Wells, Wells Cathedral, Wells, Somerset," + //space between diedName
                " BA5 2PA, United Kingdom was born on 12/01/2020 and died on 14/01/2020, " +
                "domiciled in England and Wales. The will appoints an executor."));
    }

    @Test
    public void verifySoTFirstParagraphPersonWhoDiedForClearingTwo() {
        String response = generatePdfDocument("solicitorPayloadPartnersInFirm.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The person who diedDeceased Name, of Chapter Of Wells, Wells Cathedral, Wells, Somerset," + //space between diedName
                " BA5 2PA, United Kingdom was born on 12/01/2020 and died on 14/01/2020, " +
                "domiciled in England and Wales. The will appoints an executor."));
    }


    @Test
    public void verifySecondParagraphFirmSuccessionForClearingThree() {
        String response = generatePdfDocument("solicitorPayloadSoleSuccessorLegalStatement.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor Partner Exec, is a profit-sharing Partners and Stakeholders in the firm Firmname" +
                " will that had succeeded to and carried on the practice of the " +
                "firm Successor firm, at the date of death of the deceased."));

    }

    @Test
    public void verifySoTSecondParagraphFirmSuccessionForClearingFour() {
        String response = generatePdfDocument("solicitorPayloadSolePrin.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor Partner Exec, is a profit-sharing Partners and " +
                "Stakeholders in the firm Successor firm, at the date of death of the deceased."));

    }

    @Test
    public void verifySoTThirdParagraphOthersRenouncingInSuccessorClearingNine() {
        String response = generatePdfDocument("solicitorPayloadSuccessorFirmRenounce.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor Partner Exec, is a profit-sharing Partners and Stakeholders in the firm Successor firm" +
                " that had succeeded to and carried on the practice of the " +
                "firm Firmname will, at the date of death of the deceased. The remaining profit-sharing Partners and " +
                "Stakeholders in the firm Firmname will is renouncing their right to probate."));

    }

    @Test
    public void verifySoTThirdParagraphOthersRenouncingInPartnerFirmClearingTen() {
        String response = generatePdfDocument("solicitorPayloadSuccessorFirmRenounce.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor Partner Exec, is a profit-sharing Partners and Stakeholders in the firm Successor firm" +
                " that had succeeded to and carried on the practice of the " +
                "firm Firmname will, at the date of death of the deceased. The remaining profit-sharing Partners and " +
                "Stakeholders in the firm Firmname will is renouncing their right to probate."));

    }

    @Test
    public void verifySoTFourthParagraphAllSuccessorPartnersRenouncingClearingFive() {
        String response = generatePdfDocument("solicitorPayLoadSuccessorFirmAllRenounce.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Probate Practioner, an executor named in the will, is applying for probate."));

    }

    @Test
    public void verifySoTFourthParagraphAllPartnerFirmsRenouncingClearingSix() {
        String response = generatePdfDocument("solicitorPayloadPartnersAllRenounce.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Probate Practioner, an executor named in the will, is applying for probate."));

    }

    @Test
    public void verifySoTFifthParagraphSeniorJudgeDistrictClearingSeven() {
        String response = generatePdfDocument("solicitorPayloadJudgeSeniorDistrict.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor named in the will has by a resolution, which has been filed with the Senior District Judge or Registry," +
                " in which Exfn1 Exln1 identified by the position they hold and which is still in force, appointed them " +
                "for the purpose of applying for probate of the will or for grants of probate on its behalf."));

    }

    @Test
    public void verifySoTFifthParagraphLodgedApplicationClearingEight() {
        String response = generatePdfDocument("solicitorPayloadLodgeApp.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor named in the will has by a resolution, certified copy" +
                " of which is lodged with this application, in which Exfn1 Exln1 identified by the position" +
                " they hold and which is still in force, appointed them for the purpose of applying for probate" +
                " of the will or for grants of probate on its behalf."));

    }


    @Test
    public void verifySoTFirstParagraphClearancePartnerSucceeded() {
        String response = generatePdfDocument("solicitorPayloadSuccessorFirmLegalStatement.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor Partner Exec, is a profit-sharing Partners and Stakeholders in the firm" +
                " Firmname will that had succeeded to and carried on the practice of the firm Successor firm," +
                " at the date of death of the deceased."));

    }

    @Test
    public void verifySoTSecondParagraphSoleSucceeded() {
        String response = generatePdfDocument("solicitorPayloadSoleSuccessorLegalStatement.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor Partner Exec, is a profit-sharing Partners and Stakeholders in the firm" +
                " Firmname will that had succeeded to and carried on the practice of the firm Successor firm," +
                " at the date of death of the deceased."));

    }

    @Test
    public void verifySoTThirdParagraphPartnerRenounceSucceeded() {
        String response = generatePdfDocument("solicitorPayloadSuccessorFirmRenounce.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor Partner Exec, is a profit-sharing Partners and Stakeholders in the " +
                "firm Successor firm that had succeeded to and carried on the practice of the firm Firmname " +
                "will, at the date of death of the deceased."));

    }

    @Test
    public void verifySoTFourthParagraphPartnerAllRenounceSucceeded() {
        String response = generatePdfDocument("solicitorPayloadSuccessorFirmAllRenounce.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Probate Practioner, an executor named in the will, is applying for probate.")); //not counting the professional user

    }

    @Test
    public void verifySoTFifthParagraphJudgeSeniorDistrict() {
        String response = generatePdfDocument("solicitorPayloadJudgeSeniorDistrict.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor named in the will has by a resolution," +
                " which has been filed with the Senior District Judge or Registry, in which Exfn1 Exln1 identified by" +
                " the position they hold and which is still in force, appointed them for the purpose of applying for probate" +
                " of the will or for grants of probate on its behalf."));
    }

    @Test
    public void verifySoTSixthParagraphTrustCorpResolutionLodged() {
        String response = generatePdfDocument("verifySolPayloadTrustCorpResolutionLodged.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor named in the will has by a resolution, certified copy of which is lodged" +
                " with this application, in which Exfn1 Exln1 identified by the position they hold and which" +
                " is still in force, appointed them for the purpose of applying for probate of " +
                "the will or for grants of probate on its behalf."));
    }
}
