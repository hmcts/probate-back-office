package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum DocumentType {
    @JsonProperty("legalStatement")
    LEGAL_STATEMENT("legalStatement"),

    @JsonProperty("digitalGrant")
    DIGITAL_GRANT("digitalGrant"),

    @JsonProperty("digitalGrantDraft")
    DIGITAL_GRANT_DRAFT("digitalGrantDraft"),

    @JsonProperty("IntestacyGrant")
    INTESTACY_GRANT("IntestacyGrant"),

    @JsonProperty("IntestacyGrant")
    INTESTACY_GRANT_DRAFT("IntestacyGrant"),

    @JsonProperty("AdmonWillGrant")
    ADMON_WILL_GRANT("AdmonWillGrant"),

    @JsonProperty("AdmonWillGrant")
    ADMON_WILL_GRANT_DRAFT("AdmonWillGrant"),

    @JsonProperty("sentEmail")
    SENT_EMAIL("sentEmail"),

    @JsonProperty("email")
    EMAIL("email"),

    @JsonProperty("IHT")
    IHT("IHT"),

    @JsonProperty("other")
    OTHER("other"),

    @JsonProperty("deathCertificate")
    DEATH_CERT("deathCertificate"),

    @JsonProperty("correspondence")
    CORRESPONDENCE("correspondence");

    private final String templateName;

    DocumentType(String templateName) {
        this.templateName = templateName;
    }
}
