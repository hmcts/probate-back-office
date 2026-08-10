package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "ExecutorActingForTrustCorp", generate = true)
@Data
@Builder
@AllArgsConstructor
public class AdditionalExecutorTrustCorps {

    @CCD(label = "First name(s)")
    private final String additionalExecForenames;
    @CCD(label = "Last name(s)")
    private final String additionalExecLastname;
    @CCD(label = "Name of their position within the trust corporation as per the resolution")
    private final String additionalExecutorTrustCorpPosition;

}
