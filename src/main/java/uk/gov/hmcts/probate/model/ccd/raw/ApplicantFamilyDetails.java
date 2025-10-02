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
    private final String grandchildAdoptedIn;
    private final String grandchildAdoptionInEnglandOrWales;
    private final String grandchildAdoptedOut;

}
