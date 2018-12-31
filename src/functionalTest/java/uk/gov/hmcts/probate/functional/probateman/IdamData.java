package uk.gov.hmcts.probate.functional.probateman;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdamData {

    private String email;

    private String forename;

    private String surname;

    private String password;

    @JsonProperty("user_group_name")
    private String userGroupName;
}
