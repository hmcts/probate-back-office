package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class IntestacyCoapplicantParent {
    private final String coapplicantFirstName;
    private final String coapplicantLastName;
    private final SolsAddress coapplicantAddress;
}
