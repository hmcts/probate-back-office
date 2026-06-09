package uk.gov.hmcts.probate.service.user;

import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.Optional;

public interface UserInfoService {

    Optional<UserInfo> getCaseworkerInfo();

    Optional<String> getUserEmailByCaseId(Long caseId);
}
