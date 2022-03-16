package uk.gov.hmcts.probate.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.idam.client.IdamClient;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityUtilsTest {

    private static final String USER_TOKEN = "1312jdhdh";
    private static final String CASEWORKER_PASSWORD = "caseworkerPassword";
    private static final String CASEWORKER_USER_NAME = "caseworkerUserName";
    private static final String AUTH_CLIENT_ID = "authClientId";

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    private IdamClient idamClient;

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

        ReflectionTestUtils.setField(securityUtils, "authClientId", AUTH_CLIENT_ID);
        ReflectionTestUtils.setField(securityUtils, "caseworkerUserName", CASEWORKER_USER_NAME);
        ReflectionTestUtils.setField(securityUtils, "caseworkerPassword", CASEWORKER_PASSWORD);

        when(idamClient.getAccessToken(anyString(), anyString()))
                .thenReturn(USER_TOKEN);

        securityUtils.setSecurityContextUserAsCaseworker();

        assertThat(securityUtils.getAuthorisation(), equalTo(USER_TOKEN));
    }
}
