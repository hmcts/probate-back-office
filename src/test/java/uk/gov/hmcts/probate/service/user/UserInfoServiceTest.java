package uk.gov.hmcts.probate.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserInfoServiceTest {
    private UserInfoServiceImpl userInfoService;

    @Mock
    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userInfoService = new UserInfoServiceImpl(securityUtils);
    }

    @Test
    void shouldReturnEmptyWhenSecurityDTOIsNull() {
        when(securityUtils.getSecurityDTO()).thenReturn(null);
        Optional<UserInfo> result = userInfoService.getCaseworkerInfo();

        assertThat(result).isEmpty();
        verify(securityUtils, never()).getUserInfo(any());
    }

    @Test
    void shouldReturnEmptyWhenUserInfoDoesNotHaveCaseworkerRole() {
        SecurityDTO securityDTO = mock(SecurityDTO.class);
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityDTO.getAuthorisation()).thenReturn("authToken");

        UserInfo userInfo = UserInfo.builder().roles(List.of("some-other-role")).build();
        when(securityUtils.getUserInfo("authToken")).thenReturn(userInfo);
        Optional<UserInfo> result = userInfoService.getCaseworkerInfo();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenUserInfoHasExcludedRole() {
        SecurityDTO securityDTO = mock(SecurityDTO.class);
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityDTO.getAuthorisation()).thenReturn("authToken");

        UserInfo userInfo = UserInfo.builder().roles(List.of("caseworker-probate-systemupdate")).build();
        when(securityUtils.getUserInfo("authToken")).thenReturn(userInfo);
        Optional<UserInfo> result = userInfoService.getCaseworkerInfo();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnUserInfoWhenUserHasCaseworkerRoleAndNoExcludedRoles() {

        SecurityDTO securityDTO = mock(SecurityDTO.class);
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityDTO.getAuthorisation()).thenReturn("authToken");

        UserInfo userInfo = UserInfo.builder().roles(List.of("caseworker-probate", "some-other-role")).build();
        when(securityUtils.getUserInfo("authToken")).thenReturn(userInfo);

        Optional<UserInfo> result = userInfoService.getCaseworkerInfo();

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userInfo);
    }
}