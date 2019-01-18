package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.CaseType.LEGACY;

@EqualsAndHashCode(of = "caseLink")
@Data
@Builder
public class CaseMatch implements Serializable {
    private final String id;
    private final String fullName;
    private final String aliases;
    private final String dob;
    private final String dod;
    private final String postcode;
    private final String valid;
    private final String comment;
    private final String type;
    private final CaseLink caseLink;
    private final String doImport;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;

    public static CaseMatch buildCaseMatch(Case c, CaseType caseType) {
        CaseMatch.CaseMatchBuilder caseMatchBuilder = CaseMatch.builder();
        caseMatchBuilder.fullName(c.getData().getDeceasedFullName());
        if (c.getData().getDeceasedDateOfBirth() != null) {
            caseMatchBuilder.dob(c.getData().getDeceasedDateOfBirth().format(dateTimeFormatter));
        }
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

        if (caseType.equals(LEGACY)) {
            caseMatchBuilder.id(c.getData().getLegacyId());
        }

        if (c.getData().getSolsDeceasedAliasNamesList() != null) {
            String aliases = c.getData().getSolsDeceasedAliasNamesList().stream()
                    .map(CollectionMember::getValue)
                    .map(AliasName::getSolsAliasname)
                    .collect(Collectors.joining(", "));
            caseMatchBuilder.aliases(aliases);
        }

        return caseMatchBuilder.build();
    }
}
