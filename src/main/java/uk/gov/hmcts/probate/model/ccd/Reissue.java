package uk.gov.hmcts.probate.model.ccd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Reissue {

    private final String reissueReason;
    private final String reissueReasonDetails;
}
