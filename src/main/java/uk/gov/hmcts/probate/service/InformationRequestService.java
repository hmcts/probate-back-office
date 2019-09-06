package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InformationRequestService {

    private List<Document> documents;
    private List<Document> letterIdDocuments;
    private List<String> letterIds;

    private final InformationRequestCorrespondenceService informationRequestCorrespondenceService;
    private final CallbackResponseTransformer callbackResponseTransformer;

    public CallbackResponse handleInformationRequest(CallbackRequest callbackRequest) {
        documents = new LinkedList<>();
        letterIdDocuments = new LinkedList<>();
        letterIds = new ArrayList<>();

        if (callbackRequest.getCaseDetails().getData().getPaperForm().equals("No")) {
            if (callbackRequest.getCaseDetails().getData().isBoEmailRequestInfoNotificationRequested()) {
                documents = informationRequestCorrespondenceService.emailInformationRequest(callbackRequest.getCaseDetails());
            } else {
                callbackRequest.getCaseDetails().getData().getExecutorsApplyingNotifications().forEach(executor -> {
                    documents.addAll(informationRequestCorrespondenceService.generateLetterWithCoversheet(callbackRequest,
                            executor.getValue()));
                    if (callbackRequest.getCaseDetails().getData().isBoRequestInfoSendToBulkPrintRequested()) {
                        letterIdDocuments = informationRequestCorrespondenceService.generateLetterWithCoversheet(callbackRequest,
                                executor.getValue());
                        letterIds.addAll(informationRequestCorrespondenceService
                                .getLetterId(letterIdDocuments, callbackRequest));
                    }
                });
            }
        }
        return callbackResponseTransformer.addInformationRequestDocuments(callbackRequest, documents, letterIds);
    }
}
