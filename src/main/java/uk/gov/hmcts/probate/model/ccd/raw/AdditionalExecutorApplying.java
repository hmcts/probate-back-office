package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_NAMED;

@Data
@Builder
@AllArgsConstructor
public class AdditionalExecutorApplying {

    private final String applyingExecutorFirstName;
    private final String applyingExecutorLastName;
    private final String applyingExecutorTrustCorpPosition;
    // Professional, TrustCorporation, or Named
    private String applyingExecutorType;
    private final String applyingExecutorPhoneNumber;
    private final String applyingExecutorEmail;
    private final SolsAddress applyingExecutorAddress;
    private String applyingExecutorName;
    private String applyingExecutorOtherNames;
    private String applyingExecutorOtherNamesReason;
    private String applyingExecutorOtherReason;
}
