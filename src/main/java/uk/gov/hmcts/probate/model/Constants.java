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

    public static final List<String> NON_TRUST_PTNR_TITLE_CLEARING_TYPES = new ArrayList<String>(asList(
            TITLE_AND_CLEARING_PARTNER_SUCCESSOR_POWER_RESERVED,
            TITLE_AND_CLEARING_PARTNER_POWER_RESERVED,
            TITLE_AND_CLEARING_SOLE_PRINCIPLE_SUCCESSOR,
            TITLE_AND_CLEARING_SOLE_PRINCIPLE,
            TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING,
            TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING));

    public static final List<String> TRUST_CORP_TITLE_CLEARING_TYPES = new ArrayList<>(asList(
            TITLE_AND_CLEARING_TRUST_CORP_SDJ,
            TITLE_AND_CLEARING_TRUST_CORP));

    private Constants() {
    }
}
