package uk.gov.hmcts.probate.model.ccd.caveat.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class CaveatData {

    // EVENT = cavRaiseCaveat - deceased data

    private final String cavDeceasedForenames;

    private final String cavDeceasedSurname;

    private final LocalDate cavDeceasedDateOfDeath;

    // EVENT = cavRaiseCaveat - caveator data

    private final String cavCaveatorForenames;

    private final String cavCaveatorSurname;

    private final String cavCaveatorEmailAddress;
}
