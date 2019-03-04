package uk.gov.hmcts.probate.model.probateman;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProbateManCaseResponse {

    private ProbateManModel probateManCase;
}
