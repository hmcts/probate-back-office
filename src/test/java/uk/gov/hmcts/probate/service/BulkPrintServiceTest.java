package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.probate.exception.BulkPrintException;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.client.DocumentStoreClient;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.sendletter.api.LetterWithPdfsRequest;
import uk.gov.hmcts.reform.sendletter.api.SendLetterApi;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@RunWith(MockitoJUnitRunner.class)
public class BulkPrintServiceTest {

    @InjectMocks
    private BulkPrintService bulkPrintService;

    @Mock
    private SendLetterApi sendLetterApiMock;

    @Mock
    private EventValidationService eventValidationService;

    @Mock
    private ServiceAuthTokenGenerator authTokenGeneratorMock;

    @Mock
    private DocumentStoreClient documentStoreClientMock;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    private ResponseCaseData responseCaseData;
    private CallbackResponse callbackResponse;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(authTokenGeneratorMock.generate()).thenReturn("authToken");
        when(documentStoreClientMock.retrieveDocument(any(Document.class), anyString())).thenReturn(new byte[256]);
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
        Document grant = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterWithPdfsRequest.class))).thenReturn(sendLetterResponse);
        SendLetterResponse response = bulkPrintService.sendToBulkPrint(callbackRequest, grant, coverSheet);

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
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterWithPdfsRequest.class))).thenReturn(sendLetterResponse);

        SendLetterResponse response = bulkPrintService.sendToBulkPrint(callbackRequest, document, coverSheet);

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
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(sendLetterApiMock)
                .sendLetter(anyString(), any(LetterWithPdfsRequest.class));
        SendLetterResponse response = bulkPrintService.sendToBulkPrint(callbackRequest, document, coverSheet);

        assertNull(response);
    }

    @Test
    public void shouldThrowIOException() throws IOException {
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
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        doThrow(new IOException("Error retrieving document from store with url"))
                .when(documentStoreClientMock).retrieveDocument(any(Document.class), anyString());

        bulkPrintService.sendToBulkPrint(callbackRequest, document, coverSheet);

        verify(documentStoreClientMock).retrieveDocument(any(Document.class), anyString());
    }

    @Test
    public void shouldThrowCaveatsIOException() throws IOException {

        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
                .proAddressLine2("Address 2")
                .proPostCode("EC2")
                .proCountry("UK")
                .build();
        CaveatData caseData = CaveatData.builder()
                .caveatorEmailAddress("email@email.com")
                .caveatorForenames("firstname")
                .caveatorSurname("surname")
                .caveatorAddress(address)
                .build();
        CaveatCallbackRequest callbackRequest = new CaveatCallbackRequest(new CaveatDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        doThrow(new IOException("Error retrieving document from store with url"))
                .when(documentStoreClientMock).retrieveDocument(any(Document.class), anyString());

        bulkPrintService.sendToBulkPrint(callbackRequest, document, coverSheet);

        verify(documentStoreClientMock).retrieveDocument(any(Document.class), anyString());
    }

    @Test
    public void testHttpClientExceptionCaveats() {
        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
                .proAddressLine2("Address 2")
                .proPostCode("EC2")
                .proCountry("UK")
                .build();
        CaveatData caseData = CaveatData.builder()
                .caveatorEmailAddress("email@email.com")
                .caveatorForenames("firstname")
                .caveatorSurname("surname")
                .caveatorAddress(address)
                .build();
        CaveatCallbackRequest callbackRequest = new CaveatCallbackRequest(new CaveatDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(sendLetterApiMock)
                .sendLetter(anyString(), any(LetterWithPdfsRequest.class));
        SendLetterResponse response = bulkPrintService.sendToBulkPrint(callbackRequest, document, coverSheet);

        assertNull(response);
    }

    @Test
    public void testSuccessfulSendToBulkPrintForCaveats() {

        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
                .proAddressLine2("Address 2")
                .proPostCode("EC2")
                .proCountry("UK")
                .build();
        CaveatData caseData = CaveatData.builder()
                .caveatorEmailAddress("email@email.com")
                .caveatorForenames("firstname")
                .caveatorSurname("surname")
                .caveatorAddress(address)
                .build();
        CaveatCallbackRequest callbackRequest = new CaveatCallbackRequest(new CaveatDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterWithPdfsRequest.class))).thenReturn(sendLetterResponse);
        SendLetterResponse response = bulkPrintService.sendToBulkPrint(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterWithPdfsRequest.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    public void testSuccessfulSendToBulkPrintGrantReissue() {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantEmailAddress("email@email.com")
                .primaryApplicantForenames("firstname")
                .primaryApplicantSurname("surname")
                .primaryApplicantAddress(address)
                .build();
        final CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        final Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        final Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        responseCaseData = ResponseCaseData.builder()
                .registryLocation("leeds")
                .deceasedForenames("name")
                .deceasedSurname("name")
                .build();

        final CallbackResponse callbackResponse = CallbackResponse.builder()
                .errors(new ArrayList<>())
                .data(responseCaseData)
                .build();

        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterWithPdfsRequest.class))).thenReturn(sendLetterResponse);
        when(eventValidationService.validateBulkPrintResponse(eq(uuid.toString()), any())).thenReturn(callbackResponse);

        String letterId = bulkPrintService.sendToBulkPrintGrantReissue(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterWithPdfsRequest.class));

        assertNotNull(letterId);
        assertThat(letterId, is(uuid.toString()));
    }

    @Test
    public void testSuccessfulSendToBulkPrintGrantReissueThrowsError() throws BulkPrintException {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantEmailAddress("email@email.com")
                .primaryApplicantForenames("firstname")
                .primaryApplicantSurname("surname")
                .primaryApplicantAddress(address)
                .build();
        final CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        final Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        final Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        List errors = new ArrayList();
        errors.add("test error");

        callbackResponse = callbackResponse.builder()
                .data(responseCaseData)
                .errors(errors)
                .build();

        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterWithPdfsRequest.class))).thenReturn(sendLetterResponse);
        when(eventValidationService.validateBulkPrintResponse(eq(uuid.toString()), any())).thenReturn(callbackResponse);
        when(businessValidationMessageService.generateError(any(), any())).thenReturn(FieldErrorResponse.builder().build());

        assertThatThrownBy(() -> {
            bulkPrintService.sendToBulkPrintGrantReissue(callbackRequest, coverSheet, document);
        }).isInstanceOf(BulkPrintException.class).hasMessage("Bulk print send letter response is null for: 0");
    }


}