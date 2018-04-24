package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

@Data
@Builder
public class Executor {

    private final boolean applying;
    private final SolsAddress address;
    private final String reasonNotApplying;
    private final String forename;
    private final String lastname;
}
