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
    private final String wholeNieceOrNephewParentAdoptedIn;
    private final String wholeNieceOrNephewParentAdoptionInEnglandOrWales;
    private final String wholeNieceOrNephewParentAdoptedOut;
    private final String halfNieceOrNephewParentDieBeforeDeceased;
    private final String halfNieceOrNephewParentAdoptedIn;
    private final String halfNieceOrNephewParentAdoptionInEnglandOrWales;
    private final String halfNieceOrNephewParentAdoptedOut;
}