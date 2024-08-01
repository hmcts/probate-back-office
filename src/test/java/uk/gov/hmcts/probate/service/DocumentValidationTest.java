package uk.gov.hmcts.probate.service;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DocumentValidationTest {

    private DocumentValidation documentValidation;

    @BeforeEach
    public void setUp() {
        documentValidation = new DocumentValidation();
        ReflectionTestUtils.setField(documentValidation,
            "allowedFileExtensions", ".pdf .jpeg .bmp .tif .tiff .png .pdf");
        ReflectionTestUtils.setField(documentValidation,
            "allowedMimeTypes", "image/jpeg application/pdf image/tiff image/png image/bmp");
    }

    @Test
    void rejectInvalidFileForContentType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.pdf", "filename.pdf", "text/plain", "some xml".getBytes());
        boolean result = documentValidation.isValid(file);
        assertThat(result, equalTo(false));
    }

    @Test
    void rejectInvalidFileForFileName() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.txt", "filename.txt", "image/jpeg", "some xml".getBytes());
        boolean result = documentValidation.isValid(file);
        assertThat(result, equalTo(false));
    }

    @Test
    void rejectInvalidFileForFileSize() throws IOException {
        TestUtils testUtils = new TestUtils();
        MockMultipartFile file = new MockMultipartFile("filename.txt", "filename.txt", "image/jpeg",
            testUtils.getStringFromFile("files/large_pdf.pdf").getBytes());
        boolean result = documentValidation.isValid(file);
        assertThat(result, equalTo(false));
    }

    @Test
    void approveValidFile() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.isValid(file);
        assertThat(result, equalTo(true));
    }

    @Test
    void rejectInvalidMimeType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.txt", "filename.txt", "text/plain", "some xml".getBytes());
        boolean result = documentValidation.validMimeType(file.getContentType());
        assertThat(result, equalTo(false));
    }

    @Test
    void approveValidMimeType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.validMimeType(file.getContentType());
        assertThat(result, equalTo(true));
    }

    @Test
    void rejectInvalidFileType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.txt", "filename.txt", "text/plain", "some xml".getBytes());
        boolean result = documentValidation.validFileType(file.getName());
        assertThat(result, equalTo(false));
    }

    @Test
    void approveValidFileType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.validFileType(file.getName());
        assertThat(result, equalTo(true));
    }

    @Test
    void rejectInvalidFileSize() throws IOException {
        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/large_pdf.pdf"));
        MockMultipartFile file = new MockMultipartFile("filename.pdf", "filename.pdf", "application/pdf", bytes);
        boolean result = documentValidation.validFileSize(file);
        assertThat(result, equalTo(false));
    }

    @Test
    void approveValidFileSize() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.validFileSize(file);
        assertThat(result, equalTo(true));
    }
}
