package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class IntestacyScreenSibling {
    private final String survivingDescendants;
    private final String survivingParents;
    private final String deceasedAdoptedIn;
    private final String deceasedAdoptedInEngCym;
    private final String deceasedAdoptedOut;
    private final String wholeBloodSibling;
    private final String survivingWholeBloodSibling;
    private final IntestacyScreenSiblingAdopt siblingAdopted;
}
