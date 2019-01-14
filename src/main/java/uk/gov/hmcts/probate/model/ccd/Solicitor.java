package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import java.io.Serializable;

@Data
@Builder
public class Solicitor implements Serializable {
    private final String firmName;
    private final SolsAddress firmAddress;
    private final String fullname;
    private final String jobRole;
}
