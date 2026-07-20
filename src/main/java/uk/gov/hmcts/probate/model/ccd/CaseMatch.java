package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;

import java.io.Serializable;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
public class CaseMatch implements Serializable {
    @CCD(label = "Id", showCondition = "legacyCaseViewUrl=\"NeverShowThisField\"")
    private final String id;
    @CCD(label = "ProbateMan Id")
    private final String recordId;
    @CCD(label = "Full name")
    private final String fullName;
    @CCD(label = "Aliases")
    private final String aliases;
    @CCD(label = "Date of birth")
    private final String dob;
    @CCD(label = "Date of death")
    private final String dod;
    @CCD(label = "Postcode")
    private final String postcode;
    @CCD(label = "Is match valid?", typeOverride = FieldType.YesOrNo)
    private final String valid;
    @CCD(label = "Comment")
    private final String comment;
    @CCD(label = "Type")
    private final String type;
    @CCD(label = "Reference")
    private CaseLink caseLink;
    @CCD(label = "Case Url")
    private final String legacyCaseViewUrl;
    @CCD(label = "Do import?", typeOverride = FieldType.YesOrNo)
    private final String doImport;

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CaseMatch)) {
            return false;
        }
        final CaseMatch other = (CaseMatch) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }

        final Object thisCaseLinkCaseRef = getThisCaseReference();
        final Object otherCaseLinkCaseRef = getOtherCaseReference(other.getCaseLink());
        if (thisCaseLinkCaseRef.equals(otherCaseLinkCaseRef)) {
            return true;
        }

        if (this.getLegacyCaseViewUrl() != null && other.getLegacyCaseViewUrl() != null
                && this.getLegacyCaseViewUrl().equals(other.getLegacyCaseViewUrl())) {
            return true;
        }

        final Object thisId = this.getId();
        final Object otherId = other.getId();
        return (thisId != null && otherId != null && thisId.equals(otherId));
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CaseMatch;
    }

    public int hashCode() {
        final int prime = 59;
        int result = 1;
        final Object id1 = this.getId();
        result = result * prime + (id1 == null ? 43 : id1.hashCode());
        return result;
    }

    private String getThisCaseReference() {
        if (caseLink == null) {
            return "1";
        }
        if (caseLink.getCaseReference() == null) {
            return "3";
        }
        return caseLink.getCaseReference();
    }

    private String getOtherCaseReference(CaseLink otherCaseLink) {
        if (otherCaseLink == null) {
            return "2";
        }
        if (otherCaseLink.getCaseReference() == null) {
            return "4";
        }
        return otherCaseLink.getCaseReference();
    }

  // ==== ccd-definition-converter: synthesised definition-only fields (retrofit) ====
  @CCD(label = "CCD Case No", showCondition = "legacyCaseViewUrl=\"NeverShowThisField\"")
  private String ccdCaseId;
  // ==== end synthesised definition-only fields ====
}
