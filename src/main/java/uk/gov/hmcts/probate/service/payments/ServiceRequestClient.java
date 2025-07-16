package uk.gov.hmcts.probate.service.payments;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.probate.model.payments.PaymentServiceResponse;
import uk.gov.hmcts.probate.model.payments.PaymentsResponse;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestDto;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(name = "service-requst-client", url = "${payment.serviceRequest.url}")
public interface ServiceRequestClient {
    static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @PostMapping(
        value = "/service-request",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    PaymentServiceResponse createServiceRequest(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @RequestBody final ServiceRequestDto serviceRequestDto
    );

    @GetMapping("/payments")
    PaymentsResponse retrievePayments(
            @RequestHeader(AUTHORIZATION) String authorisation,
            @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorisation,
            @RequestParam(name = "service_name") final String serviceType,
            @RequestParam(name = "ccd_case_number") final String ccdCaseNumber
    );
}
