package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.docmosis.CaveatDocmosisService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsEmailAddressNotificationValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRuleCaveats;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.State.GENERAL_CAVEAT_MESSAGE;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/caveat", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class CaveatController {

    private final EventValidationService eventValidationService;
    private final NotificationService notificationService;
    private final List<ValidationRuleCaveats> validationRuleCaveats;
    private final CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;
    private final PDFManagementService pdfManagementService;
    private final BulkPrintService bulkPrintService;
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;
    private final CaveatDocmosisService caveatDocmosisService;

    @PostMapping(path = "/raise")
    public ResponseEntity<CaveatCallbackResponse> raiseCaveat(
            @Validated({CaveatsEmailAddressNotificationValidationRule.class, BulkPrintValidationRule.class})
            @RequestBody CaveatCallbackRequest caveatCallbackRequest)
            throws NotificationClientException {

        CaveatCallbackResponse caveatCallbackResponse = CaveatCallbackResponse.builder().errors(new ArrayList<>()).build();
        Document document = null;
        List<Document> documents = new ArrayList<>();
        String letterId = null;
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();

        if (caveatDetails.getData().isCaveatRaisedEmailNotificationRequested()) {
            //send email notification
            //save pdf to dm store
            caveatCallbackResponse = eventValidationService.validateCaveatRequest(caveatCallbackRequest, validationRuleCaveats);
            if (caveatCallbackResponse.getErrors().isEmpty()) {
                document = notificationService.sendCaveatEmail(CAVEAT_RAISED, caveatDetails);
                documents.add(document);
            }
        } else {
            //generate and upload top dm store
            //1. generate coversheet
            Document coverSheet = pdfManagementService.generateAndUpload(caveatCallbackRequest, DocumentType.COVERSHEET);
            log.info("Generated and Uploaded cover document with template {} for the case id {}",
                    DocumentType.COVERSHEET.getTemplateName(), caveatCallbackRequest.getCaseDetails().getId().toString());
            documents.add(coverSheet);

            //1. generate caveat raised doc
            Map<String, Object> placeholders = caveatDocmosisService.caseDataAsPlaceholders(caveatCallbackRequest.getCaseDetails());
            Document caveatRaisedDoc = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, DocumentType.CAVEAT);
            documents.add(caveatRaisedDoc);

            if (caveatCallbackRequest.getCaseDetails().getData().getSendToBulkPrint() == YES) {
                // send to bulk print

                SendLetterResponse response = bulkPrintService.sendToBulkPrint(caveatCallbackRequest, document, coverSheet);
                letterId = response != null
                        ? response.letterId.toString()
                        : null;
                caveatCallbackResponse = eventValidationService.validateCaveatBulkPrintResponse(letterId, bulkPrintValidationRules);
            }
        }

        if (caveatCallbackResponse.getErrors().isEmpty()) {
            caveatCallbackResponse = caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, letterId);
        }

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
}
