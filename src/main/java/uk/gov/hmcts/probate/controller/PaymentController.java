package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestUpdateResponseDto;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.service.tasklist.TaskListUpdateService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentsService paymentsService;
    private final CallbackResponseTransformer callbackResponseTransformer;

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

    @PostMapping(path = "/update-tasklist", consumes = APPLICATION_JSON_VALUE,
        produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> updateTaskList(
        @RequestBody CallbackRequest request) {
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request));
    }
}
