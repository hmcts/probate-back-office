package uk.gov.hmcts.probate.service.caseaccess;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.probate.model.caseaccess.FindUsersByOrganisation;

@FeignClient(
    name = "rd-professional-api",
    url = "${prd.organisations.url}",
    configuration = FeignClientProperties.FeignClientConfiguration.class
)
public interface OrganisationApi {
    @GetMapping("/refdata/internal/v1/organisations/{orgId}/users")
    FindUsersByOrganisation findSolicitorOrganisation(
            @RequestHeader("Authorization") String authorisation,
            @RequestHeader("ServiceAuthorization") String serviceAuthorization,
            @RequestParam(value = "orgId") String organisationId
    );
}
