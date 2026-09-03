package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.ApplicantFamilyDetails;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import java.io.Serializable;

@Data
@Builder
public class ExecutorApplying implements Serializable {

    private final boolean applying;
    private final String forename;
    private final String lastname;
    private final ApplicantFamilyDetails applicantFamilyDetails;
    private final SolsAddress address;
}
