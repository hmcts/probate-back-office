package uk.gov.hmcts.probate.service;

import java.io.File;
import java.nio.file.Paths;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.config.notifications.EmailAddresses;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.RetentionPeriodDuration;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EmailWithFileServiceTest {

    @Autowired
    EmailWithFileService emailWithFileService;


    /**
     * Method under test: {@link EmailWithFileService#emailFile(File)}
     */
    @Test
    void testEmailFileFail() {
        assertFalse(
            emailWithFileService.emailFile(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toFile()));
        assertFalse(emailWithFileService.emailFile(Paths.get(System.getProperty("java.io.tmpdir"), "").toFile()));
       }
}