package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicantFamilyDetails {
    private final String relationshipToDeceased;
    private final String childAdoptedIn;
    private final String childAdoptionInEnglandOrWales;
    private final String childAdoptedOut;
    private final String childDieBeforeDeceased;
    private final String grandchildParentAdoptedIn;
    private final String grandchildParentAdoptionInEnglandOrWales;
    private final String grandchildParentAdoptedOut;
    private final String grandchildAdoptedIn;
    private final String grandchildAdoptionInEnglandOrWales;
    private final String grandchildAdoptedOut;
    private final String halfBloodSiblingAdoptedIn;
    private final String halfBloodSiblingAdoptionInEnglandOrWales;
    private final String halfBloodSiblingAdoptedOut;
    private final String halfBloodSiblingDiedBeforeDeceased;
    private final String halfBloodNieceOrNephewAdoptedIn;
    private final String halfBloodNieceOrNephewAdoptionInEnglandOrWales;
    private final String halfBloodNieceOrNephewAdoptedOut;
}
