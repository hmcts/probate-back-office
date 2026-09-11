package uk.gov.hmcts.probate.service.wa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.probate.config.FeignClientConfiguration;
import uk.gov.hmcts.probate.model.wa.GetTasksResponse;
import uk.gov.hmcts.probate.model.wa.SearchTaskRequest;
import uk.gov.hmcts.probate.model.wa.TaskData;

@FeignClient(name = "wa-api", url = "${wa.provider.client.url}", configuration = FeignClientConfiguration.class)
public interface WaApi {
    String AUTHORIZATION = "Authorization";
    String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @Operation(description = "Retrieve a list of Task resources identified by set of search criteria.",
            security = {@SecurityRequirement(name = SERVICE_AUTHORIZATION), @SecurityRequirement(name = AUTHORIZATION)})
    @PostMapping(
            value = "/task",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GetTasksResponse<TaskData>> searchWithCriteria(
            @Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String authToken,
            @Parameter(hidden = true) @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthToken,
            @Valid @RequestBody SearchTaskRequest searchTaskRequest
    );
}
