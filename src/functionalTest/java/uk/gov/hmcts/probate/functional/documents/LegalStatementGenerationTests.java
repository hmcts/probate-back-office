package uk.gov.hmcts.probate.functional.documents;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SerenityJUnit5Extension.class)
public class LegalStatementGenerationTests extends DocumentGenerationTestBase {

    // Legal statement fields
    private static final String DECLARATION_CIVIL_WORDING =
        "proceedings for contempt of court may be brought against the undersigned if it is found that the evidence "
            + "provided is deliberately untruthful or dishonest, as well as revocation of the grant";
    private static final String DECLARATION_CIVIL_WORDING_WELSH = "gellir dwyn achos dirmyg llys yn erbyn y sawl sy’n "
            + "llofnodi isod os canfyddir bod y dystiolaeth a ddarparwyd yn fwriadol anwir neu’n anonest, yn ogystal â "
            + "diddymu’r grant";
    private static final String CODICIL_DATES = " with codicil signed and dated 3rd March 2020, and codicil signed"
        + " and dated 5th March 2020, and codicil signed and dated 6th March 2020";
    private static final String DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC =
        "criminal proceedings for fraud may be brought against me if I am found to have been deliberately untruthful "
            + "or dishonest";
    private static final String LEGAL_STATEMENT = "Legal statement";
    private static final String LEGAL_STATEMENT_WELSH = "Datganiad cyfreithiol";
    private static final String AUTHORISED_SOLICITOR =
        "They have authorised Firm Name to sign a statement of truth on their behalf.";
    private static final String LEGAL_STATEMENT_DIED_ON = "died on";
    private static final String LEGAL_STATEMENT_GOP_WELSH = "grant profiant";
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
    private static final String LEGAL_STATEMENT_INTESTATE_WELSH = "heb ewyllys";
    private static final String LEGAL_STATEMENT_ADMON_WILL =
        "Administrators Applying for Letters of Administration (with will annexed)";
    private static final String LEGAL_STATEMENT_ADMON_WILL_WELSH =
            "Gweinyddwyr yn Gwneud Cais am Lythyrau Gweinyddu (pan fydd yna ewyllys)";
    private static final String FURTHER_EVIDENCE = "Further evidence";
    private static final String DOMICILITY_SENTENCE_UK = "The gross value for the estate in the United Kingdom amounts";
    private static final String DOMICILITY_SENTENCE_NON_UK = "The gross value for the estate in England and Wales";
    private static final String FIRM_AUTHORISATION = "They have authorised Firm Name to sign a statement";
    private static final String WILL_NO_CODICILS = "and is named in the will as";
    private static final String WILL_WITH_CODICIL =
            "which was settled previously to the death (and not by the will and codicil)";
    private static final String WILL_WITH__MULTIPLE_CODICILS =
            "which was settled previously to the death (and not by the will and codicils)";
    private static final String WILL_WITH_CODICIL_LETTERS_OF_ADMINISTRATION =
            "there are no beneficiaries under the age of 18 named in the will and codicil";
    private static final String SIGNED_DATE = ", signed and dated 1st January 2021";

    private static final String NO_DUPE_SOL_EXECUTORS = "solicitorPayloadLegalStatementNoDuplicateExecsCheck.json";
    private static final String SOLE_PRIN = "solicitorSoleFirmPartner.json";
    private static final String SOL_NOT_REPEATED = "solicitorPayloadTrustCorpsNoSolExecRepeat.json";
    private static final String EXEC_WITH_ALIAS = "solicitorExecutorAliasNameLegalStatement.json";
    private static final String PART_ALL_RENOUNCING = "solicitorPartAllRenouncing.json";
    private static final String PART_ALL_SUCC_RENOUNCING = "solicitorPartSuccAllRenouncing.json";
    private static final String PART_ALL_OTHERS_RENOUNCING = "solicitorPartOtherRenouncing.json";
    private static final String SOLE_PRIN_OTHER_PARTNERS = "solicitorSolPartner.json";
    private static final String SOLE_PRIN_OTHER_PARTNERS_SINGLE = "solicitorSolePrinSingleExec.json";
    private static final String DEFAULT_SOLS_PDF_PROBATE_PAYLOAD = "solicitorPDFPayloadProbateSingleExecutor.json";
    private static final String DEFAULT_SOLS_PDF_PROBATE_WELSH_PAYLOAD
            = "solicitorPDFPayloadProbateSingleExecutorLanguageWelsh.json";
    private static final String MULTIPLE_EXEC_SOLS_PDF_PROBATE_PAYLOAD =
        "solicitorPDFPayloadProbateMultipleExecutors.json";
    private static final String DEFAULT_SOLS_PDF_INTESTACY_PAYLOAD = "solicitorPDFPayloadIntestacy.json";
    private static final String DEFAULT_SOLS_PDF_INTESTACY_WELSH_PAYLOAD
            = "solicitorPDFPayloadIntestacyLanguageWelsh.json";
    private static final String CODICILS_SOLS_PDF_INTESTACY_PAYLOAD = "solicitorPDFIntestacyCodicils.json";
    private static final String DEFAULT_SOLS_PDF_ADMON_PAYLOAD = "solicitorPDFPayloadAdmonWill.json";
    private static final String DEFAULT_SOLS_PDF_ADMON_WELSH_PAYLOAD = "solicitorPDFPayloadAdmonWillLanguageWelsh.json";
    private static final String ADMON_PAYLOAD_WILL_AND_CODICILS_DATES =
        "solicitorPDFPayloadAdmonWillWithWillAndCodicilDates.json";
    private static final String ADMON_PAYLOAD_WILL_AND_ONE_CODICILS =
            "solicitorPDFPayloadAdmonWillWithOneCodicil.json";
    private static final String ADMON_PAYLOAD_WILL_AND_MULTIPLE_CODICILS =
            "solicitorPDFPayloadAdmonWillWithMultipleCodicils.json";
    private static final String GOP_PAYLOAD_MULTIPLE_CODICILS = "solicitorPDFPayloadProbateMultipleCodicils.json";
    private static final String GOP_PAYLOAD_SINGLE_CODICIL = "solicitorPDFPayloadProbateSingleCodicils.json";
    private static final String GOP_PAYLOAD_NO_CODICIL = "solicitorPDFPayloadProbate.json";
    private static final String GOP_PAYLOAD_TRUSTCORP_MULTIPLE_CODICILS =
        "solicitorPayloadTrustCorpsSchemaMultipleCodicil.json";
    private static final String GOP_PAYLOAD_TRUSTCORP_SINGLE_CODICIL =
        "solicitorPayloadTrustCorpsSchemaSingleCodicil.json";
    private static final String GOP_PAYLOAD_TRUSTCORP_NO_CODICIL = "solicitorPayloadTrustCorpsNoSolExecRepeat.json";
    private static final String SOT_DOC_NAME = "probateSotDocumentsGenerated[0].value.DocumentLink";
    private static final String GENERATE_LEGAL_STATEMENT = "/document/generate-sot";


    @Test
    void verifySuccessForGetPdfLegalStatementProbateWithSingleExecutorSols() throws IOException {
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
    void verifySuccessForGetPdfLegalStatementProbateForLanguagePreferenceBilingual() throws IOException {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_PROBATE_WELSH_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(LEGAL_STATEMENT_WELSH));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING_WELSH));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(LEGAL_STATEMENT_GOP));
        assertTrue(response.contains(LEGAL_STATEMENT_GOP_WELSH));
        assertTrue(response.contains(PRIMARY_APPLICANT_STATEMENT));

        assertTrue(!response.contains(DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC));
    }

    @Test
    void verifySuccessForGetPdfLegalStatementProbateWithMultipleExecutorSols() throws IOException {
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
    void verifySuccessForGetPdfLegalStatementIntestacyWithSingleExecutorSols() throws IOException {
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
    void verifySuccessForGetPdfLegalStatementIntestacyForLanguagePreferenceBilingual() throws IOException {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_INTESTACY_WELSH_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(LEGAL_STATEMENT_WELSH));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING_WELSH));
        assertTrue(response.contains(AUTHORISED_SOLICITOR));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(PRIMARY_APPLICANT_STATEMENT_OLD_SCHEMA));
        assertTrue(response.contains(LEGAL_STATEMENT_INTESTATE));
        assertTrue(response.contains(LEGAL_STATEMENT_INTESTATE_WELSH));
    }

    @Test
    void verifySuccessForGetPdfLegalStatementAdmonWillSols() throws IOException {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_ADMON_PAYLOAD, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(response.contains(AUTHORISED_SOLICITOR));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(LEGAL_STATEMENT_ADMON_WILL));

        assertTrue(!response.contains(DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC));
    }

    @Test
    void verifySuccessForGetPdfLegalStatementAdmonWillSolsForLanguagePreferenceBilingual() throws IOException {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_ADMON_WELSH_PAYLOAD, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(LEGAL_STATEMENT_WELSH));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING));
        assertTrue(response.contains(DECLARATION_CIVIL_WORDING_WELSH));
        assertTrue(response.contains(AUTHORISED_SOLICITOR));
        assertTrue(response.contains(LEGAL_STATEMENT_DIED_ON));
        assertTrue(response.contains(LEGAL_STATEMENT_ADMON_WILL));
        assertTrue(response.contains(LEGAL_STATEMENT_ADMON_WILL_WELSH));

        assertTrue(!response.contains(DECLARATION_CRIMINAL_WORDING_SINGLE_EXEC));
    }

    @Test
    void verifySuccessForFurtherEvidenceAdmonWill() throws IOException {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_ADMON_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(FURTHER_EVIDENCE));
        assertTrue(response.contains(DOMICILITY_SENTENCE_UK));
        assertTrue(response.contains(FIRM_AUTHORISATION));
        assertTrue(response.contains(WILL_NO_CODICILS));
    }

    @Test
    void verifySuccessForFurtherEvidenceAdmonWillWithWillDateAndCodicils() throws IOException {
        final String response = generateSotDocument(ADMON_PAYLOAD_WILL_AND_CODICILS_DATES, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(FURTHER_EVIDENCE));
        assertTrue(response.contains(DOMICILITY_SENTENCE_UK));
        assertTrue(response.contains(FIRM_AUTHORISATION));
        assertTrue(response.contains(WILL_NO_CODICILS));
        assertTrue(response.contains(SIGNED_DATE));
        assertTrue(response.contains(CODICIL_DATES));
    }

    @Test
    void verifySuccessForAdmonWillWithWillAndOneCodicilAdded() throws IOException {
        final String response = generateSotDocument(ADMON_PAYLOAD_WILL_AND_ONE_CODICILS, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DOMICILITY_SENTENCE_UK));
        assertTrue(response.contains(FIRM_AUTHORISATION));
        assertTrue(response.contains(WILL_WITH_CODICIL));
        assertTrue(response.contains(WILL_WITH_CODICIL_LETTERS_OF_ADMINISTRATION));
    }

    @Test
    void verifySuccessForAdmonWillWithWillAndMultipleCodicilAdded() throws IOException {
        final String response = generateSotDocument(ADMON_PAYLOAD_WILL_AND_MULTIPLE_CODICILS, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DOMICILITY_SENTENCE_UK));
        assertTrue(response.contains(FIRM_AUTHORISATION));
        assertTrue(response.contains(WILL_WITH__MULTIPLE_CODICILS));
    }

    @Test
    void verifySuccessForAdmonWillWithWillAndNoCodicil() throws IOException {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_ADMON_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(LEGAL_STATEMENT));
        assertTrue(response.contains(DOMICILITY_SENTENCE_UK));
        assertTrue(response.contains(FIRM_AUTHORISATION));
        assertTrue(!response.contains(WILL_WITH_CODICIL));
        assertTrue(!response.contains(WILL_WITH_CODICIL_LETTERS_OF_ADMINISTRATION));
    }

    @Test
    void verifySuccessForFurtherEvidenceIntestacy() throws IOException {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_INTESTACY_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(FURTHER_EVIDENCE));
    }

    @Test
    void verifySuccessForCodicilsIntestacy() throws IOException {
        final String response = generateSotDocument(CODICILS_SOLS_PDF_INTESTACY_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(DOMICILITY_SENTENCE_NON_UK));
        assertTrue(response.contains(FIRM_AUTHORISATION));
    }

    @Test
    void verifySuccessForFurtherEvidenceTrustCorpProbate() throws IOException {
        final String response = generateSotDocument(TRUST_CORPS_GOP_PAYLOAD, GENERATE_LEGAL_STATEMENT);

        assertTrue(response.contains(SIGNED_DATE));
        assertTrue(response.contains("1st January 2021"));
        assertTrue(response.contains(FURTHER_EVIDENCE));
    }

    @Test
    void verifySoTDomiciledInEnglandAndWales() throws IOException {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_ADMON_PAYLOAD, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Main Applicant of Test, Test, A1 2BC, UK make the following"
            + " statement:The person who diedDe Ceased, of Test, Test, Test, A1 2BC, was born on"
            + " 23/01/1998 and died on 23/01/2020, domiciled in England and Wales."));
    }

    @Test
    void verifySoTIndividualExecutorPowerReserved() throws IOException {
        final String response = generateSotDocument("solicitorExecutorsNotApplyingReasons.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("executor4_name, another executor named in the will,"
            + " is not making this application but reserves power to do so at a later date."));
    }

    @Test
    void verifySoTIndividualExecutorRenunciation() throws IOException {
        final String response = generateSotDocument("solicitorExecutorsNotApplyingReasons.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("executor3_name, another executor named in the will, "
            + "has renounced probate and letters "
            + "of administration with will annexed"));
    }

    @Test
    void verifySoTExecutorDiedBeforeAndAfterDeceased() throws IOException {
        final String response = generateSotDocument("solicitorExecutorsNotApplyingReasons.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
            "executor1_name, another executor named in the will, "
            + "has died in the lifetime of the deceased."));
        assertTrue(response.contains(
            "executor2_name, another executor named in the will, "
            + "has survived the deceased and died since."));
    }

    @Test
    void verifySoTExecutorLacksMentalCapacity() throws IOException {
        final String response = generateSotDocument("solicitorExecutorsNotApplyingReasons.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
            "executor5_name, another executor named in the will, lacks capacity to manage their"
                + " affairs under the Mental Capacity Act 2005 and is unable to act as an executor."));
    }

    @Test
    void verifySoTExecutorPowerReservedAndNoticeDispenseGiven() throws IOException {
        final String response = generateSotDocument("solicitorPayloadDispenseNotGiven.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Notice of this application has on the 10th October 2010 "
            + "been dispensed with under Rule 27(3) of the Non-Contentious Probate Rules "
            + "1987 to executor1_name to whom power is to be reserved."));
    }

    @Test
    void verifySoTExecutorConcurrentApplication() throws IOException {
        final String response = generateSotDocument("solicitorExecutorsNotApplyingReasons.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("We are concurrently applying for notice of this application"
            + " to be dispensed with under Rule 27(3) of the Non-Contentious Probate Rules"
            + " 1987 to executor6_name to whom power is to be reserved."));
    }

    @Test
    void verifySoTFirstParagraphPersonWhoDiedForClearingOne() throws IOException {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmLegalStatement.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
            "The person who diedDeceased Name, of Chapter Of Wells, Wells Cathedral, Wells, Somerset,"
                + " BA5 2PA, United Kingdom was born on 12/01/2020 and died on 14/01/2020, "
                + "domiciled in England and Wales. The will appoints an executor."));
    }

    @Test
    void verifySoTFirstParagraphPersonWhoDiedForClearingTwo() throws IOException {
        final String response = generateSotDocument("solicitorPayloadPartnersInFirm.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
            "The person who diedDeceased Name, of Chapter Of Wells, Wells Cathedral, Wells, Somerset,"
                + " BA5 2PA, United Kingdom was born on 12/01/2020 and died on 14/01/2020, "
                + "domiciled in England and Wales. The will appoints an executor."));
    }


    @Test
    void verifySecondParagraphFirmSuccessionForClearingThree() throws IOException {
        final String response = generateSotDocument("solicitorPayloadSoleSuccessorLegalStatement.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
            "The executor Partner Exec, is the only profit-sharing partner and stakeholder in the firm "
                + "Successor firm that had succeeded to and carried on the practice of the firm Firmname will, "
                + "at the date of death of the deceased."));

    }

    @Test
    void verifySoTSecondParagraphFirmSuccessionForClearingFour() throws IOException {
        final String response = generateSotDocument("solicitorPayloadSolePrin.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor Partner Exec, is the only profit-sharing partner and "
            + "stakeholder in the firm Firmname will, at the date of death of the deceased."));

    }

    @Test
    void verifySoTThirdParagraphOthersRenouncingInSuccessorClearingNine() throws IOException {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmRenounce.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
            "The executor Partner Exec, is a profit-sharing partner and stakeholder in the firm Successor firm"
                + " that had succeeded to and carried on the practice of the "
                + "firm Firmname will, at the date of death of the deceased. The remaining profit-sharing partners and "
                + "stakeholders in the firm Successor firm are renouncing their right to probate."));

    }

    @Test
    void verifySoTThirdParagraphOthersRenouncingInPartnerFirmClearingTen() throws IOException {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmRenounce.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
            "The executor Partner Exec, is a profit-sharing partner and stakeholder in the firm Successor firm"
                + " that had succeeded to and carried on the practice of the "
                + "firm Firmname will, at the date of death of the deceased. The remaining profit-sharing partners and "
                + "stakeholders in the firm Successor firm are renouncing their right to probate."));

    }

    @Test
    void verifySoTFourthParagraphAllSuccessorPartnersRenouncingClearingFive() throws IOException {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmAllRenounceNoAdditional.json",
            GENERATE_LEGAL_STATEMENT);
        // all partners are renouncing, so other partners in the collection are ignored, and wording is
        // 'the executor named in the will' as opposed to 'an executor named in the will'
        assertTrue(response.contains("Probate Practioner, the executor named in the will, is applying for probate."));
    }

    @Test
    void verifySoTFourthParagraphAllPartnerFirmsRenouncingClearingSix() throws IOException {
        final String response = generateSotDocument("solicitorPayloadPartnersAllRenounce.json",
            GENERATE_LEGAL_STATEMENT);
        // all partners are renouncing, so other partners in the collection are ignored, and wording is
        // 'the executor named in the will' as opposed to 'an executor named in the will'
        assertTrue(response.contains("Probate Practioner, the executor named in the will, is applying for probate."));
    }

    @Test
    void verifySoTFifthParagraphSeniorJudgeDistrictClearingSeven() throws IOException {
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
    void verifySoTFifthParagraphLodgedApplicationClearingEight() throws IOException {
        final String response = generateSotDocument("solicitorPayloadLodgeApp.json", GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor named in the will has by a resolution, certified copy"
            + " of which is lodged with this application, in which Exfn1 Exln1 identified by the position"
            + " they hold and which is still in force, appointed them for the purpose of applying for probate"
            + " of the will or for grants of probate on its behalf."));

    }


    @Test
    void verifySoTFirstParagraphClearancePartnerSucceeded() throws IOException {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmLegalStatement.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
            "The executor Partner Exec, is a profit-sharing partner and stakeholder in the firm"
                + " Successor firm that had succeeded to and carried on the practice of the firm Firmname will,"
                + " at the date of death of the deceased."));

    }

    @Test
    void verifySoTSecondParagraphSoleSucceeded() throws IOException {
        final String response = generateSotDocument("solicitorPayloadSoleSuccessorLegalStatement.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
            "The executor Partner Exec, is the only profit-sharing partner and stakeholder in the firm"
                + " Successor firm that had succeeded to and carried on the practice of the firm Firmname will,"
                + " at the date of death of the deceased."));

    }

    @Test
    void verifySoTThirdParagraphPartnerRenounceSucceeded() throws IOException {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmRenounce.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains(
            "The executor Partner Exec, is a profit-sharing partner and stakeholder in the "
                + "firm Successor firm that had succeeded to and carried on the practice of the firm Firmname "
                + "will, at the date of death of the deceased."));

    }

    @Test
    void verifySoTFourthParagraphPartnerAllRenounceSucceeded() throws IOException {
        final String response = generateSotDocument("solicitorPayloadSuccessorFirmAllRenounce.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Probate Practioner, an executor named in the will, is applying for probate."));
    }

    @Test
    void verifySoTFifthParagraphJudgeSeniorDistrict() throws IOException {
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
    void verifySoTSixthParagraphTrustCorpResolutionLodged() throws IOException {
        final String response = generateSotDocument("verifySolPayloadTrustCorpResolutionLodged.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("The executor named in the will has by a resolution, "
            + "certified copy of which is lodged"
            + " with this application, in which Exfn1 Exln1 identified by the position they hold and which"
            + " is still in force, appointed them for the purpose of applying for probate of "
            + "the will or for grants of probate on its behalf."));
    }

    @Test
    void verifySoTNoDuplicateSolExecutors() throws IOException {
        final String response = generateSotDocument(NO_DUPE_SOL_EXECUTORS, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
            .contains("The executor believes that all the information stated in the legal statement is true."));
        assertTrue(response.contains("Fred Smith, is a profit-sharing partner in the firm "
            + "fdgfg, at the date of death"));
        assertTrue(response.split("Fred Smith").length == 4);
    }

    @Test
    void verifySoTAliasNameForExec() throws IOException {
        final String response = generateSotDocument(EXEC_WITH_ALIAS, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Carlos Juan otherwise known as Karakiozis of"));
    }

    public void verifySoTSolePartnerWording() throws IOException {
        final String response = generateSotDocument(SOLE_PRIN, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("Fred Smith, is a profit-sharing partner in the firm "
            + "fdgfg, at the date of death"));
        assertTrue(response.split("Fred Smith").length == 4);
    }

    public void verifySoTPartAllRenouncingWording() throws IOException {
        final String response = generateSotDocument(PART_ALL_RENOUNCING, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
            .contains("I am the executor named in the will. The profit-sharing partners and stakeholders in the firm"
                + " Firmname will at the date of death of the deceased have renounced probate."));
    }

    @Test
    void verifySoTPartSuccAllRenouncingWording() throws IOException {
        final String response = generateSotDocument(PART_ALL_SUCC_RENOUNCING, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
            .contains("I am the executor named in the will. The profit-sharing partners and stakeholders in the firm"
                + " Successor firm that had succeeded to and carried on the practice of the firm Firmname will at the "
                + "date of death of the deceased have renounced probate."));
    }

    @Test
    void verifySoTPartOthersRenouncingWording() throws IOException {
        final String response = generateSotDocument(PART_ALL_OTHERS_RENOUNCING, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
            .contains("The executor Partner Exec, is a profit-sharing partner and stakeholder in the firm "
                + "Firmname will, at the date of death of the deceased."));
    }

    @Test
    void verifySoTSolPartnersWording() throws IOException {
        final String response = generateSotDocument(SOLE_PRIN_OTHER_PARTNERS, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
            .contains("The executors Probate Practitioner, Partner Exec, are the profit-sharing partners and "
                + "stakeholders in the firm "));
    }

    @Test
    void verifySoTSolPartnersWordingSingleExec() throws IOException {
        final String response = generateSotDocument(SOLE_PRIN_OTHER_PARTNERS_SINGLE, GENERATE_LEGAL_STATEMENT);
        assertTrue(response
            .contains("The executor Partner Exec, is the only profit-sharing partner and "
                + "stakeholder in the firm "));
    }

    @Test
    void verifySoTSolNotRepeated() throws IOException {
        final String response = generateSotDocument(SOL_NOT_REPEATED, GENERATE_LEGAL_STATEMENT);
        assertFalse(response
            .contains("Jim Smith (executor)"));
        assertTrue(response
            .contains("Jim Smith (Probate practitioner and executor)"));
        assertTrue(response.split("Jim Smith").length == 5);
    }

    public void verifyDefaultEvidenceToYesFromNull() throws IOException {
        Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(DEFAULT_SOLS_PAYLOAD))
            .when().post(GENERATE_GRANT).andReturn();
        assertTrue(jsonResponse.prettyPrint().contains("\"evidenceHandled\": \"Yes\""));
    }

    @Test
    void verifyDefaultEvidenceToYesFromNo() throws IOException {
        Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("evidenceHandledNo.json"))
            .when().post(GENERATE_GRANT).andReturn();
        assertTrue(jsonResponse.prettyPrint().contains("\"evidenceHandled\": \"Yes\""));
    }

    @Test
    void verifyDefaultEvidenceToYesFromYes() throws IOException {
        Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile("evidenceHandledYes.json"))
            .when().post(GENERATE_GRANT).andReturn();
        assertTrue(jsonResponse.prettyPrint().contains("\"evidenceHandled\": \"Yes\""));
    }


    @Test
    void verifyWillAccessNoLegalStatementAdmonWillSols() throws IOException {
        final String response = generateSotDocument("solicitorPDFPayloadAdmonWillNoAccess.json",
            GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("I authorise Firm Name to send on my behalf what I believe to be the true"
            + " and original last will and testament , as contained in a notarial/official copy of De Ceased."));
    }

    @Test
    void verifyWillAccessYesLegalStatementAdmonWillSols() throws IOException {
        final String response = generateSotDocument(DEFAULT_SOLS_PDF_ADMON_PAYLOAD, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("I authorise Firm Name to send on my behalf what "
            + "I believe to be the true and original last will and"
            + " testament of De Ceased"));
    }

    @Test
    void verifyGOPLegalStatementMultipleCodicils() throws IOException {
        final String response = generateSotDocument(GOP_PAYLOAD_MULTIPLE_CODICILS, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("which was settled previously to the death "
                + "(and not by the will and codicils) of"));
    }

    @Test
    void verifyGOPLegalStatementSingleCodicil() throws IOException {
        final String response = generateSotDocument(GOP_PAYLOAD_SINGLE_CODICIL, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("which was settled previously to the death "
                + "(and not by the will and codicil) of"));
    }

    @Test
    void verifyGOPLegalStatementNoCodicil() throws IOException {
        final String response = generateSotDocument(GOP_PAYLOAD_NO_CODICIL, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("which was settled previously to the death "
                + "(and not by the will) of"));
    }

    @Test
    void verifyTrustCorpGOPLegalStatementMultipleCodicils() throws IOException {
        final String response = generateSotDocument(GOP_PAYLOAD_TRUSTCORP_MULTIPLE_CODICILS, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("which was settled previously to the death "
                + "(and not by the will and codicils) of"));
    }

    @Test
    void verifyTrustCorpGOPLegalStatementSingleCodicil() throws IOException {
        final String response = generateSotDocument(GOP_PAYLOAD_TRUSTCORP_SINGLE_CODICIL, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("which was settled previously to the death "
                + "(and not by the will and codicil) of"));
    }

    @Test
    void verifyTrustCorpGOPLegalStatementNoCodicil() throws IOException {
        final String response = generateSotDocument(GOP_PAYLOAD_TRUSTCORP_NO_CODICIL, GENERATE_LEGAL_STATEMENT);
        assertTrue(response.contains("which was settled previously to the death "
                + "(and not by the will) of"));
    }

    private String generateSotDocument(String jsonFileName, String path) throws IOException {
        return generateSotDocumentFromPayload(utils.getJsonFromFile(jsonFileName), path);
    }

    private String generateSotDocumentFromPayload(String payload, String path) {
        return generateDocumentFromPayload(payload, path, SOT_DOC_NAME);
    }
}
