package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementDetails;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.docmosis.CaveatDocmosisService;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.reform.ccd.document.am.model.Document.Links;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import javax.crypto.BadPaddingException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.SOLICITOR_COVERSHEET;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;
import static uk.gov.hmcts.reform.ccd.document.am.model.Classification.PRIVATE;

@ExtendWith(SpringExtension.class)
class PDFManagementServiceTest {

    @Mock
    private PDFGeneratorService pdfGeneratorServiceMock;
    @Mock
    private DocumentManagementService documentManagementServiceMock;
    @Mock
    private EvidenceManagementFileUpload evidenceManagementFileUpload;
    @Mock
    private UploadResponse uploadResponseMock;

    private uk.gov.hmcts.reform.ccd.document.am.model.Document document;
    @Mock
    private EvidenceManagementFile evidenceManagementFile;

    private Optional<Link> optionalLink;
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
    private PDFDecoratorService pdfDecoratorService;
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

    @BeforeEach
    public void setUp() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetails);
        when(willLodgementCallbackRequestMock.getCaseDetails()).thenReturn(willLodgementDetails);
        when(pdfServiceConfiguration.getGrantSignatureEncryptedFile()).thenReturn("image.png");
        when(pdfServiceConfiguration.getGrantSignatureSecretKey()).thenReturn("testkey123456789");
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any(String.class)))
                .thenReturn("1kbCfLrFBFTQpS2PnDDYW2r11jfRBVFbjhdLYDEMCR8=");
        underTest = new PDFManagementService(pdfGeneratorServiceMock, httpServletRequest, documentManagementServiceMock,
                pdfServiceConfiguration, fileSystemResourceServiceMock, pdfDecoratorService);
    }

    @Test
    void shouldGenerateAndUploadIntestacyCoversheet() throws IOException {
        assertDocumentUploaded(SOLICITOR_COVERSHEET, "solicitorCoverSheet.pdf");
    }

    @Test
    void shouldGenerateAndUploadProbateLegalStatement() throws IOException {
        assertDocumentUploaded(LEGAL_STATEMENT_PROBATE, "legalStatementProbate.pdf");
    }

    @Test
    void shouldGenerateAndUploadProbateTrustCorpsLegalStatement() throws IOException {
        String json = "{}";
        when(pdfDecoratorService.decorate(callbackRequestMock, LEGAL_STATEMENT_PROBATE_TRUST_CORPS)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE_TRUST_CORPS, json))
            .thenReturn(evidenceManagementFileUpload);
        when(documentManagementServiceMock.upload(any(), any())).thenReturn(uploadResponseMock);
        Links links = new Links();
        links.self = new uk.gov.hmcts.reform.ccd.document.am.model.Document.Link();
        links.binary = new uk.gov.hmcts.reform.ccd.document.am.model.Document.Link();
        links.self.href = "href";
        links.binary.href = "binaryhref";
        uk.gov.hmcts.reform.ccd.document.am.model.Document document =
            uk.gov.hmcts.reform.ccd.document.am.model.Document.builder()
                .links(links)
                .build();
        when(uploadResponseMock.getDocuments()).thenReturn(Arrays.asList(document));

        Document response = underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE_TRUST_CORPS);

        String fileName = "legalStatementGrantOfProbate.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals("binaryhref", response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals("href", response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadIntestacyLegalStatement() throws IOException {
        assertDocumentUploaded(LEGAL_STATEMENT_INTESTACY, "legalStatementIntestacy.pdf");
    }

    @Test
    void shouldGenerateAndUploadAdmonLegalStatement() throws IOException {
        assertDocumentUploaded(LEGAL_STATEMENT_ADMON, "legalStatementAdmon.pdf");
    }

    @Test
    void shouldGenerateAndUploadDigitalGrant() throws IOException {
        assertDocumentUploaded(DIGITAL_GRANT, "digitalGrant.pdf");
    }

    @Test
    void shouldGenerateAndUploadIntestacyGrant() throws IOException {
        assertDocumentUploaded(INTESTACY_GRANT, "intestacyGrant.pdf");
    }

    @Test
    void shouldGenerateAndUploadAdColligendaBonaGrant() throws IOException {
        assertDocumentUploaded(AD_COLLIGENDA_BONA_GRANT, "adColligendaBonaGrant.pdf");
    }

    @Test
    void shouldGenerateAndUploadAdmonWillGrant() throws IOException {
        assertDocumentUploaded(ADMON_WILL_GRANT, "admonWillGrant.pdf");
    }

    @Test
    void shouldGenerateAndUploadWillLodgementDepositReceipt() throws IOException {
        assertDocumentUploaded(WILL_LODGEMENT_DEPOSIT_RECEIPT, "willLodgementDepositReceipt.pdf");
    }

    @Test
    void shouldGenerateAndUploadSentEmail() throws IOException {
        assertDocumentUploaded(SENT_EMAIL, "sentEmail.pdf");
    }

    @Test
    void shouldGenerateAndUploadCaveatRaised() throws IOException {
        assertDocumentUploaded(CAVEAT_RAISED, "caveatRaised.pdf");
    }

    @Test
    void shouldGenerateAndUploadDocmosisDocumentCaveatRaised() throws IOException {
        assertDocmosisDocumentUploaded(CAVEAT_RAISED, "caveatRaised.pdf");
    }

    @Test
    void shouldGenerateAndUploadGrantRaised() throws IOException {
        assertDocumentUploaded(GRANT_RAISED, "grantRaised.pdf");
    }

    @Test
    void shouldGenerateAndUploadDocmosisDocumentGrantRaised() throws IOException {
        assertDocmosisDocumentUploaded(GRANT_RAISED, "grantRaised.pdf");
    }

    @Test
    void shouldThrowExceptionIfUnableToDecryptSignatureFile() throws IOException {
        assertThrows(BadRequestException.class, () -> {
            when(pdfServiceConfiguration.getGrantSignatureSecretKey()).thenReturn("testkey");

            Document response =
                    underTest.generateAndUpload(willLodgementCallbackRequestMock, WILL_LODGEMENT_DEPOSIT_RECEIPT);
        });
    }

    @Test
    void shouldThrowExceptionForInvalidRequest() throws IOException {
        assertThrows(BadRequestException.class, () -> {
            when(pdfDecoratorService.decorate(callbackRequestMock, LEGAL_STATEMENT_PROBATE))
                    .thenThrow(BadRequestException.class);

            underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
        });
    }

    @Test
    void shouldThrowExceptionWhenUnableToGeneratePDF() throws IOException {
        assertThrows(ConnectionException.class, () -> {
            String json = "{}";

            when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
                    .thenThrow(new ConnectionException(""));
            when(pdfDecoratorService.decorate(callbackRequestMock, LEGAL_STATEMENT_PROBATE)).thenReturn(json);

            underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
        });
    }

    @Test
    void shouldThrowConnectExceptionWhenFileUploadThrowsIOException() throws IOException {
        assertThrows(ConnectionException.class, () -> {
            String json = "{}";

            when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
                    .thenReturn(evidenceManagementFileUpload);
            when(documentManagementServiceMock.upload(evidenceManagementFileUpload, LEGAL_STATEMENT_PROBATE))
                    .thenReturn(uploadResponseMock);
            when(pdfDecoratorService.decorate(callbackRequestMock, LEGAL_STATEMENT_PROBATE)).thenReturn(json);

            underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
        });
    }

    @Test
    void testGetDecodedSignatureReturnsBase64String() {
        assertThat(underTest.getDecodedSignature(), is("dGhpcyBpcyBhIHRleHQgbWVzc2FnZS4K"));
    }

    @Test
    void shouldThrowConnectExceptionWhenBinaryLinkNotPresent() throws IOException {
        assertThrows(ConnectionException.class, () -> {
            String json = "{}";

            when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
                    .thenReturn(evidenceManagementFileUpload);
            when(documentManagementServiceMock.upload(evidenceManagementFileUpload, LEGAL_STATEMENT_PROBATE))
                    .thenReturn(uploadResponseMock);
            optionalLink = Optional.of(link);
            when(evidenceManagementFile.getLink(IanaLinkRelations.SELF)).thenReturn(optionalLink);
            when(pdfDecoratorService.decorate(callbackRequestMock, LEGAL_STATEMENT_PROBATE)).thenReturn(json);

            underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
        });
    }

    @Test
    void shouldThrowConnectExceptionWhenSelfLinkNotPresent() throws IOException {
        assertThrows(ConnectionException.class, () -> {
            String json = "{}";

            when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
                    .thenReturn(evidenceManagementFileUpload);
            when(documentManagementServiceMock.upload(evidenceManagementFileUpload, LEGAL_STATEMENT_PROBATE))
                    .thenReturn(uploadResponseMock);
            optionalLink = Optional.of(link);
            when(evidenceManagementFile.getLink("binary")).thenReturn(optionalLink);
            when(pdfDecoratorService.decorate(callbackRequestMock, LEGAL_STATEMENT_PROBATE)).thenReturn(json);

            underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);
        });
    }

    private void mockLinks(String href) {
        optionalLink = Optional.of(link);
        when(evidenceManagementFile.getLink(IanaLinkRelations.SELF)).thenReturn(optionalLink);
        when(evidenceManagementFile.getLink("binary")).thenReturn(optionalLink);
        when(link.getHref()).thenReturn(href);
    }

    private void setupResponseDocument(String fileName) {
        uk.gov.hmcts.reform.ccd.document.am.model.Document.Link self =
                new uk.gov.hmcts.reform.ccd.document.am.model.Document.Link();
        self.href = SELF_URL;
        uk.gov.hmcts.reform.ccd.document.am.model.Document.Link binary =
                new uk.gov.hmcts.reform.ccd.document.am.model.Document.Link();
        binary.href = BINARY_URL;
        Links links = new Links();
        links.self = self;
        links.binary = binary;
        document = uk.gov.hmcts.reform.ccd.document.am.model.Document.builder()
                .links(links)
                .originalDocumentName(fileName)
                .classification(PRIVATE)
                .build();

        List<uk.gov.hmcts.reform.ccd.document.am.model.Document> documentsList = new ArrayList<>();
        documentsList.add(document);
        when(uploadResponseMock.getDocuments()).thenReturn(documentsList);
    }

    private void assertDocumentUploaded(DocumentType docType, String fileName) throws IOException {
        String json = "{}";
        when(pdfDecoratorService.decorate(callbackRequestMock, docType)).thenReturn(json);
        when(pdfGeneratorServiceMock.generatePdf(docType, json))
                .thenReturn(evidenceManagementFileUpload);
        when(documentManagementServiceMock.upload(evidenceManagementFileUpload, docType))
                .thenReturn(uploadResponseMock);
        setupResponseDocument(fileName);

        Document response = underTest.generateAndUpload(callbackRequestMock, docType);

        assertAll(response, fileName);
    }

    private void assertDocmosisDocumentUploaded(DocumentType docType, String fileName) throws IOException {
        when(pdfGeneratorServiceMock
            .generateDocmosisDocumentFrom(docType.getTemplateName(), placeholdersMock))
            .thenReturn(evidenceManagementFileUpload);
        when(documentManagementServiceMock.upload(evidenceManagementFileUpload, docType))
            .thenReturn(uploadResponseMock);
        setupResponseDocument(fileName);

        Document response = underTest.generateDocmosisDocumentAndUpload(placeholdersMock, docType);

        assertAll(response, fileName);
    }

    private void assertAll(Document response, String fileName) {
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(BINARY_URL, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(SELF_URL, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void testXhtmlReplacesBrTag() {
        final String inputHtml = "<p>something</p><br><p>other</p>";
        final String expectedXhtml = "<p>something</p><br /><p>other</p>";

        final String actualXhtml = underTest.rerenderAsXhtml(inputHtml);

        assertEquals(expectedXhtml, actualXhtml, "Expected result to have closed <br /> tag");
    }
}
