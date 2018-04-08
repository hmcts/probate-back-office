package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.controller.validation.SolAddDeceasedDetailsEventGroup;
import uk.gov.hmcts.probate.controller.validation.SolAddEstateDetailsEventGroup;
import uk.gov.hmcts.probate.controller.validation.SolAddExecutorDetailsEventGroup;
import uk.gov.hmcts.probate.controller.validation.SolicitorAddWillDetailsGroup;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.SolAddDeceasedEstateDetailsValidationRule;
import uk.gov.hmcts.probate.validator.SolExecutorDetailsValidationRule;
import uk.gov.hmcts.probate.validator.SolicitorCreateValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Data
@Controller
@RequestMapping("/validate")
public class BusinessValidationController {

    private static final Logger log = LoggerFactory.getLogger(BusinessValidationController.class);

    private final EventValidationService eventValidationService;
    private final CCDDataTransformer ccdBeanTransformer;
    private final ObjectMapper objectMapper;
    private final List<SolicitorCreateValidationRule> solicitorCreateValidationRules;
    private final List<SolAddDeceasedEstateDetailsValidationRule> solAddDeceasedEstateDetailsValidationRules;
    private final List<SolExecutorDetailsValidationRule> solExecutorDetailsValidationRules;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final ConfirmationResponseService confirmationResponseService;
    private final StateChangeService stateChangeService;

    @PostMapping(path = "/addDeceasedDetails", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> validateSolicitorcreate(
        @Validated(SolAddDeceasedDetailsEventGroup.class) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);
        CallbackResponse response = validate(bindingResult, callbackRequest, solicitorCreateValidationRules);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/solAddDeceasedEstateDetails", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> validateSolAddDeceasedEstateDetails(
        @Validated(SolAddEstateDetailsEventGroup.class) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);
        CallbackResponse response = validate(bindingResult, callbackRequest, solAddDeceasedEstateDetailsValidationRules);

        if (response.getErrors().isEmpty()) {
            Optional<String> newState = stateChangeService.getChangedStateForDomicility(callbackRequest.getCaseDetails().getData());
            CallbackResponse callbackResponse = callbackResponseTransformer.transformWithConditionalStateChange(callbackRequest, newState);
            return ResponseEntity.ok(callbackResponse);
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping(path = "/solExecutorDetails", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> validateSolExecutorDetails(
        @Validated(SolAddExecutorDetailsEventGroup.class) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid payload", bindingResult);
        }
        CallbackResponse callbackResponse = validate(bindingResult, callbackRequest, solExecutorDetailsValidationRules);
        if (callbackResponse.getErrors().isEmpty()) {
            Optional<String> newState = stateChangeService.getChangedStateForExecutors(callbackRequest.getCaseDetails().getData());
            callbackResponse = callbackResponseTransformer.transformWithConditionalStateChange(callbackRequest, newState);
        }

        return ResponseEntity.ok(callbackResponse);
    }


    @PostMapping(path = "/willUpdate", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> willUpdate(
        @Validated(SolicitorAddWillDetailsGroup.class) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        Optional<String> newState = stateChangeService.getChangedStateForWillDetails(callbackRequest.getCaseDetails().getData());
        CallbackResponse response = callbackResponseTransformer.transformWithConditionalStateChange(callbackRequest, newState);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/stopWillConfirmation", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<AfterSubmitCallbackResponse> stopForWillConfirmation(@RequestBody CallbackRequest callbackRequest,
                                                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = confirmationResponseService.getStopWillConfirmation(callbackRequest);
        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }

    @PostMapping(path = "/stopDomiciltyConfirmation", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<AfterSubmitCallbackResponse> stopForDomicilityConfirmation(@RequestBody CallbackRequest callbackRequest,
                                                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = confirmationResponseService
            .getDomicilityStopConfirmation(callbackRequest);
        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }


    @PostMapping(path = "/stopExecutorConfirmation", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<AfterSubmitCallbackResponse> stopForExecutorConfirmation(@RequestBody CallbackRequest callbackRequest,
                                                                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = confirmationResponseService
            .getExecutorsStopConfirmation(callbackRequest);
        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }

    private CallbackResponse validate(BindingResult bindingResult, CallbackRequest callbackRequest,
                                      List<? extends ValidationRule> rules) {
        if (bindingResult.hasErrors()) {
            return CallbackResponse.builder()
                .errors(collectErrors(bindingResult.getFieldErrors(), Collections.emptyList()))
                .build();
        }

        CCDData ccdData = ccdBeanTransformer.transform(callbackRequest);

        List<FieldErrorResponse> businessErrors = eventValidationService.validate(ccdData, rules);

        if (!businessErrors.isEmpty()) {
            return CallbackResponse.builder()
                .errors(collectErrors(Collections.emptyList(), businessErrors))
                .build();
        }

        return CallbackResponse.builder()
            .errors(Collections.emptyList())
            .build();
    }

    private List<String> collectErrors(List<FieldError> errors, List<FieldErrorResponse> fieldErrorResponses) {
        List<String> allErrors = errors.parallelStream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

        allErrors.addAll(fieldErrorResponses.parallelStream()
            .map(FieldErrorResponse::getMessage)
            .collect(Collectors.toList()));
        return allErrors;
    }

    private void logRequest(String uri, CallbackRequest callbackRequest) {
        try {
            log.debug("POST {}: {}", uri, objectMapper.writeValueAsString(callbackRequest));
        } catch (JsonProcessingException e) {
            log.error("POST {}:", uri, e);
        }
    }
}
