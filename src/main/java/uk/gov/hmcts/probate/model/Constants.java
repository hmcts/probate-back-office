package uk.gov.hmcts.probate.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public final class Constants {

    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    public static final int CAVEAT_LIFESPAN = 6;
    public static final int CAVEAT_EXPIRY_EXTENSION_PERIOD_IN_MONTHS = 6;
    public static final int STANDING_SEARCH_LIFESPAN = 6;
    public static final String BUSINESS_ERROR = "businessError";
    public static final String NEWCASTLE = "newcastle";
    public static final String CTSC = "ctsc";
    public static final String EXTRACT_PROBATE = "PROBATE";
    public static final String EXTRACT_ADMINISTRATION = "ADMINISTRATION";
    public static final String EXTRACT_ADMON_WILL = "ADMON/WILL";

    public static final String EXTRACT_AD_COLLIGENDA_BONA = "AdCol";
    public static final String PRINCIPAL_REGISTRY = "Principal Registry";
    public static final String DOC_SUBTYPE_WILL = "will";
    public static final String DOC_TYPE_COVERSHEET = "coversheet";
    public static final String DOC_TYPE_FORM = "form";
    public static final String DOC_TYPE_WILL = "will";
    public static final String DOC_SUBTYPE_ORIGINAL_WILL = "Original Will";
    public static final String DOC_SUBTYPE_COPY_WILL = "Copy Will";
    public static final String DOC_TYPE_FORENSIC_SHEETS = "forensic_sheets";
    public static final String DOC_TYPE_SUPPORTING_DOCUMENTS = "supporting_documents";
    public static final String DOC_TYPE_IHT = "iht";
    public static final String DOC_TYPE_PPS_LEGAL_STATEMENT = "pps_legal_statement";
    public static final String DOC_TYPE_CHERISHED = "cherished";
    public static final String DOC_TYPE_OTHER = "other";
    public static final String DATE_OF_DEATH_TYPE_DEFAULT = "diedOn";
    public static final String CASE_TYPE_GRANT_OF_PROBATE = "gop";
    public static final String DOCMOSIS_OUTPUT_PDF = "pdf";
    public static final String DOCMOSIS_OUTPUT_HTML = "html";
    public static final String REDEC_NOTIFICATION_SENT_STATE = "BORedecNotificationSent";

    public static final String GRANT_TYPE_PROBATE = "WillLeft";
    public static final String GRANT_TYPE_INTESTACY = "NoWill";
    public static final String GRANT_TYPE_ADMON = "WillLeftAnnexed";

    public static final String CHANNEL_CHOICE_PAPERFORM = "PaperForm";
    public static final String CHANNEL_CHOICE_DIGITAL = "Digital";
    public static final String CHANNEL_CHOICE_BULKSCAN = "BulkScan";

    public static final String STATE_STOPPED = "Stopped";
    public static final String STATE_GRANT_TYPE_PROBATE = "SolProbateCreated";
    public static final String STATE_GRANT_TYPE_INTESTACY = "SolIntestacyCreated";
    public static final String STATE_GRANT_TYPE_ADMON = "SolAdmonCreated";
    public static final String STATE_GRANT_TYPE_CREATED_SOLICITOR_DTLS = "SolAppCreatedSolicitorDtls";
    public static final String STATE_GRANT_TYPE_CREATED_DECEASED_DTLS = "SolAppCreatedDeceasedDtls";

    public static final String EXECUTOR_TYPE_PROFESSIONAL = "Professional";
    public static final String EXECUTOR_TYPE_TRUST_CORP = "TrustCorporation";
    public static final String EXECUTOR_TYPE_NAMED = "Named";

    public static final String EXECUTOR_NOT_APPLYING_REASON = "PowerReserved";

    public static final String SOLS_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD = "ChildAdopted";
    public static final String PRIMARY_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD = "adoptedChild";

    public static final String TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED = "TCTPartSuccPowerRes";
    public static final String TITLE_AND_CLEARING_FIRM_CEASED_TRADING_NO_SUCCESSOR = "TCTFirmCeasedTradingNoSucc";
    public static final String TITLE_AND_CLEARING_PARTNER_POWER_RESERVED = "TCTPartPowerRes";
    public static final String TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR = "TCTSolePrinSucc";
    public static final String TITLE_AND_CLEARING_SOLE_PRINCIPLE = "TCTSolePrin";
    public static final String TITLE_AND_CLEARING_TRUST_CORP_SDJ = "TCTTrustCorpResWithSDJ";
    public static final String TITLE_AND_CLEARING_TRUST_CORP = "TCTTrustCorpResWithApp";
    public static final String TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING = "TCTPartSuccOthersRenouncing";
    public static final String TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING = "TCTPartOthersRenouncing";
    public static final String TITLE_AND_CLEARING_PARTNER_SUCC_ALL_RENOUNCING = "TCTPartSuccAllRenouncing";
    public static final String TITLE_AND_CLEARING_PARTNER_ALL_RENOUNCING = "TCTPartAllRenouncing";
    public static final String TITLE_AND_CLEARING_NONE_OF_THESE = "TCTNoT";
    public static final String REASON_FOR_NOT_APPLYING_RENUNCIATION = "Renunciation";
    public static final String REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE = "MentallyIncapable";
    public static final String REASON_FOR_NOT_APPLYING_DIED_BEFORE = "DiedBefore";
    public static final String REASON_FOR_NOT_APPLYING_DIED_AFTER = "DiedAfter";
    public static final String EMPTY_STRING = "";
    public static final String SOLICITOR_ID = "solicitor";
    public static final String AUTHENTICATED_TRANSLATION_WILL_TEXT
        = "an authenticated translation of the will in English or Welsh";

    public static final String AUTHENTICATED_TRANSLATION_WILL_TEXT_WELSH
            = "cyfieithiad wedi’i ddilysu o’r ewyllys yn Gymraeg neu Saesneg";
    public static final String PA14_FORM_URL = "https://www.gov.uk/government/publications/form-pa14-medical-certificate-probate";
    public static final String PA14_FORM_TEXT = "Medical certificate completed by a health professional";

    public static final String PA14_FORM_TEXT_WELSH
            = "Tystysgrif feddygol wedi'i chwblhau gan weithiwr iechyd proffesiynol ";
    public static final String PA14_FORM_TEXT_AFTER = "(PA14) for ";
    public static final String PA14_FORM_TEXT_AFTER_WELSH = "(PA14) ar gyfer";
    public static final String PA15_FORM_URL = "https://www.gov.uk/government/publications/form-pa15-give-up-probate-executor-rights";
    public static final String PA15_FORM_TEXT = "Give up probate administrator rights paper form";
    public static final String PA15_FORM_TEXT_WELSH = "Ffurflen bapur rhoi’r gorau i hawliau gweinyddwr profiant ";
    public static final String PA15_FORM_TEXT_AFTER = "(PA15) for ";
    public static final String PA15_FORM_TEXT_AFTER_WELSH = "(PA15) ar gyfer ";
    public static final String PA16_FORM_URL = "https://www.gov.uk/government/publications/form-pa16-give-up-probate-administrator-rights";
    public static final String PA16_FORM_TEXT = "Give up probate administrator rights paper form (PA16)";

    public static final String PA16_FORM_TEXT_WELSH
            = "Ffurflen bapur rhoi’r gorau i hawliau gweinyddwr profiant (PA16)";
    public static final String PA17_FORM_URL = "https://www.gov.uk/government/publications/form-pa17-give-up-probate-executor-rights-for-probate-practitioners";
    public static final String PA17_FORM_TEXT = "Give up probate executor rights for probate practitioners paper form"
        + " (PA17)";

    public static final String PA17_FORM_TEXT_WELSH = "Ffurflen bapur rhoi’r gorau i hawliau ysgutor profiant ar gyfer "
            + "ymarferwyr profiant (PA17)";
    public static final String PA15_FORM_TEXT_ADMON_WILL = "PA15";
    public static final String PA17_FORM_TEXT_ADMON_WILL = "PA17";
    public static final String IHT_ESTATE_207_TEXT = "the inheritance tax form IHT 207";

    public static final String IHT_ESTATE_207_TEXT_WELSH = "y ffurflen treth etifeddiant IHT 207";
    public static final String IHT_ESTATE_CONFIRM = "I confirm that the estate is an excepted estate";
    public static final String IHT_ESTATE_NOT_CONFIRM = "I confirm that the estate is not an excepted estate";
    public static final String DISPENSE_NOTICE_SUPPORT_TEXT =
            "the documents you listed to support your request to dispense with notice to non-applying executor(s): ";

    public static final String DISPENSE_NOTICE_SUPPORT_TEXT_WELSH =
            "y dogfennau a restrwyd gennych i gefnogi eich cais i hepgor hysbysu ysgutor nad yw/ysgutorion "
                    + "nad ydynt yn gwneud cais ";
    public static final String TC_RESOLUTION_LODGED_WITH_APP = "a certified copy of the resolution";

    public static final String TC_RESOLUTION_LODGED_WITH_APP_WELSH = "copi ardystiedig o’r datrysiad";

    public static final String ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT = "if applicable, send us the appropriate"
        + " renunciation form";

    public static final String ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT_WELSH = "os yw’n berthnasol, "
            + " anfonwch y ffurflen ymwrthod briodol atom";
    public static final String ADMON_WILL_RENUNCIATION_MID_LINKS_TEXT = "/ ";
    public static final String ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT = "for executors who have"
        + " renounced their right to apply";

    public static final String ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT_WELSH = "ar gyfer ysgutorion "
            + " sydd wedi rhoi’r gorau i’w hawl i wneud cais";
    public static final String NOTARIAL_COPY_WILL_TEXT = "the notarial or court sealed copy of the will";

    public static final String NOTARIAL_COPY_WILL_TEXT_WELSH
            = "copi notarïol o’r ewyllys neu gopi wedi’i selio gan y llys";
    public static final String STATEMENT_OF_TRUTH_AND_EXHIBITS_TEXT
        = "statement of truth and Exhibits that lead to a R54 Order NCPR 1987 to prove the will is lost, "
         + "and that it has not been revoked";

    public static final String STATEMENT_OF_TRUTH_AND_EXHIBITS_TEXT_WELSH
            = "datganiad gwirionedd ac arddangosion sy’n arwain at Orchymyn R54 NCPR 1987 "
            + "i brofi bod yr ewyllys ar goll, ac nad yw wedi'i dirymu";
    public static final String ORIGINAL_WILL_TEXT = "the original will";

    public static final String ORIGINAL_WILL_TEXT_WELSH = "yr ewyllys wreiddiol";


    public static final String ORIGINAL_WILL_WITH_CODICILS_TEXT = "the original will and any codicils";

    public static final String ORIGINAL_WILL_WITH_CODICILS_TEXT_WELSH = "yr ewyllys wreiddiol ac unrhyw godisiliau";
    // Is set when Solicitor completes.We currently have either 2.0.0 or null.
    // If we need to introduce more versions, we may need to change code where this is used
    // and possibly use more constants such as VERSION_TC_INTRODUCED, and use
    // >, >=, <=, < comparisions etc, converting to numeric
    public static final String LATEST_SCHEMA_VERSION = "2.0.0";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DecimalFormat FORMAT = new DecimalFormat("#,###");

    public static final BigDecimal DIVISOR = new BigDecimal("100");
    public static final String CAVEAT_SOLICITOR_NAME = "Sir/Madam";

    public static List<String> getNonTrustPtnrTitleClearingTypes() {
        return new ArrayList<>(asList(
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED,
                TITLE_AND_CLEARING_PARTNER_POWER_RESERVED,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE,
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING,
                TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING,
                TITLE_AND_CLEARING_PARTNER_SUCC_ALL_RENOUNCING,
                TITLE_AND_CLEARING_PARTNER_ALL_RENOUNCING));
    }

    public static List<String> getNonTrustPtnrNotAllRenouncingTitleClearingTypes() {
        return new ArrayList<>(asList(
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED,
                TITLE_AND_CLEARING_PARTNER_POWER_RESERVED,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE,
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING,
                TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING));
    }

    public static List<String> getTrustCorpTitleClearingTypes() {
        return new ArrayList<>(asList(
                TITLE_AND_CLEARING_TRUST_CORP_SDJ,
                TITLE_AND_CLEARING_TRUST_CORP));
    }

    public static List<String> getSuccessorTitleClearingTypes() {
        return new ArrayList<>(asList(
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED,
                TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR,
                TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING,
                TITLE_AND_CLEARING_PARTNER_SUCC_ALL_RENOUNCING));
    }

    public static final String EMAIL_REGEX = "[a-zA-Z0-9#$%'+=?^_`{|}~-]{1,30}(?:\\.[^.\\n]{1,30})"
        + "{0,30}@[a-zA-Z0-9](?:[a-zA-Z0-9-.]{0,30}[a-zA-Z0-9])?\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,10}[a-zA-Z0-9])?";

    public static final List<String> SCANNED_DOCS_ORDER = new ArrayList<>(asList(
            DOC_TYPE_COVERSHEET,
            DOC_TYPE_FORM,
            DOC_TYPE_WILL,
            DOC_TYPE_FORENSIC_SHEETS,
            DOC_TYPE_SUPPORTING_DOCUMENTS,
            DOC_TYPE_IHT,
            DOC_TYPE_PPS_LEGAL_STATEMENT,
            DOC_TYPE_CHERISHED,
            DOC_TYPE_OTHER));

    public static final Map<String, String[]> EN_TO_WELSH = Map.of(
            "partners", new String[]{"phartner", "partneriaid"},
            "members", new String[]{"aelod", "aelodau"},
            "stakeholders", new String[]{"rhanddeiliad", "rhanddeiliaid"},
            "directors", new String[]{"cyfarwyddwr", "cyfarwyddwyr"}
    );

    private Constants() {
    }
}
