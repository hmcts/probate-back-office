package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.NotificationExecutorsApplyingValidationRule;
import uk.gov.hmcts.probate.validator.RedeclarationSoTValidationRule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InformationRequestService {
    private final NotificationExecutorsApplyingValidationRule notificationExecutorsApplyingValidationRule;

    private List<Document> documents;
    private List<Document> letterIdDocuments;
    private List<String> letterIds;

    private final InformationRequestCorrespondenceService informationRequestCorrespondenceService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final RedeclarationSoTValidationRule redeclarationSoTValidationRule;

    public CallbackResponse handleInformationRequest(CallbackRequest callbackRequest) {
        documents = new LinkedList<>();
        letterIdDocuments = new LinkedList<>();
        letterIds = new ArrayList<>();
        redeclarationSoTValidationRule.validate(callbackRequest.getCaseDetails());
        if (callbackRequest.getCaseDetails().getData().isBoEmailRequestInfoNotificationRequested()) {
            notificationExecutorsApplyingValidationRule.validate(callbackRequest.getCaseDetails());
            documents = informationRequestCorrespondenceService.emailInformationRequest(callbackRequest.getCaseDetails());
            //TODO: uncomment code when letters are being used again.

            // } else {
            //    callbackRequest.getCaseDetails().getData().getExecutorsApplyingNotifications().forEach(executor -> {
            //        documents.addAll(informationRequestCorrespondenceService.generateLetterWithCoversheet(callbackRequest,
            //                executor.getValue()));
            //        if (callbackRequest.getCaseDetails().getData().isBoRequestInfoSendToBulkPrintRequested()) {
            //            letterIdDocuments = informationRequestCorrespondenceService.generateLetterWithCoversheet(callbackRequest,
            //                    executor.getValue());
            //            letterIds.addAll(informationRequestCorrespondenceService
            //                    .getLetterId(letterIdDocuments, callbackRequest));
            //        }
            //    });
        }
        return callbackResponseTransformer.addInformationRequestDocuments(callbackRequest, documents, letterIds);
    }
}
