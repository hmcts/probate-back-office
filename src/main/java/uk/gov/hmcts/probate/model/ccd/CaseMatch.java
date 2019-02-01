package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;

import java.io.Serializable;

@Data
@Builder
public class CaseMatch implements Serializable {
    private final String id;
    private final String ccdCaseId;
    private final String fullName;
    private final String aliases;
    private final String dob;
    private final String dod;
    private final String postcode;
    private final String valid;
    private final String comment;
    private final String type;
    private CaseLink caseLink;
    private final String legacyCaseViewUrl;
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

        final Object thisCaseLink = this.getCaseLink();
        final Object thisCaseLinkCaseRef = thisCaseLink == null ? "1" : ((CaseLink) thisCaseLink).getCaseReference();
        final Object otherCaseLink = other.getCaseLink();
        final Object otherCaseLinkCaseRef = otherCaseLink == null ? "2" : ((CaseLink) otherCaseLink).getCaseReference();
        if (thisCaseLinkCaseRef.equals(otherCaseLinkCaseRef)) {
            return true;
        }

        final Object thisId = this.getId();
        final Object otherId = other.getId();
        return (thisId != null && otherId != null && thisId.equals(otherId)) ? true : false;
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
}
