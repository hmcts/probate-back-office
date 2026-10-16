package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.SolsApplicantFamilyDetails;

import java.io.Serializable;

@Data
@Builder
public class Executor implements Serializable {

    private final boolean applying;
    private final SolsAddress address;
    private final String reasonNotApplying;
    private final String forename;
    private final String lastname;
    private final SolsApplicantFamilyDetails applicantFamilyDetails;
}
