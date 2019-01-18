package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProbateExecutor {

    private final String executorTitle;
    private final String executorForenames;
    private final String executorSurname;
    private final ProbateAddress executorAddress;
    private final String executorEmailAddress;
}
