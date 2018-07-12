package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalExecutorNotApplying {

    private final String notApplyingExecutorName;
    private final String notApplyingExecutorFirstName;
    private final String notApplyingExecutorSurname;
    private final String notApplyingExecutorNameOnWill;
    private ProbateAliasName aliasName;
    private final String notApplyingExecutorNameDifferenceComment;
    private final String notApplyingExecutorReason;
    private final String notApplyingExecutorNotified;
    private final SolsAddress notApplyingExecAddress;
}
