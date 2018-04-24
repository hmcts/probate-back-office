package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
public class Deceased implements Serializable {

    private final String firstname;
    private final String lastname;
    private final LocalDate dateOfBirth;
    private final LocalDate dateOfDeath;
    private final SolsAddress address;
}
