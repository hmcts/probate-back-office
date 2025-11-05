package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.IntestacyCoapplicantHalfSiblingRelationship;

@Data
@Builder
public final class IntestacyCoapplicantHalfSibling {
    private final String coapplicantFirstName;
    private final String coapplicantLastName;
    private final SolsAddress coapplicantAddress;
    private final IntestacyCoapplicantHalfSiblingRelationship coapplicantRelationship;
    private final IntestacyScreenSiblingAdopt coapplicantSiblingAdopt;
    private final IntestacyScreenNieceNephewAdopt coapplicantNieceNephewAdopt;
}
