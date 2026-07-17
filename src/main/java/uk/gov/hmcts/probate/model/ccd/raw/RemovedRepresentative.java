package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.ccd.sdk.api.CCD;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
public class RemovedRepresentative {

    @CCD(label = "Organization Id")
    private final String organisationID;
    @CCD(label = "Solicitor First Name")
    private final String solicitorFirstName;
    @CCD(label = "Solicitor Last Name")
    private final String solicitorLastName;
    @CCD(label = "Solicitor EmailAddress")
    private final String solicitorEmail;
    @CCD(label = "Organization")
    private final Organisation organisation;
}
