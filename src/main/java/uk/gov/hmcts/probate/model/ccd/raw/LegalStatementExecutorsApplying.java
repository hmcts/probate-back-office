package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LegalStatementExecutorsApplying {

    private final LegalStatementExecutorApplying value;

    private final String id;

}
