package uk.gov.hmcts.probate.service;

import uk.gov.hmcts.reform.idam.client.models.UserInfo;

public class UserDetailsTransformer {

    private UserInfo reformUserInfo;

    public UserDetailsTransformer(UserInfo reformUserInfo) {

        this.reformUserInfo = reformUserInfo;
    }

    public UserDetails asLocalUserDetails() {
        return new UserDetails(
                this.reformUserInfo.getUid(),
                this.reformUserInfo.getSub(),
                this.reformUserInfo.getName(),
                this.reformUserInfo.getGivenName(),
                this.reformUserInfo.getFamilyName(),
                this.reformUserInfo.getRoles()
                );
    }
}


