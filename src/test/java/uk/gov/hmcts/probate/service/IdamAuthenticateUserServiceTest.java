package uk.gov.hmcts.probate.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.model.TokenExchangeResponse;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import java.util.List;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atMostOnce;

@RunWith(MockitoJUnitRunner.class)
public class IdamAuthenticateUserServiceTest {

    private IdamAuthenticateUserService idamAuthenticateUserService;

    @Mock
    private Appender mockAppender;

    @Captor
    private ArgumentCaptor captorLoggingEvent;

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    private IdamClient idamClient;

    @Before
    public void setUp() {
        idamAuthenticateUserService = new IdamAuthenticateUserService(authTokenGenerator, idamClient);

        ReflectionTestUtils.setField(idamAuthenticateUserService, "email", "email");
        ReflectionTestUtils.setField(idamAuthenticateUserService, "password", "pass");

        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(mockAppender);
    }

    @Test
    public void shouldGetIdamOAuth2Token() {
        when(idamClient.getAccessToken(any(), any())).thenReturn("Bearer accessToken");
        String oauth2Token = idamAuthenticateUserService.getIdamOauth2Token();
        assertEquals("Bearer accessToken", oauth2Token);
    }

    @Test
    public void shouldReturnAuthTokenGivenNewRequestWithAppropriateLogMessages() {
        String auth = "auth";
        String userEmail = "solicitor@probate-test.com";
        TokenExchangeResponse token = TokenExchangeResponse.builder().accessToken("accessToken").build();
        when(authTokenGenerator.generate()).thenReturn(auth);

        when(idamClient.getAccessToken("email", "pass")).thenReturn("Bearer " + token.getAccessToken());

        UserInfo expectedUserDetails =
                new UserInfo(userEmail, "16", "solicitor", "Peter", "Pan", new ArrayList<>());

        given(idamClient.getUserInfo(eq("Bearer " + token.getAccessToken()))).willReturn(expectedUserDetails);

        IdamTokens idamTokens = idamAuthenticateUserService.getIdamTokens();
        assertThat(idamTokens.getServiceAuthorization(), is(auth));
        assertThat(idamTokens.getUserId(), is(expectedUserDetails.getUid()));
        assertThat(idamTokens.getEmail(), is(expectedUserDetails.getSub()));
        assertThat(idamTokens.getIdamOauth2Token(), containsString("Bearer access"));

        verify(mockAppender, times(4)).doAppend(captorLoggingEvent.capture());
        final List<LoggingEvent> loggingEvent = (List<LoggingEvent>) captorLoggingEvent.getAllValues();

        //Check the message being logged is correct
        assertThat(loggingEvent.get(0).getFormattedMessage(),
                is("No cached IDAM token found, requesting from IDAM service."));
        assertThat(loggingEvent.get(1).getFormattedMessage(),
                containsString("Attempting to obtain token, retry attempt"));
        assertThat(loggingEvent.get(2).getFormattedMessage(), is("Requesting idam access token from Open End Point"));
        assertThat(loggingEvent.get(3).getFormattedMessage(), is("Requesting idam access token successful"));
    }

    @Test
    public void shouldExceptionGivenErrorWithAppropriateLogMessages() {

        when(idamClient.getAccessToken("email", "pass")
        ).thenThrow(new RuntimeException());

        try {
            IdamTokens idamTokens = idamAuthenticateUserService.getIdamTokens();
        } catch (RuntimeException rte) {
            // Ignore for the purposes of this test
        }

        verify(mockAppender, times(4)).doAppend(captorLoggingEvent.capture());
        final List<LoggingEvent> loggingEvent = (List<LoggingEvent>) captorLoggingEvent.getAllValues();

        //Check the message being logged is correct
        assertThat(loggingEvent.get(0).getFormattedMessage(),
                is("No cached IDAM token found, requesting from IDAM service."));
        assertThat(loggingEvent.get(1).getFormattedMessage(),
                containsString("Attempting to obtain token, retry attempt"));
        assertThat(loggingEvent.get(2).getFormattedMessage(), is("Requesting idam access token from Open End Point"));
        assertThat(loggingEvent.get(3).getFormattedMessage(), containsString("Requesting idam token failed:"));
    }

    @Test
    public void shouldReturnCacheToken() {
        String auth = "auth";
        String userEmail = "solicitor@probate-test.com";
        TokenExchangeResponse token = TokenExchangeResponse.builder().accessToken("accessToken").build();
        when(authTokenGenerator.generate()).thenReturn(auth);

        when(idamClient.getAccessToken("email", "pass")).thenReturn("Bearer " + token.getAccessToken());

        UserInfo expectedUserDetails =
                new UserInfo(userEmail, "16", "solicitor", "Peter", "Pan", new ArrayList<>());

        given(idamClient.getUserInfo(eq("Bearer " + token.getAccessToken()))).willReturn(expectedUserDetails);

        // first time
        IdamTokens idamTokens = idamAuthenticateUserService.getIdamTokens();

        assertThat(idamTokens.getServiceAuthorization(), is(auth));
        assertThat(idamTokens.getUserId(), is(expectedUserDetails.getUid()));
        assertThat(idamTokens.getEmail(), is(expectedUserDetails.getSub()));
        assertThat(idamTokens.getIdamOauth2Token(), containsString("Bearer access"));

        // second time
        idamTokens = idamAuthenticateUserService.getIdamTokens();

        assertThat(idamTokens.getServiceAuthorization(), is(auth));
        assertThat(idamTokens.getUserId(), is(expectedUserDetails.getUid()));
        assertThat(idamTokens.getEmail(), is(expectedUserDetails.getSub()));
        assertThat(idamTokens.getIdamOauth2Token(), containsString("Bearer access"));

        verify(idamClient, atMostOnce()).getAccessToken("email", "pass");

    }

    @Test
    public void shouldGetUserEmail() {
        String auth = "auth";
        String userEmail = "solicitor@probate-test.com";
        TokenExchangeResponse token = TokenExchangeResponse.builder().accessToken("accessToken").build();
        when(authTokenGenerator.generate()).thenReturn(auth);

        when(idamClient.getAccessToken("email", "pass")).thenReturn("Bearer " + token.getAccessToken());

        UserInfo expectedUserDetails =
                new UserInfo(userEmail, "16", "solicitor", "Peter", "Pan", new ArrayList<>());

        given(idamClient.getUserInfo(eq("Bearer " + token.getAccessToken()))).willReturn(expectedUserDetails);
        String email = idamAuthenticateUserService.getEmail("AuthToken");

        assertEquals(userEmail, email);
    }
}
