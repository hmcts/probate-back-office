package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
public class CodicilAddedDate implements Serializable {

    private final LocalDate dateCodicilAdded;
}