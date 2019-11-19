package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;
import org.springframework.hateoas.*;
import uk.gov.hmcts.probate.config.*;
import uk.gov.hmcts.probate.exception.*;
import uk.gov.hmcts.probate.model.*;
import uk.gov.hmcts.probate.model.ccd.caveat.request.*;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.*;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.*;
import uk.gov.hmcts.probate.model.evidencemanagement.*;
import uk.gov.hmcts.probate.service.*;
import uk.gov.hmcts.probate.service.docmosis.*;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.*;

import javax.crypto.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.probate.model.DocumentType.*;

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
    private CaveatCallbackRequest caveatCallbackRequestMock;
    @Mock
    private CaveatDocmosisService caveatDocmosisServiceMock;
    @Mock
    private SentEmail sentEmailMock;
    @Mock
    private JsonProcessingException jsonProcessingException;
    @Mock
    private BadPaddingException badPaddingException;
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
    @Mock
    private Map<String, Object> placeholdersMock;
    
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
    public void shouldGenerateAndUploadProbateLegalStatement() throws IOException {
        String json = "{}";
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);

        String fileName = "legalStatementProbate.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    public void shouldGenerateAndUploadIntestacyLegalStatement() throws IOException {
        String json = "{}";
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_INTESTACY, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_INTESTACY);

        String fileName = "legalStatementIntestacy.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    public void shouldGenerateAndUploadAdmonLegalStatement() throws IOException {
        String json = "{}";
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_ADMON, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_ADMON);

        String fileName = "legalStatementAdmon.pdf";
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

    @Test
    public void shouldGenerateAndUploadCaveatRaised() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(caveatCallbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(CAVEAT_RAISED, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateAndUpload(caveatCallbackRequestMock, CAVEAT_RAISED);

        String fileName = "caveatRaised.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    public void shouldGenerateAndUploadDocmosisDocument() throws IOException {
        String json = "{}";

        when(pdfGeneratorServiceMock
                .generateDocmosisDocumentFrom(CAVEAT_RAISED.getTemplateName(), placeholdersMock))
                .thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateDocmosisDocumentAndUpload(placeholdersMock, CAVEAT_RAISED);

        String fileName = "caveatRaised.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    public void generateDocumentAndUpload() throws IOException {
        byte[] bytes = "string".getBytes();

        when(pdfGeneratorServiceMock.uploadDocument(bytes))
                .thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);

        String href = "href";
        when(link.getHref()).thenReturn(href);

        Document response = underTest.generateDocumentAndUpload(bytes);

        String fileName = "sealedWill.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    public void shouldGenerateDocmosisDocument() {
        when(pdfGeneratorServiceMock
                .generateDocmosisDocumentFrom(THIRD_PARTY_COVERSHEET.getTemplateName(), placeholdersMock))
                .thenReturn(evidenceManagementFileUpload);

        EvidenceManagementFileUpload response = underTest.generateDocmosisDocument(placeholdersMock, THIRD_PARTY_COVERSHEET, callbackRequestMock);
        assertNotNull(response);
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowExceptionIfUnableToDecryptSignatureFile() throws IOException {
        when(pdfServiceConfiguration.getGrantSignatureSecretKey()).thenReturn("testkey");

        Document response = underTest.generateAndUpload(willLodgementCallbackRequestMock, WILL_LODGEMENT_DEPOSIT_RECEIPT);
    }
    
    @Test(expected = BadRequestException.class)
    public void shouldThrowExceptionForInvalidRequest() throws IOException {
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenThrow(jsonProcessingException);

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowExceptionWhenUnableToGeneratePDF() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json)).thenThrow(new ConnectionException(""));

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowConnectExceptionWhenFileUploadThrowsIOException() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenThrow(new IOException());

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }

    @Test
    public void testGetDecodedSignatureReturnsBase64String() {
        assertThat(underTest.getDecodedSignature(), is("dGhpcyBpcyBhIHRleHQgbWVzc2FnZS4K"));
    }
}
