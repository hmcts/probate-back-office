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
public class SolicitorUser {
    @JsonProperty("userIdentifier")
    private String userIdentifier;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("idamStatus")
    private String idamStatus;

    @JsonProperty("roles")
    private List<String> roles;

    @JsonProperty("idamStatusCode")
    private String idamStatusCode;

    @JsonProperty("idamMessage")
    private String idamMessage;
}
