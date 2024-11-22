package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentsReceivedNotificationService {

    private final CallbackResponseTransformer callbackResponseTransformer;
    private final NotificationService notificationService;
    private final EventValidationService eventValidationService;
    private final List<EmailAddressNotifyValidationRule> emailAddressNotifyValidationRules;
    private final FeatureToggleService featureToggleService;
    private static final String DOCUMENTS_RECEIVED_NOTIFICATION_TOGGLE = "probate-documents-received-notification";
    private static final String BULK_SCAN = "created from bulk scan";
    private static final String NOTIFICATION_OFF = "toggle probate-documents-received-notification off";
    private static final String NOTIFICATION_NOT_REQUESTED = "notification not requested";

    public CallbackResponse handleDocumentReceivedNotification(CallbackRequest callbackRequest,
                                                               Optional<UserInfo> caseworkerInfo)
        throws NotificationClientException {

        log.info("Preparing to send email notification for documents being recieved");
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        CallbackResponse response;

        List<Document> documents = new ArrayList<>();
        boolean isNotificationToggleOn = isNotificationFeatureToggleOn();
        boolean isBulkScan = isCaseCreatedFromBulkScan(caseData);
        boolean isNotificationRequested = caseData.isDocsReceivedEmailNotificationRequested();
        if (isNotificationToggleOn && isNotificationRequested && !isBulkScan) {
            response =
                eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules);
            if (response.getErrors().isEmpty()) {
                Document documentsReceivedSentEmail = notificationService.sendEmail(DOCUMENTS_RECEIVED, caseDetails);
                documents.add(documentsReceivedSentEmail);
                response = callbackResponseTransformer
                        .addDocuments(callbackRequest, documents, null, null, caseworkerInfo);
            }
        } else {
            String reasonIgnored;
            if (!isNotificationToggleOn) {
                reasonIgnored = NOTIFICATION_OFF;
            } else if (isBulkScan) {
                reasonIgnored = BULK_SCAN;
            } else {
                reasonIgnored = NOTIFICATION_NOT_REQUESTED;
            }
            log.info("No notification on Document received for case: {} " + reasonIgnored, caseDetails.getId());
            response = callbackResponseTransformer
                    .addDocuments(callbackRequest, documents, null, null, caseworkerInfo);
        }
        return response;
    }

    private boolean isCaseCreatedFromBulkScan(final CaseData caseData) {
        return (!StringUtils.isBlank(caseData.getBulkScanCaseReference()));
    }

    private boolean isNotificationFeatureToggleOn() {
        return featureToggleService.isFeatureToggleOn(
            DOCUMENTS_RECEIVED_NOTIFICATION_TOGGLE, false);
    }


}