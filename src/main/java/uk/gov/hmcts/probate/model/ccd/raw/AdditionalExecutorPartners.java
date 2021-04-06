package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AdditionalExecutorPartners {

    private final String additionalExecForenames;
    private final String additionalExecLastname;
    private final SolsAddress additionalExecAddress;
}
