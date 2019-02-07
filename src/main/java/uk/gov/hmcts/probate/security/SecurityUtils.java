package uk.gov.hmcts.probate.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final AuthTokenGenerator authTokenGenerator;

    private final HttpServletRequest httpServletRequest;

    private static final String USER_ID = "user-id";

    private static final String AUTHORIZATION = "Authorization";

    public SecurityDTO getSecurityDTO() {
        return SecurityDTO.builder()
            .authorisation(httpServletRequest.getHeader(AUTHORIZATION))
            .userId(httpServletRequest.getHeader(USER_ID))
            .serviceAuthorisation(generateServiceToken())
            .build();
    }

    public String generateServiceToken() {
        return authTokenGenerator.generate();
    }
}
