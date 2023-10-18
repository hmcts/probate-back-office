package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
public class RemovedRepresentative {

    private final String organisationID;
    private final String solicitorFirstName;
    private final String solicitorLastName;
    private final String solicitorEmail;
    private final Organisation organisation;
}
