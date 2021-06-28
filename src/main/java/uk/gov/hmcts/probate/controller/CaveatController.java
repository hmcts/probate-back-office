package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import uk.gov.hmcts.probate.model.payments.CreditAccountPayment;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.service.CaveatNotificationService;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.service.payments.CreditAccountPaymentTransformer;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatDataTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsEmailAddressNotificationValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsEmailValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsExpiryValidationRule;
import uk.gov.hmcts.probate.validator.CaveatorEmailAddressValidationRule;
import uk.gov.hmcts.probate.validator.CreditAccountPaymentValidationRule;
import uk.gov.hmcts.probate.validator.SolicitorPaymentMethodValidationRule;
import uk.gov.service.notify.NotificationClientException;

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
    private final CaveatDataTransformer caveatDataTransformer;
    private final CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;

    private final EventValidationService eventValidationService;
    private final NotificationService notificationService;
    private final CaveatNotificationService caveatNotificationService;
    private final ConfirmationResponseService confirmationResponseService;
    private final List<CaveatorEmailAddressValidationRule> allCaveatorEmailAddressValidationRule;
    private final PaymentsService paymentsService;
    private final FeeService feeService;
    private final CreditAccountPaymentTransformer creditAccountPaymentTransformer;
    private final CreditAccountPaymentValidationRule creditAccountPaymentValidationRule;
    private final SolicitorPaymentMethodValidationRule solicitorPaymentMethodValidationRule;

    @PostMapping(path = "/raise")
    public ResponseEntity<CaveatCallbackResponse> raiseCaveat(
        @Validated({CaveatsEmailAddressNotificationValidationRule.class, BulkPrintValidationRule.class})
        @RequestBody CaveatCallbackRequest caveatCallbackRequest)
        throws NotificationClientException {

        CaveatCallbackResponse caveatCallbackResponse = caveatNotificationService.caveatRaise(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/validate-amend-caveat")
    public ResponseEntity<CaveatCallbackResponse> validateAmendCaveat(
        @RequestBody CaveatCallbackRequest caveatCallbackRequest)
        throws NotificationClientException {

        validateEmailAddresses(caveatCallbackRequest);
        CaveatCallbackResponse caveatCallbackResponse = caveatNotificationService.caveatRaise(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/defaultValues")
    public ResponseEntity<CaveatCallbackResponse> defaultCaveatValues(
        @RequestBody CaveatCallbackRequest caveatCallbackRequest) {

        CaveatCallbackResponse caveatCallbackResponse =
            caveatCallbackResponseTransformer.defaultCaveatValues(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/default-sols-pba", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CaveatCallbackResponse> defaulsSolicitorNextStepsForPBANumbers(
        @RequestHeader(value = "Authorization") String authToken,
        @RequestBody CaveatCallbackRequest callbackRequest) {

        return ResponseEntity.ok(caveatCallbackResponseTransformer
            .transformCaseForSolicitorPBANumbers(callbackRequest, authToken));
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

    @PostMapping(path = "/solsCreate")
    public ResponseEntity<CaveatCallbackResponse> createSolsCaveat(
        @Validated({CaveatCreatedGroup.class})
        @RequestBody CaveatCallbackRequest caveatCallbackRequest) {

        validateEmailAddresses(caveatCallbackRequest);
        CaveatCallbackResponse caveatCallbackResponse =
            caveatCallbackResponseTransformer.transformForSolicitor(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/solsUpdate")
    public ResponseEntity<CaveatCallbackResponse> updateSolsCaveat(
        @Validated({CaveatCreatedGroup.class, CaveatUpdatedGroup.class})
        @RequestBody CaveatCallbackRequest caveatCallbackRequest) {

        validateEmailAddresses(caveatCallbackRequest);
        CaveatCallbackResponse caveatCallbackResponse =
            caveatCallbackResponseTransformer.transformForSolicitor(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/validate", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CaveatCallbackResponse> validate(
        @RequestHeader(value = "Authorization") String authToken,
        @Validated({CaveatCreatedGroup.class, CaveatUpdatedGroup.class, CaveatCompletedGroup.class})
        @RequestBody CaveatCallbackRequest caveatCallbackRequest,
        BindingResult bindingResult)
        throws NotificationClientException {

        if (bindingResult.hasErrors()) {
            log.error("Case Id: {} ERROR: {}", caveatCallbackRequest.getCaseDetails().getId(), bindingResult);
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        validateEmailAddresses(caveatCallbackRequest);
        CaveatCallbackResponse caveatCallbackResponse;

        solicitorPaymentMethodValidationRule.validate(caveatCallbackRequest.getCaseDetails());

        FeeResponse feeResponse = feeService.getCaveatFeesData();
        CreditAccountPayment creditAccountPayment =
            creditAccountPaymentTransformer.transform(caveatCallbackRequest.getCaseDetails(), feeResponse);
        PaymentResponse paymentResponse = paymentsService.getCreditAccountPaymentResponse(authToken,
            creditAccountPayment);
        CaveatCallbackResponse creditPaymentResponse =
            eventValidationService.validateCaveatPaymentResponse(caveatCallbackRequest.getCaseDetails(),
                paymentResponse, creditAccountPaymentValidationRule);
        if (creditPaymentResponse.getErrors().isEmpty()) {
            caveatCallbackResponse = caveatNotificationService.solsCaveatRaise(caveatCallbackRequest, paymentResponse);
        } else {
            caveatCallbackResponse = creditPaymentResponse;
        }

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

        validateEmailAddresses(caveatCallbackRequest);

        CaveatData caveatData = caveatDataTransformer.transformSolsCaveats(caveatCallbackRequest);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = confirmationResponseService
            .getNextStepsConfirmation(caveatData);

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

    private void validateEmailAddresses(CaveatCallbackRequest caveatCallbackRequest) {
        for (CaveatorEmailAddressValidationRule rule : allCaveatorEmailAddressValidationRule) {
            rule.validate(caveatCallbackRequest.getCaseDetails());
        }
    }
}
