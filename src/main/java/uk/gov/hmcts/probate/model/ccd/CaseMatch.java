package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

import static uk.gov.hmcts.probate.model.CaseType.LEGACY;

@EqualsAndHashCode(of = "caseLink")
@Data
@Builder
public class CaseMatch implements Serializable {
    private final String fullName;
    private final String dod;
    private final String postcode;
    private final String valid;
    private final String comment;
    private final String type;
    private final CaseLink caseLink;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;

    public static CaseMatch buildCaseMatch(Case c, CaseType caseType) {
        CaseMatch.CaseMatchBuilder caseMatchBuilder = CaseMatch.builder();
        caseMatchBuilder.fullName(c.getData().getDeceasedFullName());
        if (c.getData().getDeceasedDateOfDeath() != null) {
            caseMatchBuilder.dod(c.getData().getDeceasedDateOfDeath().format(dateTimeFormatter));
        }
        if (c.getData().getDeceasedAddress() != null) {
            caseMatchBuilder.postcode(c.getData().getDeceasedAddress().getPostCode());
        }
        if (!caseType.equals(LEGACY)) {
            caseMatchBuilder.caseLink(CaseLink.builder().caseReference(c.getId().toString()).build());
        }
        if (caseType.equals(LEGACY)) {
            caseMatchBuilder.type(caseType.getName() + " " + c.getData().getLegacyCaseType());
        } else {
            caseMatchBuilder.type(caseType.getName());
        }

        return caseMatchBuilder.build();
    }
}
