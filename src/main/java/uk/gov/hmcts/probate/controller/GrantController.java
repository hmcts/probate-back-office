package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.controller.validation.AmendCaseDetailsGroup;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.DocumentIssueType;
import uk.gov.hmcts.probate.model.DocumentStatus;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.response.WillLodgementCallbackResponse;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.DocumentService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RegistryDetailsService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.WillLodgementCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.CaseworkerAmendValidationRule;
import uk.gov.hmcts.probate.validator.CheckListAmendCaseValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.hmcts.probate.validator.RedeclarationSoTValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.Constants.LONDON;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.EDGE_CASE_NAME;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/grant")
public class GrantController {

    private final ObjectMapper objectMapper;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final EventValidationService eventValidationService;
    private static final String DEFAULT_LOG_ERROR = "Case Id: {} ERROR: {}";
    private static final String INVALID_PAYLOAD = "Invalid payload";

    @PostMapping(path = "/start-delayed-notify-period", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> startDelayedNotificationPeriod(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        validateForPayloadErrors(callbackRequest, bindingResult);

        log.info("GrantController /start-delayed-notify-period started");
        @Valid CaseData data = callbackRequest.getCaseDetails().getData();
        String evidenceHandled = data.getEvidenceHandled();
        if (!StringUtils.isEmpty(evidenceHandled)) {
            log.info("Evidence Handled flag {} ", evidenceHandled);
            if(evidenceHandled.equals(Constants.NO)){
                data.setGrantDelayedNotificationDate(LocalDate.now());
            }
        }

        List<CollectionMember<ScannedDocument>> scannedDocs = data.getScannedDocuments();
        int scannedDocsSize = (null!=scannedDocs?scannedDocs.size():0);
        log.info("Case {} has a total of {} scanned documents", callbackRequest.getCaseDetails().getId(),
                scannedDocsSize);
        if (null != scannedDocs) {
            for (CollectionMember<ScannedDocument> scannedDocument : scannedDocs) {
                log.info("Scanned document DCN {} and type {}", scannedDocument.getValue().getControlNumber(),
                        scannedDocument.getValue().getType());
            }
        }

        CallbackResponse response = callbackResponseTransformer.transformCase(callbackRequest);
        return ResponseEntity.ok(response);
    }

    private void validateForPayloadErrors(CallbackRequest callbackRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info(DEFAULT_LOG_ERROR, callbackRequest.getCaseDetails().getId(), bindingResult);
            throw new BadRequestException(INVALID_PAYLOAD, bindingResult);
        }
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        log.error("Returning HTTP 400 Bad Request", e);
    }
}
