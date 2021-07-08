package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.util.Base64;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationClientService {

    private final NotificationClient notificationClient;

    public SendEmailResponse sendEmail(String templateId, String emailAddress, Map<String, ?> personalisation,
                                       String reference)
        throws NotificationClientException {
        log.info("Preparing to send email to email address: {}", getEmailEncodedBase64(emailAddress));
        return notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);
    }

    public SendEmailResponse sendEmail(Long caseID, String templateId, String emailAddress,
                                       Map<String, ?> personalisation, String reference)
        throws NotificationClientException {
        log.info("Preparing to send email for case: {}, to email address: {}", caseID,
            getEmailEncodedBase64(emailAddress));
        return notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);
    }

    public SendEmailResponse sendEmail(String templateId, String emailAddress,
                                       Map<String, ?> personalisation,
                           String reference, String emailReplyToId)
        throws NotificationClientException {
        log.info("Preparing to send email to email address: {}", getEmailEncodedBase64(emailAddress));
        return notificationClient.sendEmail(templateId, emailAddress, personalisation, reference, emailReplyToId);
    }

    private String getEmailEncodedBase64(String emailAddress) {
        return new String(Base64.getEncoder().encode(emailAddress.getBytes()));
    }

}
