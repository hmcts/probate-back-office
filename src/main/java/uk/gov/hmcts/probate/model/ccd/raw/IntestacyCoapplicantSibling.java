package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.IntestacyCoapplicantSiblingRelationship;

@Data
@Builder
public final class IntestacyCoapplicantSibling {
    private final String coapplicantFirstName;
    private final String coapplicantLastName;
    private final SolsAddress coapplicantAddress;
    private final IntestacyCoapplicantSiblingRelationship coapplicantRelationship;
    private final IntestacyScreenSiblingAdopt coapplicantSiblingAdopt;
    private final IntestacyScreenNieceNephewAdopt coapplicantNieceNephewAdopt;
}
