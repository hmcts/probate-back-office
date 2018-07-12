package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalExecutor {

    private final String additionalExecForenames;
    private final String additionalExecLastname;
    private final String additionalExecNameOnWill;
    private final String additionalExecAliasNameOnWill;
    private ProbateAliasName aliasName;
    private final String additionalApplying;
    private final SolsAddress additionalExecAddress;
    private final String additionalExecReasonNotApplying;

}
