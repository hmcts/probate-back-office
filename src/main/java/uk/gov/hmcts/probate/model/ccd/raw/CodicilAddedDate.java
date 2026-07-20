package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import uk.gov.hmcts.ccd.sdk.api.CCD;

@Data
@Builder
public class CodicilAddedDate implements Serializable {
    @CCD(label = " ")
    private final LocalDate dateCodicilAdded;
}
