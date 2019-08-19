package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Reissue {

    private final String reissueReason;
    private final String reissueReasonDetails;
}
