package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;

@Data
@Builder
public class LegalStatementExecutorNotApplying {

    @CCD(label = "Executor")
    private final String executor;
}
