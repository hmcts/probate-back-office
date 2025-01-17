package uk.gov.hmcts.probate.service;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.config.notifications.EmailAddresses;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.RetentionPeriodDuration;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.NotificationClientException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EmailWithFileServiceTest {

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private EmailAddresses emailAddresses;

    @Mock
    private EmailValidationService emailValidationService;

    @InjectMocks
    private EmailWithFileService emailWithFileService;

    private AutoCloseable closeableMocks;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void testEmailFile_Success() throws IOException, NotificationClientException {
        try (final var mockStatic = mockStatic(NotificationClient.class)) {
            JSONObject jsonObject = new JSONObject().put("testKey", "testValue");
            when(emailAddresses.getHmrcEmail()).thenReturn("hmrc@example.com");
            when(NotificationClient.prepareUpload(any(byte[].class), anyBoolean(), any(RetentionPeriodDuration.class)))
                    .thenReturn(jsonObject);
            SendEmailResponse mockResponse = mock(SendEmailResponse.class);
            when(notificationClient.sendEmail(any(), any(), any(), any(), any())).thenReturn(mockResponse);
            File file = ResourceUtils.getFile(FileUtils.class.getResource("/files/hmrcPersonal.txt"));
            assertTrue(emailWithFileService.emailFile(file, "dateStr"));
        }
    }

    /**
     * Method under test: {@link EmailWithFileService#emailFile(File, String)}.
     */
    @Test
    void testEmailFileNoFileFail() {
        assertFalse(emailWithFileService
            .emailFile(Paths.get(System.getProperty("java.io.tmpdir"), "").toFile(), "dateStr"));
        verifyNoInteractions(notificationClient);
    }

    @Test
    void testEmailFile_ReadFileException() throws IOException {
        assertFalse(emailWithFileService.emailFile(
            Paths.get(System.getProperty("java.io.tmpdir"), "absenty.txt").toFile(), "dateStr"));
        verifyNoInteractions(notificationClient);
    }

    @Test
    void testEmailFileGreaterThan2MB() throws IOException {
        File file = ResourceUtils.getFile(FileUtils.class.getResource("/files/hmrcPersonal.txt"));
        byte[] largeFileContents = new byte[(int) (3 * 1048576)];
        try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {
            when(FileUtils.readFileToByteArray(any(File.class))).thenReturn(largeFileContents);
            assertFalse(emailWithFileService.emailFile(file, "dateStr"));
            verifyNoInteractions(notificationClient);
        }
    }

    @Test
    void givenMultipleEmails_whenOneFails_thenRemainderSent() throws IOException, NotificationClientException {
        final JSONObject jsonObject = new JSONObject().put("testKey", "testValue");

        final String hmrcEmails = "hmrc@example.com;hmrc2@example.com;hmrc3@example.com";
        final SendEmailResponse mockResponse = mock(SendEmailResponse.class);
        final NotificationClientException mockException = mock(NotificationClientException.class);

        try (final var mockStatic = mockStatic(NotificationClient.class)) {
            when(emailAddresses.getHmrcEmail()).thenReturn(hmrcEmails);
            when(NotificationClient.prepareUpload(any(), anyBoolean(), any()))
                    .thenReturn(jsonObject);
            when(notificationClient.sendEmail(any(), any(), any(), any(), any()))
                    .thenReturn(mockResponse)
                    .thenThrow(mockException)
                    .thenReturn(mockResponse);

            File file = ResourceUtils.getFile(FileUtils.class.getResource("/files/hmrcPersonal.txt"));

            final boolean allSuccess = emailWithFileService.emailFile(file,"dateStr");

            assertAll(
                    () -> verify(notificationClient, times(3)).sendEmail(any(), any(), any(), any(), any()),
                    () -> assertFalse(allSuccess, "Expected failure reported due to exception from Notify client"));
        }

    }
}
