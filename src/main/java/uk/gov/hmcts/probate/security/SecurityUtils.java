package uk.gov.hmcts.probate.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.ServiceAndUserDetails;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.probate.model.idam.TokenRequest;
import uk.gov.hmcts.reform.probate.model.idam.TokenResponse;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityUtils {

    private final AuthTokenGenerator authTokenGenerator;

    private final HttpServletRequest httpServletRequest;

    private final IdamApi idamApi;

    private static final String USER_ID = "user-id";
    private static final String OPENID_GRANT_TYPE = "password";

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private TokenResponse cacheTokenResponse;

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
    @Value("${probate.scheduler.username}")
    private String schedulerUserName;

    @Value("${probate.scheduler.password}")
    private String schedulerPassword;

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

    public SecurityDTO getUserBySchedulerTokenAndServiceSecurityDTO() {
        String token = getSchedulerToken();
        return SecurityDTO.builder()
                .authorisation(token)
                .serviceAuthorisation(generateServiceToken())
                .userId(getUserId(token))
                .build();
    }

    public String getUserId(String authToken) {
        UserInfo userInfo = idamApi.retrieveUserInfo(authToken);
        return Objects.requireNonNull(userInfo.getUid());
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

    public String getCaseworkerToken() {
        return getIdamOauth2Token(caseworkerUserName, caseworkerPassword);
    }

    public String getSchedulerToken() {
        return getIdamOauth2TokenScheduler(schedulerUserName, schedulerPassword);
    }

    private String getIdamOauth2TokenScheduler(String username, String password) {
        log.info("user and password for debug {},{}",username,password);
        TokenResponse idamOpenIdTokenResponse;
        log.info("Client ID: {} . Authenticating...", authClientId);
        return "abc";
        /*try {
            log.info("No cached IDAM token found, requesting from IDAM service.");
            //idamOpenIdTokenResponse = getOpenIdTokenResponse(username, password);
            log.info("Getting AccessToken...");
            //return BEARER + idamOpenIdTokenResponse.accessToken;
            return "abc";
        } catch (Exception e) {
            log.error("Exception on IDAM token" + e.getMessage());
            throw e;
        }*/
    }

    private String getIdamOauth2Token(String username, String password) {

        TokenResponse idamOpenIdTokenResponse;
        log.info("Client ID: {} . Authenticating...", authClientId);
        try {
            if (ObjectUtils.isEmpty(cacheTokenResponse) || isExpired(cacheTokenResponse)) {
                log.info("No cached IDAM token found, requesting from IDAM service.");
                idamOpenIdTokenResponse = getOpenIdTokenResponse(username, password);
            } else {
                log.info("Using cached IDAM token.");
                idamOpenIdTokenResponse = cacheTokenResponse;
            }
            log.info("Getting AccessToken...");
            return BEARER + idamOpenIdTokenResponse.accessToken;
        } catch (Exception e) {
            log.error("Exception on IDAM token" + e.getMessage());
            throw e;
        }
    }

    private TokenResponse getOpenIdTokenResponse(String username, String password) {
        TokenResponse tokenResponse = idamApi.generateOpenIdToken(
                new TokenRequest(
                        authClientId,
                        authClientSecret,
                        OPENID_GRANT_TYPE,
                        authRedirectUrl,
                        username,
                        password,
                        "openid profile roles",
                        null,
                        null
                ));
        cacheTokenResponse = tokenResponse;
        return cacheTokenResponse;
    }

    private boolean isExpired(TokenResponse tokenResponse) {
        Instant now = Instant.now();
        log.info("IDAM token expire time {}", tokenResponse.getExpiresAtTime());
        Instant expiresAt = ZonedDateTime.parse(tokenResponse.getExpiresAtTime()).toInstant();
        return now.isAfter(expiresAt.minus(Duration.ofMinutes(1L)));
    }

    public String getEmail(String authToken) {
        UserInfo userInfo = idamApi.retrieveUserInfo(authToken);
        String result = Objects.requireNonNull(userInfo.getSub());
        return result.toLowerCase();
    }
}
