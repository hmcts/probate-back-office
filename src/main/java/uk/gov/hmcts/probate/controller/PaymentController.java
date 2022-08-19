package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestUpdateResponseDto;
import uk.gov.hmcts.probate.service.payments.PaymentsService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentsService paymentsService;

    @PutMapping(path = "/gor-payment-request-update", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity doGorServiceRequestUpdate(
            @RequestBody ServiceRequestUpdateResponseDto serviceRequestUpdateResponseDto) {
        paymentsService.updateCaseFromServiceRequest(serviceRequestUpdateResponseDto,
                CcdCaseType.GRANT_OF_REPRESENTATION);
        return ResponseEntity.ok().body(null);
    }

    @PutMapping(path = "/caveat-payment-request-update", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity doCaveatServiceRequestUpdate(
            @RequestBody ServiceRequestUpdateResponseDto serviceRequestUpdateResponseDto) {
        paymentsService.updateCaseFromServiceRequest(serviceRequestUpdateResponseDto,
                CcdCaseType.CAVEAT);
        return ResponseEntity.ok().body(null);
    }
}
