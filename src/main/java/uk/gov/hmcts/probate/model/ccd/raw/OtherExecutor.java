package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtherExecutor {

    private final String additionalExecForenames;
    private final String additionalExecLastname;
    private final String additionalExecNameOnWill;
    private final String additionalExecAliasNameOnWill;
    private final String additionalApplying;
    private final SolsAddress additionalExecAddress;

}
