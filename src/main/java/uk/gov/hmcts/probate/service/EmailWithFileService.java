package uk.gov.hmcts.probate.service;

import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${extract.templates.hmrcExtract}")
    private String templateId;

    @Autowired
    private final EmailAddresses emailAddresses;

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

    public boolean sendEmail(Map<String, Object> personalisation) {
        //list of emails in secrets need to be sent separately for service
        emailAddresses.getHmrcEmail();
        String[] emails = StringUtils.split(emailAddresses.getHmrcEmail(), ";");
        try {
            for (String email : emails) {
                SendEmailResponse response = notificationClient.sendEmail(templateId,
                    email,
                    personalisation,
                    null,
                    null);
                if (null != response) {
                    log.info("HMRC email to: {} response: {}",
                        new String(Base64.getEncoder().encode(email.getBytes())),response.toString());
                }
            }
        } catch (NotificationClientException e) {
            log.error("Error Preparing to send email to HMRC: {} ", e.getMessage());
            return false;
        }

        return true;
    }

    private boolean prepareUpload(byte[] fileContents, HashMap<String, Object> personalisation) {
        try {
            personalisation.put("link_to_file",
                NotificationClient.prepareUpload(
                    fileContents,
                    false,
                    false,
                    new RetentionPeriodDuration(26, ChronoUnit.WEEKS).toString()
                ));
        } catch (NotificationClientException e) {
            log.error("Error Preparing to send email to HMRC: {} ", e.getMessage());
            return false;
        }
        return true;
    }
}
