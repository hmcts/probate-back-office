package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetails {
    String id;
    String email;
    String name;
    String forename;
    String surname;
    List<String> roles;

    public UserDetails(@JsonProperty("id") String id,
                       @JsonProperty("email") String email,
                       @JsonProperty("roles") List<String> roles) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.name = null;
        this.forename = null;
        this.surname = null;
    }

    public UserDetails(@JsonProperty("id") String id,
                       @JsonProperty("email") String email,
                       @JsonProperty("name") String name,
                       @JsonProperty("forename") String forename,
                       @JsonProperty("surname") String surname,
                       @JsonProperty("roles") List<String> roles) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.forename = forename;
        this.surname = surname;
    }

    @JsonIgnore
    public boolean hasRole(UserRole role) {
        return roles != null && roles.contains(role.getValue());
    }

}
