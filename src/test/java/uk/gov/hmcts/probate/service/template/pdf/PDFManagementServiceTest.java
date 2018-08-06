package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.hateoas.Link;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.UploadService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT;

@RunWith(MockitoJUnitRunner.class)
public class PDFManagementServiceTest {

    @Mock
    private PDFGeneratorService pdfGeneratorServiceMock;
    @Mock
    private UploadService uploadServiceMock;
    @Mock
    private ObjectMapper objectMapperMock;

    @Mock
    private EvidenceManagementFileUpload evidenceManagementFileUpload;
    @Mock
    private EvidenceManagementFile evidenceManagementFile;
    @Mock
    private Link link;
    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private JsonProcessingException jsonProcessingException;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private PDFServiceConfiguration pdfServiceConfiguration;

    private PDFManagementService underTest;

    @Before
    public void setUp() {
        when(objectMapperMock.copy()).thenReturn(objectMapperMock);
        underTest = new PDFManagementService(pdfGeneratorServiceMock, uploadServiceMock,
                objectMapperMock, httpServletRequest, pdfServiceConfiguration);
    }

    @Test
    public void shouldGenerateAndUpload() throws IOException {
        String json = "{}";
        String fileName = "legalStatement.pdf";
        String href = "href";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);
        when(evidenceManagementFile.getOriginalDocumentName()).thenReturn(fileName);

        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT);

        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowExceptionForInvalidRequest() throws IOException {
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenThrow(jsonProcessingException);

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT);
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowExceptionWhenUnableToGeneratePDF() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT, json)).thenThrow(new ConnectionException(""));

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT);
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowConnectExceptionWhenFileUploadThrowsIOException() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenThrow(new IOException());

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT);
    }
}
