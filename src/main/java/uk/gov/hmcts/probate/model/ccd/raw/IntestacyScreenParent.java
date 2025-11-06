package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class IntestacyScreenParent {
    private final String survivingDescendants;
    private final String deceasedAdoptedIn;
    private final String deceasedAdoptedInEngCym;
    private final String deceasedAdoptedOut;
}
