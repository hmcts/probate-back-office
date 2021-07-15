package uk.gov.hmcts.probate.functional.documents;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        "High Court of Justice England and WalesBirmingham District Probate Registry The Priory Courts33 Bull "
            + "StreetBirminghamB4 6DU0300 303 0648";
    private static final String REGISTRY_ADDRESS_HARLOW =
        "High Court of Justice England and WalesPrincipal Registry of the Family DivisionHMCTS ProbatePO Box 12625"
        + "HarlowCM20 9QE";
    private static final String LONDON_REGISTRY_ADDRESS =
        "High Court of Justice England and WalesPrincipal Registry of the Family DivisionFirst Avenue House42-49 High"
            + " HolbornLondonWC1V 6NP0300 303 0648 ";
    private static final String CTSC_REGISTRY_ADDRESS =
        "High Court of Justice England and WalesPrincipal Registry of the Family DivisionHMCTS ProbatePO Box "
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
    private static final String ADD_EXEC_ONE = "Add Ex First Name 1 Add Ex Last Name 1";
    private static final String ADD_EXEC_ONE_PRIMARY_APPLICANT = "Add Ex First Name 1 Add Ex Last Name 1";
    private static final String ADD_EXEC_TWO = "Add Ex First Name 2 Add Ex Last Name 2";
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
    private static final String FURTHER_EVIDENCE = "Further evidence";
    private static final String DOMICILITY_SENTENCE_UK = "The gross value for the estate in the United Kingdom amounts";
    private static final String DOMICILITY_SENTENCE_NON_UK = "The gross value for the estate in England and Wales";
    private static final String FIRM_AUTHORISATION = "They have authorised Firm Name to sign a statement";
    private static final String WILL_NO_CODICILS = "and is named in the will as";
    private static final String SIGNED_DATE = ", signed and dated 1st January 2021";
    private static final String POSTCODE = "CM20 9QE";
    // Legal statement fields
    private static final String DECLARATION_CIVIL_WORDING =
        "proceedings for contempt of court may be brought against the undersigned if it is found that the evidence "
            + "provided is deliberately untruthful or dishonest, as well as revocation of the grant";
    private static final String CODICIL_DATES = " with codicil signed and dated 3rd March 2020, and codicil signed"
        + " and dated 5th March 2020, and codicil signed and dated 6th March 2020";
    private static final String DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC =
        "criminal proceedings for fraud may be brought against me if I am found to have been deliberately untruthful "
            + "or dishonest";
    private static final String LEGAL_STATEMENT = "Legal statement";
    private static final String AUTHORISED_SOLICITOR =
        "They have authorised Firm Name to sign a statement of truth on their behalf.";
    private static final String LEGAL_STATEMENT_DIED_ON = "died on";
    private static final String LEGAL_STATEMENT_GOP = "grant of probate";
    private static final String PRIMARY_APPLICANT_STATEMENT =
        "I, FirstName LastName of 123 Street, Town, Postcode, make the following statement";

    // doesn't lowercase the names before then capitalising first letter
    private static final String PRIMARY_APPLICANT_STATEMENT_OLD_SCHEMA =
            "I, FirstName LastName of 123 Street, Town, Postcode, make the following statement";

    private static final String APPLYING_EXECUTOR_STATEMENT_OLD_SCHEMA =
            "We, FirstName LastName of 123 Street, Town, Postcode, UK and Exfn3 Exln3 of addressline 1, "
                    + "addressline 2, addressline 3, posttown, county, postcode, country and FirstName3 LastName3"
                    + " of addressline 1, addressline 2, addressline 3, posttown, county, postcode, country";

    private static final String LEGAL_STATEMENT_INTESTATE = "intestate";
    private static final String LEGAL_STATEMENT_ADMON_WILL =
        "Administrators Applying for Letters of Administration (with will annexed)";
    private static final String HMCTS_VALUE = "HMCTS";

    private static String MULTI_EXEC_TC_PROB_PRACTITIONER = "Tony Stark";
    private static String MULTI_EXEC_TC_DECEASED = "The Last Will and Testament of  (An official copy of "
        + "which is available from the Court) was John Smith";
    private static String MULTI_EXEC_TC_AMINISTRATION_STATEMENT = "The Administration of 's estate is John Smith"
        + "granted by this court to the following Executors";
    private static String MULTI_EXEC_TC_TRUST_CORP_DETAILS = "and  MyTc 19 Curtis Street Charlton Kings Swindon Glos "
        + "Sn2 2JU United Kingdom";
    private static String NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_MULTI = "Executorsof  MyTc 19 Curtis Street "
        + "Charlton Kings Swindon Glos Sn2 2JU United Kingdom";
    private static String POWER_RESERVED_TO_ONE = "Power reserved to another Executor";
    private static String EXTRANEOUS_CURLY_START_BRACE = "{";
    private static String EXTRANEOUS_CURLY_END_BRACE = "}";
    private static String SINGLE_EXEC_TC_AMINISTRATION_STATEMENT = "The Administration of 's estate is John Smith"
        + "granted by this court to the following Executorof";
    private static String NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_SINGLE = "Executorof  MyTc 19 Curtis Street "
        + "Charlton Kings Swindon Glos Sn2 2JU United Kingdom";

    private static final String GENERATE_GRANT = "/document/generate-grant";
    private static final String GENERATE_GRANT_DRAFT = "/document/generate-grant-draft";
    private static final String GENERATE_DEPOSIT_RECEIPT = "/document/generate-deposit-receipt";
    private static final String GENERATE_GRANT_DRAFT_REISSUE = "/document/generate-grant-draft-reissue";

    private static final String GENERATE_LEGAL_STATEMENT = "/document/generate-sot";

    private static final String ASSEMBLE_LETTER = "/document/assembleLetter";
    private static final String DEFAULT_PRINT_VALUES = "/document/default-reprint-values";
    private static final String GENERATE_LETTER = "/document/generateLetter";
    private static final String PREVIEW_LETTER = "/document/previewLetter";
    private static final String RE_PRINT = "/document/reprint";

    public static final String VALIDATE_PROBATE_URL = "/case/sols-validate-probate";
    public static final String VALIDATE_INTESTACY_URL = "/case/sols-validate-intestacy";
    public static final String VALIDATE_ADMON_URL = "/case/sols-validate-admon";


    private static final String DEFAULT_SOLS_PAYLOAD = "solicitorPayloadNotifications.json";
    private static final String DEFAULT_SOLS_PDF_PROBATE_PAYLOAD = "solicitorPDFPayloadProbateSingleExecutor.json";
    private static final String MULTIPLE_EXEC_SOLS_PDF_PROBATE_PAYLOAD =
            "solicitorPDFPayloadProbateMultipleExecutors.json";
    private static final String EMPTY_REQUEST = "emptyRequest.json";

    private static final String DEFAULT_SOLS_PDF_INTESTACY_PAYLOAD = "solicitorPDFPayloadIntestacy.json";
    private static final String CODICILS_SOLS_PDF_INTESTACY_PAYLOAD = "solicitorPDFIntestacyCodicils.json";
    private static final String DEFAULT_SOLS_PDF_ADMON_PAYLOAD = "solicitorPDFPayloadAdmonWill.json";
    private static final String ADMON_PAYLOAD_WILL_AND_CODICILS_DATES =
        "solicitorPDFPayloadAdmonWillWithWillAndCodicilDates.json";
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
    private static final String NO_DUPE_SOL_EXECUTORS = "solicitorPayloadLegalStatementNoDuplicateExecsCheck.json";
    private static final String SOLE_PRIN = "solicitorSoleFirmPartner.json";
    private static final String SOL_NOT_REPEATED = "solicitorPayloadTrustCorpsNoSolExecRepeat.json";
    private static final String EXEC_WITH_ALIAS = "solicitorExecutorAliasNameLegalStatement.json";
    private static final String PART_ALL_RENOUNCING = "solicitorPartAllRenouncing.json";
    private static final String PART_ALL_SUCC_RENOUNCING = "solicitorPartSuccAllRenouncing.json";
    private static final String PART_ALL_OTHERS_RENOUNCING = "solicitorPartOtherRenouncing.json";
    private static final String SOLE_PRIN_OTHER_PARTNERS = "solicitorSolPartner.json";
    private static final String SOLE_PRIN_OTHER_PARTNERS_SINGLE = "solicitorSolePrinSingleExec.json";
    private static final String CW_PART_SUCC = "caseworkerPartSuccPowerReservedToOthers.json";
    private static final String CW_PART = "caseworkerPartOtherRenouncing.json";
    private static final String MULTI_EXEC_TC_PAYLOAD = "solicitorPayloadMultiExecTcReadyToIssue.json";
    private static final String NOT_NAMED_TC_PAYLOAD = "solicitorPayloadTrustCorpsNotNamed.json";
    private static final String NOT_NAMED_TC_TC_EXEC_PAYLOAD = "solicitorPayloadTrustCorpsNotNamedTcExec.json";
    private static final String NOT_NAMED_TC_POWER_RESERVED_PAYLOAD =
            "solicitorPayloadTrustCorpsNotNamedPowerReserved.json";
    private static final String FRAGMENT_WITH_NO_MULTIPLE_ANDS =
        "Executorsof  MyTc 19 Curtis Street Charlton Kings Swindon Glos Sn2 2JU United Kingdomof and "
        + "Fred FlintstoneApplying 7 Ashley Avenue Burnham-on-Sea Somerset SN15JU United Kingdom"
        + "The application has stated that the gross value";

    private static final String GRANT_DOC_NAME = "probateDocumentsGenerated[0].value.DocumentLink";
    private static final String SOT_DOC_NAME = "probateSotDocumentsGenerated[0].value.DocumentLink";
    private static final String NON_PROBATE_DOC_NAME = "documentsGenerated[0].value.DocumentLink";

    @Before
    public void setUp() {
        initialiseConfig();
    }

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

    private String generateGrantDocument(String jsonFileName, String path) {
        return generateDocument(jsonFileName, path, GRANT_DOC_NAME);
    }

    private String generateDocument(String jsonFileName, String path, String documentName) {
        return generateDocumentFromPayload(utils.getJsonFromFile(jsonFileName), path, documentName);
    }

    private String generateGrantDocumentFromPayload(String payload, String path) {
        return generateDocumentFromPayload(payload, path, GRANT_DOC_NAME);
    }

    private String generateDocumentFromPayload(String payload, String path, String documentName) {

        Response jsonResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(payload)
                .when().post(path).andReturn();

        JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());

        final String documentUrl =
                jsonPath.get("data." + documentName + ".document_binary_url");
        final String response = utils.downloadPdfAndParseToString(documentUrl);
        return removeCrLfs(response);
    }

    private String generateNonProbateDocument(String jsonFileName, String path) {
        return generateDocument(jsonFileName, path, NON_PROBATE_DOC_NAME);
    }

    private String generateSotDocument(String jsonFileName, String path) {
        return generateSotDocumentFromPayload(utils.getJsonFromFile(jsonFileName), path);
    }

    private String generateSotDocumentFromPayload(String payload, String path) {
        return generateDocumentFromPayload(payload, path, SOT_DOC_NAME);
    }

    @Test
    public void verifySuccessForGetAdmonWillGrantForCardiff() {
        final CaseData caseData = CaseData.builder().build();

        final String response = generateGrantDocument(DEFAULT_ADMON_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("admonWillGrantForCardiffResponse.txt"));
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetAdmonWillGrantDraftForCardiff() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(DEFAULT_ADMON_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("admonWillGrantDraftForCardiffResponse.txt"));
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGoPChangesForGopGenerate() {
        final String response = generateGrantDocument(TRUST_CORPS_GOP_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains("Trust Corporation Name 1 High St"));
    }

    @Test
    public void verifySuccessForDigitalGrantDraftAddresses() {
        final String response = generateGrantDocument(CW_PART_SUCC, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains("Probate Practitioner 123 London London"));
    }

    @Test
    public void verifySuccessForDigitalGrantAddresses() {
        final String response = generateGrantDocument(CW_PART_SUCC, GENERATE_GRANT);

        assertTrue(response.contains("Probate Practitioner 123 London London"));
    }

    @Test
    public void verifySuccessForDigitalGrantAddressesFirm() {
        final String response = generateGrantDocument(CW_PART, GENERATE_GRANT);

        assertTrue(response.contains("Probate Practitioner 123 London London"));
    }

    @Test
    public void verifySuccessForDigitalGrantDraftAddressesFirm() {
        final String response = generateGrantDocument(CW_PART, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains("Probate Practitioner 123 London London"));
    }

    @Test
    public void verifySuccessForGoPChangesForGopGenerateDraft() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(TRUST_CORPS_GOP_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains("Trust Corporation Name 1 High St"));
    }

    @Test
    public void verifySuccessForGetIntestacyGrantForCardiff() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(DEFAULT_INTESTACY_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("intestacyGrantForCardiffResponse.txt"));
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetIntestacyGrantDraftForCardiff() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(DEFAULT_INTESTACY_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("intestacyGrantDraftForCardiffResponse.txt"));
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetGopGrantForCardiff() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(DEFAULT_GOP_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("gopGrantForCardiffResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetGopGrantDraftForCardiff() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(DEFAULT_GOP_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("gopGrantDraftForCardiffResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForWillLodgementForCardiff() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateNonProbateDocument(DEFAULT_WILL_NO_DOCS_PAYLOAD, GENERATE_DEPOSIT_RECEIPT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("willLodgementDepositReceiptResponse.txt"));
        expectedText = expectedText.replaceAll("19th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForOxfordGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(OXFORD_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("oxfordGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForManchesterGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(MANCHESTER_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("manchesterGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLeedsGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(LEEDS_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("leedsGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLiverpoolGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(LIVERPOOL_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("liverpoolGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForBrightonGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(BRIGHTON_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("brightonGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLondonGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(LONDON_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("londonGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForNewcastleGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(NEWCASTLE_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("newcastleGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForWinchesterGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(WINCHESTER_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("winchesterGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForBristolGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = generateGrantDocument(BRISTOL_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("bristolGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetPdfLegalStatementProbateWithSingleExecutorSols() {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_PROBATE_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(!response.contains(AUTHORISED_SOLICITOR));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(LEGAL_STATEMENT_GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT_STATEMENT));

        assertTrue(!response.contains(DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC));
    }

    @Test
    public void verifySuccessForGetPdfLegalStatementProbateWithMultipleExecutorSols() {
        final String response = generateSotDocument(MULTIPLE_EXEC_SOLS_PDF_PROBATE_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(!response.contains(AUTHORISED_SOLICITOR));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(LEGAL_STATEMENT_GOP));
        assertTrue(response.contains(APPLYING_EXECUTOR_STATEMENT_OLD_SCHEMA));

        assertTrue(!response.contains(DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC));
    }

    @Test
    public void verifySuccessForGetPdfLegalStatementIntestacyWithSingleExecutorSols() {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_INTESTACY_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(response.contains(AUTHORISED_SOLICITOR));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(PRIMARY_APPLICANT_STATEMENT_OLD_SCHEMA));
        assertTrue(response.contains(LEGAL_STATEMENT_INTESTATE));

        assertTrue(!response.contains(DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC));
    }

    @Test
    public void verifySuccessForGetPdfLegalStatementAdmonWillSols() {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_ADMON_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(response.contains(AUTHORISED_SOLICITOR));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(LEGAL_STATEMENT_ADMON_WILL));

        assertTrue(!response.contains(DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC));
    }

    @Test
    public void verifySuccessForFurtherEvidenceAdmonWill() {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_ADMON_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(FURTHER_EVIDENCE));
        assertTrue(response.contains(DOMICILITY_SENTENCE_UK));
        assertTrue(response.contains(FIRM_AUTHORISATION));
        assertTrue(response.contains(WILL_NO_CODICILS));
    }

    @Test
    public void verifySuccessForFurtherEvidenceAdmonWillWithWillDateAndCodicils() {
        final String response = generateSotDocument(ADMON_PAYLOAD_WILL_AND_CODICILS_DATES, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(FURTHER_EVIDENCE));
        assertTrue(response.contains(DOMICILITY_SENTENCE_UK));
        assertTrue(response.contains(FIRM_AUTHORISATION));
        assertTrue(response.contains(WILL_NO_CODICILS));
        assertTrue(response.contains(SIGNED_DATE));
        assertTrue(response.contains(CODICIL_DATES));
    }

    @Test
    public void verifySuccessForFurtherEvidenceIntestacy() {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_INTESTACY_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(FURTHER_EVIDENCE));
    }

    @Test
    public void verifySuccessForCodicilsIntestacy() {
        final String response = generateSotDocument(CODICILS_SOLS_PDF_INTESTACY_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(DOMICILITY_SENTENCE_NON_UK));
        assertTrue(response.contains(FIRM_AUTHORISATION));
    }

    @Test
    public void verifySuccessForFurtherEvidenceTrustCorpProbate() {
        final String response = generateSotDocument(TRUST_CORPS_GOP_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(SIGNED_DATE));
        assertTrue(response.contains("1st January 2021"));
        assertTrue(response.contains(FURTHER_EVIDENCE));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithSingleExecutorSols() {
        final String response = generateGrantDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

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
        final String response = generateGrantDocument(DEFAULT_PA_PAYLOAD, GENERATE_GRANT);

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
    public void verifySuccessForGetDigitalGrantWithMultipleExecutorsSolTc() {
        final String response = generateGrantDocument(MULTI_EXEC_TC_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));

        assertTrue(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(MULTI_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(MULTI_EXEC_TC_TRUST_CORP_DETAILS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithMultipleExecutorsSolTc() {
        final String response = generateGrantDocument(MULTI_EXEC_TC_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));

        assertTrue(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(MULTI_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(MULTI_EXEC_TC_TRUST_CORP_DETAILS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithNotNamedSolTc() {
        final String response = generateGrantDocument(NOT_NAMED_TC_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));
        assertFalse(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(MULTI_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_MULTI));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithNotNamedSolTc() {
        final String response = generateGrantDocument(NOT_NAMED_TC_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));
        assertFalse(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(MULTI_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_MULTI));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithNotNamedSolTcTcExec() {
        final String response = generateGrantDocument(NOT_NAMED_TC_TC_EXEC_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));
        assertFalse(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(SINGLE_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_SINGLE));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithNotNamedSolTcTcExec() {
        final String response = generateGrantDocument(NOT_NAMED_TC_TC_EXEC_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));
        assertFalse(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(SINGLE_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_SINGLE));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithNotNamedNotApplyingSolTcTcExec() {
        final String payload = replaceAllInString(utils.getJsonFromFile(NOT_NAMED_TC_PAYLOAD),
                "\"solsSolicitorIsApplying\": \"Yes\"", "\"solsSolicitorIsApplying\": \"No\"");
        final String response = generateGrantDocumentFromPayload(payload, GENERATE_GRANT);
        assertTrue(response.contains(FRAGMENT_WITH_NO_MULTIPLE_ANDS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithNotNamedNotApplyingSolTcTcExec() {
        final String payload = replaceAllInString(utils.getJsonFromFile(NOT_NAMED_TC_PAYLOAD),
                "\"solsSolicitorIsApplying\": \"Yes\"", "\"solsSolicitorIsApplying\": \"No\"");
        final String response = generateGrantDocumentFromPayload(payload, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(FRAGMENT_WITH_NO_MULTIPLE_ANDS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithNotNamedSolTcPowerReserved() {
        final String response = generateGrantDocument(NOT_NAMED_TC_POWER_RESERVED_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));
        assertFalse(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(MULTI_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_MULTI));
        assertTrue(response.contains(POWER_RESERVED_TO_ONE));
        assertFalse(response.contains(EXTRANEOUS_CURLY_START_BRACE));
        assertFalse(response.contains(EXTRANEOUS_CURLY_END_BRACE));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithNotNamedSolTcPowerReserved() {
        final String response = generateGrantDocument(NOT_NAMED_TC_POWER_RESERVED_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));
        assertFalse(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(MULTI_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_MULTI));
        assertTrue(response.contains(POWER_RESERVED_TO_ONE));
        assertFalse(response.contains(EXTRANEOUS_CURLY_START_BRACE));
        assertFalse(response.contains(EXTRANEOUS_CURLY_END_BRACE));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithMultipleExecutorsSOls() {
        final String response = generateGrantDocument("solicitorPayloadNotificationsMultipleExecutors.json",
                GENERATE_GRANT);

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
        final String response = generateGrantDocument("solicitorPayloadNotificationsPowerReservedMultiple.json",
                GENERATE_GRANT);

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
        final String response = generateGrantDocument("solicitorPayloadNotificationsPowerReserved.json",
                GENERATE_GRANT);

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
        final String response = generateGrantDocument("solicitorPayloadNotificationsGrantInfo.json",
                GENERATE_GRANT);

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
        final String response = generateGrantDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

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
        final String response = generateGrantDocument(DEFAULT_PA_PAYLOAD, GENERATE_GRANT_DRAFT);

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
        final String response = generateGrantDocument("solicitorPayloadNotificationsMultipleExecutors.json",
                GENERATE_GRANT_DRAFT);

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
        final String response = generateGrantDocument("solicitorPayloadNotificationsPowerReservedMultiple.json",
                    GENERATE_GRANT_DRAFT);

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
        final String response = generateGrantDocument("solicitorPayloadNotificationsPowerReserved.json",
                GENERATE_GRANT_DRAFT);

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
        final String response = generateGrantDocument("solicitorPayloadNotificationsGrantInfo.json",
                GENERATE_GRANT_DRAFT);

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
        final String response = generateGrantDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(DOD));
        assertTrue(response.contains(GOP));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDateFormat() {
        final String response = generateGrantDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(DOD));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftMoneyFormat() {
        final String response = generateGrantDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(IHT_GROSS));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(IHT_NET));
        assertTrue(response.contains(GOP));

    }

    @Test
    public void verifySuccessForGetDigitalGrantMoneyFormat() {
        final String response = generateGrantDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(IHT_GROSS));
        assertTrue(response.contains(IHT_NET));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantMoneyFormatWithPence() {
        final String response = generateGrantDocument("solicitorPayloadNotificationsIHTCurrencyFormat.json",
                GENERATE_GRANT);

        assertTrue(response.contains(IHT_GROSS_PENCE));
        assertTrue(response.contains(IHT_NET_PENCE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftMoneyFormatWithPence() {
        final String response = generateGrantDocument("solicitorPayloadNotificationsIHTCurrencyFormat.json",
                GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(IHT_GROSS_PENCE));
        assertTrue(response.contains(IHT_NET_PENCE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantNotApplying() {
        final String response =
                generateGrantDocument("solicitorPayloadNotificationsMultipleExsPANotApplying.json",
                        GENERATE_GRANT_DRAFT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(DIED_ON_OR_BEFORE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantPrimaryApplicantNotApplying() {
        final String response =
            generateGrantDocument("solicitorPayloadNotificationsMultipleExsPANotApplying.json", GENERATE_GRANT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantNotApplyingPowerReserved() {
        final String response =
            generateGrantDocument("solicitorPayloadNotificationsMultipleExsPANotApplyingPowerReserved.json",
                GENERATE_GRANT_DRAFT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));
        assertTrue(!response.contains(POWER_RESERVED));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantPrimaryApplicantNotApplyingPowerReserved() {
        final String response =
            generateGrantDocument("solicitorPayloadNotificationsMultipleExsPANotApplyingPowerReserved.json",
                    GENERATE_GRANT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));
        assertTrue(!response.contains(POWER_RESERVED));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantNotApplyingPowerReservedMultiple() {
        final String response =
            generateGrantDocument("solicitorPayloadNotificationsPANotApplyingPowerReservedMultiple.json",
                GENERATE_GRANT_DRAFT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(LONDON_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantPrimaryApplicantNotApplyingPowerReservedMultiple() {
        final String response =
            generateGrantDocument("solicitorPayloadNotificationsPANotApplyingPowerReservedMultiple.json",
                    GENERATE_GRANT);

        assertTrue(!response.contains(PRIMARY_APPLICANT));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(LONDON_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantPartialSolsAddress() {
        final String response =
                generateGrantDocument("solicitorPayloadNotificationsPartialAddress.json", GENERATE_GRANT);

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
        final String response = generateGrantDocument("solicitorPayloadNotificationsPartialAddress.json",
                GENERATE_GRANT);

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
        final String response = generateGrantDocument("solicitorPayloadNotificationsForeignDomicile.json",
                GENERATE_GRANT);

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
        final String response = generateGrantDocument(DEFAULT_REISSUE_PAYLOAD, GENERATE_GRANT_DRAFT_REISSUE);
        assertTrue(response.contains(ENGLAND_AND_WALES));
        assertTrue(response.contains(CASE_REFERENCE));
        assertTrue(response.contains(DECEASED_DETAILS));
        assertTrue(response.contains(DECEASED_DOD));
        assertTrue(response.contains(HMCTS_VALUE));
        assertTrue(response.contains(POSTCODE));
    }

    @Test
    public void verifyAssembleLetterShouldReturnOkResponseCode() {
        final ResponseBody response = validatePostSuccess("/document/assembleLetterPayLoad.json",
                ASSEMBLE_LETTER);
        final JsonPath jsonPath = JsonPath.from(response.asString());
        final List paragraphDetails = jsonPath.get("data.paragraphDetails");
        final String templateName = jsonPath.get("data.paragraphDetails[1].value.templateName");
        response.prettyPrint();

        assertEquals(paragraphDetails.size(), 3);
        assertEquals(templateName, ParagraphCode.MissInfoWill.getTemplateName());
    }

    @Test
    public void verifyAssembleLetterShouldReturnIHTReferenceNumber() {
        final String jsonAsString = getJsonFromFile("/document/assembleLetterTransform.json");
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(jsonAsString)
            .when().post(ASSEMBLE_LETTER)
            .andReturn();

        final JsonPath jsonPath = JsonPath.from(response.asString());
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        assertEquals(jsonPath.get("data.ihtReferenceNumber"), "ONLINE-123434");
    }

    @Test
    public void verifyDefaultRePrintValuesReturnsOkResponseCode() {
        final ResponseBody response =
            validatePostSuccess("/document/rePrintDefaultGrantOfProbate.json", DEFAULT_PRINT_VALUES);

        response.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(response.asString());
        assertEquals(jsonPath.get("data.reprintDocument.list_items[0].label"), "Grant");
        assertEquals(jsonPath.get("data.reprintDocument.list_items[0].code"), "WelshGrantFileName");
    }

    @Test
    public void verifyDefaultRePrintValuesReturnsIhtReferenceNumber() {
        String jsonAsString = getJsonFromFile("/document/rePrintDefaultGrantOfProbate.json");
        jsonAsString = jsonAsString.replaceFirst("\"paperForm\": \"Yes\",", "\"paperForm\": \"No\",");

        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(jsonAsString)
            .when().post(DEFAULT_PRINT_VALUES)
            .andReturn();
        assertEquals(response.getStatusCode(), 200);
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertEquals(jsonPath.get("data.ihtReferenceNumber"), "ONLINE-123434");
    }

    @Test
    public void verifySolicitorGenerateLetterReturnOkResponseCode() {
        final String response = generateGrantDocument(GENERATE_LETTER_PAYLOAD, GENERATE_LETTER);
        assertEquals(getJsonFromFile("/document/assembledLetter.txt"), response);
    }

    @Test
    public void verifySolicitorGenerateLetterReturnsIHTReferenceNumber() {
        final ResponseBody responseBody =
            validatePostSuccess("/document/generateLetterDefaultLocation.json", GENERATE_LETTER);
        responseBody.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        assertEquals(jsonPath.get("data.ihtFormId"), "IHT205");
        assertNull(jsonPath.get("data.errors"));
    }

    @Test
    public void verifySolicitorPreviewLetterReturnsCorrectResponse() {
        final Response jsonResponse = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("/document/generateLetter.json"))
            .when().post(PREVIEW_LETTER).andReturn();
        jsonResponse.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());
        final String documentUrl = jsonPath.get("data.previewLink.document_binary_url");

        String response = removeCrLfs(utils.downloadPdfAndParseToString(documentUrl));
        assertEquals(response, getJsonFromFile("/document/previewLetterResponse.txt"));
    }

    @Test
    public void verifySolicitorPreviewLetterReturnsIHTReferenceNumber() {
        final ResponseBody responseBody = validatePostSuccess("/document/generateLetterDefaultLocation.json",
                PREVIEW_LETTER);
        responseBody.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        assertEquals(jsonPath.get("data.ihtFormId"), "IHT205");
        assertNull(jsonPath.get("data.errors"));
    }

    @Test
    public void verifySolicitorRePrintReturnBadResponseCode() {
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId("serviceToken", "userId"))
            .body(getJsonFromFile("/document/rePrint.json"))
            .when().post(RE_PRINT)
            .andReturn();
        assertEquals(response.statusCode(), 403);
        assertTrue(response.getBody().asString().contains("Forbidden"));
    }

    @Test
    public void verifySoTDomiciledInEnglandAndWales() {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_ADMON_PAYLOAD, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Main Applicant of Test, Test, A1 2BC, UK make the following"
                + " statement:The person who diedDe Ceased, of Test, Test, Test, A1 2BC, was born on"
                + " 23/01/1998 and died on 23/01/2020, domiciled in England and Wales."));
    }

    @Test
    public void verifySoTIndividualExecutorPowerReserved() {
        final String response = generateSotDocument("solicitorExecutorsNotApplyingReasons.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("executor4_name, another executor named in the will,"
                + " is not making this application but reserves power to do so at a later date."));
    }

    @Test
    public void verifySoTIndividualExecutorRenunciation() {
        final String response = generateSotDocument("solicitorExecutorsNotApplyingReasons.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("executor3_name, another executor named in the will, "
                + "has renounced probate and letters "
                + "of administration with will annexed"));
    }

    @Test
    public void verifySoTExecutorDiedBeforeAndAfterDeceased() {
        final String response = generateSotDocument("solicitorExecutorsNotApplyingReasons.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
                "executor1_name, another executor named in the will, has died in the lifetime of the deceased."));
        assertTrue(response.contains(
                "executor2_name, another executor named in the will, has survived the deceased and died since."));
    }

    @Test
    public void verifySoTExecutorLacksMentalCapacity() {
        final String response = generateSotDocument("solicitorExecutorsNotApplyingReasons.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
                "executor5_name, another executor named in the will, lacks capacity to manage their"
                        + " affairs under the Mental Capacity Act 2005 and is unable to act as an executor."));
    }

    @Test
    public void verifySoTExecutorPowerReservedAndNoticeDispenseGiven() {
        final String response = generateSotDocument("solicitorPayloadDispenseNotGiven.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Notice of this application has on the 10th October 2010 "
                + "been dispensed with under Rule 27(3) of the Non-Contentious Probate Rules "
                + "1987 to executor1_name to whom power is to be reserved."));
    }

    @Test
    public void verifySoTExecutorConcurrentApplication() {
        final String response = generateSotDocument("solicitorExecutorsNotApplyingReasons.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("We are concurrently applying for notice of this application"
                + " to be dispensed with under Rule 27(3) of the Non-Contentious Probate Rules"
                + " 1987 to executor6_name to whom power is to be reserved."));
    }

    @Test
    public void verifySoTFirstParagraphPersonWhoDiedForClearingOne() {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmLegalStatement.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
                "The person who diedDeceased Name, of Chapter Of Wells, Wells Cathedral, Wells, Somerset,"
                + " BA5 2PA, United Kingdom was born on 12/01/2020 and died on 14/01/2020, "
                + "domiciled in England and Wales. The will appoints an executor."));
    }

    @Test
    public void verifySoTFirstParagraphPersonWhoDiedForClearingTwo() {
        final String response = generateSotDocument("solicitorPayloadPartnersInFirm.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
                "The person who diedDeceased Name, of Chapter Of Wells, Wells Cathedral, Wells, Somerset,"
                + " BA5 2PA, United Kingdom was born on 12/01/2020 and died on 14/01/2020, "
                + "domiciled in England and Wales. The will appoints an executor."));
    }


    @Test
    public void verifySecondParagraphFirmSuccessionForClearingThree() {
        final String response = generateSotDocument("solicitorPayloadSoleSuccessorLegalStatement.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
                "The executor Partner Exec, is the only profit-sharing partner and stakeholder in the firm "
                    + "Successor firm that had succeeded to and carried on the practice of the firm Firmname will, "
                    + "at the date of death of the deceased."));

    }

    @Test
    public void verifySoTSecondParagraphFirmSuccessionForClearingFour() {
        final String response = generateSotDocument("solicitorPayloadSolePrin.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor Partner Exec, is the only profit-sharing partner and "
                + "stakeholder in the firm Firmname will, at the date of death of the deceased."));

    }

    @Test
    public void verifySoTThirdParagraphOthersRenouncingInSuccessorClearingNine() {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmRenounce.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
                "The executor Partner Exec, is a profit-sharing partner and stakeholder in the firm Successor firm"
                + " that had succeeded to and carried on the practice of the "
                + "firm Firmname will, at the date of death of the deceased. The remaining profit-sharing partners and "
                + "stakeholders in the firm Firmname will are renouncing their right to probate."));

    }

    @Test
    public void verifySoTThirdParagraphOthersRenouncingInPartnerFirmClearingTen() {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmRenounce.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
                "The executor Partner Exec, is a profit-sharing partner and stakeholder in the firm Successor firm"
                + " that had succeeded to and carried on the practice of the "
                + "firm Firmname will, at the date of death of the deceased. The remaining profit-sharing partners and "
                + "stakeholders in the firm Firmname will are renouncing their right to probate."));

    }

    @Test
    public void verifySoTFourthParagraphAllSuccessorPartnersRenouncingClearingFive() {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmAllRenounceNoAdditional.json",
                GENERATE_LEGAL_STATEMENT);
        // all partners are renouncing, so other partners in the collection are ignored, and wording is
        // 'the executor named in the will' as opposed to 'an executor named in the will'
        assertTrue(response.contains("Probate Practioner, the executor named in the will, is applying for probate."));
    }

    @Test
    public void verifySoTFourthParagraphAllPartnerFirmsRenouncingClearingSix() {
        final String response = generateSotDocument("solicitorPayloadPartnersAllRenounce.json",
                GENERATE_LEGAL_STATEMENT);
        // all partners are renouncing, so other partners in the collection are ignored, and wording is
        // 'the executor named in the will' as opposed to 'an executor named in the will'
        assertTrue(response.contains("Probate Practioner, the executor named in the will, is applying for probate."));
    }

    @Test
    public void verifySoTFifthParagraphSeniorJudgeDistrictClearingSeven() {
        final String response = generateSotDocument("solicitorPayloadJudgeSeniorDistrict.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
                "The executor named in the will has by a resolution, which has been filed with the "
                + "Senior District Judge or Registry,"
                + " in which Exfn1 Exln1 identified by the position they hold and which is still in force, "
                + "appointed them "
                + "for the purpose of applying for probate of the will or for grants of probate on its behalf."));

    }

    @Test
    public void verifySoTFifthParagraphLodgedApplicationClearingEight() {
        final String response = generateSotDocument("solicitorPayloadLodgeApp.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor named in the will has by a resolution, certified copy"
                + " of which is lodged with this application, in which Exfn1 Exln1 identified by the position"
                + " they hold and which is still in force, appointed them for the purpose of applying for probate"
                + " of the will or for grants of probate on its behalf."));

    }


    @Test
    public void verifySoTFirstParagraphClearancePartnerSucceeded() {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmLegalStatement.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
                "The executor Partner Exec, is a profit-sharing partner and stakeholder in the firm"
                + " Successor firm that had succeeded to and carried on the practice of the firm Firmname will,"
                + " at the date of death of the deceased."));

    }

    @Test
    public void verifySoTSecondParagraphSoleSucceeded() {
        final String response = generateSotDocument("solicitorPayloadSoleSuccessorLegalStatement.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
                "The executor Partner Exec, is the only profit-sharing partner and stakeholder in the firm"
                + " Successor firm that had succeeded to and carried on the practice of the firm Firmname will,"
                + " at the date of death of the deceased."));

    }

    @Test
    public void verifySoTThirdParagraphPartnerRenounceSucceeded() {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmRenounce.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
                "The executor Partner Exec, is a profit-sharing partner and stakeholder in the "
                + "firm Successor firm that had succeeded to and carried on the practice of the firm Firmname "
                + "will, at the date of death of the deceased."));

    }

    @Test
    public void verifySoTFourthParagraphPartnerAllRenounceSucceeded() {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmAllRenounce.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Probate Practioner, an executor named in the will, is applying for probate."));
    }

    @Test
    public void verifySoTFifthParagraphJudgeSeniorDistrict() {
        final String response = generateSotDocument("solicitorPayloadJudgeSeniorDistrict.json",
                GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains("We, Probate Practioner of Chapter Of Wells, Wells Cathedral, Wells, Somerset, "
                + "BA5 2PA, United Kingdom and Exfn1 Exln1 of Chapter Of Wells, Wells Cathedral, Somerset, Wells, "
                + "Somerset, BA5 2PA, United Kingdom make the following statement:"));

        assertTrue(response.contains("The executor named in the will has by a resolution,"
            + " which has been filed with the Senior District Judge or Registry, in which Exfn1 Exln1 identified by"
            + " the position they hold and which is still in force, "
            + "appointed them for the purpose of applying for probate"
            + " of the will or for grants of probate on its behalf."));

        assertTrue(response.contains("Exfn1 Exln1 is acting on behalf of Trust_Corporation_pls trust corporation. "
            + "They hold the position of Solicitor as per the resolution."));
    }

    @Test
    public void verifySoTSixthParagraphTrustCorpResolutionLodged() {
        final String response = generateSotDocument("verifySolPayloadTrustCorpResolutionLodged.json",
                GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor named in the will has by a resolution, "
                + "certified copy of which is lodged"
                + " with this application, in which Exfn1 Exln1 identified by the position they hold and which"
                + " is still in force, appointed them for the purpose of applying for probate of "
                + "the will or for grants of probate on its behalf."));
    }

    @Test
    public void verifySoTNoDuplicateSolExecutors() {
        final String response = generateSotDocument(NO_DUPE_SOL_EXECUTORS, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
                .contains("The executor believes that all the information stated in the legal statement is true."));
        assertTrue(response.contains("Fred Smith, is a profit-sharing partner in the firm "
            + "fdgfg, at the date of death"));
        assertTrue(response.split("Fred Smith").length == 4);
    }

    @Test
    public void verifySoTAliasNameForExec() {
        final String response = generateSotDocument(EXEC_WITH_ALIAS, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Carlos Juan otherwise known as Karakiozis of"));
    }
    
    public void verifySoTSolePartnerWording() {
        final String response = generateSotDocument(SOLE_PRIN, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Fred Smith, is a profit-sharing partner in the firm "
            + "fdgfg, at the date of death"));
        assertTrue(response.split("Fred Smith").length == 4);
    }
    
    public void verifySoTPartAllRenouncingWording() {
        final String response = generateSotDocument(PART_ALL_RENOUNCING, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
            .contains("I am the executor named in the will. The profit-sharing partners and stakeholders in the firm"
                + " Firmname will at the date of death of the deceased have renounced probate."));
    }

    @Test
    public void verifySoTPartSuccAllRenouncingWording() {
        final String response = generateSotDocument(PART_ALL_SUCC_RENOUNCING, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
            .contains("I am the executor named in the will. The profit-sharing partners and stakeholders in the firm"
                + " Firmname will that had succeeded to and carried on the practice of the firm Successor firm at the "
                + "date of death of the deceased have renounced probate."));
    }

    @Test
    public void verifySoTPartOthersRenouncingWording() {
        final String response = generateSotDocument(PART_ALL_OTHERS_RENOUNCING, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
            .contains("The executor Partner Exec, is a profit-sharing partner and stakeholder in the firm "
                + "Firmname will, at the date of death of the deceased."));
    }

    @Test
    public void verifySoTSolPartnersWording() {
        final String response = generateSotDocument(SOLE_PRIN_OTHER_PARTNERS, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
            .contains("The executors Probate Practitioner, Partner Exec, are the profit-sharing partners and "
                + "stakeholders in the firm "));
    }

    @Test
    public void verifySoTSolPartnersWordingSingleExec() {
        final String response = generateSotDocument(SOLE_PRIN_OTHER_PARTNERS_SINGLE, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
            .contains("The executor Partner Exec, is the only profit-sharing partner and "
                + "stakeholder in the firm "));
    }

    @Test
    public void verifySoTSolNotRepeated() {
        final String response = generateSotDocument(SOL_NOT_REPEATED, GENERATE_LEGAL_STATEMENT);
        assertFalse(response
                .contains("Jim Smith (executor)"));
        assertTrue(response
                .contains("Jim Smith (Probate practitioner and executor)"));
        assertTrue(response.split("Jim Smith").length == 5);
    }
    
    public void verifyDefaultEvidenceToYesFromNull() {
        Response jsonResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(DEFAULT_SOLS_PAYLOAD))
                .when().post(GENERATE_GRANT).andReturn();
        assertTrue(jsonResponse.prettyPrint().contains("\"evidenceHandled\": \"Yes\""));
    }

    @Test
    public void verifyDefaultEvidenceToYesFromNo() {
        Response jsonResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("evidenceHandledNo.json"))
                .when().post(GENERATE_GRANT).andReturn();
        assertTrue(jsonResponse.prettyPrint().contains("\"evidenceHandled\": \"Yes\""));
    }

    @Test
    public void verifyDefaultEvidenceToYesFromYes() {
        Response jsonResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("evidenceHandledYes.json"))
                .when().post(GENERATE_GRANT).andReturn();
        assertTrue(jsonResponse.prettyPrint().contains("\"evidenceHandled\": \"Yes\""));
    }

    @Test
    public void verifyGenerateSolsCoverSheetGopRenouncingExecutors() {
        String payload = "/caseprogress/04a-caseCreated.json";
        String response = generateDocument(payload, VALIDATE_PROBATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogress/expectedDocumentText/04a-caseCreatedRenouncingExecutors");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetGopWillHasCodicils() {
        String payload = "/caseprogress/04b-caseCreated.json";
        String response = generateDocument(payload, VALIDATE_PROBATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogress/expectedDocumentText/04b-caseCreatedWillHasCodicils");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetGopIht217() {
        String payload = "/caseprogress/04c-caseCreated.json";
        String response = generateDocument(payload, VALIDATE_PROBATE_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogress/expectedDocumentText/04c-caseCreatedIHT217");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetIntestacy() {
        String payload = "/caseprogressintestacy/04-caseCreated.json";
        String response = generateDocument(payload, VALIDATE_INTESTACY_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogressintestacy/expectedDocumentText/04-caseCreated");
        assertTrue(response.contains(expectedText));

    }

    @Test
    public void verifyGenerateSolsCoverSheetAdmonWill() {
        String payload = "/caseprogressadmonwill/04-caseCreated.json";
        String response = generateDocument(payload, VALIDATE_ADMON_URL, "solsCoversheetDocument");
        String expectedText = utils
            .getJsonFromFile("/caseprogressadmonwill/expectedDocumentText/04-caseCreated");
        assertTrue(response.contains(expectedText));

    }

}
