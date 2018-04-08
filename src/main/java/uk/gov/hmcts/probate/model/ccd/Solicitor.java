package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Solicitor implements Serializable {
    private final String firmName;
    private final String firmPostcode;
    private final String fullname;
    private final String jobRole;
}
