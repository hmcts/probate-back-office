package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalExecutorApplying {

    private final String applyingExecutorName;
    private final String applyingExecutorPhoneNumber;
    private final String applyingExecutorEmail;
    private final SolsAddress applyingExecutorAddress;
    private final String applyingExecutorOtherNames;
    private final String applyingExecutorOtherNamesReason;
    private final String applyingExecutorOtherReason;
}
