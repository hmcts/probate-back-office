package uk.gov.hmcts.probate.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.response.AuditEvent;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.AuditEventService;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserInfoServiceTest {
    private UserInfoServiceImpl userInfoService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private AuditEventService auditEventService;

    @Mock
    private UserDetails userDetails;

    private static final long CASE_ID = 123456L;
    private static final String USER_ID = "user-789";
    private static final String EMAIL = "test@example.com";
    private static final String AUTH_TOKEN = "auth-token";
    private static final String SERVICE_AUTH_TOKEN = "service-auth-token";
    private SecurityDTO securityDTO;
    private AuditEvent auditEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityDTO = mock(SecurityDTO.class);
        auditEvent = mock(AuditEvent.class);

        userInfoService = new UserInfoServiceImpl(auditEventService, securityUtils);
        when(securityDTO.getAuthorisation()).thenReturn(AUTH_TOKEN);
        when(securityDTO.getServiceAuthorisation()).thenReturn(SERVICE_AUTH_TOKEN);

    }

    @Test
    void shouldReturnEmptyWhenSecurityDTOIsNull() {
        when(securityUtils.getSecurityDTO()).thenReturn(null);
        Optional<UserInfo> result = userInfoService.getCaseworkerInfo();

        assertTrue(result.isEmpty());
        verify(securityUtils, never()).getUserInfo(any());
    }

    @Test
    void shouldReturnEmptyWhenUserInfoDoesNotHaveCaseworkerRole() {
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityDTO.getAuthorisation()).thenReturn(AUTH_TOKEN);

        UserInfo userInfo = UserInfo.builder().roles(List.of("some-other-role")).build();
        when(securityUtils.getUserInfo(AUTH_TOKEN)).thenReturn(userInfo);
        Optional<UserInfo> result = userInfoService.getCaseworkerInfo();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenUserInfoHasExcludedRole() {
        securityDTO = mock(SecurityDTO.class);
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityDTO.getAuthorisation()).thenReturn(AUTH_TOKEN);

        UserInfo userInfo = UserInfo.builder().roles(List.of("caseworker-probate-systemupdate")).build();
        when(securityUtils.getUserInfo("authToken")).thenReturn(userInfo);
        Optional<UserInfo> result = userInfoService.getCaseworkerInfo();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnUserInfoWhenUserHasCaseworkerRoleAndNoExcludedRoles() {
        securityDTO = mock(SecurityDTO.class);
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityDTO.getAuthorisation()).thenReturn(AUTH_TOKEN);

        UserInfo userInfo = UserInfo.builder().roles(List.of("caseworker-probate", "some-other-role")).build();
        when(securityUtils.getUserInfo(AUTH_TOKEN)).thenReturn(userInfo);

        Optional<UserInfo> result = userInfoService.getCaseworkerInfo();
        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> assertEquals(userInfo, result.get())
        );
    }

    @Test
    void shouldReturnUserEmailWhenAllDataExists() {
        when(securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(auditEventService.getLatestAuditEventByName(
                any(),
                any(),
                eq(AUTH_TOKEN),
                eq(SERVICE_AUTH_TOKEN))
        ).thenReturn(Optional.of(auditEvent));

        when(auditEvent.getUserId()).thenReturn(USER_ID);
        when(securityUtils.getUserDetailsByUserId(AUTH_TOKEN, USER_ID)).thenReturn(userDetails);
        when(userDetails.getEmail()).thenReturn(EMAIL);

        Optional<String> result = userInfoService.getUserEmailByCaseId(CASE_ID);

        assertTrue(result.isPresent());
        assertEquals(EMAIL, result.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhenSecurityDTOIsNull() {
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(null);

        Optional<String> result = userInfoService.getUserEmailByCaseId(CASE_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhenAuditEventNotFound() {
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(auditEventService.getLatestAuditEventByName(
                any(),
                any(),
                eq(AUTH_TOKEN),
                eq(SERVICE_AUTH_TOKEN))
        ).thenReturn(Optional.empty());

        Optional<String> result = userInfoService.getUserEmailByCaseId(CASE_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserDetailsNotFound() {
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(auditEventService.getLatestAuditEventByName(
                any(),
                any(),
                eq(AUTH_TOKEN),
                eq(SERVICE_AUTH_TOKEN))
        ).thenReturn(Optional.of(auditEvent));

        when(auditEvent.getUserId()).thenReturn(USER_ID);
        when(securityUtils.getUserDetailsByUserId(AUTH_TOKEN, USER_ID)).thenReturn(null);

        Optional<String> result = userInfoService.getUserEmailByCaseId(CASE_ID);

        assertTrue(result.isEmpty());
    }
}