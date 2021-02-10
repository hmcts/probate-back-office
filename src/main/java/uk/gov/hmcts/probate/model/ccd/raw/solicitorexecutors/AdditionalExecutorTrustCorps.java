package uk.gov.hmcts.probate.model.ccd.raw.solicitorexecutors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

@Data
@Builder
@AllArgsConstructor
public class AdditionalExecutorTrustCorps {

    private final String additionalExecForenames;
    private final String additionalExecLastname;
    private final String additionalExecutorTrustCorpPosition;
    private final SolsAddress additionalExecAddress;

}
