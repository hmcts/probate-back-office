package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DeathRecord {
    private final Integer systemNumber;
    private final String name;
    private final LocalDate dateOfBirth;
    private final String sex;
    private final String address;
    private final LocalDate dateOfDeath;
}
