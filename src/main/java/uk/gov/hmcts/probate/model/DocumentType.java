package uk.gov.hmcts.probate.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum DocumentType {
    @JsonProperty("legalStatementAdmon")
    LEGAL_STATEMENT_ADMON("legalStatementAdmon", "legal_statement_admon/original.html"),

    @JsonProperty("legalStatementProbate")
    LEGAL_STATEMENT_PROBATE("legalStatementProbate", "legal_statement_probate/original.html"),

    @JsonProperty("legalStatementGrantOfProbate")
    LEGAL_STATEMENT_PROBATE_TRUST_CORPS(
            "legalStatementGrantOfProbate",
            "legal_statement_grant_of_probate/original.html"),

    @JsonProperty("legalStatementIntestacy")
    LEGAL_STATEMENT_INTESTACY("legalStatementIntestacy", "legal_statement_intestacy/original.html"),

    @JsonProperty("legalStatement")
    LEGAL_STATEMENT("legalStatement"),

    @JsonProperty("uploadedLegalStatement")
    UPLOADED_LEGAL_STATEMENT("uploadedLegalStatement"),

    @JsonProperty("digitalGrant")
    DIGITAL_GRANT("digitalGrant", "digital_grant/original.html"),

    @JsonProperty("grantCover")
    GRANT_COVER("grantCover", "grant_cover/original.html"),

    @JsonProperty("solicitorCoverSheet")
    SOLICITOR_COVERSHEET("solicitorCoverSheet", "solicitor_cover_sheet/original.html"),

    @JsonProperty("blank")
    BLANK("blank", "blank/original.html"),

    @JsonProperty("digitalGrantDraft")
    DIGITAL_GRANT_DRAFT("digitalGrantDraft", "digital_grant_draft/original.html"),

    @JsonProperty("willLodgementDepositReceipt")
    WILL_LODGEMENT_DEPOSIT_RECEIPT("willLodgementDepositReceipt", "will_lodgement_deposit_receipt/original.html"),

    @JsonProperty("intestacyGrant")
    INTESTACY_GRANT("intestacyGrant", "intestacy_grant/original.html"),

    @JsonProperty("intestacyGrantDraft")
    INTESTACY_GRANT_DRAFT("intestacyGrantDraft", "intestacy_grant_draft/original.html"),

    @JsonProperty("admonWillGrant")
    ADMON_WILL_GRANT("admonWillGrant", "admon_will_grant/original.html"),

    @JsonProperty("admonWillGrantDraft")
    ADMON_WILL_GRANT_DRAFT("admonWillGrantDraft", "admon_will_grant_draft/original.html"),

    @JsonProperty("adColligendaBonaGrant")
    AD_COLLIGENDA_BONA_GRANT("adColligendaBonaGrant", "ad_colligenda_bona_grant/original.html"),

    @JsonProperty("adColligendaBonaGrantDraft")
    AD_COLLIGENDA_BONA_GRANT_DRAFT("adColligendaBonaGrantDraft", "ad_colligenda_bona_grant_draft/original.html"),

    @JsonProperty("sentEmail")
    SENT_EMAIL("sentEmail", "sent_email/original.html"),

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

    @JsonProperty("will")
    WILL("will"),

    @JsonProperty("citizenHubUpload")
    CITIZEN_HUB_UPLOAD("citizenHubUpload"),

    @JsonProperty("caveatExtension")
    CAVEAT_EXTENSION("caveatExtension"),

    @JsonProperty("caveatCoversheet")
    CAVEAT_COVERSHEET("caveatCoversheet"),

    @JsonProperty("caveatRaised")
    CAVEAT_RAISED("caveatRaised"),

    @JsonProperty("grantRaised")
    GRANT_RAISED("grantRaised"),

    @JsonProperty("caveatStopped")
    CAVEAT_STOPPED("caveatStopped"),

    @JsonProperty("caveatExtended")
    CAVEAT_EXTENDED("caveatExtended"),

    @JsonProperty("caveatWithdrawn")
    CAVEAT_WITHDRAWN("caveatWithdrawn"),

    @JsonProperty("cherished")
    CHERISHED("cherished"),

    @JsonProperty("grantCoversheet")
    GRANT_COVERSHEET("grantCoversheet"),

    @JsonProperty("digitalGrantReissueDraft")
    DIGITAL_GRANT_REISSUE_DRAFT("digitalGrantReissueDraft", "digital_grant_reissue_draft/original.html"),

    @JsonProperty("intestacyGrantReissueDraft")
    INTESTACY_GRANT_REISSUE_DRAFT("intestacyGrantReissueDraft"),

    @JsonProperty("admonWillGrantReissueDraft")
    ADMON_WILL_GRANT_REISSUE_DRAFT("admonWillGrantReissueDraft"),

    @JsonProperty("adColligendaBonaGrantReissueDraft")
    AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT("adColligendaBonaGrantReissueDraft"),

    @JsonProperty("digitalGrantReissue")
    DIGITAL_GRANT_REISSUE("digitalGrantReissue", "digital_grant_reissue/original.html"),

    @JsonProperty("intestacyGrantReissue")
    INTESTACY_GRANT_REISSUE("intestacyGrantReissue"),

    @JsonProperty("admonWillGrantReissue")
    ADMON_WILL_GRANT_REISSUE("admonWillGrantReissue"),

    @JsonProperty("adColligendaBonaGrantReissue")
    AD_COLLIGENDA_BONA_GRANT_REISSUE("adColligendaBonaGrantReissue"),

    @JsonProperty("sotInformationRequest")
    SOT_INFORMATION_REQUEST("sotInformationRequest"),

    @JsonProperty("statementOfTruth")
    STATEMENT_OF_TRUTH("statementOfTruth"),

    @JsonProperty("welshStatementOfTruth")
    WELSH_STATEMENT_OF_TRUTH("welshStatementOfTruth"),

    @JsonProperty("assembledLetter")
    ASSEMBLED_LETTER("assembledLetter"),

    @JsonProperty("blankLetter")
    BLANK_LETTER("blankLetter"),

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
    WELSH_INTESTACY_GRANT("welshIntestacyGrant"),

    @JsonProperty("welshAdColligendaBonaGrantDraft")
    WELSH_AD_COLLIGENDA_BONA_GRANT_DRAFT("welshAdColligendaBonaGrantDraft"),

    @JsonProperty("welshAdColligendaBonaGrant")
    WELSH_AD_COLLIGENDA_BONA_GRANT("welshAdColligendaBonaGrant"),

    @JsonProperty("welshDigitalGrantReissueDraft")
    WELSH_DIGITAL_GRANT_REISSUE_DRAFT("welshDigitalGrantReissueDraft"),

    @JsonProperty("welshIntestacyGrantReissueDraft")
    WELSH_INTESTACY_GRANT_REISSUE_DRAFT("welshIntestacyGrantReissueDraft"),

    @JsonProperty("welshAdmonWillGrantReissueDraft")
    WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT("welshAdmonWillGrantReissueDraft"),

    @JsonProperty("welshAdColligendaBonaGrantReissueDraft")
    WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT("welshAdColligendaBonaGrantReissueDraft"),

    @JsonProperty("welshDigitalGrantReissue")
    WELSH_DIGITAL_GRANT_REISSUE("welshDigitalGrantReissue"),

    @JsonProperty("welshIntestacyGrantReissue")
    WELSH_INTESTACY_GRANT_REISSUE("welshIntestacyGrantReissue"),

    @JsonProperty("welshAdmonWillGrantReissue")
    WELSH_ADMON_WILL_GRANT_REISSUE("welshAdmonWillGrantReissue"),

    @JsonProperty("welshAdColligendaBonaGrantReissue")
    WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE("welshAdColligendaBonaGrantReissue");

    private final String templateName;
    private final Optional<String> commonsTemplateName;

    DocumentType(
            final String templateName,
            final String commonsTemplateName) {
        this.templateName = templateName;
        this.commonsTemplateName = Optional.of(commonsTemplateName);
    }

    DocumentType(final String templateName) {
        this.templateName = templateName;
        this.commonsTemplateName = Optional.empty();
    }

    private static Map<String, DocumentType> map = new HashMap<>();
    static {
        for (DocumentType s : DocumentType.values()) {
            map.put(s.name(), s);
            map.put(s.name().toLowerCase(), s);
        }
    }

    public static DocumentType lookup(String name) {
        return map.get(name);
    }
}
