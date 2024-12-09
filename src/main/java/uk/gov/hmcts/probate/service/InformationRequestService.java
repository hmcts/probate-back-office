package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InformationRequestService {
    private final NotificationService notificationService;
    private final InformationRequestCorrespondenceService informationRequestCorrespondenceService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;

    public CallbackResponse handleInformationRequest(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        CCDData dataForEmailAddress = CCDData.builder()
                .applicationType(caseData.getApplicationType().name())
                .primaryApplicantEmailAddress(caseData.getPrimaryApplicantEmailAddress())
                .solsSolicitorEmail(caseData.getSolsSolicitorEmail())
                .build();
        List<FieldErrorResponse> emailErrors = emailAddressNotifyApplicantValidationRule.validate(dataForEmailAddress);
        if (!emailErrors.isEmpty()) {
            return CallbackResponse.builder()
                    .errors(emailErrors.stream().map(FieldErrorResponse::getMessage).toList())
                    .build();
        }
        List<Document> documents = informationRequestCorrespondenceService
                .emailInformationRequest(callbackRequest.getCaseDetails());
        return callbackResponseTransformer.addInformationRequestDocuments(callbackRequest, documents);
    }

    public Document emailPreview(CallbackRequest callbackRequest) {
        try {
            return notificationService.emailPreview(callbackRequest.getCaseDetails());
        } catch (NotificationClientException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
