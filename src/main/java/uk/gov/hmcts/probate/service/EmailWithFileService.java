package uk.gov.hmcts.probate.service;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.notifications.EmailAddresses;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.RetentionPeriodDuration;

import org.apache.commons.io.FileUtils;
import uk.gov.service.notify.SendEmailResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailWithFileService {

    private final NotificationClient notificationClient;

    private final EmailAddresses emailAddresses;

    private final EmailValidationService emailValidationService;

    @Value("${extract.templates.hmrcExtract}")
    private String templateId;

    public boolean emailFile(File file, String date) {
        if (null == file || !file.exists()) {
            log.error("Error HMRC file does not exist");
            return false;
        }


        byte[] fileContents;
        try {
            fileContents = FileUtils.readFileToByteArray(file);
            //check file size as there is a 2mb limit
            long totalSpace = fileContents.length / 1048576L;
            log.info("HMRC file is {}MB", totalSpace);
            if (totalSpace > 2L) {
                //not expecting this size, service will error but we'll log additional error too
                log.error("HMRC File is over 2MB, skip email process");
                return false;
            }
        } catch (IOException e) {
            log.error("Error reading HMRC file {}", e.getMessage());
            return false;
        }

        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("extract_date", date);

        if (!prepareUpload(fileContents, (HashMap<String, Object>) personalisation)) {
            return false;
        }
        return sendEmail(personalisation);

    }

    private boolean sendEmail(final Map<String, Object> personalisation) {
        boolean allSuccessful = true;

        //list of emails in secrets need to be sent separately for service
        final String emailsStr = emailAddresses.getHmrcEmail();
        final String[] emails = StringUtils.split(emailsStr, ";");
        for (final String email : emails) {
            try {
                final SendEmailResponse response = notificationClient.sendEmail(templateId,
                    email,
                    personalisation,
                    null,
                    null);
                if (null != response) {
                    log.info("HMRC email to: {} notificationId: {}",
                            emailValidationService.getHashedEmail(email),
                            response.getNotificationId());
                }
            } catch (NotificationClientException e) {
                final String message = MessageFormat.format("HMRC email to: {0} failed to send",
                        emailValidationService.getHashedEmail(email));
                log.error(message, e);
                allSuccessful = false;
            }
        }

        return allSuccessful;
    }

    private boolean prepareUpload(byte[] fileContents, HashMap<String, Object> personalisation) {
        try {
            personalisation.put("link_to_file",
                NotificationClient.prepareUpload(
                    fileContents,
                    false,
                    new RetentionPeriodDuration(26, ChronoUnit.WEEKS)
                ));
        } catch (NotificationClientException e) {
            log.error("Error Preparing to send email to HMRC: {} ", e.getMessage());
            return false;
        }
        return true;
    }
}
