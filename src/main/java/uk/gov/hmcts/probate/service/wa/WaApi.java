package uk.gov.hmcts.probate.service.wa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.probate.config.FeignClientConfiguration;
import uk.gov.hmcts.probate.model.wa.TaskSearchResponse;

@FeignClient(name = "wa-api", url = "${wa.provider.client.url}", configuration = FeignClientConfiguration.class)
public interface WaApi {
    String AUTHORIZATION = "Authorization";
    String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @Operation(
            description = "Retrieve tasks by case id",
            security = {
                @SecurityRequirement(name = SERVICE_AUTHORIZATION),
                @SecurityRequirement(name = AUTHORIZATION)
            }
    )
    @GetMapping(path = "/task/searchByCaseId")
    ResponseEntity<TaskSearchResponse> searchTasksByCaseId(
            @Parameter(hidden = true) @RequestHeader(AUTHORIZATION) String authToken,
            @Parameter(hidden = true) @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthToken,
            @RequestParam("case_id") String caseId
    );
}