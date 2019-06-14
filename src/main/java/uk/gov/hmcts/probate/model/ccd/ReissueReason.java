package uk.gov.hmcts.probate.model.ccd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReissueReason {

    private final String reason;
    private final String details;
}
