package uk.gov.hmcts.probate.service;

import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${extract.templates.hmrcExtract}")
    private String templateId;

    @Autowired
    private final EmailAddresses emailAddresses;

    public void emailFile(File file) {
        if(null==file || !file.exists()) {
            log.error("Error HMRC file does not exist");
            return;
        }

        byte[] fileContents = new byte[0];
        try {
            fileContents = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            log.error("Error reading HMRC file {}",e.getMessage() );
        }

        HashMap<String, Object> personalisation = new HashMap<>();
        try {
            personalisation.put("link_to_file",
                NotificationClient.prepareUpload(
                    fileContents,
                    false,
                    false,
                    new RetentionPeriodDuration(10, ChronoUnit.WEEKS)
                ));
        } catch (NotificationClientException e) {
            log.error("Error Preparing to send email to HMRC: {} ", e.getMessage());
        }
        try {
            SendEmailResponse response = notificationClient.sendEmail(templateId,
                emailAddresses.getHmrcEmail(),
                personalisation,
                null,
                null);
            log.info("HMRC email response: {}", response.toString());
        } catch (NotificationClientException e) {
            log.error("Error sending email to HMRC: {}", e.getMessage());
        }

    }
}
