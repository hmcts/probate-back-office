package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalExecutorActingForTrustCorp {
    private final String otherActingForTrustCorpName;
    private final String otherActingForTrustCorpPosition;
}
