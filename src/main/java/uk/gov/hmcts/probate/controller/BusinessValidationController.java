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
import uk.gov.hmcts.probate.controller.validation.ApplicationAdmonGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationCreatedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationIntestacyGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationProbateGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationUpdatedGroup;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.CaseOrigin;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.CaseStoppedService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.CaseworkerAmendValidationRule;
import uk.gov.hmcts.probate.validator.CheckListAmendCaseValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.hmcts.probate.validator.RedeclarationSoTValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;
import uk.gov.service.notify.NotificationClientException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.ADMON_WILL_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.GRANT_OF_PROBATE_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.INTESTACY_NAME;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/case")
public class BusinessValidationController {

    private final EventValidationService eventValidationService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final List<ValidationRule> allValidationRules;
    private final List<CaseworkerAmendValidationRule> allCaseworkerAmendValidationRules;
    private final List<CheckListAmendCaseValidationRule> checkListAmendCaseValidationRules;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final ConfirmationResponseService confirmationResponseService;
    private final StateChangeService stateChangeService;
    private final PDFManagementService pdfManagementService;
    private final RedeclarationSoTValidationRule redeclarationSoTValidationRule;
    private final CaseStoppedService caseStoppedService;
    private final EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;
    private static final String DEFAULT_LOG_ERROR = "Case Id: {} ERROR: {}";
    private static final String INVALID_PAYLOAD = "Invalid payload";

    @PostMapping(path = "/sols-apply-as-exec")
    public ResponseEntity<CallbackResponse> setApplicantFieldsForSolsApplyAsExec(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(callbackResponseTransformer.setApplicantFieldsForSolsApplyAsExec(request));
    }

    @PostMapping(path = "/sols-validate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> solsValidate(
            @Validated({ApplicationCreatedGroup.class, ApplicationUpdatedGroup.class}) @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, allValidationRules);
        if (response.getErrors().isEmpty()) {
            Optional<String> newState = stateChangeService.getChangedStateForGrantType(callbackRequest.getCaseDetails().getData());
            response = callbackResponseTransformer.transformWithConditionalStateChange(callbackRequest, newState);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-validate-probate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> solsValidateProbate(
            @Validated({ApplicationProbateGroup.class}) @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, allValidationRules);
        if (response.getErrors().isEmpty()) {
            Optional<String> newState = stateChangeService.getChangedStateForProbateUpdate(callbackRequest.getCaseDetails().getData());
            response = getCallbackResponseForGenerateAndUpload(callbackRequest, newState, LEGAL_STATEMENT_PROBATE, GRANT_OF_PROBATE_NAME);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-validate-intestacy", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> solsValidateIntestacy(
            @Validated({ApplicationIntestacyGroup.class}) @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, allValidationRules);
        if (response.getErrors().isEmpty()) {
            Optional<String> newState = stateChangeService.getChangedStateForIntestacyUpdate(callbackRequest.getCaseDetails().getData());
            response = getCallbackResponseForGenerateAndUpload(callbackRequest, newState, LEGAL_STATEMENT_INTESTACY, INTESTACY_NAME);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-validate-admon", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> solsValidateAdmon(
            @Validated({ApplicationAdmonGroup.class}) @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, allValidationRules);
        if (response.getErrors().isEmpty()) {
            Optional<String> newState = stateChangeService.getChangedStateForAdmonUpdate(callbackRequest.getCaseDetails().getData());
            response = getCallbackResponseForGenerateAndUpload(callbackRequest, newState, LEGAL_STATEMENT_ADMON, ADMON_WILL_NAME);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/validateCaseDetails", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> validateCaseDetails(
            @Validated({AmendCaseDetailsGroup.class}) @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, allCaseworkerAmendValidationRules);
        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.transform(callbackRequest);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/validateCheckListDetails", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> validateCheckListDetails(
            @Validated({CheckListAmendCaseValidationRule.class}) @RequestBody CallbackRequest callbackRequest,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, checkListAmendCaseValidationRules);

        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.selectForQA(callbackRequest);
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping(path = "/case-stopped", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> startDelayedNotificationPeriod(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        log.info("case-stopped started");

        caseStoppedService.caseStopped(callbackRequest.getCaseDetails());

        CallbackResponse response = callbackResponseTransformer.transformCase(callbackRequest);
        return ResponseEntity.ok(response);
    }


    @PostMapping(path = "/resolveStop", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> resolveStopState(@RequestBody CallbackRequest callbackRequest,
                                                             HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        caseStoppedService.caseResolved(callbackRequest.getCaseDetails());

        CallbackResponse response = callbackResponseTransformer.resolveStop(callbackRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/stopConfirmation", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<AfterSubmitCallbackResponse> stopWithConfirmation(
            @Validated({ApplicationCreatedGroup.class, ApplicationUpdatedGroup.class}) @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult) {

        validateForPayloadErrors(callbackRequest, bindingResult);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = confirmationResponseService.getStopConfirmation(callbackRequest);
        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }

    @PostMapping(path = "/casePrinted", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> casePrinted(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult) {

        validateForPayloadErrors(callbackRequest, bindingResult);

        notificationService.startAwaitingDocumentationNotificationPeriod(callbackRequest.getCaseDetails());
        CallbackResponse response = callbackResponseTransformer.transformCase(callbackRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/paperForm", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> paperFormCaseDetails(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult) throws NotificationClientException {

        validateForPayloadErrors(callbackRequest, bindingResult);

        Document document = null;
        if (hasRequiredEmailAddress(callbackRequest.getCaseDetails().getData())) {
            document = notificationService.sendEmail(APPLICATION_RECEIVED, callbackRequest.getCaseDetails(), Optional.of(CaseOrigin.CASEWORKER));
        }
        CallbackResponse response = callbackResponseTransformer.paperForm(callbackRequest, document);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/redeclarationComplete", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> redeclarationComplete(
            @RequestBody CallbackRequest callbackRequest) {
        Optional<String> state = stateChangeService.getRedeclarationComplete(callbackRequest.getCaseDetails().getData());
        return ResponseEntity.ok(callbackResponseTransformer.transformWithConditionalStateChange(callbackRequest, state));
    }


    @PostMapping(path = "/redeclarationSot", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> redeclarationSot(
            @RequestBody CallbackRequest callbackRequest) {

        redeclarationSoTValidationRule.validate(callbackRequest.getCaseDetails());

        return ResponseEntity.ok(callbackResponseTransformer.transform(callbackRequest));
    }

    @PostMapping(path = "/default-sols-next-steps", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> defaulsSolicitorNextStepsForLegalStatementRegeneration(
            @RequestBody CallbackRequest callbackRequest) {

        return ResponseEntity.ok(callbackResponseTransformer.transformCaseForSolicitorLegalStatementRegeneration(callbackRequest));
    }

    private void validateForPayloadErrors(CallbackRequest callbackRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info(DEFAULT_LOG_ERROR, callbackRequest.getCaseDetails().getId(), bindingResult);
            throw new BadRequestException(INVALID_PAYLOAD, bindingResult);
        }
    }

    private CallbackResponse getCallbackResponseForGenerateAndUpload(
            CallbackRequest callbackRequest, Optional<String> newState, DocumentType documentType, String caseType) {
        CallbackResponse response;
        if (newState.isPresent()) {
            response = callbackResponseTransformer.transformWithConditionalStateChange(callbackRequest, newState);
        } else {
            Document document = pdfManagementService.generateAndUpload(callbackRequest, documentType);
            response = callbackResponseTransformer.transform(callbackRequest, document, caseType);
        }
        return response;
    }

    private void logRequest(String uri, CallbackRequest callbackRequest) {
        try {
            log.info("POST: {} Case Id: {} ", uri, callbackRequest.getCaseDetails().getId().toString());
            if (log.isDebugEnabled()) {
                log.debug("POST: {} {}", uri, objectMapper.writeValueAsString(callbackRequest));
            }
        } catch (JsonProcessingException e) {
            log.error("POST: {}", uri, e);
        }
    }
    
    private boolean hasRequiredEmailAddress(CaseData data) {
        CCDData dataForEmailAddress = CCDData.builder()
            .applicationType(data.getApplicationType().name())
            .primaryApplicantEmailAddress(data.getPrimaryApplicantEmailAddress())
            .solsSolicitorEmail(data.getSolsSolicitorEmail())
            .build();
        List<FieldErrorResponse> emailErrors = emailAddressNotifyApplicantValidationRule.validate(dataForEmailAddress);
        return emailErrors.isEmpty();
    }
}
