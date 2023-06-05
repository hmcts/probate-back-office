package uk.gov.hmcts.probate.model.noc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static com.fasterxml.jackson.annotation.Nulls.AS_EMPTY;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FindUsersByOrganisationResponse {

    @JsonSetter(nulls = AS_EMPTY)
    private List<ProfessionalUser> users;

    private String organisationIdentifier;
}
