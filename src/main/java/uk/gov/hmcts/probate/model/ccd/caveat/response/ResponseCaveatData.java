package uk.gov.hmcts.probate.model.ccd.caveat.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCaveatData {

    private final String cavDeceasedForenames;
    private final String cavDeceasedSurname;
    private final String cavDeceasedDateOfDeath;

    private final String cavCaveatorForenames;
    private final String cavCaveatorSurname;
    private final String cavCaveatorEmailAddress;

}
