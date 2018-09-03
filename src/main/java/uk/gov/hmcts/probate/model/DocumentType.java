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
