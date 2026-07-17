package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
public class AddedRepresentative {

    @CCD(label = "Organization Id")
    private final String organisationID;
    @CCD(label = "Updated By")
    private final String updatedBy;
    @CCD(label = "Solicitor First Name")
    private final String solicitorFirstName;
    @CCD(label = "Solicitor Last Name")
    private final String solicitorLastName;
    @CCD(label = "Update via")
    private final String updatedVia;
    @CCD(label = "Channel choice")
    private final String channelChoice;
}
