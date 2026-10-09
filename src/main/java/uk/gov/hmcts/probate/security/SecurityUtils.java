package uk.gov.hmcts.probate.security;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import uk.gov.hmcts.probate.exception.NoSecurityContextException;
import uk.gov.hmcts.probate.exception.model.InvalidTokenException;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;
import uk.gov.hmcts.reform.probate.model.idam.TokenRequest;
import uk.gov.hmcts.reform.probate.model.idam.TokenResponse;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import jakarta.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityUtils {

    private final AuthTokenGenerator authTokenGenerator;
    private final AuthTokenValidator authTokenValidator;

    private final HttpServletRequest httpServletRequest;
    private final IdamApi idamApi;

    private static final String USER_ID = "user-id";
    private static final String OPENID_GRANT_TYPE = "password";

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private TokenResponse cacheTokenResponse;

    private TokenResponse cacheSchedulerTokenResponse;

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

    @Value("${idam.s2s-auth.services-allowed-to-payment-update}")
    private List<String> allowedToUpdateDetails;

    public SecurityDTO getSecurityDTO() {
        String authorisation = getHeader(AUTHORIZATION);
        String userId = getHeader(USER_ID);

        if (StringUtils.isNotBlank(authorisation) && StringUtils.isNotBlank(userId)) {
            return buildSecurityDTO(authorisation, userId);
        }

        log.warn("Missing authorisation or userId from headers, checking SecurityContext");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (StringUtils.isBlank(authorisation)) {
                authorisation = getAuthCredentials(auth);
            }

            if (StringUtils.isBlank(userId)) {
                try {
                    userId = getUserId(authorisation);
                } catch (FeignException | NullPointerException e) {
                    log.error("Failed to resolve user ID from token: {}", e.getMessage());
                }
            }
        }

        if (StringUtils.isBlank(authorisation)) {
            log.error("Unable to resolve security context: missing authorisation token");
            throw new NoSecurityContextException();
        }
        if (StringUtils.isBlank(userId)) {
            log.error("Unable to resolve security context: missing userId");
            throw new NoSecurityContextException();
        }

        return buildSecurityDTO(authorisation, userId);
    }

    public SecurityDTO getOrDefaultCaseworkerSecurityDTO() {
        try {
            return getSecurityDTO();
        } catch (NoSecurityContextException e) {
            log.info("Unable to getSecurityDTO, use default caseworker token");
        }
        return getUserByCaseworkerTokenAndServiceSecurityDTO();
    }

    private String getHeader(String headerName) {
        try {
            return httpServletRequest != null ? httpServletRequest.getHeader(headerName) : null;
        } catch (IllegalStateException e) {
            log.warn("HttpServletRequest not available");
            return null;
        }
    }

    private String getAuthCredentials(Authentication auth) {
        return auth.getCredentials() != null ? auth.getCredentials().toString() : null;
    }

    public SecurityDTO getUserBySchedulerTokenAndServiceSecurityDTO() {
        String token = getSchedulerToken();
        return SecurityDTO.builder()
                .authorisation(token)
                .serviceAuthorisation(generateServiceToken())
                .userId(getUserId(token))
                .build();
    }

    public SecurityDTO getUserByCaseworkerTokenAndServiceSecurityDTO() {
        String token = getCaseworkerToken();
        return SecurityDTO.builder()
                .authorisation(token)
                .serviceAuthorisation(generateServiceToken())
                .userId(getUserId(token))
                .build();
    }

    public String getUserId(String authToken) {
        UserInfo userInfo = idamApi.retrieveUserInfo(getBearerToken(authToken));
        return Objects.requireNonNull(userInfo.getUid());
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

    public void setSecurityContextUserAsScheduler() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(schedulerUserName, getSchedulerToken()));
    }

    public String getCaseworkerToken() {
        return getIdamOauth2Token(caseworkerUserName, caseworkerPassword);
    }

    public String getSchedulerToken() {
        return getIdamOauth2TokenScheduler(schedulerUserName, schedulerPassword);
    }

    private String getIdamOauth2TokenScheduler(String username, String password) {
        TokenResponse idamOpenIdTokenResponse;
        log.info("Client ID: {} . Authenticating...", authClientId);
        try {
            if (ObjectUtils.isEmpty(cacheSchedulerTokenResponse) || isExpired(cacheSchedulerTokenResponse)) {
                log.info("No cached IDAM token found, requesting from IDAM service.");
                TokenResponse tokenResponse = idamApi.generateOpenIdToken(
                        new TokenRequest(
                                authClientId,
                                authClientSecret,
                                OPENID_GRANT_TYPE,
                                authRedirectUrl,
                                username,
                                password,
                                "openid profile roles search-user",
                                null,
                                null
                        ));
                cacheSchedulerTokenResponse = tokenResponse;
            }
            idamOpenIdTokenResponse = cacheSchedulerTokenResponse;
            log.info("Getting AccessToken...");
            return getBearerToken(idamOpenIdTokenResponse.accessToken);
        } catch (Exception e) {
            log.error("Exception on IDAM token" + e.getMessage());
            throw e;
        }
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
            return getBearerToken(idamOpenIdTokenResponse.accessToken);
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
                        "openid profile roles search-user",
                        null,
                        null
                ));
        cacheTokenResponse = tokenResponse;
        return cacheTokenResponse;
    }

    public Boolean checkIfServiceIsAllowed(String token) throws InvalidTokenException {
        String serviceName = this.authenticate(token);
        if (Objects.nonNull(serviceName)) {
            return allowedToUpdateDetails.contains(serviceName);
        } else {
            log.info("Service name from token is null");
            return Boolean.FALSE;
        }
    }

    public String getBearerToken(String token) {
        if (StringUtils.isBlank(token)) {
            return token;
        }

        return token.startsWith(BEARER) ? token : BEARER.concat(token);
    }

    public String authenticate(String authHeader) throws InvalidTokenException {
        if (StringUtils.isBlank(authHeader)) {
            throw new InvalidTokenException("Provided S2S token is missing or invalid");
        }
        String bearerAuthToken = getBearerToken(authHeader);
        log.info("S2S token found in the request");

        return authTokenValidator.getServiceName(bearerAuthToken);
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

    public List<String> getRoles(String authToken) {
        UserInfo userInfo = idamApi.retrieveUserInfo(authToken);
        return userInfo.getRoles();
    }

    public UserInfo getUserInfo(String authToken) {
        return idamApi.retrieveUserInfo(authToken);
    }

    public UserDetails getUserDetailsByUserId(String authToken, String userId) {
        log.info("Getting user details by userId: {}", userId);
        List<UserDetails> userList = idamApi.searchUsers(authToken, getSearchQuery(userId));
        return !userList.isEmpty() ? userList.get(0) : null;
    }

    private String getSearchQuery(String userId) {
        return MessageFormat.format("id:{0}", userId);
    }

    private SecurityDTO buildSecurityDTO(String authorisation, String userId) {
        return SecurityDTO.builder()
                .authorisation(authorisation)
                .userId(userId)
                .serviceAuthorisation(generateServiceToken())
                .build();
    }
}