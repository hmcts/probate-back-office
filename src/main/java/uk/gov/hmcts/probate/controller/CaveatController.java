package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.CaveatNotificationService;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.docmosis.CaveatDocmosisService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsEmailAddressNotificationValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRuleCaveats;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.State.GENERAL_CAVEAT_MESSAGE;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/caveat", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class CaveatController {

    private final ObjectMapper objectMapper;
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;
    private final List<ValidationRuleCaveats> validationRuleCaveats;
    private final CCDDataTransformer ccdBeanTransformer;
    private final CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;

    private final EventValidationService eventValidationService;
    private final NotificationService notificationService;
    private final PDFManagementService pdfManagementService;
    private final BulkPrintService bulkPrintService;
    private final CaveatDocmosisService caveatDocmosisService;
    private final CaveatNotificationService caveatNotificationService;
    private final ConfirmationResponseService confirmationResponseService;

    @PostMapping(path = "/raise")
    public ResponseEntity<CaveatCallbackResponse> raiseCaveat(
            @Validated({CaveatsEmailAddressNotificationValidationRule.class, BulkPrintValidationRule.class})
            @RequestBody CaveatCallbackRequest caveatCallbackRequest)
            throws NotificationClientException {

        CaveatCallbackResponse caveatCallbackResponse = caveatNotificationService.caveatRaise(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/defaultValues")
    public ResponseEntity<CaveatCallbackResponse> defaultCaveatValues(@RequestBody CaveatCallbackRequest caveatCallbackRequest) {

        CaveatCallbackResponse caveatCallbackResponse = caveatCallbackResponseTransformer.defaultCaveatValues(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/general-message")
    public ResponseEntity<CaveatCallbackResponse> sendGeneralMessageNotification(
            @Validated({CaveatsEmailAddressNotificationValidationRule.class})
            @RequestBody CaveatCallbackRequest caveatCallbackRequest)
            throws NotificationClientException {
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();

        CaveatCallbackResponse response = eventValidationService.validateCaveatRequest(caveatCallbackRequest, validationRuleCaveats);
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

        CaveatCallbackResponse caveatCallbackResponse = caveatCallbackResponseTransformer.transform(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/solsUpdate")
    public ResponseEntity<CaveatCallbackResponse> updateSolsCaveat(
            @Validated({CaveatCreatedGroup.class, CaveatUpdatedGroup.class})
            @RequestBody CaveatCallbackRequest caveatCallbackRequest) {

        CaveatCallbackResponse caveatCallbackResponse = caveatCallbackResponseTransformer.transform(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/validate", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<CaveatCallbackResponse> validate(
            @Validated({CaveatCreatedGroup.class, CaveatUpdatedGroup.class, CaveatCompletedGroup.class})
            @RequestBody CaveatCallbackRequest caveatCallbackRequest,
            BindingResult bindingResult)
            throws NotificationClientException {

        if (bindingResult.hasErrors()) {
            log.error("Case Id: {} ERROR: {}", caveatCallbackRequest.getCaseDetails().getId(), bindingResult);
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        CaveatCallbackResponse caveatCallbackResponse = caveatNotificationService.solsCaveatRaise(caveatCallbackRequest);

        return ResponseEntity.ok(caveatCallbackResponse);
    }

    @PostMapping(path = "/confirmation", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<AfterSubmitCallbackResponse> getNextSteps(
            @Validated({CaveatCreatedGroup.class, CaveatUpdatedGroup.class, CaveatCompletedGroup.class})
            @RequestBody CaveatCallbackRequest caveatCallbackRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("Case Id: {} ERROR: {}", caveatCallbackRequest.getCaseDetails().getId(), bindingResult);
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        CaveatData caveatData = ccdBeanTransformer.transformSolsCaveats(caveatCallbackRequest);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = confirmationResponseService
                .getNextStepsConfirmation(caveatData);

        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }
}
