package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.TemplatePreview;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationClientService {

    private final EmailValidationService emailValidationService;
    private final NotificationClient notificationClient;

    public SendEmailResponse sendEmail(String templateId, String emailAddress, Map<String, ?> personalisation,
                                       String reference)
        throws NotificationClientException {
        log.info("Preparing to send email to email address: {}", emailValidationService.getHashedEmail(emailAddress));
        return notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);
    }

    public SendEmailResponse sendEmail(Long caseID, String templateId, String emailAddress,
                                       Map<String, ?> personalisation, String reference)
        throws NotificationClientException {
        log.info("Preparing to send email for case: {}, to email address: {}", caseID,
                emailValidationService.getHashedEmail(emailAddress));
        return notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);
    }

    public SendEmailResponse sendEmail(Long caseId, String templateId, String emailAddress,
                                       Map<String, ?> personalisation, String reference, String emailReplyToId)
        throws NotificationClientException {
        log.info("Preparing to send email for case: {}, to email address: {}", caseId,
                emailValidationService.getHashedEmail(emailAddress));
        return notificationClient.sendEmail(templateId, emailAddress, personalisation, reference, emailReplyToId);
    }

    public TemplatePreview emailPreview(Long caseId, String templateId, Map<String, Object> personalisation)
            throws NotificationClientException {
        log.info("Preparing to send email for case: {}", caseId);
        return notificationClient.generateTemplatePreview(templateId, personalisation);
    }
}
