package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.client.DocumentStoreClient;
import uk.gov.hmcts.probate.service.template.pdf.PDFGeneratorService;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.sendletter.api.LetterWithPdfsRequest;
import uk.gov.hmcts.reform.sendletter.api.SendLetterApi;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BulkPrintServiceTest {

    @InjectMocks
    private BulkPrintService bulkPrintService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SendLetterApi sendLetterApiMock;

    @Mock
    private ServiceAuthTokenGenerator authTokenGeneratorMock;

    @Mock
    private DocumentStoreClient documentStoreClientMock;

    @Mock
    private PDFGeneratorService pdfGeneratorServiceMock;

    @Mock
    private ObjectMapper objectMapperMock;

    @Before
    public void setUp() throws Exception {
        when(authTokenGeneratorMock.generate()).thenReturn("authToken");
        when(documentStoreClientMock.retrieveDocument(any(Document.class), anyString())).thenReturn(new byte[256]);
        when(pdfGeneratorServiceMock.generatePdf(any(DocumentType.class), anyString()))
                .thenReturn(new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, new byte[256]));
        when(objectMapperMock.writeValueAsString(any(Object.class))).thenReturn("");
    }

    @Test
    public void testSuccessfulSendToBulkPrintWithNoExtraCopies() {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterWithPdfsRequest.class))).thenReturn(sendLetterResponse);
        SendLetterResponse response =  bulkPrintService.sendToBulkPrint(callbackRequest, document);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterWithPdfsRequest.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }


    @Test
    public void testSuccessfulSendToBulkPrintWithSixExtraCopies() {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(6L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterWithPdfsRequest.class))).thenReturn(sendLetterResponse);

        SendLetterResponse response = bulkPrintService.sendToBulkPrint(callbackRequest, document);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterWithPdfsRequest.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    public void testHttpClientException() {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(6L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(sendLetterApiMock)
                .sendLetter(anyString(), any(LetterWithPdfsRequest.class));
        SendLetterResponse response = bulkPrintService.sendToBulkPrint(callbackRequest, document);

        assertNull(response);
    }

    @Test
    public void shouldThrowgIOException() throws IOException {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(6L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        doThrow(new IOException("Error retrieving document from store with url"))
                .when(documentStoreClientMock).retrieveDocument(any(Document.class), anyString());

        SendLetterResponse response = bulkPrintService.sendToBulkPrint(callbackRequest, document);

        verify(documentStoreClientMock).retrieveDocument(any(Document.class), anyString());
    }
}