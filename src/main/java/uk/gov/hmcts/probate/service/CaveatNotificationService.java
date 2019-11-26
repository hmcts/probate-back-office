package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.model.Constants.CAVEAT_LIFESPAN;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED_SOLS;

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
        setCaveatExpiryDate(caveatDetails.getData());

        if (caveatDetails.getData().isCaveatRaisedEmailNotificationRequested()) {
            caveatCallbackResponse = eventValidationService.validateCaveatRequest(caveatCallbackRequest, validationRuleCaveats);
            if (caveatCallbackResponse.getErrors().isEmpty()) {
                document = notificationService.sendCaveatEmail(CAVEAT_RAISED, caveatDetails);
                documents.add(document);
            }
        } else {
            Map<String, Object> placeholders = caveatDocmosisService.caseDataAsPlaceholders(caveatCallbackRequest.getCaseDetails());
            Document coversheet = pdfManagementService
                    .generateDocmosisDocumentAndUpload(placeholders, DocumentType.CAVEAT_COVERSHEET);
            documents.add(coversheet);
            Document caveatRaisedDoc = pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, DocumentType.CAVEAT_RAISED);
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

    public CaveatCallbackResponse solsCaveatRaise(CaveatCallbackRequest caveatCallbackRequest)
            throws NotificationClientException {

        CaveatCallbackResponse caveatCallbackResponse = CaveatCallbackResponse.builder().errors(new ArrayList<>()).build();
        Document document;
        List<Document> documents = new ArrayList<>();
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();
        setCaveatExpiryDate(caveatDetails.getData());

        document = notificationService.sendCaveatEmail(CAVEAT_RAISED_SOLS, caveatDetails);
        documents.add(document);

        if (caveatCallbackResponse.getErrors().isEmpty()) {
            caveatCallbackResponse = caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null);
        }
        return caveatCallbackResponse;
    }

    private void setCaveatExpiryDate(CaveatData caveatData) {
        caveatData.setExpiryDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN));
    }


}
