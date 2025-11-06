package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class IntestacyScreenNieceNephewAdopt {
    private final String applicantParentSiblingOfDeceased;
    private final String applicantParentAdoptedIn;
    private final String applicantParentAdoptedInEngCym;
    private final String applicantParentAdoptedOut;
    private final String applicantAdoptedIn;
    private final String applicantAdoptedInEngCym;
    private final String applicantAdoptedOut;
}
