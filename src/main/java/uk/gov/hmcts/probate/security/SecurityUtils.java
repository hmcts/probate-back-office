package uk.gov.hmcts.probate.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.AuthenticateUserResponse;
import uk.gov.hmcts.probate.model.TokenExchangeResponse;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.probate.service.IdamAuthenticateUserService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.Base64;
import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityUtils {

    private final AuthTokenGenerator authTokenGenerator;

    private final HttpServletRequest httpServletRequest;

    private static final String USER_ID = "user-id";

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic ";
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE = "code";

    @Value("${auth.provider.client.redirect}")
    private String authRedirectUrl;

    @Value("${auth.provider.client.id}")
    private String authClientId;

    @Value("${auth.provider.client.secret}")
    private String authClientSecret;

    @Value("${auth.provider.client.email}")
    private String caseworkerUserName;

    @Value("${auth.provider.client.password}")
    private String caseworkerPassword;

    private final IdamApi idamClient;

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
        String basicAuthHeader = getBasicAuthHeader(username, password);

        log.info("Client ID: {} . Authenticating...", authClientId);

        AuthenticateUserResponse authenticateUserResponse = idamClient.authenticateUser(
                basicAuthHeader,
                CODE,
                authClientId,
                authRedirectUrl
        );

        log.info("Authenticated. Exchanging...");
        TokenExchangeResponse tokenExchangeResponse = idamClient.exchangeCode(
                authenticateUserResponse.getCode(),
                AUTHORIZATION_CODE,
                authRedirectUrl,
                authClientId,
                authClientSecret
        );

        log.info("Getting AccessToken...");
        return tokenExchangeResponse.getAccessToken();
    }

    private String getBasicAuthHeader(String username, String password) {
        String authorisation = username + ":" + password;
        return BASIC + Base64.getEncoder().encodeToString(authorisation.getBytes());
    }
}
