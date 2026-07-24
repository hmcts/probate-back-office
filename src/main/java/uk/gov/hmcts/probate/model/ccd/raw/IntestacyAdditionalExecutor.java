package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntestacyAdditionalExecutor {

    private final String additionalExecForenames;
    private final String additionalExecLastname;
    private final SolsAddress additionalExecAddress;
    private final SolsApplicantFamilyDetails solsApplicantFamilyDetails;
}
