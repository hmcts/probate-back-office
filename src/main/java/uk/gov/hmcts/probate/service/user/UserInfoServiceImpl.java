package uk.gov.hmcts.probate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private static final List<String> EXCLUDED_ROLE_LIST = Arrays.asList(
            "caseworker-probate-systemupdate", "caseworker-probate-scheduler", "caseworker-probate-registrar",
            "caseworker-probate-rparobot", "idam-service-account");
    private static final String CASEWORKER_ROLE = "caseworker-probate";

    private final SecurityUtils securityUtils;

    @Override
    public Optional<UserInfo> getCaseworkerInfo() {
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        if (securityDTO == null) {
            return Optional.empty();
        }
        UserInfo userInfo = securityUtils.getUserInfo(securityDTO.getAuthorisation());
        return shouldSetAuthorName(userInfo) ? Optional.of(userInfo) : Optional.empty();
    }

    private boolean shouldSetAuthorName(UserInfo userInfo) {
        return userInfo.getRoles().stream().noneMatch(EXCLUDED_ROLE_LIST::contains)
            && userInfo.getRoles().contains(CASEWORKER_ROLE);
    }
}
