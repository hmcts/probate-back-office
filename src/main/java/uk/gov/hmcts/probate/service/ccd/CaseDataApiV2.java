package uk.gov.hmcts.probate.service.ccd;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.probate.model.ccd.raw.response.AuditEventsResponse;

@FeignClient(
        name = "core-case-data-api-v2",
        url = "${core_case_data.api.url}"
)
public interface CaseDataApiV2 {
    @GetMapping("/cases/{caseId}/events")
    AuditEventsResponse getAuditEvents(
            @RequestHeader("Authorization") String authorisation,
            @RequestHeader("ServiceAuthorization") String serviceAuthorization,
            @RequestHeader("experimental") boolean experimental,
            @PathVariable("caseId") String caseId
    );
}
