package uk.gov.hmcts.probate.service.payments;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestDto;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(name = "service-requst-client", url = "${payment.serviceRequest.url}")
public interface ServiceRequestClient {
    static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @PostMapping(
        value = "/service-request",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    String createServiceRequest(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @RequestBody final ServiceRequestDto serviceRequestDto
    );
}
