package uk.gov.hmcts.probate.functional.probateman;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IdamData {

    private String email;

    private String forename;

    private String surname;

    private String password;

    private int levelOfAccess;

    private UserGroup userGroup;

    private List<Role> roles;
}
