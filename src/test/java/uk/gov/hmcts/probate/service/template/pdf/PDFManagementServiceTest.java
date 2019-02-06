package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.hateoas.Link;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementDetails;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.UploadService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;

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
    private WillLodgementCallbackRequest willLodgementCallbackRequestMock;    
    @Mock
    private SentEmail sentEmailMock;
    @Mock
    private JsonProcessingException jsonProcessingException;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private PDFServiceConfiguration pdfServiceConfiguration;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private WillLodgementDetails willLodgementDetails;
    @Mock
    private FileSystemResourceService fileSystemResourceServiceMock;
    
    private PDFManagementService underTest;

    @Before
    public void setUp() {
        when(objectMapperMock.copy()).thenReturn(objectMapperMock);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetails);
        when(willLodgementCallbackRequestMock.getCaseDetails()).thenReturn(willLodgementDetails);
        when(pdfServiceConfiguration.getGrantSignatureEncryptedFile()).thenReturn("image.png");
        when(pdfServiceConfiguration.getGrantSignatureSecretKey()).thenReturn("testkey123456789");
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any(String.class)))
            .thenReturn("1kbCfLrFBFTQpS2PnDDYW2r11jfRBVFbjhdLYDEMCR8=");
        underTest = new PDFManagementService(pdfGeneratorServiceMock, uploadServiceMock,
                objectMapperMock, httpServletRequest, pdfServiceConfiguration, fileSystemResourceServiceMock);
    }
    
    @Test
    public void shouldGenerateAndUploadLegalStatement() throws IOException {
        String json = "{}";
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT);

        String fileName = "legalStatement.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    public void shouldGenerateAndUploadDigitalGrant() throws IOException {
        String json = "{}";
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(DIGITAL_GRANT, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateAndUpload(callbackRequestMock, DIGITAL_GRANT);

        String fileName = "digitalGrant.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }
    
    @Test
    public void shouldGenerateAndUploadIntestacyGrant() throws IOException {
        String json = "{}";
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(INTESTACY_GRANT, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateAndUpload(callbackRequestMock, INTESTACY_GRANT);

        String fileName = "intestacyGrant.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    public void shouldGenerateAndUploadAdmonWillGrant() throws IOException {
        String json = "{}";
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(ADMON_WILL_GRANT, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateAndUpload(callbackRequestMock, ADMON_WILL_GRANT);

        String fileName = "admonWillGrant.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    public void shouldGenerateAndUploadWillLodgementDepositReceipt() throws IOException {
        String json = "{}";
        when(objectMapperMock.writeValueAsString(willLodgementCallbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(WILL_LODGEMENT_DEPOSIT_RECEIPT, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateAndUpload(willLodgementCallbackRequestMock, WILL_LODGEMENT_DEPOSIT_RECEIPT);

        String fileName = "willLodgementDepositReceipt.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    public void shouldGenerateAndUploadSentEmail() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(sentEmailMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(SENT_EMAIL, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateAndUpload(sentEmailMock, SENT_EMAIL);

        String fileName = "sentEmail.pdf";
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
