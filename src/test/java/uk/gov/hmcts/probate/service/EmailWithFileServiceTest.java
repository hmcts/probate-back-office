package uk.gov.hmcts.probate.service;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.config.notifications.EmailAddresses;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.NotificationClientException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EmailWithFileServiceTest {

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private EmailAddresses emailAddresses;

    @InjectMocks
    private EmailWithFileService emailWithFileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailWithFileService = new EmailWithFileService(notificationClient, emailAddresses);
    }

    @Test
    void testEmailFile_Success() throws IOException, NotificationClientException {
        File file = ResourceUtils.getFile(FileUtils.class.getResource("/files/hmrcPersonal.txt"));
        SendEmailResponse mockResponse = mock(SendEmailResponse.class);
        JSONObject jsonObject = new JSONObject().put("testKey", "testValue");
        when(emailAddresses.getHmrcEmail()).thenReturn("hmrc@example.com");
        mockStatic(NotificationClient.class);
        when(NotificationClient.prepareUpload(any(byte[].class), anyBoolean(), anyBoolean(), anyString()))
            .thenReturn(jsonObject);
        when(notificationClient.sendEmail(any(), any(), any(), any(), any())).thenReturn(mockResponse);
        assertTrue(emailWithFileService.emailFile(file));

    }

    /**
     * Method under test: {@link EmailWithFileService#emailFile(File)}.
     */
    @Test
    void testEmailFileNoFileFail() {
        assertFalse(emailWithFileService
            .emailFile(Paths.get(System.getProperty("java.io.tmpdir"), "absenty.txt").toFile()));
        assertFalse(emailWithFileService
            .emailFile(Paths.get(System.getProperty("java.io.tmpdir"), "").toFile()));
        verifyNoInteractions(notificationClient);
    }

    @Test
    void testEmailFile_ReadFileException() throws IOException {

        assertFalse(emailWithFileService.emailFile(
            Paths.get(System.getProperty("java.io.tmpdir"), "absenty.txt").toFile()));
        verifyNoInteractions(notificationClient);
    }
}