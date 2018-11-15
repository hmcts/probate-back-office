package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(of = "ccdId")
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
