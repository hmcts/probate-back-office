package uk.gov.hmcts.probate.model;

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
    public static final String STATE_GRANT_TYPE_CREATED = "SolAppCreated";
    public static final String EMAIL_VALIDATION_REGEX = "[a-zA-Z0-9#$%'+=?^_`{|}~-]{1,30}(?:\\.[^.\\n]{1,30}){0,30}@"
        + "[a-zA-Z0-9](?:[a-zA-Z0-9-.]{0,30}[a-zA-Z0-9])?\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,10}[a-zA-Z0-9])?";

    private Constants() {
    }
}
