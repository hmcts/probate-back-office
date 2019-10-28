package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.validator.AddressExecutorsApplyingValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressExecutorsApplyingValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_REQUEST_INFORMATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class InformationRequestCorrespondenceService {

    private final NotificationService notificationService;
    private final EmailAddressExecutorsApplyingValidationRule emailAddressExecutorsApplyingValidationRule;
    private final AddressExecutorsApplyingValidationRule addressExecutorsApplyingValidationRule;
    private final DocumentGeneratorService documentGeneratorService;
    private final BulkPrintService bulkPrintService;
    private List<Document> documents;

    public List<Document> emailInformationRequest(CaseDetails caseDetails) {
        documents = new ArrayList<>();
        emailAddressExecutorsApplyingValidationRule.validate(caseDetails);

        caseDetails.getData().getExecutorsApplyingNotifications().forEach(executor -> {
            if (executor.getValue().getNotification().equals(YES)) {
                log.info("Initiate call to send request for information email for case id {} and executor: {} ",
                        caseDetails.getId(), executor.getId());
                try {
                    documents.add(notificationService.sendEmail(CASE_STOPPED_REQUEST_INFORMATION, caseDetails, executor.getValue()));
                    log.info("Successful response for request for information email for case id {} ",
                            caseDetails.getId());
                } catch (NotificationClientException e) {
                    log.error(e.getMessage());
                }
            }
        });

        return documents;
    }

    public List<Document> generateLetterWithCoversheet(CallbackRequest callbackRequest,
                                                       ExecutorsApplyingNotification executor) {
        documents = new LinkedList<>();
        addressExecutorsApplyingValidationRule.validate(callbackRequest.getCaseDetails());

        log.info("Initiate call to send request for information letter for case id {}",
                callbackRequest.getCaseDetails().getId());

        documents.add(documentGeneratorService.generateCoversheet(callbackRequest, executor.getName(),
                executor.getAddress()));
        documents.add(documentGeneratorService.generateRequestForInformation(callbackRequest.getCaseDetails(),
                executor));

        return documents;
    }

    public List<String> getLetterId(List<Document> documents, CallbackRequest callbackRequest) {
        List<String> letterIds = new ArrayList<>();
        String letterId = bulkPrintService.sendToBulkPrint(callbackRequest, documents.get(0), documents.get(1),
                callbackRequest.getCaseDetails().getData().isBoRequestInfoSendToBulkPrintRequested());

        letterIds.add(letterId);
        return letterIds;
    }
}
