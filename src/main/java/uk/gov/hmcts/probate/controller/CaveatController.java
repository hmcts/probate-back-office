package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.controller.validation.CaveatCompletedGroup;
import uk.gov.hmcts.probate.controller.validation.CaveatCreatedGroup;
import uk.gov.hmcts.probate.controller.validation.CaveatUpdatedGroup;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.service.CaveatNotificationService;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RegistrarDirectionService;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatDataTransformer;
import uk.gov.hmcts.probate.transformer.ServiceRequestTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.CaveatAcknowledgementValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsEmailAddressNotificationValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsEmailValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsExpiryValidationRule;
import uk.gov.hmcts.probate.validator.CaveatDodValidationRule;
import uk.gov.service.notify.NotificationClientException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.State.GENERAL_CAVEAT_MESSAGE;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/caveat", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class CaveatController {

    private final List<CaveatsEmailValidationRule> validationRuleCaveats;
    private final List<CaveatsExpiryValidationRule> validationRuleCaveatsExpiry;
    private final CaveatDodValidationRule caveatDodValidationRule;
    private final CaveatDataTransformer caveatDataTransformer;
    private final CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;
    private final ServiceRequestTransformer serviceRequestTransformer;
    private final EventValidationService eventValidationService;
    private final NotificationService notificationService;
    private final CaveatNotificationService caveatNotificationService;
    private final ConfirmationResponseService confirmationResponseService;
    private final PaymentsService paymentsService;
    private final FeeService feeService;
    private final RegistrarDirectionService registrarDirectionService;
    private final DocumentGeneratorService documentGeneratorService;
    private final CaveatAcknowledgementValidationRule caveatAcknowledgementValidationRule;

    @PostMapping(path = "/raise")
    public ResponseEntity<CaveatCallbackResponse> raiseCaveat(
        @Validated({CaveatsEmailAddressNotificationValidationRule.class, BulkPrintValidationRule.class})
        @RequestBody CaveatCallbackRequest caveatCallbackRequest)
        throws NotificationClientException {

        CaveatCallbackResponse caveatCallbackResponse = caveatNotificationService.caveatRaise(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/setCaseSubmissionDate")
    public ResponseEntity<CaveatCallbackResponse> setCaseSubmissionDateForSolicitorCases(
            @RequestBody CaveatCallbackRequest caveatCallbackRequest) {
        caveatNotificationService.setPaymentTaken(caveatCallbackRequest);
        CaveatCallbackResponse caveatCallbackResponse = caveatNotificationService
                .solsCaveatRaise(caveatCallbackRequest);
        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/raise-caveat-validate")
    public ResponseEntity<CaveatCallbackResponse> raiseCaveatValidate(
            @RequestBody CaveatCallbackRequest caveatCallbackRequest) {
        caveatDodValidationRule.validate(caveatCallbackRequest.getCaseDetails());
        return ResponseEntity.ok(
                caveatCallbackResponseTransformer.transformResponseWithNoChanges(caveatCallbackRequest));
    }

    @PostMapping(path = "/defaultValues")
    public ResponseEntity<CaveatCallbackResponse> defaultCaveatValues(
        @RequestBody CaveatCallbackRequest caveatCallbackRequest) {

        CaveatCallbackResponse caveatCallbackResponse =
            caveatCallbackResponseTransformer.defaultCaveatValues(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/default-sols-payments", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CaveatCallbackResponse> defaultSolicitorNextStepsForPayment(
        @RequestBody CaveatCallbackRequest callbackRequest) {

        return ResponseEntity.ok(caveatCallbackResponseTransformer
            .transformCaseForSolicitorPayment(callbackRequest));
    }

    @PostMapping(path = "/general-message")
    public ResponseEntity<CaveatCallbackResponse> sendGeneralMessageNotification(
        @Validated({CaveatsEmailAddressNotificationValidationRule.class})
        @RequestBody CaveatCallbackRequest caveatCallbackRequest)
        throws NotificationClientException {
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();

        CaveatCallbackResponse response =
            eventValidationService.validateCaveatRequest(caveatCallbackRequest, validationRuleCaveats);
        if (response.getErrors().isEmpty()) {
            Document document = notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, caveatDetails);
            response = caveatCallbackResponseTransformer.generalMessage(caveatCallbackRequest, document);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sols-created")
    public ResponseEntity<CaveatCallbackResponse> createSolsCaseWithOrganisation(
            @Validated({CaveatCreatedGroup.class})
            @RequestHeader(value = "Authorization") String authToken,
            @RequestBody CaveatCallbackRequest caveatCallbackRequest) {

        CaveatCallbackResponse caveatCallbackResponse =
                caveatCallbackResponseTransformer.transformForSolicitor(caveatCallbackRequest, authToken);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/solsCreate")
    public ResponseEntity<CaveatCallbackResponse> createSolsCaveat(
        @Validated({CaveatCreatedGroup.class})
        @RequestBody CaveatCallbackRequest caveatCallbackRequest) {

        CaveatCallbackResponse caveatCallbackResponse =
            caveatCallbackResponseTransformer.transformForSolicitor(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/solsUpdate")
    public ResponseEntity<CaveatCallbackResponse> updateSolsCaveat(
        @Validated({CaveatCreatedGroup.class, CaveatUpdatedGroup.class})
        @RequestBody CaveatCallbackRequest caveatCallbackRequest) {

        CaveatCallbackResponse caveatCallbackResponse =
            caveatCallbackResponseTransformer.transformForSolicitor(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/sols-complete-application", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CaveatCallbackResponse> solsCompleteApplication(
            @Validated({CaveatCreatedGroup.class, CaveatUpdatedGroup.class})
        @RequestBody CaveatCallbackRequest caveatCallbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.error("Case Id: {} ERROR: {}", caveatCallbackRequest.getCaseDetails().getId(), bindingResult);
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        CaveatCallbackResponse caveatCallbackResponse;

        String userId = request.getHeader("user-id");
        caveatCallbackResponse = caveatCallbackResponseTransformer.transformResponseWithServiceRequest(
                caveatCallbackRequest, userId);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/confirmation", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<AfterSubmitCallbackResponse> getNextSteps(
        @Validated({CaveatCreatedGroup.class, CaveatUpdatedGroup.class, CaveatCompletedGroup.class})
        @RequestBody CaveatCallbackRequest caveatCallbackRequest,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("Case Id: {} ERROR: {}", caveatCallbackRequest.getCaseDetails().getId(), bindingResult);
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        CaveatData caveatData = caveatDataTransformer.transformSolsCaveats(caveatCallbackRequest);

        FeeResponse feeResponse = feeService.getCaveatFeesData();
        paymentsService.createServiceRequest(serviceRequestTransformer
                .buildServiceRequest(caveatCallbackRequest.getCaseDetails(), feeResponse));

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = confirmationResponseService
            .getNextStepsConfirmation(caveatData, caveatCallbackRequest.getCaseDetails().getId());

        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }

    @PostMapping(path = "/validate-extend")
    public ResponseEntity<CaveatCallbackResponse> validateExtend(
        @RequestBody CaveatCallbackRequest caveatCallbackRequest) {

        CaveatCallbackResponse caveatCallbackResponse =
            eventValidationService.validateCaveatRequest(caveatCallbackRequest, validationRuleCaveatsExpiry);
        if (caveatCallbackResponse.getErrors().isEmpty()) {
            caveatCallbackResponse =
                caveatCallbackResponseTransformer.transformResponseWithExtendedExpiry(caveatCallbackRequest);
        }

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/extend")
    public ResponseEntity<CaveatCallbackResponse> extend(@RequestBody CaveatCallbackRequest caveatCallbackRequest)
        throws NotificationClientException {

        CaveatCallbackResponse response = caveatNotificationService.caveatExtend(caveatCallbackRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/withdraw")
    public ResponseEntity<CaveatCallbackResponse> withDraw(@RequestBody CaveatCallbackRequest caveatCallbackRequest)
        throws NotificationClientException {
        CaveatCallbackResponse response = caveatNotificationService.withdraw(caveatCallbackRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/default-registrars-decision",
            consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CaveatCallbackResponse> setupRegistrarsDecision(
            @RequestBody CaveatCallbackRequest callbackRequest) {
        return ResponseEntity.ok(caveatCallbackResponseTransformer
                .transformCaseWithRegistrarDirection(callbackRequest));
    }

    @PostMapping(path = "/registrars-decision", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CaveatCallbackResponse> registrarsDecision(
            @RequestBody CaveatCallbackRequest callbackRequest) {
        registrarDirectionService.addAndOrderDirectionsToCaveat(callbackRequest.getCaseDetails().getData());
        return ResponseEntity.ok(caveatCallbackResponseTransformer.transformResponseWithNoChanges(callbackRequest));
    }

    @PostMapping(path = "/setup-for-permanent-removal", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaveatCallbackResponse> setupForPermanentRemovalCaveat(
            @RequestBody CaveatCallbackRequest callbackRequest) {
        return ResponseEntity.ok(caveatCallbackResponseTransformer.setupOriginalDocumentsForRemoval(callbackRequest));
    }

    @PostMapping(path = "/permanently-delete-removed", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaveatCallbackResponse> permanentlyDeleteRemovedCaveat(
            @RequestBody CaveatCallbackRequest callbackRequest) {
        documentGeneratorService.permanentlyDeleteRemovedDocumentsForCaveat(callbackRequest);
        return ResponseEntity.ok(caveatCallbackResponseTransformer.transformResponseWithNoChanges(callbackRequest));
    }

    @PostMapping(path = "/rollback", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaveatCallbackResponse> rollbackDataMigration(@RequestBody CaveatCallbackRequest
                                                                                    callbackRequest) {
        log.info("Rollback Data migration - {}", callbackRequest.getCaseDetails().getId());
        return ResponseEntity.ok(caveatCallbackResponseTransformer.rollback(callbackRequest));
    }

    @PostMapping(path = "/validate-acknowledgement")
    public ResponseEntity<CaveatCallbackResponse> validateAcknowledgement(@RequestBody CaveatCallbackRequest
                                                                                      callbackRequest) {
        caveatAcknowledgementValidationRule.validate(callbackRequest.getCaseDetails());
        return ResponseEntity.ok(caveatCallbackResponseTransformer.transformResponseWithNoChanges(callbackRequest));
    }
}
