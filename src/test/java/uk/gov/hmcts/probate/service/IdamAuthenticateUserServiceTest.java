package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.model.AuthenticateUserResponse;
import uk.gov.hmcts.probate.model.TokenExchangeResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class IdamAuthenticateUserServiceTest {
    @InjectMocks
    private IdamAuthenticateUserService idamAuthenticateUserService;

    @Mock
    private IdamApi idamApi;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetIdamOAuth2Token() {
        AuthenticateUserResponse user = AuthenticateUserResponse.builder().code("code").build();
        when(idamApi.authenticateUser(any(), any(), any(), any()))
            .thenReturn(user);
        TokenExchangeResponse token = TokenExchangeResponse.builder().accessToken("accessToken").build();
        when(idamApi.exchangeCode(any(), any(), any(), any(), any()))
            .thenReturn(token);
        String oauth2Token = idamAuthenticateUserService.getIdamOauth2Token();
        assertEquals("Bearer accessToken", oauth2Token);
    }

    @Test
    public void shouldGetUserEmail() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", "solicitor@probate-test.com");
        ResponseEntity<Map<String, Object>> response = ResponseEntity.of(Optional.of(map));
        when(idamApi.getUserDetails("AuthToken")).thenReturn(response);
        String email = idamAuthenticateUserService.getEmail("AuthToken");

        assertEquals("solicitor@probate-test.com", email);
    }
}