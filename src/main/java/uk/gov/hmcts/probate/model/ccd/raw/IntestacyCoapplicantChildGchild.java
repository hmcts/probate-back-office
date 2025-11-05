package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.IntestacyCoapplicantChildGrandchildRelationship;

@Data
@Builder
public final class IntestacyCoapplicantChildGchild {
    private final String coapplicantFirstName;
    private final String coapplicantLastName;
    private final SolsAddress coapplicantAddress;
    private final IntestacyCoapplicantChildGrandchildRelationship coapplicantRelationship;
    private final IntestacyScreenChild coapplicantChildScreen;
    private final IntestacyScreenGrandchild coapplicantGrandchildScreen;
}
