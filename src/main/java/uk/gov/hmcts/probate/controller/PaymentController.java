package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestUpdateResponseDto;
import uk.gov.hmcts.probate.model.payments.PaymentStatusReponse;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.exception.model.InvalidTokenException;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentsService paymentsService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final CaseDataTransformer caseDataTransformer;

    private final SecurityUtils authS2sUtil;

    public static final String SUCCESSFUL_UPDATE = "success";
    public static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";

    @PutMapping(path = "/gor-payment-request-update", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<PaymentStatusReponse> doGorServiceRequestUpdate(
            @RequestHeader (value = SERVICE_AUTHORIZATION_HEADER) String s2sAuthToken,
            @RequestBody ServiceRequestUpdateResponseDto serviceRequestUpdateResponseDto) {
        try {
            Boolean isServiceAllowed = authS2sUtil.checkIfServiceIsAllowed(s2sAuthToken);
            if (Boolean.TRUE.equals(isServiceAllowed)) {
                paymentsService.updateCaseFromServiceRequest(serviceRequestUpdateResponseDto,
                        CcdCaseType.GRANT_OF_REPRESENTATION);
                return ResponseEntity.ok().body(new PaymentStatusReponse(SUCCESSFUL_UPDATE));
            } else {
                log.info("Calling Service is not authorised to use the endpoint");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (InvalidTokenException e) {
            log.error(e.getMessage());
            log.info("Provided s2s token is missing or invalid");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping(path = "/caveat-payment-request-update", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<PaymentStatusReponse> doCaveatServiceRequestUpdate(
            @RequestHeader(value = SERVICE_AUTHORIZATION_HEADER) String s2sAuthToken,
            @RequestBody ServiceRequestUpdateResponseDto serviceRequestUpdateResponseDto) {
        try {
            Boolean isServiceAllowed = authS2sUtil.checkIfServiceIsAllowed(s2sAuthToken);
            if (Boolean.TRUE.equals(isServiceAllowed)) {
                paymentsService.updateCaseFromServiceRequest(serviceRequestUpdateResponseDto,
                        CcdCaseType.CAVEAT);
                return ResponseEntity.ok().body(new PaymentStatusReponse(SUCCESSFUL_UPDATE));
            } else {
                log.info("Calling Service is not authorised to use the endpoint");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (InvalidTokenException e) {
            log.error(e.getMessage());
            log.info("Provided s2s token is missing or invalid");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping(path = "/update-tasklist", consumes = APPLICATION_JSON_VALUE,
        produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> updateTaskList(
        @RequestBody CallbackRequest request) {
        caseDataTransformer.transformCaseDataForEvidenceHandled(request);
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request, Optional.empty()));
    }
}
