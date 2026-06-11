package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolsApplicantFamilyDetails {
    private final DynamicRadioList relationship;
    private final String applicantRelationshipToDeceased;
    private final String coApplicantAdoptedIn;
    private final String coApplicantAdoptionInEnglandOrWales;
    private final String coApplicantAdoptedOut;
    private final String grandchildParentDieBeforeDeceased;
    private final String grandchildParentAdoptedIn;
    private final String grandchildParentAdoptionInEnglandOrWales;
    private final String grandchildParentAdoptedOut;
    private final String wholeNieceOrNephewParentDieBeforeDeceased;
    private final String halfNieceOrNephewParentDieBeforeDeceased;
}