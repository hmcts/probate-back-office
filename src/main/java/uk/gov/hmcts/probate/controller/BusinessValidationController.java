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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.probate.controller.validation.AmendCaseDetailsGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationAdmonGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationCreatedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationIntestacyGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationProbateGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationUpdatedGroup;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.CaseOrigin;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.CaseEscalatedService;
import uk.gov.hmcts.probate.service.CaseStoppedService;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RegistrarDirectionService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.transformer.HandOffLegacyTransformer;
import uk.gov.hmcts.probate.validator.CaseworkerAmendAndCreateValidationRule;
import uk.gov.hmcts.probate.validator.CaseworkersSolicitorPostcodeValidationRule;
import uk.gov.hmcts.probate.validator.CheckListAmendCaseValidationRule;
import uk.gov.hmcts.probate.validator.ChangeToSameStateValidationRule;
import uk.gov.hmcts.probate.validator.CodicilDateValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.hmcts.probate.validator.FurtherEvidenceForApplicationValidationRule;
import uk.gov.hmcts.probate.validator.IHTFourHundredDateValidationRule;
import uk.gov.hmcts.probate.validator.IHTValidationRule;
import uk.gov.hmcts.probate.validator.IhtEstateValidationRule;
import uk.gov.hmcts.probate.validator.NaValidationRule;
import uk.gov.hmcts.probate.validator.NumberOfApplyingExecutorsValidationRule;
import uk.gov.hmcts.probate.validator.OriginalWillSignedDateValidationRule;
import uk.gov.hmcts.probate.validator.RedeclarationSoTValidationRule;
import uk.gov.hmcts.probate.validator.SolicitorPostcodeValidationRule;
import uk.gov.hmcts.probate.validator.TitleAndClearingPageValidationRule;
import uk.gov.hmcts.probate.validator.UniqueCodeValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;
import uk.gov.service.notify.NotificationClientException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.ADMON_WILL_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.GRANT_OF_PROBATE_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.INTESTACY_NAME;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/case")
public class BusinessValidationController {

    private static final String DEFAULT_LOG_ERROR = "Case Id: {} ERROR: {}";
    private static final String INVALID_PAYLOAD = "Invalid payload";
    private final EventValidationService eventValidationService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final List<ValidationRule> allValidationRules;
    private final List<CaseworkerAmendAndCreateValidationRule> allCaseworkerAmendAndCreateValidationRules;
    private final List<CheckListAmendCaseValidationRule> checkListAmendCaseValidationRules;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final CaseDataTransformer caseDataTransformer;
    private final ConfirmationResponseService confirmationResponseService;
    private final StateChangeService stateChangeService;
    private final PDFManagementService pdfManagementService;
    private final RedeclarationSoTValidationRule redeclarationSoTValidationRule;
    private final NumberOfApplyingExecutorsValidationRule numberOfApplyingExecutorsValidationRule;
    private final CodicilDateValidationRule codicilDateValidationRule;
    private final OriginalWillSignedDateValidationRule originalWillSignedDateValidationRule;
    private final List<TitleAndClearingPageValidationRule> allTitleAndClearingValidationRules;
    private final CaseStoppedService caseStoppedService;
    private final CaseEscalatedService caseEscalatedService;
    private final EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;
    private final IHTFourHundredDateValidationRule ihtFourHundredDateValidationRule;
    private final IhtEstateValidationRule ihtEstateValidationRule;
    private final IHTValidationRule ihtValidationRule;
    private final UniqueCodeValidationRule uniqueCodeValidationRule;
    private final NaValidationRule naValidationRule;
    private final SolicitorPostcodeValidationRule solicitorPostcodeValidationRule;
    private final CaseworkersSolicitorPostcodeValidationRule caseworkersSolicitorPostcodeValidationRule;
    private final AssignCaseAccessService assignCaseAccessService;
    private final FurtherEvidenceForApplicationValidationRule furtherEvidenceForApplicationValidationRule;
    private final ChangeToSameStateValidationRule changeToSameStateValidationRule;
    private final HandOffLegacyTransformer handOffLegacyTransformer;
    private final RegistrarDirectionService registrarDirectionService;

    @PostMapping(path = "/update-task-list")
    public ResponseEntity<CallbackResponse> updateTaskList(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request));
    }

    @PostMapping(path = "/default-iht-estate")
    public ResponseEntity<CallbackResponse> defaultIhtEstateFromDateOfDeath(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(callbackResponseTransformer.defaultIhtEstateFromDateOfDeath(request));
    }

    @PostMapping(path = "/validate-iht-estate")
    public ResponseEntity<CallbackResponse> validateIhtEstateData(@RequestBody CallbackRequest request) {
        naValidationRule.validate(request.getCaseDetails());
        ihtEstateValidationRule.validate(request.getCaseDetails());
        final List<ValidationRule> ihtValidation = Arrays.asList(ihtValidationRule);
        CallbackResponse response = eventValidationService.validateRequest(request, ihtValidation);
        if (response.getErrors().isEmpty()) {
            return ResponseEntity.ok(callbackResponseTransformer.transform(request));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/validate-further-evidence")
    public ResponseEntity<CallbackResponse> validateFurtherEvidence(@RequestBody CallbackRequest request) {
        furtherEvidenceForApplicationValidationRule.validate(request.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.transform(request));
    }

    @PostMapping(path = "/cw-create-validate-default-iht-estate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> validateSolsCreateDefaultIhtEstate(
            @RequestBody CallbackRequest callbackRequest) {

        final List<ValidationRule> solPcValidation = Arrays.asList(caseworkersSolicitorPostcodeValidationRule);
        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, solPcValidation);
        if (response.getErrors().isEmpty()) {
            return ResponseEntity.ok(callbackResponseTransformer.defaultIhtEstateFromDateOfDeath(callbackRequest));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-create-validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> validateSolsCreate(
            @Validated({ApplicationCreatedGroup.class}) @RequestBody
            CallbackRequest callbackRequest) {

        final List<ValidationRule> solPcValidation = Arrays.asList(solicitorPostcodeValidationRule);

        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, solPcValidation);
        if (response.getErrors().isEmpty()) {
            return ResponseEntity.ok(callbackResponseTransformer.transform(callbackRequest));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-created")
    public ResponseEntity<CallbackResponse> createSolsCaseWithOrganisation(
        @RequestHeader(value = "Authorization") String authToken,
        @RequestBody CallbackRequest request) {
        logRequest("/sols-created", request);
        return ResponseEntity.ok(callbackResponseTransformer.createSolsCase(request, authToken));
    }

    @PostMapping(path = "/sols-access")
    public ResponseEntity<AfterSubmitCallbackResponse> solicitorAccess(
        @RequestHeader(value = "Authorization") String authToken,
        @RequestParam(value = "caseTypeId") String caseTypeId,
        @RequestBody CallbackRequest request) {
        assignCaseAccessService.assignCaseAccess(authToken, request.getCaseDetails().getId().toString(), caseTypeId);
        AfterSubmitCallbackResponse afterSubmitCallbackResponse = AfterSubmitCallbackResponse.builder().build();
        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }

    @PostMapping(path = "/sols-validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> solsValidate(
        @Validated({ApplicationCreatedGroup.class, ApplicationUpdatedGroup.class}) @RequestBody
            CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);
        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, allValidationRules);
        CaseDetails details = callbackRequest.getCaseDetails();
        if (response.getErrors().isEmpty()) {
            if (YES.equals(details.getData().getHmrcLetterId()) || null == details.getData().getHmrcLetterId()) {
                Optional<String> newState =
                        stateChangeService.getChangedStateForGrantType(callbackRequest.getCaseDetails().getData());
                response = callbackResponseTransformer.transformForDeceasedDetails(callbackRequest, newState);
            } else {
                log.info("selected No to Hmrc letter");
                response = callbackResponseTransformer.transformCase(callbackRequest);
            }
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/iht-net-value")
    public ResponseEntity<CallbackResponse> ihtNetValueLabel(@RequestBody CallbackRequest callbackRequest,
                                                                      HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        CallbackResponse response = callbackResponseTransformer.transformIhNetValue(callbackRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-validate-probate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> solsValidateProbate(
        @Validated({ApplicationProbateGroup.class}) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        numberOfApplyingExecutorsValidationRule.validate(callbackRequest.getCaseDetails());
        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, allValidationRules);
        if (response.getErrors().isEmpty()) {

            caseDataTransformer.transformCaseDataForValidateProbate(callbackRequest);

            Optional<String> newState =
                stateChangeService.getChangedStateForProbateUpdate(callbackRequest.getCaseDetails().getData());
            response = getCallbackResponseForGenerateAndUpload(callbackRequest, newState,
                    LEGAL_STATEMENT_PROBATE_TRUST_CORPS, GRANT_OF_PROBATE_NAME);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-validate-intestacy", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> solsValidateIntestacy(
        @Validated({ApplicationIntestacyGroup.class}) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        numberOfApplyingExecutorsValidationRule.validate(callbackRequest.getCaseDetails());
        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, allValidationRules);
        if (response.getErrors().isEmpty()) {
            Optional<String> newState =
                stateChangeService.getChangedStateForIntestacyUpdate(callbackRequest.getCaseDetails().getData());
            response = getCallbackResponseForGenerateAndUpload(callbackRequest, newState, LEGAL_STATEMENT_INTESTACY,
                INTESTACY_NAME);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-validate-executors", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> solsValidateExecutors(
            @RequestBody CallbackRequest callbackRequest,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateTitleAndClearingPage(callbackRequest);
        numberOfApplyingExecutorsValidationRule.validate(callbackRequest.getCaseDetails());

        caseDataTransformer.transformCaseDataForSolicitorExecutorNames(callbackRequest);
        CallbackResponse response = callbackResponseTransformer.transformForSolicitorExecutorNames(callbackRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-validate-will-and-codicil-dates", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> solsValidateProbatePage1(
            @RequestBody CallbackRequest callbackRequest,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);
        var rules = new ValidationRule[]{codicilDateValidationRule, originalWillSignedDateValidationRule};
        final List<ValidationRule> gopPage1ValidationRules = Arrays.asList(rules);

        CallbackResponse response = eventValidationService.validateRequest(callbackRequest,
                gopPage1ValidationRules);

        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.transformForSolicitorExecutorNames(callbackRequest);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-validate-admon", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> solsValidateAdmon(
        @Validated({ApplicationAdmonGroup.class}) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        numberOfApplyingExecutorsValidationRule.validate(callbackRequest.getCaseDetails());
        furtherEvidenceForApplicationValidationRule.validate(callbackRequest.getCaseDetails());
        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, allValidationRules);
        if (response.getErrors().isEmpty()) {

            caseDataTransformer.transformCaseDataForValidateAdmon(callbackRequest);

            Optional<String> newState =
                stateChangeService.getChangedStateForAdmonUpdate(callbackRequest.getCaseDetails().getData());
            response = getCallbackResponseForGenerateAndUpload(callbackRequest, newState, LEGAL_STATEMENT_ADMON,
                ADMON_WILL_NAME);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-validate-iht400", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> solsValidateIHT400Date(@RequestBody CallbackRequest callbackRequest) {
        validateIHT400Date(callbackRequest);
        return ResponseEntity.ok(callbackResponseTransformer.transform(callbackRequest));
    }

    @PostMapping(path = "/sols-default-iht400421Page")
    public ResponseEntity<CallbackResponse> defaultIht400DatePage(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(callbackResponseTransformer.defaultIht400421DatePageFlow(request));
    }

    @PostMapping(path = "/validateCaseDetails", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> validateCaseDetails(
        @Validated({AmendCaseDetailsGroup.class}) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);
        numberOfApplyingExecutorsValidationRule.validate(callbackRequest.getCaseDetails());
        CallbackResponse response =
            eventValidationService.validateRequest(callbackRequest, allCaseworkerAmendAndCreateValidationRules);
        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.transform(callbackRequest);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/validateCheckListDetails", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> validateCheckListDetails(
        @Validated({CheckListAmendCaseValidationRule.class}) @RequestBody CallbackRequest callbackRequest,
        HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        CallbackResponse response =
            eventValidationService.validateRequest(callbackRequest, checkListAmendCaseValidationRules);

        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.selectForQA(callbackRequest);
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping(path = "/case-stopped", consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(path = "/fail-qa", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> caseFailQa(@RequestBody CallbackRequest callbackRequest) {
        caseStoppedService.caseStopped(callbackRequest.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(callbackRequest));
    }


    @PostMapping(path = "/case-escalated", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> caseEscalated(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        log.info("case-escalated started");

        caseEscalatedService.caseEscalated(callbackRequest.getCaseDetails());

        CallbackResponse response = callbackResponseTransformer.transformCase(callbackRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/case-worker-escalated", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> caseworkerEscalated(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        caseEscalatedService.setCaseWorkerEscalatedDate(callbackRequest.getCaseDetails());
        CallbackResponse response = callbackResponseTransformer.transform(callbackRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/resolve-case-worker-escalated", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> resolveCaseworkerEscalated(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        log.info("resolve-case-worker-escalated started");

        caseEscalatedService.setResolveCaseWorkerEscalatedDate(callbackRequest.getCaseDetails());

        CallbackResponse response = callbackResponseTransformer.resolveCaseWorkerEscalationState(callbackRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/resolveStop", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> resolveStopState(@RequestBody CallbackRequest callbackRequest,
                                                             HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        caseStoppedService.caseResolved(callbackRequest.getCaseDetails());

        CallbackResponse response = callbackResponseTransformer.resolveStop(callbackRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/changeCaseState", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> changeCaseState(@RequestBody CallbackRequest callbackRequest,
                                                             HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        changeToSameStateValidationRule.validate(callbackRequest.getCaseDetails());
        log.info("superuser change state  started");
        CallbackResponse response = callbackResponseTransformer.transferToState(callbackRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/validate-unique-code")
    public ResponseEntity<CallbackResponse> validateUniqueProbateCode(@RequestBody CallbackRequest callbackRequest,
                                                                      HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        uniqueCodeValidationRule.validate(callbackRequest.getCaseDetails());
        CallbackResponse response = callbackResponseTransformer.transformUniqueProbateCode(callbackRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/stopConfirmation", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<AfterSubmitCallbackResponse> stopWithConfirmation(
        @Validated({ApplicationCreatedGroup.class, ApplicationUpdatedGroup.class}) @RequestBody
            CallbackRequest callbackRequest,
        BindingResult bindingResult) {

        validateForPayloadErrors(callbackRequest, bindingResult);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse =
            confirmationResponseService.getStopConfirmation(callbackRequest);
        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }

    @PostMapping(path = "/casePrinted", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> casePrinted(
        @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult) {

        validateForPayloadErrors(callbackRequest, bindingResult);

        notificationService.startAwaitingDocumentationNotificationPeriod(callbackRequest.getCaseDetails());
        caseDataTransformer.transformCaseDataForEvidenceHandled(callbackRequest);
        CallbackResponse response = callbackResponseTransformer.transformCase(callbackRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/initPaperForm", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> initPaperForm(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult) {

        validateForPayloadErrors(callbackRequest, bindingResult);
        CallbackResponse response = callbackResponseTransformer.defaultDateOfDeathType(callbackRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/paperForm", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> paperFormCaseDetails(
        @Validated({AmendCaseDetailsGroup.class}) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult) throws NotificationClientException {

        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequest);
        validateForPayloadErrors(callbackRequest, bindingResult);
        numberOfApplyingExecutorsValidationRule.validate(callbackRequest.getCaseDetails());

        Document document = null;
        final CaseData data = callbackRequest.getCaseDetails().getData();

        CallbackResponse response;

        response = eventValidationService.validateRequest(callbackRequest, allCaseworkerAmendAndCreateValidationRules);

        if (!response.getErrors().isEmpty()) {
            return ResponseEntity.ok(response);
        }

        if (hasRequiredEmailAddress(data)) {
            document = notificationService
                .sendEmail(APPLICATION_RECEIVED,callbackRequest.getCaseDetails(),Optional.of(CaseOrigin.CASEWORKER));
        }

        caseDataTransformer.transformCaseDataForEvidenceHandledForManualCreateByCW(callbackRequest);
        // validate the new trust corps (if we're on the new schema, not bulk scan / paper form yes)
        // note - we are assuming here that bulk scan imports set paper form = yes
        if (SOLICITOR.equals(callbackRequest.getCaseDetails().getData().getApplicationType())
                && NO.equals(callbackRequest.getCaseDetails().getData().getPaperForm())) {

            var rules = new ValidationRule[]{codicilDateValidationRule, originalWillSignedDateValidationRule};
            final List<ValidationRule> gopPage1ValidationRules = Arrays.asList(rules);

            response = eventValidationService.validateRequest(callbackRequest,
                    gopPage1ValidationRules);

            if (response.getErrors().isEmpty()) {
                response = callbackResponseTransformer.paperForm(callbackRequest, document);
            }
        } else {
            response = callbackResponseTransformer.paperForm(callbackRequest, document);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/pa-create", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> paCreate(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult) {
        validateForPayloadErrors(callbackRequest, bindingResult);
        caseDataTransformer.transformCaseDataForEvidenceHandled(callbackRequest);
        return ResponseEntity.ok(callbackResponseTransformer.transformCase(callbackRequest));
    }

    @PostMapping(path = "/redeclarationComplete", consumes = APPLICATION_JSON_VALUE, produces = {
        APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> redeclarationComplete(
        @RequestBody CallbackRequest callbackRequest) {
        Optional<String> state =
            stateChangeService.getRedeclarationComplete(callbackRequest.getCaseDetails().getData());
        return ResponseEntity
            .ok(callbackResponseTransformer.transformWithConditionalStateChange(callbackRequest, state));
    }


    @PostMapping(path = "/redeclarationSot", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> redeclarationSot(
        @RequestBody CallbackRequest callbackRequest) {

        redeclarationSoTValidationRule.validate(callbackRequest.getCaseDetails());

        return ResponseEntity.ok(callbackResponseTransformer.transform(callbackRequest));
    }

    @PostMapping(path = "/default-sols-next-steps", consumes = APPLICATION_JSON_VALUE, produces = {
        APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> defaulsSolicitorNextStepsForLegalStatementRegeneration(
        @RequestBody CallbackRequest callbackRequest) {

        return ResponseEntity
            .ok(callbackResponseTransformer.transformCaseForSolicitorLegalStatementRegeneration(callbackRequest));
    }

    @PostMapping(path = "/default-sols-payments", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> defaultSolicitorNextStepsForPayment(
        @RequestBody CallbackRequest callbackRequest) {

        return ResponseEntity.ok(callbackResponseTransformer
            .transformCaseForSolicitorPayment(callbackRequest));
    }

    @PostMapping(path = "/reactivate-case", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> reactivateCase(
        @RequestBody CallbackRequest callbackRequest) {
        log.info("Reactivating case - " + callbackRequest.getCaseDetails().getId().toString());
        caseStoppedService.setEvidenceHandledNo(callbackRequest.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.transformCase(callbackRequest));
    }

    @PostMapping(path = "/default-registrars-decision",
            consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> setupRegistrarsDecision(
            @RequestBody CallbackRequest callbackRequest) {
        return ResponseEntity.ok(callbackResponseTransformer.transformCaseWithRegistrarDirection(callbackRequest));
    }

    @PostMapping(path = "/registrars-decision", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> registrarsDecision(
            @RequestBody CallbackRequest callbackRequest) {
        registrarDirectionService.addAndOrderDirectionsToGrant(callbackRequest.getCaseDetails().getData());
        return ResponseEntity.ok(callbackResponseTransformer.transformCase(callbackRequest));
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
            if (log != null && uri != null && callbackRequest != null) {
                final var caseDetails = callbackRequest.getCaseDetails();
                if (caseDetails != null) {
                    final Long id =  callbackRequest.getCaseDetails().getId();
                    log.info("POST: {} Case Id: {} ", uri, id == null ? "Unknown" : id.toString());
                    if (log.isDebugEnabled()) {
                        log.debug("POST: {} {}", uri, objectMapper.writeValueAsString(callbackRequest));
                    }
                }
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

    private void validateIHT400Date(CallbackRequest callbackRequest) {
        ihtFourHundredDateValidationRule.validate(callbackRequest.getCaseDetails());
    }

    private void validateTitleAndClearingPage(CallbackRequest callbackRequest) {
        for (TitleAndClearingPageValidationRule rule : allTitleAndClearingValidationRules) {
            rule.validate(callbackRequest.getCaseDetails());
        }
    }
}
