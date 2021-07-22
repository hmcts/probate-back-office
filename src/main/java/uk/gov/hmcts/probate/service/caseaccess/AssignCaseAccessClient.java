package uk.gov.hmcts.probate.service.caseaccess;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(name = "aca-api-client", url = "${aca.api.url}")
public interface AssignCaseAccessClient {
    static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    static final String USE_USER_TOKEN = "use_user_token";

    @PostMapping(
        value = "/case-assignments",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void assignCaseAccess(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @RequestParam(USE_USER_TOKEN) boolean useUserToken,
        @RequestBody final AssignCaseAccessRequest assignCaseAccessRequest
    );
}