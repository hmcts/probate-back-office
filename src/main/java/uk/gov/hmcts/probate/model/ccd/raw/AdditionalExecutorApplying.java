package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalExecutorApplying {

    private final String applyingExecutorFirstName;
    private final String applyingExecutorLastName;
    private final String applyingExecutorPhoneNumber;
    private final String applyingExecutorEmail;
    private final SolsAddress applyingExecutorAddress;
    private String applyingExecutorName;
    private String applyingExecutorOtherNames;
    private String applyingExecutorOtherNamesReason;
    private String applyingExecutorOtherReason;
}
