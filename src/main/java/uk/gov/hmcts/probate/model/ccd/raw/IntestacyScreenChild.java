package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class IntestacyScreenChild {
    private final String applicantAdoptedIn;
    private final String applicantAdoptedInEngCym;
    private final String applicantAdoptedOut;
}
