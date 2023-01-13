package uk.gov.hmcts.probate.service.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(name = "aca-api-client", url = "${aca.api.url}")
public interface AssignCaseAccessClient {

    @PostMapping(
            value = "/case-assignments",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void assignCaseAccess(
            @RequestHeader(AUTHORIZATION) String authorisation,
            @RequestHeader("ServiceAuthorization") String serviceAuthorization,
            @RequestParam("use_user_token") boolean useUserToken,
            @RequestBody final AssignCaseAccessRequest assignCaseAccessRequest
    );

    @PostMapping(
            value = "noc/check-noc-approval",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void checkNocApproval(
            @RequestHeader(AUTHORIZATION) String authorisation,
            @RequestHeader("ServiceAuthorization") String serviceAuthorization,
            @RequestParam("use_user_token") boolean useUserToken,
            @RequestBody final AssignCaseAccessRequest assignCaseAccessRequest
    );
}
