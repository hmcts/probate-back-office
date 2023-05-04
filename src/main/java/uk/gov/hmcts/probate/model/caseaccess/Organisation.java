package uk.gov.hmcts.probate.model.caseaccess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organisation {

    @JsonProperty("OrganisationID")
    private String organisationID;

    @JsonProperty("OrganisationName")
    private String organisationName;
}