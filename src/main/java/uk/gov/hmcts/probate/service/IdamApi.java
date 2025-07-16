package uk.gov.hmcts.probate.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.probate.config.FeignClientConfiguration;
import uk.gov.hmcts.probate.model.TokenExchangeResponse;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;
import uk.gov.hmcts.reform.probate.model.idam.TokenRequest;
import uk.gov.hmcts.reform.probate.model.idam.TokenResponse;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@FeignClient(name = "idam-api", url = "${auth.provider.client.user}", configuration = FeignClientConfiguration.class)
public interface IdamApi {

    @PostMapping(
        value = "/oauth2/token",
        headers = CONTENT_TYPE + "=" + APPLICATION_FORM_URLENCODED_VALUE,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    TokenExchangeResponse exchangeCode(
        @RequestParam("code") final String code,
        @RequestParam("grant_type") final String grantType,
        @RequestParam("redirect_uri") final String redirectUri,
        @RequestParam("client_id") final String clientId,
        @RequestParam("client_secret") final String clientSecret
    );

    @GetMapping(
        value = "/details",
        headers = CONTENT_TYPE + "=" + APPLICATION_FORM_URLENCODED_VALUE,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    ResponseEntity<Map<String, Object>> getUserDetails(
        @RequestHeader(HttpHeaders.AUTHORIZATION) final String authorisation
    );

    @GetMapping("/o/userinfo")
    UserInfo retrieveUserInfo(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation
    );

    @PostMapping(
        value = "/o/token",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    TokenResponse generateOpenIdToken(@RequestBody TokenRequest tokenRequest);

    @GetMapping("/api/v1/users")
    List<UserDetails> searchUsers(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
            @RequestParam("query") final String elasticSearchQuery
    );
}
