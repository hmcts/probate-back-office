package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@JsonSerialize
public class AdditionalExecutorApplying implements Serializable {

    private final String applyingExecutorFirstName;
    private final String applyingExecutorLastName;
    private final String applyingExecutorTrustCorpPosition;
    // Professional, TrustCorporation, or Named
    private final String applyingExecutorType;
    private final String applyingExecutorPhoneNumber;
    private final String applyingExecutorEmail;
    private final SolsAddress applyingExecutorAddress;
    private String applyingExecutorName;
    private String applyingExecutorOtherNames;
    private String applyingExecutorOtherNamesReason;
    private String applyingExecutorOtherReason;
}
