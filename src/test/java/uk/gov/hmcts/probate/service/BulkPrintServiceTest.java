package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.probate.exception.BulkPrintException;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.DocumentType;
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
import uk.gov.hmcts.probate.transformer.DocumentTransformer;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.printletter.api.PrintLetterApi;
import uk.gov.hmcts.reform.printletter.api.PrintLetterResponse;
import uk.gov.hmcts.reform.printletter.api.exception.PrintResponseException;
import uk.gov.hmcts.reform.printletter.api.model.v1.PrintLetterRequest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BulkPrintServiceTest {

    @InjectMocks
    private BulkPrintService bulkPrintService;
    
    @Mock
    private PrintLetterApi printLetterApiMock;

    @Mock
    private EventValidationService eventValidationService;

    @Mock
    private ServiceAuthTokenGenerator authTokenGeneratorMock;

    @Mock
    private DocumentStoreClient documentStoreClientMock;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    private ResponseCaseData responseCaseData;
    @Mock
    private DocumentTransformer documentTransformer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(authTokenGeneratorMock.generate()).thenReturn("authToken");
        when(documentStoreClientMock.retrieveDocument(any(Document.class), anyString())).thenReturn(new byte[256]);
    }

    @Test
    public void testSuccessfulSendToBulkPrintWithNoExtraCopies() throws PrintResponseException {

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
                .documentType(DocumentType.DIGITAL_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .documentType(DocumentType.GRANT_COVERSHEET)
                .build();
        UUID uuid = UUID.randomUUID();
        PrintLetterResponse printLetterResponse = new PrintLetterResponse(uuid);
        when(printLetterApiMock.printLetter(anyString(), any(PrintLetterRequest.class)))
                .thenReturn(printLetterResponse);
        PrintLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, grant, coverSheet);

        verify(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }


    @Test
    public void testSuccessfulSendToBulkPrintWithSixExtraCopies() throws PrintResponseException {

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
                .documentType(DocumentType.DIGITAL_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .documentType(DocumentType.GRANT_COVERSHEET)
                .build();
        UUID uuid = UUID.randomUUID();
        PrintLetterResponse printLetterResponse = new PrintLetterResponse(uuid);
        when(printLetterApiMock.printLetter(anyString(), any(PrintLetterRequest.class)))
                .thenReturn(printLetterResponse);
        when(documentTransformer.hasDocumentWithType(Collections.singletonList(document), DocumentType.DIGITAL_GRANT))
            .thenReturn(true);

        PrintLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    public void testHttpClientException() throws PrintResponseException {
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
                .documentType(DocumentType.DIGITAL_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "StatusText", "Body".getBytes(),
             Charset.defaultCharset()))
            .when(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));
        PrintLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        assertNull(response);
    }

    @Test
    public void shouldThrowException() throws PrintResponseException {
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
            .documentType(DocumentType.DIGITAL_GRANT)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();

        doThrow(new RuntimeException("Some exception"))
                .when(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));
        PrintLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

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
                .documentType(DocumentType.ASSEMBLED_LETTER)
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

        bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

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
            .caveatorEmailAddress("caveator@probate-test.com")
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
            .documentType(DocumentType.DIGITAL_GRANT_REISSUE)
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

        bulkPrintService.sendToBulkPrintForCaveat(callbackRequest, document, coverSheet);

        verify(documentStoreClientMock).retrieveDocument(any(Document.class), anyString());
    }

    @Test
    public void shouldThrowCaveatsException() throws PrintResponseException {

        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
            .proAddressLine2("Address 2")
            .proPostCode("EC2")
            .proCountry("UK")
            .build();
        CaveatData caseData = CaveatData.builder()
            .caveatorEmailAddress("caveator@probate-test.com")
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
            .documentType(DocumentType.DIGITAL_GRANT_REISSUE)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();

        doThrow(new RuntimeException("SomeException"))
            .when(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));

        PrintLetterResponse response = bulkPrintService.sendToBulkPrintForCaveat(callbackRequest, document, coverSheet);

        assertNull(response);
    }

    @Test
    public void testHttpClientExceptionCaveats() throws PrintResponseException {
        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
                .proAddressLine2("Address 2")
                .proPostCode("EC2")
                .proCountry("UK")
                .build();
        CaveatData caseData = CaveatData.builder()
                .caveatorEmailAddress("caveator@probate-test.com")
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
                .documentType(DocumentType.CAVEAT_RAISED)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "StatusText", "Body".getBytes(),
            Charset.defaultCharset()))
                .when(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));
        PrintLetterResponse response = bulkPrintService.sendToBulkPrintForCaveat(callbackRequest, document, coverSheet);

        assertNull(response);
    }

    @Test
    public void testSuccessfulSendToBulkPrintForCaveats() throws PrintResponseException {

        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
                .proAddressLine2("Address 2")
                .proPostCode("EC2")
                .proCountry("UK")
                .build();
        CaveatData caseData = CaveatData.builder()
                .caveatorEmailAddress("caveator@probate-test.com")
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
                .documentType(DocumentType.CAVEAT_RAISED)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .documentType(DocumentType.GRANT_COVERSHEET)
                .build();
        UUID uuid = UUID.randomUUID();
        PrintLetterResponse printLetterResponse = new PrintLetterResponse(uuid);
        when(printLetterApiMock.printLetter(anyString(), any(PrintLetterRequest.class)))
                .thenReturn(printLetterResponse);
        PrintLetterResponse response = bulkPrintService.sendToBulkPrintForCaveat(callbackRequest, document, coverSheet);

        verify(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    public void testSuccessfulSendToBulkPrintGrant() throws PrintResponseException {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.DIGITAL_GRANT);
    }

    @Test
    public void testSuccessfulSendToBulkPrintIntestacyGrant() throws PrintResponseException {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.INTESTACY_GRANT);
    }

    @Test
    public void testSuccessfulSendToBulkPrintAdmonWillGrant() throws PrintResponseException {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.ADMON_WILL_GRANT);
    }

    @Test
    public void testSuccessfulSendToBulkPrintGrantReissue() throws PrintResponseException {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.DIGITAL_GRANT_REISSUE);
    }

    @Test
    public void testSuccessfulSendToBulkPrintIntestacyGrantReissue() throws PrintResponseException {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.INTESTACY_GRANT_REISSUE);
    }

    @Test
    public void testSuccessfulSendToBulkPrintAdmonWillGrantReissue() throws PrintResponseException {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.ADMON_WILL_GRANT_REISSUE);
    }

    @Test
    public void testSuccessfulSendToBulkPrintGrantReissueWelsh() throws PrintResponseException {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.WELSH_DIGITAL_GRANT_REISSUE);
    }

    @Test
    public void testSuccessfulSendToBulkPrintIntestacyGrantReissueWelsh() throws PrintResponseException {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.WELSH_INTESTACY_GRANT_REISSUE);
    }

    @Test
    public void testSuccessfulSendToBulkPrintAdmonWillGrantReissueWelsh() throws PrintResponseException {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE);
    }

    @Test
    public void testUnSuccessfulValidateEmailThrowsError() throws BulkPrintException, PrintResponseException {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantEmailAddress("primary@probate-test.com")
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

        List<String> errors = new ArrayList<>();
        errors.add("test error");

        CallbackResponse callbackResponse = CallbackResponse.builder()
                .data(responseCaseData)
                .errors(errors)
                .build();

        when(printLetterApiMock.printLetter(anyString(), any(PrintLetterRequest.class))).thenReturn(null);
        when(eventValidationService.validateBulkPrintResponse(any(), any())).thenReturn(callbackResponse);
        when(businessValidationMessageService.generateError(any(), any()))
            .thenReturn(FieldErrorResponse.builder().build());

        assertThatThrownBy(
            () -> bulkPrintService.optionallySendToBulkPrint(callbackRequest, coverSheet, document, true))
            .isInstanceOf(BulkPrintException.class).hasMessage("Bulk print send letter response is null for: 0");
    }

    @Test
    public void testNoSendToBulkPrintReturnsNull() {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantForenames("firstname")
                .primaryApplicantSurname("surname")
                .primaryApplicantAddress(address)
                .build();
        final CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        assertNull(bulkPrintService.optionallySendToBulkPrint(callbackRequest, Document.builder().build(),
                Document.builder().build(), false));
    }

    @Test
    public void sendToBulkPrintWith50ExtraCopiesWDG() throws PrintResponseException {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(50L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.WELSH_DIGITAL_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .documentType(DocumentType.GRANT_COVERSHEET)
                .build();
        UUID uuid = UUID.randomUUID();
        PrintLetterResponse printLetterResponse = new PrintLetterResponse(uuid);
        when(printLetterApiMock.printLetter(anyString(), any(PrintLetterRequest.class)))
                .thenReturn(printLetterResponse);
        when(documentTransformer
            .hasDocumentWithType(Collections.singletonList(document), DocumentType.WELSH_DIGITAL_GRANT))
            .thenReturn(true);

        PrintLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    public void sendToBulkPrintWith50ExtraCopiesWIG() throws PrintResponseException {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(50L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.WELSH_INTESTACY_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .documentType(DocumentType.GRANT_COVERSHEET)
                .build();
        UUID uuid = UUID.randomUUID();
        PrintLetterResponse printLetterResponse = new PrintLetterResponse(uuid);
        when(printLetterApiMock.printLetter(anyString(), any(PrintLetterRequest.class)))
                .thenReturn(printLetterResponse);
        when(documentTransformer
            .hasDocumentWithType(Collections.singletonList(document), DocumentType.WELSH_INTESTACY_GRANT))
            .thenReturn(true);

        PrintLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    public void sendToBulkPrintWith50ExtraCopiesAWDG() throws PrintResponseException {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(50L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.WELSH_ADMON_WILL_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .documentType(DocumentType.GRANT_COVERSHEET)
                .build();
        UUID uuid = UUID.randomUUID();
        PrintLetterResponse printLetterResponse = new PrintLetterResponse(uuid);
        when(printLetterApiMock.printLetter(anyString(), any(PrintLetterRequest.class)))
                .thenReturn(printLetterResponse);
        when(documentTransformer
            .hasDocumentWithType(Collections.singletonList(document), DocumentType.WELSH_ADMON_WILL_GRANT))
            .thenReturn(true);

        PrintLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Captor
    private ArgumentCaptor<PrintLetterRequest> printLetterRequestArgumentCaptor;

    @Test
    public void shouldSendToBulkPrintForReprint() throws PrintResponseException {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
            .addressLine2("Address 2")
            .postCode("EC2")
            .country("UK")
            .build();
        CaseData caseData = CaseData.builder()
            .primaryApplicantForenames("first")
            .primaryApplicantSurname("last")
            .primaryApplicantAddress(address)
            .reprintNumberOfCopies("10")
            .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
            .documentUrl("http://localhost")
            .build();
        Document grant = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentType(DocumentType.DIGITAL_GRANT)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .documentType(DocumentType.GRANT_COVERSHEET)
            .build();
        UUID uuid = UUID.randomUUID();
        PrintLetterResponse printLetterResponse = new PrintLetterResponse(uuid);

        List<String> errors = new ArrayList<>();
        CallbackResponse callbackResponse = CallbackResponse.builder()
            .data(responseCaseData)
            .errors(errors)
            .build();

        when(printLetterApiMock.printLetter(anyString(), printLetterRequestArgumentCaptor.capture()))
            .thenReturn(printLetterResponse);
        when(eventValidationService.validateBulkPrintResponse(eq(uuid.toString()), any())).thenReturn(callbackResponse);

        PrintLetterResponse response = bulkPrintService.sendDocumentsForReprint(callbackRequest, grant, coverSheet);

        assertEquals(printLetterResponse, response);
        assertEquals(1, printLetterRequestArgumentCaptor.getValue().documents.get(0).copies);
        assertEquals(10, printLetterRequestArgumentCaptor.getValue().documents.get(1).copies);

        verify(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));
    }

    @Test
    public void shouldSendToBulkPrintForReprintWillNullLetterId() throws PrintResponseException {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
            .addressLine2("Address 2")
            .postCode("EC2")
            .country("UK")
            .build();
        CaseData caseData = CaseData.builder()
            .primaryApplicantForenames("first")
            .primaryApplicantSurname("last")
            .primaryApplicantAddress(address)
            .reprintNumberOfCopies("10")
            .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
            .documentUrl("http://localhost")
            .build();
        Document grant = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentType(DocumentType.DIGITAL_GRANT)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .documentType(DocumentType.GRANT_COVERSHEET)
            .build();
        UUID uuid = UUID.randomUUID();
        PrintLetterResponse printLetterResponse = new PrintLetterResponse(uuid);

        List<String> errors = new ArrayList<>();
        CallbackResponse callbackResponse = CallbackResponse.builder()
            .data(responseCaseData)
            .errors(errors)
            .build();

        when(eventValidationService.validateBulkPrintResponse(any(), any())).thenReturn(callbackResponse);
        when(printLetterApiMock.printLetter(anyString(), any(PrintLetterRequest.class)))
                .thenReturn(printLetterResponse);

        PrintLetterResponse response = bulkPrintService.sendDocumentsForReprint(callbackRequest, grant, coverSheet);

        assertEquals(printLetterResponse, response);
        assertEquals(printLetterResponse.letterId, response.letterId);
        verify(printLetterApiMock).printLetter(anyString(), any(PrintLetterRequest.class));
    }

    @Test
    public void shouldErrorOnSendToBulkPrintForReprint() throws PrintResponseException {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
            .addressLine2("Address 2")
            .postCode("EC2")
            .country("UK")
            .build();
        CaseData caseData = CaseData.builder()
            .primaryApplicantForenames("first")
            .primaryApplicantSurname("last")
            .primaryApplicantAddress(address)
            .reprintNumberOfCopies("10")
            .build();
        final CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
            .documentUrl("http://localhost")
            .build();
        final Document grant = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentType(DocumentType.DIGITAL_GRANT)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        final Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .documentType(DocumentType.GRANT_COVERSHEET)
            .build();
        UUID uuid = UUID.randomUUID();
        PrintLetterResponse printLetterResponse = new PrintLetterResponse(uuid);

        List<String> errors = new ArrayList<>();
        errors.add("test error");
        CallbackResponse callbackResponse = CallbackResponse.builder()
            .data(responseCaseData)
            .errors(errors)
            .build();

        when(printLetterApiMock.printLetter(anyString(), any(PrintLetterRequest.class)))
                .thenReturn(printLetterResponse);
        when(eventValidationService.validateBulkPrintResponse(eq(uuid.toString()), any())).thenReturn(callbackResponse);

        when(businessValidationMessageService.generateError(any(), any()))
            .thenReturn(FieldErrorResponse.builder().build());

        assertThatThrownBy(() -> bulkPrintService.sendDocumentsForReprint(callbackRequest, grant, coverSheet))
            .isInstanceOf(BulkPrintException.class)
            .hasMessage("Bulk print send letter for reprint response is null for: 0");
    }

    private void testSuccessfulSendToBulkPrintForDocumentType(DocumentType documentType) throws PrintResponseException {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
            .addressLine2("Address 2")
            .postCode("EC2")
            .country("UK")
            .build();
        CaseData caseData = CaseData.builder()
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .primaryApplicantForenames("firstname")
            .primaryApplicantSurname("surname")
            .primaryApplicantAddress(address)
            .extraCopiesOfGrant(1L)
            .build();
        final CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
            .documentUrl("http://localhost")
            .build();
        final Document document = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentType(documentType)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        final Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .documentType(documentType)
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
        PrintLetterResponse printLetterResponse = new PrintLetterResponse(uuid);
        when(printLetterApiMock.printLetter(anyString(), any(PrintLetterRequest.class)))
                .thenReturn(printLetterResponse);
        when(eventValidationService.validateBulkPrintResponse(eq(uuid.toString()), any())).thenReturn(callbackResponse);
        when(documentTransformer.hasDocumentWithType(Collections.singletonList(document), documentType))
            .thenReturn(true);

        final String letterId = bulkPrintService.optionallySendToBulkPrint(callbackRequest, coverSheet, document, true);

        verify(printLetterApiMock).printLetter(anyString(), printLetterRequestArgumentCaptor.capture());

        assertEquals(1, printLetterRequestArgumentCaptor.getValue().documents.get(0).copies);
        assertEquals(2, printLetterRequestArgumentCaptor.getValue().documents.get(1).copies);

        assertNotNull(letterId);
        assertThat(letterId, is(uuid.toString()));
    }


}