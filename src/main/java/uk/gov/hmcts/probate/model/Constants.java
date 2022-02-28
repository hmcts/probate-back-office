package uk.gov.hmcts.probate.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public final class Constants {

    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final int CAVEAT_LIFESPAN = 6;
    public static final int CAVEAT_EXPIRY_EXTENSION_PERIOD_IN_MONTHS = 6;
    public static final int STANDING_SEARCH_LIFESPAN = 6;
    public static final String BUSINESS_ERROR = "businessError";
    public static final String LONDON = "london";
    public static final String CTSC = "ctsc";
    public static final String EXTRACT_PROBATE = "PROBATE";
    public static final String EXTRACT_ADMINISTRATION = "ADMINISTRATION";
    public static final String EXTRACT_ADMON_WILL = "ADMON/WILL";
    public static final String PRINCIPAL_REGISTRY = "Principal Registry";
    public static final String DOC_SUBTYPE_WILL = "will";
    public static final String DATE_OF_DEATH_TYPE_DEFAULT = "diedOn";
    public static final String CASE_TYPE_DEFAULT = "gop";
    public static final String DOCMOSIS_OUTPUT_PDF = "pdf";
    public static final String DOCMOSIS_OUTPUT_HTML = "html";
    public static final String REDEC_NOTIFICATION_SENT_STATE = "BORedecNotificationSent";

    public static final String GRANT_TYPE_PROBATE = "WillLeft";
    public static final String GRANT_TYPE_INTESTACY = "NoWill";
    public static final String GRANT_TYPE_ADMON = "WillLeftAnnexed";

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

    public static final String TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED = "TCTPartSuccPowerRes";
    public static final String TITLE_AND_CLEARING_PARTNER_POWER_RESERVED = "TCTPartPowerRes";
    public static final String TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR = "TCTSolePrinSucc";
    public static final String TITLE_AND_CLEARING_SOLE_PRINCIPLE = "TCTSolePrin";
    public static final String TITLE_AND_CLEARING_TRUST_CORP_SDJ = "TCTTrustCorpResWithSDJ";
    public static final String TITLE_AND_CLEARING_TRUST_CORP = "TCTTrustCorpResWithApp";
    public static final String TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING = "TCTPartSuccOthersRenouncing";
    public static final String TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING = "TCTPartOthersRenouncing";
    public static final String TITLE_AND_CLEARING_PARTNER_SUCC_ALL_RENOUNCING = "TCTPartSuccAllRenouncing";
    public static final String TITLE_AND_CLEARING_PARTNER_ALL_RENOUNCING = "TCTPartAllRenouncing";
    public static final String REASON_FOR_NOT_APPLYING_RENUNCIATION = "Renunciation";
    public static final String REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE = "MentallyIncapable";
    public static final String REASON_FOR_NOT_APPLYING_DIED_BEFORE = "DiedBefore";
    public static final String REASON_FOR_NOT_APPLYING_DIED_AFTER = "DiedAfter";
    public static final String EMPTY_STRING = "";
    public static final String SOLICITOR_ID = "solicitor";
    public static final String PA14_FORM_URL = "https://www.gov.uk/government/publications/form-pa14-medical-certificate-probate";
    public static final String PA14_FORM_TEXT = "Medical certificate completed by a health professional";
    public static final String PA14_FORM_TEXT_AFTER = "(PA14) for ";
    public static final String PA15_FORM_URL = "https://www.gov.uk/government/publications/form-pa15-give-up-probate-executor-rights";
    public static final String PA15_FORM_TEXT = "Give up probate administrator rights paper form";
    public static final String PA15_FORM_TEXT_AFTER = "(PA15) for ";
    public static final String PA16_FORM_URL = "https://www.gov.uk/government/publications/form-pa16-give-up-probate-administrator-rights";
    public static final String PA16_FORM_TEXT = "Give up probate administrator rights paper form (PA16)";
    public static final String PA17_FORM_URL = "https://www.gov.uk/government/publications/form-pa17-give-up-probate-executor-rights-for-probate-practitioners";
    public static final String PA17_FORM_TEXT = "Give up probate executor rights for probate practitioners paper form" 
        + " (PA17)";
    public static final String PA15_FORM_URL = "https://www.gov.uk/government/publications/form-pa15-give-up-probate-executor-rights";
    public static final String PA15_FORM_TEXT_ADMON_WILL = "PA15";
    public static final String PA17_FORM_TEXT_ADMON_WILL = "PA17";
    public static final String IHT_ESTATE_207_TEXT = "the inheritance tax form IHT 207";
    public static final String IHT_ESTATE_CONFIRM = "I confirm that the estate is an excepted estate";
    public static final String IHT_ESTATE_NOT_CONFIRM = "I confirm that the estate is not an excepted estate";

    public static final String ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT = "if applicable, send us the appropriate"
        + " renunciation form ";
    public static final String ADMON_WILL_RENUNCIATION_MID_LINKS_TEXT = " / ";
    public static final String ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT = " for executors who have"
        + " renounced their right to apply";

    // Is set when Solicitor completes.We currently have either 2.0.0 or null.
    // If we need to introduce more versions, we may need to change code where this is used
    // and possibly use more constants such as VERSION_TC_INTRODUCED, and use
    // >, >=, <=, < comparisions etc, converting to numeric
    public static final String LATEST_SCHEMA_VERSION = "2.0.0";

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

    private Constants() {
    }
}
