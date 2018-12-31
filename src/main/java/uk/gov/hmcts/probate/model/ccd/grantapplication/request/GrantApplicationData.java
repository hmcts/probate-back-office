package uk.gov.hmcts.probate.model.ccd.grantapplication.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import java.time.LocalDate;

@JsonDeserialize(builder = GrantApplicationData.GrantApplicationDataBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GrantApplicationData {

    private String gaPrimaryApplicantForenames;

    private String gaPrimaryApplicantSurname;

    private String gaDeceasedForenames;

    private String gaDeceasedSurname;

    private LocalDate gaDateOfBirth;

    private LocalDate gaDateOfDeath;

    private SolsAddress gaDeceasedAddress;

    private String wlApplicantEmailAddress;

    private String wlApplicantReferenceNumber;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class GrantApplicationDataBuilder {
    }
}
