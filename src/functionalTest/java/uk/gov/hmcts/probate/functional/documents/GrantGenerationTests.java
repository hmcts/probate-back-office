package uk.gov.hmcts.probate.functional.documents;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.LocalDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP;

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
    private static final String POSTCODE = "CM20 9QE";

    private static final String HMCTS_VALUE = "HMCTS";

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

    private final String deceasedDomiciledInEngWalesText = "The application has stated that the gross value"
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
    public void verifyGenerateAllEnglishGopPersonalGrantTypesWhenDeceasedDomiciledInEnglandOrWales() {
        final String gopPayload = "/default/gop/personal/";

        String response = generateGrantDocument(gopPayload + "gop.json", GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocument(gopPayload + "gop.json", GENERATE_GRANT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocument(gopPayload + "gopReissue.json", GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));
    }

    @Test
    public void verifyGenerateAllEnglishGopPersonalGrantTypesWhenDeceasedNotDomiciledInEnglandOrWales() {
        final String gopPayload = "/default/gop/personal/";

        final String expectedText = replaceAllInString(deceasedDomiciledInEngWalesText, "the United Kingdom",
            "England and Wales");

        final String payloadIssue = replaceAllInString(getJsonFromFile(gopPayload + "gop.json"),
            "\"deceasedDomicileInEngWales\": \"Yes\"", "\"deceasedDomicileInEngWales\": \"No\"");

        String response = generateGrantDocumentFromPayload(payloadIssue, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(payloadIssue, GENERATE_GRANT);
        assertTrue(response.contains(expectedText));

        final String payloadReissue = replaceAllInString(getJsonFromFile(gopPayload + "gopReissue.json"),
            "\"deceasedDomicileInEngWales\": \"Yes\"", "\"deceasedDomicileInEngWales\": \"No\"");

        response = generateGrantDocumentFromPayload(payloadReissue, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyGenerateAllEnglishGopSolicitorGrantTypesWhenDeceasedDomiciledInEnglandOrWales() {
        String response = generateGrantDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocument(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        final String payload = replaceAllInString(getJsonFromFile(DEFAULT_SOLS_PAYLOAD),
            "\"case_data\": {", "\"case_data\": {\n      \"schemaVersion\": \"2.0.0\",");
        response = generateReissueGrantDraftDocumentFromPayload(payload);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocumentFromPayload(payload, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));
    }

    @Test
    public void verifyGenerateAllEnglishGopSolicitorGrantTypesWhenDeceasedNotDomiciledInEnglandOrWales() {
        final String expectedText = replaceAllInString(deceasedDomiciledInEngWalesText, "the United Kingdom",
            "England and Wales");

        String payload = replaceAllInString(getJsonFromFile(DEFAULT_SOLS_PAYLOAD),
            "\"deceasedDomicileInEngWales\": \"Yes\"", "\"deceasedDomicileInEngWales\": \"No\"");

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
    public void verifyGenerateAllEnglishAdmonWillPersonalGrantTypesWhenDeceasedDomiciledInEnglandOrWales() {
        String admonWillPayload = "/default/admonwill/personal/";

        String response = generateGrantDocument(admonWillPayload + "admonWill.json", GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocument(admonWillPayload + "admonWill.json", GENERATE_GRANT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocument(admonWillPayload + "admonWillReissue.json", GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));
    }

    @Test
    public void verifyGenerateAllEnglishAdmonWillPersonalGrantTypesWhenDeceasedNotDomiciledInEnglandOrWales() {
        String admonWillPayload = "/default/admonwill/personal/";

        String admonWillPayloadIssue = replaceAllInString(
            getJsonFromFile(admonWillPayload + "admonWill.json"),
            "\"deceasedDomicileInEngWales\": \"Yes\"", "\"deceasedDomicileInEngWales\": \"No\"");

        String expectedText = replaceAllInString(deceasedDomiciledInEngWalesText,
            "the United Kingdom", "England and Wales");

        String response = generateGrantDocumentFromPayload(admonWillPayloadIssue, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(admonWillPayloadIssue, GENERATE_GRANT);
        assertTrue(response.contains(expectedText));

        String admonWillPayloadReissue = replaceAllInString(
            getJsonFromFile(admonWillPayload + "admonWillReissue.json"),
            "\"deceasedDomicileInEngWales\": \"Yes\"", "\"deceasedDomicileInEngWales\": \"No\"");

        response = generateGrantDocumentFromPayload(admonWillPayloadReissue, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyGenerateAllEnglishAdmonWillSolicitorGrantTypesWhenDeceasedDomiciledInEnglandOrWales() {
        String admonWillPayload = "/default/admonwill/solicitor/";

        String response = generateGrantDocument(admonWillPayload + "admonWill.json", GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocument(admonWillPayload + "admonWill.json", GENERATE_GRANT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocument(admonWillPayload + "admonWillReissue.json", GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));
    }

    @Test
    public void verifyGenerateAllEnglishAdmonWillSolicitorGrantTypesWhenDeceasedNotDomiciledInEnglandOrWales() {
        String admonWillPayload = "/default/admonwill/solicitor/";

        String admonWillPayloadIssue = replaceAllInString(
            getJsonFromFile(admonWillPayload + "admonWill.json"),
            "\"deceasedDomicileInEngWales\": \"Yes\"", "\"deceasedDomicileInEngWales\": \"No\"");

        String expectedText = replaceAllInString(deceasedDomiciledInEngWalesText,
            "the United Kingdom", "England and Wales");

        String response = generateGrantDocumentFromPayload(admonWillPayloadIssue, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(admonWillPayloadIssue, GENERATE_GRANT);
        assertTrue(response.contains(expectedText));

        String admonWillPayloadReissue = replaceAllInString(
            getJsonFromFile(admonWillPayload + "admonWillReissue.json"),
            "\"deceasedDomicileInEngWales\": \"Yes\"", "\"deceasedDomicileInEngWales\": \"No\"");

        response = generateGrantDocumentFromPayload(admonWillPayloadReissue, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyGenerateAllEnglishIntestacyPersonalGrantTypesWhenDeceasedDomiciledInEnglandOrWales() {
        String intestacyPayload = "/default/intestacy/personal/";

        String response = generateGrantDocument(intestacyPayload + "intestacy.json", GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocument(intestacyPayload + "intestacy.json", GENERATE_GRANT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocument(intestacyPayload + "intestacyReissue.json", GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));
    }

    @Test
    public void verifyGenerateAllEnglishIntestacyPersonalGrantTypesWhenDeceasedNotDomiciledInEnglandOrWales() {
        String intestacyPayload = "/default/intestacy/personal/";

        String intestacyPayloadIssue = replaceAllInString(
            getJsonFromFile(intestacyPayload + "intestacy.json"),
            "\"deceasedDomicileInEngWales\": \"Yes\"", "\"deceasedDomicileInEngWales\": \"No\"");

        String expectedText = replaceAllInString(deceasedDomiciledInEngWalesText,
            "the United Kingdom", "England and Wales");

        String response = generateGrantDocumentFromPayload(intestacyPayloadIssue, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(intestacyPayloadIssue, GENERATE_GRANT);
        assertTrue(response.contains(expectedText));

        String intestacyPayloadReissue = replaceAllInString(
            getJsonFromFile(intestacyPayload + "intestacyReissue.json"),
            "\"deceasedDomicileInEngWales\": \"Yes\"", "\"deceasedDomicileInEngWales\": \"No\"");

        response = generateGrantDocumentFromPayload(intestacyPayloadReissue, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyGenerateAllEnglishIntestacySolicitorGrantTypesWhenDeceasedDomiciledInEnglandOrWales() {
        String intestacyPayload = "/default/intestacy/solicitor/";

        String response = generateGrantDocument(intestacyPayload + "intestacy.json", GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocument(intestacyPayload + "intestacy.json", GENERATE_GRANT);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));

        response = generateGrantDocument(intestacyPayload + "intestacyReissue.json", GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(deceasedDomiciledInEngWalesText));
    }

    @Test
    public void verifyGenerateAllEnglishIntestacySolicitorGrantTypesWhenDeceasedDomiciledNotInEnglandOrWales() {
        String intestacyPayload = "/default/intestacy/solicitor/";

        String intestacyPayloadIssue = replaceAllInString(
            getJsonFromFile(intestacyPayload + "intestacy.json"),
            "\"deceasedDomicileInEngWales\": \"Yes\"", "\"deceasedDomicileInEngWales\": \"No\"");

        String expectedText = replaceAllInString(deceasedDomiciledInEngWalesText,
            "the United Kingdom", "England and Wales");

        String response = generateGrantDocumentFromPayload(intestacyPayloadIssue, GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(expectedText));

        response = generateGrantDocumentFromPayload(intestacyPayloadIssue, GENERATE_GRANT);
        assertTrue(response.contains(expectedText));

        String intestacyPayloadReissue = replaceAllInString(
            getJsonFromFile(intestacyPayload + "intestacyReissue.json"),
            "\"deceasedDomicileInEngWales\": \"Yes\"", "\"deceasedDomicileInEngWales\": \"No\"");

        response = generateGrantDocumentFromPayload(intestacyPayloadReissue, GENERATE_GRANT_REISSUE);
        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTrustCorpsShouldReturnOkResponseCode() {
        validatePostSuccess(TRUST_CORPS_GOP_PAYLOAD, GENERATE_GRANT);
    }

    @Test
    public void verifySolicitorGenerateGrantDraftReissueCtsc() {
        final String response = generateReissueGrantDraftDocument(SOL_PAYLOAD_REISSUE_CTSC);
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(REISSUE_REASON_DUPLICATE));
        assertTrue(response.contains(REISSUE_ORIGINAL_ISSUE_DATE));
    }

    @Test
    public void verifySolicitorGenerateGrantDraftReissueOxford() {
        final String payload = replaceAllInString(getJsonFromFile(SOL_PAYLOAD_REISSUE_CTSC),
            "\"registryLocation\": \"ctsc\"", "\"registryLocation\": \"Oxford\"");
        final String response = generateReissueGrantDraftDocumentFromPayload(payload);
        assertTrue(response.contains(OXFORD_REGISTRY_ADDRESS));
        assertTrue(response.contains(REISSUE_REASON_DUPLICATE));
        assertTrue(response.contains(REISSUE_ORIGINAL_ISSUE_DATE));
    }

    @Test
    public void verifySuccessForGetAdmonWillGrantForCardiff() {
        final CaseData caseData = CaseData.builder().build();

        final String response = getFirstProbateDocumentsText(DEFAULT_ADMON_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("admonWillGrantForCardiffResponse.txt"));
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetAdmonWillGrantDraftForCardiff() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(DEFAULT_ADMON_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

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
    public void verifySuccessForGenerateDraftTCPartnerFirmPowerReserved() {
        final String responseWithSinglePowerReserved = generateGrantDocument(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD,
            GENERATE_GRANT_DRAFT);
        assertTrue(responseWithSinglePowerReserved.contains("Power reserved to another Executor"));

        final String payload = replaceAllInString(utils.getJsonFromFile(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD),
            "\"morePartnersHoldingPowerReserved\": \"No\"",
            "\"morePartnersHoldingPowerReserved\": \"Yes\"");
        final String responseWithMultiplePowerReserved = generateGrantDocumentFromPayload(payload,
            GENERATE_GRANT_DRAFT);
        assertTrue(responseWithMultiplePowerReserved.contains("Power reserved to other Executors"));
    }

    @Test
    public void verifySuccessForGenerateGrantTCPartnerFirmPowerReserved() {
        final String responseWithSinglePowerReserved = generateGrantDocument(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD,
            GENERATE_GRANT);
        assertTrue(responseWithSinglePowerReserved.contains("Power reserved to another Executor"));

        final String payload = replaceAllInString(utils.getJsonFromFile(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD),
            "\"morePartnersHoldingPowerReserved\": \"No\"",
            "\"morePartnersHoldingPowerReserved\": \"Yes\"");
        final String responseWithMultiplePowerReserved = generateGrantDocumentFromPayload(payload,
            GENERATE_GRANT);
        assertTrue(responseWithMultiplePowerReserved.contains("Power reserved to other Executors"));
    }

    @Test
    public void verifySuccessForGenerateGrantReissueDraftTCPartnerFirmPowerReserved() {
        final String responseWithSinglePowerReserved = generateGrantDocument(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD,
            GENERATE_GRANT_DRAFT_REISSUE);
        assertTrue(responseWithSinglePowerReserved.contains("Power reserved to another Executor"));

        final String payload = replaceAllInString(utils.getJsonFromFile(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD),
            "\"morePartnersHoldingPowerReserved\": \"No\"",
            "\"morePartnersHoldingPowerReserved\": \"Yes\"");
        final String responseWithMultiplePowerReserved = generateGrantDocumentFromPayload(payload,
            GENERATE_GRANT_DRAFT_REISSUE);
        assertTrue(responseWithMultiplePowerReserved.contains("Power reserved to other Executors"));
    }

    @Test
    public void verifySuccessForGenerateGrantReissueTCPartnerFirmPowerReserved() {
        final String responseWithSinglePowerReserved = generateGrantDocument(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD,
            "/document/generate-grant-reissue");
        assertTrue(responseWithSinglePowerReserved.contains("Power reserved to another Executor"));

        final String payload = replaceAllInString(utils.getJsonFromFile(PARTNERS_FIRM_POWER_RESERVED_PAYLOAD),
            "\"morePartnersHoldingPowerReserved\": \"No\"",
            "\"morePartnersHoldingPowerReserved\": \"Yes\"");
        final String responseWithMultiplePowerReserved = generateGrantDocumentFromPayload(payload,
            "/document/generate-grant-reissue");
        assertTrue(responseWithMultiplePowerReserved.contains("Power reserved to other Executors"));
    }

    @Test
    public void verifySuccessForGetIntestacyGrantForCardiff() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(DEFAULT_INTESTACY_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("intestacyGrantForCardiffResponse.txt"));
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetIntestacyGrantDraftForCardiff() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(DEFAULT_INTESTACY_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("intestacyGrantDraftForCardiffResponse.txt"));
        expectedText = expectedText.replaceAll("18th November 2020", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetGopGrantForCardiff() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(DEFAULT_GOP_CARDIFF_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("gopGrantForCardiffResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetGopGrantDraftForCardiff() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(DEFAULT_GOP_CARDIFF_PAYLOAD, GENERATE_GRANT_DRAFT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("gopGrantDraftForCardiffResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForOxfordGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(OXFORD_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("oxfordGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForManchesterGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(MANCHESTER_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("manchesterGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLeedsGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(LEEDS_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("leedsGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLiverpoolGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(LIVERPOOL_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("liverpoolGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForBrightonGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(BRIGHTON_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("brightonGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForLondonGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(LONDON_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("londonGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForNewcastleGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(NEWCASTLE_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("newcastleGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForWinchesterGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(WINCHESTER_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("winchesterGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifyTelephoneForBristolGopGenerateGrant() {
        final CaseData caseData = CaseData.builder().build();
        final String response = getFirstProbateDocumentsText(BRISTOL_GOP_PAYLOAD, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile("bristolGopGenerateGrantResponse.txt"));
        expectedText = expectedText.replaceAll("5th July 2021", caseData.convertDate(LocalDate.now()));

        assertTrue(response.contains(expectedText));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithSingleExecutorSols() {
        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

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
        final String response = getFirstProbateDocumentsText(DEFAULT_PA_PAYLOAD, GENERATE_GRANT);

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
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExecutors.json",
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
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsPowerReservedMultiple.json",
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
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsPowerReserved.json",
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
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsGrantInfo.json",
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
        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

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
        final String response = getFirstProbateDocumentsText(DEFAULT_PA_PAYLOAD, GENERATE_GRANT_DRAFT);

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
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExecutors.json",
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
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsPowerReservedMultiple.json",
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
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsPowerReserved.json",
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
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsGrantInfo.json",
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
        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(DOD));
        assertTrue(response.contains(GOP));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDateFormat() {
        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(DOD));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(GOP));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftMoneyFormat() {
        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(IHT_GROSS));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
        assertTrue(response.contains(IHT_NET));
        assertTrue(response.contains(GOP));

    }

    @Test
    public void verifySuccessForGetDigitalGrantMoneyFormat() {
        final String response = getFirstProbateDocumentsText(DEFAULT_SOLS_PAYLOAD, GENERATE_GRANT);

        assertTrue(response.contains(IHT_GROSS));
        assertTrue(response.contains(IHT_NET));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantMoneyFormatWithPence() {
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsIHTCurrencyFormat.json",
            GENERATE_GRANT);

        assertTrue(response.contains(IHT_GROSS_PENCE));
        assertTrue(response.contains(IHT_NET_PENCE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftMoneyFormatWithPence() {
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsIHTCurrencyFormat.json",
            GENERATE_GRANT_DRAFT);

        assertTrue(response.contains(IHT_GROSS_PENCE));
        assertTrue(response.contains(IHT_NET_PENCE));
        assertTrue(response.contains(GOP));
        assertTrue(response.contains(CTSC_REGISTRY_ADDRESS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantApplyingButNotSet() {

        final String payload = replaceAllInString(utils.getJsonFromFile(LONDON_GOP_PAYLOAD),
            "\"primaryApplicantIsApplying\": \"Yes\",", "");
        final String response =
            generateGrantDocumentFromPayload(payload,
                GENERATE_GRANT_DRAFT);
        assertTrue(response.contains(PRIMARY_APPLICANT));
    }


    @Test
    public void verifySuccessForGetDigitalGrantDraftPrimaryApplicantNotApplying() {
        final String response =
            getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExsPANotApplying.json",
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
            getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExsPANotApplying.json", GENERATE_GRANT);

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
            getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExsPANotApplyingPowerReserved.json",
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
            getFirstProbateDocumentsText("solicitorPayloadNotificationsMultipleExsPANotApplyingPowerReserved.json",
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
            getFirstProbateDocumentsText("solicitorPayloadNotificationsPANotApplyingPowerReservedMultiple.json",
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
            getFirstProbateDocumentsText("solicitorPayloadNotificationsPANotApplyingPowerReservedMultiple.json",
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
            getFirstProbateDocumentsText("solicitorPayloadNotificationsPartialAddress.json", GENERATE_GRANT);

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
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsPartialAddress.json",
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
        final String response = getFirstProbateDocumentsText("solicitorPayloadNotificationsForeignDomicile.json",
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

    //Ignored due to Docmosis not allowing screen readers as images overlay all text
    @Ignore
    @Test
    public void verifySuccessForDigitalGrantDraftReissueForDuplicateNotation() {
        final String response = getFirstProbateDocumentsText(DEFAULT_REISSUE_PAYLOAD, GENERATE_GRANT_DRAFT_REISSUE);
        assertTrue(response.contains(ENGLAND_AND_WALES));
        assertTrue(response.contains(CASE_REFERENCE));
        assertTrue(response.contains(DECEASED_DETAILS));
        assertTrue(response.contains(DECEASED_DOD));
        assertTrue(response.contains(HMCTS_VALUE));
        assertTrue(response.contains(POSTCODE));
    }

    @Test
    public void verifyGenerateSolsCoverSheetGopTcResolutionLodgedWithinApplication() {
        String payload = "/caseprogress/04e-caseCreated.json";
        String response = getDocumentTextAtPath(payload, VALIDATE_PROBATE_URL, "solsCoversheetDocument");
        assertTrue(response.contains(TC_RESOLUTION_LODGED_WITH_APP));

    }

    private String generateGrantDocument(String jsonFileName, String path) {
        return generateDocument(jsonFileName, path, GRANT_DOC_NAME);
    }

    private String generateGrantDocumentFromPayload(String payload, String path) {
        return generateDocumentFromPayload(payload, path, GRANT_DOC_NAME);
    }

    private String generateReissueGrantDraftDocument(String jsonFileName) {
        return generateDocument(jsonFileName, GENERATE_GRANT_DRAFT_REISSUE, GRANT_DOC_NAME);
    }

    private String generateReissueGrantDraftDocumentFromPayload(String payload) {
        return generateDocumentFromPayload(payload, GENERATE_GRANT_DRAFT_REISSUE, GRANT_DOC_NAME);
    }
}
