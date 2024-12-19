package uk.gov.hmcts.probate.service;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.ccd.document.am.model.Document;

import java.io.IOException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DocumentValidationTest {
    @Mock
    private DocumentManagementService documentManagementService;

    private AutoCloseable closeableMocks;

    private DocumentValidation documentValidation;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);
        documentValidation = new DocumentValidation(documentManagementService);
        ReflectionTestUtils.setField(documentValidation,
            "allowedFileExtensions", ".pdf .jpeg .bmp .tif .tiff .png .pdf");
        ReflectionTestUtils.setField(documentValidation,
            "allowedMimeTypes", "image/jpeg application/pdf image/tiff image/png image/bmp");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
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

    @Test
    void acceptPdfUpload() {
        final long caseId = 1;
        final String documentUrl = "documentUrl";
        final DocumentLink amendedLegalStatement = DocumentLink.builder()
                .documentUrl(documentUrl)
                .build();

        final Document mockDocument = Document.builder()
                .mimeType(MediaType.APPLICATION_PDF_VALUE)
                .build();

        final MediaType wantedType = MediaType.APPLICATION_PDF;

        when(documentManagementService.getMetadataByUrl(documentUrl)).thenReturn(mockDocument);

        final Optional<String> actual = documentValidation.validateUploadedDocumentIsType(
                caseId,
                amendedLegalStatement,
                wantedType);

        assertTrue(actual.isEmpty(), "Expected no error response from validating PDF");
    }

    @Test
    void rejectTextUpload() {
        final long caseId = 1;
        final String documentUrl = "documentUrl";
        final DocumentLink amendedLegalStatement = DocumentLink.builder()
                .documentUrl(documentUrl)
                .build();

        final String docName = "some.txt";
        final String docType = MediaType.TEXT_PLAIN_VALUE;
        final Document mockDocument = Document.builder()
                .originalDocumentName(docName)
                .mimeType(docType)
                .build();

        when(documentManagementService.getMetadataByUrl(documentUrl)).thenReturn(mockDocument);

        final MediaType wantedType = MediaType.APPLICATION_PDF;

        final Optional<String> actual = documentValidation.validateUploadedDocumentIsType(
                caseId,
                amendedLegalStatement,
                wantedType);

        assertTrue(actual.isPresent(), "Expected error response for text upload.");

        final String actualMsg = actual.get();
        assertAll(
                () -> assertTrue(actualMsg.contains(docName),
                        "Expected error message to contain original document name: " + docName),
                () -> assertTrue(actualMsg.contains(docType),
                        "Expected error message to contain original document type: " + docType),
                () -> assertTrue(actualMsg.contains(wantedType.toString()),
                        "Expected error message to contain expected document type: " + wantedType)
        );
    }

}
