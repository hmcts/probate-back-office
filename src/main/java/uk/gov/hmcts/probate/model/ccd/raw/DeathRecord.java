package uk.gov.hmcts.probate.model.ccd.raw;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeathRecord {
    private final Integer systemNumber;
    private final String name;
    private final LocalDate dateOfBirth;
    private final String sex;
    private final String address;
    private final LocalDate dateOfDeath;

//    TODO where do these come from?
//    private final String subDistrict;
//    private final String district;
//    private final String administrativeArea ;
//    private final String dateOfRegistration;
}
