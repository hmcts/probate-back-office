package uk.gov.hmcts.probate.functional.documents;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(SpringIntegrationSerenityRunner.class)
public class GrantGenerationTests extends DocumentGenerationTestBase {
    // Grant fields
    private static final String SOLICITOR_INFO1 =
        "Extracted by Solicitor Firm Name (Ref: 1231-3984-3949-0300) SolAddLn1, SolAddLn2, SolAddLn3, ";
    private static final String SOLICITOR_INFO2 = "SolAddPT, SolAddCounty, KT10 0LA, SolAddCo";
    private static final String SOLICITOR_INFO3 =
        "Extracted by Solicitor Firm Name (Ref: 1231-3984-3949-0300) SolAddLn1, SolAddLn3, SolAddPT, KT10 0LA, "
            + "SolAddCo";
    private static final String PA = "Extracted personally";
    private static final String PRIMARY_APPLICANT = "Executor name 1 Executor Last Name 1";
    private static final String WILL_MESSAGE = "Will Message With a codicil";
    private static final String ADMIN_MESSAGE = "admin clause limitation message";
    private static final String LIMITATION_MESSAGE = "Limitation message";
    private static final String EXECUTOR_LIMITATION_MESSAGE = "Executor limitation message";
    private static final String POWER_RESERVED = "Power reserved to other Executors";
    private static final String POWER_RESERVED_SINGLE = "Power reserved to another Executor";
    private static final String TITLE = "Captain";
    private static final String HONOURS = "OBE";
    private static final String ADD_EXEC_ONE = "Add Ex First Name 1 Add Ex Last Name 1";
    private static final String ADD_EXEC_ONE_FIRST_NAME = "Add Executor First1";
    private static final String ADD_EXEC_ONE_LAST_NAME = "Add Executor Last1";
    private static final String ADD_EXEC_TWO_FIRST_NAME = "Add Executor First2";
    private static final String ADD_EXEC_TWO_LAST_NAME = "Add Executor Last2";
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

    private static final String MULTI_EXEC_TC_PROB_PRACTITIONER = "Tony Stark";
    private static final String MULTI_EXEC_TC_DECEASED = "The Last Will and Testament of  (An official copy of "
        + "which is available from the Court) was John Smith";
    private static final String MULTI_EXEC_TC_AMINISTRATION_STATEMENT = "The Administration of 's estate is John Smith"
        + "granted by this court to the following Executors";
    private static final String MULTI_EXEC_TC_TRUST_CORP_DETAILS = "and  MyTc 19 Curtis Street Charlton Kings Swindon "
        + "Glos Sn2 2JU United Kingdom";
    private static final String NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_MULTI = "Executorsof  MyTc 19 Curtis Street "
        + "Charlton Kings Swindon Glos Sn2 2JU United Kingdom";
    private static final String POWER_RESERVED_TO_ONE = "Power reserved to another Executor";
    private static final String EXTRANEOUS_CURLY_START_BRACE = "{";
    private static final String EXTRANEOUS_CURLY_END_BRACE = "}";
    private static final String SINGLE_EXEC_TC_AMINISTRATION_STATEMENT = "The Administration of 's estate is John Smith"
        + "granted by this court to the following Executorof";
    private static final String NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_SINGLE = "Executorof  MyTc 19 Curtis Street "
        + "Charlton Kings Swindon Glos Sn2 2JU United Kingdom";
    public static final String THE_UNITED_KINGDOM = "the United Kingdom";
    public static final String GOP_JSON = "gop.json";
    public static final String DECEASED_DOMICILE_IN_ENG_WALES_YES = "\"deceasedDomicileInEngWales\": \"Yes\"";
    public static final String DECEASED_DOMICILE_IN_ENG_WALES_NO = "\"deceasedDomicileInEngWales\": \"No\"";
    public static final String ADMON_WILL_JSON = "admonWill.json";
    public static final String ADMON_WILL_REISSUE_JSON = "admonWillReissue.json";
    public static final String INTESTACY_JSON = "intestacy.json";
    public static final String INTESTACY_REISSUE_JSON = "intestacyReissue.json";
    public static final String NOVEMBER_2020 = "18th November 2020";
    public static final String PROBATE_PRACTITIONER_123_LONDON_LONDON = "Probate Practitioner 123 London London";
    public static final String MORE_PARTNERS_HOLDING_POWER_RESERVED_NO = "\"morePartnersHoldingPowerReserved\": \"No\"";
    public static final String MORE_PARTNERS_HOLDING_POWER_RESERVED_YES =
        "\"morePartnersHoldingPowerReserved\": \"Yes\"";
    public static final String JULY_2021 = "5th July 2021";

    private static final String DECEASED_DOMICILED_IN_ENG_WALES_TEXT = "The application has stated that the gross value"
        + " of the estate in the United Kingdom amounts to £10,000.00 and the net value amounts to £8,000.00";

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
    private static final String OXFORD_REGISTRY_ADDRESS = "High Court of Justice England and Wales"
        + "Oxford District Probate Registry Combined Court BuildingSt AldatesOxfordOX1 1LY0300 303 0648";
    private static final String REISSUE_REASON_DUPLICATE = "Grant of Probate Duplicate of original "
        + "created on 1st April 2020";
    private static final String REISSUE_ORIGINAL_ISSUE_DATE = "1st April 2021";

    private static final String SOL_PAYLOAD_REISSUE_CTSC = "solicitorPayloadReissueCtsc.json";
    private static final String DEFAULT_WILL_PAYLOAD = "willLodgementPayload.json";
    private static final String DEFAULT_ADMON_CARDIFF_PAYLOAD = "solicitorPayloadNotificationsAdmonWillCardiff.json";
    private static final String DEFAULT_INTESTACY_CARDIFF_PAYLOAD =
        "solicitorPayloadNotificationsIntestacyCardiff.json";
    private static final String DEFAULT_GOP_CARDIFF_PAYLOAD = "solicitorPayloadNotificationsGopCardiff.json";
    private static final String OXFORD_GOP_PAYLOAD = "solicitorPayloadNotificationsGopOxford.json";
    private static final String MANCHESTER_GOP_PAYLOAD = "solicitorPayloadNotificationsGopManchester.json";
    private static final String LEEDS_GOP_PAYLOAD = "solicitorPayloadNotificationsGopLeeds.json";
    private static final String LIVERPOOL_GOP_PAYLOAD = "solicitorPayloadNotificationsGopLiverpool.json";
    private static final String BRIGHTON_GOP_PAYLOAD = "solicitorPayloadNotificationsGopBrighton.json";
    private static final String LONDON_GOP_PAYLOAD = "solicitorPayloadNotificationsGopLondon.json";
    private static final String NEWCASTLE_GOP_PAYLOAD = "solicitorPayloadNotificationsGopNewcastle.json";
    private static final String WINCHESTER_GOP_PAYLOAD = "solicitorPayloadNotificationsGopWinchester.json";
    private static final String BRISTOL_GOP_PAYLOAD = "solicitorPayloadNotificationsGopBristol.json";
    private static final String CW_PART_SUCC = "caseworkerPartSuccPowerReservedToOthers.json";
    private static final String CW_PART = "caseworkerPartOtherRenouncing.json";
    private static final String MULTI_EXEC_TC_PAYLOAD = "solicitorPayloadMultiExecTcReadyToIssue.json";
    private static final String NOT_NAMED_TC_PAYLOAD = "solicitorPayloadTrustCorpsNotNamed.json";
    private static final String NOT_NAMED_TC_TC_EXEC_PAYLOAD = "solicitorPayloadTrustCorpsNotNamedTcExec.json";
    private static final String NOT_NAMED_TC_POWER_RESERVED_PAYLOAD =
        "solicitorPayloadTrustCorpsNotNamedPowerReserved.json";
    private static final String PARTNERS_FIRM_POWER_RESERVED_PAYLOAD =
        "solicitorPayloadTrustCorpsPartnersInFirmPowerReserved.json";
    private static final String FRAGMENT_WITH_NO_MULTIPLE_ANDS =
        "Executorsof  MyTc 19 Curtis Street Charlton Kings Swindon Glos Sn2 2JU United Kingdom of and "
            + "Fred FlintstoneApplying 7 Ashley Avenue Burnham-on-Sea Somerset SN15JU United Kingdom"
            + "The application has stated that the gross value";

    @Test
    public void verifySolicitorGenerateGrantShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);
    }

    @Test
    public void verifySolicitorGenerateGrantDraftShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);
    }

    @Test
    public void verifySolicitorGenerateIntestacyGrantShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("solicitorPayloadNotificationsIntestacy.json", GENERATE_GRANT);
    }

    @Test
    public void verifySolicitorGenerateIntestacyGrantDraftShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("solicitorPayloadNotificationsIntestacy.json", GENERATE_GRANT_DRAFT);
    }


    @Test
    public void verifySolicitorGenerateAdmonWillGrantShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("solicitorPayloadNotificationsAdmonWill.json", GENERATE_GRANT);
    }

    @Test
    public void verifySolicitorGenerateAdmonWillGrantDraftShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("solicitorPayloadNotificationsAdmonWill.json", GENERATE_GRANT_DRAFT);
    }


    @Test
    public void verifyPersonalApplicantGenerateGrantShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess(DEFAULT_PA_PAYLOAD, GENERATE_GRANT);
    }

    @Test
    public void verifyPersonalApplicantGenerateGrantDraftShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess(DEFAULT_PA_PAYLOAD, GENERATE_GRANT_DRAFT);
    }

    @Test
    public void verifyGenerateGrantDraftReissueShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess(DEFAULT_REISSUE_PAYLOAD, GENERATE_GRANT_DRAFT_REISSUE);
    }

    @Test
    public void verifyGenerateAllEnglishGopPersonalGrantTypesWhenDeceasedDomiciledInEnglandOrWales()
        throws IOException {
        final String gopPayload = "/default/gop/personal/";

        String response = generateGrantDocument(gopPayload + GOP_JSON, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response = generateGrantDocument(gopPayload + GOP_JSON, GENERATE_GRANT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response = generateGrantDocument(gopPayload + "gopReissue.json", GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));
    }

    @Test
    public void verifyGenerateAllEnglishGopPersonalGrantTypesWhenDeceasedNotDomiciledInEnglandOrWales()
        throws IOException {
        final String gopPayload = "/default/gop/personal/";

        final String expectedText = replaceAllInString(DECEASED_DOMICILED_IN_ENG_WALES_TEXT, THE_UNITED_KINGDOM,
            ENGLAND_AND_WALES);

        final String payloadIssue = replaceAllInString(getJsonFromFile(gopPayload + GOP_JSON),
            DECEASED_DOMICILE_IN_ENG_WALES_YES, DECEASED_DOMICILE_IN_ENG_WALES_NO);

        String response = generateGrantDocumentFromPayload(payloadIssue, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(payloadIssue, GENERATE_GRANT);
        assertTrue(response.contains(expectedText));

        final String payloadReissue = replaceAllInString(getJsonFromFile(gopPayload + "gopReissue.json"),
            DECEASED_DOMICILE_IN_ENG_WALES_YES, DECEASED_DOMICILE_IN_ENG_WALES_NO);

        response = generateGrantDocumentFromPayload(payloadReissue, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyGenerateAllEnglishGopSolicitorGrantTypesWhenDeceasedDomiciledInEnglandOrWales()
        throws IOException {
        String response = generateGrantDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response = generateGrantDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        final String payload = replaceAllInString(getJsonFromFile(DEFAULT_SOLS_PAYLOAD),
            "\"case_data\": {", "\"case_data\": {\n      \"schemaVersion\": \"2.0.0\",");
        response = generateReissueGrantDraftDocumentFromPayload(payload);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response = generateGrantDocumentFromPayload(payload, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));
    }

    @Test
    public void verifyGenerateAllEnglishGopSolicitorGrantTypesWhenDeceasedNotDomiciledInEnglandOrWales()
        throws IOException {
        final String expectedText = replaceAllInString(DECEASED_DOMICILED_IN_ENG_WALES_TEXT, THE_UNITED_KINGDOM,
            ENGLAND_AND_WALES);

        String payload = replaceAllInString(getJsonFromFile(DEFAULT_SOLS_PAYLOAD),
            DECEASED_DOMICILE_IN_ENG_WALES_YES, DECEASED_DOMICILE_IN_ENG_WALES_NO);

        String response = generateGrantDocumentFromPayload(payload, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(payload, GENERATE_GRANT);
        assertTrue(response.contains(expectedText));

        payload = replaceAllInString(payload,
            "\"case_data\": {", "\"case_data\": {\n      \"schemaVersion\": \"2.0.0\",");
        response = generateReissueGrantDraftDocumentFromPayload(payload);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(payload, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyGenerateAllEnglishAdmonWillPersonalGrantTypesWhenDeceasedDomiciledInEnglandOrWales()
        throws IOException {
        String admonWillPayload = "/default/admonwill/personal/";

        String response = generateGrantDocument(admonWillPayload + ADMON_WILL_JSON, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response = generateGrantDocument(admonWillPayload + ADMON_WILL_JSON, GENERATE_GRANT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response =
            generateGrantDocument(admonWillPayload + ADMON_WILL_REISSUE_JSON, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));
    }

    @Test
    public void verifyGenerateAllEnglishAdmonWillPersonalGrantTypesWhenDeceasedNotDomiciledInEnglandOrWales()
        throws IOException {
        String admonWillPayload = "/default/admonwill/personal/";

        String admonWillPayloadIssue = replaceAllInString(
            getJsonFromFile(admonWillPayload + ADMON_WILL_JSON),
            DECEASED_DOMICILE_IN_ENG_WALES_YES, DECEASED_DOMICILE_IN_ENG_WALES_NO);

        String expectedText = replaceAllInString(DECEASED_DOMICILED_IN_ENG_WALES_TEXT,
            THE_UNITED_KINGDOM, ENGLAND_AND_WALES);

        String response = generateGrantDocumentFromPayload(admonWillPayloadIssue, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(admonWillPayloadIssue, GENERATE_GRANT);
        assertTrue(response.contains(expectedText));

        String admonWillPayloadReissue = replaceAllInString(
            getJsonFromFile(admonWillPayload + ADMON_WILL_REISSUE_JSON),
            DECEASED_DOMICILE_IN_ENG_WALES_YES, DECEASED_DOMICILE_IN_ENG_WALES_NO);

        response = generateGrantDocumentFromPayload(admonWillPayloadReissue, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyGenerateAllEnglishAdmonWillSolicitorGrantTypesWhenDeceasedDomiciledInEnglandOrWales()
        throws IOException {
        String admonWillPayload = "/default/admonwill/solicitor/";

        String response = generateGrantDocument(admonWillPayload + ADMON_WILL_JSON, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response = generateGrantDocument(admonWillPayload + ADMON_WILL_JSON, GENERATE_GRANT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response =
            generateGrantDocument(admonWillPayload + ADMON_WILL_REISSUE_JSON, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));
    }

    @Test
    public void verifyGenerateAllEnglishAdmonWillSolicitorGrantTypesWhenDeceasedNotDomiciledInEnglandOrWales()
        throws IOException {
        String admonWillPayload = "/default/admonwill/solicitor/";

        String admonWillPayloadIssue = replaceAllInString(
            getJsonFromFile(admonWillPayload + ADMON_WILL_JSON),
            DECEASED_DOMICILE_IN_ENG_WALES_YES, DECEASED_DOMICILE_IN_ENG_WALES_NO);

        String expectedText = replaceAllInString(DECEASED_DOMICILED_IN_ENG_WALES_TEXT,
            THE_UNITED_KINGDOM, ENGLAND_AND_WALES);

        String response = generateGrantDocumentFromPayload(admonWillPayloadIssue, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(admonWillPayloadIssue, GENERATE_GRANT);
        assertTrue(response.contains(expectedText));

        String admonWillPayloadReissue = replaceAllInString(
            getJsonFromFile(admonWillPayload + ADMON_WILL_REISSUE_JSON),
            DECEASED_DOMICILE_IN_ENG_WALES_YES, DECEASED_DOMICILE_IN_ENG_WALES_NO);

        response = generateGrantDocumentFromPayload(admonWillPayloadReissue, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyGenerateAllEnglishIntestacyPersonalGrantTypesWhenDeceasedDomiciledInEnglandOrWales()
        throws IOException {
        String intestacyPayload = "/default/intestacy/personal/";

        String response = generateGrantDocument(intestacyPayload + INTESTACY_JSON, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response = generateGrantDocument(intestacyPayload + INTESTACY_JSON, GENERATE_GRANT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response = generateGrantDocument(intestacyPayload + INTESTACY_REISSUE_JSON, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));
    }

    @Test
    public void verifyGenerateAllEnglishIntestacyPersonalGrantTypesWhenDeceasedNotDomiciledInEnglandOrWales()
        throws IOException {
        String intestacyPayload = "/default/intestacy/personal/";

        String intestacyPayloadIssue = replaceAllInString(
            getJsonFromFile(intestacyPayload + INTESTACY_JSON),
            DECEASED_DOMICILE_IN_ENG_WALES_YES, DECEASED_DOMICILE_IN_ENG_WALES_NO);

        String expectedText = replaceAllInString(DECEASED_DOMICILED_IN_ENG_WALES_TEXT,
            THE_UNITED_KINGDOM, ENGLAND_AND_WALES);

        String response = generateGrantDocumentFromPayload(intestacyPayloadIssue, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(intestacyPayloadIssue, GENERATE_GRANT);
        assertTrue(response.contains(expectedText));

        String intestacyPayloadReissue = replaceAllInString(
            getJsonFromFile(intestacyPayload + INTESTACY_REISSUE_JSON),
            DECEASED_DOMICILE_IN_ENG_WALES_YES, DECEASED_DOMICILE_IN_ENG_WALES_NO);

        response = generateGrantDocumentFromPayload(intestacyPayloadReissue, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyGenerateAllEnglishIntestacySolicitorGrantTypesWhenDeceasedDomiciledInEnglandOrWales()
        throws IOException {
        String intestacyPayload = "/default/intestacy/solicitor/";

        String response = generateGrantDocument(intestacyPayload + INTESTACY_JSON, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response = generateGrantDocument(intestacyPayload + INTESTACY_JSON, GENERATE_GRANT);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));

        response = generateGrantDocument(intestacyPayload + INTESTACY_REISSUE_JSON, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(DECEASED_DOMICILED_IN_ENG_WALES_TEXT));
    }

    @Test
    public void verifyGenerateAllEnglishIntestacySolicitorGrantTypesWhenDeceasedDomiciledNotInEnglandOrWales()
        throws IOException {
        String intestacyPayload = "/default/intestacy/solicitor/";

        String intestacyPayloadIssue = replaceAllInString(
            getJsonFromFile(intestacyPayload + INTESTACY_JSON),
            DECEASED_DOMICILE_IN_ENG_WALES_YES, DECEASED_DOMICILE_IN_ENG_WALES_NO);

        String expectedText = replaceAllInString(DECEASED_DOMICILED_IN_ENG_WALES_TEXT,
            THE_UNITED_KINGDOM, ENGLAND_AND_WALES);

        String response = generateGrantDocumentFromPayload(intestacyPayloadIssue, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(intestacyPayloadIssue, GENERATE_GRANT);
        assertTrue(response.contains(expectedText));

        String intestacyPayloadReissue = replaceAllInString(
            getJsonFromFile(intestacyPayload + INTESTACY_REISSUE_JSON),
            DECEASED_DOMICILE_IN_ENG_WALES_YES, DECEASED_DOMICILE_IN_ENG_WALES_NO);

        response = generateGrantDocumentFromPayload(intestacyPayloadReissue, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTrustCorpsShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess(TRUST_CORPS_GOP_PAYLOAD, GENERATE_GRANT);
    }

    @Test
    public void verifySolicitorGenerateGrantDraftReissueCtsc() throws IOException {
        final String response = generateReissueGrantDraftDocument(SOL_PAYLOAD_REISSUE_CTSC);
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(REISSUE_REASON_DUPLICATE));
        assertTrue(response.contains(REISSUE_ORIGINAL_ISSUE_DATE));
    }

    @Test
    public void verifySolicitorGenerateGrantDraftReissueOxford() throws IOException {
        final String payload = replaceAllInString(getJsonFromFile(SOL_PAYLOAD_REISSUE_CTSC),
            "\"registryLocation\": \"ctsc\"", "\"registryLocation\": \"Oxford\"");
        final String response = generateReissueGrantDraftDocumentFromPayload(payload);
        assertTrue(response.contains(OXFORD_REGISTRY_ADDRESS));
        assertTrue(response.contains(REISSUE_REASON_DUPLICATE));
        assertTrue(response.contains(REISSUE_ORIGINAL_ISSUE_DATE));
    }

    @Test
    public void verifySuccessForGetAdmonWillGrantForCardiff() throws IOException {
        final CaseData caseData = CaseData.builder().build();

        final String response = getFirstProbateDocumentsText(DEFAULT_ADMON_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("admonWillGrantForCardiffResponse.txt"));
        expectedText = expectedText.replace(NOVEMBER_2020, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetAdmonWillGrantDraftForCardiff() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(DEFAULT_ADMON_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("admonWillGrantDraftForCardiffResponse.txt"));
        expectedText = expectedText.replace(NOVEMBER_2020, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGoPChangesForGopGenerate() throws IOException {
        final String response = generateGrantDocument(TRUST_CORPS_GOP_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains("Trust Corporation Name 1 High St"));
    }

    @Test
    public void verifySuccessForDigitalGrantDraftAddresses() throws IOException {
        final String response = generateGrantDocument(CW_PART_SUCC, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(PROBATE_PRACTITIONER_123_LONDON_LONDON));
    }

    @Test
    public void verifySuccessForDigitalGrantAddresses() throws IOException {
        final String response = generateGrantDocument(CW_PART_SUCC, GENERATE_GRANT);

        assertTrue(response.contains(PROBATE_PRACTITIONER_123_LONDON_LONDON));
    }

    @Test
    public void verifySuccessForDigitalGrantAddressesFirm() throws IOException {
        final String response = generateGrantDocument(CW_PART, GENERATE_GRANT);

        assertTrue(response.contains(PROBATE_PRACTITIONER_123_LONDON_LONDON));
    }

    @Test
    public void verifySuccessForDigitalGrantDraftAddressesFirm() throws IOException {
        final String response = generateGrantDocument(CW_PART, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(PROBATE_PRACTITIONER_123_LONDON_LONDON));
    }

    @Test
    public void verifySuccessForGoPChangesForGopGenerateDraft() throws IOException {
        final String response = generateGrantDocument(TRUST_CORPS_GOP_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains("Trust Corporation Name 1 High St"));
    }

    @Test
    public void verifySuccessForGenerateDraftTCPartnerFirmPowerReserved() throws IOException {
        final String responseWithSinglePowerReserved = generateGrantDocument(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD,
            GENERATE_GRANT_DRAFT);
        assertTrue(responseWithSinglePowerReserved.contains(POWER_RESERVED_SINGLE));

        final String payload = replaceAllInString(utils.getJsonFromFile(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD),
            MORE_PARTNERS_HOLDING_POWER_RESERVED_NO,
            MORE_PARTNERS_HOLDING_POWER_RESERVED_YES);
        final String responseWithMultiplePowerReserved = generateGrantDocumentFromPayload(payload,
            GENERATE_GRANT_DRAFT);
        assertTrue(responseWithMultiplePowerReserved.contains(POWER_RESERVED));
    }

    @Test
    public void verifySuccessForGenerateGrantTCPartnerFirmPowerReserved() throws IOException {
        final String responseWithSinglePowerReserved = generateGrantDocument(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD,
            GENERATE_GRANT);
        assertTrue(responseWithSinglePowerReserved.contains(POWER_RESERVED_SINGLE));

        final String payload = replaceAllInString(utils.getJsonFromFile(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD),
            MORE_PARTNERS_HOLDING_POWER_RESERVED_NO,
            MORE_PARTNERS_HOLDING_POWER_RESERVED_YES);
        final String responseWithMultiplePowerReserved = generateGrantDocumentFromPayload(payload,
            GENERATE_GRANT);
        assertTrue(responseWithMultiplePowerReserved.contains(POWER_RESERVED));
    }

    @Test
    public void verifySuccessForGenerateGrantReissueDraftTCPartnerFirmPowerReserved() throws IOException {
        final String responseWithSinglePowerReserved = generateGrantDocument(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD,
            GENERATE_GRANT_DRAFT_REISSUE);
        assertTrue(responseWithSinglePowerReserved.contains(POWER_RESERVED_SINGLE));

        final String payload = replaceAllInString(utils.getJsonFromFile(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD),
            MORE_PARTNERS_HOLDING_POWER_RESERVED_NO,
            MORE_PARTNERS_HOLDING_POWER_RESERVED_YES);
        final String responseWithMultiplePowerReserved = generateGrantDocumentFromPayload(payload,
            GENERATE_GRANT_DRAFT_REISSUE);
        assertTrue(responseWithMultiplePowerReserved.contains(POWER_RESERVED));
    }

    @Test
    public void verifySuccessForGenerateGrantReissueTCPartnerFirmPowerReserved() throws IOException {
        final String responseWithSinglePowerReserved = generateGrantDocument(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD,
            "/document/generate-grant-reissue");
        assertTrue(responseWithSinglePowerReserved.contains(POWER_RESERVED_SINGLE));

        final String payload = replaceAllInString(utils.getJsonFromFile(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD),
            MORE_PARTNERS_HOLDING_POWER_RESERVED_NO,
            MORE_PARTNERS_HOLDING_POWER_RESERVED_YES);
        final String responseWithMultiplePowerReserved = generateGrantDocumentFromPayload(payload,
            "/document/generate-grant-reissue");
        assertTrue(responseWithMultiplePowerReserved.contains(POWER_RESERVED));
    }

    @Test
    public void verifySuccessForGetIntestacyGrantForCardiff() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(DEFAULT_INTESTACY_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("intestacyGrantForCardiffResponse.txt"));
        expectedText = expectedText.replace(NOVEMBER_2020, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetIntestacyGrantDraftForCardiff() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(DEFAULT_INTESTACY_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("intestacyGrantDraftForCardiffResponse.txt"));
        expectedText = expectedText.replace(NOVEMBER_2020, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetGopGrantForCardiff() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(DEFAULT_GOP_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("gopGrantForCardiffResponse.txt"));
        expectedText = expectedText.replace(JULY_2021, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetGopGrantDraftForCardiff() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(DEFAULT_GOP_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("gopGrantDraftForCardiffResponse.txt"));
        expectedText = expectedText.replace(JULY_2021, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForOxfordGopGenerateGrant() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(OXFORD_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("oxfordGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replace(JULY_2021, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForManchesterGopGenerateGrant() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(MANCHESTER_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("manchesterGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replace(JULY_2021, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLeedsGopGenerateGrant() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(LEEDS_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("leedsGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replace(JULY_2021, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLiverpoolGopGenerateGrant() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(LIVERPOOL_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("liverpoolGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replace(JULY_2021, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForBrightonGopGenerateGrant() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(BRIGHTON_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("brightonGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replace(JULY_2021, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLondonGopGenerateGrant() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(LONDON_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("londonGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replace(JULY_2021, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForNewcastleGopGenerateGrant() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(NEWCASTLE_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("newcastleGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replace(JULY_2021, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForWinchesterGopGenerateGrant() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(WINCHESTER_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("winchesterGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replace(JULY_2021, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForBristolGopGenerateGrant() throws IOException {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(BRISTOL_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("bristolGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replace(JULY_2021, caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithSingleExecutorSols() throws IOException {

        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(DIED_ON_OR_SINCE));

        assertFalse(response.contains(PA));
        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(POWER_RESERVED));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithSingleExecutorPA() throws IOException {
        final String response = getFirstProbateDocumentsText(DEFAULT_PA_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PA));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(PRESUMED_DIED_ON));

        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(POWER_RESERVED));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantWithMultipleExecutorsSolTc() throws IOException {
        final String response = generateGrantDocument(MULTI_EXEC_TC_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));

        assertTrue(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(MULTI_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(MULTI_EXEC_TC_TRUST_CORP_DETAILS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithMultipleExecutorsSolTc() throws IOException {
        final String response = generateGrantDocument(MULTI_EXEC_TC_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));

        assertTrue(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(MULTI_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(MULTI_EXEC_TC_TRUST_CORP_DETAILS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithNotNamedSolTc() throws IOException {
        final String response = generateGrantDocument(NOT_NAMED_TC_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));
        assertFalse(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(MULTI_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_MULTI));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithNotNamedSolTc() throws IOException {
        final String response = generateGrantDocument(NOT_NAMED_TC_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));
        assertFalse(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(MULTI_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_MULTI));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithNotNamedSolTcTcExec() throws IOException {
        final String response = generateGrantDocument(NOT_NAMED_TC_TC_EXEC_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));
        assertFalse(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(SINGLE_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_SINGLE));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithNotNamedSolTcTcExec() throws IOException {
        final String response = generateGrantDocument(NOT_NAMED_TC_TC_EXEC_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS_HARLOW));
        assertTrue(response.contains(GOP));
        assertFalse(response.contains(MULTI_EXEC_TC_PROB_PRACTITIONER));
        assertTrue(response.contains(MULTI_EXEC_TC_DECEASED));
        assertTrue(response.contains(SINGLE_EXEC_TC_AMINISTRATION_STATEMENT));
        assertTrue(response.contains(NOT_NAMED_SOL_TC_TRUST_CORP_DETAILS_SINGLE));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithNotNamedNotApplyingSolTcTcExec() throws IOException {
        final String payload = replaceAllInString(utils.getJsonFromFile(NOT_NAMED_TC_PAYLOAD),
            "\"solsSolicitorIsApplying\": \"Yes\"", "\"solsSolicitorIsApplying\": \"No\"");
        final String response = generateGrantDocumentFromPayload(payload, GENERATE_GRANT);
        assertTrue(response.contains(FRAGMENT_WITH_NO_MULTIPLE_ANDS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithNotNamedNotApplyingSolTcTcExec() throws IOException {
        final String payload = replaceAllInString(utils.getJsonFromFile(NOT_NAMED_TC_PAYLOAD),
            "\"solsSolicitorIsApplying\": \"Yes\"", "\"solsSolicitorIsApplying\": \"No\"");
        final String response = generateGrantDocumentFromPayload(payload, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(FRAGMENT_WITH_NO_MULTIPLE_ANDS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithNotNamedSolTcPowerReserved() throws IOException {
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
    public void verifySuccessForGetDigitalGrantDraftWithNotNamedSolTcPowerReserved() throws IOException {
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
    public void verifySuccessForGetDigitalGrantWithMultipleExecutorsSOls() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExecutors.json",
            GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON_OR_ABOUT));

        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(PA));
        assertFalse(response.contains(POWER_RESERVED));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithMultipleExecutorsFirstLastName() throws IOException {
        final String response =
                getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExecutorsFLName.json",
                GENERATE_GRANT);

        assertTrue(response.contains(GOP));
        assertTrue(response.contains(ADD_EXEC_ONE_FIRST_NAME));
        assertTrue(response.contains(ADD_EXEC_ONE_LAST_NAME));
        assertTrue(response.contains(ADD_EXEC_TWO_FIRST_NAME));
        assertTrue(response.contains(ADD_EXEC_TWO_LAST_NAME));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithPowerReservedMultipleSOls() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsPowerReservedMultiple.json",
            GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON));

        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(PA));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithPowerReservedSingleSOls() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsPowerReserved.json",
            GENERATE_GRANT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON));

        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(PA));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantWithGrantInfoSOls() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsGrantInfo.json",
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

        assertFalse(response.contains(POWER_RESERVED));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));
        assertFalse(response.contains(PA));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithSingleExecutorSols() throws IOException {
        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(DIED_ON_OR_SINCE));

        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(POWER_RESERVED));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithSingleExecutorPA() throws IOException {
        final String response = getFirstProbateDocumentsText(DEFAULT_PA_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PA));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(PRESUMED_DIED_ON));

        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(POWER_RESERVED));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithMultipleExecutorsSOls() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExecutors.json",
            GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON_OR_ABOUT));

        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(PA));
        assertFalse(response.contains(POWER_RESERVED));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithMultipleExecutorsFirstLastName() throws IOException {
        final String response =
                getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExecutorsFLName.json",
                GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_ONE_FIRST_NAME));
        assertTrue(response.contains(ADD_EXEC_ONE_LAST_NAME));
        assertTrue(response.contains(ADD_EXEC_TWO_FIRST_NAME));
        assertTrue(response.contains(ADD_EXEC_TWO_LAST_NAME));
    }

    @Test
    public void verifySuccessForDigitalGrantReissueWithMultipleExecutorsFirstLastName()
            throws IOException {
        String response =
                generateGrantDocument("solicitorPayloadNotificationsMultipleExecutorsFLName.json",
                        GENERATE_GRANT);
        assertTrue(response.contains(ADD_EXEC_ONE_FIRST_NAME));

        final String payload =
                replaceAllInString(
                        getJsonFromFile("solicitorPayloadNotificationsMultipleExecutorsFLName.json"),
                 "\"case_data\": {", "\"case_data\": {\n      \"schemaVersion\": \"2.0.0\",");
        response = generateReissueGrantDraftDocumentFromPayload(payload);
        assertTrue(response.contains(ADD_EXEC_ONE_FIRST_NAME));
        assertTrue(response.contains(ADD_EXEC_ONE_LAST_NAME));

        response = generateGrantDocumentFromPayload(payload, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(ADD_EXEC_TWO_FIRST_NAME));
        assertTrue(response.contains(ADD_EXEC_TWO_LAST_NAME));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithPowerReservedMultipleSOls() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsPowerReservedMultiple.json",
            GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON));

        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(PA));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithPowerReservedSingleSOls() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsPowerReserved.json",
            GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(DIED_ON));

        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(PA));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithGrantInfoSOls() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsGrantInfo.json",
            GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(LIMITATION_MESSAGE));
        assertTrue(response.contains(ADMIN_MESSAGE));
        assertTrue(response.contains(WILL_MESSAGE));
        assertTrue(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(response.contains(SOLICITOR_INFO1));
        assertTrue(response.contains(SOLICITOR_INFO2));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(TITLE));
        assertTrue(response.contains(HONOURS));
        assertTrue(response.contains(DIED_ON));

        assertFalse(response.contains(POWER_RESERVED));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));
        assertFalse(response.contains(PA));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftDateFormat() throws IOException {
        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(DOD));
        assertTrue(response.contains(GOP));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDateFormat() throws IOException {
        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(DOD));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftMoneyFormat() throws IOException {
        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(IHT_GROSS));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(IHT_NET));
        assertTrue(response.contains(GOP));

    }

    @Test
    public void verifySuccessForGetDigitalGrantMoneyFormat() throws IOException {
        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(IHT_GROSS));
        assertTrue(response.contains(IHT_NET));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantMoneyFormatWithPence() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsIHTCurrencyFormat.json",
            GENERATE_GRANT);

        assertTrue(response.contains(IHT_GROSS_PENCE));
        assertTrue(response.contains(IHT_NET_PENCE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftMoneyFormatWithPence() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsIHTCurrencyFormat.json",
            GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(IHT_GROSS_PENCE));
        assertTrue(response.contains(IHT_NET_PENCE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantApplyingButNotSet() throws IOException {

        final String payload = replaceAllInString(utils.getJsonFromFile(LONDON_GOP_PAYLOAD),
            "\"primaryApplicantIsApplying\": \"Yes\",", "");
        final String response =
            generateGrantDocumentFromPayload(payload,
                GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(PRIMARY_APPLICANT));
    }


    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantNotApplying() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExsPANotApplying.json",
                GENERATE_GRANT_DRAFT);

        assertFalse(response.contains(PRIMARY_APPLICANT));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(DIED_ON_OR_BEFORE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantPrimaryApplicantNotApplying() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExsPANotApplying.json",
                GENERATE_GRANT);

        assertFalse(response.contains(PRIMARY_APPLICANT));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantNotApplyingPowerReserved() throws IOException {
        final String response =
            getFirstProbateDocumentsText(
                "solicitorPayloadNotificationsMultipleExsPANotApplyingPowerReserved.json",
                GENERATE_GRANT_DRAFT);

        assertFalse(response.contains(PRIMARY_APPLICANT));
        assertFalse(response.contains(POWER_RESERVED));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantPrimaryApplicantNotApplyingPowerReserved() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExsPANotApplyingPowerReserved.json",
                GENERATE_GRANT);

        assertFalse(response.contains(PRIMARY_APPLICANT));
        assertFalse(response.contains(POWER_RESERVED));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantNotApplyingPowerReservedMultiple()
        throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsPANotApplyingPowerReservedMultiple.json",
                GENERATE_GRANT_DRAFT);

        assertFalse(response.contains(PRIMARY_APPLICANT));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(LONDON_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantPrimaryApplicantNotApplyingPowerReservedMultiple() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsPANotApplyingPowerReservedMultiple.json",
                GENERATE_GRANT);

        assertFalse(response.contains(PRIMARY_APPLICANT));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_ONE_PRIMARY_APPLICANT));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(LONDON_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantPartialSolsAddress() throws IOException {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsPartialAddress.json", GENERATE_GRANT);

        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(SOLICITOR_INFO3));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(DIED_ON_OR_SINCE));

        assertFalse(response.contains(PA));
        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(POWER_RESERVED));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDomiciledUK() throws IOException {
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsPartialAddress.json",
            GENERATE_GRANT);

        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(SOLICITOR_INFO3));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(DIED_ON_OR_SINCE));
        assertTrue(response.contains(UK));
        assertTrue(response.contains(ENGLAND_AND_WALES));

        assertFalse(response.contains(PA));
        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(POWER_RESERVED));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));
    }


    @Test
    public void verifySuccessForGetDigitalGrantDomiciledForeignDomicile() throws IOException {
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsForeignDomicile.json",
            GENERATE_GRANT);

        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(SOLICITOR_INFO3));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(DIED_ON_OR_SINCE));
        assertTrue(response.contains(ENGLAND_AND_WALES));
        assertTrue(response.contains(SPAIN));

        assertFalse(response.contains(UK));
        assertFalse(response.contains(PA));
        assertFalse(response.contains(WILL_MESSAGE));
        assertFalse(response.contains(ADMIN_MESSAGE));
        assertFalse(response.contains(LIMITATION_MESSAGE));
        assertFalse(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertFalse(response.contains(POWER_RESERVED));
        assertFalse(response.contains(POWER_RESERVED_SINGLE));
        assertFalse(response.contains(TITLE));
        assertFalse(response.contains(HONOURS));
    }

    @Test
    public void verifyWillLodgementDepositReceiptShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess(DEFAULT_WILL_PAYLOAD, GENERATE_DEPOSIT_RECEIPT);
    }

    private String generateGrantDocument(String jsonFileName, String path) throws IOException {
        return generateDocument(jsonFileName, path, GRANT_DOC_NAME);
    }

    private String generateGrantDocumentFromPayload(String payload, String path) {
        return generateDocumentFromPayload(payload, path, GRANT_DOC_NAME);
    }

    private String generateReissueGrantDraftDocument(String jsonFileName) throws IOException {
        return generateDocument(jsonFileName, GENERATE_GRANT_DRAFT_REISSUE, GRANT_DOC_NAME);
    }

    private String generateReissueGrantDraftDocumentFromPayload(String payload) {
        return generateDocumentFromPayload(payload, GENERATE_GRANT_DRAFT_REISSUE, GRANT_DOC_NAME);
    }
}
