package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "DispenseWithNoticeExecutor", generate = true)
@Data
@Builder
public class AdditionalExecutorNotApplyingPowerReserved {

    @CCD(label = "Name of the executor to whom power is reserved")
    private final String notApplyingExecutorName;

}
