package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtherPartnerExecutorApplying {
    private final String nameOfExecutorApplying;
}
