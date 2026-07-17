package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
@AllArgsConstructor
public class ProbateAliasName {

    @CCD(label = "Alias first name(s)")
    @JsonProperty(value = "Forenames")
    private final String forenames;

    @CCD(label = "Alias last name")
    @JsonProperty(value = "LastName")
    private final String lastName;

    @CCD(label = "Appear on grant", typeOverride = FieldType.YesOrNo)
    @JsonProperty(value = "AppearOnGrant")
    private final String appearOnGrant;

}
