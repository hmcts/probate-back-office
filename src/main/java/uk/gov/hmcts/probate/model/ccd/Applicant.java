package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Applicant implements Serializable {
    private final String childAlive;
    private final String primaryApplicantParentAdoptionInEnglandOrWales;
    private final String primaryApplicantParentAdoptedOut;
    private final String primaryApplicantAdoptionInEnglandOrWales;
    private final String primaryApplicantAdoptedOut;
    private final String anyLivingWholeBloodSiblings;
}
