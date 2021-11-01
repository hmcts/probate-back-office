package uk.gov.hmcts.probate.service;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class DocumentValidationTest {

    private DocumentValidation documentValidation;

    @Before
    public void setUp() {
        documentValidation = new DocumentValidation();
        ReflectionTestUtils.setField(documentValidation,
            "allowedFileExtensions", ".pdf .jpeg .bmp .tif .tiff .png .pdf");
        ReflectionTestUtils.setField(documentValidation,
            "allowedMimeTypes", "image/jpeg application/pdf image/tiff image/png image/bmp");
    }

    @Test
    public void rejectInvalidFileForContentType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.pdf", "filename.pdf", "text/plain", "some xml".getBytes());
        boolean result = documentValidation.isValid(file);
        assertThat(result, equalTo(false));
    }

    @Test
    public void rejectInvalidFileForFileName() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.txt", "filename.txt", "image/jpeg", "some xml".getBytes());
        boolean result = documentValidation.isValid(file);
        assertThat(result, equalTo(false));
    }

    @Test
    public void rejectInvalidFileForFileSize() throws IOException {
        TestUtils testUtils = new TestUtils();
        MockMultipartFile file = new MockMultipartFile("filename.txt", "filename.txt", "image/jpeg",
            testUtils.getStringFromFile("files/large_pdf.pdf").getBytes());
        boolean result = documentValidation.isValid(file);
        assertThat(result, equalTo(false));
    }

    @Test
    public void approveValidFile() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.isValid(file);
        assertThat(result, equalTo(true));
    }

    @Test
    public void rejectInvalidMimeType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.txt", "filename.txt", "text/plain", "some xml".getBytes());
        boolean result = documentValidation.validMimeType(file.getContentType());
        assertThat(result, equalTo(false));
    }

    @Test
    public void approveValidMimeType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.validMimeType(file.getContentType());
        assertThat(result, equalTo(true));
    }

    @Test
    public void rejectInvalidFileType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.txt", "filename.txt", "text/plain", "some xml".getBytes());
        boolean result = documentValidation.validFileType(file.getName());
        assertThat(result, equalTo(false));
    }

    @Test
    public void approveValidFileType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.validFileType(file.getName());
        assertThat(result, equalTo(true));
    }

    @Test
    public void rejectInvalidFileSize() throws IOException {
        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/large_pdf.pdf"));
        MockMultipartFile file = new MockMultipartFile("filename.pdf", "filename.pdf", "application/pdf", bytes);
        boolean result = documentValidation.validFileSize(file);
        assertThat(result, equalTo(false));
    }

    @Test
    public void approveValidFileSize() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.validFileSize(file);
        assertThat(result, equalTo(true));
    }
}
