package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalExecutorApplying {

    private final String applyingExecutorName;
    private final String applyingExecutorFirstName;
    private final String applyingExecutorSurname;
    private final String applyingExecutorPhoneNumber;
    private final String applyingExecutorEmail;
    private ProbateAliasName aliasName;
    private String applyingExecutorOtherNames;
    private final SolsAddress applyingExecutorAddress;


}
