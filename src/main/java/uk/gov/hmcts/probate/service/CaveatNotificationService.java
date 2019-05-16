package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.docmosis.CaveatDocmosisService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRuleCaveats;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;

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
        Document document = null;
        List<Document> documents = new ArrayList<>();
        String letterId = null;
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();

        return caveatCallbackResponse;
    }


}
