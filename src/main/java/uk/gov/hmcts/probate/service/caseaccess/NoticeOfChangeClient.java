package uk.gov.hmcts.probate.service.caseaccess;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.probate.model.caseaccess.AssignCaseAccessRequest;
import uk.gov.hmcts.probate.model.caseaccess.DecisionCCDRequest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(name = "aca-api-client", url = "${aca.api.url}")
public interface NoticeOfChangeClient {
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

    @PostMapping(
            value = "/noc/apply-decision",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    AboutToStartOrSubmitCallbackResponse applyDecision(
            @RequestHeader(AUTHORIZATION) String authorisation,
            @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
            @RequestBody DecisionCCDRequest decisionRequest);
}