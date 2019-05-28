package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.AuthenticateUserResponse;
import uk.gov.hmcts.probate.model.TokenExchangeResponse;

import java.util.Base64;

@Component
public class IdamAuthenticateUserService {

    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE = "code";
    private static final String BASIC = "Basic ";

    @Value("${auth.provider.client.id}")
    private String id;

    @Value("${auth.provider.client.secret}")
    private String secret;

    @Value("${auth.provider.client.redirect}")
    private String redirect;

    @Value("${auth.provider.client.email}")
    private String email;

    @Value("${auth.provider.client.password}")
    private String password;

    private final IdamApi idamApi;

    @Autowired
    public IdamAuthenticateUserService(
            IdamApi idamApi
    ) {
        this.idamApi = idamApi;
    }

    public String getIdamOauth2Token() {
        String authorisation = email + ":" + password;
        String base64Authorisation = Base64.getEncoder().encodeToString(authorisation.getBytes());

        AuthenticateUserResponse authenticateUserResponse = idamApi.authenticateUser(
                BASIC + base64Authorisation,
                CODE,
                id,
                redirect
        );

        TokenExchangeResponse tokenExchangeResponse = idamApi.exchangeCode(
                authenticateUserResponse.getCode(),
                AUTHORIZATION_CODE,
                redirect,
                id,
                secret
        );

        return BEARER + tokenExchangeResponse.getAccessToken();
    }

}