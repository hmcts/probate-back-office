package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Applicant implements Serializable {
    private final String isApplicantParentDeceasedChild;
    private final String primaryApplicantAdoptedIn;
    private final String primaryApplicantParentAdoptedIn;
    private final String primaryApplicantParentAdoptionInEnglandOrWales;
    private final String primaryApplicantParentAdoptedOut;
    private final String primaryApplicantAdoptionInEnglandOrWales;
    private final String primaryApplicantAdoptedOut;
    private final String anyLivingWholeBloodSiblings;
}
