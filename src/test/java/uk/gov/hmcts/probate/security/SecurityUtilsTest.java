package uk.gov.hmcts.probate.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.model.AuthenticateUserResponse;
import uk.gov.hmcts.probate.model.TokenExchangeResponse;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityUtilsTest {

    public static final String CODE = "CODE_VAL";
    private static final String SERVICE_TOKEN = "XXXXXX12345";
    private static final String USER_TOKEN = "1312jdhdh";
    private static final String CASEWORKER_PASSWORD = "caseworkerPassword";
    private static final String CASEWORKER_USER_NAME = "caseworkerUserName";
    private static final String AUTH_CLIENT_SECRET = "authClientSecret";
    private static final String AUTH_CLIENT_ID = "authClientId";
    private static final String REDIRECT = "http://redirect";
    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    private IdamApi idamClient;

    @InjectMocks
    private SecurityUtils securityUtils;


    @Test
    public void shouldGetAuthorisation() {
        TestSecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("user", USER_TOKEN, "ROLE_USER"));

        String authorisation = securityUtils.getAuthorisation();

        assertThat(authorisation, equalTo(USER_TOKEN));
    }

    @Test
    public void shouldSecurityContextUserAsCaseworker() {
        ReflectionTestUtils.setField(securityUtils, "authRedirectUrl", REDIRECT);
        ReflectionTestUtils.setField(securityUtils, "authClientId", AUTH_CLIENT_ID);
        ReflectionTestUtils.setField(securityUtils, "authClientSecret", AUTH_CLIENT_SECRET);
        ReflectionTestUtils.setField(securityUtils, "caseworkerUserName", CASEWORKER_USER_NAME);
        ReflectionTestUtils.setField(securityUtils, "caseworkerPassword", CASEWORKER_PASSWORD);

        AuthenticateUserResponse authenticateUserResponse = AuthenticateUserResponse.builder().code(CODE).build();
        when(idamClient.authenticateUser(anyString(), eq("code"), eq(AUTH_CLIENT_ID), eq(REDIRECT)))
            .thenReturn(authenticateUserResponse);

        TokenExchangeResponse tokenExchangeResponse = TokenExchangeResponse.builder()
            .accessToken(USER_TOKEN)
            .build();

        when(idamClient.exchangeCode(eq(CODE), eq("authorization_code"), eq(REDIRECT), eq(AUTH_CLIENT_ID),
            eq(AUTH_CLIENT_SECRET)))
            .thenReturn(tokenExchangeResponse);

        securityUtils.setSecurityContextUserAsCaseworker();

        assertThat(securityUtils.getAuthorisation(), equalTo(USER_TOKEN));
    }
}
