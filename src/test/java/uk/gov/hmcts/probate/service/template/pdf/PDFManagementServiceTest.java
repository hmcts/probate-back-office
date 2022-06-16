package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.gov.hmcts.probate.service.evidencemanagement.upload.UploadService;

import javax.crypto.BadPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.SOLICITOR_COVERSHEET;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;

@ExtendWith(SpringExtension.class)
class PDFManagementServiceTest {

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

    @BeforeEach
    public void setUp() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetails);
        when(willLodgementCallbackRequestMock.getCaseDetails()).thenReturn(willLodgementDetails);
        when(pdfServiceConfiguration.getGrantSignatureEncryptedFile()).thenReturn("image.png");
        when(pdfServiceConfiguration.getGrantSignatureSecretKey()).thenReturn("testkey123456789");
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any(String.class)))
            .thenReturn("1kbCfLrFBFTQpS2PnDDYW2r11jfRBVFbjhdLYDEMCR8=");
        underTest = new PDFManagementService(pdfGeneratorServiceMock, uploadServiceMock,
            httpServletRequest, pdfServiceConfiguration, fileSystemResourceServiceMock, pdfDecoratorService);
    }

    @Test
    void shouldGenerateAndUploadIntestacyCoversheet() throws IOException {
        String json = "{}";
        when(pdfGeneratorServiceMock.generatePdf(SOLICITOR_COVERSHEET, json))
            .thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(callbackRequestMock, SOLICITOR_COVERSHEET)).thenReturn(json);

        Document response = underTest.generateAndUpload(callbackRequestMock, SOLICITOR_COVERSHEET);

        String fileName = "solicitorCoverSheet.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadProbateLegalStatement() throws IOException {
        String json = "{}";
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE, json))
            .thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(callbackRequestMock, LEGAL_STATEMENT_PROBATE)).thenReturn(json);

        Document response = underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE);

        String fileName = "legalStatementProbate.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadProbateTrustCorpsLegalStatement() throws IOException {
        String json = "{}";
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_PROBATE_TRUST_CORPS, json))
                .thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(callbackRequestMock, LEGAL_STATEMENT_PROBATE_TRUST_CORPS)).thenReturn(json);

        Document response = underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_PROBATE_TRUST_CORPS);

        String fileName = "legalStatementGrantOfProbate.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadIntestacyLegalStatement() throws IOException {
        String json = "{}";
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_INTESTACY, json))
            .thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(callbackRequestMock, LEGAL_STATEMENT_INTESTACY)).thenReturn(json);

        Document response = underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_INTESTACY);

        String fileName = "legalStatementIntestacy.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadAdmonLegalStatement() throws IOException {
        String json = "{}";
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT_ADMON, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(callbackRequestMock, LEGAL_STATEMENT_ADMON)).thenReturn(json);

        Document response = underTest.generateAndUpload(callbackRequestMock, LEGAL_STATEMENT_ADMON);

        String fileName = "legalStatementAdmon.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadDigitalGrant() throws IOException {
        String json = "{}";
        when(pdfGeneratorServiceMock.generatePdf(DIGITAL_GRANT, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(callbackRequestMock, DIGITAL_GRANT)).thenReturn(json);

        Document response = underTest.generateAndUpload(callbackRequestMock, DIGITAL_GRANT);

        String fileName = "digitalGrant.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadIntestacyGrant() throws IOException {
        String json = "{}";
        when(pdfGeneratorServiceMock.generatePdf(INTESTACY_GRANT, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(callbackRequestMock, INTESTACY_GRANT)).thenReturn(json);

        Document response = underTest.generateAndUpload(callbackRequestMock, INTESTACY_GRANT);

        String fileName = "intestacyGrant.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadAdmonWillGrant() throws IOException {
        String json = "{}";
        when(pdfGeneratorServiceMock.generatePdf(ADMON_WILL_GRANT, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(callbackRequestMock, ADMON_WILL_GRANT)).thenReturn(json);

        Document response = underTest.generateAndUpload(callbackRequestMock, ADMON_WILL_GRANT);

        String fileName = "admonWillGrant.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadWillLodgementDepositReceipt() throws IOException {
        String json = "{}";
        when(pdfGeneratorServiceMock.generatePdf(WILL_LODGEMENT_DEPOSIT_RECEIPT, json))
            .thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(willLodgementCallbackRequestMock, WILL_LODGEMENT_DEPOSIT_RECEIPT))
            .thenReturn(json);

        Document response =
            underTest.generateAndUpload(willLodgementCallbackRequestMock, WILL_LODGEMENT_DEPOSIT_RECEIPT);

        String fileName = "willLodgementDepositReceipt.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadSentEmail() throws IOException {
        String json = "{}";

        when(pdfGeneratorServiceMock.generatePdf(SENT_EMAIL, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(sentEmailMock, SENT_EMAIL)).thenReturn(json);

        Document response = underTest.generateAndUpload(sentEmailMock, SENT_EMAIL);

        String fileName = "sentEmail.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadCaveatRaised() throws IOException {
        String json = "{}";

        when(pdfGeneratorServiceMock.generatePdf(CAVEAT_RAISED, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(caveatCallbackRequestMock, CAVEAT_RAISED)).thenReturn(json);

        Document response = underTest.generateAndUpload(caveatCallbackRequestMock, CAVEAT_RAISED);

        String fileName = "caveatRaised.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadDocmosisDocumentCaveatRaised() throws IOException {
        String json = "{}";

        when(pdfGeneratorServiceMock
            .generateDocmosisDocumentFrom(CAVEAT_RAISED.getTemplateName(), placeholdersMock))
            .thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);

        Document response = underTest.generateDocmosisDocumentAndUpload(placeholdersMock, CAVEAT_RAISED);

        String fileName = "caveatRaised.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadGrantRaised() throws IOException {
        String json = "{}";

        when(pdfGeneratorServiceMock.generatePdf(GRANT_RAISED, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);
        when(pdfDecoratorService.decorate(callbackRequestMock, GRANT_RAISED)).thenReturn(json);

        Document response = underTest.generateAndUpload(callbackRequestMock, GRANT_RAISED);

        String fileName = "grantRaised.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
    }

    @Test
    void shouldGenerateAndUploadDocmosisDocumentGrantRaised() throws IOException {
        String json = "{}";

        when(pdfGeneratorServiceMock
            .generateDocmosisDocumentFrom(GRANT_RAISED.getTemplateName(), placeholdersMock))
            .thenReturn(evidenceManagementFileUpload);
        when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        String href = "href";
        mockLinks(href);

        Document response = underTest.generateDocmosisDocumentAndUpload(placeholdersMock, GRANT_RAISED);

        String fileName = "grantRaised.pdf";
        assertNotNull(response);
        assertEquals(fileName, response.getDocumentLink().getDocumentFilename());
        assertEquals(href, response.getDocumentLink().getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentLink().getDocumentUrl());
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
            when(uploadServiceMock.store(evidenceManagementFileUpload)).thenThrow(new IOException());
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
            when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
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
            when(uploadServiceMock.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
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
}
