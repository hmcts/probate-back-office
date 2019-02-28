package uk.gov.hmcts.probate.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecurityDTO {

    private String authorisation;

    private String userId;

    private String serviceAuthorisation;
}
