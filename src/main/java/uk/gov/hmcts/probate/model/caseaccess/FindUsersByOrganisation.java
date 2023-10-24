package uk.gov.hmcts.probate.model.caseaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class FindUsersByOrganisation {
    @JsonProperty("organisationIdentifier")
    private String organisationIdentifier;

    @JsonProperty("users")
    private List<SolicitorUser> users;
}
