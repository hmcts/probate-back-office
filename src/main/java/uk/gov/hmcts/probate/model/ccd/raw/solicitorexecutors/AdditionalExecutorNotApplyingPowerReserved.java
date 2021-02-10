package uk.gov.hmcts.probate.model.ccd.raw.solicitorexecutors;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalExecutorNotApplyingPowerReserved {

    private final String notApplyingExecutorName;

}
