package uk.gov.hmcts.probate.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.probate.config.FeignClientConfiguration;
import uk.gov.hmcts.probate.model.AuthenticateUserResponse;
import uk.gov.hmcts.probate.model.TokenExchangeResponse;

@FeignClient(name = "idam-api", url = "${auth.provider.client.user}", configuration = FeignClientConfiguration.class)
public interface IdamApi {
    @PostMapping(
            value = "/oauth2/authorize",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    AuthenticateUserResponse authenticateUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) final String authorisation,
            @RequestParam("response_type") final String responseType,
            @RequestParam("client_id") final String clientId,
            @RequestParam("redirect_uri") final String redirectUri
    );

    @PostMapping(
            value = "/oauth2/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    TokenExchangeResponse exchangeCode(
            @RequestParam("code") final String code,
            @RequestParam("grant_type") final String grantType,
            @RequestParam("redirect_uri") final String redirectUri,
            @RequestParam("client_id") final String clientId,
            @RequestParam("client_secret") final String clientSecret
    );
}