package uk.gov.hmcts.probate.service;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.UserDetails;
import uk.gov.hmcts.probate.model.UserDetailsTransformer;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

@Slf4j
@Component
public class IdamAuthenticateUserService {

    public static final int ONE_HOUR = 1000 * 60 * 60;
    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Value("${auth.provider.client.email}")
    private String email;

    @Value("${auth.provider.client.password}")
    private String password;

    private final AuthTokenGenerator authTokenGenerator;

    private String cachedToken;

    private final IdamClient idamClient;

    @Autowired
    public IdamAuthenticateUserService(AuthTokenGenerator authTokenGenerator, IdamClient idamClient) {
        this.authTokenGenerator = authTokenGenerator;
        this.idamClient = idamClient;
    }

    public String generateServiceAuthorization() {
        return authTokenGenerator.generate();
    }

    @Retryable
    public String getUserId(String oauth2Token) {

        return idamClient.getUserDetails(oauth2Token).getId();
    }

    public UserDetails getUserDetails(String oauth2Token)  {
        UserInfo userInfo = idamClient.getUserInfo(oauth2Token);
        return new UserDetailsTransformer(userInfo).asLocalUserDetails();
    }

    public String getIdamOauth2Token() {
        cachedToken = getOpenAccessToken();
        return cachedToken;
    }

    public String getOpenAccessToken() {
        try {
            log.info("Requesting idam access token from Open End Point");
            String accessToken = idamClient.getAccessToken(email, password);
            log.info("Requesting idam access token successful");
            return accessToken;
        } catch (Exception e) {
            log.error("Requesting idam token failed: " + e.getMessage());
            throw e;
        }
    }

    @Scheduled(fixedRate = ONE_HOUR)
    public void evictCacheAtIntervals() {
        log.info("Evicting idam token cache");
        cachedToken = null;
    }

    @Retryable(backoff = @Backoff(delay = 15000L, multiplier = 1.0, random = true))
    public IdamTokens getIdamTokens() {

        String idamOauth2Token;

        if (StringUtils.isEmpty(cachedToken)) {
            log.info("No cached IDAM token found, requesting from IDAM service.");
            log.info("Attempting to obtain token, retry attempt {}", atomicInteger.getAndIncrement());
            idamOauth2Token =  getIdamOauth2Token();
        } else {
            atomicInteger.set(1);
            log.info("Using cached IDAM token.");
            idamOauth2Token =  cachedToken;
        }

        UserDetails userDetails = getUserDetails(idamOauth2Token);

        return IdamTokens.builder()
                .idamOauth2Token(idamOauth2Token)
                .serviceAuthorization(generateServiceAuthorization())
                .userId(userDetails.getId())
                .email(userDetails.getEmail())
                .roles(userDetails.getRoles())
                .build();
    }

    public String getEmail(String authToken) {
        return getUserDetails(authToken).getEmail();
    }

}
