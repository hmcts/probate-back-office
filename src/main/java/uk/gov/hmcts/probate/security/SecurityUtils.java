package uk.gov.hmcts.probate.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.ServiceAndUserDetails;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.idam.client.IdamClient;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityUtils {

    private final AuthTokenGenerator authTokenGenerator;

    private final HttpServletRequest httpServletRequest;

    private static final String USER_ID = "user-id";

    private static final String AUTHORIZATION = "Authorization";

    @Value("${auth.provider.client.id}")
    private String authClientId;

    @Value("${auth.provider.client.email}")
    private String caseworkerUserName;

    @Value("${auth.provider.client.password}")
    private String caseworkerPassword;

    private final IdamClient idamClient;

    public SecurityDTO getSecurityDTO() {
        return SecurityDTO.builder()
            .authorisation(httpServletRequest.getHeader(AUTHORIZATION))
            .userId(httpServletRequest.getHeader(USER_ID))
            .serviceAuthorisation(generateServiceToken())
            .build();
    }

    public SecurityDTO getUserAndServiceSecurityDTO() {
        return SecurityDTO.builder()
            .authorisation(httpServletRequest.getHeader(AUTHORIZATION))
            .userId(getUserId())
            .serviceAuthorisation(generateServiceToken())
            .build();
    }

    public String getUserId() {
        return ((ServiceAndUserDetails) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal())
            .getUsername();
    }

    public String generateServiceToken() {
        return authTokenGenerator.generate();
    }

    public String getAuthorisation() {
        return (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials();
    }

    public void setSecurityContextUserAsCaseworker() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(caseworkerUserName, getCaseworkerToken()));
    }

    private String getCaseworkerToken() {
        return getIdamOauth2Token(caseworkerUserName, caseworkerPassword);
    }

    private String getIdamOauth2Token(String username, String password) {
        log.info("Client ID: {} . Authenticating...", authClientId);
        log.info("Getting AccessToken...");
        return idamClient.getAccessToken(username, password);
    }
}
