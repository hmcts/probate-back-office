package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.controller.validation.AmendCaseDetailsGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationCreatedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationUpdatedGroup;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.ValidationRule;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/case")
public class BusinessValidationController {

    private final EventValidationService eventValidationService;
    private final CCDDataTransformer ccdBeanTransformer;
    private final ObjectMapper objectMapper;
    private final List<ValidationRule> validationRules;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final ConfirmationResponseService confirmationResponseService;
    private final StateChangeService stateChangeService;
    private final PDFManagementService pdfManagementService;

    @PostMapping(path = "/validate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> validate(
            @Validated({ApplicationCreatedGroup.class, ApplicationUpdatedGroup.class}) @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        if (bindingResult.hasErrors()) {
            log.error("Case Id: {} ERROR: {}", callbackRequest.getCaseDetails().getId(), bindingResult);
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        CallbackResponse response = validateRequest(callbackRequest, validationRules);
        if (response.getErrors().isEmpty()) {
            Optional<String> newState = stateChangeService.getChangedStateForCaseUpdate(callbackRequest.getCaseDetails().getData());
            if (newState.isPresent()) {
                response = callbackResponseTransformer.transformWithConditionalStateChange(callbackRequest, newState);
            } else {
                Document document = pdfManagementService.generateAndUpload(callbackRequest, LEGAL_STATEMENT);
                response = callbackResponseTransformer.transform(callbackRequest, document);
            }
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/validateCaseDetails", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> validateCaseDetails(
            @Validated({AmendCaseDetailsGroup.class}) @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        if (bindingResult.hasErrors()) {
            log.error("Case Id: {} ERROR: {}", callbackRequest.getCaseDetails().getId(), bindingResult);
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        CallbackResponse response = validateRequest(callbackRequest, validationRules);

        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.transform(callbackRequest);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/stopConfirmation", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<AfterSubmitCallbackResponse> stopWithConfirmation(
            @Validated({ApplicationCreatedGroup.class, ApplicationUpdatedGroup.class}) @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("Case: Id {} ERROR: {}", callbackRequest.getCaseDetails().getId(), bindingResult);
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = confirmationResponseService.getStopConfirmation(callbackRequest);
        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }

    private CallbackResponse validateRequest(CallbackRequest callbackRequest,
                                             List<? extends ValidationRule> rules) {

        CCDData ccdData = ccdBeanTransformer.transform(callbackRequest);

        List<FieldErrorResponse> businessErrors = eventValidationService.validate(ccdData, rules);

        return CallbackResponse.builder()
                .errors(businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                .build();
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
