package uk.gov.hmcts.probate.model.idam;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class User {
    private String email;
    private String id;

    private String forename;

    private String surname;

    private String displayName;
    private List<String> roleNames;
    private String ssoId;
    private String ssoProvider;
    private AccountStatus accountStatus;
    private RecordType recordType;
    private ZonedDateTime createDate;
    private ZonedDateTime lastModified;
    private ZonedDateTime accessLockedDate;
    private ZonedDateTime lastLoginDate;

    @JsonIgnore
    private String revision;
}