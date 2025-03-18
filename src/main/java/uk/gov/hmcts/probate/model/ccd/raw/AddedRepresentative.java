package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
public class AddedRepresentative {

    private final String organisationID;
    private final String updatedBy;
    private final String solicitorFirstName;
    private final String solicitorLastName;
    private final String updatedVia;
    private final String channelChoice;
}
