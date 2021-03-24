package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CodicilAddedDate {
    private final LocalDate dateCodicilAdded;
}
