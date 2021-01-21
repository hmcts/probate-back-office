package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AdditionalExecutorTrustCorp {

    private final String otherActingForTrustCorpName;
    private final String otherActingForTrustCorpPosition;

}
