package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementDetails;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.reform.ccd.document.am.model.Classification;
import uk.gov.hmcts.reform.ccd.document.am.model.Document.Link;
import uk.gov.hmcts.reform.ccd.document.am.model.Document.Links;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;

@RunWith(MockitoJUnitRunner.class)
public class PDFManagementServiceTest {

    @Mock
    private PDFGeneratorService pdfGeneratorServiceMock;
    @Mock
    private DocumentManagementService documentManagementServiceMock;
    @Mock
    private ObjectMapper objectMapperMock;

    @Mock
    private EvidenceManagementFileUpload evidenceManagementFileUpload;
    @Mock
    private UploadResponse uploadResponseMock;

    private uk.gov.hmcts.reform.ccd.document.am.model.Document document;

    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private WillLodgementCallbackRequest willLodgementCallbackRequestMock;
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
    @Mock
    private Map<String, Object> placeholdersMock;

    private PDFManagementService underTest;

    private static final String SELF_URL = "selfURL";
    private static final String BINARY_URL = "binaryURL";


    @Before
    public void setUp() {
        when(objectMapperMock.copy()).thenReturn(objectMapperMock);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetails);
        when(willLodgementCallbackRequestMock.getCaseDetails()).thenReturn(willLodgementDetails);
        when(pdfServiceConfiguration.getGrantSignatureEncryptedFile()).thenReturn("image.png");
        when(pdfServiceConfiguration.getGrantSignatureSecretKey()).thenReturn("testkey123456789");
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any(String.class)))
            .thenReturn("1kbCfLrFBFTQpS2PnDDYW2r11jfRBVFbjhdLYDEMCR8=");
        underTest = new PDFManagementService(pdfGeneratorServiceMock, documentManagementServiceMock,
            objectMapperMock, httpServletRequest, pdfServiceConfiguration, fileSystemResourceServiceMock);

    }

    @Test
    public void shouldGenerateAndUploadProbateLegalStatement() throws IOException {
        assertDocumentUploaded(LEGAL_STATEMENT_PROBATE, "legalStatementProbate.pdf");
    }

    @Test
    public void shouldGenerateAndUploadIntestacyLegalStatement() throws IOException {
        assertDocumentUploaded(LEGAL_STATEMENT_INTESTACY, "legalStatementIntestacy.pdf");
    }

    @Test
    public void shouldGenerateAndUploadAdmonLegalStatement() throws IOException {
        assertDocumentUploaded(LEGAL_STATEMENT_ADMON, "legalStatementAdmon.pdf");
    }

    @Test
    public void shouldGenerateAndUploadDigitalGrant() throws IOException {
        assertDocumentUploaded(DIGITAL_GRANT, "digitalGrant.pdf");
    }

    @Test
    public void shouldGenerateAndUploadIntestacyGrant() throws IOException {
        assertDocumentUploaded(INTESTACY_GRANT, "intestacyGrant.pdf");
    }

    @Test
    public void shouldGenerateAndUploadAdmonWillGrant() throws IOException {
        assertDocumentUploaded(ADMON_WILL_GRANT, "admonWillGrant.pdf");
    }

    @Test
    public void shouldGenerateAndUploadWillLodgementDepositReceipt() throws IOException {
        assertDocumentUploaded(WILL_LODGEMENT_DEPOSIT_RECEIPT, "willLodgementDepositReceipt.pdf");
    }

    @Test
    public void shouldGenerateAndUploadSentEmail() throws IOException {
        assertDocumentUploaded(SENT_EMAIL, "sentEmail.pdf");
    }

    @Test
    public void shouldGenerateAndUploadCaveatRaised() throws IOException {
        assertDocumentUploaded(CAVEAT_RAISED, "caveatRaised.pdf");
    }

    @Test
    public void shouldGenerateAndUploadDocmosisDocumentCaveatRaised() throws IOException {
        assertDocmosisDocumentUploaded(CAVEAT_RAISED, "caveatRaised.pdf");
    }

    @Test
    public void shouldGenerateAndUploadGrantRaised() throws IOException {
        assertDocumentUploaded(GRANT_RAISED, "grantRaised.pdf");
    }

    @Test
    public void shouldGenerateAndUploadDocmosisDocumentGrantRaised() throws IOException {
        assertDocmosisDocumentUploaded(GRANT_RAISED, "grantRaised.pdf");
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowExceptionIfUnableToDecryptSignatureFile() {
        when(pdfServiceConfiguration.getGrantSignatureSecretKey()).thenReturn("testkey");

        Document response =
            underTest.generateAndUpload(willLodgementCallbackRequestMock, WILL_LODGEMENT_DEPOSIT_RECEIPT);
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
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
            .thenThrow(new ConnectionException(""));

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowConnectExceptionResponseIsNull() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
            .thenReturn(evidenceManagementFileUpload);
        when(documentManagementServiceMock.store(evidenceManagementFileUpload, LEGAL_STATEMENT_PROBATE))
            .thenReturn(null);

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowConnectExceptionResponseDocumentsIsNull() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
            .thenReturn(evidenceManagementFileUpload);
        when(uploadResponseMock.getDocuments()).thenReturn(null);
        when(documentManagementServiceMock.store(evidenceManagementFileUpload, LEGAL_STATEMENT_PROBATE))
            .thenReturn(uploadResponseMock);

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowConnectExceptionResponseDocumentsIsEmpty() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
            .thenReturn(evidenceManagementFileUpload);
        when(uploadResponseMock.getDocuments()).thenReturn(Collections.emptyList());
        when(documentManagementServiceMock.store(evidenceManagementFileUpload, LEGAL_STATEMENT_PROBATE))
            .thenReturn(uploadResponseMock);

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowConnectExceptionResponseFirstDocumentLinksAreNUll() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
            .thenReturn(evidenceManagementFileUpload);
        uk.gov.hmcts.reform.ccd.document.am.model.Document document =
            uk.gov.hmcts.reform.ccd.document.am.model.Document.builder()
            .build();
        when(uploadResponseMock.getDocuments()).thenReturn(Arrays.asList(document));
        when(documentManagementServiceMock.store(evidenceManagementFileUpload, LEGAL_STATEMENT_PROBATE))
            .thenReturn(uploadResponseMock);

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowConnectExceptionResponseFirstDocumentLinkBinaryIsNUll() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
            .thenReturn(evidenceManagementFileUpload);
        Links links = new Links();
        links.self = new Link();
        uk.gov.hmcts.reform.ccd.document.am.model.Document document =
            uk.gov.hmcts.reform.ccd.document.am.model.Document.builder()
                .links(links)
                .build();
        when(uploadResponseMock.getDocuments()).thenReturn(Arrays.asList(document));
        when(documentManagementServiceMock.store(evidenceManagementFileUpload, LEGAL_STATEMENT_PROBATE))
            .thenReturn(uploadResponseMock);

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowConnectExceptionResponseFirstDocumentLinkSelfIsNUll() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
            .thenReturn(evidenceManagementFileUpload);
        Links links = new Links();
        links.binary = new Link();
        uk.gov.hmcts.reform.ccd.document.am.model.Document document =
            uk.gov.hmcts.reform.ccd.document.am.model.Document.builder()
                .links(links)
                .build();
        when(uploadResponseMock.getDocuments()).thenReturn(Arrays.asList(document));
        when(documentManagementServiceMock.store(evidenceManagementFileUpload, LEGAL_STATEMENT_PROBATE))
            .thenReturn(uploadResponseMock);

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }
    
    @Test
    public void testGetDecodedSignatureReturnsBase64String() {
        assertThat(underTest.getDecodedSignature(), is("dGhpcyBpcyBhIHRleHQgbWVzc2FnZS4K"));
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowConnectExceptionWhenBinaryLinkNotPresent() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
            .thenReturn(evidenceManagementFileUpload);
        when(documentManagementServiceMock.store(evidenceManagementFileUpload, LEGAL_STATEMENT_PROBATE))
            .thenReturn(uploadResponseMock);

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowConnectExceptionWhenSelfLinkNotPresent() throws IOException {
        String json = "{}";

        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
            .thenReturn(evidenceManagementFileUpload);
        when(documentManagementServiceMock.store(evidenceManagementFileUpload, LEGAL_STATEMENT_PROBATE))
            .thenReturn(uploadResponseMock);

        underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
    }

    private void setupResponseDocument(String fileName) {
        Link self = new Link();
        self.href = SELF_URL;
        Link binary = new Link();
        binary.href = BINARY_URL;
        Links links = new Links();
        links.self = self;
        links.binary = binary;
        document = uk.gov.hmcts.reform.ccd.document.am.model.Document.builder()
            .links(links)
            .originalDocumentName(fileName)
            .classification(Classification.PRIVATE)
            .build();

        List<uk.gov.hmcts.reform.ccd.document.am.model.Document> documentsList = new ArrayList<>();
        documentsList.add(document);
        when(uploadResponseMock.getDocuments()).thenReturn(documentsList);
    }

    private void assertAll(Document response, String fileName) {
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(BINARY_URL, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(SELF_URL, response.getDocumentLink().getDocumentUrl());
    }

    private void assertDocumentUploaded(DocumentType docType, String fileName) throws IOException {
        String json = "{}";
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(docType, json))
            .thenReturn(evidenceManagementFileUpload);
        when(documentManagementServiceMock.store(evidenceManagementFileUpload, docType))
            .thenReturn(uploadResponseMock);
        setupResponseDocument(fileName);

        Document response = underTest.generateAndUpload(callbackRequestMock, docType);

        assertAll(response, fileName);
    }

    private void assertDocmosisDocumentUploaded(DocumentType docType, String fileName) throws IOException {
        when(pdfGeneratorServiceMock
            .generateDocmosisDocumentFrom(docType.getTemplateName(), placeholdersMock))
            .thenReturn(evidenceManagementFileUpload);
        when(documentManagementServiceMock.store(evidenceManagementFileUpload, docType))
            .thenReturn(uploadResponseMock);
        setupResponseDocument(fileName);

        Document response = underTest.generateDocmosisDocumentAndUpload(placeholdersMock, docType);

        assertAll(response, fileName);
    }

}
