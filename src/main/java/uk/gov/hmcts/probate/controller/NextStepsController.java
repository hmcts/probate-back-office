package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.controller.validation.ApplicationCreatedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationReviewedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationUpdatedGroup;
import uk.gov.hmcts.probate.controller.validation.NextStepsConfirmationGroup;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.transformer.HandOffLegacyTransformer;
import uk.gov.hmcts.probate.transformer.ServiceRequestTransformer;
import uk.gov.hmcts.probate.validator.ServiceRequestAlreadyCreatedValidationRule;
import uk.gov.service.notify.NotificationClientException;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/nextsteps")
public class NextStepsController {

    private final CCDDataTransformer ccdBeanTransformer;
    private final ConfirmationResponseService confirmationResponseService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final ServiceRequestTransformer serviceRequestTransformer;
    private final CaseDataTransformer caseDataTransformer;
    private final ObjectMapper objectMapper;
    private final FeeService feeService;
    private final StateChangeService stateChangeService;
    private final PaymentsService paymentsService;
    private final HandOffLegacyTransformer handOffLegacyTransformer;
    private final ServiceRequestAlreadyCreatedValidationRule serviceRequestAlreadyCreatedValidationRule;

    public static final String CASE_ID_ERROR = "Case Id: {} ERROR: {}";

    @PostMapping(path = "/validate", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> validate(
        @Validated({ApplicationCreatedGroup.class, ApplicationUpdatedGroup.class, ApplicationReviewedGroup.class})
        @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) throws NotificationClientException {

        logRequest(request.getRequestURI(), callbackRequest);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequest);

        CallbackResponse callbackResponse;
        Optional<String> newState =
            stateChangeService.getChangedStateForCaseReview(callbackRequest.getCaseDetails().getData());
        if (newState.isPresent()) {
            callbackResponse = callbackResponseTransformer
                .transformWithConditionalStateChange(callbackRequest, newState);
        } else {
            if (bindingResult.hasErrors()) {
                log.error(CASE_ID_ERROR, callbackRequest.getCaseDetails().getId(), bindingResult);
                throw new BadRequestException("Invalid payload", bindingResult);
            }
            caseDataTransformer.transformCaseDataForEvidenceHandled(callbackRequest);

            serviceRequestAlreadyCreatedValidationRule.validate(callbackRequest.getCaseDetails());

            CCDData ccdData = ccdBeanTransformer.transform(callbackRequest);

            FeesResponse feesResponse = feeService.getAllFeesData(
                ccdData.getIht().getNetValueInPounds(),
                ccdData.getFee().getExtraCopiesOfGrant(),
                ccdData.getFee().getOutsideUKGrantCopies());
            if (feesResponse.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
                String serviceRequestReference = paymentsService.createServiceRequest(serviceRequestTransformer
                        .buildServiceRequest(callbackRequest.getCaseDetails(), feesResponse));

                callbackResponse = callbackResponseTransformer.transformForSolicitorComplete(callbackRequest,
                        feesResponse, serviceRequestReference);
            } else {
                callbackResponse = callbackResponseTransformer.transformForSolicitorComplete(callbackRequest,
                        feesResponse, null);
            }
        }

        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(path = "/confirmation", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<AfterSubmitCallbackResponse> getNextSteps(
        @Validated({ApplicationCreatedGroup.class, ApplicationUpdatedGroup.class, ApplicationReviewedGroup.class,
            NextStepsConfirmationGroup.class})
        @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        Optional<String> newState =
            stateChangeService.getChangedStateForCaseReview(callbackRequest.getCaseDetails().getData());
        if (newState.isPresent()) {
            return ResponseEntity.ok(AfterSubmitCallbackResponse.builder().build());
        }

        if (bindingResult.hasErrors()) {
            log.error(CASE_ID_ERROR, callbackRequest.getCaseDetails().getId(), bindingResult);
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        CCDData ccdData = ccdBeanTransformer.transform(callbackRequest);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = confirmationResponseService
            .getNextStepsConfirmation(ccdData, callbackRequest.getCaseDetails().getData());

        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }

    private void logRequest(String uri, CallbackRequest callbackRequest) {
        try {
            log.info("POST: {} Case Id: {} ", uri, callbackRequest.getCaseDetails().getId().toString());
            log.debug("POST: {} {}", uri, objectMapper.writeValueAsString(callbackRequest));
        } catch (JsonProcessingException e) {
            log.error("POST: {}", uri, e);
        }
    }
}
