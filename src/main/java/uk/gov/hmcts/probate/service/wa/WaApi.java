package uk.gov.hmcts.probate.service.wa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.probate.config.FeignClientConfiguration;
import uk.gov.hmcts.probate.model.wa.SearchEventAndCase;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.model.wa.GetTasksCompletableResponse;

@FeignClient(name = "wa-api", url = "${wa.provider.client.url}", configuration = FeignClientConfiguration.class)
public interface WaApi {
    String AUTHORIZATION = "Authorization";
    String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @Operation(description = "Retrieve a list of Task resources identified by set of search"
            + " criteria that are eligible for automatic completion",
            security = {@SecurityRequirement(name = SERVICE_AUTHORIZATION), @SecurityRequirement(name = AUTHORIZATION)})
    @PostMapping(path = "/task/search-for-completable")
    public ResponseEntity<GetTasksCompletableResponse<TaskData>> searchWithCriteriaForAutomaticCompletion(
            @Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String authToken,
            @Parameter(hidden = true) @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthToken,
            @RequestBody SearchEventAndCase searchEventAndCase
    );
}
