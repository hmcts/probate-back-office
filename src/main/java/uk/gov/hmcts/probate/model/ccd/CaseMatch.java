package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CaseMatch implements Serializable {
    private final String ccdId;
    private final String fullName;
    private final String dod;
    private final String postcode;
    private final String valid;
    private final String comment;
}
