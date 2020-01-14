package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum DocumentType {
    @JsonProperty("legalStatement")
    LEGAL_STATEMENT_ADMON("legalStatementAdmon"),

    @JsonProperty("legalStatement")
    LEGAL_STATEMENT_PROBATE("legalStatementProbate"),

    @JsonProperty("legalStatement")
    LEGAL_STATEMENT_INTESTACY("legalStatementIntestacy"),

    @JsonProperty("digitalGrant")
    DIGITAL_GRANT("digitalGrant"),

    @JsonProperty("grantCover")
    GRANT_COVER("grantCover"),

    @JsonProperty("blank")
    BLANK("blank"),

    @JsonProperty("digitalGrantDraft")
    DIGITAL_GRANT_DRAFT("digitalGrantDraft"),

    @JsonProperty("willLodgementDepositReceipt")
    WILL_LODGEMENT_DEPOSIT_RECEIPT("willLodgementDepositReceipt"),

    @JsonProperty("intestacyGrant")
    INTESTACY_GRANT("intestacyGrant"),

    @JsonProperty("intestacyGrantDraft")
    INTESTACY_GRANT_DRAFT("intestacyGrantDraft"),

    @JsonProperty("admonWillGrant")
    ADMON_WILL_GRANT("admonWillGrant"),

    @JsonProperty("admonWillGrantDraft")
    ADMON_WILL_GRANT_DRAFT("admonWillGrantDraft"),

    @JsonProperty("sentEmail")
    SENT_EMAIL("sentEmail"),

    @JsonProperty("email")
    EMAIL("email"),

    @JsonProperty("IHT")
    IHT("IHT"),

    @JsonProperty("other")
    OTHER("other"),

    @JsonProperty("edgeCase")
    EDGE_CASE("edgeCase"),

    @JsonProperty("deathCertificate")
    DEATH_CERT("deathCertificate"),

    @JsonProperty("correspondence")
    CORRESPONDENCE("correspondence"),

    @JsonProperty("caveatCoversheet")
    CAVEAT_COVERSHEET("caveatCoversheet"),

    @JsonProperty("caveatRaised")
    CAVEAT_RAISED("caveatRaised"),

    @JsonProperty("caveatStopped")
    CAVEAT_STOPPED("caveatStopped"),

    @JsonProperty("grantCoversheet")
    GRANT_COVERSHEET("grantCoversheet"),

    @JsonProperty("digitalGrantReissueDraft")
    DIGITAL_GRANT_REISSUE_DRAFT("digitalGrantReissueDraft"),

    @JsonProperty("intestacyGrantReissueDraft")
    INTESTACY_GRANT_REISSUE_DRAFT("intestacyGrantReissueDraft"),

    @JsonProperty("admonWillGrantReissueDraft")
    ADMON_WILL_GRANT_REISSUE_DRAFT("admonWillGrantReissueDraft"),

    @JsonProperty("digitalGrantReissue")
    DIGITAL_GRANT_REISSUE("digitalGrantReissue"),

    @JsonProperty("intestacyGrantReissue")
    INTESTACY_GRANT_REISSUE("intestacyGrantReissue"),

    @JsonProperty("admonWillGrantReissue")
    ADMON_WILL_GRANT_REISSUE("admonWillGrantReissue"),

    @JsonProperty("sotInformationRequest")
    SOT_INFORMATION_REQUEST("sotInformationRequest"),

    @JsonProperty("statementOfTruth")
    STATEMENT_OF_TRUTH("statementOfTruth"),

    @JsonProperty("assembledLetter")
    ASSEMBLED_LETTER("assembledLetter"),

    @JsonProperty("welshDigitalGrantDraft")
    WELSH_DIGITAL_GRANT_DRAFT("welshDigitalGrantDraft"),

    @JsonProperty("welshDigitalGrant")
    WELSH_DIGITAL_GRANT("welshDigitalGrant"),

    @JsonProperty("welshAdmonWillGrantDraft")
    WELSH_ADMON_WILL_GRANT_DRAFT("welshAdmonWillGrantDraft"),

    @JsonProperty("welshAdmonWillGrant")
    WELSH_ADMON_WILL_GRANT("welshAdmonWillGrant"),

    @JsonProperty("welshIntestacyGrantDraft")
    WELSH_INTESTACY_GRANT_DRAFT("welshIntestacyGrantDraft"),

    @JsonProperty("welshIntestacyGrant")
    WELSH_INTESTACY_GRANT("welshIntestacyGrant");

    private final String templateName;

    DocumentType(String templateName) {
        this.templateName = templateName;
    }
}
