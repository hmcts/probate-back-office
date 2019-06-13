package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.docmosis.CaveatDocmosisService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRuleCaveats;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED;

@Service
@Slf4j
@AllArgsConstructor
public class CaveatNotificationService {
    private final EventValidationService eventValidationService;
    private final NotificationService notificationService;
    private final List<ValidationRuleCaveats> validationRuleCaveats;
    private final CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;
    private final PDFManagementService pdfManagementService;
    private final BulkPrintService bulkPrintService;
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;
    private final CaveatDocmosisService caveatDocmosisService;

    public CaveatCallbackResponse caveatRaise(CaveatCallbackRequest caveatCallbackRequest)
            throws NotificationClientException {

        CaveatCallbackResponse caveatCallbackResponse = CaveatCallbackResponse.builder().errors(new ArrayList<>()).build();
        Document document;
        List<Document> documents = new ArrayList<>();
        String letterId = null;
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();

        if (caveatDetails.getData().isCaveatRaisedEmailNotificationRequested()) {
            caveatCallbackResponse = eventValidationService.validateCaveatRequest(caveatCallbackRequest, validationRuleCaveats);
            if (caveatCallbackResponse.getErrors().isEmpty()) {
                document = notificationService.sendCaveatEmail(CAVEAT_RAISED, caveatDetails);
                documents.add(document);
            }
        } else {
            Map<String, Object> placeholdersCoversheet =
                    caveatDocmosisService.caseDataAsPlaceholders(caveatCallbackRequest.getCaseDetails());
            Document coversheet = pdfManagementService
                    .generateDocmosisDocumentAndUpload(placeholdersCoversheet, DocumentType.CAVEAT_COVERSHEET, false);
            documents.add(coversheet);
            Map<String, Object> placeholders = caveatDocmosisService.caseDataAsPlaceholders(caveatCallbackRequest.getCaseDetails());
            Document caveatRaisedDoc = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders,
                    DocumentType.CAVEAT_RAISED, false);
            documents.add(caveatRaisedDoc);

            if (caveatCallbackRequest.getCaseDetails().getData().isSendForBulkPrintingRequested()) {
                SendLetterResponse response = bulkPrintService.sendToBulkPrint(caveatCallbackRequest, caveatRaisedDoc, coversheet);
                letterId = response != null
                        ? response.letterId.toString()
                        : null;
                caveatCallbackResponse = eventValidationService.validateCaveatBulkPrintResponse(letterId, bulkPrintValidationRules);
            }
        }

        if (caveatCallbackResponse.getErrors().isEmpty()) {
            caveatCallbackResponse = caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, letterId);
        }
        return caveatCallbackResponse;
    }


}
