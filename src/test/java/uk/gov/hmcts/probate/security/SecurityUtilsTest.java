package uk.gov.hmcts.probate.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.exception.NoSecurityContextException;
import uk.gov.hmcts.probate.exception.model.InvalidTokenException;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.probate.model.idam.TokenRequest;
import uk.gov.hmcts.reform.probate.model.idam.TokenResponse;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SecurityUtilsTest {

    public static final String CODE = "CODE_VAL";
    private static final String SERVICE_TOKEN = "XXXXXX12345";
    private static final String USER_TOKEN = "1312jdhdh";
    private static final String CASEWORKER_PASSWORD = "caseworkerPassword";
    private static final String CASEWORKER_USER_NAME = "caseworkerUserName";
    private static final String SCHEDULER_PASSWORD = "schedulerPassword";
    private static final String SCHEDULER_USER_NAME = "schedulerUserName";
    private static final String AUTH_CLIENT_SECRET = "authClientSecret";
    private static final String AUTH_CLIENT_ID = "authClientId";
    private static final String REDIRECT = "http://redirect";
    private static final String BEARER = "Bearer ";

    @InjectMocks
    private SecurityUtils securityUtils;

    @Mock
    private IdamApi idamApi;
    @Mock
    private AuthTokenGenerator authTokenGenerator;
    @Mock
    private AuthTokenValidator authTokenValidator;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldGetSecurityDTO() {
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("AUTH");
        when(httpServletRequestMock.getHeader("user-id")).thenReturn("USER");

        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        assertEquals("AUTH", securityDTO.getAuthorisation());
    }

    @Test
    void shouldGetAuthorisation() {
        TestSecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("user", USER_TOKEN, "ROLE_USER"));

        String authorisation = securityUtils.getAuthorisation();

        assertThat(authorisation, equalTo(USER_TOKEN));
    }

    @Test
    void shouldSecurityContextUserAsCaseworker() {
        ReflectionTestUtils.setField(securityUtils, "authRedirectUrl", REDIRECT);
        ReflectionTestUtils.setField(securityUtils, "authClientId", AUTH_CLIENT_ID);
        ReflectionTestUtils.setField(securityUtils, "authClientSecret", AUTH_CLIENT_SECRET);
        ReflectionTestUtils.setField(securityUtils, "caseworkerUserName", CASEWORKER_USER_NAME);
        ReflectionTestUtils.setField(securityUtils, "caseworkerPassword", CASEWORKER_PASSWORD);

        TokenResponse tokenResponse = new TokenResponse(USER_TOKEN,"360000",USER_TOKEN,null,null,null);
        when(idamApi.generateOpenIdToken(any(TokenRequest.class)))
            .thenReturn(tokenResponse);

        securityUtils.setSecurityContextUserAsCaseworker();

        assertThat(securityUtils.getAuthorisation(), equalTo(BEARER + USER_TOKEN));
    }

    @Test
    void shouldGetUserEmail() {
        UserInfo userInfo = UserInfo.builder().sub("solicitor@probate-test.com").build();
        when(idamApi.retrieveUserInfo("AuthToken")).thenReturn(userInfo);
        String email = securityUtils.getEmail("AuthToken");

        assertEquals("solicitor@probate-test.com", email);
    }

    @Test
    void shouldReturnCacheToken() {
        ReflectionTestUtils.setField(securityUtils, "caseworkerUserName", CASEWORKER_USER_NAME);
        ReflectionTestUtils.setField(securityUtils, "caseworkerPassword", CASEWORKER_PASSWORD);

        TokenResponse tokenResponse = new TokenResponse(USER_TOKEN,"360000",USER_TOKEN,null,null,null);
        when(idamApi.generateOpenIdToken(any(TokenRequest.class)))
                .thenReturn(tokenResponse);

        // first time
        String idamToken = securityUtils.getCaseworkerToken();

        assertThat(idamToken, containsString("Bearer " + USER_TOKEN));

        // second time
        idamToken = securityUtils.getCaseworkerToken();

        assertThat(idamToken, containsString("Bearer " + USER_TOKEN));

        verify(idamApi, atMostOnce()).generateOpenIdToken(any(TokenRequest.class));

    }

    @Test
    void shouldReturnSchedulerCacheToken() {
        ReflectionTestUtils.setField(securityUtils, "schedulerUserName", SCHEDULER_USER_NAME);
        ReflectionTestUtils.setField(securityUtils, "schedulerPassword", SCHEDULER_PASSWORD);

        TokenResponse tokenResponse = new TokenResponse(USER_TOKEN,"360000",USER_TOKEN,null,null,null);
        when(idamApi.generateOpenIdToken(any(TokenRequest.class)))
                .thenReturn(tokenResponse);

        // first time
        String idamToken = securityUtils.getSchedulerToken();

        assertThat(idamToken, containsString("Bearer " + USER_TOKEN));

        // second time
        idamToken = securityUtils.getSchedulerToken();

        assertThat(idamToken, containsString("Bearer " + USER_TOKEN));

        verify(idamApi, atMostOnce()).generateOpenIdToken(any(TokenRequest.class));
    }

    @Test
    void shouldReturnExceptionOnSchedulerToken() {
        ReflectionTestUtils.setField(securityUtils, "schedulerUserName", SCHEDULER_USER_NAME);
        ReflectionTestUtils.setField(securityUtils, "schedulerPassword", SCHEDULER_PASSWORD);

        assertThrows(RuntimeException.class, () -> {
            when(idamApi.generateOpenIdToken(any(TokenRequest.class)))
                    .thenReturn(null);
            securityUtils.getSchedulerToken();
        });

    }

    @Test
    void shouldReturnSchedulerToken() {
        ReflectionTestUtils.setField(securityUtils, "schedulerUserName", SCHEDULER_USER_NAME);
        ReflectionTestUtils.setField(securityUtils, "schedulerPassword", SCHEDULER_PASSWORD);
        UserInfo userInfo = UserInfo.builder().sub("ProbateSchedulerDEMO@probate-test.com")
                .uid("12344").build();
        when(idamApi.retrieveUserInfo(any())).thenReturn(userInfo);

        when(authTokenGenerator.generate()).thenReturn("Test");

        TokenResponse tokenResponse = new TokenResponse(USER_TOKEN,"360000",USER_TOKEN,null,null,null);
        when(idamApi.generateOpenIdToken(any(TokenRequest.class)))
                .thenReturn(tokenResponse);

        SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
        assertEquals("12344", securityDTO.getUserId());
        assertEquals("Bearer " + USER_TOKEN, securityDTO.getAuthorisation());
        assertEquals("Test", securityDTO.getServiceAuthorisation());
    }

    @Test
    void shouldetUserByCaseworkerTokenAndServiceSecurityDTO() {
        ReflectionTestUtils.setField(securityUtils, "caseworkerUserName", SCHEDULER_USER_NAME);
        ReflectionTestUtils.setField(securityUtils, "caseworkerPassword", SCHEDULER_PASSWORD);
        UserInfo userInfo = UserInfo.builder().sub("CWTest@probate-test.com")
                .uid("12344").build();
        when(idamApi.retrieveUserInfo(any())).thenReturn(userInfo);

        when(authTokenGenerator.generate()).thenReturn("CWTest");

        TokenResponse tokenResponse = new TokenResponse("CW_TOKEN","360000","CW_TOKEN_ID",null,null,null);
        when(idamApi.generateOpenIdToken(any(TokenRequest.class)))
                .thenReturn(tokenResponse);

        SecurityDTO securityDTO = securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO();
        assertEquals("12344", securityDTO.getUserId());
        assertEquals("Bearer CW_TOKEN", securityDTO.getAuthorisation());
        assertEquals("CWTest", securityDTO.getServiceAuthorisation());
    }

    @Test
    void givenTokenIsNull_whenGetBearToken_thenReturnNull() {
        testGetBearToken(null, null);
    }

    @Test
    void givenTokenIsBlank_whenGetBearToken_thenReturnBlank() {
        testGetBearToken(" ", " ");
    }

    @Test
    void givenTokenDoesNotHaveBearer_whenGetBearToken_thenReturnWithBearer() {
        testGetBearToken("TestToken", "Bearer TestToken");
    }

    @Test
    void givenTokenDoesHaveBearer_whenGetBearToken_thenReturnWithBearer() {
        testGetBearToken("Bearer TestToken", "Bearer TestToken");
    }

    private void testGetBearToken(String input, String expected) {
        assertEquals(securityUtils.getBearerToken(input), expected);
    }

    @Test
    void givenServiceNameIsAuthenticated() throws InvalidTokenException {
        when(authTokenValidator.getServiceName("Bearer TestService")).thenReturn("TestService");
        assertEquals("TestService", securityUtils.authenticate("TestService"));
    }

    @Test()
    void authenticateABlankToken() {
        assertThrows(InvalidTokenException.class, () -> {
            securityUtils.authenticate(" ");
        });
    }

    @Test
    void givenServiceNameIsNullFromToken() throws InvalidTokenException {
        when(authTokenValidator.getServiceName("Bearer TestService")).thenReturn(null);
        assertEquals(Boolean.FALSE, securityUtils.checkIfServiceIsAllowed("TestService"));
    }

    @Test
    void shouldGetUserRole() {
        UserInfo userInfo = UserInfo.builder().roles(List.of("caseworker-probate")).build();
        when(idamApi.retrieveUserInfo("AuthToken")).thenReturn(userInfo);
        List<String> roles = securityUtils.getRoles("AuthToken");

        assertEquals(List.of("caseworker-probate"), roles);
    }

    @Test
    void shouldGetUserInfo() {
        UserInfo userInfo = UserInfo.builder().build();
        when(idamApi.retrieveUserInfo(USER_TOKEN)).thenReturn(userInfo);
        UserInfo userInfoResponse = securityUtils.getUserInfo(USER_TOKEN);
        assertEquals(userInfo, userInfoResponse);
    }

    @Test
    void shouldGetUserDetailsById() {
        uk.gov.hmcts.reform.idam.client.models.UserDetails userDetails =
                uk.gov.hmcts.reform.idam.client.models.UserDetails.builder()
                        .id("1234")
                        .email("test@probate.com")
                        .build();
        when(idamApi.searchUsers(USER_TOKEN, "id:1234")).thenReturn(List.of(userDetails));
        uk.gov.hmcts.reform.idam.client.models.UserDetails userDetailsResponse  =
                securityUtils.getUserDetailsByUserId(USER_TOKEN, "1234");
        assertEquals(userDetails, userDetailsResponse);
    }

    @Test
    void shouldReturnSecurityDTOFromSecurityContextHolderWhenRequestHeadersAreNull() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(AUTH_CLIENT_ID, USER_TOKEN)
        );

        HttpServletRequest nullRequest = mock(HttpServletRequest.class);
        when(nullRequest.getHeader("Authorization")).thenReturn(null);
        when(nullRequest.getHeader("user-id")).thenReturn(null);
        when(authTokenGenerator.generate()).thenReturn(SERVICE_TOKEN);
        UserInfo userInfo = UserInfo.builder().uid(AUTH_CLIENT_ID).build();
        when(idamApi.retrieveUserInfo(any())).thenReturn(userInfo);

        SecurityDTO dto = securityUtils.getSecurityDTO();

        assertEquals(USER_TOKEN, dto.getAuthorisation());
        assertEquals(AUTH_CLIENT_ID, dto.getUserId());
        assertEquals(SERVICE_TOKEN, dto.getServiceAuthorisation());
    }

    @Test
    void shouldThrowExceptionWhenNoHeaderAndNoSecurityContext() {
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn(null);

        SecurityContextHolder.clearContext();

        assertThrows(NoSecurityContextException.class, () -> {
            securityUtils.getSecurityDTO();
        });
    }

    @Test
    void shouldGetUserIdIfHeaderMissing() {
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("AUTH_HEADER");
        when(httpServletRequestMock.getHeader("user-id")).thenReturn(null);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("userFromContext", "AUTH_HEADER")
        );
        when(authTokenGenerator.generate()).thenReturn(SERVICE_TOKEN);
        UserInfo userInfo = UserInfo.builder().uid("userFromContext").build();
        when(idamApi.retrieveUserInfo(any())).thenReturn(userInfo);

        SecurityDTO dto = securityUtils.getSecurityDTO();

        assertEquals("AUTH_HEADER", dto.getAuthorisation());
        assertEquals("userFromContext", dto.getUserId());
        assertEquals(SERVICE_TOKEN, dto.getServiceAuthorisation());
    }

    @Test
    void shouldReturnSecurityDTOWhenAvailableInContext() {
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("AUTH_HEADER");
        when(httpServletRequestMock.getHeader("user-id")).thenReturn("USER_ID");
        when(authTokenGenerator.generate()).thenReturn(SERVICE_TOKEN);

        SecurityDTO dto = securityUtils.getOrDefaultCaseworkerSecurityDTO();

        assertEquals("AUTH_HEADER", dto.getAuthorisation());
        assertEquals("USER_ID", dto.getUserId());
        assertEquals(SERVICE_TOKEN, dto.getServiceAuthorisation());
    }

    @Test
    void shouldFallbackToCaseworkerSecurityDTOWhenNoContextAvailable() {
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn(null);
        when(httpServletRequestMock.getHeader("user-id")).thenReturn(null);
        SecurityContextHolder.clearContext();

        when(authTokenGenerator.generate()).thenReturn("fallback-service");
        when(idamApi.generateOpenIdToken(any(TokenRequest.class)))
                .thenReturn(new TokenResponse("fallback-auth", "3600", "id", null, null, null));
        when(idamApi.retrieveUserInfo(any()))
                .thenReturn(UserInfo.builder().uid("fallback-user").build());

        SecurityDTO dto = securityUtils.getOrDefaultCaseworkerSecurityDTO();

        assertEquals("Bearer fallback-auth", dto.getAuthorisation());
        assertEquals("fallback-user", dto.getUserId());
        assertEquals("fallback-service", dto.getServiceAuthorisation());
    }
}
