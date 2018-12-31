package uk.gov.hmcts.probate.model.ccd.willlodgement.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

@JsonDeserialize(builder = WillLodgementData.WillLodgementDataBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WillLodgementData {

    private String wlApplicantNameSection;

    private String wlApplicantForenames;

    private String wlApplicantSurname;

    private SolsAddress wlApplicantAddress;

    private String wlApplicantEmailAddress;

    private String wlApplicantReferenceNumber;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class WillLodgementDataBuilder {
    }
}
