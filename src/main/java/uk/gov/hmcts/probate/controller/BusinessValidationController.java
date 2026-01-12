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
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import uk.gov.hmcts.probate.service.CaseEscalatedService;
import uk.gov.hmcts.probate.service.CaseStoppedService;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RegistrarDirectionService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.transformer.DocumentTransformer;
import uk.gov.hmcts.probate.transformer.HandOffLegacyTransformer;
import uk.gov.hmcts.probate.validator.AdColligendaBonaCaseTypeValidationRule;
import uk.gov.hmcts.probate.validator.CaseworkerAmendAndCreateValidationRule;
import uk.gov.hmcts.probate.validator.CaseworkersSolicitorPostcodeValidationRule;
import uk.gov.hmcts.probate.validator.ChangeToSameStateValidationRule;
import uk.gov.hmcts.probate.validator.CodicilDateValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.hmcts.probate.validator.FurtherEvidenceForApplicationValidationRule;
import uk.gov.hmcts.probate.validator.IHTFormIDValidationRule;
import uk.gov.hmcts.probate.validator.IHTFourHundredDateValidationRule;
import uk.gov.hmcts.probate.validator.IHTValidationRule;
import uk.gov.hmcts.probate.validator.IhtEstateValidationRule;
import uk.gov.hmcts.probate.validator.NaValidationRule;
import uk.gov.hmcts.probate.validator.NumberOfApplyingExecutorsValidationRule;
import uk.gov.hmcts.probate.validator.OriginalWillSignedDateValidationRule;
import uk.gov.hmcts.probate.validator.Pre1900DOBValidationRule;
import uk.gov.hmcts.probate.validator.RedeclarationSoTValidationRule;
import uk.gov.hmcts.probate.validator.SolicitorPostcodeValidationRule;
import uk.gov.hmcts.probate.validator.StopReasonValidationRule;
import uk.gov.hmcts.probate.validator.TitleAndClearingPageValidationRule;
import uk.gov.hmcts.probate.validator.UniqueCodeValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;
import uk.gov.hmcts.probate.validator.ZeroApplyingExecutorsValidationRule;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;
import uk.gov.service.notify.NotificationClientException;

import jakarta.servlet.http.HttpServletRequest;
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
    private static final String INVALID_CREATION_EVENT = "Invalid creation event";
    private static final String USE_DIFFERENT_EVENT = "Use different event";
    private static final String UPLOAD_DOCUMENTS_EVENT = "uploadDocumentsDormantCase";
    private final EventValidationService eventValidationService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final List<ValidationRule> allValidationRules;
    private final List<CaseworkerAmendAndCreateValidationRule> allCaseworkerAmendAndCreateValidationRules;
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
    private final StopReasonValidationRule stopReasonValidationRule;
    private final NaValidationRule naValidationRule;
    private final IHTFormIDValidationRule ihtFormIDValidationRule;
    private final SolicitorPostcodeValidationRule solicitorPostcodeValidationRule;
    private final CaseworkersSolicitorPostcodeValidationRule caseworkersSolicitorPostcodeValidationRule;
    private final AssignCaseAccessService assignCaseAccessService;
    private final FurtherEvidenceForApplicationValidationRule furtherEvidenceForApplicationValidationRule;
    private final ChangeToSameStateValidationRule changeToSameStateValidationRule;
    private final HandOffLegacyTransformer handOffLegacyTransformer;
    private final RegistrarDirectionService registrarDirectionService;
    private final Pre1900DOBValidationRule pre1900DOBValidationRule;
    private final AdColligendaBonaCaseTypeValidationRule adColligendaBonaCaseTypeValidationRule;
    private final ZeroApplyingExecutorsValidationRule zeroApplyingExecutorsValidationRule;
    private final BusinessValidationMessageService businessValidationMessageService;
    private final UserInfoService userInfoService;
    private final DocumentTransformer documentTransformer;

    @PostMapping(path = "/default-iht-estate", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> defaultIhtEstateFromDateOfDeath(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(callbackResponseTransformer.defaultIhtEstateFromDateOfDeath(request));
    }

    @PostMapping(path = "/validate-iht-estate", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> validateIhtEstateData(@RequestBody CallbackRequest request) {
        caseDataTransformer.transformFormCaseData(request);
        naValidationRule.validate(request.getCaseDetails());
        ihtFormIDValidationRule.validate(request.getCaseDetails());
        ihtEstateValidationRule.validate(request.getCaseDetails());
        final List<ValidationRule> ihtValidation = Arrays.asList(ihtValidationRule);
        CallbackResponse response = eventValidationService.validateRequest(request, ihtValidation);
        if (response.getErrors().isEmpty()) {
            return ResponseEntity.ok(callbackResponseTransformer.transformValuesPage(request));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/validate-further-evidence", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> validateFurtherEvidence(@RequestBody CallbackRequest request) {
        furtherEvidenceForApplicationValidationRule.validate(request.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.transform(request, Optional.empty()));
    }

    @PostMapping(path = "/cw-create-validate-default-iht-estate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> validateSolsCreateDefaultIhtEstate(
            @RequestBody CallbackRequest callbackRequest) {

        final List<ValidationRule> solPcValidation = Arrays.asList(caseworkersSolicitorPostcodeValidationRule);
        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, solPcValidation);
        if (response.getErrors().isEmpty()) {
            return ResponseEntity.ok(callbackResponseTransformer.defaultIhtEstateFromDateOfDeath(callbackRequest));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-create-validate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> validateSolsCreate(
            @Validated({ApplicationCreatedGroup.class}) @RequestBody
            CallbackRequest callbackRequest) {

        final List<ValidationRule> solPcValidation = Arrays.asList(solicitorPostcodeValidationRule);

        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, solPcValidation);
        if (response.getErrors().isEmpty()) {
            return ResponseEntity.ok(callbackResponseTransformer.transform(callbackRequest, Optional.empty()));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-created", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> createSolsCaseWithOrganisation(
        @RequestHeader(value = "Authorization") String authToken,
        @RequestBody CallbackRequest request) {
        logRequest("/sols-created", request);
        return ResponseEntity.ok(callbackResponseTransformer.createSolsCase(request, authToken));
    }

    @PostMapping(path = "/sols-access", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<AfterSubmitCallbackResponse> solicitorAccess(
        @RequestHeader(value = "Authorization") String authToken,
        @RequestParam(value = "caseTypeId") String caseTypeId,
        @RequestBody CallbackRequest request) {
        assignCaseAccessService.assignCaseAccess(authToken, request.getCaseDetails().getId().toString(), caseTypeId);
        AfterSubmitCallbackResponse afterSubmitCallbackResponse = AfterSubmitCallbackResponse.builder().build();
        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }

    @PostMapping(path = "/sols-validate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> solsValidate(
        @Validated({ApplicationCreatedGroup.class, ApplicationUpdatedGroup.class}) @RequestBody
            CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        caseDataTransformer.transformFormCaseData(callbackRequest);
        validateForPayloadErrors(callbackRequest, bindingResult);
        CallbackResponse response = eventValidationService.validateRequest(callbackRequest, allValidationRules);
        CaseDetails details = callbackRequest.getCaseDetails();
        if (response.getErrors().isEmpty()) {
            if (YES.equals(details.getData().getHmrcLetterId()) || null == details.getData().getHmrcLetterId()) {
                Optional<String> newState =
                        stateChangeService.getChangedStateForGrantType(callbackRequest.getCaseDetails().getData());
                response = callbackResponseTransformer.transformForDeceasedDetails(callbackRequest, newState);
            } else {
                response = callbackResponseTransformer.transformCase(callbackRequest, Optional.empty());
            }
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-validate-probate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> solsValidateProbate(
        @Validated({ApplicationProbateGroup.class}) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        numberOfApplyingExecutorsValidationRule.validate(callbackRequest.getCaseDetails());
        zeroApplyingExecutorsValidationRule.validate(callbackRequest.getCaseDetails());

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

    @PostMapping(path = "/sols-validate-intestacy", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
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

    @PostMapping(path = "/sols-validate-executors", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
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

    @PostMapping(path = "/sols-validate-will-and-codicil-dates", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
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

    @PostMapping(path = "/sols-validate-admon", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
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

    @PostMapping(path = "/sols-validate-iht400", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> solsValidateIHT400Date(@RequestBody CallbackRequest callbackRequest) {
        validateIHT400Date(callbackRequest);
        return ResponseEntity.ok(callbackResponseTransformer.transform(callbackRequest, Optional.empty()));
    }

    @PostMapping(path = "/sols-default-iht400421Page", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> defaultIht400DatePage(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(callbackResponseTransformer.defaultIht400421DatePageFlow(request));
    }

    @PostMapping(path = "/validateCaseDetails", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> validateCaseDetails(
        @Validated({AmendCaseDetailsGroup.class}) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        caseDataTransformer.transformFormCaseData(callbackRequest);
        validateForPayloadErrors(callbackRequest, bindingResult);
        numberOfApplyingExecutorsValidationRule.validate(callbackRequest.getCaseDetails());
        CallbackResponse response =
            eventValidationService.validateRequest(callbackRequest, allCaseworkerAmendAndCreateValidationRules);
        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.transform(callbackRequest, Optional.empty());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/case-stopped", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> startDelayedNotificationPeriod(
        @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        log.info("case-stopped started for case: {}", callbackRequest.getCaseDetails().getId());

        caseStoppedService.caseStopped(callbackRequest.getCaseDetails());
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        CallbackResponse response = callbackResponseTransformer.transformCase(callbackRequest, caseworkerInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/fail-qa", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> caseFailQa(@RequestBody CallbackRequest callbackRequest) {
        caseStoppedService.caseStopped(callbackRequest.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(callbackRequest,
                Optional.empty()));
    }


    @PostMapping(path = "/case-escalated", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> caseEscalated(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        log.info("case-escalated started for case: {}", callbackRequest.getCaseDetails().getId());

        caseEscalatedService.caseEscalated(callbackRequest.getCaseDetails());
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();

        final CaseDetails caseDetails = callbackRequest.getCaseDetails();
        Document sentNotification;
        try {
            sentNotification = notificationService.sendRegistrarEscalationNotification(caseDetails);
        } catch (NotificationService.RegistrarEscalationException e) {
            log.info("Sending registrar escalation notification failed for case: {}", caseDetails.getId());
            sentNotification = notificationService.sendRegistrarEscalationNotificationFailed(
                    caseDetails,
                    caseworkerInfo);
        }
        if (sentNotification != null) {
            final List<CollectionMember<Document>> notifications = caseDetails
                    .getData()
                    .getProbateNotificationsGenerated();
            notifications.add(new CollectionMember<>(null, sentNotification));
        }

        CallbackResponse response = callbackResponseTransformer.transformCase(callbackRequest, caseworkerInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/case-worker-escalated", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> caseworkerEscalated(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        caseEscalatedService.setCaseWorkerEscalatedDate(callbackRequest.getCaseDetails());
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        CallbackResponse response = callbackResponseTransformer.transform(callbackRequest, caseworkerInfo);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/resolve-case-worker-escalated", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> resolveCaseworkerEscalated(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        log.info("resolve-case-worker-escalated started for case: {}", callbackRequest.getCaseDetails().getId());

        caseEscalatedService.setResolveCaseWorkerEscalatedDate(callbackRequest.getCaseDetails());
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        CallbackResponse response = callbackResponseTransformer
                .resolveCaseWorkerEscalationState(callbackRequest, caseworkerInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/resolveStop", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> resolveStopState(@RequestBody CallbackRequest callbackRequest,
                                                             HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);

        caseStoppedService.caseResolved(callbackRequest.getCaseDetails());
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        CallbackResponse response = callbackResponseTransformer.resolveStop(callbackRequest, caseworkerInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/changeCaseState", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> changeCaseState(@RequestBody CallbackRequest callbackRequest,
                                                             HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        changeToSameStateValidationRule.validate(callbackRequest.getCaseDetails());
        log.info("superuser change state  started for case: {}", callbackRequest.getCaseDetails().getId());
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        CallbackResponse response = callbackResponseTransformer.transferToState(callbackRequest, caseworkerInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/resolveCaveatStopState", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> resolveCaveatStopState(@RequestBody CallbackRequest callbackRequest,
                                                            HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        log.info("resolve caveat stop state started");

        final Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        CallbackResponse response = callbackResponseTransformer.transferCaveatStopState(
                callbackRequest,
                caseworkerInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/changeDob", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> changeDob(@RequestBody CallbackRequest callbackRequest,
                                                      HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        log.info("superuser change Dob for case: {}", callbackRequest.getCaseDetails().getId());
        pre1900DOBValidationRule.validate(callbackRequest.getCaseDetails());
        CallbackResponse response = callbackResponseTransformer.changeDob(callbackRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/superUserMakeDormantCase", consumes = APPLICATION_JSON_VALUE,
            produces =  {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> superUserMakeDormantCase(
            @RequestBody CallbackRequest callbackRequest,
            HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        log.info("superuser make case Dormant for case reference {}", callbackRequest.getCaseDetails().getId());
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        CallbackResponse response = callbackResponseTransformer
            .superUserMakeCaseDormant(callbackRequest, caseworkerInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/validate-stop-reason", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> validateStopReason(@RequestBody CallbackRequest callbackRequest,
                                                                      HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        stopReasonValidationRule.validate(callbackRequest.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.transformCase(callbackRequest, Optional.empty()));
    }

    @PostMapping(path = "/validate-unique-code", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> validateUniqueProbateCode(@RequestBody CallbackRequest callbackRequest,
                                                                      HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        uniqueCodeValidationRule.validate(callbackRequest.getCaseDetails());
        CallbackResponse response = callbackResponseTransformer.transformUniqueProbateCode(callbackRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/validate-values-page", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> validateValuesPage(@RequestBody CallbackRequest callbackRequest,
                                                               HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        CallbackResponse response = callbackResponseTransformer.transformValuesPage(callbackRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/rollback", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> rollbackDataMigration(@RequestBody CallbackRequest callbackRequest,
                                                            HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        log.info("Rollback Data migration for case: {}", callbackRequest.getCaseDetails().getId());
        CallbackResponse response = callbackResponseTransformer.rollback(callbackRequest);
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
        caseDataTransformer.transformIhtFormCaseDataByDeceasedDOD(callbackRequest);
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        CallbackResponse response = callbackResponseTransformer.transformCase(callbackRequest, caseworkerInfo);
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
        caseDataTransformer.transformCaseDataForPaperForm(callbackRequest);
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequest);
        validateForPayloadErrors(callbackRequest, bindingResult);
        adColligendaBonaCaseTypeValidationRule.validate(callbackRequest.getCaseDetails());
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
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        // validate the new trust corps (if we're on the new schema, not bulk scan / paper form yes)
        // note - we are assuming here that bulk scan imports set paper form = yes
        if (SOLICITOR.equals(callbackRequest.getCaseDetails().getData().getApplicationType())
                && NO.equals(callbackRequest.getCaseDetails().getData().getPaperForm())) {

            var rules = new ValidationRule[]{codicilDateValidationRule, originalWillSignedDateValidationRule};
            final List<ValidationRule> gopPage1ValidationRules = Arrays.asList(rules);

            response = eventValidationService.validateRequest(callbackRequest,
                    gopPage1ValidationRules);

            if (response.getErrors().isEmpty()) {
                response = callbackResponseTransformer.paperForm(callbackRequest, document, caseworkerInfo);
            }
        } else {
            response = callbackResponseTransformer.paperForm(callbackRequest, document, caseworkerInfo);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/pa-create", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> paCreate(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult) {
        validateForPayloadErrors(callbackRequest, bindingResult);
        caseDataTransformer.transformCaseDataForEvidenceHandled(callbackRequest);
        caseDataTransformer.transformIhtFormCaseDataByDeceasedDOD(callbackRequest);
        caseDataTransformer.setApplicationSubmittedDateForPA(callbackRequest.getCaseDetails());
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        return ResponseEntity.ok(callbackResponseTransformer.transformCase(callbackRequest, caseworkerInfo));
    }

    @PostMapping(path = "/redeclarationComplete", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> redeclarationComplete(@RequestBody CallbackRequest callbackRequest) {
        Optional<String> state =
            stateChangeService.getRedeclarationComplete(callbackRequest.getCaseDetails().getData());
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        return ResponseEntity
            .ok(callbackResponseTransformer
                    .transformWithConditionalStateChange(callbackRequest, state, caseworkerInfo));
    }


    @PostMapping(path = "/redeclarationSot", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> redeclarationSot(
        @RequestBody CallbackRequest callbackRequest) {

        redeclarationSoTValidationRule.validate(callbackRequest.getCaseDetails());
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        return ResponseEntity.ok(callbackResponseTransformer.transform(callbackRequest, caseworkerInfo));
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
        try {
            if (callbackRequest.getCaseDetails().getData().getApplicationType().equals(SOLICITOR)
                    && UPLOAD_DOCUMENTS_EVENT.equalsIgnoreCase(callbackRequest.getEventId())) {
                Document document = notificationService.sendStopResponseReceivedEmail(callbackRequest.getCaseDetails());
                documentTransformer.addDocument(callbackRequest, document, false);
            }
        } catch (NotificationClientException e) {
            log.warn("Fails to send StopResponseReceived notification for case: {}, message: {}",
                    callbackRequest.getCaseDetails().getId(), e.getHttpResult());
        } catch (RuntimeException e) {
            log.warn("Fails to generate or upload notification pdf for case: {}",
                    callbackRequest.getCaseDetails().getId(), e);
        }
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        return ResponseEntity.ok(callbackResponseTransformer.transformCase(callbackRequest, caseworkerInfo));
    }

    @PostMapping(path = "/transformRelationshipToDeceased",
            consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> setupDynamicList(
            @RequestBody CallbackRequest callbackRequest) {
        return ResponseEntity.ok(callbackResponseTransformer.setupDynamicList(callbackRequest));
    }

    @PostMapping(path = "/default-registrars-decision",
            consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> setupRegistrarsDecision(
            @RequestBody CallbackRequest callbackRequest) {
        return ResponseEntity.ok(callbackResponseTransformer.transformCaseWithRegistrarDirection(callbackRequest));
    }

    @PostMapping(path = "/clearRelationships",
            consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> clearRelationships(
            @RequestBody CallbackRequest callbackRequest) {
        return ResponseEntity.ok(callbackResponseTransformer.clearRelationships(callbackRequest));
    }

    @PostMapping(path = "/registrars-decision", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> registrarsDecision(@RequestBody CallbackRequest callbackRequest) {
        registrarDirectionService.addAndOrderDirectionsToGrant(callbackRequest.getCaseDetails().getData());
        return ResponseEntity.ok(callbackResponseTransformer.transformCase(callbackRequest, Optional.empty()));
    }

    @PostMapping(path = "/setLastModifiedDate", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> setLastModifiedDateForDormant(
            @RequestBody CallbackRequest callbackRequest) {
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        return ResponseEntity.ok(callbackResponseTransformer.transformCase(callbackRequest, caseworkerInfo));
    }

    @PostMapping(path = "/invalidEvent", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> invalidEvent(@RequestBody CallbackRequest callbackRequest) {
        List<String> errors = Arrays.asList(businessValidationMessageService.generateError(INVALID_CREATION_EVENT,
                "invalidCreationEvent").getMessage(), businessValidationMessageService
                .generateError(INVALID_CREATION_EVENT, "invalidCreationEventWelsh").getMessage());
        CallbackResponse callbackResponse = CallbackResponse.builder()
                .errors(errors)
                .build();

        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(path = "/use-caveat-notification-event", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> useCaveatEvent() {
        List<String> errors = Arrays.asList(businessValidationMessageService
                .generateError(USE_DIFFERENT_EVENT, "caveatNotificationEvent").getMessage());
        CallbackResponse callbackResponse = CallbackResponse.builder()
                .errors(errors)
                .build();

        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(path = "/use-assemble-letter-event", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> useAssembleLetterEvent() {
        List<String> errors = Arrays.asList(businessValidationMessageService
                .generateError(USE_DIFFERENT_EVENT, "AssembleLetterEvent").getMessage());
        CallbackResponse callbackResponse = CallbackResponse.builder()
                .errors(errors)
                .build();

        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(
            path = "/moveToPostGrantIssued",
            consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> moveToPostGrantIssue(
            @RequestBody final CallbackRequest callbackRequest,
            final HttpServletRequest httpRequest) {
        logRequest(httpRequest.getRequestURI(), callbackRequest);

        final CaseDetails caseDetails = callbackRequest.getCaseDetails();
        final Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();

        final Document sentNotification = notificationService.sendPostGrantIssuedNotification(caseDetails);
        if (sentNotification != null) {
            final List<CollectionMember<Document>> notifications = caseDetails
                    .getData()
                    .getProbateNotificationsGenerated();
            notifications.add(new CollectionMember<>(null, sentNotification));
        }

        return ResponseEntity.ok(callbackResponseTransformer.transformCase(callbackRequest, caseworkerInfo));
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
            response = callbackResponseTransformer
                    .transformWithConditionalStateChange(callbackRequest, newState, Optional.empty());
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
